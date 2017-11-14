package gov.usgs.volcanoes.core.legacy.ew;

import gov.usgs.volcanoes.core.time.Ew;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * A class that represents an item from an Earthworm Wave Server MENU listing.
 *
 * @author Dan Cervelli
 */
public class MenuItem implements Comparable<MenuItem> {
  public final String channel;
  public final String dataType;
  public final double endTime;
  public final String location;
  public final String network;
  public final int pin;
  public final double startTime;
  public final String station;

  public MenuItem(String item) {
    final String[] ss = item.split(" ");
    pin = Integer.parseInt(ss[0]);
    station = ss[1];
    channel = ss[2];
    network = ss[3];
    if (ss.length == 7) {
      location = null;
      startTime = Double.parseDouble(ss[4]);
      endTime = Double.parseDouble(ss[5]);
      dataType = ss[6];
    } else {
      location = ss[4];
      startTime = Double.parseDouble(ss[5]);
      endTime = Double.parseDouble(ss[6]);
      dataType = ss[7];
    }
  }

  @Override
  public int compareTo(MenuItem omi) {
    return getSCN("_").compareTo(omi.getSCN("_"));
  }

  public String getSCN(String d) {
    return station + d + channel + d + network;
  }

  public String getSCNL(String d) {
    return getSCN(d) + d + (location == null ? "--" : location);
  }

  public String getSCNSCNL(String d) {
    if (location == null || location.equals("--")) {
      return getSCN(d);
    } else {
      return getSCNL(d);
    }
  }

  public boolean isSCN(String s, String c, String n) {
    return (station.equals(s) && channel.equals(c) && network.equals(n));
  }

  public boolean match(String s, String c, String n, String l) {
    if (s != null && !s.equals("*") && !Pattern.matches(s, station)) {
      return false;
    }

    if (c != null && !c.equals("*") && !Pattern.matches(c, channel)) {
      return false;
    }

    if (n != null && !n.equals("*") && !Pattern.matches(n, network)) {
      return false;
    }

    if (l == null) {
      return (location == null || location.equals("--"));
    }

    if (l.equals("*")) {
      return true;
    }

    if (location == null) {
      return false;
    }

    if (!Pattern.matches(l, location)) {
      return false;
    }

    return true;
  }

  public String toFullString() {
    final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    final String t1 = df.format(Ew.asDate(startTime));
    final String t2 = df.format(Ew.asDate(endTime));
    return String.format("%4d %16s %3s %14.2f (%s) %14.2f (%s)", pin, getSCNL("_"), dataType,
        startTime, t1, endTime, t2);
  }

  @Override
  public String toString() {
    return getSCN("_");
  }
}
