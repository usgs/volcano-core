package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.legacy.plot.Plot;
import gov.usgs.volcanoes.core.math.Util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>An AxisRenderer is used to render the rectangular axis that surrounds a plot.
 * It includes tick marks, labels, grid lines, and the surrounding box. All
 * AxisRenderers must live inside a FrameRenderer.</p>
 *
 * @author Dan Cervelli
 */
public class AxisRenderer implements Renderer {
  private Color backgroundColor;
  private Color color;
  private RectangleRenderer frame;
  // private Renderer title;
  protected static NumberFormat numberFormat = DecimalFormat.getInstance();

  protected FrameRenderer frameRenderer;

  // axis labels
  private Renderer leftLabel;
  private Renderer topLabel;
  private Renderer rightLabel;
  private Renderer bottomLabel;

  private Renderer bottomLeftLabel;
  private Renderer bottomRightLabel;

  // ticks
  protected Renderer[] bottomTicks;
  protected Renderer[] bottomLabels;
  protected Renderer[] topTicks;
  protected Renderer[] topLabels;
  public Renderer[] leftTicks;
  private Renderer[] leftLabels;
  private Renderer[] rightTicks;
  private Renderer[] rightLabels;

  // grid
  private Renderer[] horizGridLines;
  private Renderer[] vertGridLines;

  // extra random renderers
  private List<Renderer> renderers;
  private List<Renderer> postRenderers;

  static {
    // this may not be too safe, I'm not sure if NumberFormat is
    // thread-safe.
    numberFormat.setMaximumFractionDigits(3);
  }

  /** Generic constructor.
   * @param fr the parent FrameRenderer
   */
  public AxisRenderer(FrameRenderer fr) {
    frameRenderer = fr;
    color = Color.black;
    renderers = new ArrayList<Renderer>();
    postRenderers = new ArrayList<Renderer>();
  }

  /**
   * Getter for renderer's frame
   */
  public RectangleRenderer getFrame() {
    return frame;
  }

  /** Gets the parent FrameRenderer that this AxisRenderer resides in.
   * @return the parent FrameRenderer
   */
  public FrameRenderer getFrameRenderer() {
    return frameRenderer;
  }

  /** Adds a random renderer.  These renderers will be drawn after all of 
   * the standard AxisRenderer components are drawn.
   * @param r the renderer
   */
  public void addRenderer(Renderer r) {
    renderers.add(r);
  }

  /** Adds a post renderer.  These renderers will be drawn with a call to 
   *  postRender.
   * @param r the renderer
   */
  public void addPostRenderer(Renderer r) {
    postRenderers.add(r);
  }

  /** Sets the color for all of this AxisRenderer's components.
   * @param c the color
   */
  public void setColor(Color c) {
    color = c;
  }

  /**
   * Create default AxisRenderer.  This just sets up the frame to be a 
   * rectangle surrounding the space the graph is going to take up.
   */
  public void createDefault() {
    if (frameRenderer != null) {
      if (frame == null)
        frame = new RectangleRenderer();
      frame.rect.x = frameRenderer.getGraphX();
      frame.rect.y = frameRenderer.getGraphY();
      frame.rect.width = frameRenderer.getGraphWidth();
      frame.rect.height = frameRenderer.getGraphHeight();
    }
  }

  /**
   * Creates 10-pixel tickmarks (no labels) for the bottom of the axis.
   * @param ticks the values that should have tickmarks
   */
  public void createBottomTicks(double[] ticks) {
    createBottomTicks(ticks, 10.0, Color.BLACK);
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
    double minY = frameRenderer.getMinY();
    // double maxY = frameRenderer.getMaxY();
    for (int i = 0; i < ticks.length; i++) {
      LineRenderer lr = new LineRenderer();
      lr.line = new Line2D.Double(frameRenderer.getXPixel(ticks[i]), frameRenderer.getYPixel(minY),
          frameRenderer.getXPixel(ticks[i]), frameRenderer.getYPixel(minY) - length);
      lr.color = color;
      lr.stroke = LineRenderer.TICK_STROKE;
      bottomTicks[i] = lr;
    }
  }

