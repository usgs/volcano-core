package gov.usgs.volcanoes.core.math;

import gov.usgs.volcanoes.core.configfile.ConfigFile;

/**
 * A class for doing Butterworth filters.
 *
 * <p>Adapted from code by A.J. Fisher available here:
 * http://www-users.cs.york.ac.uk/~fisher/mkfilter/
 *
 * <p>Revision 1.1 2005/08/26 17:30:00 uid879
 * Initial avosouth commit.
 *
 * @author Dan Cervelli
 */
public class Butterworth {
  public enum FilterType {
    BANDPASS("B"), HIGHPASS("H"), LOWPASS("L");

    /**
     * Parse a filter type from a string.
     * 
     * @param s string containing filter name
     * @return FilterType found
     */
    public static FilterType fromString(String s) {
      if (s == null) {
        return null;
      }

      if (s.equals("L")) {
        return LOWPASS;
      } else if (s.equals("H")) {
        return HIGHPASS;
      } else if (s.equals("B")) {
        return BANDPASS;
      } else {
        return null;
      }
    }

    public String code;

    private FilterType(String c) {
      code = c;
    }
  }
  
  class PZRep {
    int numPoles;
    int numZeros;
    Complex[] poles;
    Complex[] zeros;

    public PZRep(int max) {
      poles = new Complex[max];
      zeros = new Complex[max];
      numPoles = 0;
      numZeros = 0;
    }

    public void output() {
      System.out.println("poles:");
      for (int i = 0; i < numPoles; i++) {
        System.out.println(poles[i]);
      }
      System.out.println("zeros:");
      for (int i = 0; i < numZeros; i++) {
        System.out.println(zeros[i]);
      }
    }
  }

  private Complex[] botCoeffs;
  private double corner1 = 6.0;
  private double corner2 = 0.0;
  private Complex dcGain;
  private Complex fcGain;
  private Complex hfGain;
  private int order = 4;
  private double rawAlpha1;
  private double rawAlpha2;
  private double samplingRate = 100;
  private PZRep sPlane;
  private Complex[] topCoeffs;
  private FilterType type = FilterType.LOWPASS;
  private double warpedAlpha1;
  private double warpedAlpha2;
  private double[] xCoeffs;
  private double[] yCoeffs;

  private PZRep zPlane;

  public Butterworth() {
    this(FilterType.BANDPASS, 4, 100, 1, 10);
  }

  public Butterworth(Butterworth bw) {
    this(bw.type, bw.order, bw.samplingRate, bw.corner1, bw.corner2);
  }

  public Butterworth(FilterType t, int o, double sr, double c1, double c2) {
    set(t, o, sr, c1, c2);
  }

  private void choosePole(Complex z) {
    if (z.re < 0.0) {
      sPlane.poles[sPlane.numPoles++] = z;
    }
  }

  private void computeS() {
    for (int i = 0; i < 2 * order; i++) {
      final double theta = (order % 2 == 1) ? (i * Math.PI) / order : ((i + 0.5) * Math.PI) / order;
      choosePole(Complex.expj(theta));
    }
  }

  private void computeZ() {
    zPlane.numPoles = sPlane.numPoles;
    zPlane.numZeros = sPlane.numZeros;

    for (int i = 0; i < zPlane.numPoles; i++) {
      zPlane.poles[i] = sPlane.poles[i].blt();
    }
    for (int i = 0; i < zPlane.numZeros; i++) {
      zPlane.zeros[i] = sPlane.zeros[i].blt();
    }

    while (zPlane.numZeros < zPlane.numPoles) {
      zPlane.zeros[zPlane.numZeros++] = new Complex(-1.0, 0);
    }
  }

  /**
   * Create.
   */
  public void create() {
    sPlane = new PZRep(order * 2);
    zPlane = new PZRep(order * 2);
    rawAlpha1 = corner1 / samplingRate;
    rawAlpha2 = corner2 / samplingRate;
    computeS();
    prewarp();
    normalize();
    computeZ();
    expandPoly();
  }

  private void expand(Complex[] pz, int npz, Complex[] coeffs) {
    int i;
    coeffs[0] = new Complex(1.0, 0);
    for (i = 0; i < npz; i++) {
      coeffs[i + 1] = new Complex(0.0, 0.0);
    }
    for (i = 0; i < npz; i++) {
      multIn(pz[i], npz, coeffs);
    }
  }

  private void expandPoly() {
    int i;
    topCoeffs = new Complex[order * 2 + 1];
    botCoeffs = new Complex[order * 2 + 1];
    xCoeffs = new double[order * 2 + 1];
    yCoeffs = new double[order * 2 + 1];
    expand(zPlane.zeros, zPlane.numZeros, topCoeffs);
    expand(zPlane.poles, zPlane.numPoles, botCoeffs);
    dcGain = Complex.evaluate(topCoeffs, zPlane.numZeros, botCoeffs, zPlane.numPoles,
        new Complex(1.0, 0));
    final double theta = 2 * Math.PI * 0.5 * (rawAlpha1 + rawAlpha2);
    fcGain = Complex.evaluate(topCoeffs, zPlane.numZeros, botCoeffs, zPlane.numPoles,
        Complex.expj(theta));
    hfGain = Complex.evaluate(topCoeffs, zPlane.numZeros, botCoeffs, zPlane.numPoles,
        new Complex(-1.0, 0));
    for (i = 0; i <= zPlane.numZeros; i++) {
      xCoeffs[i] = +(topCoeffs[i].re / botCoeffs[zPlane.numPoles].re);
    }
    for (i = 0; i <= zPlane.numPoles; i++) {
      yCoeffs[i] = -(botCoeffs[i].re / botCoeffs[zPlane.numPoles].re);
    }
  }

