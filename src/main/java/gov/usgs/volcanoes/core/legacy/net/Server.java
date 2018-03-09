package gov.usgs.volcanoes.core.legacy.net;

import gov.usgs.volcanoes.core.CodeTimer;
import gov.usgs.volcanoes.core.legacy.util.Pool;

import gov.usgs.volcanoes.core.util.StringUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.Level;

/**
 * The base class for implementing a Java NIO-based server.
 *
 * @author Dan Cervelli
 */
public class Server {
  protected ByteBuffer inBuffer = ByteBuffer.allocate(65536);
  protected NetTools netTools = new NetTools();
  protected static final int COMMAND_BUFFER_SIZE = 2048;

  protected DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  protected String name = "Server";
  protected int serverPort = -1;
  protected InetAddress serverIP = null;
  protected boolean keepalive = false;

  protected long connectionIndex = 0;

  private Pool<CommandHandler> commandHandlerPool;

  private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
  protected int maxReadHandlers = -1;

  protected Connections connections = Connections.getInstance();

  protected boolean dropOldest = true;

  protected long totalBytesSent = 0;

  protected Server() {
    commandHandlerPool = new Pool<CommandHandler>();
  }

  protected Server(InetAddress a, int p) {
    this();
    serverIP = a;
    serverPort = p;
  }

  protected void addCommandHandler(CommandHandler rh) {
    commandHandlerPool.checkin(rh);
    int max = Math.max(maxReadHandlers, commandHandlerPool.size());
    if (max > maxReadHandlers)
      LOGGER.debug("command handler pool size: {}", max);
    maxReadHandlers = max;
  }

  public int getPoolSize() {
    return commandHandlerPool.size();
  }

  public static String getHost(SocketChannel channel) {
    String addr;

    // avoid a check-then-act error, by catching the NullPointerException
    // rather than checking for null
    try {
      addr = channel.socket().getInetAddress().getHostAddress();
    } catch (NullPointerException e) {
      addr = "(closed)";
    }

    return addr;
  }

  public void log(Level level, String msg, SocketChannel channel) {
    String channelString = (channel == null ? "" : getHost(channel) + "/");
    String logMsg = channelString + msg;
    switch (level.toInt()) {
      case Level.DEBUG_INT:
        LOGGER.debug(logMsg);
        break;
      case Level.INFO_INT:
        LOGGER.info(logMsg);
        break;
      case Level.WARN_INT:
        LOGGER.warn(logMsg);
        break;
      case Level.ERROR_INT:
        LOGGER.error(logMsg);
        break;
      default:
        LOGGER.trace(logMsg);
        break;
    }
  }

