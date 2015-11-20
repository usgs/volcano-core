package gov.usgs.volcanoes.core.args;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;

import gov.usgs.volcanoes.core.args.Args;
import gov.usgs.volcanoes.core.args.ArgumentException;
import gov.usgs.volcanoes.core.args.Arguments;

/**
 * 
 * @author Tom Parker
 *
 */
public class ArgsTest {

  private static final String TEST_PARAM = "test";
  private static final String TEST_VAL = "testVal";

  private Parameter parameter;

  /**
   * 
   */
  @Before
  public void setUp() {
    parameter = new FlaggedOption(TEST_PARAM, JSAP.STRING_PARSER, JSAP.NO_DEFAULT,
        JSAP.NOT_REQUIRED, 't', TEST_PARAM, "Test param.");
  }

  /**
   * 
   * @throws JSAPException when things go wrong
   * @throws ArgumentException 
   */
  @Test
  public void when_intializedWithParam_them_paramSet() throws JSAPException, ArgumentException {
    Parameter[] params = new Parameter[] {parameter};
    Arguments args = new Args(null, null, params);

    Parameter p = args.getById(TEST_PARAM);

    assertSame(p, parameter);
  }

  /**
   * 
   * @throws JSAPException when things go wrong
   * @throws ArgumentException 
   */
  @Test
  public void when_paramRegistered_them_paramSet() throws JSAPException, ArgumentException {
    Arguments args = new Args(null, null, new Parameter[0]);
    args.registerParameter(parameter);

    Parameter p = args.getById(TEST_PARAM);

    assertSame(p, parameter);
  }

  /**
   * 
   * @throws Exception when things go wrong
   */
  @Test
  public void when_givenArgs_then_setValues() throws Exception {
    Parameter[] params = new Parameter[] {parameter};
    Arguments args = new Args(null, null, params);

    JSAPResult jsapResult = args.parse(new String[] {"--" + TEST_PARAM, TEST_VAL});
    // assertEquals(jsapResult.getString(TEST_PARAM), TEST_VAL);
  }
}
