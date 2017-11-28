package gov.usgs.volcanoes.core.legacy.ew;

import gov.usgs.volcanoes.core.legacy.ew.globals.Installation;
import gov.usgs.volcanoes.core.legacy.ew.globals.Module;
import gov.usgs.volcanoes.core.legacy.ew.message.Message;
import gov.usgs.volcanoes.core.legacy.ew.message.MessageFactory;
import gov.usgs.volcanoes.core.legacy.ew.message.MessageLogo;
import gov.usgs.volcanoes.core.legacy.ew.message.MessageType;
import gov.usgs.volcanoes.core.util.ByteUtil;
import gov.usgs.volcanoes.core.util.Retriable;
import gov.usgs.volcanoes.core.util.UtilException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Stub for writing programs that communicate with Earthworm export_generic.
 *
 * @author Dan Cervelli
 */
public class ImportGeneric extends Thread {

  class ExpectedHeartbeatThread extends Thread implements MessageListener {
    private boolean kill = false;

    public ExpectedHeartbeatThread() {
      super("ExpectedHeartbeat");
      lastHeartbeat = System.currentTimeMillis();
      start();
    }

    public void kill() {
      kill = true;
      interrupt();
    }

    @Override
    public void messageReceived(Message msg) {
      logger.finer("Received heartbeat.");
      final String recv = msg.bytesToString().trim();

      if (!recv.equals(recvIDString)) {
        logger.fine("Warning: heartbeat message, '" + recv + "', does not match expected message, '"
            + recvIDString + "'.");
      }
      lastHeartbeat = System.currentTimeMillis();
      if (msg.sendAck) {
        sendAck(msg.seq);
      }
    }

    @Override
    public void run() {
      while (!kill) {
        try {
          Thread.sleep(expectedHeartbeatInterval);
          if (System.currentTimeMillis() - lastHeartbeat >= expectedHeartbeatInterval * 2) {
            logger.fine("Have not received heartbeat recently.  Reconnecting.");
            needReconnect = true;
            kill = true;
          }
        } catch (final InterruptedException e) {
        } catch (final OutOfMemoryError e) {
          outOfMemoryErrorOccurred(e);
        }
      }
      logger.fine("Expected heartbeat thread died.");
    }
  }
  class HeartbeatThread extends Thread {
    private boolean kill = false;
    private final MessageLogo logo;

    public HeartbeatThread() {
      super("Heartbeat");
      logo = new MessageLogo();
      logo.setInstallationId(ByteUtil.intToByte(Installation.INST_UNKNOWN));
      logo.setModule(ByteUtil.intToByte(Module.MOD_IMPORT_GENERIC));
      logo.setType(MessageType.TYPE_HEARTBEAT);
    }

    public void kill() {
      kill = true;
      interrupt();
    }

    @Override
    public void run() {
      while (!kill) {
        try {
          Thread.sleep(heartbeatInterval);
          logger.finer("Sending heartbeat.");
          sendMsg(logo, sendIDString);
          lastHeartbeatSent = System.currentTimeMillis();
        } catch (final InterruptedException e) {
        } catch (final IOException e) {
          logger.warning("Exception in heartbeat sending thread, reconnecting.");
          needReconnect = true;
          kill = true;
          // reconnect?
        } catch (final OutOfMemoryError e) {
          outOfMemoryErrorOccurred(e);
        }
      }
      logger.fine("Heartbeat thread died.");
    }
  }

  private static final byte ESC = 27;
  private static final byte ETX = 3;

  private static final byte STX = 2;

  public static void main(String[] args) {
    if (args.length == 2) {
      final ImportGeneric imp = new ImportGeneric(args[0], Integer.parseInt(args[1]));
      imp.addListener(MessageType.TYPE_HEARTBEAT, new MessageListener() {
        @Override
        public void messageReceived(Message msg) {
          System.out.println(msg);
        }
      });
      imp.connect();
    } else {
      System.err.println("This program is intended to be a stub for more interesting applications");
      System.err.println("that interpret Earthworm information.  Use this program alone to test");
      System.err.println("a connection to export_generic.");
      System.err.println();
      System.err.println("usage: java gov.usgs.earthworm.ImportGeneric [host] [port]");
      System.exit(1);
    }
  }

