/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.time;

import java.text.ParseException;
import java.util.Date;

/**
 * A utility class for working with j2ksecs. J2ksec is a time standard created by Dan Cervelli for
 * use with winston and associated tools. The j2ksec ephoch is 2000-01-01T00:00+12:00. 
 * 
 * @author Tom Parker
 *
 */
public class J2kSec {
  public static final double UNIXZERO = -946728000;


  /**
   * Converts a j2ksec to a <CODE>Date</CODE> object.
   *
   * @param j2k the j2ksec
   * @return the Date
   */
  public static java.util.Date asDate(double j2k) {
    return new java.util.Date((long) (1000 * (j2k - UNIXZERO)));
  }

  /**
   * Converts a <CODE>Date</CODE> object into a j2ksec.
   * 
   * @param date the Date
   * @return date in j2ksec
   */
  public static double fromDate(Date date) {
    return ((double) date.getTime() / 1000) + UNIXZERO;

  }

  /**
   * Formats J2K date.
   *
   * @param format Name of format to search
   * @param j2k date to format
   * @return Formatted date as string
   */
  public static synchronized String format(String format, double j2k) {
    return Time.format(format, asDate(j2k));
  }

  /**
   * Formats J2K date as "yyyy-MM-dd HH:mm:ss".
   *
   * @param j2k date
   * @return formatted date
   */
  public static String toDateString(double j2k) {
    return format(Time.STANDARD_TIME_FORMAT, j2k);
  }

  /**
   * Parse string into J2K date, log errors.
   *
   * @param format Format name to parse
   * @param date date string
   * @return parsed J2K date or 0 if exception occurred
   * @throws ParseException when string cannot be parsed
   */
  public static synchronized double parse(String format, String date) throws ParseException {
    return fromDate(Time.getFormat(format).parse(date));
  }

  /**
   * Uninstantiatable.
   */
  private J2kSec() {}

}
