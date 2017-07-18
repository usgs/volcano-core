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
import gov.usgs.volcanoes.core.args.parser.TimeSpanParser;


/**
 * Gather a date range from the command line.
 *
 * @author Tom Parker
 */
public class TimeSpanArg extends ArgsDecorator {

  /**
   * Register arguments that define a date range.
   *
   * @param dateFormat Format string suitable for feeding to SimpleDateFormat
   * @param nextArg The Argument object I'm wrapping
   * @throws ArgumentException if parameters cannot be registered
   */
  public TimeSpanArg(String dateFormat, boolean isRequired, Arguments nextArg)
      throws ArgumentException {
    super(nextArg);


    final StringParser timeSpanParser = new TimeSpanParser(dateFormat);
    nextArg.registerParameter(
        new FlaggedOption("timeSpan", timeSpanParser, JSAP.NO_DEFAULT, isRequired, 't', "timeSpan",
            String.format("Time span as %s-%s\n", dateFormat, dateFormat)));
  }
}
