package gov.usgs.volcanoes.util.args.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.martiansoftware.jsap.ParseException;

import gov.usgs.volcanoes.util.data.Scnl;
import gov.usgs.volcanoes.util.data.ScnlTest;

public class ScnlParserTest {

	ScnlParser parser = new ScnlParser();
	
	@Test
	public void when_givenScnl_return_scnl() throws ParseException {
		Scnl scnl = (Scnl) parser.parse(ScnlTest.EXAMPLE_SCNL);
	
		assertEquals(ScnlTest.STATION, scnl.station);
		assertEquals(ScnlTest.COMPONENT, scnl.channel);
		assertEquals(ScnlTest.NETWORK, scnl.network);
		assertEquals(ScnlTest.LOCATION, scnl.location);
	}

	@Test
	public void when_givenScn_return_scn() throws ParseException {
		Scnl scnl = (Scnl) parser.parse(ScnlTest.EXAMPLE_SCN);
	
		assertEquals(ScnlTest.STATION, scnl.station);
		assertEquals(ScnlTest.COMPONENT, scnl.channel);
		assertEquals(ScnlTest.NETWORK, scnl.network);
		assertEquals(Scnl.DEFAULT_LOCATION, scnl.location);
	}
}
