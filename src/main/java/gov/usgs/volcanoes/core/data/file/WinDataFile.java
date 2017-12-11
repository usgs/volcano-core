package gov.usgs.volcanoes.core.data.file;

import gov.usgs.volcanoes.core.data.Wave;
import gov.usgs.volcanoes.core.time.J2kSec;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;


/**
 * A class to read WIN files, adapted from code written by Joel Shellman,
 * which in turn was adapted from Fissures code, via WIN.java.
 * 
 * @author Diana Norgaard
 */
public class WinDataFile extends SeismicDataFile {

  public static File configFile = null;
  private String timeZone = "UTC";
  private HashMap<Integer, String> channelInfo = new HashMap<Integer, String>();
  private Map<Integer, List<ChannelData>> channelMap = new TreeMap<Integer, List<ChannelData>>();
  private ByteBuffer converter2 = ByteBuffer.wrap(new byte[2]);
  private ByteBuffer converter4 = ByteBuffer.wrap(new byte[4]);

  public WinDataFile(String filename) {
    super(filename, "WIN^");
  }

  static int intFromSingleByte(byte b) {
    return b;
  }

  private int intFromFourBytes(byte[] bites) {
    converter4.clear();
    converter4.mark();
    converter4.put(bites);
    converter4.rewind();
    return converter4.getInt();
  }

  private int intFromThreeBytes(byte[] bites) {
    byte pad = (byte) ((bites[0] < 0) ? -1 : 0);
    byte[] padded = new byte[] {pad, bites[0], bites[1], bites[2]};
    return intFromFourBytes(padded);
  }

  private short shortFromTwoBytes(byte[] bites) {
    converter2.clear();
    converter2.mark();
    converter2.put(bites);
    converter2.rewind();
    return converter2.getShort();
  }

  private int decodeBcd(byte[] b) {
    StringBuffer buf = new StringBuffer(b.length * 2);
    for (int i = 0; i < b.length; ++i) {
      buf.append((char) (((b[i] & 0xf0) >> 4) + '0'));
      if ((i != b.length) && ((b[i] & 0xf) != 0x0A)) {
        buf.append((char) ((b[i] & 0x0f) + '0'));
      }
    }
    return Integer.parseInt(buf.toString());
  }

  /**
   * Reads the header from the given stream.
   * 
   * @param dis DataInputStream to read WIN from
   * @throws FileNotFoundException if the file cannot be found
   * @throws IOException if it isn't a WIN file
   */
  private void readHeader(ChannelData c, DataInputStream dis)
      throws FileNotFoundException, IOException {
    // read first 4 byte: file size
    byte[] fourBytes = new byte[4];
    byte[] oneByte = new byte[1];

    dis.readFully(fourBytes);
    c.packetSize = intFromFourBytes(fourBytes);

    // read next 6 bytes: yy mm dd hh mi ss
    dis.readFully(oneByte);
    c.year = 2000 + decodeBcd(oneByte);

    dis.readFully(oneByte);
    c.month = decodeBcd(oneByte);

    dis.readFully(oneByte);
    c.day = decodeBcd(oneByte);

    dis.readFully(oneByte);
    c.hour = decodeBcd(oneByte);

    dis.readFully(oneByte);
    c.minute = decodeBcd(oneByte);

    dis.readFully(oneByte);
    c.second = decodeBcd(oneByte);
  }

  /**
   * Read the data portion of WIN format from the given stream.
   * 
   * @param dis DataInputStream to read WIN from
   * @throws IOException if it isn't a WIN file
   */
  private void readData(final ChannelData header, final DataInputStream dis) throws IOException {
    int bytesRead = 10;

    do {
      ChannelData c = new ChannelData(header);
      c.inBuf = new ArrayList<Integer>();
      byte[] oneByte = new byte[1];
      dis.readFully(oneByte);

      dis.readFully(oneByte);
      c.channelNumber = intFromSingleByte(oneByte[0]);

      dis.readFully(oneByte);
      byte sampleRateUpperBits = (byte) (oneByte[0] & 0xF);
      c.dataSize = intFromSingleByte(oneByte[0]) >> 4;

      dis.readFully(oneByte);
      c.samplingRate = intFromSingleByte(oneByte[0]) + (sampleRateUpperBits << 4);

      byte[] fourBytes = new byte[4];
      dis.readFully(fourBytes);
      int accum = intFromFourBytes(fourBytes);

      c.inBuf.add(accum);

      float[] d = new float[(int) c.samplingRate - 1];

      bytesRead += 8;
      if (c.dataSize == 0) {
        for (int ix = 0; ix < ((int) c.samplingRate - 1); ix++) {
          accum += dis.readByte();
          c.inBuf.add(accum);

        }
      } else if (c.dataSize == 1) {
        for (int ix = 0; ix < ((int) c.samplingRate - 1); ix++) {
          accum += dis.readByte();
          c.inBuf.add(accum);
          bytesRead++;
        }
      } else if (c.dataSize == 2) {
        byte[] twoBytes = new byte[2];
        for (int ix = 0; ix < ((int) c.samplingRate - 1); ix++) {
          dis.readFully(twoBytes);
          accum += shortFromTwoBytes(twoBytes);
          d[ix] = accum;
          c.inBuf.add(accum);
          bytesRead += 2;
        }
      } else if (c.dataSize == 3) {
        byte[] threeBytes = new byte[3];
        for (int ix = 0; ix < ((int) c.samplingRate - 1); ix++) {
          dis.readFully(threeBytes);
          accum += intFromThreeBytes(threeBytes);
          d[ix] = accum;
          c.inBuf.add(accum);
          bytesRead += 3;
        }
      } else if (c.dataSize == 4) {
        for (int ix = 0; ix < ((int) c.samplingRate - 1); ix++) {
          dis.readFully(fourBytes);
          accum += intFromFourBytes(fourBytes);

          d[ix] = accum;
          c.inBuf.add(accum);
          bytesRead += 4;
        }
      }
      List<ChannelData> list = channelMap.get(c.channelNumber);
      if (list == null) {
        list = new ArrayList<ChannelData>();
        channelMap.put(c.channelNumber, list);
      }
      list.add(c);
    } while (bytesRead < header.packetSize);
  }

