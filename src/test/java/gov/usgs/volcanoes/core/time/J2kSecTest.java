package gov.usgs.volcanoes.core.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

public class J2kSecTest {

  // test date is 11/20/2015 21:00:03
  private static final long UNIX_TIME_MS = 1448053203000L;
  private static final double UNIX_TIME = 1448053203;
  private static final double J2K_TIME = (UNIX_TIME_MS / 1000) + J2kSec.UNIXZERO;
  private static final String INPUT_TIME_STRING = "20151120210003";
  private static final String STANDARD_TIME_STRING = "2015-11-20 21:00:03";

  @Test
  public void asDate() {
    Date date = J2kSec.asDate(J2K_TIME);
    assertTrue(date.equals(new Date(UNIX_TIME_MS)));
  }

  @Test
  public void fromDate() {
    Double j2ksec = J2kSec.fromDate(new Date(UNIX_TIME_MS));
    assertEquals(j2ksec, J2K_TIME);
  }
  
  @Test
  public void asEpoch() {
    double epoch = J2kSec.asEpoch(J2K_TIME);
    assertEquals(epoch, UNIX_TIME);
  }
  
  @Test
  public void asEpochMs() {
    long epoch = J2kSec.asEpoch(J2K_TIME);
    assertEquals(epoch, UNIX_TIME_MS);
  }

  @Test
  public void fromEpoch() {
    Double j2ksec = J2kSec.fromEpoch(UNIX_TIME_MS);
    assertEquals(j2ksec, J2K_TIME);
  }

  @Test
  public void format() {
    String time = J2kSec.format(Time.INPUT_TIME_FORMAT, J2K_TIME);
    assertTrue(INPUT_TIME_STRING.equals(time));
  }

  @Test
  public void toDateString() {
    String time = J2kSec.toDateString(J2K_TIME);
    assertTrue(STANDARD_TIME_STRING.equals(time));
  }

  @Test
  public void parse() throws ParseException {
    double date = J2kSec.parse(Time.STANDARD_TIME_FORMAT, STANDARD_TIME_STRING);
    assertEquals(J2K_TIME, date);
  }
  
  @Test
  public void now() {
    long nowDate = new Date().getTime();
    long nowJ2k = J2kSec.asEpoch(J2kSec.now());
    assertTrue(Math.abs(nowDate - nowJ2k) < 3);
    
  }

}
