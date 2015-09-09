/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */
package gov.usgs.volcanoes.util.configFile;

/**
 * 
 * @author tparker
 */
public interface Parser {
  /**
   * 
   * @param input   input string
   * @return        parsed object
   */
  public Object parse(String input);
}
