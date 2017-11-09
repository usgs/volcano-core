package gov.usgs.volcanoes.core.legacy.plot.render;

import java.awt.Color;
import java.awt.geom.Line2D;


/**
 * Intended to be used on plots with an inverted Y axis
 * 
 * @author Tom Parker
 */
public class InvertedAxisRenderer extends AxisRenderer {

  public InvertedAxisRenderer(FrameRenderer fr) {
    super(fr);
  }

  /**
   * Creates user-specified size tickmarks (no labels) for the bottom of the
   * axis.
   * 
   * @param ticks the values that should have tickmarks
   * @param length the length of the tickmarks in pixels
   */
  public void createBottomTicks(double[] ticks, double length, Color color) {
    if (bottomTicks == null || bottomTicks.length != ticks.length)
      bottomTicks = new Renderer[ticks.length];
    double maxY = frameRenderer.getMaxY();
    for (int i = 0; i < ticks.length; i++) {
      LineRenderer lr = new LineRenderer();
      lr.line = new Line2D.Double(frameRenderer.getXPixel(ticks[i]), frameRenderer.getYPixel(maxY),
          frameRenderer.getXPixel(ticks[i]), frameRenderer.getYPixel(maxY) - length);
      lr.color = color;
      lr.stroke = LineRenderer.TICK_STROKE;
      bottomTicks[i] = lr;
    }
  }

  /**
   * Creates user-specified size tickmarks (no labels) for the bottom of the
   * axis.
   * @param majorTicks the values that should have major tickmarks
   * @param minorTicks the values that should have minor tickmarks
   * @param color Mark color
   */
  public void createBottomTicks(double[] majorTicks, double[] minorTicks, Color color) {
    int numTicks = 0;
    if (majorTicks != null)
      numTicks += majorTicks.length;
    if (minorTicks != null)
      numTicks += minorTicks.length;

    double majorLength = 10.0;
    double minorLength = 5.0;
    bottomTicks = new Renderer[numTicks];
    double maxY = frameRenderer.getMaxY();
    int i = 0;
    if (majorTicks != null) {
      for (int j = 0; j < majorTicks.length; j++) {
        ShapeRenderer sr =
            new ShapeRenderer(new Line2D.Double(frameRenderer.getXPixel(majorTicks[j]),
                frameRenderer.getYPixel(maxY), frameRenderer.getXPixel(majorTicks[j]),
                frameRenderer.getYPixel(maxY) - majorLength));
        if (color != null)
          sr.color = color;
        bottomTicks[i++] = sr;

      }
    }
    if (minorTicks != null) {
      for (int j = 0; j < minorTicks.length; j++) {
        ShapeRenderer sr =
            new ShapeRenderer(new Line2D.Double(frameRenderer.getXPixel(minorTicks[j]),
                frameRenderer.getYPixel(maxY), frameRenderer.getXPixel(minorTicks[j]),
                frameRenderer.getYPixel(maxY) - minorLength));

        if (color != null)
          sr.color = color;
        bottomTicks[i++] = sr;
      }
    }
  }

  /** Creates tickmark labels for the bottom of the axis.
   * @param ticks the values that should have labels (used for locating label)
   * @param labels the label strings
   * @param color Label color
   */
  public void createBottomTickLabels(double[] ticks, String[] labels, Color color) {
    bottomLabels = new Renderer[ticks.length];
    double maxY = frameRenderer.getMaxY();
    for (int i = 0; i < ticks.length; i++) {
      TextRenderer tr = new TextRenderer();
      if (labels != null)
        tr.text = labels[i];
      else
        tr.text = numberFormat.format(ticks[i]);
      tr.x = (float) frameRenderer.getXPixel(ticks[i]);
      tr.y = (float) frameRenderer.getYPixel(maxY);
      tr.horizJustification = TextRenderer.CENTER;
      tr.vertJustification = TextRenderer.TOP;
      if (color != null)
        tr.color = color;
      bottomLabels[i] = tr;
    }
  }

  /**
   * Creates user-specified size tickmarks (no labels) for the top of the
   * axis.
   * 
   * @param ticks the values that should have tickmarks
   * @param length the length of the tickmarks in pixels
   * @param color Tick color
   */
  public void createTopTicks(double[] ticks, double length, Color color) {
    if (topTicks == null || topTicks.length != ticks.length)
      topTicks = new Renderer[ticks.length];

    double minY = frameRenderer.getMinY();
    for (int i = 0; i < ticks.length; i++) {
      LineRenderer lr = new LineRenderer();
      lr.line = new Line2D.Double(frameRenderer.getXPixel(ticks[i]), frameRenderer.getYPixel(minY),
          frameRenderer.getXPixel(ticks[i]), frameRenderer.getYPixel(minY) + length);
      lr.color = color;
      lr.stroke = LineRenderer.TICK_STROKE;
      topTicks[i] = lr;
    }
  }

  /**
   * Creates user-specified size tickmarks (no labels) for the top of the
   * axis.
   * @param majorTicks the values that should have major tickmarks
   * @param minorTicks the values that should have minor tickmarks
   * @param color Mark color
   */
  public void createTopTicks(double[] majorTicks, double[] minorTicks, Color color) {
    int numTicks = 0;
    if (majorTicks != null)
      numTicks += majorTicks.length;
    if (minorTicks != null)
      numTicks += minorTicks.length;

    double majorLength = 10.0;
    double minorLength = 5.0;
    topTicks = new Renderer[numTicks];
    double minY = frameRenderer.getMinY();
    int i = 0;
    if (majorTicks != null) {
      for (int j = 0; j < majorTicks.length; j++) {
        ShapeRenderer sr =
            new ShapeRenderer(new Line2D.Double(frameRenderer.getXPixel(majorTicks[j]),
                frameRenderer.getYPixel(minY), frameRenderer.getXPixel(majorTicks[j]),
                frameRenderer.getYPixel(minY) + majorLength));
        if (color != null)
          sr.color = color;
        topTicks[i++] = sr;
      }
    }
    if (minorTicks != null) {
      for (int j = 0; j < minorTicks.length; j++) {
        ShapeRenderer sr =
            new ShapeRenderer(new Line2D.Double(frameRenderer.getXPixel(minorTicks[j]),
                frameRenderer.getYPixel(minY), frameRenderer.getXPixel(minorTicks[j]),
                frameRenderer.getYPixel(minY) + minorLength));
        if (color != null)
          sr.color = color;
        topTicks[i++] = sr;
      }
    }
  }

  /** 
   * Creates tickmark labels for the top of the axis.
   * @param ticks that values that should have labels (used for locating label)
   * @param labels the label strings
   */
  public void createTopTickLabels(double[] ticks, String[] labels) {
    topLabels = new Renderer[ticks.length];
    double minY = frameRenderer.getMinY();
    for (int i = 0; i < ticks.length; i++) {
      TextRenderer tr = new TextRenderer();
      if (labels != null)
        tr.text = labels[i];
      else
        tr.text = numberFormat.format(ticks[i]);
      tr.x = (float) frameRenderer.getXPixel(ticks[i]);
      tr.y = (float) frameRenderer.getYPixel(minY);
      tr.horizJustification = TextRenderer.CENTER;
      tr.vertJustification = TextRenderer.BOTTOM;
      topLabels[i] = tr;
    }
  }

}
