package gov.usgs.volcanoes.core.legacy.plot;

import gov.usgs.volcanoes.core.configfile.ConfigFile;
import gov.usgs.volcanoes.core.contrib.PngEncoder;
import gov.usgs.volcanoes.core.contrib.PngEncoderB;
import gov.usgs.volcanoes.core.legacy.plot.color.ColorCycler;
import gov.usgs.volcanoes.core.legacy.plot.color.ColorParser;
import gov.usgs.volcanoes.core.legacy.plot.decorate.SmartTick;
import gov.usgs.volcanoes.core.legacy.plot.render.AxisRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.DataPointRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.MatrixRenderer;
import gov.usgs.volcanoes.core.time.J2kSec;
import gov.usgs.volcanoes.core.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * <p>Configurable tool to render data stored in file (or from stdin)
 *  to screen frame or to png image file.</p>

 * @author Dan Cervelli
 */
public class Plotter {
  private final static Logger LOGGER = LoggerFactory.getLogger(Plotter.class);
  protected List<DoubleMatrix2D> data;
  protected double minData = 1E300;
  protected double maxData = -1E300;
  protected double minTime = 1E300;
  protected double maxTime = -1E300;

  protected ConfigFile config;
  protected BufferedImage image;

  protected JFrame frame;

  /**
   * Constructor
   * @param args array of command line options
   */
  public Plotter(String[] args) {
    readOptions(args);
    readData();
    try {
      plot();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Print message on stdout and exit
   * @param s message
   */
  public void fatalError(String s) {
    System.err.println("fatal: " + s);
    System.exit(-1);
  }

  /**
   * Scans command line options array and build internal options and their values representation
   * @param args
   */
  protected void readOptions(String[] args) {
    HashMap<String, String> options = new HashMap<String, String>();

    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("--")) {
        String key = args[i].substring(2, args[i].indexOf('='));
        String val = args[i].substring(args[i].indexOf('=') + 1);
        options.put(key, val);
      } else {
        System.err.println("ignored command line option: " + args[i]);
      }
    }

    String cfn = options.get("configFile");
    if (cfn != null) {
      config = new ConfigFile(cfn);
      if (!config.wasSuccessfullyRead())
        fatalError("configFile not found.");
    } else
      config = new ConfigFile();

    for (String key : options.keySet()) {
      config.put(key, options.get(key), false);
    }
    if (isOutputConfig())
      LOGGER.info("Config: {}", config);
  }

