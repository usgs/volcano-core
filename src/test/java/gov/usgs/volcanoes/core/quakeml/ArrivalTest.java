package gov.usgs.volcanoes.core.quakeml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import gov.usgs.volcanoes.core.quakeml.Pick.Onset;
import gov.usgs.volcanoes.core.quakeml.Pick.Polarity;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ArrivalTest {

  private Arrival arrival;
  private HashMap<String, Pick> picks = new HashMap<String, Pick>();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {
    Pick pick = new Pick("pickId", System.currentTimeMillis(), "TEST");
    pick.setOnset(Onset.EMERGENT);
    pick.setPolarity(Polarity.POSITIVE);
    picks.put(pick.publicId, pick);
    this.arrival = new Arrival("arrivalId", pick, "P");
    arrival.setAzimuth(100.0);
    arrival.setDistance(150.0);
    arrival.setTakeoffAngle(45.0);
    arrival.setTimeResidual(0.2);
    arrival.setTimeWeight(1.5);
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public final void testToElement() {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;
    try {
      docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();
      Element element = arrival.toElement(doc);

      Arrival arrival2 = new Arrival(element, picks);
      assertEquals(100.0, arrival2.getAzimuth(), 0.01);
      assertEquals(150.0, arrival2.getDistance(), 0.01);
      assertEquals(45.0, arrival2.getTakeoffAngle(), 0.01);
      assertEquals(0.2, arrival2.getTimeResidual(), 0.01);
      assertEquals(1.5, arrival2.getTimeWeight(), 0.01);
    } catch (Exception e) {
      fail("Exception: " + e.getMessage()); // TODO
    }
  }


  @Test
  public final void testGetTag() {
    assertEquals("1.50eP+", arrival.getTag());
  }

}
