package gov.usgs.volcanoes.core.args.decorator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;

import gov.usgs.volcanoes.core.args.Args;
import gov.usgs.volcanoes.core.args.ArgumentException;
import gov.usgs.volcanoes.core.args.Arguments;
import gov.usgs.volcanoes.core.data.Scnl;
import gov.usgs.volcanoes.core.util.UtilException;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

public class ScnlArgTest {

  private static final String EXAMPLE_SCN = "CH1$EHZ$AV";
  private static final String EXAMPLE_SCNL = "CH1$EHZ$AV$01";

  private Arguments arg;

  /**
   * 
   * @throws JSAPException when things go wrong
   */
  @Before
  public void setUp() throws JSAPException, ArgumentException {
    arg = new ScnlArg(false, new Args(null, null, new Parameter[0]));
  }


  /**
  *
  * @throws ArgumentException when things go wrong
   * @throws ParseException 
   * @throws UtilException 
  */
  @Test
  public void when_givenScnl_then_returnScnl()
      throws JSAPException, ArgumentException, ParseException, UtilException {

    String[] commandLine = {"-c", EXAMPLE_SCNL};
    JSAPResult jsapResult = arg.parse(commandLine);
    Scnl scnl = (Scnl) jsapResult.getObject("channel");

    assertEquals(scnl, Scnl.parse(EXAMPLE_SCNL));
  }

  /**
  *
  * @throws ArgumentException when things go wrong
   * @throws ParseException 
   * @throws UtilException 
  */
  @Test
  public void when_givenScn_then_returnScn()
      throws JSAPException, ArgumentException, ParseException, UtilException {

    String[] commandLine = {"-c", EXAMPLE_SCN};
    JSAPResult jsapResult = arg.parse(commandLine);
    Scnl scnl = (Scnl) jsapResult.getObject("channel");

    assertEquals(scnl, Scnl.parse(EXAMPLE_SCN));
  }

  /**
  *
  * @throws ArgumentException when things go wrong
   * @throws ParseException 
  */
  @Test
  public void when_givenBadScnl_then_returnError()
      throws JSAPException, ArgumentException, ParseException {

    String[] commandLine = {"-c", "tete-201705021211"};
    JSAPResult jsapResult = arg.parse(commandLine);
    Scnl scnl = (Scnl) jsapResult.getObject("timeSpan");

    assertTrue(arg.messagePrinted());
  }
}