  public double getCorner1() {
    return corner1;
  }

  public double getCorner2() {
    return corner2;
  }

  /**
   * Gain.
   * 
   * @return filter gain
   */
  public double getGain() {
    switch (type) {
      case LOWPASS:
        return dcGain.hypot();
      case HIGHPASS:
        return hfGain.hypot();
      case BANDPASS:
        return fcGain.hypot();
      default:
        return 0;
    }
  }

  public int getOrder() {
    return order;
  }

  /**
   * Size.
   * 
   * @return filter size
   */
  public int getSize() {
    if (type == FilterType.BANDPASS) {
      return order * 2;
    } else {
      return order;
    }
  }

  public FilterType getType() {
    return type;
  }

  public double[] getXCoeffs() {
    return xCoeffs;
  }

  public double[] getYCoeffs() {
    return yCoeffs;
  }

  private void multIn(Complex w, int npz, Complex[] coeffs) {
    final Complex nw = w.neg();
    for (int i = npz; i >= 1; i--) {
      coeffs[i] = (nw.mult(coeffs[i])).plus(coeffs[i - 1]);
    }
    coeffs[0] = nw.mult(coeffs[0]);
  }

  private void normalize() {
    final double w1 = 2 * Math.PI * warpedAlpha1;
    final double w2 = 2 * Math.PI * warpedAlpha2;
    switch (type) {
      case LOWPASS:
        for (int i = 0; i < sPlane.numPoles; i++) {
          sPlane.poles[i] = sPlane.poles[i].mult(w1);
        }
        sPlane.numZeros = 0;
        break;
      case HIGHPASS:
        for (int i = 0; i < sPlane.numPoles; i++) {
          sPlane.poles[i] = new Complex(w1, 0).divide(sPlane.poles[i]);
        }
        for (int i = 0; i < sPlane.numPoles; i++) {
          sPlane.zeros[sPlane.numZeros++] = new Complex(); // also N zeros at (0,0) /
        }
        break;
      case BANDPASS:
        final double w0 = Math.sqrt(w1 * w2);
        final double bw = w2 - w1;
        int i;
        for (i = 0; i < sPlane.numPoles; i++) {
          final Complex hba = new Complex(0.5, 0).mult(sPlane.poles[i].mult(bw));
          final Complex temp =
              (new Complex(1.0, 0).minus(new Complex(new Complex(w0, 0).divide(hba)).sqr())).sqrt();
          sPlane.poles[i] = hba.mult(new Complex(1.0, 0).plus(temp));
          sPlane.poles[sPlane.numPoles + i] = hba.mult(new Complex(1.0, 0).minus(temp));
        }

        for (i = 0; i < sPlane.numPoles; i++) {
          sPlane.zeros[i] = new Complex();
        }

        sPlane.numZeros = sPlane.numPoles;
        sPlane.numPoles *= 2;
        break;
      default:
        throw new RuntimeException("Unknown filter type " + type);
    }
  }

  /**
   * outputRR.
   */
  public void outputRR() {
    System.out.println("RR:\ny[n]=");
    int i;
    for (i = 0; i < zPlane.numZeros + 1; i++) {
      final double x = xCoeffs[i];
      System.out.println(x + " * x[n-" + (zPlane.numZeros - i) + "]");
    }
    for (i = 0; i < zPlane.numPoles; i++) {
      System.out.println("     + (" + yCoeffs[i] + " * y[n-" + (zPlane.numPoles - i) + "])");
    }
    System.out.println("Gain=" + getGain());
  }

  private void prewarp() {
    warpedAlpha1 = Math.tan(Math.PI * rawAlpha1) / Math.PI;
    warpedAlpha2 = Math.tan(Math.PI * rawAlpha2) / Math.PI;
  }

  /**
   * Save filter settings to a ConfigFile.
   * 
   * @param cf ConfigFile to receive settings
   * @param prefix prefix added to each setting name
   */
  public void save(ConfigFile cf, String prefix) {
    cf.put(prefix + ".type", type.code);
    cf.put(prefix + ".order", Integer.toString(order));
    cf.put(prefix + ".corner1", Double.toString(corner1));
    cf.put(prefix + ".corner2", Double.toString(corner2));
    cf.put(prefix + ".samplingRate", Double.toString(samplingRate));
  }

  /**
   * Set filter values from a ConfigFile.
   * 
   * @param cf ConfigFile containing settings
   */
  public void set(ConfigFile cf) {
    type = FilterType.fromString(cf.getString("type"));
    order = Integer.parseInt(cf.getString("order"));
    corner1 = Double.parseDouble(cf.getString("corner1"));
    corner2 = Double.parseDouble(cf.getString("corner2"));
    samplingRate = Double.parseDouble(cf.getString("samplingRate"));
  }

  /**
   * Set filter with provided values.
   * 
   * @param t type
   * @param o order
   * @param sr sampling rate
   * @param c1 corner 1
   * @param c2 corner 2
   */
  public void set(FilterType t, int o, double sr, double c1, double c2) {
    type = t;
    order = o;
    samplingRate = sr;
    corner1 = c1;
    corner2 = c2;
  }

  public void setSamplingRate(double s) {
    samplingRate = s;
  }
}
