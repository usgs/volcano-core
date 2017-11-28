package gov.usgs.volcanoes.core.legacy.plot.transform;

/**
 * <p>An interface for an object that can perform a translation from user unit-space
 * (cm, microradians, lat/lon, etc) to pixel-space on a Plot.</p>
 *
 * @author  Dan Cervelli
 */
public interface Transformer {
  /** Transforms an x coordinate in user-space to pixel-space.
   * @param x the x coordinate
   * @return the transformed x coordinate
   */
  public double getXPixel(double x);

  /** Transforms an y coordinate in user-space to pixel-space.
   * @param y the y coordinate
   * @return the transformed y coordinate
   */
  public double getYPixel(double y);
}
