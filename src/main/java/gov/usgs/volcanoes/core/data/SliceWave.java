package gov.usgs.volcanoes.core.data;

import gov.usgs.volcanoes.core.time.J2kSec;

/**
 * Represents slice - continuous part of wave time series. Contain computed and cached statistics
 * about wave series zone.
 * 
 * @author Dan Cervelli
 */
public class SliceWave {
  private Wave source;

  private int position;
  private int limit;
  private int readPosition;

  // These values are cached to improved performance
  private transient double mean = Double.NaN;
  private transient double rsam = Double.NaN;
  private transient double max = -1E300;
  private transient double min = 1E300;
  private transient double b = Double.NaN;
  private transient double m = Double.NaN;
  private transient double[] dataRange = null;

  /**
   * Constructor.
   * 
   * @param sw Time series
   */
  public SliceWave(Wave sw) {
    source = sw;
    position = 0;
    limit = source.buffer.length;
  }

  /**
   * Getter for time series.
   * 
   * @return wave
   */
  public Wave getWave() {
    return source;
  }

  /**
   * Get data range - max and min slice data limits.
   * 
   * @return max and min slice data limits
   */
  public double[] getDataRange() {
    if (dataRange == null) {
      deriveStatistics();
    }

    return dataRange;
  }

  /**
   * Get samples count in represented slice.
   * 
   * @return samples count
   */
  public int samples() {
    return limit - position;
  }

  /**
   * Get Nyquist frequency.
   * 
   * @return Nyquist frequency
   */
  public double getNyquist() {
    return source.getNyquist();
  }

  /**
   * Get sample rate.
   * 
   * @return sample rate
   */
  public double getSamplingRate() {
    return source.getSamplingRate();
  }

  /**
   * Get slice start time.
   * 
   * @return start time
   */
  public double getStartTime() {
    return source.getStartTime() + position * (1 / getSamplingRate());
  }

  /**
   * Get slice end time.
   * 
   * @return end time
   */
  public double getEndTime() {
    return source.getStartTime() + limit * (1 / getSamplingRate());
  }

  /**
   * Get wave series end time.
   * 
   * @return end time
   */
  private double getTrueEndTime() {
    return getStartTime() + source.buffer.length * (1 / getSamplingRate());
  }

  /**
   * Set time limits to define slice zone, compute statistics.
   * 
   * @param t1 start time
   * @param t2 end time
   */
  public void setSlice(double t1, double t2) {
    if (t1 < source.getStartTime() || t2 > getTrueEndTime() || t1 >= t2) {
      return;
    }

    invalidateStatistics();

    position = (int) Math.round((t1 - source.getStartTime()) * getSamplingRate());
    limit = position + (int) Math.round((t2 - t1) * getSamplingRate());

    if (limit > source.buffer.length) {
      limit = source.buffer.length;
    }
  }

  /**
   * Invalidates the cached statistics.
   */
  public void invalidateStatistics() {
    mean = Double.NaN;
    rsam = Double.NaN;
    max = -1E300;
    min = 1E300;
  }

  /**
   * Compute slice statistics.
   */
  private void deriveStatistics() {
    if (source.buffer == null || source.buffer.length == 0) {
      mean = 0;
      rsam = 0;
      max = 0;
      min = 0;
      return;
    }
    int noDatas = 0;
    long sum = 0;
    long rs = 0;
    for (int i = position; i < limit; i++) {
      int d = source.buffer[i];
      if (d != Wave.NO_DATA) {
        sum += d;
        rs += Math.abs(d);
        min = Math.min(min, d);
        max = Math.max(max, d);
      } else {
        noDatas++;
      }
    }

    mean = (double) sum / (double) (samples() - noDatas);
    rsam = (double) rs / (double) (samples() - noDatas);
    dataRange = new double[] {min, max};

    double xm = (limit - position + 1) / 2;
    double ssxx = 0;
    double ssxy = 0;
    for (int i = position; i < limit; i++) {
      if (source.buffer[i] == Wave.NO_DATA) {
        continue;
      }
      ssxy += (i - xm) * (source.buffer[i] - mean);
      ssxx += (i - xm) * (i - xm);
    }
    m = ssxy / ssxx;
    b = mean - m * xm;
  }

  /**
   * Gets the mean or bias of the samples. Ignores NO_DATA samples.
   * 
   * @return the mean or bias
   */
  public double mean() {
    if (Double.isNaN(mean)) {
      deriveStatistics();
    }

    return mean;
  }

  /**
   * Gets the maximum value of the samples. Ignores NO_DATA samples.
   * 
   * @return the maximum value
   */
  public double max() {
    if (max == -1E300) {
      deriveStatistics();
    }

    return max;
  }

  /**
   * Gets the minimum value of the samples. Ignores NO_DATA samples.
   * 
   * @return the minimum value
   */
  public double min() {
    if (min == 1E300) {
      deriveStatistics();
    }

    return min;
  }

  /**
   * Gets the RSAM of the samples. Ignores NO_DATA samples.
   * 
   * @return the RSAM value
   */
  public double rsam() {
    if (Double.isNaN(rsam)) {
      deriveStatistics();
    }

    return rsam;
  }

  /**
   * Determines if all of the samples are NO_DATA samples.
   * 
   * @return whether or not this consists entirely of NO_DATA samples
   */
  public boolean isData() {
    for (int i = 0; i < source.buffer.length; i++) {
      if (source.buffer[i] != Wave.NO_DATA) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns an array containing the signal from the slice starting position to the slice ending
   * position. Detrend data before returning.
   * 
   * @return the signal array
   */
  public double[] getSignal() {

    double[] signal;

    mean(); // required to derive statistics if it is not already done

    int nsamples = samples();

    signal = new double[nsamples];

    reset();
    for (int i = 0; i < nsamples; i++) {
      signal[i] = next();
      if (signal[i] == Wave.NO_DATA) {
        signal[i] = 0;
      } else {
        signal[i] -= (i + 1) * m + b;
      }
    }

    return signal;

  }

  /**
   * Set read pointer in the slice starting position.
   */
  public void reset() {
    readPosition = position;
  }

  /**
   * Check if read pointer in the slice zone.
   * 
   * @return true if more to read
   */
  public boolean hasNext() {
    return readPosition < limit;
  }

  /**
   * Move read pointer in the next position.
   * 
   * @return data value in the read pointer position before moving
   */
  public double next() {
    if (hasNext()) {
      return source.buffer[readPosition++];
    } else {
      return Double.NaN;
    }
  }

  @Deprecated
  public String toCSV() {
    return this.toCsv();
  }

  /**
   * Dump slice content to CSV string.
   * 
   * @return CSV string
   */
  public String toCsv() {
    if (source.buffer == null || source.buffer.length == 0) {
      return "";
    }

    StringBuffer sb = new StringBuffer();
    double time = getStartTime();

    for (int i = position; i < limit; i++) {
      sb.append(J2kSec.toDateString(time) + ',' + source.buffer[i] + '\n');
      time += (1 / getSamplingRate());
    }

    return sb.toString();
  }

}
