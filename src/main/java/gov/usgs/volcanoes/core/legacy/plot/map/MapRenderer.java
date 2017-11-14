package gov.usgs.volcanoes.core.legacy.plot.map;

import gov.usgs.volcanoes.core.data.LineData;
import gov.usgs.volcanoes.core.legacy.plot.Plot;
import gov.usgs.volcanoes.core.legacy.plot.decorate.SmartTick;
import gov.usgs.volcanoes.core.legacy.plot.render.BasicFrameRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.LineDataRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.LineRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.RenderedImageDataRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.Renderer;
import gov.usgs.volcanoes.core.legacy.plot.render.ShadowedTextRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.TextRenderer;
import gov.usgs.volcanoes.core.math.proj.GeoRange;
import gov.usgs.volcanoes.core.math.proj.Projection;
import gov.usgs.volcanoes.core.math.proj.TransverseMercator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>A renderer that renders a map</p>
 * 
 * @author Dan Cervelli
 */
public class MapRenderer extends BasicFrameRenderer {
  // private static final BufferedImage NO_BACKGROUND_IMAGE = new BufferedImage(8, 8,
  // BufferedImage.TYPE_4BYTE_ABGR);
  // private static final Paint NO_BACKGROUND_PAINT;
  protected Projection projection;
  protected GeoRange range;

  protected RenderedImage mapImage;
  protected LineData graticule;
  protected LineData box;
  protected List<LineData> userDefined = new ArrayList<LineData>();
  protected List<Renderer> labels;
  protected List<LineData> lineDatas;
  protected GeoLabelSet geoLabelSet;

  private static final double[] scales = new double[] {100000, 50000, 20000, 10000, 5000, 2000,
      1000, 500, 200, 100, 50, 20, 10, 5, 2, 1, 0.5, 0.2, 0.1, 0.05, 0.02};

  protected Renderer scaleRenderer;

  protected NumberFormat numberFormat = DecimalFormat.getInstance();

  /**
   * Constructor
   * @param r geographic coordinate range
   * @param p geographic projection to use while rendering
   */
  public MapRenderer(GeoRange r, Projection p) {
    numberFormat.setMaximumFractionDigits(3);
    range = r;
    projection = p;
    double[] extents = range.getProjectedExtents(projection);
    setExtents(extents[0], extents[1], extents[2], extents[3]);
  }

  /** 
   * Sets the location of the frame in the plot to be no bigger than wxh
   * @param x the x-pixel location
   * @param y the y-pixel location
   * @param w the width in pixels
   * @param h the height in pixels
   */
  public void setLocationByMaxBounds(int x, int y, int w, int h) {
    double[] extents = range.getProjectedExtents(projection);
    double aspect = (extents[3] - extents[2]) / (extents[1] - extents[0]);

    int height = (int) ((double) w * aspect);
    // System.out.println("try " + w + "x" + height);
    if (height <= h) {
      setLocation(x, y, w, height);
      return;
    }

    int width = (int) ((double) h * 1 / aspect);
    // System.out.println("try " + width + "x" + h);
    if (width <= w) {
      int ofs = w - width;
      setLocation(x + ofs / 2, y, width, h);
      return;
    }

    // System.out.println("fallback (out of aspect): " + w + "x" + h);
    setLocation(x, y, w, h);
  }

  /**  
   * Sets the location of the frame in the plot.
   * @param x the x-pixel location
   * @param y the y-pixel location
   * @param w the width in pixels
   */
  public void setLocation(int x, int y, int w) {
    double[] extents = range.getProjectedExtents(projection);
    double aspect = (extents[3] - extents[2]) / (extents[1] - extents[0]);

    int height = (int) ((double) w * aspect);
    // int height = (int)Math.ceil((double)w * aspect);
    // System.out.println("mr bounds: " + x + " " + y + " " + w + " " + height);
    setLocation(x, y, w, height);
  }

  /**  
   * Setter for geoLabel set
   * @param ls geoLabelSet
   */
  public void setGeoLabelSet(GeoLabelSet ls) {
    geoLabelSet = ls;
  }

  /**  
   * Setter for map image
   * @param ri map image
   */
  public void setMapImage(RenderedImage ri) {
    mapImage = ri;
  }

  /**  
   * Setter for graticule
   * @param ld graticule 
   */
  public void setGraticule(LineData ld) {
    graticule = ld;
  }

