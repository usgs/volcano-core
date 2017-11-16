package gov.usgs.volcanoes.core.math;

/**
 * A class for the Goertzel algorithm.  
 *
 * @author Tom Parker
 */
public class Goertzel {

  public static double goertzel(double freq, double sampleR, int[] signal) {
    return goertzel(freq, sampleR, signal, true);
  }

  public static double goertzel(double freq, double sampleR, int[] signal, boolean hamming) {
    int len = signal.length;

    // hamming window -
    if (hamming)
      for (int i = 0; i < signal.length; i++)
        signal[i] *= (0.54 - 0.46 * Math.cos(2 * Math.PI * i / sampleR));

    double s;
    double s_prev = 0;
    double s_prev2 = 0;
    double coeff = 2 * Math.cos(2 * Math.PI * freq / sampleR);
    for (int i = 0; i < len; i++) {
      s = signal[i] + coeff * s_prev - s_prev2;
      s_prev2 = s_prev;
      s_prev = s;
    }

    double power = s_prev2 * s_prev2 + s_prev * s_prev - coeff * s_prev2 * s_prev;
    return power;
  }
}
