/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * A utility class for dealing with time, especially formatting and J2Ks.
 *
 * @author Dan Cervelli
 * @author Tom Parker
 */
public final class Time {
  // public static final String ISO_8601_TIME_FORMAT = "yyyyMMdd'T'HHmmss.SSSS'Z'";

  /**
   * format used by the FDSNWS standard. Almost, but not quite, ISO. This doesn't really belong
   * here.
   */
  public static final String FDSN_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSS";

  private static Map<String, SimpleDateFormat> formats;

  /** Format used for time input. */
  public static final String INPUT_TIME_FORMAT = "yyyyMMddHHmmss";

  /** standard display format. */
  public static final String STANDARD_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /** standard display format with ms. */
  public static final String STANDARD_TIME_FORMAT_MS = "yyyy-MM-dd HH:mm:ss.SSS";

  /** Seconds in a year. */
  public static final double YEAR_IN_S = 31557600;

  static {
    formats = new HashMap<String, SimpleDateFormat>();
  }


  /**
   * Convert a Ew to J2kSec.
   *
   * @param ew Date as Ew
   * @return date as j2kSec
   */
  public static double ewToj2k(double ew) {
    return J2kSec.fromEpoch(Ew.asEpoch(ew));
  }


  /**
   * Formats {@link Date} by format name.
   *
   * @param format Name of format to search
   * @param date date to format
   * @return Formatted date as string
   */
  public static synchronized String format(String format, Date date) {
    return getFormat(format).format(date);
  }

  /**
   * Formats long date by format name.
   *
   * @param format Name of format to search
   * @param time long date to format
   * @return Formatted date as string
   */
  public static synchronized String format(String format, long time) {
    return format(format, new Date(time));
  }

  /**
   * Searches in internal formats list.
   *
   * @param format Format name to search
   * @return Found SimpleDateFormat or, if absent, initialized with fs string one
   */
  protected static SimpleDateFormat getFormat(String format) {
    SimpleDateFormat sdFormat = formats.get(format);
    if (sdFormat == null) {
      sdFormat = new SimpleDateFormat(format);
      sdFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      formats.put(format, sdFormat);
    }
    return sdFormat;
  }

  /**
   * Converts a Valve style relative time to a number of seconds. First
   * character must be '-', then a number, then one of these characters:
   * s, i, h, d, w, y.
   *
   * @param timeStr the time string
   * @return the number of seconds this represents (or NaN on error)
   */
  public static double getRelativeTime(String timeStr) {
    if (timeStr == null || timeStr.charAt(0) != '-') {
      return Double.NaN;
    }

    int number = Integer.MIN_VALUE;
    try {
      number = Integer.parseInt(timeStr.substring(1, timeStr.length() - 1));
    } catch (final Exception e) {
      return Double.NaN;
    }
    final char unit = timeStr.charAt(timeStr.length() - 1);
    double unitSize = 0;
    switch (unit) {
      case 's':
        unitSize = 1;
        break;
      case 'i':
        unitSize = 60;
        break;
      case 'h':
        unitSize = 60 * 60;
        break;
      case 'd':
        unitSize = 60 * 60 * 24;
        break;
      case 'w':
        unitSize = 60 * 60 * 24 * 7;
        break;
      case 'm':
        unitSize = 60 * 60 * 24 * 30;
        break;
      case 'y':
        unitSize = 60 * 60 * 24 * 365;
        break;
      default:
        return Double.NaN;
    }

    return number * unitSize;
  }

  /**
   * Convert a J2kSec to Ew.
   *
   * @param j2k Date as J2kSec
   * @return date as Ew
   */
  public static double j2kToEw(double j2k) {
    return Ew.fromEpoch(J2kSec.asEpoch(j2k));
  }

  /**
   * @param timeRange two string dates in "yyyyMMddHHmmss" format or relative time, divided by
   *          comma.
   * 
   * @return array of two doubles - start and end J2K dates
   * @throws ParseException when the string looks odd
   */
  public static synchronized double[] parseTimeRange(String timeRange) throws ParseException {
    if (timeRange == null || timeRange.equals("")) {
      throw new ParseException("Time range is null.", -1);
    }

    final double[] result = new double[2];
    final String[] ss = timeRange.split(",");

    result[1] = J2kSec.now();
    result[0] = 0;
    if (ss.length == 2) {
      if (ss[1].charAt(0) == '-') {
        final double rt = Time.getRelativeTime(ss[1]);
        if (Double.isNaN(rt)) {
          throw new ParseException("Unparsable relative end time.", -1);
        }
        result[1] = result[1] - rt;
      } else {
        result[1] = J2kSec.fromDate(getFormat(INPUT_TIME_FORMAT).parse(ss[1]));
      }
    }
    if (ss[0].charAt(0) == '-') {
      result[0] = Time.getRelativeTime(ss[0]);
      if (Double.isNaN(result[0])) {
        throw new ParseException("Unparsable relative start time.", -1);
      }
      result[0] = result[1] - result[0];
    } else {
      result[0] = J2kSec.fromDate(getFormat(INPUT_TIME_FORMAT).parse(ss[0]));
    }

    return result;
  }

  /**
   * Formats {@link Date} as "yyyy-MM-dd HH:mm:ss".
   *
   * @param date date
   * @return formatted date
   */
  public static String toDateString(Date date) {
    return format(STANDARD_TIME_FORMAT, date);
  }

  /**
   * Formats long date as "yyyy-MM-dd HH:mm:ss".
   *
   * @param time Date
   * @return formatted date
   */
  public static String toDateString(long time) {
    return format(STANDARD_TIME_FORMAT, time);
  }


  /**
   * Formats {@link Date} as "yyyyMMddHHmmss".
   *
   * @param date date
   * @return formatted date
   */
  public static String toShortString(Date date) {
    return format(INPUT_TIME_FORMAT, date);
  }



  /**
   * Uninstantiatable.
   */
  private Time() {}

}
