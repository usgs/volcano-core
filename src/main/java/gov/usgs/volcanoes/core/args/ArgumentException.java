/*
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args;

/**
 * Signals a fatal error creating an argument processor or parsing an argument..
 * 
 * @author Tom Parker
 *
 */
public class ArgumentException extends Exception {

  /** Maybe I'll be serialized one day? */
  private static final long serialVersionUID = -2778645971529842119L;

  /**
   * Constructor.
   * 
   * @param message Helpful information on cause of trouble
   */
  public ArgumentException(String message) {
    super(message);
  }

  /**
   * Exception-wrapping constructor.
   * 
   * @param e exception to wrap
   */
  public ArgumentException(Exception e) {
    super(e);
  }
}
