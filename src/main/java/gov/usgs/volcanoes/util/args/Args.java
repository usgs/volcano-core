package gov.usgs.volcanoes.util.args;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

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
public abstract class Args {
    private static final Logger LOGGER = LoggerFactory.getLogger(Args.class);

    protected SimpleJSAP jsap;
    protected JSAPResult config;
    public String configFileName;

    private boolean createConfigParam = false;
    private String exampleFileName = null;

    protected Args(String programName, String explanation, Parameter[] parameters) {
        try {
            jsap = new SimpleJSAP(programName, explanation, parameters);
        } catch (JSAPException e) {
            System.err.println("Try using the --help flag.");
            System.exit(1);
        }
    }

    protected void addCreateConfig(String exampleFileName, String defaultFileName) {
        createConfigParam = true;
        this.exampleFileName = exampleFileName;

        try {
            jsap.registerParameter(new UnflaggedOption("config-filename", JSAP.STRING_PARSER, defaultFileName,
                    JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "The config file name."));
            jsap.registerParameter(new Switch("create-config", 'c', "create-config",
                    "Create an example config file in the curent working directory."));
        } catch (JSAPException e) {
            LOGGER.error("Cannot register create-config parameter");
            System.exit(1);
        }
    }

    private void createConfig() {
        LOGGER.warn("Creating example config " + exampleFileName);
        InputStream is = null;
        OutputStream os = null;

        try {
            is = ClassLoader.getSystemClassLoader().getResourceAsStream(exampleFileName);
            os = new FileOutputStream(config.getString("config-filename"));

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

    public JSAPResult parse(String[] args) {
        config = jsap.parse(args);
        if (jsap.messagePrinted()) {
            if (!config.getBoolean("help"))
                System.err.println("Try using the --help flag.");
            System.exit(1);

        } else if (createConfigParam) {
            configFileName = config.getString("config-filename");
            if (config.getBoolean("create-config")) {
                createConfig();
                System.exit(1);
            }
        }

        return config;
    }
}