/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
   * Parse a QuakeML time string.
   * 
   * @param timeString Time string
   * @return time in typical epoch ms
   */
  public static long parseTime(String timeString) {
    final String inString = timeString;
    timeString = timeString.replaceFirst("\\.(\\d)Z?", ".$100Z");
    timeString = timeString.replaceFirst("\\.(\\d{2})Z?$", ".$10Z");
    timeString = timeString.replaceFirst(":(\\d{2})Z?$", ":$1.000Z");

    long time = Long.MIN_VALUE;
    final SimpleDateFormat dateF = new SimpleDateFormat(DATE_FORMAT);
    try {
      time = dateF.parse(timeString).getTime();
    } catch (final ParseException ex) {
      LOGGER.error("Cannot parse time String {}", inString);
      throw new RuntimeException("Cannot parse time string " + inString);
    }
    return time;
  }
}
