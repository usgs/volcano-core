package gov.usgs.volcanoes.core.legacy.pinnacle;

import gov.usgs.volcanoes.core.time.J2kSec;
import gov.usgs.volcanoes.core.time.Time;
import java.text.SimpleDateFormat;

/**
 * <p>This class presents block of status data retrieved from a device.
 * It provides methods to parse block content and unpack desired parameters.</p>
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/09/21 18:05:39  dcervelli
 * Moved createInt here from Command.
 *
 * Revision 1.1  2005/09/20 18:22:06  dcervelli
 * Initial commit.
 *
 * @author Dan Cervelli
 * @version $Id: StatusBlock.java,v 1.3 2007-04-25 21:01:26 dcervelli Exp $
 */
public class StatusBlock
{
  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private byte[] buffer;

  /**
   * <p>Constructor</p>
   * @param buf byte array got from device
   */
  public StatusBlock(byte[] buf)
  {
    buffer = buf;
  }

  /**
   * <p>Create integer from 4 bytes</p>
   */
  public static int createInt(byte b0, byte b1, byte b2, byte b3)
  {
    return (int)((b0 & 0xff) |
        (((b1 & 0xff) << 8) & 0x0000ff00) |
        (((b2 & 0xff) << 16) & 0x00ff0000)  |
        (((b3 & 0xff) << 24) & 0xff000000) & 0x00000000ffffffff);
  }

  /**
   * <p>Getter for whole buffer</p>
   */
  public byte[] getBuffer()
  {
    return buffer;
  }

  /**
   * <p>Parse device time from buffer</p>
   * @return time as integer
   */
  public int getTime()
  {
    return createInt(buffer[0], buffer[1], buffer[2], buffer[3]);
  }

  /**
   * <p>Parse device time from buffer</p>
   * @return time as J2k
   */
  public double getJ2K()
  {
    return Time.ewToj2k(getTime());
  }

  /**
   * <p>Parse X data value count from buffer</p>
   */
  public int getXCounts()
  {
    return createInt(buffer[4], buffer[5], buffer[6], (byte)0);
  }

  /**
   * <p>Parse Y data value from buffer</p>
   */
  public int getYCounts()
  {
    return createInt(buffer[7], buffer[8], buffer[9], (byte)0);
  }

  /**
   * <p>Get adjusted X data value in mv</p>
   */
  public double getXMillis()
  {
    return (double)(getXCounts() * 2.980232e-4) - 2500.0;
  }

  /**
   * <p>Get adjusted Y data value in mv</p>
   */
  public double getYMillis()
  {
    return (double)(getYCounts() * 2.980232e-4) - 2500.0;
  }

  /**
   * <p>Parse rezero enabling status from buffer</p>
   */
  public boolean isRezeroEnabled()
  {
    return (buffer[19] & 64) > 0;
  }

  /**
   * <p>Parse rezero X axis enabling status from buffer</p>
   */
  public boolean isRezeroingX()
  {
    return (buffer[19] & 32) > 0;
  }

  /**
   * <p>Parse rezero Y axis enabling status from buffer</p>
   */
  public boolean isRezeroingY()
  {
    return (buffer[19] & 16) > 0;
  }

  /**
   * <p>Parse Y ampl gain value from buffer</p>
   */
  public int getGain()
  {
//		return ((int)buffer[19] & 2) + (buffer[19] & 1);
    return ((int)buffer[19] & 3);
  }

  /**
   * <p>Parse temperature data values count from buffer</p>
   */
  public int getTemperatureCounts()
  {
    return createInt(buffer[12], buffer[13], buffer[14], (byte)0);
  }

  /**
   * <p>Get adjusted temperature value in Celsius degrees</p>
   */
  public double getTemperature()
  {
    return (double)getTemperatureCounts() * 2.980232e-5;
  }

  /**
   * <p>Parse voltage data values count from buffer</p>
   */
  public int getVoltageCounts()
  {
    return createInt(buffer[15], buffer[16], buffer[17], (byte)0);
  }

  /**
   * <p>Get adjusted voltage value in dc volts</p>
   */
  public double getVoltage()
  {
    return (double)getVoltageCounts() * 3.2782552e-6;
  }

  /**
   * <p>Get stored buffer checksum</p>
   */
  public int getChecksum()
  {
    return buffer[31];
  }

  /**
   * @return string buffer representation</p>
   */
  public String toString()
  {
    return String.format("StatusBlock: T: [%d/%s] X: [%d/%.3f], Y: [%d/%.3f]\n" +
            "  Rezeroing: enabled: %b, X: %b, Y: %b\n" +
            "       Gain: %d\n" +
            "Temperature: [%d/%.3f]\n" +
            "    Voltage: [%d/%.3f]",
        getTime(), dateFormat.format(J2kSec.asDate(getJ2K())),
        getXCounts(), getXMillis(),
        getYCounts(), getYMillis(),
        isRezeroEnabled(), isRezeroingX(), isRezeroingY(),
        getGain(),
        getTemperatureCounts(), getTemperature(),
        getVoltageCounts(), getVoltage());
  }
}
