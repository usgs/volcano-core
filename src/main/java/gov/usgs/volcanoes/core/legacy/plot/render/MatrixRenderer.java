package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.legacy.plot.Plot;
import gov.usgs.volcanoes.core.legacy.plot.color.ColorCycler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * A class that renders data in a 2D matrix.  The first column is the x axis, 
 * the second column is the rank, all subsequent columns are data columns.
 * Can not be instantiated with a null matrix.
 *
 * @author Dan Cervelli
 */
public class MatrixRenderer extends BasicFrameRenderer {

  private DoubleMatrix2D data;
  private boolean NO_DATA = false;
  private boolean[] visible;
  private int offset;

  /** The line renderers.  The length of this array should equal the number 
   * of columns in the data.  A value of null for a column means that no 
   * line renderer should be displayed for that column
   */
  protected ShapeRenderer[] lineRenderers;

  /** The point renderers.  The length of this array should equal the number 
   * of columns in the data.  A value of null for a column means that no 
   * point renderer should be displayed for that column
   */
  protected DataPointRenderer[] pointRenderers;

  /**
   * Default constructor
   */
  protected MatrixRenderer() {}

  /**
   * Constructor
   * @param d 2d matrix to initialize data
   * @param ranks true iff ranks are included
   */
  public MatrixRenderer(DoubleMatrix2D d, boolean ranks) {
    if (ranks) {
      offset = 2;
    } else {
      offset = 1;
    }
    setData(d);
  }

  /**
   * Initialize renderer with data
   * @param d 2d matrix to initialize data
   */
  public void setData(DoubleMatrix2D d) {
    if (d == null)
      throw new IllegalArgumentException("data must not be null");

    // check if this is a pre-formatted no data matrix
    if (d.rows() == 1 && Double.isNaN(d.getQuick(0, 0))) {
      NO_DATA = true;
    }

    data = d;
    visible = new boolean[data.columns() - offset];
    Arrays.fill(visible, true);
  }

  /**
   * Yield matrix data
   * @return matrix data
   */
  public DoubleMatrix2D getData() {
    return data;
  }

  /**
   * Switch all columns visible/unvisible
   * @param b value to set visibility to
   */
  public void setAllVisible(boolean b) {
    Arrays.fill(visible, b);
  }

  /**
   * Switch one column visibility
   * @param col column number
   * @param v visibility flag
   */
  public void setVisible(int col, boolean v) {
    if (col < data.columns() - offset)
      visible[col] = v;
  }

  /**
   * Get array of renderers for graph line, for every matrix column
   * @return array of line renderers
   */
  public ShapeRenderer[] getLineRenderers() {
    return lineRenderers;
  }

  /**
   * Set array of renderers for graph line, for every matrix column
   * @param r array of renderers
   */
  public void setLineRenderers(ShapeRenderer[] r) {
    lineRenderers = r;
  }

  /**
   * Set renderer for graph line to draw column
   * @param col matrux column number
   * @param r renderer to set
   */
  public void setLineRenderer(int col, ShapeRenderer r) {
    if (col < data.columns() - offset)
      lineRenderers[col] = r;
  }

  /**
   * Get array of renderers for graph point, for every matrix column
   * @return array of renderers
   */
  public PointRenderer[] getPointRenderers() {
    return pointRenderers;
  }

  /**
   * Set array of renderers for graph point, for every matrix column
   * @param r array of renderers
   */
  public void setPointRenderers(DataPointRenderer[] r) {
    pointRenderers = r;
  }

  /**
   * Set renderer for graph point to draw column
   * @param col matrux column number
   * @param r renderer to set
   */
  public void setPointRenderer(int col, PointRenderer r) {
    if (col < data.columns() - offset)
      pointRenderers[col] = (DataPointRenderer) r;
  }

  /**
   * Create and set one column line renderer
   * @param col column number
   * @param s stroke 
   * @param  color
   */
  public void createLineRenderer(int col, Stroke s, Color color) {
    if (visible[col]) {
      int ic = 1000;
      if (data != null)
        ic = data.rows() + 1;
      ShapeRenderer sr = new ShapeRenderer(new GeneralPath(GeneralPath.WIND_NON_ZERO, ic));
      sr.antiAlias = true;
      sr.color = color;
      sr.stroke = s;
      lineRenderers[col] = sr;
    }
  }

