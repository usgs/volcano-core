package gov.usgs.volcanoes.core.util;

public class ByteUtil {
  /**
   * Converts integer to byte value.
   * 
   * @param myInt integer to convert
   * @return converted byte
   */
  public static byte intToByte(int myInt) {
    return (byte) (myInt & 0xff);
  }

  /**
   * Converts byte to integer value.
   * 
   * @param myByte byte to convert
   * @return converted int
   */
  public static int byteToInt(byte myByte) {
    return (int) myByte & 0xff;
  }


  /**
   * Swaps byte order, integer.
   *
   * @param myInt int to have bytes swapped
   * @return int w/ bytes of i swapped
   */
  public static int swap(int myInt) {
    return (((myInt & 0xff000000) >> 24) & 0x000000ff) | ((myInt & 0x00ff0000) >> 8)
        | ((myInt & 0x0000ff00) << 8) | ((myInt & 0x000000ff) << 24);
  }

  /**
   * Swaps byte order, short.
   * 
   * @param myShort short to have bytes swapped
   * @return short w/ bytes of s swapped
   */
  public static short swap(short myShort) {
    return (short) (((myShort & 0xff00) >> 8) | ((myShort & 0x00ff) << 8));
  }

  /**
   * Swaps byte order, double.
   * 
   * @param myDouble double to have bytes swapped
   * @return double w/ bytes of d swapped
   */
  public static double swap(double myDouble) {
    long lng = Double.doubleToRawLongBits(myDouble);
    long sl = ((((lng & 0xff00000000000000L) >> 56) & 0x00000000000000ff)
        | ((lng & 0x00ff000000000000L) >> 40) | ((lng & 0x0000ff0000000000L) >> 24)
        | ((lng & 0x000000ff00000000L) >> 8) | ((lng & 0x00000000ff000000L) << 8)
        | ((lng & 0x0000000000ff0000L) << 24) | ((lng & 0x000000000000ff00L) << 40)
        | ((lng & 0x00000000000000ffL) << 56));
    return Double.longBitsToDouble(sl);
  }

  /**
   * Converts array of bytes into string on per-symbol basis, till first 0 value.
   * 
   * @param myByte byte array to convert
   * @return converted string
   */
  public static String bytesToString(byte[] myByte) {
    return bytesToString(myByte, 0, myByte.length);
  }

  /**
   * Converts array of bytes into string on per-symbol basis, till first 0 value.
   * 
   * @param myByte byte array to convert
   * @param offset number of first byte to convert
   * @param length length of converting part
   * @return converted string
   */
  public static String bytesToString(byte[] myByte, int offset, int length) {
    int trunc = -1;
    for (int i = offset; i < offset + length; i++) {
      if (myByte[i] == 0) {
        trunc = i;
        break;
      }
    }
    if (trunc != -1) {
      // return new String(b, o, trunc - o);
      return quickBytesToString(myByte, offset, trunc - offset);
    } else {
      // return new String(b, o, l);
      return quickBytesToString(myByte, offset, length);
    }
  }


  /**
   * Converts array of bytes into string on per-symbol basis.
   * 
   * @param myByte byte array to convert 
   * @param offset offset from first array member to start conversion
   * @param length resulting string length
   * @return converted string
   */
  public static String quickBytesToString(byte[] myByte, int offset, int length) {
    char[] chars = new char[length];
    for (int i = 0; i < chars.length; i++) {
      chars[i] = (char) myByte[i + offset];
    }
    return new String(chars);
  }



}
