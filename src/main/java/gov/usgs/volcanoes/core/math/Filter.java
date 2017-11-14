package gov.usgs.volcanoes.core.math;

/**
 * A class for performing an arbitrary IIR filter.
 *
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 */
public class Filter {
  /** Perform an IIR filter.  The filtering occurs in place.
   * @param data the source data
   * @param size the size of the filter, used in lieu of coeffX.length
   * @param coeffX the X (input) coefficients
   * @param coeffY the Y (output) coefficients
   * @param gain the gain
   * @param fillCount the number of samples to fill at the beginning
   * @param fill the value to put in filled samples
   */
  public static void filter(double[] data, int size, double[] coeffX, double[] coeffY, double gain,
      double fillCount, double fill) {
    double[] yn = new double[size + 1];
    double[] xn = new double[size + 1];

    for (int i = 0; i < data.length; i++) {
      // move old values over
      for (int j = 1; j < size + 1; j++) {
        yn[j - 1] = yn[j];
        xn[j - 1] = xn[j];
      }
      // put new value on end
      xn[size] = data[i] / gain;

      double y = 0;
      // calculate y[n]
      for (int j = 0; j < size + 1; j++)
        y += xn[j] * coeffX[j];
      for (int j = 0; j < size; j++)
        y += yn[j] * coeffY[j];
      yn[size] = y;

      if (i > fillCount)
        data[i] = y;
      else
        data[i] = fill;
    }
  }

  public static double[] filterWithCopy(double[] data, int size, double[] coeffX, double[] coeffY,
      double gain, double fillCount, double fill) {
    double[] nd = new double[data.length];
    System.arraycopy(data, 0, nd, 0, data.length);
    filter(nd, size, coeffX, coeffY, gain, fillCount, fill);
    return nd;
  }
}
