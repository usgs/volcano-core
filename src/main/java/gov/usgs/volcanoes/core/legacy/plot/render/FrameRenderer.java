package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.legacy.plot.decorate.SmartTick;
import gov.usgs.volcanoes.core.legacy.plot.transform.Transformer;
import gov.usgs.volcanoes.core.math.Util;

import java.text.NumberFormat;

/**
 * <p>FrameRenderer is the base class to all of the Valve-level renderers:
 * DataRenderer, ImageDataRenderer, HistogramRenderer, etc.  It contains
 * information about the location of the axis and the unit-space extent of the
 * view inside that axis.</p>
 *
 * <p>Also, the unit variable is used for differentiating portions of a graph
 * that could be combined into a single graph, see ValveGraphInfo.</p>
 *
 * <p>A bit more of an explanation how this works: the FrameRenderer describes
 * the extent of the space inside its axis.  It knows nothing about whatever
 * data will be renderered in it.  So, for example, the sgram module could grab
 * exactly two full days worth of data (the day being the size of the storage
 * file), set it's data extent to be the two full days, but set the view extent 
 * of the FrameRenderer to be only the requested time interval (and potentially
 * frequency range).  This way, the user would get back exactly what was 
 * expected but the programmer doesn't have to battle the data to grab exactly
 * what was needed.  Likewise, rescaling the y-axis is trivial this way.</p>
 *
 * TODO: eliminite or use units
 * 
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/06/15 14:29:15  dcervelli
 * Swarm 1.3.4 changes.
 *
 * Revision 1.3  2006/03/02 00:55:41  dcervelli
 * Added multipliers and offset to y-axis values (for Swarm).
 *
 * Revision 1.2  2006/01/27 20:55:37  tparker
 * Add configure options for wave plotter
 *
 * Revision 1.1  2005/08/26 18:05:04  uid879
 * Initial avosouth commit.
 * 
 * @author Dan Cervelli
 */
abstract public class FrameRenderer implements Renderer, Transformer {
  /** The names of the units in this FrameRenderer.  Used by ValveGraphInfo
   * to combine FrameRenderers with identical units.
   */
  protected String unit;

  /** The x-pixel location of the axis within the plot.
   */
  protected int graphX;

  /** The y-pixel location of the axis within the plot.
   */
  protected int graphY;

  /** The width in pixels of the axis within the plot.
   */
  protected int graphWidth;

  /** The height in pixels of the axis within the plot.
   */
  protected int graphHeight;

  /** The minimum x view extent (typically earliest j2ksec).
   */
  protected double minX;

  /** The maximum x view extent (typically latest j2ksec).
   */
  protected double maxX;

  /** The minimum y view extent.
   */
  protected double minY;

  /** The maximum y view extent.
   */
  protected double maxY;

  protected double yAxisMult = 1;
  protected double yAxisOffset = 0;

  protected double xAxisMult = 1;
  protected double xAxisOffset = 0;

  public boolean NO_DATA = false;

  /** The axis (box, gridlines, labels, etc).
   */
  protected AxisRenderer axis;

  /** The legend.
   */
  private LegendRenderer legendRenderer;

  /** Generic empty constructor.
   */
  public FrameRenderer() {}

  /** Sets the unit name.
   * @param u the unit.
   */
  public void setUnit(String u) {
    unit = u;
  }

  /** Gets the unit name.
   * @return the unit.
   */
  public String getUnit() {
    return unit;
  }

  /** Gets the axis.
   * @return the axis
   */
  public AxisRenderer getAxis() {
    return axis;
  }

  /** Sets the axis.
   * @param a the axis
   */
  public void setAxis(AxisRenderer a) {
    axis = a;
  }

  /** Sets the axis to an empty Renderer
  */
  public void createEmptyAxis() {
    axis = new AxisRenderer(this);
  }

  /**
   * Gets the Y axis multiplier
   * @return Y axis multiplier
   */
  public double getYAxisMult() {
    return yAxisMult;
  }

  /**
   * Gets the X axis multiplier
   * @return X axis multiplier
   */
  public double getXAxisMult() {
    return xAxisMult;
  }

  /**
   * Gets the X axis offset
   * @return X axis offset
   */
  public double getXAxisOffset() {
    return xAxisOffset;
  }

