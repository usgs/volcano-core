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

}
