/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */
package gov.usgs.volcanoes.util.args.decorator;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import gov.usgs.volcanoes.util.args.ArgsDecorator;
import gov.usgs.volcanoes.util.args.Arguments;

/**
 * Create an example config file and exit.
 * 
 * @author Tom Parker
 */
public class CreateConfigArg extends ArgsDecorator {

  /** String representing an example configfile resource */
  private final String exampleConfigFile;

  /**
   * Construct a CreateConfigArg adding its Parameter to the list.
   * 
   * @param exampleConfigFile String resource of example configfile
   * @param nextArg The next Argument in the list
   * @throws JSAPException if parameter is already registered or cannot be added.
   */
  public CreateConfigArg(final String exampleConfigFile, Arguments nextArg) throws JSAPException {
    super(nextArg);
    this.exampleConfigFile = exampleConfigFile;

    nextArg.registerParameter(new Switch("create-config", 'c', "create-config",
        "Create an example config file in the curent working directory."));

    if (nextArg.getById("config-filename") == null)
      throw new JSAPException("CreateConfigArg relies on ConfigFileArg. Please wrap it first.");
  }

  /**
   * Pass the args up the chain. If called upon, create the example config and exit.
   * 
   * @return the JSAPResult
   */
  @Override
  public JSAPResult parse(final String[] args) throws Exception {
    final JSAPResult jsap = super.parse(args);
    final String configFileName = jsap.getString("config-filename");
    if (jsap.getBoolean("create-config")) {
      InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(exampleConfigFile);
      Path defaultPath = new File(configFileName).toPath();
      Files.copy(is, defaultPath);
    }
    return jsap;
  }
}
