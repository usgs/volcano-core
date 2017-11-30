package gov.usgs.volcanoes.core.args.decorator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;

import gov.usgs.volcanoes.core.args.Args;
import gov.usgs.volcanoes.core.args.ArgumentException;
import gov.usgs.volcanoes.core.args.Arguments;
import gov.usgs.volcanoes.core.time.Time;
import gov.usgs.volcanoes.core.time.TimeSpan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;


@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "STCAL_INVOKE_ON_STATIC_DATE_FORMAT_INSTANCE",
    justification = "Single-threded testing.")
public class TimeSpanArgTest {

  private static final String DATE_FORMAT_STRING = "yyyyMMddHHmm";
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

  static {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  private Arguments arg;

  /**
   * 
   * @throws JSAPException when things go wrong
   */
  @Before
  public void setUp() throws JSAPException, ArgumentException {
    arg = new TimeSpanArg(DATE_FORMAT_STRING, true, new Args(null, null, new Parameter[0]));
  }


  /**
  *
  * @throws ArgumentException when things go wrong
   * @throws ParseException 
  */
  @Test
  public void when_givenTimeSpan_then_returnTimeSpan()
      throws JSAPException, ArgumentException, ParseException {

    String start = "201705010000";
    String end = "201705021211";
    String[] commandLine = {"-t", start + "-" + end};
    JSAPResult jsapResult = arg.parse(commandLine);
    TimeSpan timeSpan = (TimeSpan) jsapResult.getObject("timeSpan");

    assertNotNull(timeSpan);

    assertEquals(DATE_FORMAT.parse(start).getTime(), timeSpan.startTime);
    assertEquals(DATE_FORMAT.parse(end).getTime(), timeSpan.endTime);
  }

  /**
  *
  * @throws ArgumentException when things go wrong
   * @throws ParseException 
  */
  @Test
  public void when_givenNoTimeSpan_then_returnError()
      throws JSAPException, ArgumentException, ParseException {

    String[] commandLine = {""};
    JSAPResult jsapResult = arg.parse(commandLine);
    TimeSpan timeSpan = (TimeSpan) jsapResult.getObject("timeSpan");

    assertTrue(arg.messagePrinted());
  }

  /**
  *
  * @throws ArgumentException when things go wrong
   * @throws ParseException 
  */
  @Test
  public void when_givenBadStart_then_returnError()
      throws JSAPException, ArgumentException, ParseException {

    String[] commandLine = {"-t", "tete-201705021211"};
    JSAPResult jsapResult = arg.parse(commandLine);
    TimeSpan timeSpan = (TimeSpan) jsapResult.getObject("timeSpan");

    assertTrue(arg.messagePrinted());
  }


  /**
  *
  * @throws ArgumentException when things go wrong
   * @throws ParseException 
  */
  @Test
  public void when_givenBadEnd_then_returnError()
      throws JSAPException, ArgumentException, ParseException {

    String[] commandLine = {"-t", "201705021211-tete"};
    JSAPResult jsapResult = arg.parse(commandLine);
    TimeSpan timeSpan = (TimeSpan) jsapResult.getObject("timeSpan");

    assertTrue(arg.messagePrinted());
  }

  /**
  *
  * @throws ArgumentException when things go wrong
   * @throws ParseException 
  */
  @Test
  public void when_givenNow_then_returnCurrentTime()
      throws JSAPException, ArgumentException, ParseException {

    String[] commandLine = {"-t", "201705021211-now"};
    JSAPResult jsapResult = arg.parse(commandLine);
    TimeSpan timeSpan = (TimeSpan) jsapResult.getObject("timeSpan");

    assertNotNull(timeSpan);
    long offset = System.currentTimeMillis() - timeSpan.endTime;
    assertTrue(offset < 1000);
  }

  /**
  *
  * @throws ArgumentException when things go wrong
   * @throws ParseException 
  */
  @Test
  public void when_givenRelativeTime_then_returnCorrectSpan()
      throws JSAPException, ArgumentException, ParseException {

    String timeStr = "-1d";
    String[] commandLine = {"-t", "-1d"};
    JSAPResult jsapResult = arg.parse(commandLine);
    TimeSpan timeSpan = (TimeSpan) jsapResult.getObject("timeSpan");

    assertNotNull(timeSpan);
    long now = System.currentTimeMillis();
    long endOffset = now - timeSpan.endTime;
    assertTrue(endOffset < 1000);

    long startTime = timeSpan.endTime - (long) (Time.getRelativeTime(timeStr) * 1000);
    assertEquals(startTime, timeSpan.startTime);
  }

}
