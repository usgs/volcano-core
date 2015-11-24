/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.util;

/**
 * A holder for exceptions thrown by util classes.
 * 
 * @author Dan Cervelli
 *
 */
public class UtilException extends Exception {
  private static final long serialVersionUID = 1L;

  /**
   * Class constructor.
   * 
   * @param msg The exceptions message
   */
  public UtilException(String msg) {
    super(msg);
  }

}
