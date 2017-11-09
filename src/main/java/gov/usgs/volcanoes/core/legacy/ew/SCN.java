package gov.usgs.volcanoes.core.legacy.ew;

/**
 * A class for holding SCN data.
 *
 * @author Dan Cervelli
 */
public class SCN {
  public final String channel;
  public final String network;
  public final String station;

  public SCN(String s, String c, String n) {
    station = s;
    channel = c;
    network = n;
  }

  @Override
  public String toString() {
    return station + "_" + channel + "_" + network;
  }
}
