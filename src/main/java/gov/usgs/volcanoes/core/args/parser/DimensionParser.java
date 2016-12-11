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

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Parse a date from a command line argument.
 * 
 * @author Tom Parker
 */
public class DimensionParser extends StringParser {

  @Override
  public Object parse(String arg) throws ParseException {
    int seperatorIdx = arg.indexOf('x');
    
    if (seperatorIdx > 1) {
      throw new ParseException("Cannot parse dimension: %s" + arg);
    } 

    int height = Integer.parseInt(arg.substring(0, seperatorIdx));
    int width = Integer.parseInt(arg.substring(seperatorIdx + 1, arg.length()));
    
    return new Dimension(width, height);
  }
}
