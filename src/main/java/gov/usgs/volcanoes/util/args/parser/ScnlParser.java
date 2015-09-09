/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */
package gov.usgs.volcanoes.util.args.parser;

import java.util.regex.Pattern;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

import gov.usgs.volcanoes.util.data.Scnl;

/**
 * 
 * @author Tom Parker
 */
public class ScnlParser extends StringParser {

  @Override
  public Object parse(String arg) throws ParseException {
    String[] comps = arg.split(Pattern.quote(Scnl.DELIMITER));

    Scnl result = null;
    if (comps.length == 4)
      result = new Scnl(comps[0], comps[1], comps[2], comps[3]);
    else if (comps.length == 3)
      result = new Scnl(comps[0], comps[1], comps[2]);
    else
      throw new ParseException("Can't parse SCNL: " + arg + " : " + comps.length);

    return result;
  }
}
