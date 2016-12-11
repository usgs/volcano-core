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
import gov.usgs.volcanoes.core.args.parser.DimensionParser;


/**
 * Gather a date range from the command line.
 *
 * <p>A startTime and endTime Parameter will be added to the parameter list. Retrieve them with
 * getDate.
 *
 * @author Tom Parker
 */
public class DimensionArg extends ArgsDecorator {

  /**
   * Register arguments that define a date range.
   *
   * @param dateFormat Format string suitable for feeding to SimpleDateFormat
   * @param nextArg The Argument object I'm wrapping
   * @throws ArgumentException if parameters cannot be registered
   */
  public DimensionArg(String defaultDimensions, Arguments nextArg) throws ArgumentException {
    super(nextArg);

    final StringParser dimensionParser = new DimensionParser();
    
    StringBuffer helpStrB = new StringBuffer();
    helpStrB.append("dimension as heightxwidth in pixels. (example: 640x480)\n");
    
    if (defaultDimensions.length() > 0) {
      helpStrB.append("Defaults:\n").append(defaultDimensions);
    }
    
    nextArg.registerParameter(new FlaggedOption("dimension", dimensionParser, JSAP.NO_DEFAULT,
        JSAP.NOT_REQUIRED, 'd', "dimension", helpStrB.toString()));
  }

  public DimensionArg(Arguments nextArg) throws ArgumentException {
    this("", nextArg);
  }
}
