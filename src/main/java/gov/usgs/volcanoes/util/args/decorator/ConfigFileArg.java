package gov.usgs.volcanoes.util.args.decorator;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.UnflaggedOption;

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
public class ConfigFileArg extends ArgsDecorator {

	public ConfigFileArg(String defaultFileName, Arguments nextArg) throws JSAPException {
		super(nextArg);
        nextArg.registerParameter(new UnflaggedOption("config-filename", JSAP.STRING_PARSER, defaultFileName,
                JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "The config file name."));
	}
}