  protected boolean connected;
  protected ExpectedHeartbeatThread expectedHeartbeat;
  protected int expectedHeartbeatInterval = 30000;
  protected HeartbeatThread heartbeat;
  protected int heartbeatInterval = 30000;
  protected String host;

  protected long lastConnectUpdate = -1;
  protected long lastHeartbeat;
  protected long lastHeartbeatSent;
  protected Map<MessageType, List<MessageListener>> listeners;

  protected Logger logger;
  protected int maxRetries = 3;

  protected byte[] msgBuf;// = new byte[65000];

  protected boolean needReconnect;
  protected int port;
  protected String recvIDString;

  protected String sendIDString;

  protected volatile boolean shutdown = false;
  protected Socket socket;

  protected DataInputStream socketIn;

  protected DataOutputStream socketOut;

  protected PrintWriter socketWriter;

  protected int timeout = 10000;

  public ImportGeneric() {
    super("ImportGeneric");
    msgBuf = new byte[65000];
    logger = Logger.getLogger("gov.usgs.earthworm");
    listeners = new HashMap<>();
  }

  public ImportGeneric(String h, int p) {
    this();
    host = h;
    port = p;
  }

  public void addListener(MessageType type, MessageListener ml) {
    List<MessageListener> list = listeners.get(type);
    if (list == null) {
      list = new ArrayList<>();
      listeners.put(type, list);
    }
    list.add(ml);
  }

  public void close() {
    try {
      if (socket.isClosed()) {
        return;
      }
      connected = false;
      heartbeat.kill();
      expectedHeartbeat.kill();
      removeListener(MessageType.TYPE_HEARTBEAT, expectedHeartbeat);
      socketOut.close();
      socketWriter.close();
      socketIn.close();
      socket.close();
    } catch (final IOException e) {
      logger.severe("ImportGeneric.close() IOException: " + e.getMessage());
    }
  }

  public boolean connect() {
    Boolean b = null;
    final Retriable<Boolean> result = new Retriable<Boolean>("Import.connect()", maxRetries) {
      @Override
      public boolean attempt() throws UtilException {
        output = false;
        try {
          socket = new Socket();
          socket.connect(new InetSocketAddress(host, port), timeout);
          socket.setSoLinger(false, 0);
          socket.setSoTimeout(timeout);
          socketOut = new DataOutputStream(socket.getOutputStream());
          socketWriter = new PrintWriter(socket.getOutputStream());
          socketIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
          logger.fine("ImportGeneric connected.");
          result = new Boolean(true);
          heartbeat = new HeartbeatThread();
          heartbeat.start();
          expectedHeartbeat = new ExpectedHeartbeatThread();
          addListener(MessageType.TYPE_HEARTBEAT, expectedHeartbeat);
          connected = true;
          needReconnect = false;
          if (!ImportGeneric.this.isAlive()) {
            start();
          }
          return true;
        } catch (final SocketTimeoutException e) {
          // if (!quiet)
          // logger.severe("Import.connect() socket timeout: could not connect.");
        } catch (final IOException e) {
          // if (!quiet)
          // logger.severe("ImportGeneric.connect() IOException: " +
          // e.getMessage());
        }
        // quiet = true;
        return false;
      }

      @Override
      public void giveUp() {
        if (System.currentTimeMillis() - lastConnectUpdate > 10 * 60 * 1000) {
          logger.severe(
              "ImportGeneric: can not connect, will update periodically while attempting to reconnect.");
          try {
            Thread.sleep(1000);
          } catch (final Exception e) {
          }
          lastConnectUpdate = System.currentTimeMillis();
        }
      }
    };
    try {
      b = result.go();
    } catch (final UtilException e) {
      // Do nothing
    }
    return b != null && b.booleanValue();
  }

  public void dispatchMessage(Message msg) {
    final List<MessageListener> list = listeners.get(msg.logo.getType());
    if (list != null) {

      for (final MessageListener ml : list) {
        ml.messageReceived(msg);
      }
    }
  }

  public int findSeq(String s) {
    int seq;
    if (s.startsWith("SQ:")) {
      seq = Integer.parseInt(s.substring(3, 6).trim());
    } else {
      seq = Integer.MIN_VALUE;
    }

    return seq;
  }

