package gov.usgs.volcanoes.core.legacy.plot.decorate;

import gov.usgs.volcanoes.core.math.Util;
import gov.usgs.volcanoes.core.time.J2kSec;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>
 * This class provides a set of functions for creating intelligent automatic
 * time and data tickmarks for plots.
 * </p>
 * 
 * @author Dan Cervelli
 */
public class SmartTick {

  /**
   * Shortcut for autoTick(min, max, ticks, expand, false).
   * 
   * @param min the minimum
   * @param max the maximum
   * @param ticks the desired number of ticks
   * @param expand whether or not to expand the range for more "pleasant"
   * @return an array containing the values of the ticks
   */
  public static double[] autoTick(double min, double max, int ticks, boolean expand) {
    return autoTick(min, max, ticks, expand, false);
  }

  /**
   * Generates ticks over an arbitrary range. The algorithm computes
   * stepMult.length sets of ticks and chooses the one with the number of
   * ticks closest to what is requested. If you specify the expand property
   * the ticks returned will be slightly smaller and larger than the minimum
   * and maximum specified, otherwise the ticks will be inclusive to min and
   * max.
   * 
   * @param min the minimum axis value
   * @param max the maximum axis value
   * @param tickCountRequested the desired number of ticks
   * @param expand whether or not to expand the range for more "pleasant" ticks
   * @param clip clip the last ticks to the min/max values
   * @return an array containing the values of the ticks
   */
  public static double[] autoTick(double min, double max, int tickCountRequested, boolean expand,
      boolean clip) {

    double spanExp = Util.getExp(max - min);
    double[] stepMult = new double[] {0.1, 0.2, 0.5, 1.0, 2.0, 5.0};

    int[] numTicks = new int[stepMult.length];
    double[] minTickValues = new double[stepMult.length];
    double[] maxTickValues = new double[stepMult.length];
    double[] steps = new double[stepMult.length];

    // choose possible tick counts
    for (int i = 0; i < stepMult.length; i++) {
      steps[i] = stepMult[i] * Math.pow(10, spanExp);

      double step = steps[i];
      double minTickValue;
      double maxTickValue;

      if (expand) {
        minTickValue = Math.floor(min / step) * step;
        maxTickValue = Math.ceil(max / step) * step;
      } else {
        minTickValue = Math.ceil(min / step) * step;
        maxTickValue = Math.floor(max / step) * step;
      }

      minTickValues[i] = minTickValue;
      maxTickValues[i] = maxTickValue;
      numTicks[i] = (int) (Math.round((maxTickValue - minTickValue) / step)) + 1;
    }

    // find number of ticks to return. Pick the value nearest requested
    // value
    int minDeltaTicks = Integer.MAX_VALUE;
    int minDeltaTicksIndex = -1;
    for (int i = 0; i < numTicks.length; i++) {
      int deltaTicks = Math.abs(tickCountRequested - numTicks[i]);
      if (deltaTicks < minDeltaTicks) {
        minDeltaTicksIndex = i;
        minDeltaTicks = deltaTicks;
      }
    }

    if (minDeltaTicksIndex == -1) {
      return null;
    }

    int tickIndex = minDeltaTicksIndex;

    // calculate tick values
    double[] result = new double[numTicks[minDeltaTicksIndex]];
    for (int i = 0; i < numTicks[tickIndex]; i++) {
      result[i] = minTickValues[tickIndex] + i * steps[tickIndex];
      if (clip && result[i] < min) {
        result[i] = min;
      }
      if (clip && result[i] > max) {
        result[i] = max;
      }
    }

    return result;
  }

  /**
   * Generate array of tick values.
   * 
   * @param min min interval malue
   * @param max max interval value
   * @param ticks ticks count
   */
  public static double[] intervalTick(double min, double max, int ticks) {
    double interval = (max - min) / (double) ticks;
    double[] result = new double[ticks];
    for (int i = 0; i < ticks; i++) {
      result[i] = min + interval * (i + 1);
    }

    return result;
  }

  /**
   * Generates time ticks over a specified time interval. The ticks parameter
   * is a suggestion: it is not guaranteed to produce that many ticks.
   * 
   * @param ts
   *            the start time as a Date
   * @param te
   *            the end time as a Date
   * @param ticks
   *            the desired number of ticks
   * @return the dates (in j2ksecs) of the ticks
   */
  public static Object[] autoTimeTick(Date ts, Date te, int ticks) {
    return autoTimeTick(J2kSec.fromDate(ts), J2kSec.fromDate(te), ticks);
  }