  /**  
   * Gets the map scale in meters/pixel.  Assumes that the plot aspect ratio
   * is properly set and that pixels are square.
   * 
   * @return the scale
   */
  public double getScale() {
    return (maxX - minX) / graphWidth;
  }

  /**  
   * Shortcut for createGraticule(pts, true, true, createLabels, createLabels, Color.BLACK)
   * @param pts # of ticks
   * @param createLabels if true, tick values are rendered
   */
  public void createGraticule(int pts, boolean createLabels) {
    createGraticule(pts, true, true, createLabels, createLabels, Color.BLACK);
  }

  /**  
   * Shortcut for createGraticule(pts, true, true, createLabels, createLabels, Color.BLACK)
   * @param pts # of ticks
   * @param xTickMarks if true, tick marks on X axis are rendered
   * @param yTickMarks if true, tick marks on Y axis are rendered
   * @param xTickValues if true, tick values on X axis are rendered
   * @param yTickValues if true, tick values on Y axis are rendered
   * @param labelColor color of label text
   */
  public void createGraticule(int pts, boolean xTickMarks, boolean yTickMarks, boolean xTickValues,
      boolean yTickValues, Color labelColor) {
    double[] gridX =
        SmartTick.autoTick(range.getWest(), range.getWest() + range.getLonRange(), pts, true, true);
    double[] gridY = SmartTick.autoTick(range.getSouth(), range.getNorth(), pts, true, true);
    double[] gridXreal = null;
    double[] gridYreal = null;
    if (xTickMarks) {
      gridXreal = gridX;
    } else {
      gridXreal = new double[2];
      gridXreal[0] = range.getWest();
      gridXreal[1] = range.getWest() + range.getLonRange();
    }
    if (yTickMarks) {
      gridYreal = gridY;
    } else {
      gridYreal = new double[2];
      gridYreal[0] = range.getSouth();
      gridYreal[1] = range.getNorth();
    }
    graticule = new LineData(gridXreal, gridYreal, false);
    graticule.applyProjection(projection);
    labels = new ArrayList<Renderer>();
    if (xTickValues && (gridX != null)) {
      for (int i = 1; i < gridX.length - 1; i++) {
        double nx = GeoRange.normalize(gridX[i]);
        Point2D.Double pt = new Point2D.Double(nx, gridY[0]);
        pt = projection.forward(pt);
        TextRenderer tr = new TextRenderer(pt.x, pt.y, numberFormat.format(nx));
        tr.transformer = this;
        tr.horizJustification = TextRenderer.CENTER;
        tr.vertJustification = TextRenderer.TOP;
        tr.color = labelColor;
        labels.add(tr);
      }
    }
    if (yTickValues && (gridY != null)) {
      for (int i = 1; i < gridY.length - 1; i++) {
        Point2D.Double pt = new Point2D.Double(gridX[0], gridY[i]);
        pt = projection.forward(pt);
        TextRenderer tr = new TextRenderer(pt.x, pt.y, numberFormat.format(gridY[i]));
        tr.transformer = this;
        tr.xBump = -2;
        tr.horizJustification = TextRenderer.RIGHT;
        tr.vertJustification = TextRenderer.CENTER;
        tr.color = labelColor;
        labels.add(tr);
      }
    }
  }

  /**  
   * Create a line: a user defined linear feature
   * @param fn : File name containing line data
   */
  public void createLine(String fn) {
    LineData ld = new LineData(fn);
    ld.applyProjection(projection);
    userDefined.add(ld);
    // userDefined = new LineData(fn);
    // userDefined.applyProjection(projection);
  }

  /**  
   * Create a box: a quadrilateral specified by the lower left hand corner, the width, and the height  
   * @param pts # How many point along each edge of the quadrilateral
   */
  public void createBox(int pts) {
    box = new LineData(range.getWest(), range.getSouth(), range.getLonRange(), range.getLatRange(),
        pts);
    box.applyProjection(projection);
  }

  /**  
   * Yield true iff bb intersects any element of boxes
   * @param boxes list of boxes to compare bb against
   * @param bb a Rectangle
   * @return true iff bb intersects any element of boxes
   */
  private boolean boxOverlaps(List<Rectangle> boxes, Rectangle bb) {
    for (Iterator<Rectangle> it2 = boxes.iterator(); it2.hasNext();) {
      Rectangle rect = it2.next();
      if (rect.intersects(bb))
        return false;
    }

    return true;
  }

