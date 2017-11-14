package gov.usgs.volcanoes.core.math;

import gov.usgs.volcanoes.core.CodeTimer;

/**
 * A class for the Fast Fourier Transform (FFT) algorithm.  All of these 
 * functions take double[][] as arguments.  Each double[] is a complex 
 * number, element 0 being real, element 1 imaginary.
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/08/26 17:30:00  uid879
 * Initial avosouth commit.
 *
 * @author Dan Cervelli
 */
public class FFT {
  /** Log 10 shortcut.
   */
  public static final double LOG10 = Math.log(10);

  /** Pads a data array so that its size is a power of 2, a requirement of the
   * FFT.
   * @param array the data array
   * @return a new array that is the smallest power of 2 in length that holds 
   * all of the data
   */
  public static double[][] pad(double[][] array) {
    int n = array.length;
    int p2 = (int) Math.ceil(Math.log((double) n) / Math.log(2));
    int newSize = (int) Math.pow(2, p2);
    if (newSize == n)
      return array;
    double[][] result = new double[newSize][];
    for (int i = 0; i < n; i++)
      result[i] = array[i];
    for (int i = n; i < newSize; i++)
      result[i] = new double[] {0, 0};
    return result;
  }

  /** Throws out the second half of a data array, for use after performing the 
   * FFT.
   * @param array the original data
   * @return the first half of the data array
   */
  public static double[][] halve(double[][] array) {
    int n = array.length / 2;
    double[][] result = new double[n][];
    for (int i = 0; i < n; i++)
      result[i] = array[i];

    return result;
  }

  public static double[] fastHalve(double[] array) {
    int n = array.length / 2;
    double[] result = new double[n];
    System.arraycopy(array, 0, result, 0, n);
    return result;
  }

  /** Converts a complex array into a power/frequency array.  Element 0 
   * becomes the frequency, element 1 becomes the power of that frequency.
   * @param array the source array
   * @param samplingRate the data samplingRate
   * @param logPower whether or not to take the log of the power
   */
  public static void toPowerFreq(double[][] array, double samplingRate, boolean logPower) {
    double re, im, mag;
    for (int i = 0; i < array.length; i++) {
      re = array[i][0];
      im = array[i][1];

      mag = Math.sqrt(re * re + im * im);
      if (logPower)
        mag = Math.log(mag) / LOG10;

      array[i][0] = ((double) i / (double) array.length) * (samplingRate / 2);
      array[i][1] = mag;
    }
  }

  public static void fastToPowerFreq(double[] array, double samplingRate, boolean logPower,
      boolean logFreq) {
    double re, im, mag;
    double n = array.length / 4;
    for (int i = 0; i < n; i++) {
      re = array[i * 2];
      im = array[i * 2 + 1];

      mag = Math.sqrt(re * re + im * im);
      if (logPower)
        mag = Math.log(mag) / LOG10;

      array[i * 2] = ((double) i / (double) n) * (samplingRate / 2);
      if (logFreq)
        array[i * 2] = Math.log(array[i * 2]) / LOG10;
      array[i * 2 + 1] = mag;
    }
  }

  public static void fastToPowerFreq(double[] array, double samplingRate, boolean logPower) {
    double re, im, mag;
    double n = array.length / 4;
    for (int i = 0; i < n; i++) {
      re = array[i * 2];
      im = array[i * 2 + 1];

      mag = Math.sqrt(re * re + im * im);
      if (logPower)
        mag = Math.log(mag) / LOG10;

      array[i * 2] = ((double) i / (double) n) * (samplingRate / 2);
      array[i * 2 + 1] = mag;
    }
  }


  /** Converts a complex array into a power/frequency array.  Element 0 
   * becomes the frequency, element 1 becomes the power of that frequency.
   * @param array the source array
   * @param samplingRate the data samplingRate
   * @param logPower whether or not to take the log of the power
   * @param logFreq whether or not to take the log of the frequency
   */
  public static void toPowerFreq(double[][] array, double samplingRate, boolean logPower,
      boolean logFreq) {
    double re, im, mag;
    for (int i = 0; i < array.length; i++) {
      re = array[i][0];
      im = array[i][1];

      mag = Math.sqrt(re * re + im * im);
      if (logPower)
        mag = Math.log(mag) / LOG10;

      array[i][0] = ((double) i / (double) array.length) * (samplingRate / 2);
      if (logFreq)
        array[i][0] = Math.log(array[i][0]) / LOG10;
      array[i][1] = mag;
    }
  }


