package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.data.HelicorderData;
import gov.usgs.volcanoes.core.legacy.plot.decorate.FrameDecorator;
import gov.usgs.volcanoes.core.legacy.plot.decorate.SmartTick;
import gov.usgs.volcanoes.core.time.J2kSec;
import gov.usgs.volcanoes.core.time.Time;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * A class for rendering helicorders.
 *
 * @author Dan Cervelli
 */
public class HelicorderRenderer extends FrameRenderer {
  private static final Font LARGE_FONT = Font.decode("Dialog-BOLD-40");
  private HelicorderData data;
  private boolean noData = false;

  private boolean forceCenter;
  private double timeChunk;
  private int numRows;
  private double rowHeight;
  private double hcMinX;
  private double hcMaxX;
  private double hcMinY;
  private double hcMaxY;
  private Color[] defaultColors = new Color[] {new Color(0, 0, 255), new Color(0, 0, 205),
      new Color(0, 0, 155), new Color(0, 0, 105)};
  private Color color = null;

  private TimeZone timeZone = TimeZone.getTimeZone("UTC");

  private int clipValue = 3000;
  private boolean showClip = false;
  private boolean alertClip = false;
  private String clipWav;
  private int alertClipTimeout;
  private double lastClipAlert;
  private double lastClipTime;

  private String channel;
  private boolean largeChannelDisplay;

  private FrameDecorator decorator;
  private boolean showDecorator = true;

  public boolean xTickMarks = true;
  public boolean xTickValues = true;
  public boolean xUnits = true;
  public boolean xLabel = false;
  public boolean yTickMarks = true;
  public boolean yTickValues = true;
  public boolean yUnits = true;
  public boolean yLabel = false;

  /**
   * Default constructor.
   */
  public HelicorderRenderer() {
    forceCenter = false;
  }

  /**
   * Constructor.
   * 
   * @param d data to render
   * @param xs raw duration
   */
  public HelicorderRenderer(HelicorderData d, double xs) {
    this();
    data = d;
    timeChunk = xs;
  }

  /**
   * Setter for data.
   * 
   * @param d helicorder data
   */
  public void setData(HelicorderData d) {
    data = d;

    // check if this is a pre-formatted no data matrix
    if (d.rows() == 1 && Double.isNaN(data.getData().getQuick(0, 0))) {
      noData = true;
    }
  }

  /**
   * Setter for raw duration.
   * 
   * @param xs duration
   */
  public void setTimeChunk(double xs) {
    timeChunk = xs;
  }

  /**
   * Getter for raw duration.
   * 
   * @return raw duration
   */
  public double getTimeChunk() {
    return timeChunk;
  }

  /**
   * Sets large channel flag.
   * 
   * @param b large channel display flag
   */
  public void setLargeChannelDisplay(boolean b) {
    largeChannelDisplay = b;
  }

  /**
   * Compute double array with description of graph axis information.
   * 
   * @param adjTime flag if we take into account time zone offset while time boundaries computing
   * @return double array with description of graph axis information
   */
  public double[] getTranslationInfo(boolean adjTime) {
    double tzo = 0;
    if (adjTime) {
      tzo = timeZone.getOffset((long) Time.j2kToEw(getViewEndTime()));
    }

    return new double[] {graphX, graphX + graphWidth, rowHeight, graphY, hcMinX + tzo, hcMaxX + tzo,
        timeChunk, timeChunk / graphWidth};
  }

  /**
   * Gets the x-scale (the graph width / horizontal view extent).
   * 
   * @return the x-scale
   */
  public double helicorderGetXScale() {
    return graphWidth / timeChunk;
  }

  /**
   * Gets the y-scale (the graph height / vertical view extent).
   * 
   * @return the y-scale
   */
  public double helicorderGetYScale() {
    return ((graphHeight / (double) numRows) / (hcMaxY - hcMinY));
  }

  /**
   * Get pixel X coordinate, taking into account raw structure of helicorder.
   * 
   * @param x time value
   * @return pixel x coordinate
   */
  public double helicorderGetXPixel(double x) {
    while (x < hcMinX) {
      x += timeChunk;
    }
    double tx = (x - hcMinX) % timeChunk;
    return (tx * helicorderGetXScale()) + graphX;
  }

  /**
   * Get pixel Y coordinate, taking into account raw structure of helicorder.
   * 
   * @param x time value
   * @param y data value
   * @return pixel y coordinate
   */
  public double helicorderGetYPixel(double x, double y) {
    int row = numRows - (int) ((x - hcMinX) / timeChunk) - 1;
    return graphY + graphHeight - ((y - hcMinY) * helicorderGetYScale())
        - ((double) row * rowHeight);
  }

