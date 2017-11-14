package gov.usgs.volcanoes.core.legacy.plot.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import hep.aida.ref.Histogram1D;

/**
 * <p>HistogramRenderer is a FrameRenderer that renders a traditional 1-D histogram.
 * The histogram code is from the Colt library.</p>
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/08/28 19:14:10  dcervelli
 * Allows null histograms.
 *
 * Revision 1.1  2005/08/26 18:05:04  uid879
 * Initial avosouth commit.
 *
 * @author Dan Cervelli
 */
public class HistogramRenderer extends FrameRenderer {

  private Histogram1D histogram;

  /** The fill color of the histogram boxes.
   */
  public Color fillColor;

  /** The stroke <i>color</i> of the histrogram boxes.
   */
  public Color strokeColor;

  /** A flag to indicate the presence of real data in the data matrix
   */
  public boolean NO_DATA = false;

  private List<Renderer> renderers;

  /** Constructs a new HistogramRenderer for renderering the specified 
   * histrogram.
   * @param h the histogram
   */
  public HistogramRenderer(Histogram1D h) {
    histogram = h;
    fillColor = new Color(0.0f, 0.0f, 0.56f);
    strokeColor = Color.BLACK;
    renderers = new ArrayList<Renderer>();
    if (histogram.entries() == 0)
      NO_DATA = true;
  }

  /** Add a generic renderer.
   * @param r the Renderer.
   */
  public void addRenderer(Renderer r) {
    renderers.add(r);
  }

  /** Sets the default extents of the parent FrameRenderer.  The vertical
   * extent is such that there is a small bit of space above the tallest
   * bin.
   */
  public void setDefaultExtents() {
    if (histogram == null) {
      minX = 0;
      maxX = 1;
      minY = 0;
      maxY = 1;
    } else {
      minX = histogram.xAxis().lowerEdge();
      maxX = histogram.xAxis().upperEdge();
      minY = 0;
      maxY = histogram.binHeight(histogram.minMaxBins()[1]) * 1.1;
      if (maxY == 0)
        maxY = 1;
    }
  }

  /** Creates a standard legend, a small line and point sample followed by
   * the specified names.
   * @param s the legend names
   */
  public void createDefaultLegendRenderer(String[] s) {

    setLegendRenderer(new LegendRenderer(NO_DATA));
    getLegendRenderer().x = graphX + 6;
    getLegendRenderer().y = graphY + 6;
    getLegendRenderer().sided_line = true;
    DataPointRenderer dpr = new DataPointRenderer('s', 8);
    dpr.color = strokeColor;
    dpr.filled = true;
    dpr.fillColor = fillColor;
    ShapeRenderer sr = null;
    if (renderers.size() > 0) {
      Renderer r = renderers.get(0);
      if (r instanceof MatrixRenderer) {
        MatrixRenderer mr = (MatrixRenderer) r;
        if ((mr.pointRenderers != null) && (mr.pointRenderers.length > 0)) {
          sr = new ShapeRenderer(new Line2D.Double(graphX, graphY, graphX + 6, graphY));
          sr.color = mr.pointRenderers[0].color;
        } else if ((mr.lineRenderers != null) && (mr.lineRenderers.length > 0)) {
          sr = new ShapeRenderer(new Line2D.Double(graphX, graphY, graphX + 6, graphY));
          sr.color = mr.lineRenderers[0].color;
        }
      }
    }
    getLegendRenderer().addLine(sr, dpr, s[0]);
  }

  /** Renderers the histogram.
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {
    Color origColor = g.getColor();
    Paint origPaint = g.getPaint();
    AffineTransform origAT = g.getTransform();
    Shape origClip = g.getClip();

    if (axis != null)
      axis.render(g);

    if (histogram != null) {
      int bins = histogram.xAxis().bins();

      g.setClip(new Rectangle(graphX + 1, graphY + 1, graphWidth - 1, graphHeight - 1));
      Rectangle2D.Double rect = new Rectangle2D.Double();
      double offset = 0;
      for (int i = 0; i < bins; i++) {
        double binWidth = histogram.xAxis().binWidth(i);
        rect.setRect(getXPixel(histogram.xAxis().lowerEdge() + offset),
            getYPixel(histogram.binHeight(i)), binWidth * getXScale(),
            histogram.binHeight(i) * getYScale());
        offset += binWidth;

        if (fillColor != null) {
          g.setPaint(fillColor);
          g.fill(rect);
        }
        if (strokeColor != null) {
          g.setPaint(Color.black);
          g.draw(rect);
        }
      }
      g.setClip(origClip);
    }

    for (Renderer renderer : renderers)
      renderer.render(g);

    if (getLegendRenderer() != null)
      getLegendRenderer().render(g);

    g.setTransform(origAT);
    g.setPaint(origPaint);
    g.setColor(origColor);
  }

}
