/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args.decorator;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;

import gov.usgs.volcanoes.core.args.ArgsDecorator;
import gov.usgs.volcanoes.core.args.ArgumentException;
import gov.usgs.volcanoes.core.args.Arguments;
import gov.usgs.volcanoes.core.args.parser.ScnlParser;
import gov.usgs.volcanoes.core.data.Scnl;


/**
 * Gather a SCNL from the command line.
 *
 *
 * @author Tom Parker
 */
public class ScnlArg extends ArgsDecorator {

  /**
   * Register arguments that define a SCNL.
   *
   * @param isRequired If true, this option must be provided
   * @param nextArg The Argument object I'm wrapping
   * @throws ArgumentException if parameters cannot be registered
   */
  public ScnlArg(boolean isRequired, Arguments nextArg) throws ArgumentException {
    super(nextArg);

    String delimiter = Scnl.DELIMITER;
    nextArg.registerParameter(
        new FlaggedOption("channel", new ScnlParser(), JSAP.NO_DEFAULT, isRequired, 'c', "channel",
            String.format("Channel as 'S%sC%sN%sL'\n", delimiter, delimiter, delimiter)));
  }
}
