/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args.parser;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

import gov.usgs.volcanoes.core.time.Time;
import gov.usgs.volcanoes.core.time.TimeSpan;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Parse a date from a command line argument.
 * 
 * @author Tom Parker
 */
public class TimeSpanParser extends StringParser {

  private SimpleDateFormat format;

  /**
   * Add my Parameter to the list.
   * 
   * @param inputFormat Format string suitable for feeding to SimpleDataFormat
   */
  public TimeSpanParser(String inputFormat) {
    format = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  @Override
  public Object parse(String arg) throws ParseException {

    int seperatorIdx = arg.lastIndexOf('-');

    String startString;
    String endString;
    if (seperatorIdx > 0) {
      startString = arg.substring(0, seperatorIdx);
      endString = arg.substring(seperatorIdx + 1);      
    } else {
      startString = arg;
      endString = "";
    }
    
    long endTime;
    long startTime;

    if (endString.length() == 0 || "now".equalsIgnoreCase(endString)) {
      endTime = System.currentTimeMillis();
    } else {
      try {
        endTime = format.parse(endString).getTime();
      } catch (java.text.ParseException e) {
        throw new ParseException("Unable to convert  end '" + endString + "' to a time.");
      }
    }

    try {
      startTime = format.parse(startString).getTime();
    } catch (java.text.ParseException ex) {

      double start = Time.getRelativeTime(startString);
      if (!Double.isNaN(start)) {
        startTime = endTime - (long) (start * 1000);

      } else {
        throw new ParseException("Unable to convert  start '" + startString + "' to a time.");
      }
    }
    
    return new TimeSpan(startTime, endTime);
  }
}
