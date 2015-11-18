/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.args.decorator;


import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

import gov.usgs.volcanoes.core.args.ArgsDecorator;
import gov.usgs.volcanoes.core.args.ArgumentException;
import gov.usgs.volcanoes.core.args.Arguments;
import gov.usgs.volcanoes.core.args.parser.DateStringParser;

import java.util.Date;


/**
 * Gather a date range from the command line.
 *
 * <p>A startTime and endTime Parameter will be added to the parameter list. Retrieve them with
 * getDate.
 *
 * @author Tom Parker
 */
public class DateRangeArg extends ArgsDecorator {

  private String startText = "startTime";
  private String endText = "endTime";
  
  /**
   * Register arguments that define a date range.
   * 
   * @param dateFormat Format string suitable for feeding to SimpleDateFormat
   * @param nextArg The Argument object I'm wrapping
   * @throws ArgumentException if parameters cannot be registered
   */
  public DateRangeArg(String dateFormat, Arguments nextArg) throws ArgumentException {
    super(nextArg);

    final StringParser dateParser = new DateStringParser(dateFormat);
    nextArg.registerParameter(new FlaggedOption("startTime", dateParser, JSAP.NO_DEFAULT,
        JSAP.NOT_REQUIRED, 's', "startTime", startText + "\n"));
    nextArg.registerParameter(new FlaggedOption("endTime", dateParser, JSAP.NO_DEFAULT,
        JSAP.NOT_REQUIRED, 'e', "endTime", endText = "\n"));
  }


  @Override
  public JSAPResult parse(String[] args) throws ArgumentException {
    final JSAPResult jsap = super.parse(args);

    try {
      validateDates(jsap.getDate("startTime"), jsap.getDate("endTime"));
    } catch (ParseException e) {
      throw new ArgumentException(e);
    }
    return jsap;
  }

  /**
   * Set the text displayed when --help is given.
   * 
   * @param startText
   * @return this
   */
  public DateRangeArg setStartText(String startText) {
    this.startText= startText;
    return this;
  }
  
  
  /**
   * Returns the text displayed when --help is given.
   * 
   * @return the start text
   */
  public String getStartText() {
    return startText;
  }

  
  /**
   * Set the text displayed when --help is given.
   * 
   * @param endText
   * @return this
   */
  public DateRangeArg setEndText(String endText) {
    this.endText = endText;
    return this;
  }

  /**
   * Returns the text displayed when --help is given.
   * 
   * @return the end text
   */
  public String getEndText() {
    return endText;
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
