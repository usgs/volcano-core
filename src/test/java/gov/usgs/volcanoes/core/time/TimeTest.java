package gov.usgs.volcanoes.core.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTest {


  private static final String INPUT_TIME_STRING = "20151120210003";
  private static final String STANDARD_TIME_STRING = "2015-11-20 21:00:03";
  // test date is 11/20/2015 21:00:03
  private static final long UNIX_TIME = 1448053203000L;
  private static final PrintStream originalOut = System.out;
  private static final PrintStream originalErr = System.err;

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  @Before
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @After
  public void cleanUpStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

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

  @Test
  public void when_givenBadRelativeTime_then_returnNan() {
    assertTrue(Double.isNaN(Time.getRelativeTime("notTime")));
    assertTrue(Double.isNaN(Time.getRelativeTime("-1")));
    assertTrue(Double.isNaN(Time.getRelativeTime("-1z")));
  }


  @Test
  public void when_givenRelativeTime_then_returnTime() {
    assertEquals(1.0, Time.getRelativeTime("-1s"), 0);
    assertEquals(60.0, Time.getRelativeTime("-1i"), 0);
    assertEquals(60.0 * 60, Time.getRelativeTime("-1h"), 0);
    assertEquals(60.0 * 60 * 24, Time.getRelativeTime("-1d"), 0);
    assertEquals(60.0 * 60 * 24 * 7, Time.getRelativeTime("-1w"), 0);
    assertEquals(60.0 * 60 * 24 * 30, Time.getRelativeTime("-1m"), 0);
    assertEquals(60.0 * 60 * 24 * 365, Time.getRelativeTime("-1y"), 0);
  }

  @Test
  public void when_mainCalled_then_convertTime() throws Exception {
    Time.main(new String[0]);
    assertTrue(outContent.toString().contains("-d2j [yyyymmddhhmmss] date to j2k"));
    outContent.reset();

    String[] args = new String[2];

    args[0] = "-j2d";
    args[1] = "5.01325203E8";
    Time.main(args);
    assertEquals("2015-11-20 21:00:03.000\n", outContent.toString());
    outContent.reset();

    args[0] = "-j2e";
    args[1] = "5.01325203E8";
    Time.main(args);
    assertEquals("1.448053203E9\n", outContent.toString());
    outContent.reset();

    args[0] = "-d2j";
    args[1] = INPUT_TIME_STRING;
    Time.main(args);
    assertEquals("5.01325203E8\n", outContent.toString());
    outContent.reset();

    args[0] = "-e2d";
    args[1] = "1448053203";
    Time.main(args);
    assertEquals("2015-11-20 21:00:03.000\n", outContent.toString());
    outContent.reset();
  }

  @Test
  public void when_givenLength_then_return_string() {
    assertEquals("5s", Time.secondsToString(5));
    assertEquals("5m 5s", Time.secondsToString(305));
    assertEquals("5h 5m 5s", Time.secondsToString(18305));
    assertEquals("5d 5h 5m 5s", Time.secondsToString(450305));
  }

  @Test
  public void when_askedSecondsInYear_then_provideAnswer() {
    assertEquals(31557600.0, Time.YEAR_IN_S);
  }
}
