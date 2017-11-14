package gov.usgs.volcanoes.core.math.proj;

import gov.usgs.volcanoes.core.CodeTimer;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

/**
 * @author Dan Cervelli
 */
abstract public class Projection {
  protected String name;
  protected static final double DEG2RAD = Math.PI / 180;
  protected static final double RAD2DEG = 180.0 / Math.PI;
  protected Ellipsoid ellipsoid = Ellipsoid.ELLIPSOIDS[23];

  public void setEllipsoid(Ellipsoid e) {
    ellipsoid = e;
  }

  public Ellipsoid getEllipsoid() {
    return ellipsoid;
  }

  public String getName() {
    return name;
  }

  public void setOrigin(Point2D.Double o) {}

  abstract public Point2D.Double forward(Point2D.Double lonLat);

  abstract public Point2D.Double inverse(Point2D.Double xy);

  abstract public double getScale(Point2D.Double lonLat);

  public FastProjector getFastProjector() {
    return new FastProjector() {
      public void forward(Point2D.Double pt) {
        Point2D.Double pt2 = Projection.this.forward(pt);
        pt.x = pt2.x;
        pt.y = pt2.y;
      }

      public void inverse(Point2D.Double pt) {
        Point2D.Double pt2 = Projection.this.inverse(pt);
        pt.x = pt2.x;
        pt.y = pt2.y;
      }
    };
  }

  public String getInverseJavaScript() {
    return "function inverse(x, y) { return new Array(0, 0); }";
  }

  public Point2D.Double[] forward(Point2D.Double[] lonLat) {
    Point2D.Double[] result = new Point2D.Double[lonLat.length];
    for (int i = 0; i < lonLat.length; i++)
      result[i] = forward(lonLat[i]);
    return result;
  }

  public Point2D.Double[] inverse(Point2D.Double[] xy) {
    Point2D.Double[] result = new Point2D.Double[xy.length];
    for (int i = 0; i < xy.length; i++)
      result[i] = inverse(xy[i]);
    return result;
  }

  public double[] getProjectedExtents(GeoRange gr) {
    Point2D.Double[] pt = new Point2D.Double[8];
    pt[0] = new Point2D.Double(gr.getWest(), gr.getNorth());
    pt[1] = new Point2D.Double(gr.getWest(), gr.getSouth());
    pt[2] = new Point2D.Double(gr.getEast(), gr.getNorth());
    pt[3] = new Point2D.Double(gr.getEast(), gr.getSouth());
    pt[4] =
        new Point2D.Double(GeoRange.normalize(gr.getWest() + gr.getLonRange() / 2), gr.getSouth());
    pt[5] =
        new Point2D.Double(GeoRange.normalize(gr.getWest() + gr.getLonRange() / 2), gr.getNorth());
    pt[6] = new Point2D.Double(gr.getWest(), gr.getSouth() + (gr.getNorth() - gr.getSouth()) / 2);
    pt[7] = new Point2D.Double(gr.getEast(), gr.getSouth() + (gr.getNorth() - gr.getSouth()) / 2);

    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;

    for (int i = 0; i < 8; i++) {
      // System.out.print(pt[i]);
      pt[i] = forward(pt[i]);
      // System.out.println(" " + pt[i]);
      minX = Math.min(minX, pt[i].x);
      minY = Math.min(minY, pt[i].y);
      maxX = Math.max(maxX, pt[i].x);
      maxY = Math.max(maxY, pt[i].y);
    }

    return new double[] {minX, maxX, minY, maxY};
  }

  public GeoRange getGeoRange(Point2D.Double center, double xm, double ym) {
    Point2D.Double pt = forward(center);
    double left = pt.x - xm / 2;
    double right = pt.x + xm / 2;
    double top = pt.y + ym / 2;
    double bottom = pt.y - ym / 2;
    return getGeoRange(left, right, bottom, top);
  }

  public GeoRange getGeoRange(double left, double right, double bottom, double top) {
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

    double west = Double.MAX_VALUE;
    double south = Double.MAX_VALUE;
    double east = -Double.MAX_VALUE;
    double north = -Double.MAX_VALUE;

    for (int i = 0; i < 8; i++) {
      // System.out.print(pt[i]);
      pt[i] = inverse(pt[i]);
      // System.out.println(" P-> " + pt[i]);
      west = Math.min(west, pt[i].x);
      south = Math.min(south, pt[i].y);
      east = Math.max(east, pt[i].x);
      north = Math.max(north, pt[i].y);
    }
    GeoRange gr = new GeoRange(west, east, south, north);
    return gr;
  }