  /**  
   * Yield location where to put a label, null if nowhere
   * @param boxes list of boxes to compare bb against
   * @param bb a Rectangle
   * @return Point where label can go; null if nowhere
   */
  private Point allowLabel(List<Rectangle> boxes, Rectangle bb) {
    if (boxOverlaps(boxes, bb))
      return new Point(0, 0);

    bb.translate(-bb.width - 7, 0);
    if (boxOverlaps(boxes, bb))
      return new Point(-bb.width - 7, 0);

    bb.translate(0, bb.height / 2);
    if (boxOverlaps(boxes, bb))
      return new Point(-bb.width - 7, bb.height / 2 + 3);

    bb.translate(bb.width + 7, 0);
    if (boxOverlaps(boxes, bb))
      return new Point(0, bb.height / 2 + 3);

    return null;
  }

  /**
   * Render labels om map
   * @param g the graphics object upon which to render
   */
  public void renderGeoLabels(Graphics2D g) {
    ArrayList<Rectangle> boxes = new ArrayList<Rectangle>();

    if (geoLabelSet != null) {
      List<GeoLabel> gls = geoLabelSet.getGeoLabels();
      for (GeoLabel gl : gls) {
        if (!range.contains(gl.location))
          continue;

        Point2D.Double pt = projection.forward(gl.location);

        if (gl.shadow) {
          ShadowedTextRenderer str = new ShadowedTextRenderer(pt.x, pt.y, gl.text);
          str.xBump = 4;
          str.yBump = -1;
          str.color = gl.color;
          str.font = gl.font;
          str.transformer = this;
          Rectangle bb = str.getBoundingBox(g);
          Point offset = allowLabel(boxes, bb);
          if (offset != null) {
            str.xBump += offset.x;
            str.yBump += offset.y;
            boxes.add(bb);
            str.render(g);
          }
        } else {
          TextRenderer tr = new TextRenderer(pt.x, pt.y, gl.text);
          tr.font = gl.font;
          tr.xBump = 4;
          tr.yBump = -1;
          tr.color = gl.color;
          tr.transformer = this;

          Rectangle bb = tr.getBoundingBox(g);
          Point offset = allowLabel(boxes, bb);
          if (offset != null) {
            tr.xBump += offset.x;
            tr.yBump += offset.y;
            boxes.add(bb);
            tr.render(g);
          }
        }


        if (gl.marker != null) {
          gl.marker.transformer = this;
          gl.marker.x = pt.x;
          gl.marker.y = pt.y;
          gl.marker.render(g);
        }


      }
    }
  }

  /**
   * Create renderer for scale with factor = 1 and black
   */
  public void createScaleRenderer() {
    createScaleRenderer(1.0, graphX, graphY + graphHeight + 32, Color.BLACK);
  }

  /**
   * Create renderer for scale with default color
   * @param f factor
   * @param x x position
   * @param y y position
   */
  public void createScaleRenderer(final double f, final int x, final int y) {
    createScaleRenderer(f, x, y, null);
  }

  /**
   * Create renderer for scale
   * @param f factor
   * @param x x position
   * @param y y position
   * @param color color
   */
  public void createScaleRenderer(final double f, final int x, final int y, final Color color) {
    scaleRenderer = new Renderer() {
      public void render(Graphics2D g) {
        Font origFont = g.getFont();
        if (color != null)
          g.setColor(color);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setStroke(new BasicStroke(2.0f));
        double scale = getScale() / 1000 * f;
        Line2D.Double line = new Line2D.Double();
        double len = 0;
        int index = -1;
        double cur = 1E300;
        for (int i = 0; i < scales.length; i++) {
          double s = 1.0 / scale * scales[i];
          double d = Math.abs(s - 150);
          if (d < cur) {
            len = s;
            index = i;
            cur = d;
          }
        }
        // line.setLine(graphX, graphY + graphHeight + 32, graphX + len, graphY + graphHeight + 32);
        // line.setLine(graphX, graphY + graphHeight + 19, graphX + len, graphY + graphHeight + 19);
        line.setLine(x, y, x + len, y);
        g.draw(line);
        // g.drawString(scales[index] + " km", (float)graphX, (float)(graphY + graphHeight + 30));
        g.drawString(scales[index] + " km", (float) x, (float) (y - 2));
        g.setFont(origFont);
      }
    };
  }