  /**
   * Generates time ticks over a specified time interval. The ticks parameter
   * is a suggestion: it is not guaranteed to produce that many ticks.
   * 
   * @param ts the start time as a j2ksec
   * @param te the end time as a j2ksec
   * @param ticks the desired number of ticks
   * @return the dates (in j2ksecs) of the ticks
   */
  public static Object[] autoTimeTick(double ts, double te, int ticks) {
    int ti = -1;
    long mindt = Integer.MAX_VALUE;
    for (int i = 0; i < tickers.length; i++) {
      if (Math.abs(tickers[i].numTicks(ts, te) - ticks) < mindt) {
        ti = i;
        mindt = Math.abs(tickers[i].numTicks(ts, te) - ticks);
      }
    }
    return tickers[ti].getTicks(ts, te);
  }

  /**
   * Helper class to create time ticks.
   */
  private abstract static class TimeTicker {
    protected double interval;
    protected SimpleDateFormat dateFormat;
    protected String labelFormatString;

    /**
     * Constructor w/ interval i.
     * 
     * @param i interval
     */
    public TimeTicker(double i) {
      interval = i;
    }

    /**
     * Set label & date format.
     * 
     * @param s label format
     */
    public void setLabelFormatString(String s) {
      labelFormatString = s;
      dateFormat = new SimpleDateFormat(labelFormatString);
      dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Yield number of ticks from ts to te.
     * 
     * @param ts start value
     * @param ts end value
     * @return number of ticks
     */
    public long numTicks(double ts, double te) {
      return (Math.round((getMaxTick(te) - getMinTick(ts)) / interval)) + 1;
    }

    /**
     * Yield minimum tick for ts.
     * 
     * @param ts start value
     * @return minimum tick
     */
    public double getMinTick(double ts) {
      return Math.ceil(ts / interval) * interval;
    }

    /**
     * Yield maximum tick for ts.
     * 
     * @param te end value
     * @return maximum tick
     */
    public double getMaxTick(double te) {
      return Math.floor(te / interval) * interval;
    }

    /**
     * Yield array of ticks & labels for range ts..te.
     * 
     * @param ts start value
     * @param ts end value
     * @return array with 2 elements: list of ticks, and list of labels
     */
    public Object[] getTicks(double ts, double te) {
      double minTick = getMinTick(ts);
      int nt = (int) numTicks(ts, te);
      double[] ticks = new double[nt];
      String[] labels = new String[nt];
      for (int i = 0; i < nt; i++) {
        ticks[i] = minTick + i * interval;
        labels[i] = dateFormat.format(J2kSec.asDate(ticks[i]));
      }
      return new Object[] {ticks, labels};
    }

    /**
     * Yield string representation of this TimeTicker.
     * 
     * @return string representation
     */
    public String toString() {
      return this.getClass().getName() + ", interval: " + interval;
    }
  }

  /**
   * Helper class to create year ticks.
   */
  private static class YearTicker extends TimeTicker {
    private int years;
    private Calendar cal;

    /**
     * Constructor for year y.
     * 
     * @param y year
     */
    public YearTicker(int y) {
      super(y * 365.24 * 24 * 60 * 60);
      years = y;
      setLabelFormatString("yyyy");
    }

    /**
     * Yield minimum tick for ts.
     * 
     * @param ts start value
     * @return minimum tick
     */
    public synchronized double getMinTick(double ts) {
      Date d = J2kSec.asDate(ts);
      cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      cal.setTime(d);
      cal.set(Calendar.MONTH, 0);
      cal.set(Calendar.DAY_OF_MONTH, 1);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      cal.add(Calendar.YEAR, 1);
      return J2kSec.fromDate(cal.getTime());
    }

