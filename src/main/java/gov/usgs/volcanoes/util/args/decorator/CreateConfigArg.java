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
 * @author Tom Parker
 * 
 *         I waive copyright and related rights in the this work worldwide
 *         through the CC0 1.0 Universal public domain dedication.
 *         https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */
public class CreateConfigArg extends ArgsDecorator {

	private final String exampleConfigFile;
	
	public CreateConfigArg(String exampleConfigFile, Arguments nextArg) throws JSAPException {
		super(nextArg);
		this.exampleConfigFile = exampleConfigFile;
		nextArg.registerParameter(new Switch("create-config", 'c', "create-config",
				"Create an example config file in the curent working directory."));
	}

	public JSAPResult parse(String[] args) throws ParseException {
		JSAPResult jsap = nextArg.parse(args);
		String configFileName = jsap.getString("config-filename");
		if (jsap.getBoolean("create-config")) {
			createConfig(exampleConfigFile, configFileName);
			System.exit(1);
		}
		return nextArg.parse(args);
	}

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
				if (os != null)
					os.close();
			} catch (IOException e2) {
				LOGGER.error("Error creating config. " + e2.getMessage());
			}
		}
	}
}