  protected void closeConnection(SocketChannel channel, SelectionKey selectionKey) {
    try {

      connections.remove(channel);
      if (channel != null && channel.isOpen())
        channel.close();
      if (selectionKey != null) {
        selectionKey.cancel();
        selectionKey.selector().wakeup(); // what does this do?
        selectionKey.attach(null);
      }
      log(Level.DEBUG,
          String.format("Connection closed: %d/%d.", connections.getNumConnections(),
              connections.getMaxConnections()), channel);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected void dispatchCommand(SocketChannel channel, SelectionKey key, String s) {
    CodeTimer ct = new CodeTimer("getReadHandler");
    CommandHandler ch = commandHandlerPool.checkout();
    ct.stop();
    if (ct.getRunTimeMillis() > 1000)
      log(Level.DEBUG, String.format("long wait for read handler: %1.2f ms. ", ct.getRunTimeMillis()), channel);
    ch.doCommand(channel, key, s);
  }

  public void recordSent(SocketChannel channel, int nb) {
    totalBytesSent += nb;
    connections.sent(channel, nb);
  }

  /**
   * Read a single command from a channel. Command is either a single line or
   * a HTTP request
   *
   * @param selectionKey
   */
  public void processRead(SelectionKey selectionKey) {
    ByteBuffer commandBuffer;
    SocketChannel channel = (SocketChannel) selectionKey.channel();
    if (!channel.isConnected() || !channel.isOpen())
      return;

    // imcomplete buffer from last attempt?
    Object attachment = selectionKey.attachment();
    if (attachment != null)
      commandBuffer = (ByteBuffer) attachment;
    else
      commandBuffer = ByteBuffer.allocate(COMMAND_BUFFER_SIZE);

    boolean close = false;
    try {
      inBuffer.clear();
      int bytesRead = channel.read(inBuffer);
      if (bytesRead == -1)
        close = true;
      else {
        connections.read(channel, bytesRead);
        inBuffer.flip();
        if (readLine(inBuffer, commandBuffer)) {

          // peek at first four chars to detect HTTP
          StringBuffer sb = new StringBuffer();
          for (int i = 0; i < 5; i++)
            sb.append((char) commandBuffer.get(i));
          String cmd = sb.toString();

          // support HTTP simple-request and full-request.
          // Simple-request does not contain have HTTP version
          if (cmd.startsWith("GET ") || cmd.startsWith("POST "))
            if (!cmd.contains("HTTP/"))
              readHTTP(inBuffer, commandBuffer);

          commandBuffer.flip();
          String commandString = netTools.decoder.decode(commandBuffer).toString();
          if (connections.isTraced(channel)) {
            SocketAddress s = channel.socket().getRemoteSocketAddress();
            if (s != null)
              LOGGER.warn("{}: {}", s, commandString);
          }
          connections.beginCommand(channel, commandString);
          dispatchCommand(channel, selectionKey, commandString);
          commandBuffer.clear();
        }

        // command incomplete, check again next time.
        if (commandBuffer.position() != 0)
          selectionKey.attach(commandBuffer);
        else
          selectionKey.attach(null);
      }
    } catch (IOException e) {
      close = true;
    } catch (BufferOverflowException e) {
      LOGGER.error("Buffer overflow on read.  Possible malicious attack?");
      close = true;
    } catch (Exception e) {
      LOGGER.error("Unhandled exception.", e);
      close = true;
    }
    if (close)
      closeConnection(channel, selectionKey);
  }

  /**
   * Move a full line from one buffer to another
   *
   * @param inBuffer
   *            Source buffer
   * @param commandBuffer
   *            Destination buffer
   * @return true if I found end of line
   */
  private boolean readLine(ByteBuffer inBuffer, ByteBuffer commandBuffer) {
    boolean foundEoL = false;
    while (!foundEoL && inBuffer.position() < inBuffer.limit()) {
      byte b = inBuffer.get();
      if (b == '\n')
        foundEoL = true;
      else {
        commandBuffer.put(b);
      }
    }

    return foundEoL;
  }

  /**
   * Move a full HTTP request from one buffer to another. Assume first line
   * has already been taken care of
   *
   * @param inBuffer
   * @param commandBuffer
   * @return true if the complete HTTP request has been moved
   */
  private boolean readHTTP(ByteBuffer inBuffer, ByteBuffer commandBuffer) {
    boolean haveCompleteRequest = false;

    // only HTTP commands have a first line terminated by a newline
    commandBuffer.put((byte) '\n');

    boolean haveHeaders = false;
    while (!haveHeaders && inBuffer.position() < inBuffer.limit()) {
      int position = commandBuffer.position();
      if (readLine(inBuffer, commandBuffer)) {
        commandBuffer.put((byte) '\n');
        haveHeaders = (commandBuffer.position() - position < 2);
      }
    }

    int contentLength = 0;
    if (haveHeaders) {
      String header = commandBuffer.duplicate().toString();
      String headers[] = header.split("/\n/");
      for (String headerLine : headers)
        if (headerLine.startsWith("Content-Length:"))
          contentLength = Integer.parseInt(headerLine.substring(15));

      while (contentLength-- > 0 && inBuffer.position() < inBuffer.limit())
        commandBuffer.put(inBuffer.get());

      if (contentLength == 0)
        haveCompleteRequest = true;
    }

    return haveCompleteRequest;
  }

  public void printConnections(String s) {
    StringBuffer sb = new StringBuffer();
    sb.append(connections.printConnections(s));
    sb.append("Available command handlers: " + commandHandlerPool.size() + "/" + maxReadHandlers + "\n");
    sb.append("Total bytes sent:           " + StringUtils.numBytesToString(totalBytesSent) + "\n");

    System.out.println(sb);
  }

  public void printCommands(String s) {
    StringBuffer sb = new StringBuffer();
    sb.append(connections.printCommands(s));
    sb.append("\nAvailable command handlers: " + commandHandlerPool.size() + "/" + maxReadHandlers + "\n");

    System.out.println(sb);
  }

  public void dropConnections() {
    dropConnections(0);
  }

  public void dropConnections(long idleLimit) {
    LOGGER.info("Dropping connections.");
    List<SocketChannel> channels = new ArrayList<SocketChannel>(connections.connectionCount());
    // must move current connections to a list so that they can be dropped
    Map<SocketChannel, ConnectionStatistics> connectionStats = connections.getConnections();
    for (SocketChannel sc : connectionStats.keySet()) {
      ConnectionStatistics cs = connectionStats.get(sc);
      if (System.currentTimeMillis() - cs.lastRequestTime > idleLimit)
        channels.add(sc);
    }

    for (SocketChannel sc : channels)
      closeConnection(sc, null);
  }

  public void dropOldestConnection() {
    long least = Long.MAX_VALUE;
    SocketChannel lc = null;
    Map<SocketChannel, ConnectionStatistics> connectionStats = connections.getConnections();
    for (ConnectionStatistics cs : connectionStats.values()) {
      if (cs.lastRequestTime < least) {
        least = cs.lastRequestTime;
        lc = cs.channel;
      }
    }
    if (lc != null)
      closeConnection(lc, null);
  }

  public void toggleTrace(String cmd) {
    try {
      int index = Integer.parseInt(cmd.substring(1));
      connections.toggleTrace(index);
    } catch (NumberFormatException e) {
      LOGGER.warn("Can't parse index. Use 'c' to find the connection index.");
    }
  }

  protected void startListening() {
    if (commandHandlerPool.size() <= 0 || serverPort == -1)
      return;

    try {
      Selector selector = Selector.open();

      ServerSocketChannel serverChannel = ServerSocketChannel.open();
      serverChannel.configureBlocking(false);
      if (serverIP == null) {
        serverChannel.socket().bind(new InetSocketAddress(serverPort));
        LOGGER.info("listening on IP *.");
      } else {
        serverChannel.socket().bind(new InetSocketAddress(serverIP, serverPort));
        LOGGER.info("listening on IP {}.", serverIP.getHostAddress());
      }

      serverChannel.register(selector, SelectionKey.OP_ACCEPT);

      LOGGER.info("listening on port {}.", serverPort);

      while (true) {
        selector.select();

        // TODO: figure out why this threw a nullPointer exception in
        // production
        Set<SelectionKey> sk = selector.selectedKeys();
        Iterator<SelectionKey> it = sk.iterator();

        while (it.hasNext()) {
          // it is actually possible that the key will go invalid
          // between the call to isValid and isAcceptable/isReadable
          // which will
          // then throw the CancelledKeyException
          try {
            SelectionKey selKey = (SelectionKey) it.next();
            it.remove();
            if (!selKey.isValid())
              continue;

            if (selKey.isAcceptable()) {
              ServerSocketChannel ssChannel = (ServerSocketChannel) selKey.channel();

              // Why are wee seeing null pointers here?
              SocketChannel channel = ssChannel.accept();
              if (channel == null) {
                System.err.println("channel is null in net.Server.startListening.");
                continue;
              }
              Socket socket = channel.socket();

              if (socket == null) {
                System.err.println("socket is null in net.Server.startListening.");
                continue;
              }
              socket.setKeepAlive(keepalive);
              socket.setTcpNoDelay(true);

              ConnectionStatistics cs = connections.getConnectionStatistics(channel);
              cs.touch();
              if (connections.getMaxConnections() != 0
                  && connections.getNumConnections() > connections.getMaxConnections()) {
                if (dropOldest) {
                  LOGGER.error("Max connections reached, dropped least recently used connection.");
                  dropOldestConnection();
                } else {
                  LOGGER.error("Max connections reached, rejected connection.");
                  channel.close();
                  // DO NOT PASS selKey to closeConnection, it
                  // will break the server.
                  closeConnection(channel, null);
                  continue;
                }
              }
              log(Level.DEBUG, String.format("Connection accepted: %d/%d",
                  connections.getNumConnections(), connections.getMaxConnections()), channel);
              channel.configureBlocking(false);
              channel.register(selector, SelectionKey.OP_READ);
            }

            if (selKey.isValid() && selKey.isReadable()) {
              SocketChannel channel = (SocketChannel) selKey.channel();
              if (channel.isOpen() && channel.isConnected()) {
                ConnectionStatistics cs = connections.getConnectionStatistics(channel);
                cs.touch();
                processRead(selKey);
              }
            }
          } catch (CancelledKeyException e) {
          }
        }
      }
    } catch (IOException e) {
      LOGGER.error("Fatal exception.", e);
    }
  }
}

