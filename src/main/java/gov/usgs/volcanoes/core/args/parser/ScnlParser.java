/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args.parser;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

import gov.usgs.volcanoes.core.data.Scnl;
import gov.usgs.volcanoes.core.util.UtilException;

/**
 * Parse a SCNL from a command line argument.
 * 
 * @author Tom Parker
 */
public class ScnlParser extends StringParser {

  public static final char DELIMITER = '$';

  /**
   * Constructor.
   * 
   */
  public ScnlParser() {}

  @Override
  public Scnl parse(String arg) throws ParseException {

    try {
      return Scnl.parse(arg);
    } catch (UtilException ex) {
      throw new ParseException(ex);
    }
  }
}
