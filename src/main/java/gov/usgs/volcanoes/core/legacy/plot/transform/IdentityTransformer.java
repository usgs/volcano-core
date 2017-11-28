package gov.usgs.volcanoes.core.legacy.plot.transform;


/**
 * <p>A basic implementation of Transformer which returns exactly what it is passed.</p>
 *
 * $Log: not supported by cvs2svn $
 * @author  Dan Cervelli
 */
public class IdentityTransformer implements Transformer {
  /** Empty constructor
   */
  public IdentityTransformer() {}

  /** Transforms nothing, in x == out x.
   * @param x the input
   * @return the output
   */
  public double getXPixel(double x) {
    return x;
  }

  /** Transforms nothing, in y == out y.
   * @param y the input
   * @return the output
   */
  public double getYPixel(double y) {
    return y;
  }

}
