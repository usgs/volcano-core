/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args;

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
   * @param args The arguments provided at program launch
   * @return The parsed arguments
   * @throws ArgumentException if anything goes wrong. Yes, pretty much anything.
   */
  public JSAPResult parse(String[] args) throws ArgumentException;

  /**
   * Register a Parameter with JSAP.
   * 
   * @param parameter The Parameter to register
   * @throws ArgumentException if I cannot register the parameter
   */
  public void registerParameter(Parameter parameter) throws ArgumentException;

  /**
   * Report whether JSAP displayed a message to the user. Typically means either an error or that
   * the user requested help.
   * 
   * @return True if JSAP printed a message while parsing arguments
   */
  public boolean messagePrinted();
}
