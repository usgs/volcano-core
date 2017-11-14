package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.legacy.plot.transform.IdentityTransformer;
import gov.usgs.volcanoes.core.math.Geometry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * <p>A renderer for individual data points.</p>
 *
 * TODO: use enum for shape types
 * TODO: allow filling of shape
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2005/11/30 16:50:51  dcervelli
 * Added filling.
 *
 * Revision 1.2  2005/09/21 18:07:27  dcervelli
 * Added a to-do.
 *
 * Revision 1.1  2005/08/26 18:05:04  uid879
 * Initial avosouth commit.
 *
 * @author Dan Cervelli
 * @version $Id: DataPointRenderer.java,v 1.4 2007-05-21 02:39:24 dcervelli Exp $
 */
public class DataPointRenderer extends PointRenderer {
  /** The Shape.
   */
  public Shape shape;

  /** The Fill Color
   */
  public Color fillColor;

  /** The Stroke.
   */
  public Stroke stroke = new BasicStroke(1.0f);

  /** The Paint.
   */
  public Paint paint;

  public boolean antiAlias;

  public boolean filled;

  /** Generic constructor, creates a DataPointRenderer that draws
   * a 8-pixel circle 
   */
  public DataPointRenderer() {
    this('o', 8);
  }

  /** User-specified constructor to set shape and size. Legal values are 'o'
   * for a circle, 's' for a square, '*' for star and 't' for triangle
   *
   * @param type the shape of the data point, see above.
   * @param size the size of the data point.
   */
  public DataPointRenderer(char type, float size) {
    setShapeFromType(type, size);
  }

  /**
   * Parse shape type from string. Legal values are 'o'
   * for a circle, 's' for a square, '*' for star and 't' for triangle
   * @param type
   * @param size
   */
  public void setShapeFromType(char type, float size) {
    switch (type) {
      case 'o':
      case 'O':
      case '0':
        shape = new Ellipse2D.Double(-size / 2, -size / 2, size, size);
        break;
      case 's':
        shape = new Rectangle2D.Double(-size / 2, -size / 2, size, size);
        break;
      case 't':
        GeneralPath gp = new GeneralPath();
        gp.moveTo(0, -size / 2);
        gp.lineTo(size / 2, size / 2);
        gp.lineTo(-size / 2, size / 2);
        gp.lineTo(0, -size / 2);
        shape = gp;
        break;
      case 'd':
        shape = new Ellipse2D.Double(0, 0, 1, 1);
        break;
      case '*':
        shape = Geometry.getStar(size, true);
        break;
    }
  }

  /** Renders the data point using the internally specified stroke, paint, 
   * shape, and color.
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {

    Stroke origStroke = g.getStroke();
    Paint origPaint = g.getPaint();
    Color origColor = g.getColor();
    Object origRenderingHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

    if (antiAlias)
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (stroke != null)
      g.setStroke(stroke);

    if (color != null)
      g.setColor(color);

    if (transformer == null)
      transformer = new IdentityTransformer();
    double xt = transformer.getXPixel(x);
    double yt = transformer.getYPixel(y);
    g.translate(xt, yt);

    if (filled) {
      if (fillColor != null) {
        g.setColor(fillColor);
      } else if (color != null) {
        g.setColor(color);
      }
      g.fill(shape);
    }
    if (stroke != null) {
      g.setStroke(stroke);
      if (color != null) {
        g.setColor(color);
      }
      g.draw(shape);
    }

    g.translate(-xt, -yt);

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, origRenderingHint);
    g.setStroke(origStroke);
    g.setPaint(origPaint);
    g.setColor(origColor);
  }

  /**
   * Renders point in current position
   * @param g the graphics object upon which to render
   */
  public void renderAtOrigin(Graphics2D g) {
    Stroke origStroke = g.getStroke();
    Paint origPaint = g.getPaint();
    Color origColor = g.getColor();
    Object origRenderingHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

    if (antiAlias)
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (stroke != null)
      g.setStroke(stroke);

    if (color != null)
      g.setColor(color);

    // TODO: change render above to this system
    if (filled) {
      if (paint != null) {
        g.setPaint(paint);
      }
      g.fill(shape);
    }
    if (stroke != null) {
      if (color != null) {
        g.setColor(color);
      }
      g.draw(shape);
    }

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, origRenderingHint);
    g.setStroke(origStroke);
    g.setPaint(origPaint);
    g.setColor(origColor);
  }
}
