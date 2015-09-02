package gov.usgs.volcanoes.util.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
    
    @Test
    public void when_equal_return_zero() {
        Scnl scnl = new Scnl(STATION, COMPONENT, NETWORK, Scnl.DEFAULT_LOCATION);
        Scnl scn = new Scnl(STATION, COMPONENT, NETWORK);
        
        assertTrue(scnl.compareTo(scn) == 0);
    }

    @Test
    public void when_notEqual_return_false() {
        Scnl scnl1 = new Scnl(STATION, COMPONENT, NETWORK, LOCATION);
        Scnl scnl2 = new Scnl(STATION, COMPONENT, NETWORK, "XX");
        assertFalse(scnl1.equals(scnl2));

        scnl2 = new Scnl(STATION, COMPONENT, "XX", LOCATION);
        assertFalse(scnl1.equals(scnl2));

        scnl2 = new Scnl(STATION, "XXX", NETWORK, LOCATION);
        assertFalse(scnl1.equals(scnl2));

        scnl2 = new Scnl("XXXX", COMPONENT, NETWORK, LOCATION);
        assertFalse(scnl1.equals(scnl2));
    }

}
