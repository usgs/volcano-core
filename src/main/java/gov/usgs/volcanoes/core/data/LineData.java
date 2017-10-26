package gov.usgs.volcanoes.core.data;

import gov.usgs.plot.decorate.SmartTick;
import gov.usgs.proj.GeoRange;
import gov.usgs.proj.Projection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.transform.Transformer;

/**
 * <p>LineData is a class that represents a collection of lines.  This is used
 * to hold the set of lines that should be drawn on spatial plots.</p> 
 *
 * TODO: use new optimized version
 * TODO: cache line files
 * TODO: provide copy constructor or clone
 *
 * @author Dan Cervelli
 */
public class LineData {
  private List<Point2D.Double> points;
  public Color color = Color.BLACK;
  public Stroke stroke = new BasicStroke(1.0f);

  /** Generic constructor
   */
  public LineData() {
    points = new ArrayList<Point2D.Double>();
  }

  /** Constructor that reads a data file.
   * @param fn the data filename
   */
  public LineData(String fn) {
    this();
    readLineDataFile(fn);
  }

  /** Shortcut for LineData(x, y, true )
   * 
   * @param x the x values for the grid
   * @param y the y values for the grid
   */
  public LineData(double[] x, double[] y) {
    this(x, y, true);
  }

  /** Constructor that creates a grid.  
   * 
   * @param x the x values for the grid
   * @param y the y values for the grid
   * @param edge if true, leave space on edge
   */
  public LineData(double[] x, double[] y, boolean edge) {
    this();
    if (x != null) {
      for (int i = (edge ? 0 : 1); i < x.length - (edge ? 0 : 1); i++) {
        if (y != null) {
          for (int j = 0; j < y.length; j++)
            points.add(new Point2D.Double(x[i], y[j]));
        }
        points.add(new Point2D.Double(Double.NaN, Double.NaN));
      }
    }
    if (y != null) {
      for (int i = (edge ? 0 : 1); i < y.length - (edge ? 0 : 1); i++) {
        if (x != null) {
          for (int j = 0; j < x.length; j++)
            points.add(new Point2D.Double(x[j], y[i]));
        }
        points.add(new Point2D.Double(Double.NaN, Double.NaN));
      }
    }
  }

  /**
   * Constructor that creates a box (quadrilateral)    
   * @param x west coord of quadrilateral  
   * @param y south coord of quadrilateral
   * @param width
   * @param height
   * @param pts How many point along each edge of the quadrilateral
   */
  public LineData(double x, double y, double width, double height, int pts) {
    this();
    points.add(new Point2D.Double(x, y));
    for (int i = 0; i <= pts; i++)
      points.add(new Point2D.Double(x + ((double) i * width / (double) pts), y));
    for (int i = 0; i <= pts; i++)
      points.add(new Point2D.Double(x + width, y + ((double) i * height / (double) pts)));
    for (int i = pts; i >= 0; i--)
      points.add(new Point2D.Double(x + ((double) i * width / (double) pts), y + height));
    for (int i = pts; i >= 0; i--)
      points.add(new Point2D.Double(x, y + ((double) i * height / (double) pts)));

  }

  /**
   * Creates a grid over a GeoRange 
   * @param range GeoRange grid is to cover
   * @param pts Count of grid lines
   */
  public static LineData createLonLatGrid(GeoRange range, int pts) {
    double[] x = SmartTick.autoTick(range.getWest(), range.getEast(), pts, false);
    double[] y = SmartTick.autoTick(range.getSouth(), range.getNorth(), pts, false);

    return new LineData(x, y);
  }