  /**
   * @see gov.usgs.plot.data.file.SeismicDataFile#read()
   */
  public void read() throws IOException {
    // Read WIN configuration file
    if (configFile != null) {
      FileReader fileReader = new FileReader(configFile);
      BufferedReader reader = new BufferedReader(fileReader);
      timeZone = reader.readLine();
      channelInfo.clear();
      int num = 0;
      while (true) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        channelInfo.put(num, line);
        num++;
      }
      reader.close();
      fileReader.close();
    }

    // Read WIN file
    FileInputStream fis = new FileInputStream(fileName);
    BufferedInputStream buf = new BufferedInputStream(fis);
    DataInputStream dis = new DataInputStream(buf);
    while (dis.available() != 0) {
      ChannelData cur = new ChannelData();
      readHeader(cur, dis);
      readData(cur, dis);
    }
    dis.close();

    // create wave objects
    for (List<ChannelData> channels : channelMap.values()) {
      List<Wave> subParts = new ArrayList<Wave>(channels.size());
      int index = channels.get(0).channelNumber;
      String channel = channelInfo.get(index);
      if (channel == null) {
        channel = Integer.toString(index);
      } else {
        channel = channel.replaceAll(" ", "\\$");
      }
      for (ChannelData c : channels) {
        subParts.add(toWave(c));
        Wave wave = Wave.join(subParts);
        waves.put(channel, wave);
      }
    }
  }

  private Wave toWave(ChannelData c) {
    Wave sw = new Wave();
    sw.setStartTime(J2kSec.fromDate(getStartTime(c)));
    sw.setSamplingRate(c.samplingRate);
    sw.buffer = new int[c.inBuf.size()];
    for (int j = 0; j < c.inBuf.size(); j++) {
      sw.buffer[j] = c.inBuf.get(j);
    }
    return sw;
  }

  /**
   * Get start time of data.
   * 
   * @return start time of data
   */
  private Date getStartTime(ChannelData c) {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
    cal.setTimeInMillis(0);
    cal.set(Calendar.YEAR, c.year);
    cal.set(Calendar.MONTH, c.month - 1);
    cal.set(Calendar.DAY_OF_MONTH, c.day);
    cal.set(Calendar.HOUR_OF_DAY, c.hour);
    cal.set(Calendar.MINUTE, c.minute);
    cal.set(Calendar.SECOND, c.second);
    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
    return cal.getTime();
  }

  /**
   * Write function is not supported for WIN.
   * 
   * @see gov.usgs.plot.data.file.SeismicDataFile#write()
   */
  @Override
  public void write() throws IOException {
    // Not supported
  }

  public static class ChannelData {

    public int packetSize;
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;
    public int channelNumber;
    public int dataSize;
    public float samplingRate;
    public List<Integer> inBuf;

    public ChannelData() {}

    /**
     * Constructor with argument.
     * @param copy channel data
     */
    public ChannelData(ChannelData copy) {
      this.packetSize = copy.packetSize;
      this.year = copy.year;
      this.month = copy.month;
      this.day = copy.day;
      this.hour = copy.hour;
      this.minute = copy.minute;
      this.second = copy.second;
      this.channelNumber = copy.channelNumber;
      this.dataSize = copy.dataSize;
      this.samplingRate = copy.samplingRate;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
      String text = String.format("Channel: %s, Sample Rate: %s, Data Size: %s", channelNumber,
          samplingRate, dataSize);
      return text;
    }
  }
}
