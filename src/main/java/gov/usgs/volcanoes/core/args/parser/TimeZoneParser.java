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
public class TimeZoneParser extends StringParser {

  private SimpleDateFormat format;

  @Override
  public Object parse(String arg) throws ParseException {

    TimeZone timeZone = TimeZone.getTimeZone(arg);
    
    return timeZone;
  }
}
