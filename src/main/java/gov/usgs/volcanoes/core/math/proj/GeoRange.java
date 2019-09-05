package gov.usgs.volcanoes.core.math.proj;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

/**
 * This class encapsulated information about a lon/lat rectangle.  
 * 
 * @author Dan Cervelli
 */
public class GeoRange {
  private double west;
  private double east;
  private double south;
  private double north;

  /**
   * Default constructor.
   */
  public GeoRange() {
    west = east = south = north = Double.NaN;
  }

  /**
   * Constructor.
   * @param copy georange
   */
  public GeoRange(GeoRange copy) {
    west = copy.west;
    east = copy.east;
    south = copy.south;
    north = copy.north;
  }

  /**
   * Constructor.
   * @param rect rectangle
   */
  public GeoRange(Rectangle2D.Double rect) {
    setWest(rect.getMinX());
    setEast(rect.getMaxX());
    setSouth(rect.getMinY());
    setNorth(rect.getMaxY());
  }

  /**
   * Constructor.
   * @param w west longitude
   * @param e east longitude
   * @param s south latitude
   * @param n north latitude
   */
  public GeoRange(double w, double e, double s, double n) {
    setWest(w);
    setEast(e);
    setSouth(s);
    setNorth(n);
  }

  /**
   * Constructor.
   * @param proj projection
   * @param center center coordinate
   * @param xm x range meters?
   * @param ym y range meters?
   */
  public GeoRange(Projection proj, Point2D.Double center, double xm, double ym) {
    Point2D.Double pt = proj.forward(center);
    double left = pt.x - xm / 2;
    double right = pt.x + xm / 2;
    double top = pt.y + ym / 2;
    double bottom = pt.y - ym / 2;
    set(proj, left, right, bottom, top);
  }

