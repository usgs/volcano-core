package gov.usgs.volcanoes.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ZipTest {

  @Test
  public void compress_empty_buffer_returns_empty_buffer() {

    byte[] bytes = Zip.compress(new byte[0]);
    assertEquals(bytes.length, 0);
  }


  @Test
  public void decompress_empty_buffer_returns_empty_buffer() {

    byte[] bytes = Zip.decompress(new byte[0]);
    assertEquals(bytes.length, 0);
  }
}
