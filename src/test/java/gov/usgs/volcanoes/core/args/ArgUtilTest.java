package gov.usgs.volcanoes.core.args;

import static org.junit.Assert.assertEquals;

import com.martiansoftware.jsap.JSAP;

import org.junit.Test;

public class ArgUtilTest {

  @Test
  public void when_givenDefault_then_notRequired() {
    assertEquals(JSAP.NOT_REQUIRED, ArgUtil.isRequired("default"));
  }

  @Test
  public void when_givenNoOrNullDefault_then_Required() {
    assertEquals(JSAP.REQUIRED, ArgUtil.isRequired(null));
    assertEquals(JSAP.REQUIRED, ArgUtil.isRequired(""));
  }
}
