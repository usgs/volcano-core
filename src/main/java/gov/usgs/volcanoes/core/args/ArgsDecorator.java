/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add parameters to the command line.
 *
 * @author Tom Parker
 */
public abstract class ArgsDecorator implements Arguments {
  protected static final Logger LOGGER = LoggerFactory.getLogger(ArgsDecorator.class);
  protected final Arguments nextArg;

  /**
   * Keep track of object I'm wrapping.
   *
   * @param nextArg The argument I'm wrapping
   */
  protected ArgsDecorator(Arguments nextArg) {
    this.nextArg = nextArg;
  }

  /**
   * Locate a Parameter by its id.
   *
   * @param id id of Parameter to return
   * @return the requested Parameter
   */
  public Parameter getById(String id) {
    return nextArg.getById(id);
  }

  public JSAPResult parse(String[] args) throws ArgumentException {
    return nextArg.parse(args);
  }

  public void registerParameter(Parameter parameter) throws ArgumentException {
    nextArg.registerParameter(parameter);
  }

  public boolean messagePrinted() {
    return nextArg.messagePrinted();
  }

}
