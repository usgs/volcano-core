package gov.usgs.plot.data.file;

import gov.usgs.plot.data.Wave;
import gov.usgs.util.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Holds data representing a Seisan file. This includes channel headers and
 * channel data.
 * 
 * <p>File format taken from appendix B of the Seisan manual.
 * 
 * <p>Based on code written by Chirag Patel with funding from VDAP
 * 
 * @author Tom Parker
 * 
 */
public class SeisanDataFile extends SeismicDataFile {

  private ByteOrder byteOrder;
  private int channelCount;
  private int machineIntLength;
  private static final String writeSeparator = " ";
  private static final String readSeparator = "$";

  protected SeisanDataFile(String fileName) {
    super(fileName, "Seisan^");
  }

  /**
   * @see gov.usgs.plot.data.file.SeismicDataFile#read()
   */
  public void read() throws IOException {
    FileInputStream fis = new FileInputStream(fileName);
    BufferedInputStream buf = new BufferedInputStream(fis);

    detectArchitecture(buf);
    readEventFileHeader(buf);
    for (int i = 0; i < channelCount; i++) {
      readChannel(buf);
    }
  }

  /*
   * length of first header line length is always 80. Infer byte order and int
   * length based on how that length is represented
   */
  private void detectArchitecture(BufferedInputStream bis) throws IOException {
    byte[] bytes = new byte[8];
    bis.mark(Integer.MAX_VALUE);
    bis.read(bytes);
    bis.reset();

    byteOrder = (bytes[0] == 0x50) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    boolean is64Bit = (bytes[7] == 0x50 || bytes[7] == 0x00) ? true : false;
    machineIntLength = is64Bit ? 8 : 4;
  }

  private void readEventFileHeader(BufferedInputStream bis) throws IOException {
    String headerLine = new String(readRecord(bis));
    channelCount = Integer.parseInt(headerLine.substring(30, 33).trim());
    //System.out.println("CHANNEL COUNT " + channelCount + " :" + headerLine.substring(30, 33) + ":");

    // skip the rest of the header lines. We don't need them.
    int numHeaderLines = 2 + ((channelCount + 2) / 3);
    for (int i = 1; i < Math.max(numHeaderLines, 12); i++) {
      readRecord(bis);
    }
  }

  private void readChannel(BufferedInputStream bis) throws IOException {
    byte[] channelHeader = readRecord(bis);

    String code = extractCode(channelHeader);

    double start = Util.ewToJ2K(extractStartTime(channelHeader) / 1000d);
    double samplingRate = Double.parseDouble(new String(channelHeader, 36, 7).trim());
    int sampleCount = Integer.parseInt(new String(channelHeader, 43, 7).trim());

    char c = (char) channelHeader[76];
    if (c == ' ') {
      c = 2;
    }
    int intLength = Integer.parseInt("" + c);

    int[] samples = new int[sampleCount];
    int sampleIndex = 0;
    while (sampleIndex < sampleCount) {
      byte[] data = readRecord(bis);

      for (int i = 0; i < data.length; i += intLength) {
        byte[] b = Arrays.copyOfRange(data, i, i + intLength);
        samples[sampleIndex++] = decodeInt(b);
      }
    }
    waves.put(code, new Wave(samples, start, samplingRate));
  }

  private long extractStartTime(byte[] header) {
    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    int year = Integer.parseInt(new String(header, 9, 3).trim()) + 1900;
    c.set(Calendar.YEAR, year);

    int month = Integer.parseInt(new String(header, 17, 2).trim());
    c.set(Calendar.MONTH, month - 1);

    int day = Integer.parseInt(new String(header, 20, 2).trim());
    c.set(Calendar.DAY_OF_MONTH, day);

    int hour = Integer.parseInt(new String(header, 23, 2).trim());
    c.set(Calendar.HOUR_OF_DAY, hour);

    int min = Integer.parseInt(new String(header, 26, 2).trim());
    c.set(Calendar.MINUTE, min);

    double sec = Float.parseFloat(new String(header, 29, 6).trim());
    c.set(Calendar.SECOND, (int) sec);
    double ms = (sec - Math.floor(sec)) * 1000;
    c.set(Calendar.MILLISECOND, (int) ms);

    return c.getTimeInMillis();
  }


