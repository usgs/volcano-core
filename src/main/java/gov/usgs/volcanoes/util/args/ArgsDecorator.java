package gov.usgs.volcanoes.util.args;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;

/**
 * 
 * @author Tom Parker
 * 
 *         I waive copyright and related rights in the this work worldwide
 *         through the CC0 1.0 Universal public domain dedication.
 *         https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */
public abstract class ArgsDecorator implements Arguments {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ArgsDecorator.class);
	protected final Arguments nextArg;

	protected ArgsDecorator(Arguments nextArg) {
		this.nextArg = nextArg;
	}

	public JSAPResult parse(String[] args) throws Exception {
		return nextArg.parse(args);
	}

	public void registerParameter(Parameter parameter) throws JSAPException {
		nextArg.registerParameter(parameter);
	}

	public Parameter getById(String id) {
		return nextArg.getById(id);
	}

}
