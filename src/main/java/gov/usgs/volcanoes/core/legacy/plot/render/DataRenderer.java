package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.data.Data;
import gov.usgs.volcanoes.core.legacy.plot.color.ColorCycler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

/**
 * DataRenderer is the workhorse of the Valve plotting section.  It is 
 * responsible for plotting all the time-series.  It keeps track of the data
 * and the associated line and point renderers for each piece (column) of the
 * data.

 * @author Dan Cervelli
 */
public class DataRenderer extends FrameRenderer {
  /** 
   * The data.
   */
  protected Data data;

  /** 
   * The line renderers.  The length of this array should equal the number 
   * of columns in the data.  A value of null for a column means that no 
   * line renderer should be displayed for that column
   */
  protected Renderer[] lineRenderers;

  /** 
   * The point renderers.  The length of this array should equal the number 
   * of columns in the data.  A value of null for a column means that no 
   * point renderer should be displayed for that column
   */
  protected Renderer[] pointRenderers;

  /** 
   * Constructor that specifies that data to use.
   * @param d the data
   */
  public DataRenderer(Data d) {
    data = d;
  }

  /** 
   * Gets the data.
   * @return the data
   */
  public Data getData() {
    return data;
  }

  /** 
   * Gets the line renderers.
   * @return the line renderers.
   */
  public Renderer[] getLineRenderers() {
    return lineRenderers;
  }

  /** 
   * Gets the point renderers.
   * @return the point renderers.
   */
  public Renderer[] getPointRenderers() {
    return pointRenderers;
  }

  /** 
   * Creates a standard legend, a small line and point sample followed by
   * the specified names.
   * @param s the legend names
   */
  public void createDefaultLegendRenderer(String[] s) {
    setLegendRenderer(new LegendRenderer());
    getLegendRenderer().x = graphX + 6;
    getLegendRenderer().y = graphY + 6;
    for (int i = 0; i < s.length; i++) {
      if (s[i] != null) {
        ShapeRenderer sr = null;
        if (lineRenderers != null)
          sr = (ShapeRenderer) lineRenderers[i];
        PointRenderer pr = null;
        if (pointRenderers != null)
          pr = (PointRenderer) pointRenderers[i];
        getLegendRenderer().addLine(sr, pr, s[i]);
      }
    }
  }

  /** 
   * Creates default line renderers.  These are solid lines that cycle 
   * through the standard ColorCycler.
   */
  public void createDefaultLineRenderers() {
    if (data == null) {
      return;
    }
    double[][] d = data.getData();
    lineRenderers = new Renderer[d[0].length - 1];
    ColorCycler cc = new ColorCycler();
    for (int i = 1; i < d[0].length; i++) {
      if (data.isVisible(i - 1)) {
        ShapeRenderer sr = new ShapeRenderer(new GeneralPath());
        sr.color = cc.getNextColor();
        lineRenderers[i - 1] = sr;
      }
    }
  }

  /** 
   * LJA rank mod (2 was 1)
   * Creates default point renderers.  These are diamonds that cycle through the standard ColorCycler.
   */
  public void createDefaultPointRenderers() {
    if (data == null) {
      return;
    }
    double[][] d = data.getData();
    pointRenderers = new Renderer[d[0].length - 2];
    // ColorCycler cc = new ColorCycler();
    for (int i = 2; i < d[0].length; i++) {
      if (data.isVisible(i - 2)) {
        DataPointRenderer pr = new DataPointRenderer();
        pr.transformer = this;
        pointRenderers[i - 2] = pr;
      }
    }
  }

  /** 
   * Render the data. 
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {
    AffineTransform origAT = g.getTransform();
    Color origColor = g.getColor();
    Stroke origStroke = g.getStroke();
    Shape origClip = g.getClip();
    // g.translate(graphX, graphY);

    if (axis != null) {
      axis.render(g);
    }

    // g.setClip(new Rectangle(graphX + 1, graphY + 1, graphWidth - 1, graphHeight - 1));
    g.clip(new Rectangle(graphX + 1, graphY + 1, graphWidth - 1, graphHeight - 1));

    double[][] d = data.getData();

    // iterate through each of the data columns
    for (int j = 2; j < d[0].length; j++) {

      if (!data.isVisible(j - 2)) {
        continue;
      }

      ShapeRenderer sr = null;
      if (lineRenderers != null) {
        sr = (ShapeRenderer) lineRenderers[j - 2];
      }

      PointRenderer pr = null;
      if (pointRenderers != null) {
        pr = (PointRenderer) pointRenderers[j - 2];
      }

      GeneralPath gp = null;
      if (sr != null) {
        gp = (GeneralPath) sr.shape;
        gp.reset();
        gp.moveTo((float) getXPixel(d[0][0]), (float) (getYPixel(d[0][j])));
      }

      boolean lastnd = false;

      // iterate through each of the data rows
      for (int i = 0; i < d.length; i++) {

        // point rendering
        if (pr != null) {
          if (d[i][j] != Data.NO_DATA) {
            /*
             * Double double_rank;
             * int int_rank;
             * double_rank = d[i][1];
             * int_rank = double_rank.intValue();
             * if (int_rank == 1) {
             * g.setColor(Color.RED);
             * } else if (int_rank == 2) {
             * g.setColor(Color.ORANGE);
             * } else if (int_rank == 3) {
             * g.setColor(Color.GREEN);
             * } else if (int_rank == 4) {
             * g.setColor(Color.BLUE);
             * }
             */
            pr.x = d[i][0];
            pr.y = d[i][j];
            pr.render(g);
          }
        }

        // line line rendering
        if (gp != null && i != 0) {
          if (d[i][j] != Data.NO_DATA) {
            if (lastnd) {
              gp.moveTo((float) getXPixel(d[i][0]), (float) getYPixel(d[i][j]));
            } else {
              gp.lineTo((float) getXPixel(d[i][0]), (float) getYPixel(d[i][j]));
            }
            lastnd = false;
          } else {
            lastnd = true;
          }
        }
      }

      if (sr != null) {
        sr.render(g);
      }
    }

    g.setClip(origClip);
    g.setStroke(origStroke);
    g.setColor(origColor);
    g.setTransform(origAT);

    if (getLegendRenderer() != null) {
      getLegendRenderer().render(g);
    }
  }
}
