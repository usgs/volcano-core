package gov.usgs.volcanoes.core.args.parser;

import static org.junit.Assert.assertEquals;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

import gov.usgs.volcanoes.core.time.Time;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
  public void when_givenString_then_returnDate() throws ParseException, java.text.ParseException {
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

  @Test(expected = ParseException.class)
  public void when_givenRelativeSpan_then_returnDate() throws ParseException {
    SimpleDateFormat format = new SimpleDateFormat(INPUT_FORMAT);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    StringParser parser = new DateStringParser(INPUT_FORMAT);

    Date parsed = (Date) parser.parse("-1h");
    Date generated =
        new Date(System.currentTimeMillis() - (long) ((Time.getRelativeTime("-1h") * 1000)));
    assertEquals(parsed, generated);
  }

}
