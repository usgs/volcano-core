package gov.usgs.volcanoes.util.args;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;

/**
 * Argument processor
 * 
 * @author Tom Parker
 * 
 *         I waive copyright and related rights in the this work worldwide
 *         through the CC0 1.0 Universal public domain dedication.
 *         https://creativecommons.org/publicdomain/zero/1.0/legalcode
 *         
 *         TODO: refactor to decorator
 */
public class Args implements Arguments {
    private static final Logger LOGGER = LoggerFactory.getLogger(Args.class);

    protected SimpleJSAP jsap;
    protected JSAPResult jsapResult;

    private Map<String, String> properties;
    
    public Args(String programName, String explanation, Parameter[] parameters) {
    	properties = new HashMap<String, String>();
    	
        try {
            jsap = new SimpleJSAP(programName, explanation, parameters);
        } catch (JSAPException e) {
            LOGGER.error("Try using the --help flag.");
            System.exit(1);
        }
    }

    public JSAPResult parse(String[] args) {
        jsapResult = jsap.parse(args);
        if (jsap.messagePrinted()) {
            if (!jsapResult.getBoolean("help"))
                LOGGER.error("Try using the --help flag.");
            System.exit(1);
        }
        return jsapResult;
    }

	public void registerParameter(Parameter parameter) throws JSAPException {
		jsap.registerParameter(parameter);
	}

	public void setProperty(String property, String value) {
		properties.put(property, value);
	}

	public String getProperty(String property) {
		return properties.get(property);
	}
}