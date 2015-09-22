/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args;

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
   * @throws ArgumentException if things go wrong
   */
  public Args(String programName, String explanation, Parameter[] parameters)
      throws ArgumentException {
    try {
      jsap = new SimpleJSAP(programName, explanation, parameters);
    } catch (JSAPException e) {
      throw new ArgumentException(e);
    }
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

  public void registerParameter(Parameter parameter) throws ArgumentException {
    try {
      jsap.registerParameter(parameter);
    } catch (JSAPException e) {
      throw new ArgumentException(e);
    }
  }

  public boolean messagePrinted() {
    return jsap.messagePrinted();
  }
}
