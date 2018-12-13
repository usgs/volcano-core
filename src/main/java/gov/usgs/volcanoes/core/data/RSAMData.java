package gov.usgs.volcanoes.core.data;

import gov.usgs.volcanoes.core.math.BinSize;
import gov.usgs.volcanoes.core.time.J2kSec;
import gov.usgs.volcanoes.core.time.Time;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import hep.aida.IAxis;
import hep.aida.ref.FixedAxis;
import hep.aida.ref.Histogram1D;
import hep.aida.ref.VariableAxis;

/**
 * A class that deals with RSAM data.  The data are stored in a 2-D matrix, the
 * first column is the time (j2ksec), the second is the data.
 *
 * @author Dan Cervelli
 */
public class RSAMData extends GenericDataMatrix {

  protected static final int MAX_BINS = 1000000;
  protected DoubleMatrix2D events;
  protected int period = -1;

  /** 
   * Generic empty constructor.
   */
  public RSAMData() {
    columnMap.put("time", 0);
    columnMap.put("rsam", 0);
  }

  /**
   * Create an RSAMData from a byte buffer.  This first 4 bytes specify an
   * integer number of rows followed by rows*16 bytes, 2 doubles: j2ksec and 
   * RSAM.
   * 
   * @param bb the byte buffer
   */
  public RSAMData(ByteBuffer bb) {
    super(bb);
  }

  /**
   * Create an RSAMData from a byte buffer.  This first 4 bytes specify an
   * integer number of rows followed by rows*16 bytes, 2 doubles: j2ksec and 
   * RSAM.
   * 
   * @param bb the byte buffer
   * @param period the RSAM period
   */
  public RSAMData(ByteBuffer bb, int period) {
    super(bb);
    this.period = period;
  }

  /**
   * Constructor.
   * @param list list of matrix rows
   */
  public RSAMData(List<double[]> list) {
    super(list);
  }

  /** Gets the RSAM column (column 2) of the data. 
   * @return the data column
   */
  public DoubleMatrix2D getRSAM() {
    return data.viewPart(0, 1, rows(), 1);
  }

