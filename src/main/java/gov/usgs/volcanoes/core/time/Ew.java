/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.time;

import java.text.ParseException;
import java.util.Date;

/**
 * A utility class for working with time as decimal seconds since the epoch.
 * 
 * @author Tom Parker
 *
 */
public class Ew {
  
  /**
   * Converts ew time to a <CODE>Date</CODE> object.
   *
   * @param ew the ew time
   * @return the Date
   */
  public static Date asDate(double ew) {
    return new Date(asEpoch(ew));
  }

  /**
   * Converts a <CODE>Date</CODE> object into ew time.
   * 
   * @param date the Date
   * @return date as ew time
   */
  public static double fromDate(Date date) {
    return fromEpoch(date.getTime());

  }

  /**
   * Converts ew time to epoch time.
   * 
   * @param ew the ew time
   * @return date as UNIX epoch
   */
  public static long asEpoch(double ew) {
    return ((long)ew * 1000);
  }

  /**
   * Converts a epoch ms time to ew time
   * 
   * @param date the date as provided by Date.getTime()
   * @return date as ew time
   */
  public static double fromEpoch(Long date) {
    return (double) date / 1000;
  }

  /**
   * Formats ew date.
   *
   * @param format Name of format to search
   * @param ew date to format
   * @return Formatted date as string
   */
  public static synchronized String format(String format, double ew) {
    return Time.format(format, asDate(ew));
  }

  /**
   * Formats J2K date as "yyyy-MM-dd HH:mm:ss".
   *
   * @param ew date
   * @return formatted date
   */
  public static String toDateString(double ew) {
    return format(Time.STANDARD_TIME_FORMAT, ew);
  }

  /**
   * Parse string into ew date, log errors.
   *
   * @param format Format name to parse
   * @param date date string
   * @return parsed ew date or 0 if exception occurred
   * @throws ParseException when string cannot be parsed
   */
  public static synchronized double parse(String format, String date) throws ParseException {
    return fromDate(Time.getFormat(format).parse(date));
  }

  /**
   * Return the current timestamp.
   * 
   * @return present time as a j2ksec
   */
  public static double now() {
    return fromEpoch(CurrentTime.getInstance().now());
  }


  /**
   * Uninstantiatable.
   */
  private Ew() {}

}
