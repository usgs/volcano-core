package gov.usgs.volcanoes.core.args.parser;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

import gov.usgs.volcanoes.core.args.parser.DateStringParser;

/**
 * 
 * @author Tom Parker
 *
 */
public class DateStringParserTest {

  private static final String INPUT_FORMAT = "yyyyMMddHHmm";
  private static final String INPUT_TIME = "201508010000";

  /**
   * 
   * @throws ParseException when things go wrong
   * @throws java.text.ParseException when things go wrong
   */
  @Test
  public void when_givenString_then_returnsDate() throws ParseException, java.text.ParseException {
    SimpleDateFormat format = new SimpleDateFormat(INPUT_FORMAT);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    StringParser parser = new DateStringParser(INPUT_FORMAT);

    Date parsed = (Date) parser.parse(INPUT_TIME);
    Date generated = format.parse(INPUT_TIME);
    assertEquals(parsed, generated);
  }

  /**
   * 
   * @throws ParseException when things go wrong
   */
  @Test(expected = ParseException.class)
  public void when_givenUnparsableString_then_throwHelpfulException() throws ParseException {
    StringParser parser = new DateStringParser(INPUT_FORMAT);

    Date parsed = (Date) parser.parse("xxxxx");
  }

}
