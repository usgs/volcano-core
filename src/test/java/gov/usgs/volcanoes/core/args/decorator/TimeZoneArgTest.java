package gov.usgs.volcanoes.core.args.decorator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;

import gov.usgs.volcanoes.core.args.Args;
import gov.usgs.volcanoes.core.args.ArgumentException;
import gov.usgs.volcanoes.core.args.Arguments;

import org.junit.Before;
import org.junit.Test;

import java.util.TimeZone;

public class TimeZoneArgTest {

  private static final String ZONE = "US/Aleutian";
  private Arguments arg;

  /**
   * 
   * @throws JSAPException when things go wrong
   */
  @Before
  public void setUp() throws JSAPException, ArgumentException {
    arg = new TimeZoneArg(new Args(null, null, new Parameter[0]));
  }


  /**
  *
  * @throws ArgumentException when things go wrong
  */
  @Test
  public void when_givenTimeZone_then_returnTimeZone() throws JSAPException, ArgumentException {

    String[] commandLine = {"-z", ZONE};
    JSAPResult jsapResult = arg.parse(commandLine);
    TimeZone timeZone = (TimeZone) jsapResult.getObject("timeZone");

    assertEquals(TimeZone.getTimeZone(ZONE), timeZone);
  }

  /**
  *
  * @throws ArgumentException when things go wrong
  * @throws ArgumentException
  */
  @Test
  public void when_givenBadDimension_then_returnException()
      throws JSAPException, ArgumentException {

    String[] commandLine = {"-z", "whenever"};
    JSAPResult jsapResult = arg.parse(commandLine);
    assertTrue(arg.messagePrinted());
  }

  /**
  *
  * @throws ArgumentException when things go wrong
  * @throws ArgumentException
  */
  @Test
  public void when_givenNoTimeZone_then_returnDefault() throws JSAPException, ArgumentException {
    Arguments arg = new TimeZoneArg(new Args(null, null, new Parameter[0]));

    String[] commandLine = {};
    JSAPResult jsapResult = arg.parse(commandLine);
    TimeZone timeZone = (TimeZone) jsapResult.getObject("timeZone");

    assertNotNull(timeZone);
    assertEquals(TimeZone.getTimeZone(TimeZoneArg.DEFAULT_TIME_ZONE), timeZone);
    assertFalse(arg.messagePrinted());
  }
}
