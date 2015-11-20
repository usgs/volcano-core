package gov.usgs.volcanoes.core.args.decorator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;

import gov.usgs.volcanoes.core.args.Args;
import gov.usgs.volcanoes.core.args.ArgumentException;
import gov.usgs.volcanoes.core.args.decorator.DateRangeArg;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * 
 * @author Tom Parker
 *
 */
public class DateRangeArgTest {

  private static final String FORMAT = "yyyyMMddHHmm";
  private static final String START_TIME = "201508010000";
  private static final String END_TIME = "201508020000";

  DateRangeArg arg;

  /**
   * 
   * @throws ArgumentException 
   */
  @Before
  public void setUp() throws ArgumentException {
    arg = new DateRangeArg(FORMAT, new Args(null, null, new Parameter[0]));
  }

  /**
   * 
   * @throws ArgumentException 
   */
  @Test
  public void when_givenNothing_then_returnNothing() throws ArgumentException {
    new DateRangeArg(FORMAT, new Args(null, null, new Parameter[0]));
  }

  /**
   * 
   * @throws ArgumentException when things go wrong
   */
  @Test(expected = ArgumentException.class)
  public void when_OnlyStartTime_then_Exception() throws ArgumentException {
    String[] commandLine1 = {"--startTime", START_TIME};
    arg.parse(commandLine1);
  }

  /**
   * 
   * @throws ArgumentException when things go wrong
   */
  @Test(expected = ArgumentException.class)
  public void when_OnlyEndTime_then_Exception() throws ArgumentException {
    String[] commandLine2 = {"--endTime", END_TIME};
    arg.parse(commandLine2);
  }

  /**
   * 
   * @throws ArgumentException when things go wrong
   */
  @Test(expected = ArgumentException.class)
  public void when_EndTimeNotAfterStartTime_then_Exception() throws ArgumentException {
    String[] commandLine2 = {"--startTime", END_TIME, "--endTime", START_TIME};
    arg.parse(commandLine2);
  }

  /**
   * 
   * @throws ArgumentException when things go wrong
   */
  @Test
  public void when_nothingIn_then_nothingOut() throws ArgumentException {
    arg.parse(new String[0]);
  }

  /**
   * 
   * @throws ArgumentException when things go wrong
   * @throws  ParseException
   */
  @Test
  public void when_InputGood_then_OutputGood() throws ParseException, ArgumentException {
    SimpleDateFormat format = new SimpleDateFormat(FORMAT);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    String[] commandLine2 = {"--startTime", START_TIME, "--endTime", END_TIME};
    JSAPResult jsapResult = arg.parse(commandLine2);
    assertEquals(jsapResult.getDate("startTime"), format.parse(START_TIME));
    assertEquals(jsapResult.getDate("endTime"), format.parse(END_TIME));
  }
}