  /*
   * Extract channel code from event file channel header. Attempt to
   * accommodate a bug in earthworm versions prior to 7.7.
   */
  private String extractCode(byte[] header) {
    // try to accommodate EW bug which misplaced the third character of the
    // component. Fixed in EW 7.7
    boolean ewBug = false;
    char c1 = (char) header[7];
    char c2 = (char) header[8];
    if ((c1 == 'E' || c1 == 'Z' || c1 == 'N') && !(c2 == 'E' || c2 == 'Z' || c2 == 'N')) {
      ewBug = true;
    }

    String cmp;
    String loc;
    if (ewBug) {
      cmp = new String(header, 5, 2) + new String(header, 7, 1);
      loc = new String(header, 8, 1) + new String(header, 12, 1);
    } else {
      cmp = new String(header, 5, 2) + new String(header, 8, 1);
      loc = new String(header, 7, 1) + new String(header, 12, 1);
    }

    channel = cmp.trim();

    if (location == null) {
      location = loc;
    }
    
    if (network == null) {
      network = new String(header, 16, 1) + new String(header, 19, 1);   
    }

    String station = new String(header, 0, 5).trim();
    String code = station + readSeparator + channel + readSeparator + network;
    if (!"--".equals(location) && !"  ".equals(location)) {
      code += readSeparator + location.trim();
    }
    
    return code;
  }


  /**
   * @see gov.usgs.plot.data.file.SeismicDataFile#write()
   */
  public void write() throws IOException {
    FileOutputStream fos = new FileOutputStream(new File(fileName));
    writeEventFileHeader(fos);
    for (String code : waves.keySet()) {
      Wave wave = waves.get(code);
      writeChannelHeader(fos, code);
      byte[] bytes = new byte[wave.numSamples() * 4];
      for (int i = 0; i < wave.numSamples(); i++) {
        System.arraycopy(intAsBytes(wave.buffer[i]), 0, bytes, i * 4, 4);
      }
      writeRecord(fos, bytes);
    }

    fos.close();
  }


  public void writeEventFileHeader(FileOutputStream fos) throws IOException {
    byte[] header = new byte[80];
    Arrays.fill(header, (byte) 0x20);

    byte[] b;
    b = ("" + waves.size()).getBytes();
    System.arraycopy(b, 0, header, 31, Math.min(b.length, 3));

    double fileStart = Double.MAX_VALUE;
    double fileEnd = -Double.MAX_VALUE;
    for (String code : waves.keySet()) {
      fileStart = Math.min(fileStart, waves.get(code).getStartTime());
      fileEnd = Math.max(fileEnd, waves.get(code).getEndTime());
    }
    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    c.setTime(Util.j2KToDate(fileStart));

    b = ("" + (c.get(Calendar.YEAR) - 1900)).getBytes();
    System.arraycopy(b, 0, header, 33, Math.min(b.length, 3));

    b = ("" + c.get(Calendar.DAY_OF_YEAR)).getBytes();
    System.arraycopy(b, 0, header, 37, Math.min(b.length, 3));

    b = ("" + (c.get(Calendar.MONTH) + 1)).getBytes();
    System.arraycopy(b, 0, header, 41, Math.min(b.length, 2));

    b = ("" + c.get(Calendar.DAY_OF_MONTH)).getBytes();
    System.arraycopy(b, 0, header, 44, Math.min(b.length, 2));

    b = ("" + c.get(Calendar.HOUR_OF_DAY)).getBytes();
    System.arraycopy(b, 0, header, 47, Math.min(b.length, 2));

    b = ("" + c.get(Calendar.MINUTE)).getBytes();
    System.arraycopy(b, 0, header, 50, Math.min(b.length, 2));

    float f = c.get(Calendar.SECOND) + c.get(Calendar.MILLISECOND) / 1000;
    b = String.format("%6.3f", f).getBytes();
    System.arraycopy(b, 0, header, 53, Math.min(b.length, 6));

    b = String.format("%9.3f", (fileEnd - fileStart)).getBytes();
    System.arraycopy(b, 0, header, 60, 9);
    writeRecord(fos, header);

    Arrays.fill(header, (byte) 0x20);
    writeRecord(fos, header);

    int chanIdx = 0;
    for (String code : waves.keySet()) {
      if (chanIdx % 3 == 0) {
        if (chanIdx > 0) {
          writeRecord(fos, header);
        }
        Arrays.fill(header, (byte) 0x20);
      }

      String[] comps = code.split(writeSeparator);
      if (comps.length > 0) {
        b = comps[0].getBytes();
        System.arraycopy(b, 0, header, chanIdx % 3 * 26 + 1, Math.min(b.length, 4));
        if (b.length > 4) {
          System.arraycopy(b, 4, header, chanIdx % 3 * 26 + 9, 1);
        }
      }

      if (comps.length > 1) {
        b = comps[1].getBytes();
        System.arraycopy(b, 0, header, chanIdx % 3 * 26 + 5, Math.min(b.length, 2));
        if (b.length > 2) {
          System.arraycopy(b, 2, header, chanIdx % 3 * 26 + 8, 1);
        }
      }

      Wave wave = waves.get(code);
      double channelStart = wave.getStartTime() - fileStart;
      b = String.format("%7.2f", channelStart).getBytes();
      System.arraycopy(b, 0, header, chanIdx % 3 * 26 + 10, 7);

      double dataLen = (wave.getEndTime() - wave.getSamplingPeriod()) - wave.getStartTime();
      b = String.format("%8.2f", dataLen).getBytes();
      System.arraycopy(b, 0, header, chanIdx % 3 * 26 + 18, 8);
    }
    writeRecord(fos, header);
    Arrays.fill(header, (byte) 0x20);

    for (int i = 3 + ((channelCount + 2) / 3); i < 12; i++) {
      writeRecord(fos, header);
    }

  }


