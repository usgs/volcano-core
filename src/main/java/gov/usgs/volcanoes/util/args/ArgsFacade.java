package gov.usgs.volcanoes.util.args;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.Switch;

import gov.usgs.volcanoes.util.args.decorator.ConfigFileArg;
import gov.usgs.volcanoes.util.args.parser.DateStringParser;

/**
 * A class demonstrating use of the args package
 * 
 * @author Tom Parker
 * 
 *         I waive copyright and related rights in the this work worldwide
 *         through the CC0 1.0 Universal public domain dedication.
 *         https://creativecommons.org/publicdomain/zero/1.0/legalcode
 * 
 *         TODO: refactor to decorator
 */
public class ArgsFacade {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArgsFacade.class);

	// Everybody needs PROGRAM_NAME, EXPLANATION, and PARAMETERS
	public static final String PROGRAM_NAME = "java -jar gov.usgs.volcanoes.args.ArgsFacade";
	public static final String EXPLANATION = "I am demonstrate how to use the args package\n";
	private static final Parameter[] PARAMETERS = new Parameter[] {
			new Switch("verbose", 'v', "verbose", "Verbose logging.") };

	// ConfigFileArg will want a default
	public static final String DEFAULT_CONFIG_FILENAME = "facadeConfig.config";

	public static final String INPUT_TIME_FORMAT = "yyyyMMddHHmm";

	private static final DateStringParser DATE_PARSER = new DateStringParser(INPUT_TIME_FORMAT);

	public static void main(String[] args) {

		// base arguments
		Arguments arguments = new Args(PROGRAM_NAME, EXPLANATION, PARAMETERS);

		// add any decorators that are needed
		try {

			// config file decorator
			arguments = new ConfigFileArg(DEFAULT_CONFIG_FILENAME, arguments);
		} catch (JSAPException e1) {
			LOGGER.error("Couldn't parse command line. ({})", e1.getLocalizedMessage());
			System.exit(1);
		}

		JSAPResult jsapResult = null;
		try {
			jsapResult = arguments.parse(args);
		} catch (ParseException e) {
			LOGGER.error("Cannot parse command line. ({})", e.getLocalizedMessage());
			System.exit(1);
		}

		boolean verbose = jsapResult.getBoolean("verbose");
		LOGGER.debug("Setting: verbose={}", verbose);

		String configFileName = jsapResult.getString("config-filename");
		LOGGER.debug("Setting: config-filename={}", configFileName);
	}
}
