package gov.usgs.volcanoes.core.util;

import static org.junit.Assert.*;

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
}