  /**
   * Gets the Y axis offset
   * @return Y axis offset
   */
  public double getYAxisOffset() {
    return yAxisOffset;
  }

  /**
   * Gets minimum X axis
   * @return minimum X axis
   */
  public double getMinXAxis() {
    return minX * xAxisMult + xAxisOffset;
  }

  /**
   * Gets maximum X axis
   * @return maximum X axis
   */
  public double getMaxXAxis() {
    return maxX * xAxisMult + xAxisOffset;
  }


  /**
   * Gets minimum Y axis
   * @return minimum Y axis
   */
  public double getMinYAxis() {
    if (yAxisMult < 0) {
      return maxY * yAxisMult + yAxisOffset;
    } else {
      return minY * yAxisMult + yAxisOffset;
    }
  }

  /**
   * Gets maximum Y axis
   * @return maximum Y axis
   */
  public double getMaxYAxis() {
    if (yAxisMult < 0) {
      return minY * yAxisMult + yAxisOffset;
    } else {
      return maxY * yAxisMult + yAxisOffset;
    }
  }

  /**
   * Sets X axis coefficients
   * @param m X axis multiplier
   * @param b X axis offset
   */
  public void setXAxisCoefficients(double m, double b) {
    xAxisMult = m;
    xAxisOffset = b;
  }

  /**
   * Sets Y axis coefficients
   * @param m Y axis multiplier
   * @param b Y axis offset
   */
  public void setYAxisCoefficients(double m, double b) {
    yAxisMult = m;
    yAxisOffset = b;
  }

  /** Shortcut for createDefaultAxis(8, 8, false, false).
   */
  public void createDefaultAxis() {
    createDefaultAxis(8, 8, true, true, false, false, true, true);
  }

  /** Shortcut for createDefaultAxis(8, 8, hExpand, vExpand).
   * @param hExpand horizontal expand argument
   * @param vExpand vertical expand argument
   */
  public void createDefaultAxis(boolean hExpand, boolean vExpand) {
    createDefaultAxis(8, 8, true, true, hExpand, vExpand, true, true);
  }

  /** Shortcut for createDefaultAxis(hTicks, vTicks, false, false).
   * @param hTicks suggested number of horizontal ticks
   * @param vTicks suggested number of vertical ticks
   */
  public void createDefaultAxis(int hTicks, int vTicks) {
    createDefaultAxis(hTicks, vTicks, true, true, false, false, true, true);
  }

  /** Creates the default axis using SmartTick.
   * @param hTicks suggested number of horizontal ticks
   * @param vTicks suggested number of vertical ticks
   * @param isHTicks "
   * @param isVTicks
   * @param hExpand should horizontal be expanded for better ticks
   * @param vExpand should vertical be expanded for better ticks
   * @param isHValues flag if we draw values near horizontal ticks
   * @param isVValues flag if we draw values near vertical ticks
   */
  public void createDefaultAxis(int hTicks, int vTicks, boolean isHTicks, boolean isVTicks,
      boolean hExpand, boolean vExpand, boolean isHValues, boolean isVValues) {
    if (axis == null)
      axis = new AxisRenderer(this);
    axis.createDefault();
    createDefaultYAxis(vTicks, isVTicks, isVValues, vExpand);
    if (hTicks > 0) {
      double[] t = SmartTick.autoTick(getMinXAxis(), getMaxXAxis(), hTicks, hExpand);
      if (hExpand) {
        minX = t[0];
        maxX = t[t.length - 1];
      }
      if (isHTicks) {
        axis.createTopTicks(t);
        axis.createBottomTicks(t);
        axis.createVerticalGridLines(t);
      }
      if (isHValues) {
        axis.createBottomTickLabels(t, null);
      }
    }
  }

