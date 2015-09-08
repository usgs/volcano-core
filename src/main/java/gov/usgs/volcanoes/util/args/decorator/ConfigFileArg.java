/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */
package gov.usgs.volcanoes.util.args.decorator;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.UnflaggedOption;

import gov.usgs.volcanoes.util.args.ArgsDecorator;
import gov.usgs.volcanoes.util.args.Arguments;

/**
 * Gather a config filename from the command line and place it in "config-filename". The filename is
 * taken from the first available unflagged option. Be careful of the order that Args are added.
 * 
 * @author Tom Parker
 */
public class ConfigFileArg extends ArgsDecorator {

  /**
   * Constructs a ConfigFileArg and adds its parameter
   * 
   * @param defaultFileName The value to assign if the parameter is not given on the command line
   * @param nextArg The next Arguments object in the chain.
   * @throws JSAPException if new Parameter cannot be craeted. Not sure why this would happen
   */
  public ConfigFileArg(final String defaultFileName, final Arguments nextArg) throws JSAPException {
    super(nextArg);

    registerParameter(new UnflaggedOption("config-filename", JSAP.STRING_PARSER, defaultFileName,
        JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "The config file name."));
  }
}
