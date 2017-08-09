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
 * Gather a dimension from the command line.
 *
 * @author Tom Parker
 */
public class DimensionArg extends ArgsDecorator {
  private static final StringParser dimensionParser = new DimensionParser();
  private static final StringBuffer helpStrB = new StringBuffer();

  static {
    helpStrB.append("dimension as heightxwidth in pixels.  (example: 640x480)");
  }

  /**
   * Register dimension argument.
   * 
   * @param nextArg The Argument object I'm wrapping
   * @throws ArgumentException if parameters cannot be registered
   */
  public DimensionArg(Arguments nextArg) throws ArgumentException {
    super(nextArg);

    nextArg.registerParameter(new FlaggedOption("dimension", dimensionParser, JSAP.NO_DEFAULT,
        JSAP.REQUIRED, 'd', "dimension", helpStrB.toString()));
  }

  /**
   * Register dimension argument.
   *
   * @param defaultDimension Used when argument is not provided on command line. If no default is
   *        provided, argument is required.
   * @param nextArg The Argument object I'm wrapping
   * @throws ArgumentException if parameters cannot be registered
   */
  public DimensionArg(String defaultDimension, Arguments nextArg) throws ArgumentException {
    super(nextArg);

    helpStrB.append(String.format("default: %s", defaultDimension));

    nextArg.registerParameter(new FlaggedOption("dimension", dimensionParser, defaultDimension,
        JSAP.NOT_REQUIRED, 'd', "dimension", helpStrB.toString()));
  }
}
