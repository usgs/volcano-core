package gov.usgs.volcanoes.core.math;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class Spectra {

  private final int nfft; // FFT length
  private final double samplingRate; // Sampling rate expressed as samples per second (i.e., in Hz)
  private final double[] frequency;
  private final double[] power;

  public final int length;

  public Spectra(double[] signal, double sr, int nf) {

    samplingRate = sr;
    nfft = nf;

    double[] transform = new double[nfft];
    System.arraycopy(signal, 0, transform, 0, Math.min(transform.length, signal.length));
    DoubleFFT_1D FFT = new DoubleFFT_1D(nfft);
    FFT.realForward(transform);

    boolean odd = nfft % 2 == 1;

    if (odd)
      length = (nfft + 1) / 2;
    else
      length = nfft / 2 + 1;

    power = new double[length];
    power[0] = Math.abs(transform[0]);

    for (int i = 2; i < nfft - 1; i = i + 2)
      power[i / 2] = Math.sqrt(transform[i] * transform[i] + transform[i + 1] * transform[i + 1]);

    if (odd) {
      if (length > 1)
        power[length - 1] =
            Math.sqrt(transform[1] * transform[1] + transform[nfft - 1] * transform[nfft - 1]);
    } else
      power[length - 1] = Math.abs(transform[1]);

    frequency = new double[length];
    double delta = samplingRate / nfft;
    for (int i = 0; i < length; i++)
      frequency[i] = delta * i;

  }

  /**
   * Returns the minimum value of the "power" array.
   */
  public double getMinPower() {
    return getMinPower(0, samplingRate / 2);
  }

  /**
   * Returns the minimum value of the "power" array within a frequency range
   * @param F1 lower frequency
   * @param F2 upper frequency
   */
  public double getMinPower(double F1, double F2) {

    double MIN = Double.MAX_VALUE;
    for (int i = 0; i < length; i++)
      if (power[i] < MIN & frequency[i] >= F1 & frequency[i] <= F2)
        MIN = power[i];
    return MIN;
  }

  /**
   * Returns the maximum value of the "power" array.
   */
  public double getMaxPower() {
    return getMaxPower(samplingRate / nfft, 1 / samplingRate / 2);
  }

  /**
   * Returns the maximum value of the "power" array within a frequency range
   * @param F1 lower frequency
   * @param F2 upper frequency
   */
  public double getMaxPower(double F1, double F2) {

    double MAX = Double.MIN_VALUE;
    for (int i = 0; i < length; i++)
      if (power[i] > MAX & frequency[i] >= F1 & frequency[i] <= F2)
        MAX = power[i];
    return MAX;

  }

  public double getSamplingRate() {
    return samplingRate;
  }

  public int getNfft() {
    return nfft;
  }

  public double[] getPower() {
    return power;
  }

  public double[] getFrequency() {
    return frequency;
  }

  public double[][] getMatrix(boolean logPower, boolean logFreq) {

    double[] P, F;
    double[][] output = new double[length][2];

    P = getPower();
    F = getFrequency();

    for (int i = 0; i < length; i++) {
      output[i][0] = logFreq ? Math.log10(F[i]) : F[i];
      output[i][1] = logPower ? Math.log10(P[i]) : P[i];
    }

    return output;

  }

}
