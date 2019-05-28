package gov.usgs.volcanoes.core.data.file;

import gov.usgs.volcanoes.core.data.Wave;
import gov.usgs.volcanoes.core.quakeml.Pick;
import gov.usgs.volcanoes.core.quakeml.Pick.Onset;
import gov.usgs.volcanoes.core.quakeml.Pick.Polarity;
import gov.usgs.volcanoes.core.time.J2kSec;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
   * Read SAC file.
   * @see gov.usgs.plot.data.file.SeismicDataFile#read()
   */
  public void read() throws IOException {
    sac = new SacTimeSeries(fileName);
    header = sac.getHeader();

    // get wave data
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

    // get pick data
    double refTime = getStartTime().getTime();
    for (int i = 0; i <= 9; i++) {
      // T, KT
      long milliseconds = (long) (refTime + header.getTHeader(i) * 1000);
      String tag = header.getKTHeader(i);

      Pick pick = new Pick("", milliseconds, channel);
      switch (tag.substring(0, 1).toUpperCase()) {
        case "I":
          pick.setOnset(Onset.IMPULSIVE);
          break;
        case "E":
          pick.setOnset(Onset.EMERGENT);
          break;
        default:
          pick.setOnset(Onset.QUESTIONABLE);
          break;
      }
      pick.setPhaseHint(tag.substring(1, 2));
      switch (tag.substring(2, 3).toUpperCase()) {
        case "+":
        case "U":
          pick.setPolarity(Polarity.POSITIVE);
          break;
        case "-":
        case "D":
          pick.setPolarity(Polarity.NEGATIVE);
          break;
        default:
          pick.setPolarity(Polarity.UNDECIDABLE);
          break;
      }
      putPick(channel, pick);
    }
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
   * Write SAC file.
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
    double refTime = J2kSec.asDate(wave.getStartTime()).getTime();
    cal.setTime(J2kSec.asDate(wave.getStartTime()));
    header.setNzyear(cal.get(Calendar.YEAR));
    header.setNzjday(cal.get(Calendar.DAY_OF_YEAR));
    header.setNzhour(cal.get(Calendar.HOUR_OF_DAY));
    header.setNzmin(cal.get(Calendar.MINUTE));
    header.setNzsec(cal.get(Calendar.SECOND));
    header.setNzmsec(cal.get(Calendar.MILLISECOND));

    header.setDelta((float) wave.getSamplingPeriod());
    header.setNpts(wave.numSamples());

    // picks
    ArrayList<Pick> pickList = picks.get(channel);
    if (pickList == null) {
      return header;
    }
    int i = 0;
    double mintime = Double.MAX_VALUE;
    for (Pick p : pickList) {
      // user defined time pick or marker (seconds relative to reference time)
      float seconds = (float) (p.getTime() - refTime) / 1000;
      // A, KA
      if (seconds < mintime) {
        header.setA((float) seconds);
        header.setKa(p.getTag());
        mintime = seconds;
      }
      // T, KT
      header.setTHeader(i, seconds);
      header.setKtHeader(i, p.getTag().toUpperCase());

      // only 0-9
      i++;
      if (i > 9) { // can only store up to 9 picks
        break;
      }
    }
    return header;
  }
}
