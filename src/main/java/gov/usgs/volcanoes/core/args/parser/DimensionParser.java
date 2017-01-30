/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args.parser;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

import java.awt.Dimension;

/**
 * Parse a dimension from a command line argument.
 *
 * @author Tom Parker
 */
public class DimensionParser extends StringParser {

  @Override
  public Dimension parse(String arg) throws ParseException {
    final int seperatorIdx = arg.indexOf('x');

    if (!(seperatorIdx > 1)) {
      throw new ParseException(String.format("Cannot parse dimension: %s. "
          + "Expected something in the form of <height>x<width> (e.g., 640x480)", arg));
    }

    final int height = Integer.parseInt(arg.substring(0, seperatorIdx));
    final int width = Integer.parseInt(arg.substring(seperatorIdx + 1, arg.length()));

    return new Dimension(width, height);
  }
}
