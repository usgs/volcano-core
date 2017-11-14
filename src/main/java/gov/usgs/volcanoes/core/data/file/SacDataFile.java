package gov.usgs.volcanoes.core.data.file;

import gov.usgs.volcanoes.core.data.Wave;
import gov.usgs.volcanoes.core.time.J2kSec;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import edu.sc.seis.seisFile.sac.SacHeader;
import edu.sc.seis.seisFile.sac.SacTimeSeries;

/**
 * A concrete SeismicDataFile class for SAC files.
 * 
 * @author Tom Parker
 */
public class SacDataFile extends SeismicDataFile {

  private SacTimeSeries sac;
  private SacHeader header;

  protected SacDataFile(String fileName) {
    super(fileName, "SAC^");
  }

  /**
   * @see gov.usgs.plot.data.file.SeismicDataFile#read()
   */
  public void read() throws IOException {
    sac = new SacTimeSeries(fileName);
    header = sac.getHeader();
    Wave sw = new Wave();
    sw.setStartTime(J2kSec.fromDate(getStartTime()));
    sw.setSamplingRate(getSamplingRate());
    sw.buffer = new int[sac.getY().length];
    for (int i = 0; i < sac.getY().length; i++) {
      sw.buffer[i] = Math.round(sac.getY()[i]);
    }
    String channel = header.getKstnm().trim() + "$" + header.getKcmpnm().trim() + "$"
        + header.getKnetwk().trim();

    String loc = header.getKhole().trim();
    if (!(loc == null || loc.equals("  ") || loc.equals("--"))) {
      channel += "$" + loc;
    }
    waves.put(channel, sw);
  }

  private double getSamplingRate() {
    return 1 / header.getDelta();
  }

  private Date getStartTime() {
    if (sac == null) {
      return null;
    }
    String ds = header.getNzyear() + "," + header.getNzjday() + "," + header.getNzhour() + ","
        + header.getNzmin() + "," + header.getNzsec() + "," + header.getNzmsec();
    SimpleDateFormat format = new SimpleDateFormat("yyyy,DDD,HH,mm,ss,SSS");
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    Date d = null;
    try {
      d = format.parse(ds);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return d;

  }

  /**
   * @see gov.usgs.plot.data.file.SeismicDataFile#write()
   */
  public void write() throws FileNotFoundException, IOException {
    Wave wave = waves.values().iterator().next();
    float[] y = new float[wave.buffer.length];
    for (int i = 0; i < y.length; i++) {
      y[i] = wave.buffer[i];
    }

    SacTimeSeries sac = new SacTimeSeries(getSacHeader(), y);
    sac.write(fileName);
  }

  private SacHeader getSacHeader() {
    SacHeader header = new SacHeader();
    String channel = waves.keySet().iterator().next();

    String[] channelCmp = channel.split("[\\s\\$]");
    String s = channelCmp.length > 0 ? channelCmp[0] : "";
    String c = channelCmp.length > 1 ? channelCmp[1] : "";
    String n = channelCmp.length > 2 ? channelCmp[2] : "";
    String l = channelCmp.length > 3 ? channelCmp[3] : "";

    header.setKstnm(s);
    header.setKcmpnm(c);
    header.setKnetwk(n);
    header.setKhole(l);

    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
    Wave wave = waves.get(channel);
    cal.setTime(J2kSec.asDate(wave.getStartTime()));
    header.setNzyear(cal.get(Calendar.YEAR));
    header.setNzjday(cal.get(Calendar.DAY_OF_YEAR));
    header.setNzhour(cal.get(Calendar.HOUR_OF_DAY));
    header.setNzmin(cal.get(Calendar.MINUTE));
    header.setNzsec(cal.get(Calendar.SECOND));
    header.setNzmsec(cal.get(Calendar.MILLISECOND));

    header.setDelta((float) wave.getSamplingPeriod());
    header.setNpts(wave.numSamples());
    return header;
  }
}
