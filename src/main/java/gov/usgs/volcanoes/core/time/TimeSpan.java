package gov.usgs.volcanoes.core.time;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class TimeSpan implements Comparable<TimeSpan> {
  public final long startTime;
  public final long endTime;

  public static final int SECOND = 1000;
  public static final int MINUTE = SECOND * 60;
  public static final int HOUR = MINUTE * 60;
  public static final int DAY = HOUR * 24;
  private static final NumberFormat secFormatter = new DecimalFormat("#0.###");

  public TimeSpan(final long startTime, final long endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public static TimeSpan fromJ2kSec(double start, double end) {
    return new TimeSpan(J2kSec.asEpoch(start), J2kSec.asEpoch(end));
  }

  @Override
  public String toString() {
    return String.format("%s to %s", Time.toDateString(startTime), Time.toDateString(endTime));
  }

  public String toShortString() {
    return String.format("%s to %s", Time.toDateString(startTime), Time.toDateString(endTime));
  }

  /**
   * Return span in short string form.
   * 
   * @return human readable time span
   */
  public String span() {
    long span = endTime - startTime;

    long days = span / DAY;
    span -= days * DAY;

    long hours = span / HOUR;
    span -= hours * HOUR;

    long minutes = span / MINUTE;
    span -= minutes * MINUTE;

    final double seconds = (double) span / 1000;

    StringBuilder sb = new StringBuilder();
    if (days > 0) {
      sb.append(days).append("d ");
    }
    if (hours > 0) {
      sb.append(hours).append("h ");
    }
    if (minutes > 0) {
      sb.append(minutes).append("m ");
    }
    if (seconds > 0 || sb.length() == 0) {
      sb.append(secFormatter.format(seconds)).append("s ");
    }
    sb.deleteCharAt(sb.length() - 1);

    return sb.toString();
  }


  /**
   * Parse a TimeSpan from a string, maybe containing relative times.
   * 
   * @param timeRange "yyyyMMddHHmmss" format or relative time, divided by
   *          comma.
   *
   * @return array of two doubles - start and end J2K dates
   * @throws ParseException when the string looks odd
   */
  public static TimeSpan parse(String timeRange) throws ParseException {
    if (timeRange == null || timeRange.equals("")) {
      throw new ParseException("Time range is null.", -1);
    }

    final String[] ss = timeRange.split(",");
    long endTime;
    if (ss.length > 1) {
      endTime = TimeSpan.parseTime(ss[1], CurrentTime.getInstance().now());
    } else {
      endTime = CurrentTime.getInstance().now();
    }
    long startTime = TimeSpan.parseTime(ss[0], endTime);

    return new TimeSpan(startTime, endTime);
  }


  private static long parseTime(String timeStr, long base) throws ParseException {
    double offset = Time.getRelativeTime(timeStr);
    if (Double.isNaN(offset)) {
      return Time.getFormat(Time.INPUT_TIME_FORMAT).parse(timeStr).getTime();
    } else {
      return base - (long) (offset * 1000);
    }
  }


  /**
   * Compare this TimeSpan to another based on startTime, or endTime if startTimes match.
   * 
   * @param other Object to compare to
   * @return compare result
   */
  @Override
  public int compareTo(TimeSpan other) {
    "test".compareTo("test");
    int start = Long.compare(startTime, other.startTime);
    if (start != 0) {
      return start;
    } else {
      return Long.compare(endTime, other.endTime);
    }
  }
}
