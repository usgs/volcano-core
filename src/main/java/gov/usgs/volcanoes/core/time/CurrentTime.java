package gov.usgs.volcanoes.core.time;

import gov.usgs.volcanoes.core.configfile.ConfigFile;
import gov.usgs.volcanoes.core.contrib.NtpMessage;
import gov.usgs.volcanoes.core.util.Retriable;
import gov.usgs.volcanoes.core.util.StringUtils;
import gov.usgs.volcanoes.core.util.UtilException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * A class for getting the current time using an NTP server. Once accurate
 * time is retrieved from one of the servers, it is cached. By default,
 * if a call is made for the current time after more than 10 minutes since the
 * accurate time was last retrieved then the NTP server is queried again.
 * 
 * <p>If a file called 'NTP.config' is in the current directory it is used to
 * optionally specify the servers, timeout, or recalibration interval. Example:
 * 
 * <p># servers, comma-separated list in order
 * servers=time-a.nist.gov,132.163.4.101,129.6.15.28
 * 
 * <p># timeout in milliseconds
 * timeout=500
 * 
 * <p># interval between recalibration in milliseconds
 * recalibrationInterval=60000
 * 
 * @author Dan Cervelli
 */
public class CurrentTime {
  private static final String CONFIG_FILENAME = "NTP.config";
  private static final int DEFAULT_RECALIBRATION_INTERVAL = 10 * 60 * 1000; // 10 minutes
  private static final int DEFAULT_TIMEOUT = 500;
  private static final String[] DEFAULT_NTP_SERVERS =
      new String[] { "0.pool.ntp.org", "1.pool.ntp.org", "2.pool.ntp.org", "time.nist.gov" };

  private long lastOffsetCheck = 0;
  private long lastOffset;
  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  private boolean netFailed = false;
  private boolean synchronizeDisabled = false;
  private long recalibrationInterval = DEFAULT_RECALIBRATION_INTERVAL;
  private int timeout = DEFAULT_TIMEOUT;

  private String[] servers = DEFAULT_NTP_SERVERS;

  private static final Logger LOGGER = LoggerFactory.getLogger(CurrentTime.class);

  private static CurrentTime currentTime;

  /**
   * Default constructor
   */
  private CurrentTime() {
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    List<String> canaditeNames = new LinkedList<String>();
    canaditeNames.add(CONFIG_FILENAME);
    canaditeNames.add(System.getProperty("user.home") + File.separatorChar + CONFIG_FILENAME);
    String configFile = StringUtils.stringToString(ConfigFile.findConfig(canaditeNames), CONFIG_FILENAME);

    ConfigFile cf;
    try {
      cf = new ConfigFile(configFile);
      if (cf.wasSuccessfullyRead()) {
        String svrs = cf.getString("servers");
        if (svrs != null)
          servers = svrs.split(",");
        timeout = StringUtils.stringToInt(cf.getString("timeout"), DEFAULT_TIMEOUT);
        synchronizeDisabled = StringUtils.stringToBoolean(cf.getString("synchronizeDisabled"));
        recalibrationInterval =
            StringUtils.stringToInt(cf.getString("recalibrationInterval"), DEFAULT_RECALIBRATION_INTERVAL);
      }
    } catch (FileNotFoundException ignored) {
      LOGGER.debug("Config file not found, using defaults.");
    }

  }

  /**
   * Realize singleton pattern, permit only 1 instance of class in the application
   */
  public static CurrentTime getInstance() {
    return CurrentTimeHolder.currentTime;
  }

  /**
   * Sets recalibration interval, in milliseconds
   * 
   * @param ms milliseconds
   */
  public void setRecalibrationInterval(long ms) {
    recalibrationInterval = ms;
  }

  /**
   * Get current time
   * 
   * @return calibrated time, as formatted string
   */
  public String nowString() {
    return dateFormat.format(nowDate());
  }

  /**
   * Get current time
   * 
   * @return calibrated time, in seconds since standard java time begin.
   */
  public double nowEW() {
    return ((double) now() / (double) 1000);
  }

  /**
   * Get current time
   * 
   * @return calibrated time, in seconds since ???.
   */
  public double nowJ2K() {
    return (((double) now() / (double) 1000) - 946728000);
  }

  /**
   * Get current date
   * 
   * @return calibrated time, as Date
   */
  public Date nowDate() {
    return new Date(now());
  }

  /**
   * Get current time
   * 
   * @return calibrated time, as long
   */
  public long now() {
    if (netFailed)
      return System.currentTimeMillis();

    if (lastOffsetCheck == 0
        || System.currentTimeMillis() - lastOffsetCheck > recalibrationInterval)
      getOffset();

    return System.currentTimeMillis() + lastOffset;
  }

  /**
   * Get last offset
   * 
   * @return current offset between local time and ntp time
   */
  public long getLastOffset() {
    return lastOffset;
  }

  /**
   * Query configured ntp servers
   * 
   * @return result
   */
  public synchronized long getOffset() {
    if (synchronizeDisabled == true) {
      return 0;
    }

    Retriable<Long> rt = new Retriable<Long>("getCurrentTime()", servers.length) {
      private int attempt = 0;

      public void attemptFix() {
        attempt++;
      }

      public boolean attempt() throws UtilException {
        double localClockOffset = 0;
        DatagramSocket socket = null;
        try {
          socket = new DatagramSocket();
          socket.setSoTimeout(timeout);
          InetAddress address = InetAddress.getByName(servers[attempt]);
          byte[] buf = new NtpMessage().toByteArray();
          DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 123);

          NtpMessage.encodeTimestamp(packet.getData(), 40,
              (System.currentTimeMillis() / 1000.0) + 2208988800.0);

          socket.send(packet);

          // Get response
          packet = new DatagramPacket(buf, buf.length);
          socket.receive(packet);

          // Immediately record the incoming timestamp
          double destinationTimestamp = (System.currentTimeMillis() / 1000.0) + 2208988800.0;

          // Process response
          NtpMessage msg = new NtpMessage(packet.getData());

          localClockOffset = ((msg.receiveTimestamp - msg.originateTimestamp)
              + (msg.transmitTimestamp - destinationTimestamp)) / 2;
          long l = Math.round(localClockOffset * 1000);
          result = new Long(l);
          lastOffset = l;
          lastOffsetCheck = System.currentTimeMillis();
          LOGGER.debug("Successfully synchronized with NTP server: " + servers[attempt]);
          socket.close();
          return true;
        } catch (Exception e) {
          LOGGER.debug("Could not synchronize with NTP server: " + servers[attempt]);
        }

        try {
          socket.close();
        } catch (Exception e) {
        }

        return false;
      }
    };
    Long result = null;
    try {
      rt.setOutput(false);
      result = rt.go();
    } catch (UtilException e) {
      // Do nothing
    }
    if (result == null) {
      netFailed = true;
      return 0;
    } else
      return result.longValue();
  }

  /**
   * Main method
   * 
   * @param args command line args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    System.out.printf(
        "        GMT Time\nMillis: %d\n   J2K: %f\n    EW: %f\n  Date: %s\nOffset: %d\n",
        CurrentTime.getInstance().now(), CurrentTime.getInstance().nowJ2K(),
        CurrentTime.getInstance().nowEW(), CurrentTime.getInstance().nowString(),
        CurrentTime.getInstance().getLastOffset());
  }

  private static class CurrentTimeHolder {
    public static CurrentTime currentTime = new CurrentTime();
  }
}