    /**
     * Yield number of ticks from ts to te.
     * 
     * TODO: is there a synchronization problem here? cal.getTime()
     * sometimes throws an ArrayIndexOutOfBoundsException.
     * 
     * @param ts start value
     * @param ts end value
     * @return number of ticks
     */
    public long numTicks(double ts, double te) {
      double minTick = getMinTick(ts);
      int numTicks = 0;

      synchronized (this) {
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(J2kSec.asDate(minTick));
        while (J2kSec.fromDate(cal.getTime()) < te) {
          cal.add(Calendar.YEAR, years);
          numTicks++;
        }
      }
      return numTicks;
    }

    /**
     * Yield array of ticks & labels for range ts..te
     * 
     * @param ts
     *            start value
     * @param ts
     *            end value
     * @return array with 2 elements: list of ticks, and list of labels
     */
    public Object[] getTicks(double ts, double te) {
      int nt = (int) numTicks(ts, te);
      double[] ticks = new double[nt];
      String[] labels = new String[nt];
      double minTick = getMinTick(ts);

      synchronized (this) {
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(J2kSec.asDate(minTick));
        for (int i = 0; i < nt; i++) {
          ticks[i] = J2kSec.fromDate(cal.getTime());
          labels[i] = dateFormat.format(cal.getTime());
          cal.add(Calendar.YEAR, years);
        }
      }
      return new Object[] {ticks, labels};
    }
  }

  /**
   * Helper class to create month ticks.
   */
  private static class MonthTicker extends TimeTicker {
    private int months;
    private Calendar cal;

    /**
     * Constructor for month m.
     * 
     * @param m month
     */
    public MonthTicker(int m) {
      super(m * 30 * 24 * 60 * 60);
      months = m;
      setLabelFormatString("MMM-yyyy");
    }

    /**
     * Yield minimum tick for ts.
     * 
     * @param ts start value
     * @return minimum tick
     */
    public synchronized double getMinTick(double ts) {
      Date d = J2kSec.asDate(ts);
      cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      cal.setTime(d);
      cal.set(Calendar.DAY_OF_MONTH, 1);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      cal.add(Calendar.MONTH, 1);
      return J2kSec.fromDate(cal.getTime());
    }

    /**
     * Yield number of ticks from ts to te.
     * 
     * @param ts start value
     * @param ts end value
     * @return number of ticks
     */
    public long numTicks(double ts, double te) {
      double minTick = getMinTick(ts);

      int numTicks = 0;
      synchronized (this) {
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(J2kSec.asDate(minTick));
        while (J2kSec.fromDate(cal.getTime()) < te) {
          cal.add(Calendar.MONTH, months);
          numTicks++;
        }
      }
      return numTicks;
    }

    /**
     * Yield array of ticks & labels for range ts..te
     * 
     * @param ts
     *            start value
     * @param ts
     *            end value
     * @return array with 2 elements: list of ticks, and list of labels
     */
    public Object[] getTicks(double ts, double te) {
      int nt = (int) numTicks(ts, te);
      double[] ticks = new double[nt];
      String[] labels = new String[nt];
      double minTick = getMinTick(ts);

      synchronized (this) {
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(J2kSec.asDate(minTick));
        for (int i = 0; i < nt; i++) {
          ticks[i] = J2kSec.fromDate(cal.getTime());
          labels[i] = dateFormat.format(cal.getTime());
          cal.add(Calendar.MONTH, months);
        }
      }
      return new Object[] {ticks, labels};
    }
  }

  /**
   * Helper class to create day ticks.
   */
  private static class DayTicker extends TimeTicker {
    private int days;
    private Calendar cal;

    /**
     * Constructor for day d.
     * 
     * @param y year
     */
    public DayTicker(int d) {
      super(d * 60 * 60 * 24);
      days = d;
      setLabelFormatString("MM/dd");
    }

    /**
     * Yield minimum tick for ts.
     * 
     * @param ts start value
     * @return minimum tick
     */
    public synchronized double getMinTick(double ts) {
      Date d = J2kSec.asDate(ts);
      cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      cal.setTime(d);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      cal.add(Calendar.DAY_OF_MONTH, 1);
      return J2kSec.fromDate(cal.getTime());
    }

    /**
     * Yield number of ticks from ts to te.
     * 
     * @param ts start value
     * @param ts end value
     * @return number of ticks
     */
    public long numTicks(double ts, double te) {
      double minTick = getMinTick(ts);

      int numTicks = 0;
      synchronized (this) {
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(J2kSec.asDate(minTick));
        while (J2kSec.fromDate(cal.getTime()) < te) {
          cal.add(Calendar.DAY_OF_YEAR, days);
          numTicks++;
        }
      }
      return numTicks;
    }

