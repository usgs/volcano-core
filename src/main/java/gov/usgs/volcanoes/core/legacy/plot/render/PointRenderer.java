package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.legacy.plot.transform.Transformer;

import java.awt.Color;

/**
 * A Renderer that renders something at a point.

 * @author  Dan Cervelli
 */
abstract public class PointRenderer implements Renderer {
  /** The x coordinate.
   */
  public double x;

  /** The y coordinate.
   */
  public double y;

  /** The transformer from user-space to pixel-space for this Renderer.
   */
  public Transformer transformer;

  /** The Color.
   */
  public Color color;

  /** Empty constructor
   */
  public PointRenderer() {}
}
