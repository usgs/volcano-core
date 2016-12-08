/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args.parser;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

import gov.usgs.volcanoes.core.time.Time;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Parse a date from a command line argument.
 * 
 * @author Tom Parker
 */
public class DateStringParser extends StringParser {

  private SimpleDateFormat format;

  /**
   * Add my Parameter to the list.
   * 
   * @param inputFormat Format string suitable for feeding to SimpleDataFormat
   */
  public DateStringParser(String inputFormat) {
    format = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  @Override
  public Object parse(String arg) throws ParseException {
    Date result = null;
    
    try {
      result = format.parse(arg);
    } catch (java.text.ParseException ex) {
      long time = System.currentTimeMillis();

      double span = Time.getRelativeTime(arg);
      if (!Double.isNaN(span)) {
        time -= (long) (span * 1000);
    
      } else if (!"now".equalsIgnoreCase(arg)) {
        throw new ParseException("Unable to convert '" + arg + "' to a Date.");
      }
      
      result = new Date(time);
    }
    return result;
  }
}