  /**
   * Get max X (time) value of helicorder.
   * 
   * @return max time
   */
  public double getHelicorderMaxX() {
    return hcMaxX;
  }

  /**
   * Get min X (time) value of helicorder.
   * 
   * @return min time
   */
  public double getHelicorderMinX() {
    return hcMinX;
  }

  /**
   * Get end time of helicorder view extent.
   * 
   * @return end time
   */
  public double getViewEndTime() {
    return hcMinX + numRows * timeChunk;
  }

  /**
   * Sets the view extents of the frame.
   * 
   * @param loX the minimum x view extent
   * @param hiX the maximum x view extent
   * @param loY the minimum y view extent
   * @param hiY the maximum y view extent
   */
  public void setHelicorderExtents(double loX, double hiX, double loY, double hiY) {
    hcMinY = loY;
    hcMaxY = hiY;
    hcMinX = loX - (loX % timeChunk);
    hcMaxX = hiX + (timeChunk - (hiX % timeChunk));
    numRows = (int) ((hcMaxX - hcMinX) / timeChunk);
    rowHeight = (double) graphHeight / (double) numRows;

    super.setExtents(0, timeChunk, 0, numRows);
  }

  /**
   * Get raw number for time value.
   * 
   * @return raw number for time value
   */
  public int getRow(double x) {
    double timeOffset = x - hcMinX;
    int rowOffset = (int) (timeOffset / timeChunk);
    if (timeOffset < 0) {
      rowOffset--;
    }
    return rowOffset;
  }

  /**
   * Setter for channel.
   * 
   * @param ch channel
   */
  public void setChannel(String ch) {
    channel = ch.replace('$', ' ');
  }

  /**
   * Setter for clipBars
   * 
   * @param clipBars The clipBars to set.
   */
  public void setClipBars(int clipBars) {}

  /**
   * Setter for clipWav
   * 
   * @param cw The .wav to play when clipping is detected
   */
  public void setClipWav(String cw) {
    clipWav = cw;
  }

  /**
   * Set timeout to detect clipping.
   * 
   * @param to timeout
   */
  public void setClipAlertTimeout(int to) {
    alertClipTimeout = to;
  }

  /**
   * Set flag if we force center view.
   * 
   * @param forceCenter new value for force center flag
   */
  public void setForceCenter(boolean forceCenter) {
    this.forceCenter = forceCenter;
  }

  /**
   * Get number of raws in the helicorder.
   * 
   * @return number of rows
   */
  public int getNumRows() {
    return numRows;
  }

  /**
   * Setter for time zone.
   * 
   * @param tz timezone
   */
  public void setTimeZone(TimeZone tz) {
    if (tz != null) {
      timeZone = tz;
    }
  }

  /**
   * Setter for time zone abbreviation -- UNIMPLEMENTED.
   * 
   * @param s abbreviation
   */
  public void setTimeZoneAbbr(String s) {
    // timeZoneAbbr = s;
  }

  /**
   * Setter for time zone offset -- UNIMPLEMENTED.
   * 
   * @param h offset
   */
  public void setTimeZoneOffset(double h) {
    // timeZoneOffset = h;
  }

  /**
   * Set flag if we show clipping.
   * 
   * @param b new flag
   */
  public void setShowClip(boolean b) {
    showClip = b;
  }

  /**
   * Set flag if we play alert while clipping detected.
   * 
   * @param b new flag
   */
  public void setAlertClip(boolean b) {
    alertClip = b;
  }

  /**
   * Set clip value.
   * 
   * @param i new clip value
   */
  public void setClipValue(int i) {
    clipValue = i;
  }

  /**
   * Get raw height.
   * 
   * @return raw height
   */
  public double getRowHeight() {
    return rowHeight;
  }

  /**
   * Set decorator to render frame.
   * 
   * @param d new decorator
   */
  public void setFrameDecorator(FrameDecorator d) {
    decorator = d;
  }

  /**
   * Set default colors to render.
   * 
   * @param cs color array
   */
  public void setDefaultColors(Color[] cs) {
    defaultColors = cs;
  }

  /**
   * Set color to render.
   * 
   * @param color new color
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /**
   * set show decorator to display bottom x axis.
   * 
   * @param sd true to show decorator
   */
  public void setShowDecorator(boolean sd) {
    showDecorator = sd;
  }

