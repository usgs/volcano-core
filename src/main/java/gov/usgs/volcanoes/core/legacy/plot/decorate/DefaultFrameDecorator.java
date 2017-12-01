package gov.usgs.volcanoes.core.legacy.plot.decorate;


import gov.usgs.volcanoes.core.legacy.plot.render.AxisRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.FrameRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.RectangleRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.TextRenderer;
import gov.usgs.volcanoes.core.math.Util;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

/**
 * <p>
 * Class which holds information about method of title and axis rendering - positions, lines, ticks
 * count etc. Can configure given FrameRenderer.
 * </p>
 * 
 * @author Dan Cervelli
 */
public class DefaultFrameDecorator extends FrameDecorator {

  public enum TitleLocation {
    TOP, INSET;
  }

  public enum XAxis {
    NONE, TIME, LINEAR, LOG;
  }

  public enum YAxis {
    NONE, LINEAR, LOG;
  }

  public enum Grid {
    NONE, DASH, SOLID;
  }

  public enum Location {
    TOP, BOTTOM, LEFT, RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT, GRAPH_TOP, GRAPH_BOTTOM, GRAPH_LEFT, GRAPH_RIGHT, GRAPH_BOTTOM_LEFT, GRAPH_BOTTOM_RIGHT, GRAPH_TOP_LEFT, GRAPH_TOP_RIGHT;
  }

  private final int PIXELS_PER_CHARACTER = 6;

  public TitleLocation titleLocation = TitleLocation.INSET;
  public Color titleBackground;
  public Font titleFont;
  public String title;
  public XAxis xAxis = XAxis.TIME;
  public YAxis yAxis = YAxis.LINEAR;
  public boolean hasAxis = true;
  public boolean hasFrame = true;
  public boolean xAxisLabels = true;
  public boolean yAxisLabels = true;
  public double xAxisTopTickLength = 8;
  public double xAxisBottomTickLength = 8;
  public double yAxisLeftTickLength = 8;
  public double yAxisRightTickLength = 8;
  public Grid xAxisGrid = Grid.DASH;
  public Grid yAxisGrid = Grid.DASH;
  public int hTicks = -1;
  public int vTicks = -1;

  public String xAxisLabel = null;
  public String yAxisLabel = null;
  public String xUnit = null;
  public String yUnit = null;

  protected static NumberFormat numberFormat = NumberFormat.getInstance();

  static {
    numberFormat.setMaximumFractionDigits(6);
  }

  /**
   * Create string array with label text according to numberFormat setting.
   * 
   * @param t array of double values
   * @param log if logarithm scale enabled
   * @param m never used
   * @param b never used
   * @return String array the same size with labels text
   */
  public static String[] createLabels(double[] t, boolean log, double m, double b) {
    String[] labels = new String[t.length];
    for (int i = 0; i < t.length; i++) {
      if (log) {
        labels[i] = "10^" + numberFormat.format(t[i]);
      } else {
        double v = t[i];
        double exp = Util.getExp(v);
        labels[i] =
            (exp >= 5 ? numberFormat.format(v / Math.pow(10, exp)) + "e" + numberFormat.format(exp)
                : numberFormat.format(v));
      }

    }
    return labels;
  }

  /**
   * Create and add to Frame Renderer specialized renderer to process plot title according
   * configured properties.
   * 
   * @param fr Frame renderer to process
   */
  private void createTitle(FrameRenderer fr) {
    if (title == null || title.length() == 0) {
      return;
    }

    switch (titleLocation) {
      case TOP:
      case INSET:
        AxisRenderer ar = fr.getAxis();
        if (titleFont == null) {
          titleFont = Font.decode("dialog-plain-12");
        }

        TextRenderer label =
            new TextRenderer(fr.getGraphWidth() - (title.length() * PIXELS_PER_CHARACTER) + 32,
                fr.getGraphY() + 16, title, Color.BLACK);
        label.font = titleFont;
        if (titleBackground != null) {
          RectangleRenderer rr = ar.getFrame();
          rr.color = Color.GRAY;
          rr = new RectangleRenderer();
          rr.rect = new Rectangle2D.Double();
          FontRenderContext frc = new FontRenderContext(new AffineTransform(), false, false);
          rr.rect.setFrame(titleFont.getStringBounds(title, frc));
          // rr.rect.x = fr.getGraphX() + 3;
          rr.rect.y = fr.getGraphY() + 3;
          rr.rect.x = fr.getGraphWidth() - (title.length() * PIXELS_PER_CHARACTER) + 29;
          rr.rect.width += 6;
          rr.rect.height += 2;
          rr.color = Color.GRAY;
          rr.backgroundColor = titleBackground;
          ar.addPostRenderer(rr);
        }
        ar.addPostRenderer(label);
        break;
      default:
        break;
    }
  }

