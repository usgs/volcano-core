/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;

/**
 * Decorator interface for command line arguments.
 * 
 * @author Tom Parker
 */
public interface Arguments {

  /**
   * Find a parameter given its id.
   * 
   * @param id The id to search for
   * @return The value of the id
   */
  public Parameter getById(String id);

  /**
   * Parse a command line.
   *
   * @param args The arguments provieded at program launch
   * @return The parsed arguments
   * @throws Exception if anything goes wrong. Yes, pretty much anything.
   */
  public JSAPResult parse(String[] args) throws Exception;

  /**
   * Register a Parameter with JSAP.
   * 
   * @param parameter The Parameter to register
   * @throws JSAPException if I cannot register the parameter
   */
  public void registerParameter(Parameter parameter) throws JSAPException;
}
