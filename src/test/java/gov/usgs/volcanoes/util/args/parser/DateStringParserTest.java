package gov.usgs.volcanoes.util.args.parser;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

public class DateStringParserTest {

	private static final String INPUT_FORMAT = "yyyyMMddHHmm";
	private static final String INPUT_TIME = "201508010000";

	@Test
	public void when_givenString_then_returnsDate() throws ParseException, java.text.ParseException {
		SimpleDateFormat format = new SimpleDateFormat(INPUT_FORMAT);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		StringParser parser = new DateStringParser(INPUT_FORMAT);
		
		Date parsed = (Date) parser.parse(INPUT_TIME);
		Date generated = format.parse(INPUT_TIME);
		assertEquals(parsed, generated);
	}
}
