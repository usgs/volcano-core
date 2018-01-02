package gov.usgs.volcanoes.core.legacy.plot.render.wave;

import gov.usgs.volcanoes.core.data.SliceWave;
import gov.usgs.volcanoes.core.data.Wave;
import gov.usgs.volcanoes.core.legacy.plot.decorate.DefaultFrameDecorator;
import gov.usgs.volcanoes.core.legacy.plot.decorate.FrameDecorator;
import gov.usgs.volcanoes.core.legacy.plot.render.FrameRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.LegendRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.ShapeRenderer;
import gov.usgs.volcanoes.core.time.J2kSec;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;

/**
 * A renderer for slice of wave time series.
 *
 * @author Dan Cervelli
 */
public class SliceWaveRenderer extends FrameRenderer {
  protected SliceWave wave;

  protected boolean removeBias = false;
  protected boolean drawSamples = false;

  protected double highlightX1;
  protected double highlightX2;
  protected double viewStartTime;
  protected double viewEndTime;
  protected String timeZone;
  protected String dateFormatString = "yyyy-MM-dd HH:mm:ss";

  protected Color color = Color.BLUE;

  protected String yLabelText = null;
  protected String yUnitText = null;

  protected String title;
  protected Date date;

  protected FrameDecorator decorator;

  public boolean xTickMarks = true;
  public boolean xTickValues = true;
  public boolean xUnits = true;
  public boolean xLabel = false;
  public boolean yTickMarks = true;
  public boolean yTickValues = true;

  /**
   * Set frame decorator to draw graph's frame.
   * @param fd frame decorator
   */
  public void setFrameDecorator(FrameDecorator fd) {
    decorator = fd;
  }

  /**
   * Set highlighted zone.
   * @param x1 minimum x
   * @param x2 maximum X
   */
  public void setHighlight(double x1, double x2) {
    highlightX1 = x1;
    highlightX2 = x2;
  }

  /**
   * Get maximum Y value.
   * @return maximum y value
   */
  public double getMaxY() {
    return maxY;
  }

  /**
   * Set maximum Y value.
   * @param maxY maximum Y value
   */
  public void setMaxY(double maxY) {
    this.maxY = maxY;
  }

  /**
   * Get minimum Y value.
   * @return minimum Y value
   */
  public double getMinY() {
    return minY;
  }

  /**
   * Set minimum Y value.
   * @param minY minimum Y value
   */
  public void setMinY(double minY) {
    this.minY = minY;
  }

  /**
   * Get demean flag.
   * @return demean flag
   */
  public boolean isRemoveBias() {
    return removeBias;
  }


  /**
   * Set limits on Y axis.
   * @param min new Y minimum
   * @param max new Y maximum
   */
  public void setYLimits(double min, double max) {
    minY = min;
    maxY = max;
  }

  /**
   * Set demean flag.
   * @param b new demean flag
   */
  public void setRemoveBias(boolean b) {
    removeBias = b;
  }

  /**
   * Set draw samples flag.
   * @param b draw samples flag
   */
  public void setDrawSamples(boolean b) {
    drawSamples = b;
  }

  /**
   * Set slice to render.
   * @param w slice to render
   */
  public void setWave(SliceWave w) {
    wave = w;
  }

  /**
   * Set limits on time axis.
   * @param t1 start time
   * @param t2 end time
   */
  public void setViewTimes(double t1, double t2, String timeZone) {
    viewStartTime = t1;
    viewEndTime = t2;
    this.timeZone = timeZone;
  }

  /**
   * Set limits on time axis from wave.
   * @param timeZone
   */
  public void setViewTimes(String timeZone) {
    setViewTimes(wave.getStartTime(), wave.getEndTime(), timeZone);
  }

  /**
   * Set color.
   * @param c color
   */
  public void setColor(Color c) {
    if (c != null) {
      color = c;
    }
  }

  /**
   * Get color.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Set Y axis label.
   * @param s Y axis label
   */
  public void setYLabelText(String s) {
    yLabelText = s;
  }

  /**
   * Set Y axis unit.
   * @param s Y axis unit
   */
  public void setYUnitText(String s) {
    yUnitText = s;
  }

  /**
   * Set graph title.
   * @param s graph title
   */
  public void setTitle(String s) {
    title = s.split("\\.")[0];
  }

  /**
   * Set date for display on graphic.
   * @param date date to display on graphic
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Create default decorator to render frame.
   */
  public void createDefaultFrameDecorator() {
    decorator = new DefaultWaveFrameDecorator();
  }

  /** Creates a standard legend, a small line and point sample followed by
   * the specified names.
   * @param s the legend names
   */
  public void createDefaultLegendRenderer(String[] s) {
    setLegendRenderer(new LegendRenderer());
    getLegendRenderer().x = graphX + 6;
    getLegendRenderer().y = graphY + 6;
    ShapeRenderer sr = new ShapeRenderer(new GeneralPath(GeneralPath.WIND_NON_ZERO, 1));
    sr.antiAlias = true;
    sr.color = color;
    sr.stroke = new BasicStroke();
    for (int i = 0; i < s.length; i++) {
      if (s[i] != null) {
        getLegendRenderer().addLine(sr, null, s[i]);
      }
    }
  }