  @Deprecated
  public static double distanceBetween(Point2D.Double pt1, Point2D.Double pt2) {
    if (pt1 == null || pt2 == null || Double.isNaN(pt1.x) || Double.isNaN(pt1.y)
        || Double.isNaN(pt2.x) || Double.isNaN(pt2.y))
      return Double.NaN;

    double phi1 = Math.toRadians(pt1.y);
    double phi2 = Math.toRadians(pt2.y);
    double lam1 = Math.toRadians(pt1.x);
    double lam2 = Math.toRadians(pt2.x);
    double dlam = lam2 - lam1;

    double a = Math.cos(phi2) * Math.sin(dlam);
    double b = Math.cos(phi1) * Math.sin(phi2);
    double c = Math.sin(phi1) * Math.cos(phi2) * Math.cos(dlam);
    double d = Math.sin(phi1) * Math.sin(phi2);
    double e = Math.cos(phi1) * Math.cos(phi2) * Math.cos(dlam);
    double f = Math.atan2(Math.sqrt(a * a + (b - c) * (b - c)), (d + e));

    double r = Ellipsoid.ELLIPSOIDS[0].equatorialRadius;

    return f * r;
  }

  public static double distanceBetweenRad(Point2D.Double pt1, Point2D.Double pt2) {
    if (pt1 == null || pt2 == null || Double.isNaN(pt1.x) || Double.isNaN(pt1.y)
        || Double.isNaN(pt2.x) || Double.isNaN(pt2.y))
      return Double.NaN;

    double phi1 = Math.toRadians(pt1.y);
    double phi2 = Math.toRadians(pt2.y);
    double lam1 = Math.toRadians(pt1.x);
    double lam2 = Math.toRadians(pt2.x);
    double dlam = lam2 - lam1;

    double a = Math.cos(phi2) * Math.sin(dlam);
    double b = Math.cos(phi1) * Math.sin(phi2);
    double c = Math.sin(phi1) * Math.cos(phi2) * Math.cos(dlam);
    double d = Math.sin(phi1) * Math.sin(phi2);
    double e = Math.cos(phi1) * Math.cos(phi2) * Math.cos(dlam);
    double f = Math.atan2(Math.sqrt(a * a + (b - c) * (b - c)), (d + e));

    return f;
  }

  public static double distanceBetweenDegree(Point2D.Double pt1, Point2D.Double pt2) {
    return Math.toDegrees(distanceBetweenRad(pt1, pt2));
  }

  public static double distanceBetweenM(Point2D.Double pt1, Point2D.Double pt2) {
    return distanceBetweenRad(pt1, pt2) * Ellipsoid.ELLIPSOIDS[0].equatorialRadius;

  }

  public static double azimuthTo(Point2D.Double pt1, Point2D.Double pt2) {
    if (pt1 == null || pt2 == null || Double.isNaN(pt1.x) || Double.isNaN(pt1.y)
        || Double.isNaN(pt2.x) || Double.isNaN(pt2.y))
      return Double.NaN;

    double phi1 = Math.toRadians(pt1.y);
    double phi2 = Math.toRadians(pt2.y);
    double lam1 = Math.toRadians(pt1.x);
    double lam2 = Math.toRadians(pt2.x);
    double dlam = lam2 - lam1;

    double a = Math.sin(dlam);
    double b = Math.sin(phi1) * Math.cos(dlam);
    double c = Math.cos(phi1) * Math.tan(phi2);
    double az = Math.atan2(b - c, a);
    az = Math.toDegrees(az) + 90;
    if (az < 0)
      az += 360;
    if (az >= 360)
      az -= 360;
    return az;
  }

