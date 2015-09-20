/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.configfile;

import java.text.ParseException;

/**
 * Seek an encoded object in a config file value.
 * 
 * @author Tom Parker
 *
 * @param <T> Type parser seeks
 */
public interface Parser<T> {
  
  /**
   * Parse an object out of a config file value.
   * 
   * @param value string to be parsed
   * @return object found in value
   * @throws ParseException when value cannot be parsed
   */
  public T parse(String value) throws ParseException;
}
