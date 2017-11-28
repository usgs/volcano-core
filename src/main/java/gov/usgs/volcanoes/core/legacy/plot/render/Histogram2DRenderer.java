package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.legacy.plot.color.Jet;
import gov.usgs.volcanoes.core.legacy.plot.color.Spectrum;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import hep.aida.ref.Histogram2D;

/**
 * <p>Histogram2DRenderer is a FrameRenderer that renders a 2-D histogram.
 * The histogram code is from the Colt library.  See the application overview
 * for more information.</p>
 *
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 */
public class Histogram2DRenderer extends FrameRenderer {
  private Histogram2D histogram;
  private List<Renderer> renderers;
  private boolean log;
  private Spectrum spectrum;
  private boolean inverted;

  /** Constructs a new Histogram2DRenderer for renderering the specified 
   * histrogram.
   * @param hist the histogram
   */
  public Histogram2DRenderer(Histogram2D hist) {
    histogram = hist;
    renderers = new ArrayList<Renderer>();
    log = false;
    spectrum = Jet.getInstance();
    inverted = false;
  }

  /** Sets the log flag.  The log flag specifies whether or not the common
   * log of the data count should be displayed instead of the raw data count.
   * @param b the log flag
   */
  public void setLog(boolean b) {
    log = b;
  }

  /**
   * Sets the inverted flag. The inverted flag specifies whether or not the
   * y-Axis is inverted.
   * @param b the inverted flag
   */
  public void setInverted(boolean b) {
    inverted = b;
  }

  /** Add a generic renderer.
   * @param r the Renderer.
   */
  public void addRenderer(Renderer r) {
    renderers.add(r);
  }

  /** Gets a renderer that draws the proper scale/key/legend for this
   * Histogram2DRenderer based on its current settings.  This could made a 
   * bit more intelligent -- it makes some dumb ticks.
   *
   * @param oX the x-pixel location for the scale
   * @param oY the y-pixel location for the scale
   * @return the scale renderer
   */
  public Renderer getScaleRenderer(final int oX, final int oY) {
    return new Renderer() {

      /** The render function for the scale renderer.
       * @param g the graphics object upon which to render
       */
      public void render(Graphics2D g) {
        Rectangle2D.Double rect = new Rectangle2D.Double();

        spectrum.renderScale(g, oX, oY, 400, 10, false, true);
        g.setPaint(Color.BLACK);
        rect.setRect(oX - 1, oY - 1, 402, 11);
        g.draw(rect);
        int[] mmb = histogram.minMaxBins();
        double min = 0;
        double max = 0;
        double dm = 0;
        if (log) {
          min = (histogram.binHeight(mmb[0], mmb[1]));
          max = Math.log(histogram.binHeight(mmb[2], mmb[3])) / Math.log(10);
        } else {
          min = histogram.binHeight(mmb[0], mmb[1]);
          max = histogram.binHeight(mmb[2], mmb[3]);
        }
        dm = max - min;
        // String pre = (log ? "10^" : "");

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(log ? 2 : 0);
        Line2D.Double line = new Line2D.Double();
        FontMetrics fm = g.getFontMetrics(g.getFont());
        for (int i = 0; i <= 6; i++) {
          int x = (int) Math.round((double) oX + (double) i * 66.66666667);
          line.setLine(x, oY - 1, x, oY - 5);
          g.draw(line);

          double c = min + ((double) i / 6d) * dm;
          double d = (log ? Math.pow(10, c) : c);
          // String s = pre + nf.format(min + ((double)i / 6d) * dm);
          String s = nf.format(Math.round(d));
          // if (s.equals("10^0"))
          // s = "0";
          g.drawString(s, x - fm.stringWidth(s) / 2, oY - 7);
        }
        g.drawString("Earthquake Density", oX + 148, oY + 27);
      }
    };
  }

  /** Renderer the 2-D histogram.  This uses the Jet color spectrum.
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {
    int xBins = histogram.xAxis().bins();
    int yBins = histogram.yAxis().bins();

    Color origColor = g.getColor();
    Paint origPaint = g.getPaint();
    AffineTransform origAT = g.getTransform();
    Shape origClip = g.getClip();
    Object aa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

    if (axis != null)
      axis.render(g);

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setClip(new Rectangle(graphX + 1, graphY + 1, graphWidth - 1, graphHeight - 1));
    Rectangle2D.Double rect = new Rectangle2D.Double();
    double xOffset = 0;
    double yOffset = 0;
    int[] mmb = histogram.minMaxBins();
    double min = 0;
    double max = 0;
    double dm = 0;
    if (log) {
      min = (histogram.binHeight(mmb[0], mmb[1]));
      max = Math.log(histogram.binHeight(mmb[2], mmb[3])) / Math.log(10);
    } else {
      min = histogram.binHeight(mmb[0], mmb[1]);
      max = histogram.binHeight(mmb[2], mmb[3]);
    }
    dm = max - min;

    for (int j = 0; j < yBins; j++) {
      double binHeight = histogram.yAxis().binWidth(j);
      xOffset = 0;
      if (!inverted)
        yOffset += binHeight;
      for (int i = 0; i < xBins; i++) {
        double binWidth = histogram.xAxis().binWidth(i);
        double xVal = getXPixel(histogram.xAxis().lowerEdge() + xOffset);
        double yVal;

        if (inverted)
          yVal = getYPixel(histogram.yAxis().upperEdge() - yOffset);
        else
          yVal = getYPixel(histogram.yAxis().lowerEdge() + yOffset);

        rect.setRect(xVal, yVal, binWidth * getXScale(), binHeight * getYScale());

        xOffset += binWidth;
        // System.out.println(rect);
        double count = 0;
        if (log)
          count = Math.log(histogram.binHeight(i, j)) / Math.log(10);
        else
          count = histogram.binHeight(i, j);
        count -= min;
        int color = (int) Math.round(count / dm * (spectrum.colors.length - 1));
        Color clr = spectrum.colors[color];

        // If the color is 0, make it completely transparent so that the entire image isn't
        // coated in blue.
        if (color == 0)
          clr = new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 0);
        else
          clr = new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 160);
        g.setPaint(clr);
        g.fill(rect);

        // offset += binWidth;
        // getXPixel(minX + binWidth), graphHeight - getYPixel(histogram.binHeight(i)));
        /*
         * if (fillColor != null)
         * {
         * g.setPaint(fillColor);
         * g.fill(rect);
         * }
         * if (strokeColor != null)
         * {
         * g.setPaint(Color.black);
         * g.draw(rect);
         * }
         */
      }
      if (inverted)
        yOffset += binHeight;
    }

    // g.translate(-graphX, -graphY);
    g.setClip(origClip);

    for (Renderer renderer : renderers)
      renderer.render(g);
    // for (int i = 0; i < renderers.size(); i++)
    // ((Renderer)renderers.elementAt(i)).render(g);

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa);
    g.setTransform(origAT);
    g.setPaint(origPaint);
    g.setColor(origColor);
  }

}
