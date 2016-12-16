/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args.decorator;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.UnflaggedOption;

import gov.usgs.volcanoes.core.args.ArgUtil;
import gov.usgs.volcanoes.core.args.ArgsDecorator;
import gov.usgs.volcanoes.core.args.ArgumentException;
import gov.usgs.volcanoes.core.args.Arguments;

/**
 * Gather a config filename from the command line.
 *
 * <P>The filename is taken from the first available unflagged option and placed in 
 * "config-filename". Be careful of the order that Args are added.
 *
 * @author Tom Parker
 */
public class ConfigFileArg extends ArgsDecorator {

  /**
   * Constructs a ConfigFileArg and adds its parameter to the list.
   *
   * @param defaultFileName The value to assign if the parameter is not given on the command line
   * @param nextArg The next Arguments object in the chain
   * @throws ArgumentException if new Parameter cannot be created. Not sure why this would happen
   */
  public ConfigFileArg(final String defaultFileName, final Arguments nextArg)
      throws ArgumentException {
    super(nextArg);

    boolean isRequired = ArgUtil.isRequired(defaultFileName);
    String helpString = "The config file name.";
    if (!isRequired) {
      helpString += " (default: " + defaultFileName + ")";
    }

    registerParameter(new UnflaggedOption("config-filename", JSAP.STRING_PARSER, defaultFileName,
        isRequired, JSAP.NOT_GREEDY, helpString));
  }
}