  public static Point2D.Double getPointFrom(Point2D.Double origin, double c, double az) {
    double phi1 = origin.y * DEG2RAD;
    double lam1 = origin.x * DEG2RAD;
    // c = c / 6372795.477598;
    c = c / Ellipsoid.ELLIPSOIDS[0].equatorialRadius;
    az = az * DEG2RAD;
    Point2D.Double pt = new Point2D.Double();
    pt.y = Math.asin(Math.sin(phi1) * Math.cos(c) + Math.cos(phi1) * Math.sin(c) * Math.cos(az))
        * RAD2DEG;
    pt.x = (lam1 + Math.atan2(Math.sin(c) * Math.sin(az),
        (Math.cos(phi1) * Math.cos(c) - Math.sin(phi1) * Math.sin(c) * Math.cos(az)))) * RAD2DEG;
    return pt;
  }

  public static Point2D.Double[] getPointsFrom(Point2D.Double origin, double c, int n) {
    double phi1 = origin.y * DEG2RAD;
    double lam1 = origin.x * DEG2RAD;
    // c = c / 6372795.477598;
    c = c / Ellipsoid.ELLIPSOIDS[0].equatorialRadius;
    Point2D.Double[] pts = new Point2D.Double[n];
    for (int i = 0; i < n; i++) {
      double az = ((double) i / (double) n * 360.0) * DEG2RAD;
      Point2D.Double pt = new Point2D.Double();
      pt.y = Math.asin(Math.sin(phi1) * Math.cos(c) + Math.cos(phi1) * Math.sin(c) * Math.cos(az))
          * RAD2DEG;
      pt.x = (lam1 + Math.atan2(Math.sin(c) * Math.sin(az),
          (Math.cos(phi1) * Math.cos(c) - Math.sin(phi1) * Math.sin(c) * Math.cos(az)))) * RAD2DEG;
      pts[i] = pt;
    }
    return pts;
  }

  public void mapRect(FastProjector fast, double x1, double y1, double x2, double y2, int dx,
      int dy, int dw, int dh, int[] src, int[] dest, int sw, int sh, int scan, GeoRange range) {
    double w = range.getWest();
    double n = range.getNorth();
    double lonRange = range.getLonRange();
    double latRange = range.getLatRange();
    Point2D.Double pt = new Point2D.Double();
    for (int i = 0; i < dw; i++)
      for (int j = 0; j < dh; j++) {
        pt.x = x1 + (double) i / (double) dw * (x2 - x1);
        pt.y = y1 + (double) j / (double) dh * (y2 - y1);
        fast.inverse(pt);
        if (Double.isNaN(pt.x))
          continue;
        pt.x = pt.x % 360;
        if (pt.x > 180)
          pt.x -= 360;
        if (pt.x < -180)
          pt.x += 360;
        // int sx = (int)((e - pt.x) / lonRange * sw);
        // int sy = (int)((n - pt.y) / latRange * sh);
        int sx = (int) ((pt.x - w) / lonRange * sw);
        int sy = (int) (-(pt.y - n) / latRange * sh);
        if (sx >= sw || sy >= sh || sx < 0 || sy < 0)
          continue;
        // dest[dx + i + (dy + j) * 1000] = src.getRGB(sx, sy);
        dest[dx + i + (dy + dh - j - 1) * scan] = src[sx + sy * sw];
      }
  }