  /**
   * Parse data file described in the 'input' configuration parameter and build internal data representation
   */
  protected void readData() {
    data = new ArrayList<DoubleMatrix2D>();
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(getInput()));
      List<double[]> ld;
      while ((ld = readDataSet(in)).size() > 0) {
        DoubleMatrix2D dm = DoubleFactory2D.dense.make(ld.size(), 2);
        for (int i = 0; i < ld.size(); i++) {
          double f = ld.get(i)[0];
          minTime = Math.min(minTime, f);
          maxTime = Math.max(maxTime, f);
          dm.setQuick(i, 0, f);
          f = ld.get(i)[1];
          minData = Math.min(minData, f);
          maxData = Math.max(maxData, f);
          dm.setQuick(i, 1, f);
        }
        data.add(dm);
      }
    } catch (FileNotFoundException e) {
      fatalError("Input file not found: " + config.getString("input"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (data.size() <= 0) {
      fatalError("No data read.");
    }
  }

  /**
   * Parse given reader to extract data
   * @param in
   * @return data as list of double array[2] with J2K date-value pairs
   * @throws IOException
   */
  protected List<double[]> readDataSet(BufferedReader in) throws IOException {
    boolean done = false;
    SimpleDateFormat df = new SimpleDateFormat(getDateFormatString());
    List<double[]> ld = new ArrayList<double[]>(1000);
    while (!done) {
      String s = in.readLine();
      if (s == null || s.startsWith(">")) {
        done = true;
        continue;
      }

      try {
        String[] tokens = s.split("[ ,\t]");
        double[] d = new double[2];
        d[0] = J2kSec.fromDate(df.parse(tokens[0]));
        d[1] = Double.parseDouble(tokens[1]);
        ld.add(d);
      } catch (Exception e) {
        System.err.println("failed to parse: " + s);
      }
    }
    return ld;
  }

  /**
   * Get date format string from configuration. Used to parse dates in data files.
   * @return date format string
   */
  protected String getDateFormatString() {
    String dfs = config.getString("inputDateFormat");
    if (dfs == null)
      dfs = "yyyyMMdd";
    return dfs;
  }


  /**
   * Get InputStream to read file described in the 'input' configuration parameter. Default stdin.
   * @return InputStream
   * @throws Exception
   */
  protected InputStream getInput() throws Exception {
    String inp = config.getString("input");
    if (inp == null || inp.equals("stdin"))
      return System.in;
    else
      return new FileInputStream(inp);
  }

  /**
   * Get configuration parameter value as integer
   * @param s parameter name
   * @param def default value
   * @return s as an int, or def if it can't be translated
   */
  protected int getInt(String s, int def) {
    try {
      return Integer.parseInt(config.getString(s));
    } catch (Exception e) {
      return def;
    }
  }

  /**
   * Get Y format string from configuration
   * @return Y format string
   */
  protected String getYFormatString() {
    return config.getString("yFormatString");
  }

  /**
   * Get width from configuration
   * @return width
   */
  protected int getWidth() {
    return getInt("width", 1000);
  }

  /**
   * Get height from configuration
   * @return height
   */
  protected int getHeight() {
    return getInt("height", 300);
  }

  /**
   * Get box X position from configuration
   * @return box X position
   */
  protected int getBoxX() {
    return getInt("boxX", 55);
  }

  /**
   * Get box Y position from configuration
   * @return box Y position
   */
  protected int getBoxY() {
    return getInt("boxY", 25);
  }

  /**
   * Get box width from configuration
   * @return box width
   */
  protected int getBoxWidth() {
    return getInt("boxWidth", 890);
  }

  /**
   * Get box height from configuration
   * @return box height
   */
  protected int getBoxHeight() {
    return getInt("boxHeight", 2);
  }

  /**
   * Get left label text from configuration
   * @return left label
   */
  protected String getLeftLabel() {
    return config.getString("leftLabel");
  }

  /**
   * Get title text from configuration
   * @return title
   */
  protected String getTitle() {
    return config.getString("title");
  }

  /**
   * Get bottom label text from configuration
   * @return bottom label
   */
  protected String getBottomLabel() {
    return config.getString("bottomLabel");
  }

  /**
   * Get boolean flag from configuration if we want draw plot inside of screen window (true) or generate png file (false)
   * @return value of flag
   */
  protected boolean isToScreen() {
    return StringUtils.stringToBoolean(config.getString("toScreen"));
  }

  /**
   * Get boolean flag from configuration if we want output configuration to stdout after initialization
   * @return value of flag
   */
  protected boolean isOutputConfig() {
    return StringUtils.stringToBoolean(config.getString("outputConfig"));
  }

  /**
   * Get from configuration filename to generate png file with rendered plot
   * @return output file name
   */
  protected String getOutputFilename() {
    return config.getString("outputFilename");
  }

  /**
   * Get month tick format from configuration
   * @return month tick format
   */
  protected String getMonthTickFormat() {
    return config.getString("monthTickFormat");
  }

  /**
   * Get graph line specification from configuration
   * @return line specification
   */
  protected LineSpec getLineSpec(int col) {
    String s = config.getString("line." + col);
    if (s == null)
      return null;
    else
      return new LineSpec(s);
  }

  /**
   * Get graph point specification from configuration
   * @return point specification
   */
  protected PointSpec getPointSpec(int col) {
    String s = config.getString("point." + col);
    if (s == null)
      return null;
    else
      return new PointSpec(s);
  }

  /**
   * Get line specification for horizontal grid from configuration
   * @return point specification
   */
  protected LineSpec getHorizontalGrid() {
    String s = config.getString("horizontalGrid");
    if (s == null)
      s = "1,black";

    if (s.equals("off"))
      return null;
    else
      return new LineSpec(s);
  }

  /**
   * Get line specification for vertical grid from configuration
   * @return line specification
   */
  protected LineSpec getVerticalGrid() {
    String s = config.getString("verticalGrid");
    if (s == null)
      s = "1,black";

    if (s.equals("off"))
      return null;
    else
      return new LineSpec(s);
  }

  /**
   * Get background color from configuration
   * @return background color
   */
  protected Color getBackgroundColor() {
    String s = config.getString("background");
    if (s == null)
      return new Color(0xf8, 0xf8, 0xf8);
    else
      return ColorParser.getColor(s);
  }


  /**
   * Get axis background color from configuration
   * @return axis background color
   */
  protected Color getAxisBackgroundColor() {
    String s = config.getString("axisBackground");
    if (s == null)
      return Color.WHITE;
    else
      return ColorParser.getColor(s);
  }


  /**
   * Get bottom left label text from configuration
   * @return bottom left label
   */
  protected String getBottomLeftLabel() {
    return config.getString("bottomLeftLabel").replace("&copy;", "" + (char) 0xa9);
  }


  /**
   * Get bottom right label text from configuration
   * @return bottom right label
   */
  protected String getBottomRightLabel() {
    return config.getString("bottomRightLabel");
  }

  /**
   * Get tick specification from configuration
   * @return tick specification 
   */
  protected TickSpec getTickSpec() {
    String s = config.getString("ticks");
    if (s == null)
      s = "8,black";

    if (s.equals("off"))
      return null;
    else
      return new TickSpec(s);
  }

  /**
   * Get double[2] array of max and min x (date) limits from configuration
   * @return x limits
   */
  protected double[] getXLimits() {
    String s = config.getString("xLimits");
    if (s == null)
      return null;
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
    String[] ss = s.split(",");
    double[] d = new double[2];
    try {
      d[0] = J2kSec.fromDate(df.parse(ss[0]));
      d[1] = J2kSec.fromDate(df.parse(ss[1]));
    } catch (Exception e) {
      System.err.println("Illegal date format in xLimits.");
      d = null;
    }
    return d;
  }

  /**
   * Get double[2] array of max and min y (value) limits from configuration
   * @return y limits
   */
  protected double[] getYLimits() {
    String s = config.getString("yLimits");
    if (s == null)
      return null;
    String[] ss = s.split(",");
    double[] d = new double[2];
    d[0] = Double.parseDouble(ss[0]);
    d[1] = Double.parseDouble(ss[1]);
    return d;
  }

  /**
   * Create list of MatrixRenderers for according data matrices in data list
   * @return matrix renderers
   */
  protected List<MatrixRenderer> createMatrixRenderers() {
    List<MatrixRenderer> mrs = new ArrayList<MatrixRenderer>(data.size());
    for (DoubleMatrix2D dm : data) {
      MatrixRenderer mr = new MatrixRenderer(dm, false);
      mr.setLocation(getBoxX(), getBoxY(), getBoxWidth(), getBoxHeight());
      mr.setExtents(minTime, maxTime, minData, maxData);
      mrs.add(mr);
    }
    return mrs;
  }

  /**
   * Render loaded data to screen frame or to png file
   */
  protected void plot() throws PlotException {
    if (getMonthTickFormat() != null)
      SmartTick.setMonthTickerLabelFormatString(getMonthTickFormat());

    double[] d = getYLimits();
    if (d != null) {
      minData = d[0];
      maxData = d[1];
    }

    d = getXLimits();
    if (d != null) {
      minTime = d[0];
      maxTime = d[1];
    }

    Plot plot = new Plot();
    plot.setSize(getWidth(), getHeight());
    plot.setBackgroundColor(getBackgroundColor());
    List<MatrixRenderer> mrs = createMatrixRenderers();

    MatrixRenderer mr = mrs.get(0);
    mr.createDefaultAxis();
    AxisRenderer axis = new AxisRenderer(mr);
    axis.createDefault();
    int numHTicks = getBoxWidth() / 70;
    Object[] hTicks = SmartTick.autoTimeTick(minTime, maxTime, numHTicks);
    if (hTicks != null) {
      TickSpec ticks = getTickSpec();
      if (ticks != null) {
        axis.createBottomTicks((double[]) hTicks[0], ticks.width, ticks.color);
        axis.createTopTicks((double[]) hTicks[0], ticks.width, ticks.color);
      }
      LineSpec vert = getVerticalGrid();
      if (vert != null)
        axis.createVerticalGridLines((double[]) hTicks[0], vert.color, vert.stroke);
      axis.createBottomTickLabels((double[]) hTicks[0], (String[]) hTicks[1]);
    }

    int numVTicks = getBoxHeight() / 40;
    double[] vTicks = SmartTick.autoTick(minData, maxData, numVTicks, false);
    if (vTicks != null) {
      TickSpec ticks = getTickSpec();
      if (ticks != null) {
        axis.createLeftTicks(vTicks, ticks.width, ticks.color);
        axis.createRightTicks(vTicks, true, ticks.width, ticks.color);
      }
      LineSpec horiz = getHorizontalGrid();
      if (horiz != null) {
        axis.createHorizontalGridLines(vTicks, horiz.color, horiz.stroke);
        String format = getYFormatString();
        if (format == null)
          axis.createLeftTickLabels(vTicks, null);
        else
          axis.createFormattedLeftTickLabels(vTicks, format);
      }
    }

    mr.setAxis(axis);
    mr.getAxis().setBackgroundColor(getAxisBackgroundColor());
    mr.getAxis().setLeftLabelAsText(getLeftLabel());
    mr.getAxis().setTopLabelAsText(getTitle());
    mr.getAxis().setBottomLabelAsText(getBottomLabel());
    mr.getAxis().setBottomLeftLabelAsText(getBottomLeftLabel());
    mr.getAxis().setBottomRightLabelAsText(getBottomRightLabel());

    for (int i = 0; i < mrs.size(); i++) {
      // mr.createDefaultLineRenderers();
      LineSpec ls = getLineSpec(i);
      if (ls != null)
        mrs.get(i).createLineRenderer(0, ls.stroke, ls.color);
      // mr.createDefaultPointRenderers();

      PointSpec ps = getPointSpec(i);
      if (ps != null)
        mrs.get(i).createPointRenderer(0, ps.createDataPointRenderer());

      plot.addRenderer(mrs.get(i));
    }

    String of = getOutputFilename();
    image = plot.getAsBufferedImage(false);
    if (isToScreen())
      showOnScreen();

    if (of != null)
      writePNG(of);
  }

  /**
   * Class to describe axis tick appearance - color, stroke, color cycling etc
   */
  private static class TickSpec {
    public Color color;
    public double width;

    /**
     * Constructor
     * @param s comma-separated parameter string: width and color
     */
    public TickSpec(String s) {
      String[] ss = s.split(",");
      width = Float.parseFloat(ss[0].trim());
      color = ColorParser.getColor(ss[1].trim());
    }
  }

  /**
   * Class to describe line appearance - color, stroke, color cycling etc
   */
  private static class LineSpec {
    public Stroke stroke;
    public Color color;
    private static ColorCycler colorCycler = new ColorCycler();

    /**
     * Constructor
     * @param s comma-separated parameter string: width, color and stroke
     */
    public LineSpec(String s) {
      if (s == null) {
        stroke = new BasicStroke(1.0f);
        color = colorCycler.getNextColor();
      } else {
        String[] ss = s.split(",");
        float width = Float.parseFloat(ss[0].trim());
        color = ColorParser.getColor(ss[1].trim());
        ss[2] = ss[2].trim();
        if (ss[2].equals("dotted")) {
          stroke = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
              new float[] {1.0f, 2.0f}, 0.0f);
        } else if (ss[2].equals("solid")) {
          stroke = new BasicStroke(width);
        } else if (ss[2].equals("dashed")) {
          stroke = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
              new float[] {5.0f, 3.0f}, 0.0f);
        }
      }
    }
  }

  /**
   * Class to describe point appearance 
   */
  private static class PointSpec {
    public Color color;
    public float size;
    public char shape;
    public boolean filled;

    /**
     * Constructor
     * @param s comma-separated parameter string: size, shape and flag is filled
     */
    public PointSpec(String s) {
      String[] ss = s.split(",");
      size = Float.parseFloat(ss[0].trim());
      color = ColorParser.getColor(ss[1].trim());
      shape = ss[2].charAt(0);
      filled = StringUtils.stringToBoolean(ss[3]);
    }

    /**
     * Fabric method to get DataPointRenderer for this point 
     * @return data point renderer
     */
    public DataPointRenderer createDataPointRenderer() {
      DataPointRenderer dr = new DataPointRenderer(shape, size);
      dr.antiAlias = true;
      dr.color = color;
      dr.filled = filled;
      return dr;
    }
  }

  /** Outputs the plot to a png file.
   * @param fn the output filename
   */
  protected void writePNG(String fn) {
    try {
      PngEncoderB png = new PngEncoderB(image, false, PngEncoder.FILTER_NONE, 7);
      FileOutputStream out = new FileOutputStream(fn);
      byte[] bytes = png.pngEncode();
      out.write(bytes);
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Render plot w/ specified dimensions
   * @param w width
   * @param h height
   */
  protected void resize(int w, int h) throws PlotException {
    int lm = getBoxX();
    int rm = getWidth() - (getBoxX() + getBoxWidth());
    int tm = getBoxY();
    int bm = getHeight() - (getBoxY() + getBoxHeight());
    config.put("width", Integer.toString(w), false);
    config.put("height", Integer.toString(h), false);
    config.put("boxWidth", Integer.toString(w - lm - rm), false);
    config.put("boxHeight", Integer.toString(h - tm - bm), false);
    plot();
  }

  /**
   * Construct screen frame with rendered plot
   */
  protected void showOnScreen() {
    if (frame != null)
      return;

    frame = new JFrame("Plot");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(image.getWidth() + 30, image.getHeight() + 50);
    frame.setLocation(0, 0);
    frame.add(new JComponent() {
      private static final long serialVersionUID = -1;

      public void paint(Graphics g) {
        g.drawRect(9, 9, image.getWidth() + 1, image.getHeight() + 1);
        g.drawImage(image, 10, 10, null);
      }
    });
    frame.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        try {
          resize(frame.getWidth() - 30, frame.getHeight() - 50);
          frame.repaint();
        } catch (Exception ex) {
          LOGGER.error(ex.getMessage());
        }
      }
    });
    frame.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        char ch = e.getKeyChar();
        if (ch == 'q' || ch == 'Q' || ch == (char) 3)
          System.exit(0);
      }
    });
    frame.setVisible(true);
  }

  /**
   * Main method
   * @param args command line arguments
   */
  public static void main(String[] args) {
    new Plotter(args);
  }
}