  protected class DefaultWaveFrameDecorator extends DefaultFrameDecorator {
    public DefaultWaveFrameDecorator() {
      if (yUnitText != null) {
        this.yUnit = yUnitText;
      }
      if (xUnits) {
        this.xUnit = timeZone + " Time (" + J2kSec.format(dateFormatString, viewStartTime) + " to "
            + J2kSec.format(dateFormatString, viewEndTime) + ")";
      }
      if (yLabelText != null) {
        this.yAxisLabel = yLabelText;
      }
      this.xAxisLabels = xTickValues;
      this.yAxisLabels = yTickValues;
      if (!xTickMarks) {
        hTicks = 0;
        xAxisGrid = Grid.NONE;
      }
      if (!yTickMarks) {
        vTicks = 0;
        yAxisGrid = Grid.NONE;
      }
      title = SliceWaveRenderer.this.title;
      titleBackground = Color.WHITE;
    }

    public void update() {
      super.date = SliceWaveRenderer.this.date;
    }
  }

  /**
   * Reinitialize frame decorator with this renderer data.
   */
  public void update() {
    if (decorator == null) {
      createDefaultFrameDecorator();
    }
    decorator.update();

    if (decorator instanceof DefaultFrameDecorator) {
      ((DefaultFrameDecorator) decorator).yAxisLabel = yLabelText;
    }
    this.setExtents(viewStartTime, viewEndTime, minY, maxY);

    decorator.decorate(this);
  }

  /**
   * Render slice graph.
   * @param g where to render to
   */
  public void render(Graphics2D g) {
    Color origColor = g.getColor();
    Stroke origStroke = g.getStroke();
    Shape origClip = g.getClip();

    if (axis != null) {
      axis.render(g);
    }

    g.clip(new Rectangle(graphX + 1, graphY + 1, graphWidth - 1, graphHeight - 1));

    double st = wave.getStartTime();
    double step = 1 / wave.getSamplingRate();
    wave.reset();

    double bias;
    if (removeBias) {
      bias = wave.mean();
    } else {
      bias = 0;
    }

    g.setColor(color);

    double ns = (double) wave.samples() * (viewEndTime - viewStartTime)
        / (wave.getEndTime() - wave.getStartTime());
    double spp = ns / (double) graphWidth;
    Rectangle2D.Double box = new Rectangle2D.Double();
    if (spp < 50.0) {
      GeneralPath gp = new GeneralPath();

      double y = wave.next();
      gp.moveTo((float) getXPixel(st), (float) (getYPixel(y - bias)));
      float lastY = (float) getYPixel(y - bias);
      while (wave.hasNext()) {
        st += step;
        y = wave.next();
        if (y == Wave.NO_DATA) {
          gp.moveTo((float) getXPixel(st), lastY);
        } else {
          lastY = (float) getYPixel(y - bias);
          gp.lineTo((float) getXPixel(st), lastY);
          if (drawSamples && (1 / spp) > 2.0) {
            box.setRect((float) getXPixel(st) - 1.5, lastY - 1.5, 3, 3);
            g.draw(box);
          }
        }
      }
      g.draw(gp);
    } else {
      double[][] spans = new double[graphWidth + 1][];
      for (int i = 0; i < spans.length; i++) {
        spans[i] = new double[] {1E300, -1E300};
      }

      double span = viewEndTime - viewStartTime;

      wave.reset();
      double y;
      int i;
      while (wave.hasNext()) {
        y = wave.next();
        i = (int) (((st - viewStartTime) / span) * graphWidth + 0.5);
        if (i >= 0 && i < spans.length && y != Wave.NO_DATA) {
          spans[i][0] = Math.min(y, spans[i][0]);
          spans[i][1] = Math.max(y, spans[i][1]);
        }
        st += step;
      }

      Line2D.Double line = new Line2D.Double();
      double minY;
      double maxY;
      double lastMinY = -1E300;
      double lastMaxY = 1E300;
      for (i = 0; i < spans.length; i++) {
        minY = getYPixel(spans[i][0] - bias);
        maxY = getYPixel(spans[i][1] - bias);

        if (maxY < lastMinY) {
          line.setLine(graphX + i - 1, lastMinY, graphX + i, maxY);
          g.draw(line);
        } else if (minY > lastMaxY) {
          line.setLine(graphX + i - 1, lastMaxY, graphX + i, minY);
          g.draw(line);
        }

        line.setLine(graphX + i, minY, graphX + i, maxY);
        g.draw(line);
        lastMinY = minY;
        lastMaxY = maxY;
      }
    }

    if (getLegendRenderer() != null) {
      g.setColor(Color.BLACK);
      getLegendRenderer().render(g);
    }
    g.setClip(origClip);

    if (axis != null) {
      axis.postRender(g);
    }
    g.setStroke(origStroke);
    g.setColor(origColor);
  }
}