  /**
   * Creates a standard legend, a small line and point sample followed by the specified names.
   * 
   * @param s the legend names
   */
  public void createDefaultLegendRenderer(String[] s) {
    setLegendRenderer(new LegendRenderer(noData));
    getLegendRenderer().x = graphX + 6;
    getLegendRenderer().y = graphY + 6;
    ShapeRenderer sr = new ShapeRenderer(new GeneralPath(GeneralPath.WIND_NON_ZERO, 1000));
    sr.color = new Color(0, 0, 255);
    for (int i = 0; i < s.length; i++) {
      getLegendRenderer().addLine(sr, null, s[i]);
    }
  }

  /**
   * Render graph.
   * 
   * @param g where to draw to
   */
  public void render(Graphics2D g) {

    if (data == null) {
      return;
    }

    if (decorator != null) {
      decorator.decorate(this);
    }

    AffineTransform origAT = g.getTransform();
    Color origColor = g.getColor();
    Shape origClip = g.getClip();

    if (axis != null) {
      axis.render(g);
    }

    g.setClip(new Rectangle(graphX + 1, graphY + 1, graphWidth - 1, graphHeight - 1));

    if (!noData) {

      DoubleMatrix2D j2k = data.getTimes();
      DoubleMatrix2D min = data.getMin();
      DoubleMatrix2D max = data.getMax();

      double t1, x, y, w, h, ymax, ymin;
      double t2 = 0;

      double bias = Double.NaN;
      int lastRow = -1;
      int numRows = j2k.rows();
      Color lastColor = null;
      if (color != null) {
        g.setColor(color);
      }
      for (int j = 0; j < numRows; j++) {
        t1 = j2k.getQuick(j, 0);
        int k = ((int) ((t1 - hcMinX) / timeChunk)) % defaultColors.length;
        if (k < 0) {
          k = 0;
        }

        if (color == null) {
          if (lastColor != defaultColors[k]) {
            g.setColor(defaultColors[k]);
            lastColor = defaultColors[k];
          }
        }

        t2 = t1 + 1;

        int r = getRow(t2);
        if (r != lastRow) {
          double st = hcMinX + r * timeChunk;
          bias = data.getBiasBetween(st, st + timeChunk);
          lastRow = r;
        }

        x = helicorderGetXPixel(t1);
        w = helicorderGetXPixel(t2) - x;
        ymax = max.getQuick(j, 0);
        ymin = min.getQuick(j, 0);

        if (ymax == Integer.MIN_VALUE || ymin == Integer.MIN_VALUE) {
          continue;
        }

        ymax -= bias;
        ymin -= bias;

        if (showClip && (ymax >= clipValue || ymin <= -clipValue)) {
          lastClipTime = t1;
          if (color == null) {
            if (lastColor != Color.red) {
              g.setColor(Color.red);
              lastColor = Color.red;
            }
          }
        }

        if (ymax > clipValue) {
          ymax = clipValue;
        }

        if (ymin < -clipValue) {
          ymin = -clipValue;
        }

        y = helicorderGetYPixel(t1, ymax);
        h = helicorderGetYPixel(t1, ymin) - y;
        int hgt = (int) (h + 1);
        if (hgt < 1) {
          hgt = 1;
        }
        if (forceCenter) {
          y = helicorderGetYPixel(t1, 0) - hgt / 2;
        }
        g.fillRect((int) (x + 1), (int) (y + 1), (int) (w + 1), hgt);
      }

      g.setClip(origClip);
      g.setColor(origColor);
      g.setTransform(origAT);

      if (largeChannelDisplay && channel != null) {
        Font oldFont = g.getFont();
        g.setFont(LARGE_FONT);
        String c = channel.replace('_', ' ');

        FontMetrics fm = g.getFontMetrics();
        Font f = g.getFont();
        float s = f.getSize();
        int width = fm.stringWidth(c);
        while ((width / (double) graphWidth > .5) && (--s > 1)) {
          g.setFont(f.deriveFont(s));
          fm = g.getFontMetrics();
          width = fm.stringWidth(c);
        }
        int height = fm.getAscent() + fm.getDescent();
        int lw = width + 20;

        if (alertClip && lastClipTime > t2 - alertClipTimeout && clipWav != null) {
          g.setColor(Color.red);
          if ((lastClipTime > lastClipAlert + alertClipTimeout)) {
            lastClipAlert = t2;
            playClipAlert();
          }
        } else if (alertClip) {
          g.setColor(new Color(128, 255, 128, 192));
        } else {
          g.setColor(new Color(255, 255, 255, 192));
        }

        g.fillRect(graphX + graphWidth / 2 - lw / 2, 3, lw, height);
        g.setColor(Color.black);
        g.drawRect(graphX + graphWidth / 2 - lw / 2, 3, lw, height);

        g.drawString(c, graphX + graphWidth / 2 - width / 2, height - fm.getDescent());
        g.setFont(oldFont);

      } else if (alertClip && lastClipTime > t2 - alertClipTimeout && clipWav != null) {
        if ((lastClipTime > lastClipAlert + alertClipTimeout)) {
          lastClipAlert = t2;
          playClipAlert();
        }
      }
    }

    g.setClip(origClip);
    g.setColor(origColor);
    g.setTransform(origAT);

    if (getLegendRenderer() != null) {
      getLegendRenderer().render(g);
    }

    if (axis != null) {
      axis.postRender(g);
    }
  }