  /**
   * Render map
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {
    super.render(g);
    // CodeTimer ct = new CodeTimer("MapRenderer.render");
    double[] extents = range.getProjectedExtents(projection);
    // System.out.printf("mr extents: %f, %f, %f, %f\n", extents[0], extents[1], extents[2],
    // extents[3]);

    LineDataRenderer ldr = null;
    LineDataRenderer sdr = null;

    if (mapImage != null) {
      RenderedImageDataRenderer mapImageRenderer = new RenderedImageDataRenderer(mapImage);
      mapImageRenderer.setLocation(this);
      mapImageRenderer.setExtents(extents[0], extents[1], extents[2], extents[3]);
      mapImageRenderer.setDataExtents(extents[0], extents[1], extents[2], extents[3]);
      mapImageRenderer.render(g);
    }
    // ct.mark("mapImage");
    if (lineDatas != null) {
      for (LineData ld : lineDatas) {
        ld.applyProjection(projection);
        LineDataRenderer lldr = new LineDataRenderer(ld);
        lldr.setAntiAlias(true);
        lldr.setLocation(this);
        lldr.setExtents(extents[0], extents[1], extents[2], extents[3]);
        lldr.render(g);
      }
    }
    // ct.mark("lineData");

    if (userDefined != null) {
      for (LineData ld : userDefined) {
        sdr = new LineDataRenderer(ld);
        sdr.color = ld.color;
        sdr.stroke = ld.stroke;
        sdr.setLocation(this);
        sdr.setExtents(extents[0], extents[1], extents[2], extents[3]);
        sdr.render(g);
      }
    }

    if (box != null) {
      ldr = new LineDataRenderer(box);
      ldr.setLocation(this);
      ldr.setExtents(extents[0], extents[1], extents[2], extents[3]);
      // ldr.setPaint(NO_BACKGROUND_PAINT);
      // ldr.render(g);
      ldr.render(g);
    }
    // ct.mark("box");

    if (graticule != null) {
      LineDataRenderer gldr = new LineDataRenderer(graticule);
      gldr.setAntiAlias(true);
      gldr.setLocation(this);
      gldr.setExtents(extents[0], extents[1], extents[2], extents[3]);
      gldr.stroke = LineRenderer.DASHED_STROKE;
      gldr.color = Color.DARK_GRAY;
      gldr.render(g);
    }
    // ct.mark("graticule");

    if (labels != null) {
      for (Renderer label : labels)
        label.render(g);
    }
    // ct.mark("labels");

    if (scaleRenderer != null)
      scaleRenderer.render(g);

    if (geoLabelSet != null)
      renderGeoLabels(g);

    // ct.mark("geolabels");
    // g.setColor(Color.RED);
    // g.drawRect(graphX, graphY, getGraphWidth(), getGraphHeight());
    // g.drawLine(graphX, graphY + getGraphHeight() / 2, graphX + getGraphWidth(), graphY +
    // getGraphHeight() / 2);
    // g.drawLine(graphX + getGraphWidth() / 2, graphY, graphX + getGraphWidth() / 2, graphY +
    // getGraphHeight());
    // ct.stop();
  }

  /**
   * Main method
   * @param args command line arguments
   */
  public static void main(String[] args) {
    GeoImageSet images = new GeoImageSet(args[0]);
    GeoLabelSet labels = new GeoLabelSet(args[1]);
    GeoRange range = new GeoRange(Double.parseDouble(args[2]), Double.parseDouble(args[3]),
        Double.parseDouble(args[4]), Double.parseDouble(args[5]));

    Plot plot = new Plot();
    plot.setBackgroundColor(Color.white);
    plot.setSize(1200, 900);

    TransverseMercator proj = new TransverseMercator();
    Point2D.Double origin = range.getCenter();
    proj.setup(origin, 0, 0);

    MapRenderer mr = new MapRenderer(range, proj);
    mr.setLocation(50, 50, 800);
    mr.setGeoLabelSet(labels.getSubset(range));
    RenderedImage ri = images.getMapBackground(proj, range, 800);
    mr.setMapImage(ri);
    mr.createBox(8);
    mr.createGraticule(8, true);
    mr.getDefaultTranslation(plot.getHeight());
    plot.addRenderer(mr);
    plot.quickShow();
  }
}
