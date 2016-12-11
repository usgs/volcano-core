/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args.decorator;


import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.StringParser;

import gov.usgs.volcanoes.core.args.ArgsDecorator;
import gov.usgs.volcanoes.core.args.ArgumentException;
import gov.usgs.volcanoes.core.args.Arguments;
import gov.usgs.volcanoes.core.args.parser.TimeZoneParser;


/**
 * Gather a date range from the command line.
 *
 * <p>A startTime and endTime Parameter will be added to the parameter list. Retrieve them with
 * getDate.
 *
 * @author Tom Parker
 */
public class TimeZoneArg extends ArgsDecorator {
  
  public static final String DEFAULT_TIME_ZONE = "Etc/UTC";

  /**
   * Register arguments that define a date range.
   *
   * @param dateFormat Format string suitable for feeding to SimpleDateFormat
   * @param nextArg The Argument object I'm wrapping
   * @throws ArgumentException if parameters cannot be registered
   */
  public TimeZoneArg(Arguments nextArg) throws ArgumentException {
    super(nextArg);

    
    final StringParser timeZoneParser = new TimeZoneParser();
    nextArg.registerParameter(new FlaggedOption("timeZone", timeZoneParser, DEFAULT_TIME_ZONE,
        JSAP.NOT_REQUIRED, 'z', "timeZone", "Time zone. Only affects plot. All args must be given in UTC."));
  }
}