  public static void fft(double[] array) {
    double u_r, u_i, w_r, w_i, t_r, t_i;
    int ln, nv2, k, l, le, le1, j, ip, i, n, p, q;

    n = array.length / 2;
    ln = (int) (Math.log((double) n) / Math.log(2) + 0.5);
    nv2 = n / 2;
    j = 1;

    for (i = 1; i < n; i++) {
      if (i < j) {
        t_r = array[2 * (i - 1)];
        t_i = array[2 * (i - 1) + 1];

        array[2 * (i - 1)] = array[2 * (j - 1)];
        array[2 * (i - 1) + 1] = array[2 * (j - 1) + 1];

        array[2 * (j - 1)] = t_r;
        array[2 * (j - 1) + 1] = t_i;
      }
      k = nv2;
      while (k < j) {
        j = j - k;
        k = k / 2;
      }
      j = j + k;
    }

    for (l = 1; l <= ln; l++) /* loops thru stages */
    {
      le = (int) (Math.exp((double) l * Math.log(2)) + 0.5);
      le1 = le / 2;
      u_r = 1.0;
      u_i = 0.0;
      w_r = Math.cos(Math.PI / (double) le1);
      w_i = -Math.sin(Math.PI / (double) le1);
      for (j = 1; j <= le1; j++) /* loops thru 1/2 twiddle values per stage */
      {
        for (i = j, p = 2 * (i - 1); i <= n; i += le, p +=
            2 * le) /* loops thru points per 1/2 twiddle */
        {
          ip = i + le1;
          q = 2 * (ip - 1);

          t_r = array[q] * u_r - u_i * array[q + 1];
          t_i = array[q + 1] * u_r + u_i * array[q];

          array[q] = array[p] - t_r;
          array[q + 1] = array[p + 1] - t_i;

          array[p] = array[p] + t_r;
          array[p + 1] = array[p + 1] + t_i;
        }
        t_r = u_r * w_r - w_i * u_i;
        u_i = w_r * u_i + w_i * u_r;
        u_r = t_r;
      }
    }
  }

  /** Does the FFT on a data array, the array must be a power of 2 in length.
   * Put the source data in the real part of the array.  The FFT is performed
   * in place.
   * @param array the data
   */
  public static void fft(double[][] array) {
    double u_r, u_i, w_r, w_i, t_r, t_i;
    int ln, nv2, k, l, le, le1, j, ip, i, n;

    n = array.length;
    ln = (int) (Math.log((double) n) / Math.log(2) + 0.5);
    nv2 = n / 2;
    j = 1;
    for (i = 1; i < n; i++) {
      if (i < j) {
        t_r = array[i - 1][0];
        t_i = array[i - 1][1];
        array[i - 1][0] = array[j - 1][0];
        array[i - 1][1] = array[j - 1][1];
        array[j - 1][0] = t_r;
        array[j - 1][1] = t_i;
      }
      k = nv2;
      while (k < j) {
        j = j - k;
        k = k / 2;
      }
      j = j + k;
    }

    for (l = 1; l <= ln; l++) /* loops thru stages */
    {
      le = (int) (Math.exp((double) l * Math.log(2)) + 0.5);
      le1 = le / 2;
      u_r = 1.0;
      u_i = 0.0;
      w_r = Math.cos(Math.PI / (double) le1);
      w_i = -Math.sin(Math.PI / (double) le1);
      for (j = 1; j <= le1; j++) /* loops thru 1/2 twiddle values per stage */
      {
        for (i = j; i <= n; i += le) /* loops thru points per 1/2 twiddle */
        {
          ip = i + le1;
          t_r = array[ip - 1][0] * u_r - u_i * array[ip - 1][1];
          t_i = array[ip - 1][1] * u_r + u_i * array[ip - 1][0];

          array[ip - 1][0] = array[i - 1][0] - t_r;
          array[ip - 1][1] = array[i - 1][1] - t_i;

          array[i - 1][0] = array[i - 1][0] + t_r;
          array[i - 1][1] = array[i - 1][1] + t_i;
        }
        t_r = u_r * w_r - w_i * u_i;
        u_i = w_r * u_i + w_i * u_r;
        u_r = t_r;
      }
    }
  }

  public static void main(String[] args) throws Exception {
    int n = (int) (Math.pow(2, Integer.parseInt(args[0])));
    System.out.println("n: " + n);
    double[] oneD = new double[n * 2];
    // double[][] twoD = new double[n][2];

    int trials = 0;
    double timeOneD = 0;
    // double timeTwoD = 0;
    while (true) {
      trials++;
      System.out.println("trial: " + trials);
      CodeTimer ct0 = new CodeTimer("init");
      for (int i = 0; i < n; i++) {
        double r = Math.random();
        oneD[i * 2] = r;
        // twoD[i][0] = r;
      }
      ct0.stopAndReport();
      CodeTimer ct1 = new CodeTimer("oneD");
      FFT.fft(oneD);
      ct1.stop();
      timeOneD += ct1.getRunTimeMillis();
      System.out.printf("1-d, last: %.3f, avg: %.3f\n", ct1.getRunTimeMillis(),
          (timeOneD / (double) trials));

      // CodeTimer ct2 = new CodeTimer("twoD");
      // FFT.fft(twoD);
      // ct2.stop(false);
      // timeTwoD += ct2.getRunTimeMillis();
      // System.out.printf("2-d, last: %.3f, avg: %.3f\n", ct2.getRunTimeMillis(), (timeTwoD /
      // (double)trials));
      //
      // System.out.printf("1-d / 2-d: %.3f\n", timeTwoD / timeOneD);
    }
  }
}