  public String getHost() {
    return host;
  }

  public long getLastHeartbeatSentTime() {
    return lastHeartbeatSent;
  }

  public long getLastHeartbeatTime() {
    return lastHeartbeat;
  }

  public int getPort() {
    return port;
  }

  public void outOfMemoryErrorOccurred(OutOfMemoryError e) {}

  public void removeListener(MessageType type, MessageListener ml) {
    // List list = (List)listeners.get(type);
    final List<MessageListener> list = listeners.get(type);
    if (list == null) {
      return;
    }

    list.remove(ml);
  }

  // TODO: prevent buffer overflows.
  @Override
  public void run() {
    // logger.fine("Starting listener thread.");
    byte lastByte = ESC;
    while (!shutdown) {
      try {
        if (needReconnect) {
          close();
          connect();
          continue;
        }
        if (!connected) {
          Thread.sleep(250);
          continue;
        }

        boolean start = false;

        try {
          final byte b = socketIn.readByte();
          if (b == STX) {
            if (lastByte != ESC) {
              start = true;
            } else {
              lastByte = b;
            }
          }
        } catch (final EOFException e) {
          needReconnect = true;
        }

        if (start) {
          int i = 0;
          int seq = Integer.MAX_VALUE;
          boolean done = false;
          boolean escape = false;
          while (!done) {
            final byte b3 = socketIn.readByte();
            if (!escape && b3 == ETX) {
              done = true;
            } else if (!escape && b3 == ESC) {
              escape = true;
            } else {
              msgBuf[i++] = b3;
              escape = false;
            }

            // check for sequence number and reset index if found
            if (i == 6 && seq == Integer.MAX_VALUE) {
              seq = findSeq(new String(msgBuf, 0, 6));
              if (seq != Integer.MIN_VALUE) {
                i = 0;
              }
            }
          }
          // message done
          msgBuf[i++] = 0;
          final Message msg = MessageFactory.createMessage(msgBuf, i, seq);
          dispatchMessage(msg);
        }
        if (shutdown) {
          close();
        }
      } catch (final Exception e) {
        // TODO: suppress logging
        logger.severe("Main loop exception: " + e.getMessage() + ", will attempt reconnect.");
        e.printStackTrace();
        needReconnect = true;
      } catch (final OutOfMemoryError e) {
        outOfMemoryErrorOccurred(e);
      }
    }
  }

  public void sendAck(int seq) {

    final String ackString = "ACK:" + seq;

    final MessageLogo logo = new MessageLogo();
    logo.setInstallationId(ByteUtil.intToByte(Installation.INST_UNKNOWN));
    logo.setModule(ByteUtil.intToByte(Module.MOD_IMPORT_GENERIC));
    logo.setType(MessageType.TYPE_ACK);

    try {
      sendMsg(logo, ackString);
    } catch (final IOException e) {
      logger.warning("Exception sending ACK #" + seq);
    }
  }

  public void sendMsg(MessageLogo l, String s) throws IOException {
    final byte[] b = new byte[50];
    b[0] = STX;
    final byte[] lb = l.toDataStreamBytes();
    System.arraycopy(lb, 0, b, 1, 9);
    int j = 10;
    for (int i = 0; i < s.length(); i++, j++) {
      b[j] = ByteUtil.intToByte(s.charAt(i));
    }
    b[j++] = 0;
    b[j++] = ETX;
    socketOut.write(b, 0, j);
    socketOut.flush();
  }

  public void setExpectedHeartbeatInterval(int ms) {
    expectedHeartbeatInterval = ms;
  }

  public void setHeartbeatInterval(int ms) {
    heartbeatInterval = ms;
  }

  public void setHostAndPort(String h, int p) {
    host = h;
    port = p;
  }

  public void setLogger(Logger l) {
    logger = l;
  }

  public void setRecvIDString(String s) {
    recvIDString = s;
  }

  public void setSendIDString(String s) {
    sendIDString = s;
  }

  public void setTimeout(int ms) {
    timeout = ms;
  }

  public void shutdown() {
    shutdown = true;
  }
}
