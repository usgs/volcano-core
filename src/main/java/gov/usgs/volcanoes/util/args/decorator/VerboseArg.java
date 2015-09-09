/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.util.args.decorator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import gov.usgs.volcanoes.util.args.ArgsDecorator;
import gov.usgs.volcanoes.util.args.Arguments;

/**
 * Gather a verbose flag from the command line.
 *
 * @author Tom Parker
 */
public class VerboseArg extends ArgsDecorator {

  /**
   *
   * @param nextArg The Argument object that I'm wrapping
   * @throws JSAPException if Parameters cannot be registered.
   */
  public VerboseArg(Arguments nextArg) throws JSAPException {
    super(nextArg);
    nextArg.registerParameter(new Switch("verbose", 'v', "verbose", "Verbose logging."));
  }

  @Override
  public JSAPResult parse(String[] args) throws Exception {
    final JSAPResult jsap = super.parse(args);

    if (jsap.getBoolean("verbose")) {
      Logger.getRootLogger().setLevel(Level.ALL);
    }
    return jsap;
  }

}
