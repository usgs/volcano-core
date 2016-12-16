/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args.decorator;


import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.StringParser;

import gov.usgs.volcanoes.core.args.ArgUtil;
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

  public DimensionArg(Arguments nextArg) throws ArgumentException {
    this("", nextArg);
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

    final StringParser dimensionParser = new DimensionParser();

    final StringBuffer helpStrB = new StringBuffer();
    helpStrB.append("dimension as heightxwidth in pixels.");

    boolean isRequired = ArgUtil.isRequired(defaultDimension);

    // optional args have defaults
    if (isRequired) {
      helpStrB.append(" (example: 640x480)");
    } else {
      helpStrB.append(String.format("default: %s", defaultDimension));
    }

    nextArg.registerParameter(new FlaggedOption("dimension", dimensionParser, JSAP.NO_DEFAULT,
        isRequired, 'd', "dimension", helpStrB.toString()));
  }
}
