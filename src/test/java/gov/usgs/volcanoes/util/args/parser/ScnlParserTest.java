package gov.usgs.volcanoes.util.args.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.martiansoftware.jsap.ParseException;

import gov.usgs.volcanoes.util.data.Scnl;
import gov.usgs.volcanoes.util.data.ScnlTest;

/**
 * 
 * @author Tom Parker
 *
 */
public class ScnlParserTest {

  // TODO: come up with reasonable defaults
  private static final String STATION = "STA";
  private static final String COMPONENT = "CMP";
  private static final String NETWORK = "NT";
  private static final String LOCATION = "01";

  private static final String EXAMPLE_SCNL =
      STATION + "$" + COMPONENT + "$" + NETWORK + "$" + LOCATION;
  private static final String EXAMPLE_SCN_AS_STRING =
      STATION + "$" + COMPONENT + "$" + NETWORK + "$" + Scnl.DEFAULT_LOCATION;
  private static final String EXAMPLE_SCN = STATION + "$" + COMPONENT + "$" + NETWORK;

  ScnlParser parser = new ScnlParser();

  /**
   * 
   * @throws ParseException when things go wrong
   */
  @Test
  public void when_givenScnl_return_scnl() throws ParseException {
    Scnl scnl = (Scnl) parser.parse(EXAMPLE_SCNL);

    assertEquals(STATION, scnl.station);
    assertEquals(COMPONENT, scnl.channel);
    assertEquals(NETWORK, scnl.network);
    assertEquals(LOCATION, scnl.location);
  }

  /**
   * 
   * @throws ParseException when things go wrong
   */
  @Test
  public void when_givenScn_return_scn() throws ParseException {
    Scnl scnl = (Scnl) parser.parse(EXAMPLE_SCN);

    assertEquals(STATION, scnl.station);
    assertEquals(COMPONENT, scnl.channel);
    assertEquals(NETWORK, scnl.network);
    assertEquals(Scnl.DEFAULT_LOCATION, scnl.location);
  }

  /**
   * 
   * @throws ParseException when things go right
   */
  @Test(expected = ParseException.class)
  public void when_givenBadScnl_then_throwHelpfulException() throws ParseException {
    parser.parse("not a SCNL");
  }
}
