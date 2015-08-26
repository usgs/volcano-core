package gov.usgs.volcanoes.util.args.decorator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.Switch;
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
public class VerboseArg extends ArgsDecorator {

	public VerboseArg(Arguments nextArg) throws JSAPException {
		super(nextArg);
        nextArg.registerParameter(new Switch("verbose", 'v', "verbose", "Verbose logging."));
	}
	
	public JSAPResult parse(String[] args) throws ParseException {
		JSAPResult jsap = super.parse(args);

		if (jsap.getBoolean("verbose"))
			Logger.getRootLogger().setLevel(Level.ALL);
		return nextArg.parse(args);
	}

}