  /** Writes the line information to a file in a format that Mathematica
   * can read.  This is used for creating a file that LiveGraphics3D 
   * can use.
   * @param out the output stream
   * @param minX the minimum x-value for clipping
   * @param maxX the maximum x-value for clipping
   * @param minY the minimum y-value for clipping
   * @param maxY the maximum y-value for clipping
   * @param zVal an arbitrary z-value to assign to the output coordinates
   */
  public void toMathematica(PrintWriter out, double minX, double maxX, double minY, double maxY,
      double zVal) {
    Point2D.Double pt1, pt2;
    for (int i = 0; i < points.size() - 1; i++) {
      pt1 = points.get(i);
      if (Double.isNaN(pt1.x) || Double.isNaN(pt1.y))
        continue;
      pt2 = points.get(i + 1);
      if (Double.isNaN(pt2.x) || Double.isNaN(pt2.y))
        continue;
      if ((pt1.x > minX && pt1.x < maxX && pt2.x > minX && pt2.x < maxX)
          && (pt1.y > minY && pt1.y < maxY && pt2.y > minY && pt2.y < maxY)) {
        out.println("Line[{{" + pt1.x + "," + pt1.y + "," + zVal + "},{" + pt2.x + "," + pt2.y + ","
            + zVal + "}}],");
      }
    }
  }

  /** Reads a line data file.  The format is very simple (but very strict):
   * x,y pairs that end with a '&gt;'.  Blank lines and lines that begin with
   * '#' are ignored.
   * Example:<br>
   * 0 0<br>
   * 0 1<br>
   * 1 1<br>
   * 1 0<br>
   * 0 0<br>
   * &gt;<br>
   * <br>
   * These should be cached such that when this is called on a previously
   * read files it grabs it from a memory cache.
   * @param fn the input filename
   */
  public void readLineDataFile(String fn) {
    try {
      File file = new File(fn);
      if (!file.exists())
        return;

      points.clear();
      BufferedReader in = new BufferedReader(new FileReader(file));
      String s;
      while ((s = in.readLine()) != null) {
        if (s.trim().length() == 0 || s.startsWith("#"))
          continue;

        if (s.toUpperCase().startsWith("COLOR")) {
          color = (Color) Color.class.getDeclaredField(s.split("=")[1].toUpperCase()).get(null);
          continue;
        }

        if (s.toUpperCase().startsWith("STROKE")) {
          stroke = new BasicStroke(Float.parseFloat(s.split("=")[1]));
          continue;
        }
        Point2D.Double pt = new Point2D.Double();
        if (s.startsWith(">") || s.indexOf("NaN") != -1) {
          pt.x = Double.NaN;
          pt.y = Double.NaN;
        } else {
          StringTokenizer st = new StringTokenizer(s, " ,\t");
          pt.x = Double.parseDouble(st.nextToken());
          pt.y = Double.parseDouble(st.nextToken());
          if (pt.x > 180.0)
            pt.x = -(360 - pt.x);
        }
        points.add(pt);
      }
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Get transformed form of data
   * @param xform Transformer
   * @return transformed data
   */
  public GeneralPath getPolygon(Transformer xform) {
    GeneralPath gp = new GeneralPath();
    boolean first = true;
    for (Point2D.Double pt : points) {
      if (Double.isNaN(pt.x) || Double.isNaN(pt.y)) {
        first = true;
        gp.closePath();
        continue;
      }
      if (first) {
        first = false;
        gp.moveTo((float) xform.getXPixel(pt.x) + 1, (float) xform.getYPixel(pt.y) + 1);
      } else {
        gp.lineTo((float) xform.getXPixel(pt.x) + 1, (float) xform.getYPixel(pt.y) + 1);
      }
    }
    return gp;
  }

  /** Applies a projection to the points.
   * @param proj the projection
   */
  public void applyProjection(Projection proj) {
    ArrayList<Point2D.Double> newPoints = new ArrayList<Point2D.Double>(points.size());
    for (Point2D.Double pt : points)
      newPoints.add(proj.forward(pt));
    points = newPoints;
  }

  /** Gets the points that make up the lines.
   * @return the points
   */
  public List<Point2D.Double> getPoints() {
    return points;
  }
}
