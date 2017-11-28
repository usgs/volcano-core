package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.legacy.plot.decorate.SmartTick;


/**
 * <p>Frame renderer with inverted Y axis</p>
 * 
 * @author Tom Parker
 */
public class InvertedFrameRenderer extends BasicFrameRenderer {

  /** Constructor that accepts a LineData object.
   */
  public InvertedFrameRenderer() {
    super();
  }

  /** Gets a transformed y-pixel (Transformer implemenatation).
   * @param y the y-coordinate in user space
   * @return the y-pixel
   */
  public double getYPixel(double y) {
    // return graphHeight - ((y - minY) * getYScale()) + (double)graphY;
    return ((y - minY) * getYScale()) + (double) graphY;

  }

  /** Sets the axis to an empty Renderer
  */
  public void createEmptyAxis() {
    axis = new InvertedAxisRenderer(this);
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
      axis = new InvertedAxisRenderer(this);
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

    double[] translation = new double[] {dx, minX - dx * graphX, dy * -1,
        maxY + dy * (plotHeight - (graphHeight) - graphY), // <- how far the graph is from the
                                                           // bottom of the plot
        minX, maxX, minY, maxY};

    return translation;
  }
}
