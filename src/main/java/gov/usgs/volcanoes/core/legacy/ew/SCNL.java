package gov.usgs.volcanoes.core.legacy.ew;


/**
 * A class for holding SCNL data.
 *
 * @author Tom Parker
 */
public class SCNL {

  public final String channel;
  public final String location;
  public final String network;
  public final String station;

  public SCNL(String s, String c, String n) {
    station = s;
    channel = c;
    network = n;
    location = null;
  }

  public SCNL(String s, String c, String n, String l) {
    station = s;
    channel = c;
    network = n;
    location = l;
  }


  /**
   * Compare another SCNL to this SCNL. A null location is different than a "--" location
   * @param scnl
   * @return Are they equal?
   */
  public boolean equals(SCNL scnl) {
    if (!station.equals(scnl.station)) {
      return false;
    }
    if (!channel.equals(scnl.channel)) {
      return false;
    }
    if (!network.equals(scnl.network)) {
      return false;
    }
    if (location == null ^ scnl.location == null) {
      return false;
    }
    if (!location.equals(scnl.location)) {
      return false;
    }

    return true;
  }

  /**
   * SCNL is a SCNL even with a "--" location
   * @return
   */
  public boolean isSCNL() {
    return location != null;
  }

  public String toSCN(String d) {
    return station + d + channel + d + network;
  }

  public String toSCNL(String d) {
    return toSCN(d) + d + location;
  }

  @Override
  public String toString() {
    if (location == null) {
      return toSCN("_");
    } else {
      return toSCNL("_");
    }
  }
}