  /**
   * Create and add to Frame Renderer specialized renderer to process X axis according configured
   * properties.
   * 
   * @param fr Frame renderer to process
   */
  private void createXAxis(FrameRenderer fr) {
    AxisRenderer axis = fr.getAxis();
    boolean doAxis = true;
    Object[] stt = null;
    boolean log = false;
    switch (xAxis) {
      case NONE:
        doAxis = false;
        break;

      case LOG:
        log = true;
        // fall through

      case LINEAR:

        String[] st;
        double[] t;

        if (log) {
          int x1 = (int) Math.floor(fr.getMinXAxis());
          int x2 = (int) Math.ceil(fr.getMaxXAxis());

          int n = (x2 - x1) * 9;

          st = new String[n];
          t = new double[n];

          int c = 0;
          double inc;
          double value;

          for (int i = x1; i < x2; i++) {
            inc = Math.pow(10, i);

            if (i < fr.getMinXAxis() || i > fr.getMaxXAxis()) {
              t[c] = Double.NaN;
              st[c] = "";
            } else {
              t[c] = i;
              st[c] = numberFormat.format(Math.pow(10, i)) + " Hz";
            }

            c++;
            for (int j = 1; j < 9; j++) {
              value = Math.log10(Math.pow(10, i) + inc * j);
              if (value < fr.getMinXAxis() | value > fr.getMaxXAxis()) {
                t[c] = Double.NaN;
              } else {
                t[c] = value;
              }

              st[c] = "";
              c++;
            }
          }
        } else {
          if (hTicks == -1) {
            hTicks = fr.getGraphWidth() / 108;
          }
          t = SmartTick.autoTick(fr.getMinXAxis(), fr.getMaxXAxis(), hTicks, false);
          st = new String[t.length];
          for (int i = 0; i < t.length; i++) {
            double val = t[i];
            st[i] = numberFormat.format(val) + " Hz";
          }
        }

        int count = 0;
        for (double d : t) {
          if (!Double.isNaN(d)) {
            count++;
          }
        }
        double[] smallT = new double[count];
        String[] smallSt = new String[count];

        count = 0;
        for (int i = 0; i < t.length; i++) {
          if (!Double.isNaN(t[i])) {
            smallT[count] = t[i];
            smallSt[count++] = st[i];
          }
        }

        stt = new Object[] {smallT, smallSt};
        break;

      case TIME:
        if (hTicks == -1) {
          hTicks = fr.getGraphWidth() / 108;
        }
        stt = SmartTick.autoTimeTick(fr.getMinXAxis(), fr.getMaxXAxis(), hTicks);
        break;
      default:
        break;
    }
    if (doAxis) {
      if (stt != null) {
        if (xAxisBottomTickLength > 0) {
          axis.createBottomTicks((double[]) stt[0], xAxisBottomTickLength, Color.BLACK);
        }
        if (xAxisTopTickLength > 0) {
          axis.createTopTicks((double[]) stt[0], xAxisTopTickLength, Color.BLACK);
        }
        if (xAxisGrid != Grid.NONE) {
          axis.createVerticalGridLines((double[]) stt[0]);
        }

        if (xAxisLabels) {
          axis.createBottomTickLabels((double[]) stt[0], (String[]) stt[1]);
        }
        if (xAxisLabel != null) {
          addLabel(fr, yAxisLabel, Location.BOTTOM);
        }
        if (xUnit != null) {
          axis.setBottomLabelAsText(xUnit);
        }
      }
    }
  }

