package gov.usgs.volcanoes.util.args;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;

public class ArgsTest {

	private static final String TEST_PARAM = "test";
	private static final String TEST_VAL = "testVal";

	private Parameter parameter;

	@Before
	public void setUp() {
		parameter = new FlaggedOption(TEST_PARAM, JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 't',
				TEST_PARAM, "Test param.");
	}

	@Test
	public void when_intializedWithParam_them_paramSet() {
		Parameter[] params = new Parameter[] { parameter };
		Arguments args = new Args(null, null, params);

		Parameter p = args.getById(TEST_PARAM);

		assertSame(args.getById(TEST_PARAM), parameter);
	}

	@Test
	public void when_paramRegistered_them_paramSet() throws JSAPException {
		Arguments args = new Args(null, null, new Parameter[0]);
		args.registerParameter(parameter);

		Parameter p = args.getById(TEST_PARAM);

		assertSame(args.getById(TEST_PARAM), parameter);
	}

	@Test
	public void when_givenArgs_then_setValues() throws ParseException {
		Parameter[] params = new Parameter[] { parameter };
		Arguments args = new Args(null, null, params);

		JSAPResult jsapResult = args.parse(new String[] { "--" + TEST_PARAM, TEST_VAL });
		// assertEquals(jsapResult.getString(TEST_PARAM), TEST_VAL);
	}
}
