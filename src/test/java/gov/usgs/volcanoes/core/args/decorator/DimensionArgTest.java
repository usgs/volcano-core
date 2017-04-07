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

import java.awt.Dimension;

/**
 * 
 * @author tparker
 *
 */
public class DimensionArgTest {
  private Arguments arg;

  /**
   * 
   * @throws JSAPException when things go wrong
   */
  @Before
  public void setUp() throws JSAPException, ArgumentException {
    arg = new DimensionArg(new Args(null, null, new Parameter[0]));
  }


  /**
  *
  * @throws ArgumentException when things go wrong
  */
  @Test
  public void when_givenDimension_then_returnDimension() throws JSAPException, ArgumentException {

    String[] commandLine = {"-d", "640x480"};
    JSAPResult jsapResult = arg.parse(commandLine);
    Dimension dim = (Dimension) jsapResult.getObject("dimension");

    assertEquals(480, dim.width);
    assertEquals(640, dim.height);
    assertFalse(arg.messagePrinted());
  }

  /**
  *
  * @throws ArgumentException when things go wrong
  * @throws ArgumentException
  */
  @Test
  public void when_givenBadDimension_then_returnException()
      throws JSAPException, ArgumentException {

    String[] commandLine = {"-d", "640"};
    JSAPResult jsapResult = arg.parse(commandLine);
    assertTrue(arg.messagePrinted());
  }

  /**
  *
  * @throws ArgumentException when things go wrong
  * @throws ArgumentException
  */
  @Test
  public void when_givenNoDimension_then_returnDefault() throws JSAPException, ArgumentException {
    Arguments arg = new DimensionArg("640x480", new Args(null, null, new Parameter[0]));

    String[] commandLine = {};
    JSAPResult jsapResult = arg.parse(commandLine);
    Dimension dim = (Dimension) jsapResult.getObject("dimension");

    assertNotNull(dim);
    assertEquals(480, dim.width);
    assertEquals(640, dim.height);
    assertFalse(arg.messagePrinted());
  }
}
