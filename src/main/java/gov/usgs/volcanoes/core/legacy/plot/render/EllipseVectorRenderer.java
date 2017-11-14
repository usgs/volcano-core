package gov.usgs.volcanoes.core.legacy.plot.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

/**
 * <p>This is a Renderer that draws vectors with error ellipses.  Access the public member variables directly 
 * to set renderer specifications.</p>
 * 
 * @author Dan Cervelli
 */
public class EllipseVectorRenderer implements Renderer {
  protected final static Logger LOGGER = LoggerFactory.getLogger(EllipseVectorRenderer.class);

  /** The x coordinate for the vector tail.
   */
  public double x;

  /** The y coordinate for the vector tail.
   */
  public double y;

  /** The x coordinate of the vector head.
   */
  public double u;
  /** The y coordinate of the vector head.
   */
  public double v;

  /** The height of the vertical component.
  */
  public double z;

  public boolean drawEllipse = true;
  public double ellipseWidth;
  public double ellipseHeight;
  public double ellipseOrientation;
  public double sigZ;

  private double scale = 1;

  /** Flag for displaying horizontal components (x and y).
   */
  public boolean displayHoriz = true;

  /** Flag for displaying vertical component (z).
  */
  public boolean displayVert = true;

  public Color colorHoriz = Color.RED;
  public Color colorVert = Color.BLUE;

  /** Coordinate transformer for this vector.
   */
  public FrameRenderer frameRenderer;

  private static final double[] SCALES = new double[] {100000, 50000, 20000, 10000, 5000, 2000,
      1000, 500, 200, 100, 50, 20, 10, 5, 2, 1, 0.5, 0.2, 0.1, 0.05, 0.02, 0.01};

  private static GeneralPath arrowhead;

  static {
    arrowhead = new GeneralPath();

    arrowhead.moveTo(0, 0);
    arrowhead.lineTo(0.5f, -1.5f);
    arrowhead.lineTo(0, -1.3f);
    arrowhead.lineTo(-0.5f, -1.5f);

    arrowhead.closePath();
  }

  /** Generic empty constructor.
   */
  public EllipseVectorRenderer() {}

  /** Gets the best scale for the vector based on a specified magnitude.
   * @param maxMag the magnitude to base the scale on
   * @return the best scale
   */
  public static double getBestScale(double maxMag) {
    double dm = 1E300;
    int mi = -1;
    for (int i = 0; i < SCALES.length; i++)
      if (Math.abs(maxMag - SCALES[i]) < dm) {
        dm = Math.abs(maxMag - SCALES[i]);
        mi = i;
      }

    return SCALES[mi];
  }

  /** Sets the head position based on azimuth and magnitude.
   * @param az the azimuth
   * @param mag the magnitude
   */
  public void setUVByAngleMag(double az, double mag) {
    u = x + Math.cos(Math.toRadians(az)) * mag;
    v = y + Math.sin(Math.toRadians(az)) * mag;
  }

  /** Gets the azimuth of this vector.
   * @return the azimuth
   */
  public double getAngle() {
    return Math.toDegrees(Math.atan2(-u, -v));
  }

  /** Gets the magnitude of this vector.
   * @return the magnitude
   */
  public double getMag() {
    return Math.sqrt(u * u + v * v);
  }

  /** Sets the scale for this vector.
   * @param s the scale
   */
  public void setScale(double s) {
    scale = s;
  }

  /** Gets the scale for this vector.
   * @return the scale
   */
  public double getScale() {
    return scale;
  }

  /** Renders the vector and ellipse.
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {
    AffineTransform origAT = g.getTransform();
    Color origColor = g.getColor();
    Paint origPaint = g.getPaint();
    Stroke origStroke = g.getStroke();

    Object aa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Line2D.Double line = new Line2D.Double();

    // draw blue vertical vector
    if (displayVert) {
      line.setLine(frameRenderer.getXPixel(x), frameRenderer.getYPixel(y),
          frameRenderer.getXPixel(x), frameRenderer.getYPixel(y + z * scale));
      g.setColor(colorVert);
      g.draw(line);
      g.translate(frameRenderer.getXPixel(x), frameRenderer.getYPixel(y + z * scale));
      if (z > 0)
        g.rotate(Math.toRadians(180));
      g.setStroke(new BasicStroke(0.1f));
      double headScaleVert = Math.abs(z * scale * frameRenderer.getYScale() * 0.2);
      headScaleVert = Math.min(12, headScaleVert);
      headScaleVert = Math.max(5, headScaleVert);
      g.scale(headScaleVert, headScaleVert);
      g.fill(arrowhead);

      // draw blue vertical error bars
      if (drawEllipse) {
        g.setTransform(origAT);
        g.setStroke(new BasicStroke(2.0f));
        g.setColor(colorVert);
        // logger.fine(z + " " + sigZ);
        line.setLine(frameRenderer.getXPixel(x) - 3,
            frameRenderer.getYPixel(y + (z - sigZ) * scale), frameRenderer.getXPixel(x) + 3,
            frameRenderer.getYPixel(y + (z - sigZ) * scale));
        g.draw(line);
        line.setLine(frameRenderer.getXPixel(x) - 3,
            frameRenderer.getYPixel(y + (z + sigZ) * scale), frameRenderer.getXPixel(x) + 3,
            frameRenderer.getYPixel(y + (z + sigZ) * scale));
        g.draw(line);
      }
    }

    // draw red vector
    if (displayHoriz) {
      g.setTransform(origAT);
      g.setStroke(origStroke);
      line.setLine(frameRenderer.getXPixel(x), frameRenderer.getYPixel(y),
          frameRenderer.getXPixel(x + u * scale), frameRenderer.getYPixel(y + v * scale));
      // logger.fine("render - x:" + frameRenderer.getXPixel(x) + "/y:" + frameRenderer.getXPixel(y)
      // + "/u:" + frameRenderer.getXPixel(x + u * scale) + "/v:" + frameRenderer.getXPixel(y + v *
      // scale));
      g.setColor(colorHoriz);
      g.draw(line);
      g.translate(frameRenderer.getXPixel(x + u * scale), frameRenderer.getYPixel(y + v * scale));
      g.rotate(Math.toRadians(getAngle()));
      // logger.fine("angle:" + getAngle());
      // logger.fine("radians:" + Math.toRadians(getAngle()));
      g.setStroke(new BasicStroke(0.1f));
      double headScale = getMag() * scale * frameRenderer.getXScale() * 0.2;
      headScale = Math.min(12, headScale);
      headScale = Math.max(5, headScale);
      g.scale(headScale, headScale);
      g.fill(arrowhead);

      // draw error ellipse
      if (drawEllipse) {
        g.setTransform(origAT);
        g.setStroke(origStroke);
        g.setColor(colorHoriz);
        Ellipse2D.Double e =
            new Ellipse2D.Double(0, 0, ellipseWidth * scale, ellipseHeight * scale);
        e.x = -e.width / 2;
        e.y = -e.height / 2;

        g.translate(frameRenderer.getXPixel(x + u * scale), frameRenderer.getYPixel(y + v * scale));
        g.rotate(ellipseOrientation);
        g.scale(frameRenderer.getXScale(), frameRenderer.getYScale());
        g.setStroke(new BasicStroke((float) (1.0 / (float) frameRenderer.getXScale())));

        g.draw(e);
        g.setTransform(origAT);
      }
    }

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa);
    g.setStroke(origStroke);
    g.setPaint(origPaint);
    g.setColor(origColor);
    g.setTransform(origAT);
  }

}
