package gov.usgs.volcanoes.util;

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
 */
public abstract class Args {

    protected SimpleJSAP jsap;
    protected JSAPResult config;

    protected Args(String programName, String explanation, Parameter[] parameters) {
        try {
            jsap = new SimpleJSAP(programName, explanation, parameters);
        } catch (JSAPException e) {
            System.err.println("Try using the --help flag.");
            System.exit(1);
        }
    }

    public JSAPResult parse(String[] args) {
        config = jsap.parse(args);
        if (jsap.messagePrinted()) {
            if (!config.getBoolean("help"))
                System.err.println("Try using the --help flag.");

            System.exit(1);
        }

        return config;
    }
}