  /** Creates the default axis using SmartTick, for use with longitude scales: properly handles
   * spanning the 180 degree line.
   * @param hTicks suggested number of horizontal ticks
   * @param vTicks suggested number of vertical ticks
   * @param hExpand should horizontal be expanded for better ticks
   * @param vExpand should vertical be expanded for better ticks
   */
  public void createDefaultLongitudeAxis(int hTicks, int vTicks, boolean isHTicks, boolean isVTicks,
      boolean hExpand, boolean vExpand) {
    axis = new AxisRenderer(this);
    axis.createDefault();
    createDefaultYAxis(vTicks, isVTicks, true, vExpand);
    double[] t = SmartTick.autoTick(getMinXAxis(), getMaxXAxis(), hTicks, hExpand);
    if (hExpand) {
      minX = t[0];
      maxX = t[t.length - 1];
    }
    if (isHTicks) {
      axis.createTopTicks(t);
      axis.createBottomTicks(t);
      axis.createVerticalGridLines(t);
    }
    axis.createBottomLongitudeTickLabels(t, null);
  }

  private static NumberFormat numberFormat = NumberFormat.getInstance();
  static {
    numberFormat.setMaximumFractionDigits(7);
  }

  /** Shortcut for createDefaultYAxis(vTicks, true,  vExpand)
   * @param vTicks suggest number of ticks
   * @param vExpand should the axis be expanded for better ticks
   */
  public void createDefaultYAxis(int vTicks, boolean vExpand) {
    createDefaultYAxis(vTicks, true, true, vExpand);
  }

  /** Creates the default Y-axis.  This includes right and left side ticks
   * and horizontal grid lines.
   * @param vTicks suggest number of ticks
   * @param isVTicks
   * @param vValues flag if we draw values near ticks
   * @param vExpand should the axis be expanded for better ticks
   */
  public void createDefaultYAxis(int vTicks, boolean isVTicks, boolean vValues, boolean vExpand) {
    double[] t = SmartTick.autoTick(getMinYAxis(), getMaxYAxis(), vTicks, vExpand);
    if (vExpand) {
      minY = t[0];
      maxY = t[t.length - 1];
    }
    if (isVTicks) {
      axis.createLeftTicks(t);
      axis.createRightTicks(t);
      axis.createHorizontalGridLines(t);
    }
    if (vValues) {
      String[] labels = null;
      if (yAxisMult != 1 || yAxisOffset != 0) {
        labels = new String[t.length];
        for (int i = 0; i < t.length; i++) {
          double exp = Util.getExp(t[i]);
          labels[i] = (exp >= 5
              ? numberFormat.format(t[i] / Math.pow(10, exp)) + "e" + numberFormat.format(exp)
              : numberFormat.format(t[i]));
          t[i] = (t[i] - yAxisOffset) / yAxisMult;
        }
      }
      axis.createLeftTickLabels(t, labels);
    }
  }

  /**
   * Initialize X axis as log axis
   * @param hTicks count of horisontal ticks
   */
  public void createDefaultLogXAxis(int hTicks) {
    // double[] t = SmartTick.autoTick(Math.pow(10, getMinXAxis()), Math.pow(10, getMaxXAxis()),
    // hTicks, false);
    double[] t = SmartTick.autoTick(getMinXAxis(), getMaxXAxis(), hTicks, false);
    String[] s = new String[t.length];
    for (int i = 0; i < t.length; i++) {
      // s[i] = Long.toString(Math.round(t[i]));
      // s[i] = Long.toString(Math.round(Math.pow(10, t[i])));
      s[i] = String.format("%.2f", Math.pow(10, t[i]));
      // t[i] = Math.log(t[i]) / Math.log(10);
    }
    axis.createBottomTicks(t);
    axis.createTopTicks(t);
    axis.createBottomTickLabels(t, s);
    axis.createVerticalGridLines(t);
  }

  /**
   * Initialize Y axis as log axis
   * @param vTicks count of vertical ticks
   */
  public void createDefaultLogYAxis(int vTicks) {
    // double[] t = SmartTick.autoTick(Math.pow(10, getMinYAxis()), Math.pow(10, getMaxYAxis()),
    // vTicks, false);
    double[] t = SmartTick.autoTick(getMinYAxis(), getMaxYAxis(), vTicks, false);
    String[] s = new String[t.length];
    for (int i = 0; i < t.length; i++) {
      // s[i] = Long.toString(Math.pow(10, t[i]));
      s[i] = String.format("%.0f", Math.pow(10, t[i]));
      // t[i] = Math.pow(10, t[i]);
    }
    axis.createLeftTicks(t);
    axis.createRightTicks(t);
    axis.createLeftTickLabels(t, s);
    axis.createHorizontalGridLines(t);
  }