  /**
   * Create and set one column point renderer
   * @param col column number
   */
  public void createPointRenderer(int col, DataPointRenderer dr) {
    if (visible[col]) {
      dr.transformer = this;
      pointRenderers[col] = dr;
    }
  }

  /** 
   * Creates default line renderers for all columns
   * @param color color of lines
   */
  public void createDefaultLineRenderers(Color color) {
    createDefaultLineRenderers(0, color);
  }

  /** 
   * Creates default line renderers.  These are solid lines that cycle 
   * through the standard ColorCycler.
   * @param start initial position in sequence
   * @param color color of lines
   */
  public void createDefaultLineRenderers(int start, Color color) {
    lineRenderers = new ShapeRenderer[data.columns() - offset];
    ColorCycler cc = new ColorCycler(start);
    for (int i = 0; i < data.columns() - offset; i++) {
      if (visible[i]) {
        int ic = 1000;
        ic = data.rows() + 1;
        ShapeRenderer sr = new ShapeRenderer(new GeneralPath(GeneralPath.WIND_NON_ZERO, ic));
        if (color == null) {
          sr.color = cc.getNextColor();
        } else {
          sr.color = color;
        }
        lineRenderers[i] = sr;
      }
    }
  }

  /**
   * Creates default point renderers for all columns
   * @param color color of lines
   */
  public void createDefaultPointRenderers(Color color) {
    createDefaultPointRenderers(0, 'o', color);
  }

  /**
   * Creates default point renderers for all columns
   * @param shape shape to draw for data points
   * @param color color of lines
   */
  public void createDefaultPointRenderers(char shape, Color color) {
    createDefaultPointRenderers(0, shape, color);
  }

  /**
   * Creates default point renderers for all columns
   * @param start first column to start rendering from
   * @param color color of lines
   */
  public void createDefaultPointRenderers(int start, Color color) {
    createDefaultPointRenderers(start, 'o', color);
  }

  /** 
   * Creates default point renderers.  These are diamonds that 
   * cycle through the standard ColorCycler.
   * @param start first column to start rendering from
   * @param shape shape to draw for data points
   * @param color color of lines
   */
  public void createDefaultPointRenderers(int start, char shape, Color color) {
    pointRenderers = new DataPointRenderer[data.columns() - offset];
    ColorCycler cc = new ColorCycler(start);
    for (int i = 0; i < data.columns() - offset; i++) {
      if (visible[i]) {
        DataPointRenderer dpr = new DataPointRenderer(shape, 8);
        if (color == null) {
          dpr.color = cc.getNextColor();
        } else {
          dpr.color = color;
        }
        dpr.transformer = this;
        pointRenderers[i] = dpr;
      }
    }
  }

  /**
   * Shortcut for createDefaultLegendRenderer(s, 0)
   * @param s the legend names
   */
  public void createDefaultLegendRenderer(String[] s) {
    createDefaultLegendRenderer(s, 0);
  }

  /** 
   * Creates a standard legend, a small line and point sample followed by
   * the specified names.
   * @param s the legend names
   */
  public void createDefaultLegendRenderer(String[] s, int leftLines) {
    double additionalYOffset = 0;
    if (leftLines > 0) {
      additionalYOffset = (leftLines + 0.5) * 16;
    }
    setLegendRenderer(new LegendRenderer(NO_DATA));
    getLegendRenderer().x = graphX + 6;
    getLegendRenderer().y = graphY + 6 + additionalYOffset;
    addToLegendRenderer(getLegendRenderer(), s, leftLines);
  }

  /**
   * Adds to given legend renderer entries for this matrix renderer
   * @param lr
   * @param s
   * @param leftLines
   */
  public void addToLegendRenderer(LegendRenderer lr, String[] s, int leftLines) {
    for (int i = 0; i < data.columns() - offset; i++) {
      if (visible[i]) {
        ShapeRenderer sr = null;
        if (lineRenderers != null) {
          sr = (ShapeRenderer) lineRenderers[i];
        }
        DataPointRenderer dpr = null;
        if (pointRenderers != null) {
          dpr = (DataPointRenderer) pointRenderers[i];
        }
        if (i < s.length) {
          lr.addLine(sr, dpr, s[i]);
        }
      }
    }
  }

