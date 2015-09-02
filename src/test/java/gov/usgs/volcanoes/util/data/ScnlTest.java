package gov.usgs.volcanoes.util.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.martiansoftware.util.StringUtils;

public class ScnlTest {

	// TODO: come up with reasonable defaults
	public static final String STATION="STA";
	public static final String COMPONENT="CMP";
	public static final String NETWORK="NT";
	public static final String LOCATION="01";

	public static final String EXAMPLE_SCNL = STATION + "$" + COMPONENT + "$" + NETWORK + "$" + LOCATION;
	public static final String EXAMPLE_SCN_AS_STRING = STATION + "$" + COMPONENT + "$" + NETWORK + "$" + Scnl.DEFAULT_LOCATION;
	public static final String EXAMPLE_SCN = STATION + "$" + COMPONENT + "$" + NETWORK;
	
	@Test
	public void when_calledWithFourArgs_return_scnl() {
	    
		Scnl scnl = new Scnl(STATION, COMPONENT, NETWORK, LOCATION);
		assertEquals(STATION, scnl.station);
		assertEquals(COMPONENT, scnl.channel);
		assertEquals(NETWORK, scnl.network);
		assertEquals(LOCATION, scnl.location);
	}

	@Test
	public void when_calledWithThreeArgs_return_scn() {
		Scnl scnl = new Scnl(STATION, COMPONENT, NETWORK);
		assertEquals(STATION, scnl.station);
		assertEquals(COMPONENT, scnl.channel);
		assertEquals(NETWORK, scnl.network);
		assertEquals(Scnl.DEFAULT_LOCATION, scnl.location);
	}

	@Test
	public void when_scnToStringCalled_return_string() {
		Scnl scnl = new Scnl(STATION, COMPONENT, NETWORK);
		
		assertEquals(EXAMPLE_SCN_AS_STRING, scnl.toString());
	}

	@Test
	public void when_scnlToStringCalled_return_string() {
		Scnl scnl = new Scnl(STATION, COMPONENT, NETWORK, LOCATION);
		
		assertEquals(EXAMPLE_SCNL, scnl.toString());
	}

}
