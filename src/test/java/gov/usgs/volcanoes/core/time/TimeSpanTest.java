package gov.usgs.volcanoes.core.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.text.ParseException;

public class TimeSpanTest {

  @Test
  public void goodSpanString() {
    TimeSpan ts = new TimeSpan(0, 30 * TimeSpan.SECOND);
    assertTrue("30s".equals(ts.span()));

    ts = new TimeSpan(30 * TimeSpan.SECOND, 0);
    assertTrue("-30s".equals(ts.span()));

    long time =
        TimeSpan.DAY + (2 * TimeSpan.HOUR) + (30 * TimeSpan.MINUTE) + (TimeSpan.SECOND) + 531;
    ts = new TimeSpan(0, time);
    assertTrue("1d 2h 30m 1.531s".equals(ts.span()));
  }

  @Test
  public void zeroSpan() {
    TimeSpan ts = new TimeSpan(10, 10);
    assertTrue("0s".equals(ts.span()));
  }

  @Test
  public void parse() throws ParseException {
    long startTime = Time.getFormat(Time.INPUT_TIME_FORMAT).parse("20171101000000").getTime();
    long endTime = Time.getFormat(Time.INPUT_TIME_FORMAT).parse("20171102000000").getTime();
    TimeSpan ts1 = new TimeSpan(startTime, endTime);
    TimeSpan ts = TimeSpan.parse("-24h,20171102000000");
    System.out.println(ts1 + " == " + ts);
    assertEquals(ts.compareTo(ts1), 0);
  }

}
