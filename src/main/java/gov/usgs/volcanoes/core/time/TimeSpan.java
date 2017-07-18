package gov.usgs.volcanoes.core.time;

public class TimeSpan {
  public final long startTime;
  public final long endTime;

  public TimeSpan(final long startTime, final long endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  @Override
  public String toString() {
    return Time.toDateString(startTime) + " to " + Time.toDateString(endTime);
  }
}
