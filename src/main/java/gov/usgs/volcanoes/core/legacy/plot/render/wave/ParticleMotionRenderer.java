package gov.usgs.volcanoes.core.legacy.plot.render.wave;

import gov.usgs.volcanoes.core.legacy.plot.decorate.DefaultFrameDecorator;
import gov.usgs.volcanoes.core.legacy.plot.render.FrameRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Renders 2D particle motion plots.
 *
 * @author Diana Norgaard
 */
public class ParticleMotionRenderer extends FrameRenderer {

  public Stroke stroke = new BasicStroke(1.0f);

  private static NumberFormat numberFormatter =
      new DecimalFormat("0.##E0", DecimalFormatSymbols.getInstance(Locale.ROOT));

  private double[] eastWaveData;
  private double[] northWaveData;
  private double[] verticalWaveData;
  private String eastComponentStation;
  private String northComponentStation;
  private String verticalComponentStation;
  private String title;

  /**
   * Default constructor.
   * @param eastWaveData detrended East wave component signal
   * @param northWaveData detrended North wave component signal
   * @param verticalWaveComponent detrended Vertical wave component signal
   * @param eastComponentStation Station for east wave component
   * @param northComponentStation Station for north wave component
   * @param verticalComponentStation Station for vertical wave component
   */
  public ParticleMotionRenderer(double[] eastWaveData, double[] northWaveData,
      double[] verticalWaveComponent, String eastComponentStation, String northComponentStation,
      String verticalComponentStation) {
    this.eastWaveData = eastWaveData;
    this.northWaveData = northWaveData;
    this.verticalWaveData = verticalWaveComponent;
    this.eastComponentStation = eastComponentStation;
    this.northComponentStation = northComponentStation;
    this.verticalComponentStation = verticalComponentStation;
  }

  /**
   * @see gov.usgs.plot.render.Renderer#render(java.awt.Graphics2D)
   */
  public void render(Graphics2D g) {
    Color origColor = g.getColor();

    int fontSize = g.getFont().getSize();

    if (axis != null) {
      axis.render(g);
    }
    g.setStroke(stroke);

    int y = fontSize;
    int h = graphHeight - y * 2;
    int w = h;
    int xoffset = (graphWidth - 3 * w) / 8;
    int x = xoffset;

    // Z vs N
    g.setColor(Color.BLACK);
    g.drawString("Z", x - fontSize, y + fontSize);
    Rectangle plotArea = new Rectangle(x, y, w, h);
    plotComponents(g, northWaveData, verticalWaveData, northComponentStation,
        verticalComponentStation, plotArea, fontSize);
    g.setColor(Color.BLACK);
    g.drawString("N", x + w + 5, y + h);

    // Z vs E
    x = x + w + 2 * xoffset;
    g.setColor(Color.BLACK);
    g.drawString("Z", x - fontSize, y + fontSize);
    plotArea = new Rectangle(x, y, w, h);
    plotComponents(g, eastWaveData, verticalWaveData, eastComponentStation,
        verticalComponentStation, plotArea, fontSize);
    g.setColor(Color.BLACK);
    g.drawString("E", x + w + 5, y + h);

    // N vs E
    x = x + w + 2 * xoffset;
    g.setColor(Color.BLACK);
    g.drawString("N", x - fontSize, y + fontSize);
    plotArea = new Rectangle(x, y, w, h);
    plotComponents(g, eastWaveData, northWaveData, eastComponentStation, northComponentStation,
        plotArea, fontSize);
    g.setColor(Color.BLACK);
    g.drawString("E", x + w + 5, y + h);

    // draw user info
    x = x + w + 2 * xoffset;
    g.setColor(Color.GRAY);
    g.drawString("Number represents", x, y + h / 3);
    g.drawString("XY limit", x, y + h / 3 + fontSize);
    g.setColor(Color.RED);
    g.drawString("Plots start as red", x, y + h / 3 + 3 * fontSize);
    g.setColor(Color.BLUE);
    g.drawString("and end as blue", x, y + h / 3 + 4 * fontSize);

    g.setColor(origColor);

    DefaultDecorator decorator = new DefaultDecorator(this.title);
    decorator.decorate(this);
    if (axis != null) {
      axis.postRender(g);
    }
  }