  /**
   * Write channel header.
   * @param fos file output stream
   * @param code channel code
   * @throws IOException exception
   */
  public void writeChannelHeader(FileOutputStream fos, String code) throws IOException {

    byte[] header = new byte[1040];
    Arrays.fill(header, (byte) 0x20);

    byte[] b;
    String[] comps = code.split(writeSeparator);
    if (comps.length > 0) {
      b = comps[0].getBytes();
      System.arraycopy(b, 0, header, 0, Math.min(b.length, 5));
    }

    if (comps.length > 1) {
      b = comps[1].getBytes();
      System.arraycopy(b, 0, header, 5, Math.min(b.length, 2));
      if (b.length > 2) {
        System.arraycopy(b, 2, header, 8, 1);
      }
    }

    if (comps.length > 2) {
      b = comps[2].getBytes();
      System.arraycopy(b, 0, header, 16, 1);
      if (b.length > 1) {
        System.arraycopy(b, 1, header, 19, 1);
      }
    }

    if (comps.length > 3) {
      b = comps[3].getBytes();
      System.arraycopy(b, 0, header, 7, 1);
      if (b.length > 1) {
        System.arraycopy(b, 1, header, 12, 1);
      }
    }

    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    Wave wave = waves.get(code);
    Date da = Util.j2KToDate(wave.getStartTime());
    c.setTime(da);

    b = String.format("%3d", c.get(Calendar.YEAR) - 1900).getBytes();
    System.arraycopy(b, 0, header, 9, Math.min(b.length, 3));

    b = String.format("%3d", c.get(Calendar.DAY_OF_YEAR)).getBytes();
    System.arraycopy(b, 0, header, 13, Math.min(b.length, 3));

    b = String.format("%2d", c.get(Calendar.MONTH) + 1).getBytes();
    System.arraycopy(b, 0, header, 17, Math.min(b.length, 2));

    b = String.format("%2d", c.get(Calendar.DAY_OF_MONTH)).getBytes();
    System.arraycopy(b, 0, header, 20, Math.min(b.length, 2));

    b = String.format("%2d", c.get(Calendar.HOUR_OF_DAY)).getBytes();
    System.arraycopy(b, 0, header, 23, Math.min(b.length, 2));

    b = String.format("%2d", c.get(Calendar.MINUTE)).getBytes();
    System.arraycopy(b, 0, header, 26, Math.min(b.length, 2));
    float f = c.get(Calendar.SECOND) + c.get(Calendar.MILLISECOND) / 1000;

    b = String.format("%6.3f", f).getBytes();
    System.arraycopy(b, 0, header, 29, b.length);

    b = String.format("%7.3f", wave.getSamplingRate()).getBytes();
    System.arraycopy(b, 0, header, 36, b.length);

    b = String.format("%7d", wave.numSamples()).getBytes();
    System.arraycopy(b, 0, header, 43, b.length);

    header[76] = '4';

    writeRecord(fos, header);
  }



  // read a single record in fortran "unformatted" form
  private byte[] readRecord(BufferedInputStream bis) throws IOException {
    int recordLen = readInt(bis, machineIntLength);
    byte[] record = new byte[recordLen];
    int read = bis.read(record);

    if (read != readInt(bis, machineIntLength)) {
      throw new IOException("Corrupt seisan record. Read " + read + ", expected " + recordLen);
    }

    return record;
  }


  // write a single record in fortran "unformatted" form
  private void writeRecord(FileOutputStream fos, byte[] bytes) throws IOException {
    int recordLen = bytes.length;
    fos.write(intAsBytes(recordLen));
    fos.write(bytes);
    fos.write(intAsBytes(recordLen));
  }


  private byte[] intAsBytes(int i) {
    return ByteBuffer.allocate(4).putInt(i).array();
  }


  private int readInt(BufferedInputStream bis, int len) throws IOException {
    byte[] buf = new byte[len];
    int read = bis.read(buf);
    if (read != len) {
      throw new IOException("Bad int read.");
    }

    return decodeInt(buf);
  }


  private int decodeInt(byte[] b) {
    ByteBuffer buffer = ByteBuffer.wrap(b);
    buffer.order(byteOrder);
    return (b.length == 8) ? (int) buffer.getLong() : buffer.getInt();
  }


}
