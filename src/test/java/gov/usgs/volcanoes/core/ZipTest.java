package gov.usgs.volcanoes.core;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;


public class ZipTest {

  private static final byte[] UNCOMPRESSED_BYTES =
      DatatypeConverter.parseHexBinary("E04FD020EA3A6910A2D808002B30309D");

  private static final byte[] COMPRESSED_BYTES =
      DatatypeConverter.parseHexBinary("78017BE07F41E19555A6C0A21B1C0CDA060673013F320667");

  private static final byte[] UNCOMPRESSED_NULL = new byte[0];

  private static final byte[] COMPRESSED_NULL =
      DatatypeConverter.parseHexBinary("7801030000000001");

  @Test
  public void compress_empty_buffer_returns_empty_buffer() {

    byte[] bytes = Zip.compress(UNCOMPRESSED_NULL);
    System.err.println("Empty: " + DatatypeConverter.printHexBinary(bytes));
    assertTrue(Arrays.equals(COMPRESSED_NULL, bytes));
  }

  @Test
  public void compress_buffer_returns_compressed_buffer() {
    final byte[] bytes = Zip.compress(UNCOMPRESSED_BYTES);
    System.err.println("Compress expected: " + DatatypeConverter.printHexBinary(COMPRESSED_BYTES));
    System.err.println("Compress received: " + DatatypeConverter.printHexBinary(bytes));

    assertTrue(Arrays.equals(COMPRESSED_BYTES, bytes));
  }

  @Test
  public void decompress_buffer_returns_decompressed_buffer() {
    final byte[] bytes = Zip.decompress(COMPRESSED_BYTES);
    System.err
        .println("Uncompress expected: " + DatatypeConverter.printHexBinary(UNCOMPRESSED_BYTES));
    System.err.println("Uncompress received: " + DatatypeConverter.printHexBinary(bytes));
    assertTrue(Arrays.equals(UNCOMPRESSED_BYTES, bytes));
  }

  @Test
  public void decompress_empty_buffer_returns_empty_buffer() {

    byte[] bytes = Zip.decompress(COMPRESSED_NULL);
    assertTrue(Arrays.equals(UNCOMPRESSED_NULL, bytes));
  }
}