  /** Shortcut for setXAxisToTime(ticks, true)
   * @param ticks the suggested number of time ticks
   */
  public void setXAxisToTime(int ticks) {
    setXAxisToTime(ticks, true, true);
  }

  /** Creates the x-axis as a time series.  This sets the bottom labels
   * to readable time stamps, top and bottom ticks and vertical grid lines.
   * @param ticks the suggested number of time ticks
   * @param isTicks
   * @param isLabels true if bottom tick labels are desired
   */
  public void setXAxisToTime(int ticks, boolean isTicks, boolean isLabels) {
    Object[] stt = SmartTick.autoTimeTick(getMinXAxis(), getMaxXAxis(), ticks);
    if (stt != null) {
      if (isTicks) {
        axis.createBottomTicks((double[]) stt[0]);
        axis.createTopTicks((double[]) stt[0]);
        axis.createVerticalGridLines((double[]) stt[0]);
      }
      if (isLabels) {
        axis.createBottomTickLabels((double[]) stt[0], (String[]) stt[1]);
      }
    }
  }

  /** Sets the location of the frame based on the location of another frame.
   * This is used for overlapping graphs.
   * @param fr the source FrameRenderer
   */
  public void setLocation(FrameRenderer fr) {
    graphX = fr.getGraphX();
    graphY = fr.getGraphY();
    graphWidth = fr.getGraphWidth();
    graphHeight = fr.getGraphHeight();
  }

  /** Sets the location of the frame in the plot.
   * @param x the x-pixel location
   * @param y the y-pixel location
   * @param w the width in pixels
   * @param h the height in pixels
   */
  public void setLocation(int x, int y, int w, int h) {
    graphX = x;
    graphY = y;
    graphWidth = w;
    graphHeight = h;
  }

  /** Sets the graph width.
   * @param w the width
   */
  public void setGraphWidth(int w) {
    graphWidth = w;
  }

  /** Sets the graph height.
   * @param h the height.
   */
  public void setGraphHeight(int h) {
    graphHeight = h;
  }

  /** Scales the height of the graph by a ratio.  New Height = Old Height * 
   * aspect.
   * @param asp the aspect ratio
   */
  public void setYAspect(double asp) {
    graphHeight = (int) Math.round((double) graphHeight * asp);
  }

  /** Sets the aspect ratio of the graph.  This sets the graph height and 
   * width based on an aspect ratio.  If the height of the graphs goes 
   * beyond yMax, the width is shrunk to fit (and recentered using 
   * plotWidth).
   * @param aspect the aspect ratio
   * @param yMax the maximum height of the graphs in pixels
   * @param plotWidth the width of the plot in pixels
   */
  public void setAspectRatio(double aspect, int yMax, int plotWidth) {
    double xr = maxX - minX;
    double yr = maxY - minY;
    double ratio = (yr / xr);
    graphHeight = (int) Math.round((double) graphWidth * ratio * aspect);
    if (graphHeight > yMax) {
      double xs = (double) graphWidth / (double) graphHeight;
      int newWidth = (int) Math.round(yMax * xs);
      setLocation((plotWidth / 2) - (newWidth / 2), graphY, newWidth, yMax);
    }
  }

  /** Sets the view extents of the frame.
   * @param loX the minimum x view extent
   * @param hiX the maximum x view extent
   * @param loY the minimum y view extent
   * @param hiY the maximum y view extent
   */
  public void setExtents(double loX, double hiX, double loY, double hiY) {
    minX = loX;
    maxX = hiX;
    minY = loY;
    maxY = hiY;
    if (minY == maxY) {
      minY = loY - 1;
      maxY = hiY + 1;
    }
  }

  /** Sets the minimum x view extent.
   * @param x the minimum x
   */
  public void setMinX(double x) {
    minX = x;
  }

  /** Sets the maximum x view extent.
   * @param x the maximum x
   */
  public void setMaxX(double x) {
    maxX = x;
  }

  /** Sets the minimum y view extent.
   * @param y the minimum y
   */
  public void setMinY(double y) {
    minY = y;
  }

  /** Sets the maximum y view extent.
   * @param y the maximum y
   */
  public void setMaxY(double y) {
    maxY = y;
  }

  /** Gets the minimum x view extent.
   * @return the minimum x
   */
  public double getMinX() {
    return minX;
  }

