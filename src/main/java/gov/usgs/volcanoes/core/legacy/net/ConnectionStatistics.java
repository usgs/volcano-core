package gov.usgs.volcanoes.core.legacy.net;

import gov.usgs.volcanoes.core.time.Time;

import java.nio.channels.SocketChannel;
import java.util.Comparator;

/**
 *
 *
 * @author Dan Cervelli
 */
public class ConnectionStatistics {
  public enum SortOrder {
    ADDRESS, CONNECT_TIME, LAST_REQUEST_TIME, RX_BYTES, TX_BYTES, INDEX;

    public static SortOrder parse(char c) {
      switch (Character.toUpperCase(c)) {
        default:
        case 'A':
          return ADDRESS;
        case 'L':
          return LAST_REQUEST_TIME;
        case 'C':
          return CONNECT_TIME;
        case 'R':
          return RX_BYTES;
        case 'T':
          return TX_BYTES;
        case 'I':
          return INDEX;
      }
    }
  }

  public SocketChannel channel;
  public String address;
  public long index;
  public long connectTime;
  public long lastRequestTime;
  public long numBytesReceived;
  public long numBytesSent;
  private String lastCommand;
  private boolean runningCommand;
  private long commandStart;
  public boolean isTraced;

  public ConnectionStatistics(SocketChannel ch) {
    channel = ch;
    runningCommand = false;
    isTraced = false;
  }

  public void touch() {
    lastRequestTime = System.currentTimeMillis();
  }

  public void beginCommand(String command) {
    runningCommand = true;
    commandStart = System.currentTimeMillis();
    lastCommand = command;

    int eol = lastCommand.indexOf('\n');
    if (eol != -1)
      lastCommand = lastCommand.substring(0, eol);

  }

  public void endCommand() {
    runningCommand = false;
  }

  public void read(int nb) {
    numBytesReceived += nb;
  }

  public void sent(int nb) {
    numBytesSent += nb;
  }

  public static String getHeaderString() {
    return "[A]ddress        [C]onnect time         [L]ast time            [R]X bytes  [T]X bytes  [I]ndex\n";
  }

  public String getConnectionString() {
    String s = String.format("%-16s %-22s %-22s %-11d %-11d %-11d", address, Time.toDateString(connectTime),
        Time.toDateString(lastRequestTime), numBytesReceived, numBytesSent, index);
    return s;
  }

  public static String getCommandHeaderString() {
    return "Index     Address         Time (s)\n";
  }

  public String getCommandString() {
    if (!runningCommand)
      return null;

    double runTime = ((System.currentTimeMillis() - commandStart) / 1000d);
    String s = String.format("%-9d %-15s %-6.3f\n\t%s\n", index, address, runTime, lastCommand);
    return s;
  }

  public static Comparator<ConnectionStatistics> getComparator(final SortOrder order, final boolean desc) {
    return new Comparator<ConnectionStatistics>() {
      public int compare(ConnectionStatistics cs1, ConnectionStatistics cs2) {
        int cmp = 0;
        switch (order) {
          default:
          case ADDRESS:
            cmp = cs1.address.compareTo(cs2.address);
            break;
          case LAST_REQUEST_TIME:
            cmp = (int) (cs1.lastRequestTime - cs2.lastRequestTime);
            break;
          case CONNECT_TIME:
            cmp = (int) (cs1.connectTime - cs2.connectTime);
            break;
          case RX_BYTES:
            cmp = (int) (cs1.numBytesReceived - cs2.numBytesReceived);
            break;
          case TX_BYTES:
            cmp = (int) (cs1.numBytesSent - cs2.numBytesSent);
            break;
          case INDEX:
            cmp = (int) (cs1.index - cs2.index);
            break;

        }
        if (desc)
          cmp = -cmp;

        return cmp;
      }
    };
  }
}