  /**
   * Create new MinimumDecorator and set it as decorator for this renderer.
   */
  public void createMinimumAxis() {
    decorator = new MinimumDecorator();
  }

  /**
   * Minimum decorator.
   */
  class MinimumDecorator extends FrameDecorator {
    public void decorate(FrameRenderer fr) {
      axis = new AxisRenderer(fr);
      axis.createDefault();

      int minutes = (int) Math.round(timeChunk / 60.0);
      int majorTicks = minutes;
      if (minutes > 30 && minutes < 180) {
        majorTicks = minutes / 5;
      } else if (minutes >= 180 && minutes < 360) {
        majorTicks = minutes / 10;
      } else if (minutes >= 360) {
        majorTicks = minutes / 20;
      }
      double[] mjt = SmartTick.intervalTick(minX, maxX, majorTicks);
      if (xTickMarks) {
        axis.createBottomTicks(null, mjt);
        axis.createTopTicks(null, mjt);
        axis.createVerticalGridLines(mjt);
      }

      String[] btl = new String[mjt.length];
      for (int i = 0; i < mjt.length; i++) {
        btl[i] = Long.toString(Math.round(mjt[i] / 60.0));
      }

      double[] labelPosLr = new double[numRows];
      String[] leftLabelText = new String[numRows];
      DateFormat timeFormat = new SimpleDateFormat("HH:mm");
      DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
      timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      dayFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      double pixelsPast = 0;
      double pixelsPerRow = graphHeight / numRows;
      for (int i = numRows - 1; i >= 0; i--) {
        pixelsPast += pixelsPerRow;
        labelPosLr[i] = i + 0.5;
        // TODO: fix
        // java.util.Date dtz = Util.j2KToDate(hcMaxX - (i + 1) * timeChunk + timeZoneOffset *
        // 3600);
        java.util.Date dtz = J2kSec.asDate(hcMaxX - (i + 1) * timeChunk);
        String ftl = timeFormat.format(dtz);

        leftLabelText[i] = null;
        if (pixelsPast > 20) {
          leftLabelText[i] = ftl;
          pixelsPast = 0;
        }
      }
      if (yTickValues) {
        axis.createLeftTickLabels(labelPosLr, leftLabelText);
      }
      if (xUnits) {
        axis.addRenderer(new TextRenderer(graphX, graphY - 3,
            channel + ", " + dayFormat.format(J2kSec.asDate(hcMaxX))));
      }

      double[] hg = new double[numRows - 1];
      for (int i = 0; i < numRows - 1; i++) {
        hg[i] = i + 1.0;
      }

      if (yTickMarks) {
        axis.createHorizontalGridLines(hg);
      }

      axis.setBackgroundColor(Color.white);
    }
  }