  /** Gets the minimum y view extent.
   * @return the minimum y
   */
  public double getMinY() {
    return minY;
  }

  /** Gets the maximum x view extent.
   * @return the maximum x
   */
  public double getMaxX() {
    return maxX;
  }

  /** Gets the maximum y view extent.
   * @return the maximum y
   */
  public double getMaxY() {
    return maxY;
  }

  /** Gets the x-scale (the graph width / horiztonal view extent).
   * @return the x-scale
   */
  public double getXScale() {
    return (double) graphWidth / (maxX - minX);
  }

  /** Gets the y-scale (the graph height / vertical view extent).
   * @return the y-scale
   */
  public double getYScale() {
    return (double) graphHeight / (maxY - minY);
  }

  /** Gets the view width.
   * @return the view width.
   */
  public double getWidth() {
    return maxX - minX;
  }

  /** Gets the view height.
   * @return the view height.
   */
  public double getHeight() {
    return maxY - minY;
  }

  /** Gets a transformed x-pixel (Transformer implemenatation).
   * @param x the x-coordinate in user space
   * @return the x-pixel
   */
  public double getXPixel(double x) {
    return ((x - minX) * getXScale()) + (double) graphX;
  }

  /** Gets a transformed y-pixel (Transformer implemenatation).
   * @param y the y-coordinate in user space
   * @return the y-pixel
   */
  public double getYPixel(double y) {
    return graphHeight - ((y - minY) * getYScale()) + (double) graphY;
  }

  /** Gets the graph x location.
   * @return the graph x location
   */
  public int getGraphX() {
    return graphX;
  }

  /** Gets the graph y location.
   * @return the graph y location
   */
  public int getGraphY() {
    return graphY;
  }

  /** Gets the graph width.
   * @return the graph width
   */
  public int getGraphWidth() {
    return graphWidth;
  }

  /** Gets the graph height.
   * @return the graph height
   */
  public int getGraphHeight() {
    return graphHeight;
  }

  public LegendRenderer getLegendRenderer() {
    return legendRenderer;
  }

  public void setLegendRenderer(LegendRenderer legendRenderer) {
    this.legendRenderer = legendRenderer;
  }

  /** Creates a standard legend, a small line and point sample followed by
   * the specified names.
   * @param s the legend names
   */
  public void createDefaultLegendRenderer(String[] s) {
    legendRenderer = new LegendRenderer(NO_DATA);
    legendRenderer.x = graphX + 6;
    legendRenderer.y = graphY + 6;
    for (int i = 0; i < s.length; i++)
      if (s[i] != null) {
        legendRenderer.addLine(null, null, s[i]);
      }
  }

  /** Gets the default translation
   * @return default translation
   */
  public double[] getDefaultTranslation() {
    double dx = (maxX - minX) / (graphWidth);
    double dy = (maxY - minY) / (graphHeight);

    /*
     * double d[] = new double[] {
     * dx, minX - dx * gMinX,
     * dy, minY - dy * (plot.getHeight() - gMaxY),
     * minX, maxX, minY, maxY};
     */

    double[] translation =
        new double[] {dx, minX - dx * graphX, dy, maxY + dy * graphY, minX, maxX, minY, maxY};

    return translation;
  }

  /** Gets the default translation for plot height
   * @param plotHeight height of plot
   * @return default translation for plot height
   */
  public double[] getDefaultTranslation(int plotHeight) {
    double dx = (maxX - minX) / (graphWidth);
    double dy = (maxY - minY) / (graphHeight);

    /*
     * double d[] = new double[] {
     * dx, minX - dx * gMinX,
     * dy, minY - dy * (plot.getHeight() - gMaxY),
     * minX, maxX, minY, maxY};
     */

    double[] translation =
        new double[] {dx, minX - dx * graphX, dy, minY - dy * (plotHeight - (graphHeight) - graphY), // <-
                                                                                                     // how
                                                                                                     // far
                                                                                                     // the
                                                                                                     // graph
                                                                                                     // is
                                                                                                     // from
                                                                                                     // the
                                                                                                     // bottom
                                                                                                     // of
                                                                                                     // the
                                                                                                     // plot
            minX, maxX, minY, maxY};

    return translation;
  }

}
