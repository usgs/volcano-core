package gov.usgs.volcanoes.core.contrib.hypo71;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Hypo71Test {



  @BeforeClass
  public static void setUpBeforeClass() throws Exception {


  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public final void testCalculateHypo71() {
    ControlCard controlCard =
        new ControlCard(0, 5.0, 50.0, 100.0, 1.78, 4, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0);
    Queue<CrustalModel> crustalModelList = new LinkedList<CrustalModel>();
    Queue<Station> stationList = new LinkedList<Station>();
    Queue<PhaseRecord> phaseRecordList = new LinkedList<PhaseRecord>();

    // Crustal Model
    crustalModelList.add(new CrustalModel(3.3, 0.0));
    crustalModelList.add(new CrustalModel(5.0, 1.0));
    crustalModelList.add(new CrustalModel(5.7, 4.0));
    crustalModelList.add(new CrustalModel(6.7, 15.0));
    crustalModelList.add(new CrustalModel(8.0, 25.0));

    // Station
    stationList.add(
        new Station(' ', "TDH", 45, 17.38, 'N', 121, 47.49, 'W', 0, 0, 0, 0, 1, 0, 0, 0, 0, 0));
    stationList.add(
        new Station(' ', "SHRK", 45, 27.86, 'N', 121, 31.73, 'W', 0, 0, 0, 0, 1, 0, 0, 0, 0, 0));
    stationList.add(
        new Station(' ', "PALM", 45, 21.51, 'N', 121, 42.33, 'W', 0, 0, 0, 0, 1, 0, 0, 0, 0, 0));
    stationList.add(
        new Station(' ', "VLL", 45, 27.79, 'N', 121, 40.82, 'W', 0, 0, 0, 0, 1, 0, 0, 0, 0, 0));
    stationList.add(
        new Station(' ', "HIYU", 45, 26.11, 'N', 121, 48.44, 'W', 0, 0, 0, 0, 1, 0, 0, 0, 0, 0));
    stationList.add(
        new Station(' ', "TIMB", 45, 20.14, 'N', 121, 42.62, 'W', 0, 0, 0, 0, 1, 0, 0, 0, 0, 0));

    // Phase Records
    phaseRecordList.add(new PhaseRecord("PALM", "EDP1", 0.0, 1, 12, 23.10, 0.0, "EUS1", 0.0, 0.0,
        0.0, 0.0, 0.0, "", 0.0, 16.0, "", 'D', "", "", ' ', "EDP1"));
    phaseRecordList.add(new PhaseRecord("TIMB", "EUP1", 0.0, 1, 12, 23.30, 0.0, "EUS1", 0.0, 0.0,
        0.0, 0.0, 0.0, "", 0.0, 12.0, "", 'D', "", "", ' ', "EDP1"));
    phaseRecordList.add(new PhaseRecord("VLL", "EUP0", 0.0, 1, 12, 24.40, 0.0, "EUS1", 0.0, 0.0,
        0.0, 0.0, 0.0, "", 0.0, 6.0, "", 'D', "", "", ' ', "EDP1"));
    phaseRecordList.add(new PhaseRecord("TDH", "I P1", 0.0, 1, 12, 24.40, 0.0, "EUS1", 0.0, 0.0,
        0.0, 0.0, 0.0, "", 0.0, 5.0, "", 'D', "", "", ' ', "EDP1"));
    phaseRecordList.add(new PhaseRecord("HIYU", "EDP1", 0.0, 1, 12, 24.50, 0.0, "EUS1", 0.0, 0.0,
        0.0, 0.0, 0.0, "", 0.0, 0.0, "", 'D', "", "", ' ', "EDP1"));
    phaseRecordList.add(new PhaseRecord("SHRK", "IUP2", 0.0, 1, 12, 25.30, 0.0, "EUS1", 0.0, 0.0,
        0.0, 0.0, 0.0, "", 0.0, 5.0, "", 'D', "", "", ' ', "EDP1"));
    phaseRecordList.add(new PhaseRecord(" ", " ", 0.0, 0, 0, 0, 0.0, " ", 0.0, 0.0, 0.0, 0.0, 0.0,
        "", 0.0, 0.0, "", ' ', "", "", ' ', " "));


    Hypo71 hypo71 = new Hypo71();
    try {
      hypo71.calculateHypo71("Mt Hood 8/9/17", null, stationList, crustalModelList, controlCard,
          phaseRecordList, null);
      List<Hypocenter> hypocenters = hypo71.getResults().getHypocenterOutput();
      Hypocenter hypocenter = hypocenters.get(0);
      assertEquals(45, hypocenter.getLAT1());
      assertEquals(21.73, hypocenter.getLAT2(), 0.01);
      assertEquals(121, hypocenter.getLON1());
      assertEquals(41.30, hypocenter.getLON2(), 0.01);
      assertEquals(6.69, hypocenter.getZ(), 0.01);
      assertEquals("0.94", hypocenter.getMAGOUT().trim());
      assertEquals(163, hypocenter.getIGAP());
    } catch (Exception e) {
      fail("Exception: " + e.getMessage());
    }
  }


}
