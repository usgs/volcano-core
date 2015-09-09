/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.util.args.decorator;

import java.util.Date;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

import gov.usgs.volcanoes.util.args.ArgsDecorator;
import gov.usgs.volcanoes.util.args.Arguments;
import gov.usgs.volcanoes.util.args.parser.DateStringParser;

/**
 * Gather a date range from the command line.
 *
 * <p>
 * A startTime and endTime Parameter will be added to the parameter list. Retrieve them with
 * getDate.
 *
 * @author Tom Parker
 */
public class DateRangeArg extends ArgsDecorator {

  /**
   * @param dateFormat Format string suitable for feeding to SimpleDateFormat
   * @param nextArg The Argument object I'm wrapping
   * @throws JSAPException if parameters cannot be registered
   */
  public DateRangeArg(String dateFormat, Arguments nextArg) throws JSAPException {
    super(nextArg);

    final StringParser dateParser = new DateStringParser(dateFormat);
    nextArg.registerParameter(new FlaggedOption("startTime", dateParser, JSAP.NO_DEFAULT,
        JSAP.NOT_REQUIRED, 's', "startTime", "Start of backfill period\n"));
    nextArg.registerParameter(new FlaggedOption("endTime", dateParser, JSAP.NO_DEFAULT,
        JSAP.NOT_REQUIRED, 'e', "endTime", "End of backfill period\n"));
  }


  @Override
  public JSAPResult parse(String[] args) throws Exception {
    final JSAPResult jsap = super.parse(args);

    validateDates(jsap.getDate("startTime"), jsap.getDate("endTime"));
    return jsap;
  }

  private void validateDates(Date startTime, Date endTime) throws ParseException {
    // If one require the other
    if (startTime != null && endTime == null) {
      throw new ParseException("endTime must be specified if startTime is specified");
    }
    if (endTime != null && startTime == null) {
      throw new ParseException("startTime must be specified if endTime is specified");
    }

    // endTime must be greater than startTime
    if (endTime != null && !endTime.after(startTime)) {
      throw new ParseException(
          "endTime must be greater than startTime." + endTime + ":" + startTime);
    }
  }
}