  /**
   * createBottomTicks() with default color
   * @param majorTicks the values that should have major tickmarks
   * @param minorTicks the values that should have minor tickmarks
   */
  public void createBottomTicks(double[] majorTicks, double[] minorTicks) {
    createBottomTicks(majorTicks, minorTicks, null);
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
    double minY = frameRenderer.getMinY();
    // double maxY = frameRenderer.getMaxY();
    int i = 0;
    if (majorTicks != null) {
      for (int j = 0; j < majorTicks.length; j++) {
        ShapeRenderer sr =
            new ShapeRenderer(new Line2D.Double(frameRenderer.getXPixel(majorTicks[j]),
                frameRenderer.getYPixel(minY), frameRenderer.getXPixel(majorTicks[j]),
                frameRenderer.getYPixel(minY) - majorLength));
        if (color != null)
          sr.color = color;
        bottomTicks[i++] = sr;

      }
    }
    if (minorTicks != null) {
      for (int j = 0; j < minorTicks.length; j++) {
        ShapeRenderer sr =
            new ShapeRenderer(new Line2D.Double(frameRenderer.getXPixel(minorTicks[j]),
                frameRenderer.getYPixel(minY), frameRenderer.getXPixel(minorTicks[j]),
                frameRenderer.getYPixel(minY) - minorLength));

        if (color != null)
          sr.color = color;
        bottomTicks[i++] = sr;
      }
    }
  }

  /** Creates tickmark labels for the bottom of the axis with default color.
   * @param ticks the values that should have labels (used for locating label)
   * @param labels the label strings
   */
  public void createBottomTickLabels(double[] ticks, String[] labels) {
    createBottomTickLabels(ticks, labels, null);
  }

  /** Creates tickmark labels for the bottom of the axis.
   * @param ticks the values that should have labels (used for locating label)
   * @param labels the label strings
   * @param color Label color
   */
  public void createBottomTickLabels(double[] ticks, String[] labels, Color color) {
    bottomLabels = new Renderer[ticks.length];
    double minY = frameRenderer.getMinY();
    // double maxY = frameRenderer.getMaxY();
    for (int i = 0; i < ticks.length; i++) {
      TextRenderer tr = new TextRenderer();
      if (labels != null)
        tr.text = labels[i];
      else
        tr.text = numberFormat.format(ticks[i]);
      tr.x = (float) frameRenderer.getXPixel(ticks[i]);
      tr.y = (float) frameRenderer.getYPixel(minY);
      tr.horizJustification = TextRenderer.CENTER;
      tr.vertJustification = TextRenderer.TOP;
      if (color != null)
        tr.color = color;
      // tr.transformer = frameRenderer;
      bottomLabels[i] = tr;
    }
  }

  /**
   * Creates tickmark labels for the bottom of the axis, adjusting for longitudes 
   * < -180.
   * @param ticks the values that should have labels (used for locating label)
   * @param labels the label strings
   */
  public void createBottomLongitudeTickLabels(double[] ticks, String[] labels) {
    bottomLabels = new Renderer[ticks.length];
    double minY = frameRenderer.getMinY();
    // double maxY = frameRenderer.getMaxY();
    for (int i = 0; i < ticks.length; i++) {
      TextRenderer tr = new TextRenderer();
      if (labels != null)
        tr.text = labels[i];
      else {
        double t = ticks[i];
        if (t < -180)
          t = 180 + (t + 180);
        tr.text = numberFormat.format(t);
      }
      tr.x = (float) frameRenderer.getXPixel(ticks[i]);
      tr.y = (float) frameRenderer.getYPixel(minY);
      tr.horizJustification = TextRenderer.CENTER;
      tr.vertJustification = TextRenderer.TOP;
      // tr.transformer = frameRenderer;
      bottomLabels[i] = tr;
    }
  }