  public void fastMapRect(FastProjector fast, double x1, double y1, double x2, double y2, int dx,
      int dy, int dw, int dh, int[] src, int[] dest, int sw, int sh, int scan, GeoRange range) {
    Point2D.Double ul = new Point2D.Double(x1, y1);
    Point2D.Double ur = new Point2D.Double(x2, y1);
    Point2D.Double lr = new Point2D.Double(x2, y2);
    Point2D.Double ll = new Point2D.Double(x1, y2);
    fast.inverse(ul);
    fast.inverse(ur);
    fast.inverse(lr);
    fast.inverse(ll);
    if (Double.isNaN(ul.x) && Double.isNaN(ur.x) && Double.isNaN(lr.x) && Double.isNaN(ll.x))
      return;
    if (Double.isNaN(ul.x) || Double.isNaN(ur.x) || Double.isNaN(lr.x) || Double.isNaN(ll.x))
      mapRect(fast, x1, y1, x2, y2, dx, dy, dw, dh, src, dest, sw, sh, scan, range);
    else {
      double w = range.getWest();
      double n = range.getNorth();
      double lonRange = range.getLonRange();
      double latRange = range.getLatRange();

      // System.out.println("lonRange: " +lonRange);
      // System.out.println("w: " + w);
      // System.out.println("ul: " + ul);
      // System.out.println("ur: " + ur);
      // System.out.println("u lr: " + GeoRange.getLonRange(ul.x, ur.x));
      // System.out.println("ll: " + ll);
      // System.out.println("lr: " + lr);

      // for each col
      for (int i = 0; i < dw; i++) {
        if (Math.abs(ur.x - ul.x) > 180) {
          if (ur.x > ul.x)
            ur.x -= 360;
          else
            ul.x -= 360;
          // System.out.println("u adj: " + (ur.x - ul.x));
        }
        if (Math.abs(lr.x - ll.x) > 180) {
          if (lr.x > ll.x)
            lr.x -= 360;
          else
            ll.x -= 360;
          // System.out.println("l adj:\n\tll: " + ll + "\n\tlr: " +
          // lr + " r: " + (lr.x - ll.x));
        }
        double wx0 = ul.x + i * (ur.x - ul.x) / (double) dw;
        double wy0 = ul.y + i * (ur.y - ul.y) / (double) dw;
        double wx1 = ll.x + i * (lr.x - ll.x) / (double) dw;
        double wy1 = ll.y + i * (lr.y - ll.y) / (double) dw;
        if (Math.signum(wx0) != Math.signum(wx1) && Math.abs(wx1 - wx0) > 180) {
          if (wx1 < 0)
            wx1 += 360;
          else if (wx0 < 0)
            wx0 += 360;
        }
        double x = wx0;
        double y = wy0;
        double sdx = (wx1 - wx0) / (double) dh;
        double sdy = (wy1 - wy0) / (double) dh;
        // int di = dx + i + (dy + dh - 1) * scan;
        int di = dx + i + (dy + dh) * scan;
        int sx;
        int sy;
        // for each row
        for (int j = 0; j < dh; j++) {
          double adjx = x;
          if (adjx < w && range.containsLongitude(adjx)) {
            adjx += 360;
          }
          sx = (int) ((adjx - w) / lonRange * sw);
          sy = (int) (-(y - n) / latRange * sh);
          x += sdx;
          y += sdy;
          if (sx >= sw || sy >= sh || sx < 0 || sy < 0) {
            di -= scan;
            continue;
          }
          if (di < dest.length)
            dest[di] = src[sx + sy * sw];
          di -= scan;
        }
      }
    }
  }

  public BufferedImage getProjectedImage(int n, int dw, int dh, BufferedImage src, GeoRange gr,
      double mw, double me, double ms, double mn) {
    Raster r = src.getData();
    DataBufferInt dbi = (DataBufferInt) r.getDataBuffer();
    int[] pix = dbi.getData();
    return getProjectedImage(n, dw, dh, pix, src.getWidth(), src.getHeight(), gr, mw, me, ms, mn);
  }

  public BufferedImage getProjectedImage(int n, int dw, int dh, int[] src, int sw, int sh,
      GeoRange gr, double mw, double me, double ms, double mn) {
    CodeTimer ct = new CodeTimer("getProjectedImage");
    FastProjector fast = getFastProjector();
    int[] buf = new int[dw * dh];
    Arrays.fill(buf, 0xffffffff);
    int stepX = dw / (n * 2);
    int stepY = dh / (n * 2);
    for (int i = -n; i < n; i++)
      for (int j = -n; j < n; j++) {
        double x0 = mw + (me - mw) * (double) (i + n) / (double) (n * 2);
        double y0 = ms + (mn - ms) * (double) (j + n) / (double) (n * 2);
        double x1 = mw + (me - mw) * (double) (i + n + 1) / (double) (n * 2);
        double y1 = ms + (mn - ms) * (double) (j + n + 1) / (double) (n * 2);
        fastMapRect(fast, x0, y0, x1, y1, (n + i) * stepX, (n - j - 1) * stepY, stepX, stepY, src,
            buf, sw, sh, dw, gr);
      }

    DataBuffer dbi = new DataBufferInt(buf, dw * dh);
    int[] bandMasks = {0xff0000, 0xff00, 0xff, 0xff000000};
    WritableRaster wr = Raster.createPackedRaster(dbi, dw, dh, dw, bandMasks, null);
    ColorModel cm = ColorModel.getRGBdefault();
    BufferedImage bi = new BufferedImage(cm, wr, false, null);
    ct.stopAndReport();
    return bi;
  }

}
