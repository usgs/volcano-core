package gov.usgs.volcanoes.core.legacy.plot.render;


import gov.usgs.volcanoes.core.data.LineData;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A class that renderers the contents of a LineData object.  This is generally
 * used for plotting the background to spatial plots.</p>
 *
 * TODO: use optimized version
 * 
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/07/22 20:11:08  cervelli
 * Added support for filling.
 *
 * Revision 1.1  2005/08/26 18:05:04  uid879
 * Initial avosouth commit.
 *
 * @author Dan Cervelli
 */
public class LineDataRenderer extends FrameRenderer {
  private LineData lineData;

  private List<Renderer> renderers;
  private boolean antiAlias;
  private Paint paint;

  /** The color for the lines
   */
  public Color color;

  /** The stroke to use for the lines
   */
  public Stroke stroke;

  /** Constructor that accepts a LineData object.
   * @param d the LineData
   */
  public LineDataRenderer(LineData d) {
    lineData = d;
    renderers = new ArrayList<Renderer>();
    color = Color.black;
  }

  /** Adds a generic renderer.
   * @param r the Renderer
   */
  public void addRenderer(Renderer r) {
    renderers.add(r);
  }

  public void setAntiAlias(boolean b) {
    antiAlias = b;
  }

  public void setPaint(Paint p) {
    paint = p;
  }

  /** Renderers the line data.
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {
    AffineTransform origAT = g.getTransform();
    Color origColor = g.getColor();
    Stroke origStroke = g.getStroke();
    Shape origClip = g.getClip();
    Paint origPaint = g.getPaint();

    if (axis != null)
      axis.render(g);

    if (antiAlias)
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // g.setClip(new Rectangle(graphX + 1, graphY + 1, graphWidth - 1, graphHeight - 1));
    g.setClip(new Rectangle(graphX, graphY, graphWidth + 2, graphHeight + 2));

    if (color != null)
      g.setColor(color);
    if (stroke != null)
      g.setStroke(stroke);

    // double partPast180 = Math.abs(minX + 180);

    if (lineData != null) {
      if (paint != null) {
        GeneralPath gp = getPolygon(lineData);
        g.setPaint(paint);
        g.fill(gp);
      } else {
        List<Point2D.Double> points = lineData.getPoints();
        Line2D.Double line = new Line2D.Double();
        Point2D.Double pt1, pt2;
        for (int i = 0; i < points.size() - 1; i++) {
          pt1 = points.get(i);
          if (Double.isNaN(pt1.x) || Double.isNaN(pt1.y))
            continue;
          pt2 = points.get(i + 1);
          if (Double.isNaN(pt2.x) || Double.isNaN(pt2.y))
            continue;

          /*
           * if (partPast180 > 0)
           * {
           * if (pt1.x > 180 - partPast180)
           * pt1.x = -180 - (180 - pt1.x);
           * if (pt2.x > 180 - partPast180)
           * pt2.x = -180 - (180 - pt2.x);
           * }
           */
          // line.setLine((float)getXPixel(pt1.x) + 1, (float)(getYPixel(pt1.y)) + 1,
          // (float)getXPixel(pt2.x) + 1, (float)(getYPixel(pt2.y)) + 1);
          line.setLine((float) getXPixel(pt1.x), (float) (getYPixel(pt1.y)),
              (float) getXPixel(pt2.x), (float) (getYPixel(pt2.y)));
          g.draw(line);
        }
      }
    }

    for (Renderer renderer : renderers)
      renderer.render(g);
    // for (int i = 0; i < renderers.size(); i++)
    // ((Renderer)renderers.elementAt(i)).render(g);

    if (antiAlias)
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    g.setClip(origClip);
    g.setPaint(origPaint);
    g.setStroke(origStroke);
    g.setColor(origColor);
    g.setTransform(origAT);
  }

  /**
   * Get transformed form of data
   * @param xform Transformer
   * @return transformed data
   */
  public GeneralPath getPolygon(LineData line) {
    GeneralPath gp = new GeneralPath();
    boolean first = true;
    for (Point2D.Double pt : line.getPoints()) {
      if (Double.isNaN(pt.x) || Double.isNaN(pt.y)) {
        first = true;
        gp.closePath();
        continue;
      }
      if (first) {
        first = false;
        gp.moveTo((float) getXPixel(pt.x) + 1, (float) getYPixel(pt.y) + 1);
      } else {
        gp.lineTo((float) getXPixel(pt.x) + 1, (float) getYPixel(pt.y) + 1);
      }
    }
    return gp;
  }

  /*
   * This is a working implemenation using the GeneralPath class
   * in Java2D. Using this would allow using dashed strokes that
   * worked as expected, however it is MUCH slower.
   *
   * boolean startLine = true;
   * // working generalPath impl
   * GeneralPath gp = new GeneralPath();
   * Point2D.Double pt;
   * for (int i = 0; i < points.size(); i++)
   * {
   * pt = (Point2D.Double)points.elementAt(i);
   * if (startLine)
   * {
   * gp.moveTo((float)getXPixel(pt.x), (float)(getYPixel(pt.y)));
   * startLine = false;
   * }
   * else
   * {
   * if (Double.isNaN(pt.x) || Double.isNaN(pt.y))
   * {
   * g.draw(gp);
   * startLine = true;
   * }
   * else
   * gp.lineTo((float)getXPixel(pt.x), (float)(getYPixel(pt.y)));
   * }
   * }
   */

}