  /**
   * Get data column offset (sometimes first column is rank)
   * @return data column offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Get array of column visibility flags
   * @return array of column visibility flags
   */
  public boolean[] getVisible() {
    return visible;
  }

  /**
   * Render the matrix
   * @param g the Graphics2D object to render to
   */
  public void render(Graphics2D g) {

    AffineTransform origAT = g.getTransform();
    Color origColor = g.getColor();
    Stroke origStroke = g.getStroke();
    Shape origClip = g.getClip();

    if (axis != null)
      axis.render(g);

    g.clip(new Rectangle(graphX, graphY, graphWidth, graphHeight));
    // g.clip(new Rectangle(graphX + 1, graphY + 1, graphWidth - 1, graphHeight - 1));

    if (!NO_DATA) {
      for (int j = 0; j < data.columns() - offset; j++) {

        if (!visible[j])
          continue;

        ShapeRenderer sr = null;
        if (lineRenderers != null) {
          sr = (ShapeRenderer) lineRenderers[j];
        }

        DataPointRenderer dpr = null;
        if (pointRenderers != null) {
          dpr = (DataPointRenderer) pointRenderers[j];
        }

        GeneralPath gp = null;
        if (sr != null) {
          gp = (GeneralPath) sr.shape;
          gp.reset();
          gp.moveTo((float) getXPixel(data.getQuick(0, 0)),
              (float) (getYPixel(data.getQuick(0, j + offset))));
        }

        boolean lastnd = false;

        // iterate through each of the data rows
        for (int i = 0; i < data.rows(); i++) {

          // lookup the coordinates from the data matrix
          double x = data.getQuick(i, 0);
          double y = data.getQuick(i, j + offset);

          if (dpr != null) {
            if (!(Double.isNaN(y)) && (y != Double.NEGATIVE_INFINITY)) {
              dpr.x = x;
              dpr.y = y;
              dpr.render(g);
            }
          }

          if ((gp != null) && (y != Double.NEGATIVE_INFINITY)) {
            if (!Double.isNaN(y)) {
              if (lastnd || i == 0) {
                gp.moveTo((float) getXPixel(x), (float) getYPixel(y));
              } else {
                gp.lineTo((float) getXPixel(x), (float) getYPixel(y));
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
    }

    if (getLegendRenderer() != null)
      getLegendRenderer().render(g);

    g.setClip(origClip);

    if (axis != null)
      axis.postRender(g);

    g.setStroke(origStroke);
    g.setColor(origColor);
    g.setTransform(origAT);
  }

  /**
   * Main method
   * @param args command line arguments
   */
  public static void main(String[] args) {
    try {
      int size = 100000;
      DoubleMatrix2D d = DoubleFactory2D.dense.make(size, 4);
      for (int i = 0; i < size; i++) {
        d.setQuick(i, 0, i * 60);
        d.setQuick(i, 1, Math.sin(8 * Math.PI * ((double) i / size)));
        d.setQuick(i, 2, Math.cos(8 * Math.PI * ((double) i / size)));
        d.setQuick(i, 3, Math.cos(8 * Math.PI * ((double) i / size))
            + 0.25 * Math.sin(28 * Math.PI * ((double) i / size)));
      }

      Plot plot = new Plot();
      plot.setSize(1000, 250);
      MatrixRenderer mr = new MatrixRenderer(d, false);
      mr.setLocation(50, 20, 900, 210);
      mr.setExtents(0, size * 60, -1.1, 1.1);
      mr.createDefaultAxis();
      mr.setVisible(1, false);
      mr.setXAxisToTime(8);
      mr.createDefaultLineRenderers(null);
      // mr.createDefaultPointRenderers();
      plot.addRenderer(mr);
      for (int i = 0; i < 10; i++)
        plot.writePNG("test.png");
      // plot.quickShow();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
