/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.time;

import java.text.DateFormat;
import java.text.DecimalFormat;
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

  /** my default time zone. */
  public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC");

  private static DecimalFormat diffFormat;

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

  /** Seconds in a day. */
  public static final int DAY_IN_S = 60 * 60 * 24;

  /** Seconds in a year. */
  public static final double YEAR_IN_S = DAY_IN_S * 365.25;

  static {
    formats = new HashMap<String, SimpleDateFormat>();
    diffFormat = new DecimalFormat("#.##");

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
    return format(format, date, DEFAULT_TIME_ZONE);
  }

  /**
   * Formats {@link Date} by format name.
   *
   * @param format Name of format to search
   * @param date date to format
   * @return Formatted date as string
   */
  public static synchronized String format(String format, Date date, TimeZone tz) {
    final SimpleDateFormat dateF = new SimpleDateFormat(format);
    dateF.setTimeZone(tz);
    return dateF.format(date);
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
    } catch (final Exception ex) {
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
   * <p>Main class method.</p>
   * <p>Usage:</p>
   * <p>without arguments - prints usage message</p>
   * <p>-j2d [j2k] j2k to date</p>
   * <p>-d2j [yyyymmddhhmmss] date to j2k</p>
   * <p>-e2d [ewtime] earthworm to date</p>
   * <p>-md5 [string] md5 of string</p>
   * <p>-md5r [resource] md5 of a resource (filename, url)</p>
   *
   * @param args command line args
   * @throws Exception when things go wrong
   */
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("-j2d [j2k]        j2k to date");
      System.out.println("-j2e [j2k]              j2k to earthworm");
      System.out.println("-d2j [yyyymmddhhmmss] date to j2k");
      System.out.println("-e2d [ewtime]           earthworm to date");
    } else {
      final DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      final DateFormat df2 = new SimpleDateFormat("yyyyMMddHHmmss");
      df1.setTimeZone(TimeZone.getTimeZone("GMT"));
      df2.setTimeZone(TimeZone.getTimeZone("GMT"));
      if (args[0].equals("-j2d")) {
        System.out.println(df1.format(J2kSec.asDate(Double.parseDouble(args[1]))));
      } else if (args[0].equals("-j2e")) {
        System.out.println(Time.j2kToEw(Double.parseDouble(args[1])));
      } else if (args[0].equals("-d2j")) {
        System.out.println(J2kSec.fromDate(df2.parse(args[1])));
      } else if (args[0].equals("-e2d")) {
        System.out.println(df1.format(Ew.asDate(Double.parseDouble(args[1]))));
      }
    }
  }

  /**
   * TODO: convert to TimeSpan class
   * 
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
   * Returns a string of this format: "#d #h #m #s" representing the amount
   * of time between two dates.
   *
   * @param diff the difference (seconds)
   * @return the string representing this difference
   */
  public static String secondsToString(double diff) {
    String diffString = "";
    if (diff < 60) {
      diffString = diffFormat.format(diff) + "s";
    } else if (diff < 3600) {
      diffString = (int) (diff / 60) + "m " + diffFormat.format(diff % 60) + "s";
    } else if (diff < 86400) {
      diffString = (int) (diff / 3600) + "h ";
      diff -= 3600 * (int) (diff / 3600);
      diffString = diffString + (int) (diff / 60) + "m " + diffFormat.format(diff % 60) + "s";
    } else {
      diffString = (int) (diff / 86400) + "d ";
      diff -= 86400 * (int) (diff / 86400);
      diffString = diffString + (int) (diff / 3600) + "h ";
      diff -= 3600 * (int) (diff / 3600);
      diffString = diffString + (int) (diff / 60) + "m " + diffFormat.format(diff % 60) + "s";
    }
    return diffString;
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
