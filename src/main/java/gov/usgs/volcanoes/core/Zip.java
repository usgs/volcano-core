/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core;

import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Utility classes for working with the JDK <code>Inflator</code>/
 * <code>Deflator</code> implementation.
 *
 * @author Dan Cervelli
 *
 */
public class Zip {
  /**
   * Compresses an array of bytes using the JDK <code>Inflator</code>/
   * <code>Deflator</code> implementation set for maximum speed.
   * Compression ratios comparable to gzip are attained.
   *
   * @param bytes the array of bytes
   * @return the compressed array of bytes
   */
  public static byte[] compress(byte[] bytes) {
    return compress(bytes, Deflater.BEST_SPEED);
  }

  /**
   * Compresses an array of bytes using the JDK <code>Inflator</code>/
   * <code>Deflator</code> implementation. Compression ratios comparable to
   * gzip are attained.
   *
   * @param bytes the array of bytes
   * @param level the compression level (1[least]-9[most])
   * @return the compressed array of bytes
   */
  public static byte[] compress(byte[] bytes, int level) {
    return compress(bytes, level, 0, bytes.length);
  }

  /**
   * Compresses an array of bytes using the JDK <code>Inflator</code>/
   * <code>Deflator</code> implementation. Compression ratios comparable to
   * gzip are attained.
   *
   * @param bytes the array of bytes
   * @param level the compression level (1[least]-9[most])
   * @param ofs number of first byte in array to process
   * @param len length of processed array zone
   * @return the compressed array of bytes
   */
  public static byte[] compress(byte[] bytes, int level, int ofs, int len) {
    final Deflater deflater = new Deflater(level);
    deflater.setInput(bytes, ofs, len);
    deflater.finish();
    final ArrayList<byte[]> list = new ArrayList<byte[]>(2);
    boolean done = false;
    int compSize = 0;
    // must allow for the compressed size to be larger than the original size
    while (!done) {
      final byte[] compBuf = new byte[bytes.length];
      compSize = deflater.deflate(compBuf);

      // needsInput check added to allow for empty input buffer
      if (deflater.finished() || deflater.needsInput()) {
        done = true;
      }
      list.add(compBuf);
    }
    final int total = (list.size() - 1) * bytes.length + compSize;
    final byte[] finalBuf = new byte[total];
    int totalLen = 0;
    for (int i = 0; i < list.size() - 1; i++) {
      System.arraycopy(list.get(i), 0, finalBuf, totalLen, bytes.length);
      totalLen += bytes.length;
    }
    System.arraycopy(list.get(list.size() - 1), 0, finalBuf, totalLen, compSize);
    return finalBuf;
  }

  /**
   * Decompresses an array of bytes compressed via the <code>Util.compress()</code>
   * methods. Uses a bufferSize of 64K.
   *
   * @param bytes the compressed bytes
   * @return the decompressed array of bytes
   */
  public static byte[] decompress(byte[] bytes) {
    return decompress(bytes, 65536);
  }

  /**
   * Decompresses an array of bytes compressed via the <code>Util.compress()</code>
   * methods. Use a bufferSize slightly larger than the expected size of the
   * decompressed data for maximum efficiency.
   *
   * @param bytes the compressed bytes
   * @param bufferSize the decompression buffer size
   * @return the decompressed array of bytes
   */
  public static byte[] decompress(byte[] bytes, int bufferSize) {
    try {
      final Inflater inflater = new Inflater();
      inflater.setInput(bytes);
      boolean done = false;

      final ArrayList<byte[]> list = new ArrayList<byte[]>(10);
      int numBytes = 0;
      while (!done) {
        final byte[] buffer = new byte[bufferSize];
        numBytes = inflater.inflate(buffer);

        // needsInput check added to allow for empty input buffer
        if (inflater.finished() || inflater.needsInput()) {
          done = true;
        }
        list.add(buffer);
      }
      inflater.end();
      final int total = (list.size() - 1) * bufferSize + numBytes;
      final byte[] finalBuf = new byte[total];
      int totalLen = 0;
      for (int i = 0; i < list.size() - 1; i++) {
        System.arraycopy(list.get(i), 0, finalBuf, totalLen, bufferSize);
        totalLen += bufferSize;
      }
      System.arraycopy(list.get(list.size() - 1), 0, finalBuf, totalLen, numBytes);
      return finalBuf;
    } catch (final Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

}
