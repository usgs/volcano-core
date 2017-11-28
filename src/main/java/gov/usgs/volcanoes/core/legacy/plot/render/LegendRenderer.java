package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.legacy.plot.transform.IdentityTransformer;
import gov.usgs.volcanoes.core.legacy.plot.transform.Transformer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The LegendRenderer is used to render a legend, generally in the upper-left corner of the plot.
 *
 * @author Dan Cervelli
 */
public class LegendRenderer implements Renderer {
  /** The coordinate space transformer.
   */
  public Transformer transformer;

  /** The entries in the legend.
   */
  public List<LegendEntry> entries;

  /** The x-coordinate of the legend location
   */
  public double x;

  /** The y-coordinate of the legend location
   */
  public double y;

  /** A flag to indicate the presence of real data in the data matrix
   */
  public boolean NO_DATA = false;

  public boolean sided_line = false;

  /** Constructs an empty legend
   */
  public LegendRenderer() {
    entries = new ArrayList<LegendEntry>();
  }

  /** Constructs an empty legend
   */
  public LegendRenderer(boolean nd) {
    this();
    NO_DATA = nd;
  }

  /** Adds a line (entry) to the legend.
   * @param e the line (entry)
   */
  public void addLine(LegendEntry e) {
    entries.add(e);
  }

  /** Adds a line (entry) to the legend creating the LegendEntry object for
   * you.
   * @param lr the data line renderer
   * @param pr the data point renderer
   * @param s the legend string
   */
  public void addLine(ShapeRenderer lr, PointRenderer pr, String s) {
    LegendEntry entry = new LegendEntry();
    entry.lineRenderer = lr;
    entry.pointRenderer = pr;
    entry.legend = s;
    entry.no_data = NO_DATA;
    entries.add(entry);
  }

  /** Renders the legend.
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {

    if (transformer == null)
      transformer = new IdentityTransformer();

    FontMetrics fm = g.getFontMetrics(g.getFont());
    double width = 1;
    double height = entries.size() * 16;
    int offset = 0;
    Shape dot = new Ellipse2D.Double(0, 0, 1, 1);

    for (LegendEntry e : entries) {
      if (e.no_data)
        e.legend = e.legend + " NO DATA";
      width = Math.max(width, fm.stringWidth(e.legend));
      if (e.lineRenderer != null || e.pointRenderer != null)
        offset = 25;
    }

    Paint origPaint = g.getPaint();
    Stroke origStroke = g.getStroke();

    // draw the box to hold the legend
    Rectangle2D.Double rect = new Rectangle2D.Double();
    rect.setRect(x, y, width + offset + 13, height + 6);
    // allow for a translucent background
    // g.setPaint(Color.white);
    g.setPaint(new Color(255, 255, 255, 192));
    g.fill(rect);
    g.setPaint(Color.black);
    g.draw(rect);

    Line2D.Double line = new Line2D.Double();

    // draw each entry
    for (int i = 0; i < entries.size(); i++) {
      LegendEntry e = entries.get(i);

      // line-based entries
      if (e.lineRenderer != null) {
        if (sided_line) {
          line.setLine(x + 2, y + 16 * (i + 1) - 5, x + 11, y + 16 * (i + 1) - 5);
        } else {
          line.setLine(x + 3, y + 16 * (i + 1) - 5, x + 28, y + 16 * (i + 1) - 5);
        }
        if (e.lineRenderer.stroke != null)
          g.setStroke(e.lineRenderer.stroke);
        if (e.lineRenderer.color != null)
          g.setPaint(e.lineRenderer.color);
        g.draw(line);
      }

      // point-based entries
      if (e.pointRenderer != null) {
        if (e.pointRenderer instanceof DataPointRenderer) {
          DataPointRenderer odpr = (DataPointRenderer) e.pointRenderer;
          DataPointRenderer ndpr = new DataPointRenderer();
          ndpr.shape = odpr.shape;
          ndpr.color = odpr.color;
          ndpr.stroke = odpr.stroke;
          ndpr.filled = odpr.filled;
          ndpr.fillColor = odpr.fillColor;
          ndpr.x = x + 18;
          ndpr.y = y + 16 * (i + 1) - 5;

          // The dot shape is impossible to discern in legends, so
          // if this shape is a dot, make it a bit bigger for the legend
          if (odpr.shape.equals(dot))
            ndpr.stroke = new BasicStroke(5.0f);

          ndpr.render(g);
        }
      }

      g.setPaint(origPaint);
      g.setStroke(origStroke);

      g.drawString(e.legend, (float) x + 8 + offset, (float) y + 16 * (i + 1));

      /*
       * if (e.no_data) {
       * AttributedString as = new AttributedString(e.legend);
       * as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 0,
       * e.legend.length());
       * g.drawString(as.getIterator(), (float)x + 8 + offset, (float)y + 16 * (i + 1));
       * } else {
       * g.drawString(e.legend, (float)x + 8 + offset, (float)y + 16 * (i + 1));
       * }
       */
    }

    g.setStroke(origStroke);
    g.setPaint(origPaint);
  }

  /** A simple class that describes a legend line (entry)
   */
  public class LegendEntry {
    /** The line renderer used to draw an example section of the data line.
     * Set to null if there is no line.
     */
    public ShapeRenderer lineRenderer;

    /** The point renderer used to draw an example data point.  
     * Set to null if there are no data points
     */
    public PointRenderer pointRenderer;

    /** The text of the legend entry.
     */
    String legend;

    /** A flag to indicate the presence of real data in the data matrix
     */
    public boolean no_data = false;

    public LegendEntry() {}

    public LegendEntry(String s) {
      legend = s;
    }
  }

}