  /**
   * Create and add to FrameRenderer specialized renderer to process Y axis according configured
   * properties.
   * 
   * @param fr Frame renderer to process
   */
  private void createYAxis(FrameRenderer fr) {
    AxisRenderer axis = fr.getAxis();
    boolean doAxis = true;
    double[] yt = null;
    double[] ytm = null;
    boolean log = false;
    switch (yAxis) {
      case NONE:
        break;
      case LOG:
        log = true;
      case LINEAR:
        if (log) {
          vTicks = (int) (fr.getMaxYAxis() - fr.getMinYAxis());
        } else if (vTicks == -1) {
          vTicks = fr.getGraphHeight() / 24;
        }

        yt = SmartTick.autoTick(fr.getMinYAxis(), fr.getMaxYAxis(), vTicks, false);

        ytm = new double[yt.length];
        for (int i = 0; i < yt.length; i++) {
          ytm[i] = (yt[i] - fr.getYAxisOffset()) / fr.getYAxisMult();
        }
        break;
      default:
        break;
    }

    if (doAxis) {
      if (yt != null) {
        if (yAxisLeftTickLength > 0) {
          axis.createLeftTicks(ytm, yAxisLeftTickLength, Color.BLACK);
        }
        if (yAxisRightTickLength > 0) {
          axis.createRightTicks(ytm, true, yAxisRightTickLength, Color.BLACK);
        }
        if (yAxisGrid != Grid.NONE) {
          axis.createHorizontalGridLines(ytm);
        }
        if (yAxisLabels) {
          axis.createLeftTickLabels(ytm,
              createLabels(yt, log, fr.getYAxisMult(), fr.getYAxisOffset()));
        }
        if (yAxisLabel != null) {
          addLabel(fr, yAxisLabel, Location.LEFT);
        }
        if (yUnit != null) {
          axis.setLeftLabelAsText(yUnit);
        }
      }
    }
  }

  /**
   * Add to FrameRenderer specialized renderer to draw label with default font and color.
   * 
   * @param fr Frame renderer to process
   * @param text label text
   * @param loc label location
   * @return Created TextRenderer to process label
   */
  public static TextRenderer addLabel(FrameRenderer fr, String text, Location loc) {
    return addLabel(fr, text, loc, null, null);
  }

  /**
   * Add to FrameRenderer specialized renderer to draw label.
   * 
   * @param fr Frame renderer to process
   * @param text label text
   * @param loc label location
   * @param font label font
   * @param bgColor label color
   * @return Created TextRenderer to process label
   */
  public static TextRenderer addLabel(FrameRenderer fr, String text, Location loc, Font font,
      Color bgColor) {
    TextRenderer tr = new TextRenderer();
    tr.text = text;
    if (font != null) {
      tr.font = font;
    }
    if (bgColor != null) {
      tr.backgroundColor = bgColor;
    }
    switch (loc) {
      case LEFT:
        tr.x = 6;
        tr.y = fr.getGraphY() + fr.getGraphHeight() / 2;
        tr.horizJustification = TextRenderer.CENTER;
        tr.orientation = -90.0f;
        fr.getAxis().addRenderer(tr);
        break;
      case RIGHT:
        tr.x = fr.getGraphX() + fr.getGraphWidth() + 15;
        tr.y = fr.getGraphY() + fr.getGraphHeight() / 2;
        tr.horizJustification = TextRenderer.CENTER;
        tr.orientation = 90.0f;
        fr.getAxis().addRenderer(tr);
        break;
      case BOTTOM:
        tr.x = fr.getGraphX() + fr.getGraphWidth() / 2;
        tr.y = fr.getGraphY() + fr.getGraphHeight() - 6;
        tr.horizJustification = TextRenderer.CENTER;
        fr.getAxis().addRenderer(tr);
        break;
      default:
        break;
    }
    return tr;
  }

  /**
   * Process FrameRenderer and add configured renderers for axises and title.
   * 
   * @param fr Frame renderer to process
   */
  public void decorate(FrameRenderer fr) {
    if (hasAxis) {
      AxisRenderer ar = new AxisRenderer(fr);
      ar.createDefault();
      if (!hasFrame) {
        ar.setFrame(null);
      }
      fr.setAxis(ar);

      createXAxis(fr);
      createYAxis(fr);
      createTitle(fr);
    }
  }
}
