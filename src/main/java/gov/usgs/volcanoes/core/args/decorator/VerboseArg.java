/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args.decorator;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import gov.usgs.volcanoes.core.args.ArgsDecorator;
import gov.usgs.volcanoes.core.args.ArgumentException;
import gov.usgs.volcanoes.core.args.Arguments;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Gather a verbose flag from the command line.
 *
 * @author Tom Parker
 */
public class VerboseArg extends ArgsDecorator {

  /**
   *
   * @param nextArg The Argument object that I'm wrapping
   * @throws ArgumentException if Parameters cannot be registered.
   */
  public VerboseArg(Arguments nextArg) throws ArgumentException {
    super(nextArg);
    nextArg.registerParameter(new Switch("verbose", 'v', "verbose", "Verbose logging."));
  }

  @Override
  public JSAPResult parse(String[] args) throws ArgumentException {
    final JSAPResult jsap = super.parse(args);

    if (jsap.getBoolean("verbose")) {
      Logger.getRootLogger().setLevel(Level.ALL);
    }
    return jsap;
  }

}
