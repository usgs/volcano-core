package gov.usgs.volcanoes.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class StringUtilsTest {

  @Test
  public void getIgnoreCaseStringComparator() {

    int i = StringUtils.getCaseInsensitiveStringComparator().compare("TEST", "test");
    assertTrue(i == 0);

    i = StringUtils.getCaseInsensitiveStringComparator().compare("T", "a");
    assertTrue(i > 0);

    i = StringUtils.getCaseInsensitiveStringComparator().compare("b", "P");
    assertTrue(i < 0);
  }

  @Test
  public void stringToBoolean() {
    assertTrue(StringUtils.stringToBoolean("true"));
    assertTrue(StringUtils.stringToBoolean("t"));
    assertFalse(StringUtils.stringToBoolean("nottrue"));
    assertFalse(StringUtils.stringToBoolean(null));
  }

  @Test
  public void stringToBooleanDefault() {
    assertTrue(StringUtils.stringToBoolean("true", false));
    assertFalse(StringUtils.stringToBoolean(null, false));
  }

  @Test
  public void stringToDouble() {
    assertEquals(StringUtils.stringToDouble("not a num", 1.5), 1.5);
    assertEquals(StringUtils.stringToDouble("2.5", 1), 2.5);
  }

  @Test
  public void stringToInt() {
    assertEquals(StringUtils.stringToInt("not a num", 1), 1);
    assertEquals(StringUtils.stringToInt("2", 1), 2);
  }

  @Test
  public void stringToString() {
    assertTrue("test".equals(StringUtils.stringToString("test", "test2")));
    assertTrue("test2".equals(StringUtils.stringToString(null, "test2")));
  }

  @Test
  public void when_givenNumBytes_then_returnString() {
    long val = 3 * 1024;
    assertTrue("3072 B".equals(StringUtils.numBytesToString(val)));
    val *= 1024;
    assertTrue("3072.000 KB".equals(StringUtils.numBytesToString(val)));
    val *= 1024;
    assertTrue("3072.000 MB".equals(StringUtils.numBytesToString(val)));
    val *= 1024;
    assertTrue("3072.000 GB".equals(StringUtils.numBytesToString(val)));
    val *= 1024;
    assertTrue("3072.000 TB".equals(StringUtils.numBytesToString(val)));
    val *= 1024;
    assertTrue("3072.000 PB".equals(StringUtils.numBytesToString(val)));
  }

  @Test
  public void mapToString() {
    Map<String, String> input = new HashMap<String, String>();
    input.put("key1", "val1");
    input.put("key2", "val2");
    assertTrue("key1=val1; key2=val2; ".equals(StringUtils.mapToString(input)));
  }

  @Test
  public void stringToMap() {
    String input = "key1=val1; key2=val2; ";
    Map<String, String> expectedResult = new HashMap<String, String>();
    expectedResult.put("key1", "val1");
    expectedResult.put("key2", "val2");
    assertEquals(expectedResult, StringUtils.stringToMap(input));
  }
}