  /** 
   * Creates tickmarks for the top of the axis.
   * @param ticks the values that should have tickmarks
   */
  public void createTopTicks(double[] ticks) {
    createTopTicks(ticks, 10.0, Color.BLACK);
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
    double maxY = frameRenderer.getMaxY();
    for (int i = 0; i < ticks.length; i++) {
      LineRenderer lr = new LineRenderer();
      lr.line = new Line2D.Double(frameRenderer.getXPixel(ticks[i]), frameRenderer.getYPixel(maxY),
          frameRenderer.getXPixel(ticks[i]), frameRenderer.getYPixel(maxY) + length);
      lr.color = color;
      lr.stroke = LineRenderer.TICK_STROKE;
      topTicks[i] = lr;
    }
  }

  /**
   * Creates user-specified size tickmarks (no labels) for the top of the
   * axis with default color
   * 
   * @param majorTicks the values that should have major tickmarks
   * @param minorTicks the values that should have minor tickmarks
   */
  public void createTopTicks(double[] majorTicks, double[] minorTicks) {
    createTopTicks(majorTicks, minorTicks, null);
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
    double maxY = frameRenderer.getMaxY();
    int i = 0;
    if (majorTicks != null) {
      for (int j = 0; j < majorTicks.length; j++) {
        ShapeRenderer sr =
            new ShapeRenderer(new Line2D.Double(frameRenderer.getXPixel(majorTicks[j]),
                frameRenderer.getYPixel(maxY), frameRenderer.getXPixel(majorTicks[j]),
                frameRenderer.getYPixel(maxY) + majorLength));
        if (color != null)
          sr.color = color;
        topTicks[i++] = sr;
      }
    }
    if (minorTicks != null) {
      for (int j = 0; j < minorTicks.length; j++) {
        ShapeRenderer sr =
            new ShapeRenderer(new Line2D.Double(frameRenderer.getXPixel(minorTicks[j]),
                frameRenderer.getYPixel(maxY), frameRenderer.getXPixel(minorTicks[j]),
                frameRenderer.getYPixel(maxY) + minorLength));
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
      tr.vertJustification = TextRenderer.BOTTOM;
      topLabels[i] = tr;
    }
  }

  /** 
   * Creates black 10-pixel tickmarks for the left side of the axis.
   * @param ticks the values that should have tickmarks
   */
  public void createLeftTicks(double[] ticks) {
    createLeftTicks(ticks, 10.0, Color.BLACK);
  }

  /**
   * Creates tickmarks for the left side of the axis.
   * @param ticks
   * @param width tick width in pixel
   * @param color mark's color
   */
  public void createLeftTicks(double[] ticks, double width, Color color) {
    // int length = 10;
    if (leftTicks == null || leftTicks.length != ticks.length)
      leftTicks = new Renderer[ticks.length];
    double minX = frameRenderer.getMinX();
    // double maxX = frameRenderer.getMaxX();
    for (int i = 0; i < ticks.length; i++) {
      double value = ticks[i];
      ShapeRenderer sr = new ShapeRenderer(
          new Line2D.Double(frameRenderer.getXPixel(minX), frameRenderer.getYPixel(value),
              frameRenderer.getXPixel(minX) + width, frameRenderer.getYPixel(value)));
      sr.color = color;
      sr.stroke = LineRenderer.TICK_STROKE;
      leftTicks[i] = sr;
    }
  }

  public void createFormattedLeftTickLabels(double[] ticks, String format) {
    leftLabels = new Renderer[ticks.length];
    double minX = frameRenderer.getMinX();
    for (int i = 0; i < ticks.length; i++) {
      TextRenderer tr = new TextRenderer();
      tr.text = String.format(format, ticks[i]);
      tr.x = (float) frameRenderer.getXPixel(minX) - 2;
      tr.y = (float) frameRenderer.getYPixel(ticks[i]);
      tr.horizJustification = TextRenderer.RIGHT;
      tr.vertJustification = TextRenderer.CENTER;
      // tr.transformer = frameRenderer;
      leftLabels[i] = tr;
    }
  }

  /** 
   * Creates tickmark labels for the left side of the axis.
   * @param ticks that values that should have labels (used for locating label)
   * @param labels the label strings
   */
  public void createLeftTickLabels(double[] ticks, String[] labels) {
    createLeftTickLabels(ticks, labels, null);
  }

  /** 
   * Creates tickmark labels for the left side of the axis.
   * @param ticks that values that should have labels (used for locating label)
   * @param labels the label strings
   * @param color Label color
   */
  public void createLeftTickLabels(double[] ticks, String[] labels, Color color) {
    if (labels == null) {
      labels = new String[ticks.length];
      double min = 1E300;
      double max = -1E300;
      for (int i = 0; i < ticks.length; i++) {
        if (ticks[i] > max)
          max = ticks[i];
        if (ticks[i] < min)
          min = ticks[i];
      }
      max = Math.max(Math.abs(max), Math.abs(min));
      double exp = Util.getExp(max);
      boolean reformat = (exp >= 5 || exp <= -4);
      for (int i = 0; i < ticks.length; i++) {
        labels[i] = (reformat ? numberFormat.format(ticks[i] / Math.pow(10, exp))
            : numberFormat.format(ticks[i]));
      }
      if (reformat) {
        TextRenderer tr = new TextRenderer();
        tr.text = "x 10^" + numberFormat.format(exp);
        tr.x = (float) frameRenderer.getXPixel(frameRenderer.getMinX()) - 55;
        tr.y = (float) frameRenderer.getYPixel(frameRenderer.getMinY()) - 10;
        tr.horizJustification = TextRenderer.LEFT;
        tr.vertJustification = TextRenderer.CENTER;
        if (color != null)
          tr.color = color;
        // tr.transformer = frameRenderer;
        addRenderer(tr);
      }
    }
    leftLabels = new Renderer[ticks.length];
    double minX = frameRenderer.getMinX();
    for (int i = 0; i < ticks.length; i++) {
      if (labels[i] != null) {
        TextRenderer tr = new TextRenderer();
        tr.text = labels[i];
        tr.x = (float) frameRenderer.getXPixel(minX) - 2;
        tr.y = (float) frameRenderer.getYPixel(ticks[i]);
        tr.horizJustification = TextRenderer.RIGHT;
        tr.vertJustification = TextRenderer.CENTER;
        if (color != null)
          tr.color = color;
        // tr.transformer = frameRenderer;
        leftLabels[i] = tr;
      }
    }
  }

  /** 
   * Creates tickmarks for the right side of the axis.
   * @param ticks the values that should have tickmarks
   */
  public void createRightTicks(double[] ticks) {
    createRightTicks(ticks, true, 10.0, Color.BLACK);
  }

  /** 
   * Creates tickmark labels for the right side of the axis.
   * @param ticks that values that should have labels (used for locating label)
   * @param isYTicks "these are tickmarks for Y axis"
   * @param width label width
   * @param color Label color
   */
  public void createRightTicks(double[] ticks, boolean isYTicks, double width, Color color) {

    if (rightTicks == null || rightTicks.length != ticks.length)
      rightTicks = new Renderer[ticks.length];
    // double minX = frameRenderer.getMinX();
    double maxX = frameRenderer.getMaxX();
    if (isYTicks) {
      for (int i = 0; i < ticks.length; i++) {
        double value = ticks[i];
        ShapeRenderer sr = new ShapeRenderer(
            new Line2D.Double(frameRenderer.getXPixel(maxX) - width, frameRenderer.getYPixel(value),
                frameRenderer.getXPixel(maxX), frameRenderer.getYPixel(value)));
        sr.color = color;
        sr.stroke = LineRenderer.TICK_STROKE;
        rightTicks[i] = sr;
      }
    }
  }

  /** 
   * Creates tickmark labels for the right side of the axis.
   * 
   * @param ticks that values that should have labels (used for locating label)
   * @param labels the label strings
   */
  public void createRightTickLabels(double[] ticks, String[] labels) {
    if (labels == null) {
      labels = new String[ticks.length];
      double min = 1E300;
      double max = -1E300;
      for (int i = 0; i < ticks.length; i++) {
        if (ticks[i] > max)
          max = ticks[i];
        if (ticks[i] < min)
          min = ticks[i];
      }
      max = Math.max(Math.abs(max), Math.abs(min));
      double exp = Util.getExp(max);
      boolean reformat = (exp >= 5 || exp <= -4);
      for (int i = 0; i < ticks.length; i++) {
        labels[i] = (reformat ? numberFormat.format(ticks[i] / Math.pow(10, exp))
            : numberFormat.format(ticks[i]));
      }
      if (reformat) {
        TextRenderer tr = new TextRenderer();
        tr.text = "x 10^" + numberFormat.format(exp);
        tr.x = (float) frameRenderer.getXPixel(frameRenderer.getMaxX()) + 15;
        tr.y = (float) frameRenderer.getYPixel(frameRenderer.getMinY()) - 4;
        tr.horizJustification = TextRenderer.LEFT;
        tr.vertJustification = TextRenderer.CENTER;
        // tr.transformer = frameRenderer;
        addRenderer(tr);
      }
    }
    rightLabels = new Renderer[ticks.length];
    double maxX = frameRenderer.getMaxX();
    for (int i = 0; i < ticks.length; i++) {
      TextRenderer tr = new TextRenderer();
      tr.text = labels[i];
      tr.x = (float) frameRenderer.getXPixel(maxX) + 2;
      tr.y = (float) frameRenderer.getYPixel(ticks[i]);
      tr.horizJustification = TextRenderer.LEFT;
      tr.vertJustification = TextRenderer.CENTER;
      // tr.transformer = frameRenderer;
      rightLabels[i] = tr;
    }
  }

  /**
   * Creates horizontal grid lines in the graph.  These are dashed lines.
   * 
   * @param lines the values where the lines should be placed
   */
  public void createHorizontalGridLines(double[] lines) {
    // Stroke stroke = new BasicStroke(
    // 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
    // new float[] {1.0f, 2.0f}, 0.0f);
    createHorizontalGridLines(lines, Color.GRAY, LineRenderer.DASHED_STROKE);
  }

  /**
   * Creates horizontal grid lines in the graph.
   * 
   * @param lines the values where the lines should be placed
   * @param color line color
   * @param stroke line stroke
   */
  public void createHorizontalGridLines(double[] lines, Color color, Stroke stroke) {
    horizGridLines = new Renderer[lines.length];
    double minX = frameRenderer.getMinX();
    double maxX = frameRenderer.getMaxX();

    for (int i = 0; i < lines.length; i++) {
      double value = lines[i];
      LineRenderer lr = new LineRenderer();
      lr.line = new Line2D.Double(frameRenderer.getXPixel(minX), frameRenderer.getYPixel(value),
          frameRenderer.getXPixel(maxX), frameRenderer.getYPixel(value));
      lr.stroke = stroke;
      lr.color = color;
      horizGridLines[i] = lr;
    }
  }

  /**
   * Creates vertical grid lines in the graph.  These are dashed lines.
   * 
   * @param lines the values where the lines should be placed
   */
  public void createVerticalGridLines(double[] lines) {
    // Stroke stroke = new BasicStroke(
    // 1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,
    // new float[] {1.0f, 2.0f}, 0.0f);
    createVerticalGridLines(lines, Color.GRAY, LineRenderer.DASHED_STROKE);
  }

  /**
   * Creates vertical grid lines in the graph.
   * 
   * @param lines the values where the lines should be placed
   * @param color line color
   * @param stroke line stroke
   */
  public void createVerticalGridLines(double[] lines, Color color, Stroke stroke) {
    vertGridLines = new Renderer[lines.length];
    double minY = frameRenderer.getMinY();
    double maxY = frameRenderer.getMaxY();

    for (int i = 0; i < lines.length; i++) {
      double value = lines[i];
      LineRenderer lr = new LineRenderer();
      lr.line = new Line2D.Double(frameRenderer.getXPixel(value), frameRenderer.getYPixel(minY),
          frameRenderer.getXPixel(value), frameRenderer.getYPixel(maxY));
      lr.stroke = stroke;
      lr.color = color;
      vertGridLines[i] = lr;
    }
  }

  /** 
   * Sets the renderer of the top label (title) of the axis.
   * 
   * @param r the Renderer for the top label
   */
  public void setTopLabel(Renderer r) {
    topLabel = r;
  }

  /** 
   * Sets the renderer of the top label (title) from a string.
   * 
   * @param s the title string
   */
  public void setTopLabelAsText(String s) {
    TextRenderer tr = new TextRenderer();
    tr.text = s;
    tr.x = (float) frameRenderer.getGraphWidth() / 2 + (float) frameRenderer.getGraphX();
    tr.y = -4 + (float) frameRenderer.getGraphY();
    tr.horizJustification = TextRenderer.CENTER;
    tr.vertJustification = TextRenderer.BOTTOM;
    // tr.transformer = frameRenderer;
    topLabel = tr;
  }

  /** 
   * Sets the renderer of the bottom label of the axis.
   * 
   * @param r the Renderer for the bottom label
   */
  public void setBottomLabel(Renderer r) {
    bottomLabel = r;
  }

  /** 
   * Sets the renderer of the bottom label from a string.  This label
   * is centered on the bottom axis.
   *  
   * @param s the bottom string
   */
  public TextRenderer setBottomLabelAsText(String s) {
    TextRenderer tr = new TextRenderer();
    tr.text = s;
    tr.x = (float) frameRenderer.getGraphWidth() / 2 + (float) frameRenderer.getGraphX();
    tr.y = (float) frameRenderer.getGraphHeight() + 24 + (float) frameRenderer.getGraphY();
    tr.horizJustification = TextRenderer.CENTER;
    tr.vertJustification = TextRenderer.CENTER;
    // tr.transformer = frameRenderer;
    bottomLabel = tr;
    return tr;
  }

  /** 
   * @param s the inner left
   */
  public void setInnerLeftLabelAsText(String s, float x) {
    TextRenderer tr = new TextRenderer();
    tr.text = s;
    tr.orientation = 270.0f;
    tr.x = x + (float) frameRenderer.getGraphX();
    tr.y = (float) frameRenderer.getGraphHeight() / 2 + (float) frameRenderer.getGraphY();
    tr.horizJustification = TextRenderer.CENTER;
    tr.vertJustification = TextRenderer.CENTER;
    // tr.transformer = frameRenderer;
    bottomLeftLabel = tr;
  }


  /** 
   * Sets the renderer of the bottom left label from a string.  This label
   * is located on the left side of the bottom axis.
   *  
   * @param s the bottom string
   */
  public void setBottomLeftLabelAsText(String s) {
    setBottomLeftLabelAsText(s, 0, 24);
  }

  /** 
   * Sets the renderer of the bottom left label from a string.  This label
   * is located on the left side of the bottom axis.
   *  
   * @param s the bottom string
   */
  public void setBottomLeftLabelAsText(String s, float x, float y) {
    TextRenderer tr = new TextRenderer();
    tr.text = s;
    tr.x = (float) frameRenderer.getGraphX() + x;
    tr.y = (float) frameRenderer.getGraphHeight() + y + (float) frameRenderer.getGraphY();
    tr.horizJustification = TextRenderer.RIGHT;
    tr.vertJustification = TextRenderer.CENTER;
    // tr.transformer = frameRenderer;
    bottomLeftLabel = tr;
  }

  /** 
   * Sets the renderer of the bottom right label from a string.  This label
   * is located on the right side of the bottom axis.
   *  
   * @param s the bottom string
   */
  public void setBottomRightLabelAsText(String s) {
    TextRenderer tr = new TextRenderer();
    tr.text = s;
    tr.x = (float) frameRenderer.getGraphX() + (float) frameRenderer.getGraphWidth();
    tr.y = (float) frameRenderer.getGraphHeight() + 24 + (float) frameRenderer.getGraphY();
    tr.horizJustification = TextRenderer.LEFT;
    tr.vertJustification = TextRenderer.CENTER;
    // tr.transformer = frameRenderer;
    bottomRightLabel = tr;
  }

  public Renderer getBottomLabel() {
    return bottomLabel;
  }

  public Renderer getBottomLeftRenderer() {
    return bottomLeftLabel;
  }

  /** 
   * Gets the left label Renderer.
   * 
   * @return the left label Renderer
   */
  public Renderer getLeftLabel() {
    return leftLabel;
  }

  /** 
   * Sets the renderer of the left label of the axis.
   * 
   * @param r the Renderer for left bottom label
   */
  public void setLeftLabel(Renderer r) {
    leftLabel = r;
  }

  /** 
   * Sets the renderer of the left label from a string.  This will be 
   * displayed vertically (reading up) along the left side of the axis.
   * This uses a default value of -45 for the x offset, and black for the color.
   * @param s the left string
   */
  public void setLeftLabelAsText(String s) {
    setLeftLabelAsText(s, Color.black);
  }

  /** 
   * Sets the renderer of the left label from a string.  This will be 
   * displayed vertically (reading up) along the left side of the axis.
   * This uses a default value of black for the color.
   * @param s the left string
   * @param x the left offset (negative moves to left)
   */
  public void setLeftLabelAsText(String s, float x) {
    setLeftLabelAsText(s, x, Color.black);
  }

  /** 
   * Sets the renderer of the left label from a string.  This will be 
   * displayed vertically (reading up) along the left side of the axis.
   * This uses a default value of -45 for the x offset.
   * @param s the left string
   * @param c color
   */
  public void setLeftLabelAsText(String s, Color c) {
    setLeftLabelAsText(s, -45, c);
  }

  /** 
   * Sets the renderer of the left label from a string.  This will be 
   * displayed vertically (reading up) along the left side of the axis.
   * @param s the left string
   * @param x the left offset (negative moves to left)
   * @param c color
   */
  public void setLeftLabelAsText(String s, float x, Color c) {
    TextRenderer tr = new TextRenderer();
    tr.text = s;
    tr.orientation = 270.0f;
    tr.color = c;
    tr.x = x + (float) frameRenderer.getGraphX();
    tr.y = (float) frameRenderer.getGraphHeight() / 2 + (float) frameRenderer.getGraphY();
    tr.horizJustification = TextRenderer.CENTER;
    tr.vertJustification = TextRenderer.CENTER;
    // tr.transformer = frameRenderer;
    leftLabel = tr;
  }

  /** 
   * Gets the right label Renderer.
   * @return the right label Renderer
   */
  public Renderer getRightLabel() {
    return rightLabel;
  }

  /** 
   * Sets the renderer of the right label of the axis.
   * @param r the Renderer for right bottom label
   */
  public void setRightLabel(Renderer r) {
    rightLabel = r;
  }

  /** 
   * Sets the renderer of the right label from a string.  This will be 
   * displayed vertically (reading down) along the right side of the axis.
   * Default color of black is used.
   * @param s the right string
   */
  public void setRightLabelAsText(String s) {
    setRightLabelAsText(s, Color.black);
  }

  /** 
   * Sets the renderer of the right label from a string.  This will be 
   * displayed vertically (reading down) along the right side of the axis.
   * @param s the right string
   * @param c color
   */
  public void setRightLabelAsText(String s, Color c) {
    TextRenderer tr = new TextRenderer();
    tr.text = s;
    tr.orientation = 270.0f;
    tr.x = (float) frameRenderer.getGraphWidth() + 45 + (float) frameRenderer.getGraphX();
    tr.y = (float) frameRenderer.getGraphHeight() / 2 + (float) frameRenderer.getGraphY();
    tr.horizJustification = TextRenderer.CENTER;
    tr.vertJustification = TextRenderer.CENTER;
    tr.color = c;
    rightLabel = tr;
  }

  /** 
   * Sets the frame (the rectangular box) around the axis.
   * @param r the rectangle
   */
  public void setFrame(RectangleRenderer r) {
    frame = r;
  }

  /** 
   * Sets the background color of this axis (everything inside the frame).
   * @param c the background color
   */
  public void setBackgroundColor(Color c) {
    backgroundColor = c;
  }

  /**
   * Yield the background color
   * @return background color
   */
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Setter for left label renderers
   * @param r renderers for left labels
   */
  public void setLeftLabels(Renderer[] r) {
    leftLabels = r;
  }

  /**
   * Getter for left label renderers
   * @return renderers for left labels
   */
  public Renderer[] getLeftLabels() {
    return leftLabels;
  }

  /**
   * Setter for bottom label renderers
   * @param r renderers for bottom labels
   */
  public void setBottomLabels(Renderer[] r) {
    bottomLabels = r;
  }

  /** 
   * Renderers this axis.  Renderering order:<br>
   * 1. Fill background<br>
   * 2. Grid lines<br>
   * 3. Tickmarks<br>
   * 4. Frame<br>
   * 5. Tickmark labels<br>
   * 6. Axis labels<br>
   * 7. Miscellaneous Renderers<br>
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {
    Color origColor = g.getColor();

    // plot background color
    if (backgroundColor != null) {
      Paint origPaint = g.getPaint();
      g.setPaint(backgroundColor);
      g.fill(new Rectangle(frameRenderer.getGraphX(), frameRenderer.getGraphY(),
          frameRenderer.getGraphWidth(), frameRenderer.getGraphHeight()));
      g.setPaint(origPaint);
    }

    if (color != null)
      g.setColor(Color.black);

    // grid lines on bottom
    Plot.renderArray(g, horizGridLines);
    Plot.renderArray(g, vertGridLines);

    // then tick marks
    Plot.renderArray(g, bottomTicks);
    Plot.renderArray(g, topTicks);
    Plot.renderArray(g, leftTicks);
    Plot.renderArray(g, rightTicks);

    // then frame
    if (frame != null)
      frame.render(g);

    // then labels
    Plot.renderArray(g, bottomLabels);
    Plot.renderArray(g, leftLabels);
    Plot.renderArray(g, topLabels);
    Plot.renderArray(g, rightLabels);

    // then axis labels
    if (topLabel != null)
      topLabel.render(g);
    if (leftLabel != null)
      leftLabel.render(g);
    if (bottomLabel != null)
      bottomLabel.render(g);
    if (rightLabel != null)
      rightLabel.render(g);
    if (bottomLeftLabel != null)
      bottomLeftLabel.render(g);
    if (bottomRightLabel != null)
      bottomRightLabel.render(g);

    for (Renderer renderer : renderers)
      renderer.render(g);

    g.setColor(origColor);
  }

  /**
   * Final renderers for this axis.
   * @param g the graphics object upon which to render
   */
  public void postRender(Graphics2D g) {
    for (Renderer renderer : postRenderers)
      renderer.render(g);
  }
}
