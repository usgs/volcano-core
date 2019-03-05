/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for working with QuakeML files.
 *
 * @author Tom Parker
 *
 */
public class QuakeMlUtils {
  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
  private static final Logger LOGGER = LoggerFactory.getLogger(QuakeMlUtils.class);

  /**
   * Parse a QuakeML time string. Similar to ISO 8601, but not quite the same. Because yet another
   * time standard is exactly what we need. When will we learn?
   *
   * @param timeString Time string
   * @return time in typical epoch ms
   */
  public static long parseTime(String timeString) {
    /*
     * final String inString = timeString; timeString = timeString.replaceFirst("\\.(\\d)Z?",
     * ".$100Z"); timeString = timeString.replaceFirst("\\.(\\d{2})Z?$", ".$10Z"); timeString =
     * timeString.replaceFirst(":(\\d{2})Z?$", ":$1.000Z");
     * 
     * long time = Long.MIN_VALUE; final SimpleDateFormat dateF = new SimpleDateFormat(DATE_FORMAT);
     * dateF.setTimeZone(TimeZone.getTimeZone("UTC")); try { time =
     * dateF.parse(timeString).getTime(); } catch (final ParseException ex) {
     * LOGGER.error("Cannot parse time String {}", inString); throw new
     * RuntimeException("Cannot parse time string " + inString); } return time;
     */
    return parseDate(timeString).getTime();
  }

  /**
   * Parse a QuakeML time string. Similar to ISO 8601, but not quite the same. Because yet another
   * time standard is exactly what we need. When will we learn?
   * 
   * @param timeString Time string
   * @return date object
   */
  public static Date parseDate(String timeString) {
    final String inString = timeString;
    timeString = inString.replaceFirst("\\.(\\d)Z?", ".$100Z");
    timeString = inString.replaceFirst("\\.(\\d{2})Z?$", ".$10Z");
    timeString = inString.replaceFirst(":(\\d{2})Z?$", ":$1.000Z");

    final SimpleDateFormat dateF = new SimpleDateFormat(DATE_FORMAT);
    dateF.setTimeZone(TimeZone.getTimeZone("UTC"));
    try {
      Date dt = dateF.parse(timeString);
      return dt;
    } catch (final ParseException ex) {
      LOGGER.error("Cannot parse time String {}", inString);
      throw new RuntimeException("Cannot parse time string " + inString);
    }
  }

  /**
   * Format date for QuakeML.
   * 
   * @param millis milliseconds since 1/1/1970 00:00:00 GMT
   * @return time string in yyyy-MM-dd'T'HH:mm:ss.SSSX format
   */
  public static String formatDate(long millis) {
    SimpleDateFormat dateF = new SimpleDateFormat(DATE_FORMAT);
    dateF.setTimeZone(TimeZone.getTimeZone("UTC"));
    Date date = new Date(millis);
    return dateF.format(date);
  }
}
