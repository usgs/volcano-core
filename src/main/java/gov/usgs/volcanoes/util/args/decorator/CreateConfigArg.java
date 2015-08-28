package gov.usgs.volcanoes.util.args.decorator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.Switch;

import gov.usgs.volcanoes.util.args.ArgsDecorator;
import gov.usgs.volcanoes.util.args.Arguments;

/**
 * 
 * Create an example config file and exit.
 * 
 * @author Tom Parker
 * 
 *         I waive copyright and related rights in the this work worldwide
 *         through the CC0 1.0 Universal public domain dedication.
 *         https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */
public class CreateConfigArg extends ArgsDecorator {

	/** String representing an example configfile resource */
	private final String exampleConfigFile;

	/**
	 * Construct a CreateConfigArg adding its Parameter to the list.
	 * 
	 * @param exampleConfigFile
	 *            String resource of example configfile
	 * @param nextArg
	 *            The next Argument in the list
	 * @throws JSAPException
	 *             If parameter is already registered or cannot be added.
	 */
	public CreateConfigArg(final String exampleConfigFile, final Arguments nextArg) throws JSAPException {
		super(nextArg);
		this.exampleConfigFile = exampleConfigFile;
		nextArg.registerParameter(new Switch("create-config", 'c', "create-config",
				"Create an example config file in the curent working directory."));
		
		if (getById("config-filename") == null) 
			throw new JSAPException("ConfigFileArg must be applied before CreateConfigArg.");
	}

	/**
	 * Pass the args up the chain. If called upon, create the example config and
	 * exit.
	 * 
	 * @return the JSAPResult
	 */
	@Override
	public JSAPResult parse(final String[] args) throws ParseException {
		final JSAPResult jsap = nextArg.parse(args);
		final String configFileName = jsap.getString("config-filename");
		if (jsap.getBoolean("create-config")) {
			createConfig(exampleConfigFile, configFileName);
			System.exit(1);
		}
		return nextArg.parse(args);
	}

	/**
	 * Create the file.
	 * 
	 * @param exampleConfig
	 *            example config file as a restource string
	 * @param configFileName
	 *            path and name of created configFile
	 * @throws ParseException
	 *             if exampleConfig is not provided
	 */
	private void createConfig(String exampleConfig, String configFileName) throws ParseException {
		if (exampleConfig == null)
			throw new ParseException(
					"Example config filename not specified. Add a setProperty(\"exampleConfig\", \"fileName\") call to the code.");

		LOGGER.info("Creating example config " + exampleConfig);
		InputStream is = null;
		OutputStream os = null;

		try {
			is = ClassLoader.getSystemClassLoader().getResourceAsStream(exampleConfig);
			os = new FileOutputStream(configFileName);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} catch (IOException e) {
			LOGGER.error("Error creating config. " + e.getMessage());
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e2) {
				LOGGER.error("Error creating config. " + e2.getMessage());
			}
			
			try {
				if (os != null)
					os.close();
			} catch (IOException e3) {
				LOGGER.error("Error creating config. " + e3.getMessage());
			}
		}
	}
}