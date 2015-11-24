/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.util;

import java.util.Comparator;

/**
 * Utility class containing methods for working with Strings.
 *
 * @author Tom Parker
 */
public final class StringUtils {

  /** Noted here to keep things simple. */
  public static final char DEGREE_SYMBOL = (char) 0xb0;

  /**
   * Constructs comparator to compare strings with ignore case mean.
   *
   * @return std comparison result
   */
  public static Comparator<String> getCaseInsensitiveStringComparator() {
    return new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o1.compareToIgnoreCase(o2);
      }
    };
  }

  /**
   * Converts a string to a boolean. To avoid confusion there is no default
   * value. This simply returns true on a val of "1" or "true".
   *
   * @param val the string that represents the boolean
   * @return the boolean
   */
  public static boolean stringToBoolean(String val) {
    if (val == null) {
      return false;
    }
    return (val.toLowerCase().equals("true") || val.equals("1") || val.toLowerCase().equals("t"));
  }

  /**
   * Converts a string to a boolean. Returns default value on null input.
   *
   * @param val the string that represents the boolean
   * @param def the default value
   * @return the boolean
   */
  public static boolean stringToBoolean(String val, boolean def) {
    if (val == null) {
      return def;
    }
    return (val.toLowerCase().equals("true") || val.equals("1") || val.toLowerCase().equals("t"));
  }

  /**
   * Converts a string to a double, sets to user-specified default if
   * there's an exception.
   *
   * @param val the string that represents the double
   * @param def the default value
   * @return the double
   */
  public static double stringToDouble(String val, double def) {
    double ret;
    try {
      ret = Double.parseDouble(val);
    } catch (final Exception e) {
      ret = def;
    }
    return ret;
  }

  /**
   * Converts a string to an integer, sets to user-specified default if
   * there's an exception.
   *
   * @param val the string that represents the integer
   * @param def the default value
   * @return the integer
   */
  public static int stringToInt(String val, int def) {
    int ret;
    try {
      ret = Integer.parseInt(val);
    } catch (final Exception e) {
      ret = def;
    }
    return ret;
  }

  /**
   * Checks if a string is null and returns a default string if it is.
   *
   * @param val the original string
   * @param def the default in case of null
   * @return the original string if != null, otherwise default string
   */
  public static String stringToString(String val, String def) {
    return (val == null) ? def : val;
  }

  /** uninstantiatable. */
  private StringUtils() {}
}
