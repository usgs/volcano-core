package gov.usgs.volcanoes.core.legacy.ew.message;

import gov.usgs.volcanoes.core.data.Wave;
import gov.usgs.volcanoes.core.time.Time;
import gov.usgs.volcanoes.core.util.ByteUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A Java implementation of the TYPE_TRACEBUF and TYPE_TRACEBUF2 earthworm
 * messages. <code>startTime</code> and <code>endTime</code> are stored in their
 * native format: decimal seconds since Jan 1. 1970.<br>
 * <br>
 * The data fields in this class were derived from <code>trace_buf.h</code> in
 * the earthworm source.<br>
 * 
 * TODO: better separate TRACEBUF from TRACEBUF2
 * 
 * @author Dan Cervelli
 */
public class TraceBuf extends Message {

  /** microsecond conversion */
  protected static final long TO_USEC = (long) 1E6;

  /** microsecond conversion */
  protected static final double FROM_USEC = 1E-6;

  /** */
  protected static final short NULL_SHORT = (short) 0x7f7f;

  /** */
  protected static final byte NULL_BYTE = (byte) 0x7f;

  /** Channel PIN */
  protected int pin; // Pin number

  /** Number of samples in packet */
  protected int numSamples; // Number of samples in packet

  /** Start time in "epoch microseconds". */
  protected long firstSampleTime; // time of first sample in epoch microseconds

  /** Sampling frequency in microseconds. */
  protected long samplingPeriod; // Sample period; nominal

  /** Station name. */
  protected String station; // 7 bytes

  /** Network name. */
  protected String network; // 9 bytes

  /** Channel name. */
  protected String channel; // 9 bytes

  // TODO: remove
  /** Location code. */
  protected String location;

  /**
   * Data type: i2, i4, s2, or s4. i or s signify Intel or Sun byte order, and
   * 2 or 4 specify bytes per sample. However, all data are converted to s4
   * when read into this message.
   */
  protected String dataType; // 3 bytes

  /** Quality flag. I have no information about this field. */
  protected String quality; // 2 bytes

  /** Byte padding. */
  protected String pad; // 2 bytes
                        // 32 bytes

  /** The samples. */
  protected int[] data;


  /** Registration offset after <code>register()</code> has been called. */
  protected long registrationOffset; // this is not part of earthworm

  // TODO: remove
  protected boolean isTraceBuf2 = false;

  /** Generic constructor. */
  public TraceBuf() {
    super();
  }

  public TraceBuf(byte[] b) throws IOException {
    super();
    processBytes(new DataInputStream(new ByteArrayInputStream(b)), false);
  }

  /**
   * Constructs a <code>TraceBuf</code> from an array of bytes.
   * 
   * @param b
   *            the bytes
   * @param i
   *            the number of bytes in the message (!= b.length)
   * @throws IOException
   */
  protected TraceBuf(byte[] b, int i, int seq) throws IOException {
    super(b, i, seq);
  }

  /**
   * Creates a TraceBuf from a Wave. This is useful for putting non- Earthworm
   * data in a Winston database.
   * 
   * @param code
   *            '$' separated SCNL
   * @param sw
   *            the wave
   */
  public TraceBuf(String code, Wave sw) {
    data = sw.buffer;
    samplingPeriod = Math.round(TO_USEC / sw.getSamplingRate());
    firstSampleTime = Math.round(Time.j2kToEw(sw.getStartTime()) * TO_USEC);
    pin = -1;
    numSamples = data.length;
    dataType = "s4";
    quality = "";
    pad = "";
    String[] cc = code.split("\\$");
    station = cc[0];
    channel = cc[1];
    network = cc[2];

    // TODO: remove
    location = null;
    if (cc.length >= 4) {
      isTraceBuf2 = true;
      location = cc[3];
    }
  }

  /**
   * 
   * @param b
   * @param i
   * @return
   * @throws IOException
   */
  public static Message createFromBytes(byte[] b, int i, int seq) throws IOException {
    TraceBuf tb = new TraceBuf(b, i, seq);
    tb.processBytes(new DataInputStream(new ByteArrayInputStream(tb.bytes)), false);
    return tb;
  }

  public static Message createFromBytesAsTraceBuf2(byte[] b, int i, int seq) throws IOException {
    TraceBuf tb = new TraceBuf(b, i, seq);
    tb.processBytes(new DataInputStream(new ByteArrayInputStream(tb.bytes)), true);
    return tb;
  }

