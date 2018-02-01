package gov.usgs.volcanoes.core.legacy.net;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A singleton class meant to hold the connection parameters for a daemon
 *
 * @author Tom Parker
 *
 */
public class Connections {
  private Map<SocketChannel, ConnectionStatistics> connectionStats = Collections.synchronizedMap(new HashMap<SocketChannel, ConnectionStatistics>());
  private long connectionIndex = 0;
  private int maxConnections = 20;

  private Connections() {
    connectionStats = Collections.synchronizedMap(new HashMap<SocketChannel, ConnectionStatistics>());
  }

  public static Connections getInstance() {
    return ConnectionsHolder.instance;
  }

  public int getNumConnections() {
    return connectionStats.size();
  }

  public Collection<ConnectionStatistics> getConnectionStats() {
    return connectionStats.values();
  }

  public Map<SocketChannel, ConnectionStatistics> getConnections() {
    return connectionStats;
  }

  public ConnectionStatistics getConnectionStatistics(SocketChannel channel) {
    ConnectionStatistics cs = connectionStats.get(channel);
    if (cs == null) {
      cs = new ConnectionStatistics(channel);
      cs.address = getHost(channel);
      cs.index = connectionIndex++;
      cs.connectTime = System.currentTimeMillis();
      connectionStats.put(channel, cs);
    }
    return cs;
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

  private static class ConnectionsHolder {
    public static Connections instance = new Connections();

  }

  public void remove(SocketChannel channel) {
    connectionStats.remove(channel);
  }

  public void sent(SocketChannel channel, int bytesSent) {
    ConnectionStatistics cs = connectionStats.get(channel);
    if (cs != null)
      cs.sent(bytesSent);
  }

  public void read(SocketChannel channel, int bytesRead) {
    ConnectionStatistics cs = connectionStats.get(channel);
    if (cs != null)
      cs.read(bytesRead);
  }

  public void beginCommand(SocketChannel channel, String commandString) {
    ConnectionStatistics cs = connectionStats.get(channel);
    if (cs != null)
      cs.beginCommand(commandString);
  }

  public String printConnections(String s) {
    char col = 'T';
    if (s.length() > 1)
      col = s.charAt(1);
    boolean desc = s.endsWith("-");

    List<ConnectionStatistics> css = new ArrayList<ConnectionStatistics>(connectionStats.size());
    css.addAll(connectionStats.values());

    Collections.sort(css, ConnectionStatistics.getComparator(ConnectionStatistics.SortOrder.parse(col), desc));
    StringBuffer sb = new StringBuffer();
    sb.append("------- Connections --------\n");
    sb.append(ConnectionStatistics.getHeaderString());
    for (ConnectionStatistics cs : css) {
      sb.append(cs.getConnectionString() + "\n");
    }
    sb.append(ConnectionStatistics.getHeaderString());
    sb.append("\n\nOpen client connections:    " + connectionStats.size() + "/" + maxConnections + "\n");

    return(sb.toString());
  }

  public String printCommands(String s) {
    char col = 'T';
    if (s.length() > 1)
      col = s.charAt(1);
    boolean desc = s.endsWith("-");

    List<ConnectionStatistics> css = new ArrayList<ConnectionStatistics>(connectionStats.size());
    css.addAll(connectionStats.values());

    Collections.sort(css, ConnectionStatistics.getComparator(ConnectionStatistics.SortOrder.parse(col), desc));
    StringBuffer sb = new StringBuffer();
    sb.append("\n------- Running Commands --------\n");
    sb.append(ConnectionStatistics.getCommandHeaderString());
    int runningCount = 0;
    for (ConnectionStatistics cs : css) {
      String command = cs.getCommandString();
      if (command != null) {
        sb.append(command);
        runningCount++;
      }
    }
    if (runningCount == 0)
      sb.append("\n  < All handlers are idle >  \n");

    return(sb.toString());
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public int connectionCount() {
    return connectionStats.size();
  }

  public void toggleTrace(int index) {
    for (SocketChannel sc : connectionStats.keySet()) {
      ConnectionStatistics cs = connectionStats.get(sc);
      if (cs.index == index)
        cs.isTraced = cs.isTraced ? false : true;
    }

  }

  public boolean isTraced(SocketChannel channel) {
    ConnectionStatistics cs = connectionStats.get(channel);
    return cs.isTraced;
  }

  public void endCommand(SocketChannel channel) {
    ConnectionStatistics cs = connectionStats.get(channel);
    if (cs != null)
      cs.endCommand();
  }

}

