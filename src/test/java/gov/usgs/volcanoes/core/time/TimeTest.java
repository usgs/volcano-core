package gov.usgs.volcanoes.core.time;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTest {


  private static final String INPUT_TIME_STRING = "20151120210003";
  private static final String STANDARD_TIME_STRING = "2015-11-20 21:00:03";
  // test date is 11/20/2015 21:00:03
  private static final long UNIX_TIME = 1448053203000L;

  @Test
  public void formatDate() {
    final String time = Time.format(Time.INPUT_TIME_FORMAT, new Date(UNIX_TIME));
    assertTrue(INPUT_TIME_STRING.equals(time));
  }

  @Test
  public void formatEpoch() {
    final String time = Time.format(Time.INPUT_TIME_FORMAT, UNIX_TIME);
    assertTrue(INPUT_TIME_STRING.equals(time));
  }

  @Test
  public void getFormat() {
    final String formatString = "yyyymmdd";
    final SimpleDateFormat format = Time.getFormat(formatString);
    assertTrue(formatString.equals(format.toPattern()));
  }

  @Test
  public void toDateStringDate() {
    final String dateString = Time.toDateString(new Date(UNIX_TIME));
    assertTrue(STANDARD_TIME_STRING.equals(dateString));

  }

  @Test
  public void toDateStringEpoch() {
    final String dateString = Time.toDateString(UNIX_TIME);
    assertTrue(STANDARD_TIME_STRING.equals(dateString));
  }


  @Test
  public void toShortString() {
    final String dateString = Time.toShortString(new Date(UNIX_TIME));
    assertTrue(INPUT_TIME_STRING.equals(dateString));

  }
}