  public GeoRange(Projection proj, double left, double right, double bottom, double top) {
    set(proj, left, right, bottom, top);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return new Double(west).hashCode() + new Double(east).hashCode() + new Double(south).hashCode()
        + new Double(north).hashCode();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof GeoRange)) {
      return false;
    }
    GeoRange gr = (GeoRange) obj;
    return west == gr.west && east == gr.east && north == gr.north && south == gr.south;
  }


  public boolean isValid() {
    return !Double.isNaN(west) && !Double.isNaN(east) && !Double.isNaN(south)
        && !Double.isNaN(north);
  }

  /**
   * Set range.
   * @param proj projection
   * @param left left coordinate
   * @param right right coordinate
   * @param bottom bottom coordinate
   * @param top top coordinate
   */
  public void set(Projection proj, double left, double right, double bottom, double top) {
    Point2D.Double[] pt = new Point2D.Double[8];
    pt[0] = new Point2D.Double(left, top);
    pt[1] = new Point2D.Double(left, bottom);
    pt[2] = new Point2D.Double(right, top);
    pt[3] = new Point2D.Double(right, bottom);
    double dx = right - left;
    double dy = top - bottom;
    // System.out.println("dx: " + dx + " dy: " + dy);
    pt[4] = new Point2D.Double(left + dx / 2, top);
    pt[5] = new Point2D.Double(left + dx / 2, bottom);
    pt[6] = new Point2D.Double(left, bottom + dy / 2);
    pt[7] = new Point2D.Double(right, bottom + dy / 2);

    west = Double.MAX_VALUE;
    south = Double.MAX_VALUE;
    east = -Double.MAX_VALUE;
    north = -Double.MAX_VALUE;

    for (int i = 0; i < 8; i++) {
      // System.out.print(pt[i]);
      pt[i] = proj.inverse(pt[i]);
      // System.out.println(" -> " + pt[i]);
      west = Math.min(west, pt[i].x);
      south = Math.min(south, pt[i].y);
      east = Math.max(east, pt[i].x);
      north = Math.max(north, pt[i].y);
    }
    setEast(east);
    setWest(west);
    setSouth(south);
    setNorth(north);
  }

  /**
   * Pad latitude/longitude.
   * @param lonPad pad value for longitude
   * @param latPad pad value for latitude
   */
  public void pad(double lonPad, double latPad) {
    double lr = getLonRange();
    if (lr + lonPad > 360.0) {
      lonPad = 360.0 - lr;
    }
    setWest(west - lonPad / 2);
    setEast(east + lonPad / 2);

    lr = getLatRange();
    if (lr + latPad > 180.0) {
      latPad = 180.0 - lr;
    }
    setNorth(north + latPad / 2);
    setSouth(south - latPad / 2);
  }

  public void padPercent(double lonPad, double latPad) {
    pad(getLonRange() * lonPad, getLatRange() * latPad);
  }

  /**
   * Include point in range.
   * @param lonLat coordinate
   * @param singleBuffer buffer
   */
  public void includePoint(Point2D.Double lonLat, double singleBuffer) {
    if (Double.isNaN(west)) {
      west = lonLat.x - singleBuffer;
      east = lonLat.x + singleBuffer;
      south = lonLat.y - singleBuffer;
      north = lonLat.y + singleBuffer;
      return;
    }

    if (contains(lonLat)) {
      return;
    }

    setSouth(Math.min(south, lonLat.y));
    setNorth(Math.max(north, lonLat.y));

    if (!containsLongitude(lonLat.x)) {
      double lrw = Math.abs(getLonRange(west, lonLat.x));
      if (lrw > 180) {
        lrw = Math.abs(lrw - 360);
      }
      double lre = Math.abs(getLonRange(east, lonLat.x));
      if (lre > 180) {
        lre = Math.abs(lre - 360);
      }
      if (lrw <= lre) {
        setWest(lonLat.x);
      } else {
        setEast(lonLat.x);

      }
    }
  }

  /**
   * Get scale.
   * @param proj projection
   * @param w width
   * @param h height
   * @return
   */
  public double getScale(Projection proj, int w, int h) {
    double[] r = getProjectedExtents(proj);
    double scale = Math.max((r[1] - r[0]) / w, (r[3] - r[2]) / h);
    return scale;
  }

  /**
   * Flip east and west.
   */
  public void flipEastWest() {
    double t = west;
    west = east;
    east = t;
  }

  /**
   * Set West coordinate.
   * @param w west coordinate
   */
  public void setWest(double w) {
    while (w >= 180) {
      w -= 360;
    }
    while (w < -180) {
      w += 360;
    }

    west = w;
  }

  /**
   * Set East coordinate.
   * @param e east coordinate
   */
  public void setEast(double e) {
    while (e > 180) {
      e -= 360;
    }
    while (e <= -180) {
      e += 360;
    }

    east = e;
  }

  /**
   * Set North coordinate.
   * @param n north coordinate
   */
  public void setNorth(double n) {
    while (n > 90) {
      n -= 180;
    }
    while (n < 0) {
      n += 180;
    }

    north = n;
  }

  /**
   * Set South coordinate.
   * @param s south coordinate
   */
  public void setSouth(double s) {
    while (s > 90) {
      s -= 180;
    }
    while (s < -90) {
      s += 180;
    }

    south = s;
  }

  /**
   * Normalize.
   * @param d longitude range in degrees
   * @return
   */
  public static double normalize(double d) {
    d = d % 360.0;
    while (d > 180) {
      d -= 360;
    }
    while (d < -180) {
      d += 360;
    }

    return d;
  }

  public double getNorth() {
    return north;
  }

  public double getSouth() {
    return south;
  }

  public double getWest() {
    return west;
  }

  public double getEast() {
    return east;
  }

  /**
   * Gets the span of longitude between a western longitude and an eastern
   * longitude.  Always returns a positive value between 0 and 360, inclusive.
   * Assumes inputs are correctly normalized.
   * 
   * @param w west edge
   * @param e east edge
   * @return
   */
  public static double getLonRange(double w, double e) {
    double d = e - w;
    if (e < w) {
      return 360 + d;
    } else {
      return d;
    }
  }

  /**
   * Get longitude range.
   * @return degrees
   */
  public double getLonRange() {
    double w = west;
    if (w > east) {
      w -= 360;
    }
    return Math.abs(w - east);
  }

  public double getLatRange() {
    return Math.abs(north - south);
  }

  public Point2D.Double getCenter() {
    return new Point2D.Double(normalize(west + (getLonRange()) / 2), north - (north - south) / 2);
  }

  public Rectangle2D.Double getRectangle() {
    return new Rectangle2D.Double(west, south, getLonRange(), getLatRange());
  }

  /**
   * Check if point is contained in the range.
   * @param pt point
   * @return
   */
  public boolean contains(Point2D.Double pt) {
    double lat = pt.y;
    boolean inLat = lat >= south && lat <= north;
    if (!inLat) {
      return false;
    }

    double lon = normalize(pt.x);
    if (west < east) {
      return west <= lon && lon <= east;
    } else {
      return (west <= lon && lon <= 180) || (lon <= east && lon >= -180);
    }
  }

  /**
   * Check if longitude is within range.
   * @param lon longitude in dd.
   * @return
   */
  public boolean containsLongitude(double lon) {
    lon = normalize(lon);
    if (west < east) {
      return west <= lon && lon <= east;
    } else {
      return (west <= lon && lon <= 180) || (lon <= east && lon >= -180);
    }
  }

  /**
   * See if this range overlaps with given range.
   * @param range other range
   * @return
   */
  public boolean overlaps(GeoRange range) {
    Rectangle2D.Double[] rects = new Rectangle2D.Double[4];
    int numRects = 2;
    rects[0] = getRectangle();
    rects[1] = range.getRectangle();
    if (rects[0].x + rects[0].width > 180) {
      double d = rects[0].x + rects[0].width - 180;
      rects[0].width = 180 - rects[0].x;
      rects[numRects++] = new Rectangle2D.Double(-180, rects[0].y, d, rects[0].height);
    }
    if (rects[1].x + rects[1].width > 180) {
      double d = rects[1].x + rects[1].width - 180;
      rects[1].width = 180 - rects[1].x;
      rects[numRects++] = new Rectangle2D.Double(-180, rects[1].y, d, rects[1].height);
    }
    for (int i = 0; i < numRects; i++) {
      for (int j = i + 1; j < numRects; j++) {
        if (rects[i].intersects(rects[j])) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Get projected extents.
   * @param proj projection
   * @return
   */
  public double[] getProjectedExtents(Projection proj) {
    Point2D.Double[] pt = new Point2D.Double[8];
    pt[0] = new Point2D.Double(west, north);
    pt[1] = new Point2D.Double(west, south);
    pt[2] = new Point2D.Double(east, north);
    pt[3] = new Point2D.Double(east, south);
    pt[4] = new Point2D.Double(normalize(west + getLonRange() / 2), south);
    pt[5] = new Point2D.Double(normalize(west + getLonRange() / 2), north);
    pt[6] = new Point2D.Double(west, south + (north - south) / 2);
    pt[7] = new Point2D.Double(east, south + (north - south) / 2);

    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;

    for (int i = 0; i < 8; i++) {
      // System.out.print(pt[i]);
      pt[i] = proj.forward(pt[i]);
      // System.out.println(" " + pt[i]);
      minX = Math.min(minX, pt[i].x);
      minY = Math.min(minY, pt[i].y);
      maxX = Math.max(maxX, pt[i].x);
      maxY = Math.max(maxY, pt[i].y);
    }

    return new double[] {minX, maxX, minY, maxY};
  }

  /**
   * Get bounding box.
   * @param pts collection of points
   * @return
   */
  public static GeoRange getBoundingBox(Collection<Point2D.Double> pts) {
    if (pts == null || pts.size() <= 0) {
      return null;
    }

    Rectangle2D.Double rect = null;
    for (Point2D.Double pt : pts) {
      if (pt != null) {
        if (rect == null) {
          rect = new Rectangle2D.Double(pt.x, pt.y, 0, 0);
        }
        rect.add(pt);
      }
    }

    // double nw = rect.width * 1.3;
    // double nh = rect.height * 1.3;
    double n = Math.max(rect.width, rect.height);
    double nw = n * 2.0;
    double nh = n * 2.0;
    rect.x -= (nw - rect.width) / 2;
    rect.y -= (nh - rect.height) / 2;
    rect.width = nw;
    rect.height = nh;

    if (rect.width == 0 || rect.height == 0) {
      rect.x -= 0.15;
      rect.y -= 0.15;
      rect.width = 0.3;
      rect.height = 0.3;
    }

    GeoRange gr = new GeoRange(rect);
    return gr;
  }

  public String toString() {
    return "GeoRange: " + west + "," + east + "," + south + "," + north;
  }

  /**
   * Test.
   */
  public static void test() {
    Point2D.Double[] tps = new Point2D.Double[] {new Point2D.Double(165, 60),
        new Point2D.Double(170, 60), new Point2D.Double(175, 60), new Point2D.Double(-180, 60),
        new Point2D.Double(180, 60), new Point2D.Double(-175, 60), new Point2D.Double(-172, 60),
        new Point2D.Double(-170, 60), new Point2D.Double(-160, 60)};

    GeoRange[] grs = new GeoRange[] {
        // new GeoRange(170, -170, 50, 70),
        // new GeoRange(175, -170, 50, 70),
        // new GeoRange(-175, -170, 50, 70),
        // new GeoRange(-180, -170, 50, 70),
        // new GeoRange(180, -170, 50, 70),


        // new GeoRange(176, 178, 50, 70),
        // new GeoRange(177, 179, 50, 70),
        // new GeoRange(178, -179.9, 50, 70),
        // new GeoRange(179, -179, 50, 70),
        // new GeoRange(179.9, -178, 50, 70),
        // new GeoRange(-179, -177, 50, 70),
        // new GeoRange(-178, -176, 50, 70),
        // new GeoRange(-177, -175, 50, 70),
        // new GeoRange(-176, -174, 50, 70),
        // new GeoRange(179.9, -151, 51, 62),
        // new GeoRange(-180, -90, 45, 90),
        // new GeoRange(170, -130, 45, 78),
        // new GeoRange(-1, 110, -40, 40),
        // new GeoRange(0, 110, -40, 40),
        // new GeoRange(90, 180, 0, 45)
        // new GeoRange(-170, -150, 0, 45),
        // new GeoRange(170, -171, 0, 45),
        new GeoRange(-170, 170, -45, 45)};

    for (int i = 0; i < grs.length; i++) {
      System.out.println(
          grs[i] + "\n\tLonRange: " + grs[i].getLonRange() + "\n\tCenter: " + grs[i].getCenter());
      for (int j = 0; j < tps.length; j++) {
        System.out.println("\t\t" + tps[j] + " in " + grs[i] + ": " + grs[i].contains(tps[j]));
      }
    }

    for (int i = 0; i < grs.length; i++) {
      for (int j = i; j < grs.length; j++) {
        System.out.println(grs[i] + " overlaps " + grs[j] + ": " + grs[i].overlaps(grs[j]) + "\n");
      }
    }
  }

  /**
   * Main method.
   * @param args arguments
   */
  public static void main(String[] args) {
    test();

    System.out.println(getLonRange(179.12659626750332, -150));
    System.out.println(getLonRange(-150, -100));
    System.out.println(getLonRange(100, 150));
    System.out.println(getLonRange(-150, 100));
    System.out.println(getLonRange(170, -170));
    System.out.println(getLonRange(180, -170));
    System.out.println(getLonRange(-179, 179));

    double[] d = new double[] {-1E300, -720, -510, -360, -270, -180, -90, 0, 90, 180, 270, 360, 510,
        720, 1E300};
    for (int i = 0; i < d.length; i++) {
      System.out.println(d[i] + " -> " + d[i] % 360.0);
    }
  }
}
