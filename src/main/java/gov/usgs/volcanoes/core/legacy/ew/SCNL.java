package gov.usgs.volcanoes.core.legacy.ew;

import gov.usgs.volcanoes.core.contrib.HashCodeUtil;

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
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof SCNL)) {
      return false;
    }

    SCNL scnl = (SCNL) other;

    if (!station.equals(scnl.station)) {
      return false;
    } else if (!channel.equals(scnl.channel)) {
      return false;
    } else if (!network.equals(scnl.network)) {
      return false;
    } else if (location == null ^ scnl.location == null) {
      return false;
    } else if (!location.equals(scnl.location)) {
      return false;
    } else {
      return true;
    }
  }


  /**
   * Provide hashCode.
   */
  @Override
  public int hashCode() {
    int result = HashCodeUtil.SEED;
    result = HashCodeUtil.hash(result, station);
    result = HashCodeUtil.hash(result, channel);
    result = HashCodeUtil.hash(result, network);
    result = HashCodeUtil.hash(result, location);

    return result;
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
