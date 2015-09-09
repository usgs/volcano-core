/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.util.args;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;

/**
 * An argument processor.
 *
 * @author Tom Parker
 */
public class Args implements Arguments {
  protected SimpleJSAP jsap;
  protected JSAPResult jsapResult;

  /**
   * Construct a Args instance.
   *
   * @param programName Name of the application use is running
   * @param explanation Brief explanation of application use
   * @param parameters Prepopulated list of JSAP Parameters
   * @throws JSAPException if SimpleJSAP throws it
   */
  public Args(String programName, String explanation, Parameter[] parameters) throws JSAPException {
    jsap = new SimpleJSAP(programName, explanation, parameters);
  }

  public Parameter getById(String id) {
    return jsap.getByID(id);
  }

  /**
   * Parse the command line.
   * 
   * @param args The command line args
   * @return the parsed command line
   */
  public JSAPResult parse(String[] args) {
    jsapResult = jsap.parse(args);

    return jsapResult;
  }

  public void registerParameter(Parameter parameter) throws JSAPException {
    jsap.registerParameter(parameter);
  }
}
