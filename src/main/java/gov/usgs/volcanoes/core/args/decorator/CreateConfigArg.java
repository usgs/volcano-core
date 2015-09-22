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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Create an example config file.
 *
 * <p>CreateConfigArg implies ConfigFileArg
 *
 * @author Tom Parker
 */
public class CreateConfigArg extends ArgsDecorator {

  private final String exampleConfigFile;

  /**
   * Construct a CreateConfigArg adding its Parameter to the list.
   *
   * @param exampleConfigFile String resource of example configfile
   * @param nextArg The next Argument in the list
   * @throws JSAPException if parameter is already registered or cannot be added.
   */
  public CreateConfigArg(final String exampleConfigFile, Arguments nextArg) throws ArgumentException {
    super(nextArg);
    this.exampleConfigFile = exampleConfigFile;

    nextArg.registerParameter(new Switch("create-config", 'c', "create-config",
        "Create an example config file in the curent working directory."));

    if (nextArg.getById("config-filename") == null) {
      throw new ArgumentException("CreateConfigArg relies on ConfigFileArg. Please wrap it first.");
    }
  }

  @Override
  public JSAPResult parse(final String[] args) throws ArgumentException {
    final JSAPResult jsap = super.parse(args);
    final String configFileName = jsap.getString("config-filename");
    if (jsap.getBoolean("create-config")) {
      final InputStream is =
          ClassLoader.getSystemClassLoader().getResourceAsStream(exampleConfigFile);
      final Path defaultPath = new File(configFileName).toPath();
      try {
        Files.copy(is, defaultPath);
      } catch (IOException e) {
        throw new ArgumentException(e);
      }
    }
    return jsap;
  }
}
