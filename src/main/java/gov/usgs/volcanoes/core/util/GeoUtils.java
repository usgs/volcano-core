package gov.usgs.volcanoes.core.util;

import java.awt.geom.Point2D;
import java.util.Comparator;

public class GeoUtils {
  public static final char DEGREE_SYMBOL = (char) 0xb0;

  /**
   * Converts signed double latitude to string north or south value.
   * @param lat latitude
   * @return string rep of latitude
   */
  public static String latitudeToString(double lat) {
    char ns = 'N';
    if (lat < 0) {
      ns = 'S';
    }

    lat = Math.abs(lat);
    return String.format("%.4f", lat) + DEGREE_SYMBOL + ns;
  }

  /**
   * Converts signed double longitude to string east or west value.
   * @param lon longitude
   * @return string rep of longitude
   */
  public static String longitudeToString(double lon) {
    while (lon < -180) {
      lon += 360;
    }
    while (lon > 180) {
      lon -= 360;
    }
    char ew = 'E';
    if (lon < 0) {
      ew = 'W';
    }

    lon = Math.abs(lon);
    return String.format("%.4f", lon) + DEGREE_SYMBOL + ew;
  }

  /**
   * Converts signed double longitude and latitude to string.
   * @see Util#longitudeToString(double)
   * @see Util#latitudeToString(double)
   */
  public static String lonLatToString(Point2D.Double pt) {
    return String.format("%s, %s", longitudeToString(pt.x), latitudeToString(pt.y));
  }

  /**
   * Constructs comparator to compare strings with ignore case mean.
   * @return std comparison result
   */
  public static Comparator<String> getIgnoreCaseStringComparator() {
    return new Comparator<String>() {
      public int compare(String o1, String o2) {
        return o1.compareToIgnoreCase(o2);
      }
    };
  }

}