  /**
   * Standard decorator.
   */
  class StandardDecorator extends FrameDecorator {
    public void decorate(FrameRenderer fr) {
      axis = new AxisRenderer(fr);
      axis.createDefault();
      // if (numRows <= 0)
      // return;

      int minutes = (int) Math.round(timeChunk / 60.0);
      int majorTicks = minutes;
      if (minutes > 30 && minutes < 180) {
        majorTicks = minutes / 5;
      } else if (minutes >= 180 && minutes < 360) {
        majorTicks = minutes / 10;
      } else if (minutes >= 360) {
        majorTicks = minutes / 20;
      }
      double[] mjt = SmartTick.intervalTick(minX, maxX, majorTicks);

      int minorTicks = 0;
      if (minutes <= 30) {
        minorTicks = (int) Math.round(timeChunk / 10.0);
      } else if (minutes > 30 && minutes <= 180) {
        minorTicks = minutes;
      } else if (minutes > 180) {
        minorTicks = minutes / 5;
      }
      double[] mnt = SmartTick.intervalTick(minX, maxX, minorTicks);

      // x axis decorations
      if (showDecorator) {
        if (xUnits) {
          axis.setBottomLabelAsText("+ Minutes");
        }
        if (xTickValues) {
          String[] btl = new String[mjt.length];
          for (int i = 0; i < mjt.length; i++) {
            btl[i] = Long.toString(Math.round(mjt[i] / 60.0));
          }
          axis.createBottomTickLabels(mjt, btl);
        }
      }
      if (xTickMarks) {
        axis.createBottomTicks(mjt, mnt);
        axis.createTopTicks(mjt, mnt);
        axis.createVerticalGridLines(mjt);
      }

      double[] labelPosLr = new double[numRows];
      String[] leftLabelText = new String[numRows];
      String[] rightLabelText = new String[numRows];

      DateFormat utcTimeFormat = new SimpleDateFormat("HH:mm");
      utcTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

      DateFormat utcDayFormat = new SimpleDateFormat("MM-dd");
      utcDayFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

      DateFormat localTimeFormat = new SimpleDateFormat("HH:mm");
      localTimeFormat.setTimeZone(timeZone);

      DateFormat localDayFormat = new SimpleDateFormat("MM-dd");
      localDayFormat.setTimeZone(timeZone);

      double timeOffset = timeZone.getOffset((long) Time.j2kToEw(getViewEndTime())) / 1000;

      double pixelsPast = 0;
      double pixelsPerRow = graphHeight / numRows;
      String lastDayL = "";
      String lastDayR = "";
      for (int i = numRows - 1; i >= 0; i--) {
        pixelsPast += pixelsPerRow;
        labelPosLr[i] = i + 0.5;
        double j2kStart = hcMaxX - (i + 1) * timeChunk;
        double j2kEnd = j2kStart + timeChunk;

        Date start = J2kSec.asDate(j2kStart);
        String localTime = localTimeFormat.format(start);
        String localDay = localDayFormat.format(start);

        Date end = J2kSec.asDate(j2kEnd);
        String utcTime = utcTimeFormat.format(end);
        String utcDay = utcDayFormat.format(end);

        leftLabelText[i] = null;
        if (!localDay.equals(lastDayL)) {
          leftLabelText[i] = localDay + "           ";
        }

        if (timeOffset != 0 && !utcDay.equals(lastDayR)) {
          rightLabelText[i] = "           " + utcDay;
        }

        lastDayL = localDay;
        lastDayR = utcDay;

        if (pixelsPast > 20) {
          if (leftLabelText[i] != null) {
            leftLabelText[i] = localDay + " " + localTime;
          } else {
            leftLabelText[i] = localTime;
          }

          if (timeOffset != 0) {
            if (rightLabelText[i] != null) {
              rightLabelText[i] = utcTime + " " + utcDay;
            } else {
              rightLabelText[i] = utcTime;
            }
          }
          pixelsPast = 0;
        }
      }
      if (yTickValues) {
        axis.createLeftTickLabels(labelPosLr, leftLabelText);
        axis.createRightTickLabels(labelPosLr, rightLabelText);
      }
      if (showDecorator && xUnits) {
        boolean inDst = timeZone.inDaylightTime(J2kSec.asDate(getViewEndTime()));
        axis.setBottomLeftLabelAsText(
            "Time (" + timeZone.getDisplayName(inDst, TimeZone.SHORT) + ")");
        if (timeOffset != 0) {
          axis.setBottomRightLabelAsText("Time (UTC)");
        }
      }

      double[] hg = new double[numRows - 1];
      for (int i = 0; i < numRows - 1; i++) {
        hg[i] = i + 1.0;
      }

      if (yTickMarks) {
        axis.createHorizontalGridLines(hg);
      }
      axis.setBackgroundColor(Color.white);
    }
  }

  /**
   * Creates the default axis using SmartTick.
   */
  public void createDefaultAxis() {
    decorator = new StandardDecorator();
  }

  /**
   * Play clip alert.
   */
  private void playClipAlert() {
    Runnable r = new Runnable() {
      public void run() {
        File soundFile = new File(clipWav);
        AudioInputStream audioInputStream = null;
        try {
          audioInputStream = AudioSystem.getAudioInputStream(soundFile);
          AudioFormat audioFormat = audioInputStream.getFormat();
          DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

          SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
          line.open(audioFormat);
          line.start();

          int nBytesRead = 0;
          byte[] abData = new byte[1024];
          while (nBytesRead != -1) {
            nBytesRead = audioInputStream.read(abData, 0, abData.length);
            if (nBytesRead >= 0) {
              line.write(abData, 0, nBytesRead);
            }
          }

          line.drain();
          line.close();
        } catch (Exception e) {
          //
        }
      }
    };
    Thread t = new Thread(r);
    t.start();
  }
}