  public void createBytes() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(baos);
      out.writeInt(pin);
      out.writeInt(numSamples);
      out.writeDouble(firstSampleTime());
      out.writeDouble(lastSampleTime());
      out.writeDouble(samplingRate());

      int p = 7 - station.length();
      out.writeBytes(station);
      for (int i = 0; i < p; i++)
        out.write((byte) 0);

      p = 9 - network.length();
      out.writeBytes(network);
      for (int i = 0; i < p; i++)
        out.write((byte) 0);

      // TraceBuf2 fix provided by Philip Crotwell
      if (isTraceBuf2 && location != null) {
        p = 4 - channel.length();
        out.writeBytes(channel);
        for (int i = 0; i < p; i++)
          out.write((byte) 0);
        p = 5 - location.length();
        out.writeBytes(location);
        for (int i = 0; i < p; i++)
          out.write((byte) 0);
      } else {
        p = 9 - channel.length();
        out.writeBytes(channel);
        for (int i = 0; i < p; i++)
          out.write((byte) 0);
      }

      out.writeBytes("s4");
      for (int i = 0; i < 5; i++)
        out.write((byte) 0);

      for (int i = 0; i < data.length; i++)
        out.writeInt(data[i]);
      out.write((byte) 0);
      bytes = baos.toByteArray();
    } catch (IOException e) {
    }
  }

  /**
   * Read samples from stream
   * 
   * @param in
   *            the input stream
   * @return
   * @throws IOException
   */
  protected int[] readData(DataInputStream in) throws IOException {
    int[] data = new int[numSamples];
    boolean swap = dataType.charAt(0) == 'i';
    boolean isShort = (dataType.charAt(1) == '2');

    // yes cumbersome, but it works with a single pass
    if (isShort && swap)
      for (int i = 0; i < numSamples; i++)
        data[i] = ByteUtil.swap(in.readShort());
    else if (isShort)
      for (int i = 0; i < numSamples; i++)
        data[i] = in.readShort();
    else if (swap)
      for (int i = 0; i < numSamples; i++)
        data[i] = ByteUtil.swap(in.readInt());
    else
      for (int i = 0; i < numSamples; i++)
        data[i] = in.readInt();

    return data;
  }

  /**
   * Fills the fields based on raw message data. There is a flag to specify
   * whether this is a TRACEBUF or a TRACEBUF2. In theory, this could be
   * avoided if the version field in TRACEBUF2 was checked but there are some
   * issues with this, so I leave it to the user to choose.
   * 
   * @param in
   *            the input stream
   * @param isTraceBuf2
   *            whether this is a TRACEBUF2
   * @throws IOException
   */
  public void processBytes(DataInputStream in, boolean isTraceBuf2) throws IOException {

    this.isTraceBuf2 = isTraceBuf2;
    pin = in.readInt();
    numSamples = in.readInt();
    double startTime = in.readDouble();
    in.readDouble(); // discard lastSampleTime; calculate it instead
    double samplingRate = in.readDouble();

    byte[] buf = new byte[32];
    in.read(buf, 0, 32);
    station = ByteUtil.bytesToString(buf, 0, 7).trim();
    network = ByteUtil.bytesToString(buf, 7, 9).trim();
    if (!isTraceBuf2) {
      channel = ByteUtil.bytesToString(buf, 16, 9).trim();
      location = null;
    } else {
      channel = ByteUtil.bytesToString(buf, 16, 4).trim();
      location = ByteUtil.bytesToString(buf, 20, 3).trim();

      // TODO: fix this. null location and -- location are not the same
      if (location.equals("--"))
        location = null;
    }
    dataType = ByteUtil.bytesToString(buf, 25, 3).trim();
    quality = ByteUtil.bytesToString(buf, 28, 2).trim();

    if (dataType.charAt(0) == 'i') {
      pin = ByteUtil.swap(pin);
      numSamples = ByteUtil.swap(numSamples);
      startTime = ByteUtil.swap(startTime);
      samplingRate = ByteUtil.swap(samplingRate);
    }

    firstSampleTime = Math.round(startTime * TO_USEC);
    samplingPeriod = Math.round(TO_USEC / samplingRate);

    data = readData(in);
  }

  /**
   * Registers the starting time to the nearest even interval based on the
   * wave's sampling rate. The offset between the original start time and the
   * registered time is stored in <code>registrationOffset</code>.
   */
  public void register() {
    long dif = firstSampleTime % samplingPeriod;
    if (dif >= samplingPeriod / 2)
      registrationOffset = samplingPeriod - dif;
    else
      registrationOffset = -dif;

    firstSampleTime += registrationOffset;
  }

  /**
   * Converts a list of time-ordered, non-overlapping <code>TraceBuf</code>s
   * into a <code>Wave</code>. Skips tracebufs whose sampling rate does not
   * match the first tracebuf. Starting time will be rounded to the nearest
   * even interval based on the wave's sampling rate.
   * 
   * Work with timestamps as microseconds to avoid rounding errors.
   * 
   * @param traceBufs
   *            the <code>TraceBuf</code>s (must be sorted in time order)
   * 
   * @return the wave
   */
  public static Wave traceBufToWave(List<TraceBuf> traceBufs) {
    // Nothing in, nothing out
    if (traceBufs == null || traceBufs.size() <= 0)
      return null;

    // get rid of all the bad stuff, except gaps
    normalize(traceBufs);

    final TraceBuf firstTraceBuf = traceBufs.get(0);
    final TraceBuf lastTraceBuf = traceBufs.get(traceBufs.size() - 1);

    long samplingPeriod = firstTraceBuf.samplingPeriod;
    long lastSampleTime =
        lastTraceBuf.firstSampleTime + ((lastTraceBuf.numSamples - 1) * samplingPeriod);

    int numSamples = (int) ((lastSampleTime - firstTraceBuf.firstSampleTime) / samplingPeriod + 1);
    int[] buffer = new int[numSamples];
    Arrays.fill(buffer, Wave.NO_DATA);

    int sampleIndex = 0;
    long lastSampleTimeSeen = firstTraceBuf.firstSampleTime - samplingPeriod;

    long lastStart = 0;
    int lastCount = 0;
    for (TraceBuf tb : traceBufs) {
      while (lastSampleTimeSeen + samplingPeriod < tb.firstSampleTime) {
        sampleIndex++;
        lastSampleTimeSeen += tb.samplingPeriod;
      }

      // assume more recent samples are more correct samples
      if (tb.firstSampleTime <= lastSampleTimeSeen) {
        int overlap = (int) ((lastSampleTimeSeen - tb.firstSampleTime) / samplingPeriod + 1);
        System.err.println(
            "Overlapping tracebuf found in " + tb.toWinstonString() + ". Overlap count=" + overlap);
        System.err.println(lastStart + " + (" + lastCount + " - 1) * " + samplingPeriod + " - "
            + tb.firstSampleTime + " = "
            + (lastStart + (lastCount - 1) * samplingPeriod - tb.firstSampleTime));
        System.err.println("count " + tb.numSamples + " : rate " + tb.samplingRate()
            + " : duration " + ((tb.samplingPeriod * tb.numSamples)));
        sampleIndex -= overlap;
      }

      // this shouldn't happen
      if (sampleIndex + tb.numSamples > buffer.length) {
        System.err.println("Too many samples in " + tb.toWinstonString()
            + ". Variable sampling rate no supported.");
        continue;
      }

      for (int j = 0; j < tb.numSamples; j++)
        buffer[sampleIndex++] = tb.data[j];

      lastSampleTimeSeen = tb.firstSampleTime + ((tb.numSamples - 1) * samplingPeriod);
      lastStart = tb.firstSampleTime;
      lastCount = tb.numSamples;
    }

    Wave wave =
        new Wave(buffer, (firstTraceBuf.firstSampleTime * FROM_USEC), firstTraceBuf.samplingRate());
    wave.setRegistrationOffset(firstTraceBuf.registrationOffset);

    return wave;
  }


  /**
   * Cleanup a list of tracebufs
   * 
   * @param traceBufs
   */
  private static void normalize(List<TraceBuf> traceBufs) {
    final TraceBuf firstTraceBuf = traceBufs.get(0);

    Iterator<TraceBuf> i = traceBufs.iterator();
    while (i.hasNext()) {
      TraceBuf tb = i.next();

      tb.register();
      if (tb.samplingPeriod != firstTraceBuf.samplingPeriod) {
        i.remove();
      }
    }
  }

  /**
   * Gets the start time.
   * 
   * @return the start time
   */
  public double getStartTime() {
    return firstSampleTime();
  }

  /**
   * Gets the start time as a j2k.
   * 
   * @return the j2k start time
   */
  public double getStartTimeJ2K() {
    return Time.ewToj2k(getStartTime());
  }

  /**
   * Gets the true end time (<code>endTime</code>+1/<code>samplingRate</code> ).
   * 
   * @return the end time
   */
  public double getEndTime() {
    return lastSampleTime() + samplingPeriod();
  }


  /**
   * Gets the true end time as a J2K.
   * 
   * @return the end time
   */
  public double getEndTimeJ2K() {
    return Time.ewToj2k(getEndTime());
  }

  /**
   * Gets the timestamp of the first sample.
   * 
   * @return the first sample time
   */
  public double firstSampleTime() {
    return firstSampleTime * FROM_USEC;
  }

  /**
   * Gets the timestamp of the last sample.
   * 
   * @return the last sample time
   */
  public double lastSampleTime() {
    return (firstSampleTime + ((numSamples - 1) * samplingPeriod)) * FROM_USEC;
  }

  /**
   * Gets the timestamp of the last sample.
   * 
   * @return the last sample time
   */
  public double samplingRate() {
    return 1 / (samplingPeriod * FROM_USEC);
  }

  /**
   * Gets the timestamp of the last sample.
   * 
   * @return the last sample time
   */
  public double samplingPeriod() {
    return samplingPeriod * FROM_USEC;
  }

  /**
   * Gets the sample count.
   * 
   * @return the sample count
   */
  public double numSamples() {
    return numSamples;
  }

  /**
   * Gets the sample count.
   * 
   * @return the sample count
   */
  public String station() {
    return station;
  }

  /**
   * Gets the sample count.
   * 
   * @return the sample count
   */
  public String channel() {
    return channel;
  }

  /**
   * Gets the sample count.
   * 
   * @return the sample count
   */
  public String network() {
    return network;
  }

  /**
   * Gets the sample count.
   * 
   * @return the sample count
   */
  public String location() {
    return location;
  }

  /**
   * Gets the data type.
   * 
   * @return the quality
   */
  public String quality() {
    return quality;
  }

  /**
   * Gets the data type.
   * 
   * @return the pad
   */
  public String pad() {
    return pad;
  }

  /**
   * Gets the data type.
   * 
   * @return the data type
   */
  public String dataType() {
    return dataType;
  }

  // /**
  // * Gets the start time as a <code>Date</code>
  // *
  // * @return the start time
  // */
  // public Date getStartTimeDate() {
  // return Util.j2KToDate(getStartTimeJ2K());
  // }

  /**
   * Gets the samples
   * 
   * @return the samples
   */
  public int[] samples() {
    return data;
  }

  /**
   * Gets a string describing this TYPE_TRACEBUF message.
   * 
   * @return the string
   */
  public String toString() {
    // return (isTraceBuf2 ? "TYPE_TRACEBUF2" : "TYPE_TRACEBUF") + ": " + station + " " + channel +
    // " " + network
    // + ", " + firstSampleTime + "->" + lastSampleTime() + "," + dataType + " " + quality + " " +
    // numSamples;
    return String.format("TRACEBUF%s: %s %s %s %s, %d, %s, %.4f -> %.4f", isTraceBuf2 ? "2" : "",
        station, channel, network, ((isTraceBuf2 && location != null) ? location : "--"),
        numSamples, dataType, firstSampleTime(), lastSampleTime());
  }

  /**
   * Gets the channel name formatted for Winston.
   * 
   * @return the formatted channel name
   */
  public String toWinstonString() {
    String code = station + "$" + channel + "$" + network;

    if (isTraceBuf2 && location != null && !location.equals("--"))
      code += "$" + location;

    return code;
  }

  /**
   * Converts the message into a <code>ByteBuffer</code> that can then be sent
   * over a network or written to a file.
   * 
   * @return the buffer
   */
  public ByteBuffer toByteBuffer() {
    ByteBuffer bb = ByteBuffer.allocate(64 + 4 * data.length);
    bb.putInt(pin);
    bb.putInt(numSamples);
    bb.putDouble(firstSampleTime());
    bb.putDouble(lastSampleTime());
    bb.putDouble(samplingRate());

    bb.put(station.getBytes());
    for (int i = station.length(); i < 7; i++)
      bb.put((byte) 0);

    bb.put(network.getBytes());
    for (int i = network.length(); i < 9; i++)
      bb.put((byte) 0);

    bb.put(channel.getBytes());
    for (int i = channel.length(); i < 9; i++)
      bb.put((byte) 0);

    bb.put(dataType.getBytes());
    for (int i = dataType.length(); i < 3; i++)
      bb.put((byte) 0);

    // quality
    bb.put((byte) 0);
    bb.put((byte) 0);

    // pad
    bb.put((byte) 0);
    bb.put((byte) 0);

    for (int i = 0; i < data.length; i++)
      bb.putInt(data[i]);

    bb.flip();
    return bb;
  }
}
