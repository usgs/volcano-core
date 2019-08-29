package gov.usgs.volcanoes.core.time;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

public class EwTest {
  // test date 11/3/2017 18:27:39
  public static final double TEST_DATE_EW = 1509733659D;
  public static final long TEST_DATE_TIME = 1509733659000L;
  public static final String TEST_DATE_STRING = "2017-11-03 18:27:39";

  @Test
  public void when_given_ew_return_date() {
    assertEquals(TEST_DATE_TIME, Ew.asDate(TEST_DATE_EW).getTime());
  }

  @Test
  public void when_given_date_return_ew() {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    cal.setTimeInMillis(TEST_DATE_TIME);

    assertEquals(TEST_DATE_EW, Ew.fromDate(cal.getTime()), 0);
  }

  @Test
  public void when_given_ew_return_string() {
    assertEquals(TEST_DATE_STRING, Ew.toDateString(TEST_DATE_EW));
  }

  @Test
  public void when_given_string_return_ew() throws ParseException {
    assertEquals(TEST_DATE_EW, Ew.parse(Time.STANDARD_TIME_FORMAT, TEST_DATE_STRING), 0);
  }
}