  /**
   * Get initialized axis to use with histogram graph.
   * @param bin histogram section period
   * @return initialized axis
   */
  protected IAxis getHistogramAxis(BinSize bin) {
    double startTime = getStartTime();
    double endTime = getEndTime();
    int bins = -1;
    IAxis axis = null;

    if (bin == BinSize.MINUTE) {
      startTime -= (startTime - 43200) % 60;
      endTime -= (endTime - 43200) % 60 - 60;
      bins = (int) (endTime - startTime) / 60;
      if (bins > MAX_BINS) {
        bin = BinSize.HOUR;
      } else {
        axis = new FixedAxis(bins, startTime, endTime);
      }
    }
    if (bin == BinSize.TENMINUTE) {
      startTime -= (startTime - 43200) % 600;
      endTime -= (endTime - 43200) % 600 - 600;
      bins = (int) (endTime - startTime) / 600;
      if (bins > MAX_BINS) {
        bin = BinSize.HOUR;
      } else {
        axis = new FixedAxis(bins, startTime, endTime);
      }
    }
    if (bin == BinSize.HOUR) {
      startTime -= (startTime - 43200) % 3600;
      endTime -= (endTime - 43200) % 3600 - 3600;
      bins = (int) (endTime - startTime) / 3600;
      if (bins > MAX_BINS) {
        bin = BinSize.DAY;
      } else {
        axis = new FixedAxis(bins, startTime, endTime);
      }
    }
    if (bin == BinSize.DAY) {
      startTime -= (startTime - 43200) % 86400;
      endTime -= (endTime - 43200) % 86400 - 86400;
      bins = (int) (endTime - startTime) / 86400;
      if (bins > MAX_BINS) {
        bin = BinSize.WEEK;
      } else {
        axis = new FixedAxis(bins, startTime, endTime);
      }
    }
    if (bin == BinSize.WEEK) {
      startTime -= (startTime - 43200) % 604800;
      endTime -= (endTime - 43200) % 604800 - 604800;
      bins = (int) (endTime - startTime) / 604800;
      if (bins > MAX_BINS) {
        bin = BinSize.MONTH;
      } else {
        axis = new FixedAxis(bins, startTime, endTime);
      }
    }
    if (bin == BinSize.MONTH) {
      Date ds = J2kSec.asDate(startTime);
      Date de = J2kSec.asDate(endTime);
      bins = Time.getMonthsBetween(ds, de) + 1;
      if (bins <= MAX_BINS) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(ds);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        double[] edges = new double[bins + 1];
        for (int i = 0; i < bins + 1; i++) {
          edges[i] = J2kSec.fromDate(cal.getTime());
          cal.add(Calendar.MONTH, 1);
        }
        axis = new VariableAxis(edges);
      } else {
        bin = BinSize.YEAR;
      }
    }
    if (bin == BinSize.YEAR) {
      Date ds = J2kSec.asDate(startTime);
      Date de = J2kSec.asDate(endTime);
      bins = Time.getYear(de) - Time.getYear(ds) + 1;
      double[] edges = new double[bins + 1];
      Calendar cal = Calendar.getInstance();
      cal.setTime(ds);
      cal.set(Calendar.MONTH, 1);
      cal.set(Calendar.DAY_OF_MONTH, 1);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      for (int i = 0; i < bins + 1; i++) {
        edges[i] = J2kSec.fromDate(cal.getTime());
        cal.add(Calendar.YEAR, 1);
      }
      axis = new VariableAxis(edges);
    }
    return axis;
  }

  /**
   * Loops for data matrix, scans columns and fills events matrix.
   * @param threshold event treshold
   * @param ratio minimum ratio between new and older value to define event
   * @param maxLength maximum event length (ms)
   */
  public void countEvents(double threshold, double ratio, double maxLength) {
    double oldValue = 0;
    double olderValue = 0;
    boolean eventOngoing = false;
    double eventStart = 0;
    int eventCount = 0;
    double[] eventTimes = new double[data.rows()];

    for (int i = 1; i < data.rows(); i++) {
      double currentTime = data.get(i, 0);
      double currentValue = data.get(i, 1);

      if (currentValue >= threshold && currentValue >= olderValue * ratio) {
        if (currentTime - eventStart > maxLength) {
          eventOngoing = false;
          eventStart = currentTime;
        } else if (eventOngoing == false) {
          eventStart = currentTime;
          eventOngoing = true;
          eventTimes[eventCount++] = currentTime;
        }
      } else {
        eventOngoing = false;
        eventStart = currentTime;
      }

      olderValue = oldValue;
      oldValue = currentValue;
    }

    if (data.rows() == 1 && Double.isNaN(data.getQuick(0, 0))) {
      events = DoubleFactory2D.dense.make(0, 2);

    } else {

      events = DoubleFactory2D.dense.make(eventCount + 2, 2);
      events.setQuick(0, 0, data.get(0, 0));
      events.setQuick(0, 1, 0);

      int c = 1;
      for (int i = 1; i <= eventCount; i++) {
        events.setQuick(i, 0, eventTimes[i - 1]);
        events.setQuick(i, 1, c++);
      }

      events.setQuick(eventCount + 1, 0, data.get(data.rows() - 1, 0));
      events.setQuick(eventCount + 1, 1, eventCount);
    }
  }

  /**
   * Get ratio of this data value and given RSAMData data value
   * on the time interval where they are intersecting.
   * @return RSAMData
   */
  public RSAMData getRatSAM(RSAMData d) {
    DoubleMatrix2D other = d.getData();
    ArrayList<double[]> ratList = new ArrayList<double[]>();

    // if either channels data is null, then make an empty list
    if ((data.rows() == 1 && Double.isNaN(data.getQuick(0, 0)))
        || (other.rows() == 1 && Double.isNaN(other.getQuick(0, 0)))) {
      double[] pt = new double[2];
      pt[0] = Double.NaN;
      pt[1] = Double.NaN;
      ratList.add(pt);

    } else {

      int i = 0;
      int j = 0;
      while (i < rows() && j < other.rows()) {
        double t1 = data.getQuick(i, 0);
        double t2 = other.getQuick(j, 0);
        if (t1 < t2) {
          i++;
        } else if (t1 > t2) {
          j++;
        } else {
          try {
            double[] pt = new double[2];
            pt[0] = t1;
            pt[1] = data.getQuick(i++, 1) / other.getQuick(j++, 1);
            ratList.add(pt);
          } catch (ArithmeticException e) {
            //
          }
        }
      }
    }

    return new RSAMData(ratList);
  }

  /**
   * Get cumulative event data by time interval.
   * @return matrix of event data
   */
  public DoubleMatrix2D getCumulativeCounts() {
    return events;
  }

  /**
   * Dump cumulative data as CSV string.
   * @return string representation in CSV
   */
  public String getCountsCSV() {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < events.rows(); i++) {
      sb.append(J2kSec.toDateString(events.getQuick(i, 0)) + ",");
      for (int j = 1; j < events.columns(); j++) {
        sb.append(events.getQuick(i, j));
        sb.append(",");
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Get initialized histogram of event count by time.
   * @param bin time interval
   * @return initialized histogram
   */
  public Histogram1D getCountsHistogram(BinSize bin) {
    if (data == null) {
      return null;
    }
    Histogram1D hist = new Histogram1D("", getHistogramAxis(bin));
    for (int i = 1; i < events.rows() - 1; i++) {
      hist.fill(events.get(i, 0));
    }

    return hist;
  }

  /**
   * Check for overlap.
   * @param rsamData RSAM data
   * @return true if RSAM data overlaps.
   */
  public boolean overlaps(RSAMData rsamData) {
    if (period != rsamData.period) {
      return false;
    }

    if (getEndTime() < rsamData.getStartTime()) {
      return false;
    }

    if (getStartTime() > rsamData.getEndTime()) {
      return false;
    }
    return true;
  }

  /**
   * Combine RSAM data.
   * @param rsam RSAM data
   * @return RSAM data
   */
  public RSAMData combine(RSAMData rsam) {
    // other wave dominates this wave
    if (getStartTime() >= rsam.getStartTime() && getEndTime() <= rsam.getEndTime()) {
      return rsam;
    }

    // this wave dominates other wave
    if (getStartTime() <= rsam.getStartTime() && getEndTime() >= rsam.getEndTime()) {
      return this;
    }

    // this wave is left of other wave
    if (getStartTime() <= rsam.getStartTime()) {
      // logger.fine("rows before: " + rows());
      DoubleMatrix2D[][] ms = new DoubleMatrix2D[2][1];
      ms[0][0] = data;
      int i = rsam.findClosestTimeIndexGreaterThan(getEndTime());
      ms[1][0] = rsam.getData().viewPart(i, 0, rsam.rows() - i, 2);
      data = DoubleFactory2D.dense.compose(ms);
      // System.out.println("combine l: " + data.rows() + " " + data.columns());
      return this;
    }

    // this wave is right of other wave
    if (rsam.getStartTime() <= getStartTime()) {
      // logger.fine("rows before: " + rows());
      DoubleMatrix2D[][] ms = new DoubleMatrix2D[2][1];
      ms[0][0] = rsam.getData();
      int i = findClosestTimeIndexLessThan(rsam.getEndTime());
      if (i == -1) {
        i = 0;
      }
      ms[1][0] = getData().viewPart(i, 0, rows() - i, 2);
      data = DoubleFactory2D.dense.compose(ms);
      // logger.fine("combine r: " + data.rows() + " " + data.columns());
      return this;
    }

    // logger.fine("unknown case");
    return null;
  }


  /**
   * Yield index of datum w/ smallest time >= time.
   * @param time lower bound of times to consider
   * @return index of time found; -1 if none found
   */
  public int findClosestTimeIndexGreaterThan(double time) {
    for (int i = 0; i < rows(); i++) {
      if (data.getQuick(i, 0) >= time) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Yield index of datum w/ largest time <= time.
   * @param time upper bound of times to consider
   * @return index of time found; -1 if none found
   */
  public int findClosestTimeIndexLessThan(double time) {
    for (int i = rows() - 1; i >= 0; i--) {
      if (data.getQuick(i, 0) <= time) {
        return i;
      }
    }
    return -1;
  }

  public int getPeriod() {
    return period;
  }


}
