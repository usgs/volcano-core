package gov.usgs.volcanoes.core.time;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TimeSpan {
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
}
