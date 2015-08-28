package gov.usgs.volcanoes.util.args.decorator;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;

import gov.usgs.volcanoes.util.args.Args;
import gov.usgs.volcanoes.util.args.Arguments;

public class DateRangeArgTest {

	private static final String FORMAT = "yyyyMMddHHmm";
	private static final String START_TIME = "201508010000";
	private static final String END_TIME = "201508020000";

	Arguments arg;

	@Before
	public void setUp() throws JSAPException {
		arg = new DateRangeArg(FORMAT, new Args(null, null, new Parameter[0]));
	}

	@Test(expected = ParseException.class)
	public void when_OnlyStartTime_then_Exception() throws ParseException {
		String[] commandLine1 = { "--startTime", START_TIME };
		arg.parse(commandLine1);
	}

	@Test(expected = ParseException.class)
	public void when_OnlyEndTime_then_Exception() throws ParseException {
		String[] commandLine2 = { "--endTime", END_TIME };
		arg.parse(commandLine2);
	}

	@Test(expected = ParseException.class)
	public void when_EndTimeNotAfterStartTime_then_Exception() throws ParseException {
		String[] commandLine2 = { "--startTime", END_TIME, "--endTime", START_TIME };
		arg.parse(commandLine2);
	}

	@Test
	public void when_InputGood_then_OutputGood() throws ParseException, java.text.ParseException {
		SimpleDateFormat format = new SimpleDateFormat(FORMAT);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		String[] commandLine2 = { "--startTime", START_TIME, "--endTime", END_TIME };
		JSAPResult jsapResult = arg.parse(commandLine2);
		assertEquals(jsapResult.getDate("startTime"), format.parse(START_TIME));
		assertEquals(jsapResult.getDate("endTime"), format.parse(END_TIME));
	}
}
