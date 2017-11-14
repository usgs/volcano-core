package gov.usgs.volcanoes.core.math;

public class Util {

  /** Shortcut for natural log of 10.
   */
  public static double LN10 = Math.log(10);

  /** Shortcut for natural log of 10.
   */
  public static double LOG10 = Math.log(10);

  /** Shortcut for natural log of 2.
   */
  public static double LOG2 = Math.log(2);

  public static double getNextPowerOf2(double arg) {
    return Math.pow(2, Math.ceil(Math.log(arg) / LOG2));
  }

  public static double getPreviousPowerOf2(double arg) {
    return Math.pow(2, Math.floor(Math.log(arg) / LOG2));
  }

  /** Gets the exponent of a double as if the number was in scientific 
   * notation. This is used by the auto plot ticking functions.
   * @param d the number
   * @return the exponent of this number
   */
  public static double getExp(double d) {
    return Math.floor(Math.log(Math.abs(d)) / LN10);
  }

  /** Gets the mantissa of a double as if the number was in scientific 
   * notation.  This is used by the auto plot ticking functions.
   * @param d the number
   * @return the manitissa of this number
   */
  public static double getMantissa(double d) {
    return d / Math.pow(10, getExp(d));
  }

  /** Computes the value of the modified Bessel function of the first 
   * kind of order 0.
   * @param x the function argument
   * @return the value of the function
   * 
   * From: A Numerical Library for Scientists and Engineers, Hang T. Lau
   * (2004), Chapman & Hall ISBN 1-58488-430-4
   */

  public static double bessi0(double x) {
    if (x == 0.0)
      return 1.0;
    if (Math.abs(x) <= 15.0) {
      double z, denominator, numerator;
      z = x * x;
      numerator = (z
          * (z * (z * (z
              * (z * (z * (z
                  * (z * (z * (z
                      * (z * (z * (z * (z * 0.210580722890567e-22 + 0.380715242345326e-19)
                          + 0.479440257548300e-16) + 0.435125971262668e-13) + 0.300931127112960e-10)
                      + 0.160224679395361e-7) + 0.654858370096785e-5) + 0.202591084143397e-2)
                  + 0.463076284721000e0) + 0.754337328948189e2) + 0.830792541809429e4)
              + 0.571661130563785e6) + 0.216415572361227e8) + 0.356644482244025e9)
          + 0.144048298227235e10);
      denominator =
          (z * (z * (z - 0.307646912682801e4) + 0.347626332405882e7) - 0.144048298227235e10);
      return -numerator / denominator;
    } else {
      return Math.exp(Math.abs(x)) * nonexpbessi0(x);
    }
  }

  /** Computes the value of the modified Bessel function of the first 
   * kind of order 0 multipled by e^(-x).
   * @param x the function argument
   * @return the value of the function
   * 
   * From: A Numerical Library for Scientists and Engineers, Hang T. Lau
   * (2004), Chapman & Hall ISBN 1-58488-430-4
   */

  public static double nonexpbessi0(double x) {
    if (x == 0.0)
      return 1.0;
    if (Math.abs(x) <= 15.0) {
      return Math.exp(-Math.abs(x)) * bessi0(x);
    } else {
      int i;
      double sqrtx, br, br1, br2, z, z2, numerator, denominator;
      double ar1[] =
          {0.2439260769778, -0.115591978104435e3, 0.784034249005088e4, -0.143464631313583e6};
      double ar2[] = {1.0, -0.325197333369824e3, 0.203128436100794e5, -0.361847779219653e6};
      x = Math.abs(x);
      sqrtx = Math.sqrt(x);
      br1 = br2 = 0.0;
      z = 30.0 / x - 1.0;
      z2 = z + z;
      for (i = 0; i <= 3; i++) {
        br = z2 * br1 - br2 + ar1[i];
        br2 = br1;
        br1 = br;
      }
      numerator = z * br1 - br2 + 0.346519833357379e6;
      br1 = br2 = 0.0;
      for (i = 0; i <= 3; i++) {
        br = z2 * br1 - br2 + ar2[i];
        br2 = br1;
        br1 = br;
      }
      denominator = z * br1 - br2 + 0.865665274832055e6;
      return (numerator / denominator) / sqrtx;
    }
  }

  /** Computes an N-point Kaiser window
   * @param windowLength length of the Kaiser window
   * @param beta the parameter for the Bessel function
   * @return the window 
   * 
   * pcervelli, 2011/07/10
   */

  public static double[] kaiser(int windowLength, double beta) {

    double[] window;
    double B0;
    double xind;

    int i;
    int odd;
    int n;

    window = new double[windowLength];
    B0 = Math.abs(Util.bessi0(beta));
    xind = (windowLength - 1) * (windowLength - 1);
    odd = windowLength % 2;
    n = (windowLength + odd) / 2;

    window[n - 1] = 1;
    for (i = 0; i < n - odd; i++) {
      window[i + n] =
          Math.abs(Util.bessi0(beta * Math.sqrt(1 - Math.pow(2 * i + 1 + odd, 2) / xind)) / B0);
      window[n - i - 1 - odd] = window[i + n];
    }

    return window;
  }

}
