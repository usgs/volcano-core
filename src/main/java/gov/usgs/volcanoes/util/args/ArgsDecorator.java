package gov.usgs.volcanoes.util.args;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;

public abstract class ArgsDecorator implements Arguments {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ArgsDecorator.class);
	protected final Arguments nextArg;

	protected ArgsDecorator(Arguments nextArg) {
		this.nextArg = nextArg;
	}
	
	public JSAPResult parse(String[] args) throws ParseException {
		return nextArg.parse(args);
	}
	
	public void registerParameter(Parameter parameter) throws JSAPException {
		nextArg.registerParameter(parameter);
	}
}