    /**
     * Yield array of ticks & labels for range ts..te
     * 
     * @param ts
     *            start value
     * @param ts
     *            end value
     * @return array with 2 elements: list of ticks, and list of labels
     */
    public Object[] getTicks(double ts, double te) {
      int nt = (int) numTicks(ts, te);
      double[] ticks = new double[nt];
      String[] labels = new String[nt];
      double minTick = getMinTick(ts);

      synchronized (this) {
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(J2kSec.asDate(minTick));
        for (int i = 0; i < nt; i++) {
          ticks[i] = J2kSec.fromDate(cal.getTime());
          labels[i] = dateFormat.format(cal.getTime());
          cal.add(Calendar.DAY_OF_YEAR, days);
        }
      }
      return new Object[] {ticks, labels};
    }

  }

  /**
   * Helper class to create hour ticks.
   */
  private static class HourTicker extends TimeTicker {
    /**
     * Constructor for hour h.
     * 
     * @param h hour
     */
    public HourTicker(int h) {
      super(h * 60 * 60);
      setLabelFormatString("HH:mm");
    }
  }

  /**
   * Helper class to create minute ticks.
   */
  private static class MinuteTicker extends TimeTicker {

    /**
     * Constructor for minute m.
     * 
     * @param m minute
     */
    public MinuteTicker(int m) {
      super(m * 60);
      setLabelFormatString("HH:mm");
    }
  }

  /**
   * Helper class to create seconds ticks.
   */
  private static class SecondTicker extends TimeTicker {
    /**
     * Constructor for second s.
     * 
     * @param s second
     */
    public SecondTicker(int s) {
      super(s);
      setLabelFormatString("HH:mm:ss");
    }
  }

  /**
   * Helper class to create milliseconds ticks.
   */
  private static class MillisecondTicker extends TimeTicker {
    /**
     * Constructor for millisecond m.
     * 
     * @param m millisecond
     */
    public MillisecondTicker(long m) {
      super((double) m / 1000d);
      setLabelFormatString("HH:mm:ss.SSS");
    }
  }

  /**
   * Set lable format string for all Time Tickers of class cal.
   * 
   * @param c class of tickers to set label format string of
   * @param s new label format String
   */
  private static void setLabelFormatString(Class<?> c, String s) {
    for (TimeTicker t : tickers) {
      if (t.getClass().equals(c)) {
        t.setLabelFormatString(s);
      }
    }
  }

  /**
   * Set lable format string for all Month Tickers.
   * 
   * @param s new label format String
   */
  public static void setMonthTickerLabelFormatString(String s) {
    setLabelFormatString(MonthTicker.class, s);
  }

  /** Standard time tickers. */
  private static final TimeTicker[] tickers =
      new TimeTicker[] {new YearTicker(10), new YearTicker(5), new YearTicker(4), new YearTicker(3),
          new YearTicker(2), new YearTicker(1), new MonthTicker(6), new MonthTicker(4),
          new MonthTicker(3), new MonthTicker(2), new MonthTicker(1), new DayTicker(20),
          new DayTicker(10), new DayTicker(7), new DayTicker(3), new DayTicker(2), new DayTicker(1),
          new HourTicker(12), new HourTicker(6), new HourTicker(4), new HourTicker(3),
          new HourTicker(2), new HourTicker(1), new MinuteTicker(30), new MinuteTicker(15),
          new MinuteTicker(10), new MinuteTicker(6), new MinuteTicker(5), new MinuteTicker(4),
          new MinuteTicker(3), new MinuteTicker(2), new MinuteTicker(1), new SecondTicker(30),
          new SecondTicker(15), new SecondTicker(10), new SecondTicker(6), new SecondTicker(5),
          new SecondTicker(4), new SecondTicker(3), new SecondTicker(2), new SecondTicker(1),
          new MillisecondTicker(500), new MillisecondTicker(200), new MillisecondTicker(100),
          new MillisecondTicker(50), new MillisecondTicker(20), new MillisecondTicker(10),
          new MillisecondTicker(5), new MillisecondTicker(2), new MillisecondTicker(1)};
}