  /**
   * Plot 2 wave components.
   * @param g Graphics2D
   * @param xWaveData wave component to plot on X-axis
   * @param yWaveData wave component to plot on y-axis
   * @param xLabel x-axis label
   * @param yLabel y-axis label
   */
  private void plotComponents(Graphics2D g, double[] xWaveData, double[] yWaveData, String xLabel,
      String yLabel, Rectangle plotArea, int fontSize) {


    // Draw box
    g.draw(plotArea);

    // get offsets
    int xoffset = plotArea.x;
    int yoffset = plotArea.y;

    // Draw plot (or text incase of missing data)
    if (xWaveData.length == 0 || yWaveData.length == 0) {
      g.drawString("Missing data", xoffset + fontSize, yoffset + fontSize * 3);
      g.drawString("X: " + xLabel, xoffset + fontSize, yoffset + fontSize * 4);
      g.drawString("X length: " + xWaveData.length, xoffset + fontSize, yoffset + fontSize * 5);
      g.drawString("Y: " + yLabel, xoffset + fontSize, yoffset + fontSize * 6);
      g.drawString("Y length: " + yWaveData.length, xoffset + fontSize, yoffset + fontSize * 7);
    } else {
      if (xWaveData.length != yWaveData.length) {
        g.drawString("Missing data", xoffset + fontSize, yoffset + fontSize * 3);
        g.drawString("X: " + xLabel, xoffset + fontSize, yoffset + fontSize * 4);
        g.drawString("X length: " + xWaveData.length, xoffset + fontSize, yoffset + fontSize * 5);
        g.drawString("Y: " + yLabel, xoffset + fontSize, yoffset + fontSize * 6);
        g.drawString("Y length: " + yWaveData.length, xoffset + fontSize, yoffset + fontSize * 7);
      } else {
        // get max extent of plot
        double max = 0;
        for (double data : xWaveData) {
          max = Math.max(max, Math.abs(data));
        }
        for (double data : yWaveData) {
          max = Math.max(max, Math.abs(data));
        }

        // Draw cross hairs
        g.setColor(Color.GRAY);
        double midY = plotArea.y + plotArea.height / 2;
        g.drawLine(plotArea.x, (int) midY, plotArea.x + plotArea.width, (int) midY);
        double midX = plotArea.x + plotArea.width / 2;
        g.drawLine((int) midX, plotArea.y, (int) midX, plotArea.y + plotArea.height);

        // Draw tick label
        String tickLabel = "";
        if (max >= 1e6) {
          tickLabel = numberFormatter.format(max);
        } else {
          tickLabel = Integer.toString((int) max);
        }
        g.drawString(tickLabel, xoffset + plotArea.width + 2, (int) (midY + fontSize / 2));

        // plot particle motion
        int x1 = (int) (midX + (plotArea.width / 2) * (xWaveData[0] / max));
        int y1 = (int) (midY + (plotArea.height / 2) * (yWaveData[0] / max));
        for (int i = 1; i < xWaveData.length; i++) {
          int blue = 255 * i / yWaveData.length;
          int red = 255 - blue;
          // motion line starts as red and gradually changes color to blue.
          Color color = new Color(red, 0, blue);
          g.setColor(color);
          int x2 = (int) (midX + (plotArea.width / 2) * (xWaveData[i] / max));
          int y2 = (int) (midY - (plotArea.height / 2) * (yWaveData[i] / max));
          g.drawLine(x1, y1, x2, y2);
          x1 = x2;
          y1 = y2;
        }

      }
    }
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  protected class DefaultDecorator extends DefaultFrameDecorator {
    public DefaultDecorator(String title) {
      super.title = title;
      super.titleBackground = Color.WHITE;
    }
  }

}
