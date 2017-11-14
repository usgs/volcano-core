package gov.usgs.volcanoes.core.math.proj;


import gov.usgs.volcanoes.core.CodeTimer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * A class that handles transverse mercator projections
 * 
 * Equations from "Map Projections -- A Working Manual"
 * 
 * @author Dan Cervelli
 */
public class TransverseMercator extends Projection {
  private Point2D.Double origin;
  private double falseEasting;
  private double falseNorthing;

  public TransverseMercator() {
    name = "Transverse Mercator";
  }

  public int hashCode() {
    return origin.hashCode() + new Double(falseEasting).hashCode()
        + new Double(falseNorthing).hashCode();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof TransverseMercator))
      return false;
    TransverseMercator tm = (TransverseMercator) obj;
    return tm.falseEasting == falseEasting && tm.falseNorthing == falseNorthing
        && tm.origin.equals(origin);
  }

  public static TransverseMercator fromUTM(String utm) {
    TransverseMercator tm = new TransverseMercator();
    try {
      tm.falseEasting = 500000;
      int zoneNumber = Integer.parseInt(utm.substring(0, utm.length() - 1));
      int zoneLetter = utm.toUpperCase().charAt(utm.length() - 1);
      if ((zoneLetter - 'N') < 0)
        tm.falseNorthing = 10000000.0;
      double olon = (zoneNumber - 1) * 6 - 180 + 3;
      tm.origin = new Point2D.Double(olon, 0);
    } catch (Exception e) {
      return null;
    }
    return tm;
  }

  public void setup(Point2D.Double o, double e, double n) {
    origin = o;
    falseEasting = e;
    falseNorthing = n;
  }

  public void setOrigin(Point2D.Double o) {
    origin = o;
  }

  // NOT CORRECT!
  public double getScale(Point2D.Double lonLat) {
    return 0.9996;
  }

  private GeoRange getSouthGeoRange(double left, double right, double bottom, double top) {
    double dx = right - left;
    double dy = top - bottom;

    Point2D.Double c = inverse(new Point2D.Double(left + dx / 2, bottom + dy / 2));

    double north = inverse(new Point2D.Double(0, top)).y;

    int n = 20;
    Point2D.Double[] ptsX = new Point2D.Double[n];
    Point2D.Double[] ptsY = new Point2D.Double[n];

    int i;
    for (i = 0; i < n; i++) {
      ptsX[i] = new Point2D.Double(left + dx * (i / (double) n), bottom);
      ptsY[i] = new Point2D.Double(left, bottom + dy * (i / (double) n));
    }
    for (i = 1; i < n; i++) {
      if (inverse(ptsY[i]).y > north)
        break;
    }
    double west = inverse(ptsY[i - 1]).x;
    double east = c.x - (west - c.x);
    for (i = 1; i < n; i++) {
      if (inverse(ptsX[i]).x > west)
        break;
    }
    double south = inverse(ptsX[i - 1]).y;
    GeoRange gr = new GeoRange(west, east, south, north);
    return gr;
  }

  private GeoRange getNorthGeoRange(double left, double right, double bottom, double top) {
    double dx = right - left;
    double dy = top - bottom;

    Point2D.Double c = inverse(new Point2D.Double(left + dx / 2, bottom + dy / 2));

    double south = inverse(new Point2D.Double(0, bottom)).y;
    // System.out.println("South: " + south);

    int n = 20;
    Point2D.Double[] ptsX = new Point2D.Double[n];
    Point2D.Double[] ptsY = new Point2D.Double[n];

    int i;
    for (i = 0; i < n; i++) {
      ptsX[i] = new Point2D.Double(left + dx * (i / (double) n), top);
      ptsY[i] = new Point2D.Double(left, bottom + dy * (i / (double) n));
    }
    for (i = 1; i < n; i++) {
      // System.out.println(ptsY[i] + " -> " + inverse(ptsY[i]));
      if (inverse(ptsY[i]).y > south)
        break;
    }
    double west = inverse(ptsY[i - 1]).x;
    // System.out.println("west: " + west);
    // System.out.println("c: " + c + " " + (west - c.x));
    double east = c.x - (west - c.x);
    // System.out.println("east: " + east);
    for (i = 1; i < n; i++) {
      // System.out.println(ptsX[i] + " -> " + inverse(ptsX[i]));
      if (inverse(ptsX[i]).x > west)
        break;
    }
    double north = inverse(ptsX[i - 1]).y;
    // System.out.println("North: " + north);
    GeoRange gr = new GeoRange(west, east, south, north);
    // System.out.println("coord of mid south: " + forward(new
    // Point2D.Double(west + gr.getLonRange() / 2, south)));
    return gr;
  }

  public GeoRange getGeoRange(double left, double right, double bottom, double top) {
    if (origin.y >= 0)
      return getNorthGeoRange(left, right, bottom, top);
    else
      return getSouthGeoRange(left, right, bottom, top);
  }

  public Point2D.Double[] forward(Point2D.Double[] lonLat) {
    double a = ellipsoid.equatorialRadius;
    double esq = ellipsoid.eccentricitySquared;
    double phiO = origin.getY() * DEG2RAD;
    double lambdaO = origin.getX() * DEG2RAD;
    double fe = falseEasting;
    double fn = falseNorthing;
    double scale = 0.9996;

    Point2D.Double[] result = new Point2D.Double[lonLat.length];

    double phi, lambda, epsq, N, T, C, A, M, MO, x, y;

    epsq = esq / (1 - esq);
    MO = a * ((1 - esq / 4 - 3 * esq * esq / 64 - 5 * esq * esq * esq / 256) * phiO
        - (3 * esq / 8 + 3 * esq * esq / 32 + 45 * esq * esq * esq / 1024) * Math.sin(2 * phiO)
        + (15 * esq * esq / 256 + 45 * esq * esq * esq / 1024) * Math.sin(4 * phiO)
        - (35 * esq * esq * esq / 3072) * Math.sin(6 * phiO));

    for (int i = 0; i < lonLat.length; i++) {
      phi = lonLat[i].getY() * DEG2RAD;
      lambda = lonLat[i].getX() * DEG2RAD;
      N = a / Math.pow(1 - esq * Math.sin(phi) * Math.sin(phi), 0.5);
      T = Math.tan(phi) * Math.tan(phi);
      C = epsq * Math.cos(phi) * Math.cos(phi);
      A = (lambda - lambdaO) * Math.cos(phi);
      M = a * ((1 - esq / 4 - 3 * esq * esq / 64 - 5 * esq * esq * esq / 256) * phi
          - (3 * esq / 8 + 3 * esq * esq / 32 + 45 * esq * esq * esq / 1024) * Math.sin(2 * phi)
          + (15 * esq * esq / 256 + 45 * esq * esq * esq / 1024) * Math.sin(4 * phi)
          - (35 * esq * esq * esq / 3072) * Math.sin(6 * phi));
      x = scale * N * (A + (1 - T + C) * A * A * A / 6
          + (5 - 18 * T + T * T + 72 * C - 58 * epsq) * A * A * A * A * A / 120) + fe;
      y = scale
          * (M - MO
              + N * Math.tan(phi)
                  * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24
                      + (61 - 58 * T + T * T + 600 * C - 330 * epsq) * A * A * A * A * A * A / 720))
          + fn;
      result[i] = new Point2D.Double(x, y);
    }
    return result;
  }

  public Point2D.Double[] inverse(Point2D.Double[] points) {
    double N1, T1, C1, R1, D, M, MO;
    double phi1;
    double a = ellipsoid.equatorialRadius;
    double esq = ellipsoid.eccentricitySquared;
    double epsq = esq / (1 - esq);
    double scale = 0.9996;
    double phiO = origin.getY() * DEG2RAD;
    double lambdaO = origin.getX() * DEG2RAD;
    double fe = falseEasting;
    double fn = falseNorthing;
    double e1 = (1 - Math.sqrt(1 - esq)) / (1 + Math.sqrt(1 - esq));
    double x, y;
    double mu;
    double phi, lambda;

    Point2D.Double[] result = new Point2D.Double[points.length];
    MO = a * ((1 - esq / 4 - 3 * esq * esq / 64 - 5 * esq * esq * esq / 256) * phiO
        - (3 * esq / 8 + 3 * esq * esq / 32 + 45 * esq * esq * esq / 1024) * Math.sin(2 * phiO)
        + (15 * esq * esq / 256 + 45 * esq * esq * esq / 1024) * Math.sin(4 * phiO)
        - (35 * esq * esq * esq / 3072) * Math.sin(6 * phiO));

    for (int i = 0; i < points.length; i++) {
      x = points[i].getX() - fe;
      y = points[i].getY() - fn;

      M = MO + y / scale;
      mu = M / (a * (1 - esq / 4 - 3 * esq * esq / 64 - 5 * esq * esq * esq / 256));

      phi1 = mu + (3 * e1 / 2 - 27 * e1 * e1 * e1 / 32) * Math.sin(2 * mu)
          + (21 * e1 * e1 / 16 - 55 * e1 * e1 * e1 * e1 / 32) * Math.sin(4 * mu)
          + (151 * e1 * e1 * e1 / 96) * Math.sin(6 * mu);
      N1 = a / Math.sqrt(1 - esq * Math.sin(phi1) * Math.sin(phi1));
      T1 = Math.tan(phi1) * Math.tan(phi1);
      C1 = epsq * Math.cos(phi1) * Math.cos(phi1);
      R1 = a * (1 - esq) / Math.pow(1 - esq * Math.sin(phi1) * Math.sin(phi1), 1.5);
      D = x / (N1 * scale);
      phi = (phi1 - (N1 * Math.tan(phi1) / R1)
          * (D * D / 2 - (5 + 3 * T1 + 10 * C1 - 4 * C1 * C1 - 9 * epsq) * D * D * D * D / 24
              + (61 + 90 * T1 + 298 * C1 + 45 * T1 * T1 - 252 * epsq - 3 * C1 * C1) * D * D * D * D
                  * D * D / 720))
          * RAD2DEG;
      lambda = (lambdaO + ((D - (1 + 2 * T1 + C1) * D * D * D / 6
          + (5 - 2 * C1 + 28 * T1 - 3 * C1 * C1 + 8 * epsq + 24 * T1 * T1) * D * D * D * D * D
              / 120)
          / Math.cos(phi1))) * RAD2DEG;

      result[i] = new Point2D.Double(lambda, phi);
    }
    return result;
  }

  public Point2D.Double forward(Point2D.Double lonLat) {
    double a = ellipsoid.equatorialRadius;
    double esq = ellipsoid.eccentricitySquared;
    double phiO = origin.getY() * DEG2RAD;
    double lambdaO = origin.getX() * DEG2RAD;
    double fe = falseEasting;
    double fn = falseNorthing;
    double scale = 0.9996;

    double phi, lambda, epsq, N, T, C, A, M, MO, x, y;

    epsq = esq / (1 - esq);
    MO = a * ((1 - esq / 4 - 3 * esq * esq / 64 - 5 * esq * esq * esq / 256) * phiO
        - (3 * esq / 8 + 3 * esq * esq / 32 + 45 * esq * esq * esq / 1024) * Math.sin(2 * phiO)
        + (15 * esq * esq / 256 + 45 * esq * esq * esq / 1024) * Math.sin(4 * phiO)
        - (35 * esq * esq * esq / 3072) * Math.sin(6 * phiO));

    phi = lonLat.getY() * DEG2RAD;
    lambda = lonLat.getX() * DEG2RAD;
    N = a / Math.pow(1 - esq * Math.sin(phi) * Math.sin(phi), 0.5);
    T = Math.tan(phi) * Math.tan(phi);
    C = epsq * Math.cos(phi) * Math.cos(phi);
    double dl = (lambda - lambdaO);
    while (dl < -Math.PI / 2)
      dl += Math.PI;
    while (dl > Math.PI / 2)
      dl -= Math.PI;
    // A = (lambda - lambdaO) * Math.cos(phi);
    A = dl * Math.cos(phi);
    M = a * ((1 - esq / 4 - 3 * esq * esq / 64 - 5 * esq * esq * esq / 256) * phi
        - (3 * esq / 8 + 3 * esq * esq / 32 + 45 * esq * esq * esq / 1024) * Math.sin(2 * phi)
        + (15 * esq * esq / 256 + 45 * esq * esq * esq / 1024) * Math.sin(4 * phi)
        - (35 * esq * esq * esq / 3072) * Math.sin(6 * phi));
    x = scale * N * (A + (1 - T + C) * A * A * A / 6
        + (5 - 18 * T + T * T + 72 * C - 58 * epsq) * A * A * A * A * A / 120) + fe;
    y = scale
        * (M - MO
            + N * Math.tan(phi)
                * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24
                    + (61 - 58 * T + T * T + 600 * C - 330 * epsq) * A * A * A * A * A * A / 720))
        + fn;
    return new Point2D.Double(x, y);
  }

  public Point2D.Double inverse(Point2D.Double xy) {
    double N1, T1, C1, R1, D, M, MO;
    double phi1;
    double a = ellipsoid.equatorialRadius;
    double esq = ellipsoid.eccentricitySquared;
    double epsq = esq / (1 - esq);
    double scale = 0.9996;
    double phiO = origin.getY() * DEG2RAD;
    double lambdaO = origin.getX() * DEG2RAD;
    // System.out.println(phiO + ",," + lambdaO);
    double fe = falseEasting;
    double fn = falseNorthing;
    double e1 = (1 - Math.sqrt(1 - esq)) / (1 + Math.sqrt(1 - esq));
    double x, y;
    double mu;
    double phi, lambda;

    MO = a * ((1 - esq / 4 - 3 * esq * esq / 64 - 5 * esq * esq * esq / 256) * phiO
        - (3 * esq / 8 + 3 * esq * esq / 32 + 45 * esq * esq * esq / 1024) * Math.sin(2 * phiO)
        + (15 * esq * esq / 256 + 45 * esq * esq * esq / 1024) * Math.sin(4 * phiO)
        - (35 * esq * esq * esq / 3072) * Math.sin(6 * phiO));

    x = xy.getX() - fe;
    y = xy.getY() - fn;

    M = MO + y / scale;
    mu = M / (a * (1 - esq / 4 - 3 * esq * esq / 64 - 5 * esq * esq * esq / 256));

    phi1 = mu + (3 * e1 / 2 - 27 * e1 * e1 * e1 / 32) * Math.sin(2 * mu)
        + (21 * e1 * e1 / 16 - 55 * e1 * e1 * e1 * e1 / 32) * Math.sin(4 * mu)
        + (151 * e1 * e1 * e1 / 96) * Math.sin(6 * mu);
    N1 = a / Math.sqrt(1 - esq * Math.sin(phi1) * Math.sin(phi1));
    T1 = Math.tan(phi1) * Math.tan(phi1);
    C1 = epsq * Math.cos(phi1) * Math.cos(phi1);
    R1 = a * (1 - esq) / Math.pow(1 - esq * Math.sin(phi1) * Math.sin(phi1), 1.5);
    D = x / (N1 * scale);
    phi = (phi1 - (N1 * Math.tan(phi1) / R1)
        * (D * D / 2 - (5 + 3 * T1 + 10 * C1 - 4 * C1 * C1 - 9 * epsq) * D * D * D * D / 24
            + (61 + 90 * T1 + 298 * C1 + 45 * T1 * T1 - 252 * epsq - 3 * C1 * C1) * D * D * D * D
                * D * D / 720))
        * RAD2DEG;
    lambda = (lambdaO + ((D - (1 + 2 * T1 + C1) * D * D * D / 6
        + (5 - 2 * C1 + 28 * T1 - 3 * C1 * C1 + 8 * epsq + 24 * T1 * T1) * D * D * D * D * D / 120)
        / Math.cos(phi1))) * RAD2DEG;

    if (lambda > 180)
      lambda -= 360;

    return new Point2D.Double(lambda, phi);
  }

  public String getInverseJavaScript() {
    StringBuffer sb = new StringBuffer(2048);
    sb.append("function inverse(x, y)\n");
    sb.append("{\n");
    sb.append("var N1,T1,C1,R1,D,M,MO,phi1,mu,phi,lambda;\n");
    sb.append("var a=" + ellipsoid.equatorialRadius + ";\n");
    sb.append("var esq=" + ellipsoid.eccentricitySquared + ";\n");
    sb.append("var epsq=esq/(1-esq);\n");
    sb.append("var scale=0.9996;\n");
    sb.append("var phiO=" + origin.getY() * DEG2RAD + ";\n");
    sb.append("var lambdaO=" + origin.getX() * DEG2RAD + ";\n");
    sb.append("var e1=(1-Math.sqrt(1-esq))/(1+Math.sqrt(1-esq));\n");
    sb.append("MO=a*(");
    sb.append("(1-esq/4-3*esq*esq/64-5*esq*esq*esq/256)*phiO-");
    sb.append("(3*esq/8+3*esq*esq/32+45*esq*esq*esq/1024)*Math.sin(2*phiO)+");
    sb.append("(15*esq*esq/256+45*esq*esq*esq/1024)*Math.sin(4*phiO)-");
    sb.append("(35*esq*esq*esq/3072)*Math.sin(6*phiO));\n");
    sb.append("x=x-" + falseEasting + ";\n");
    sb.append("y=y-" + falseNorthing + ";\n");
    sb.append("M=MO+y/scale;\n");
    sb.append("mu=M/(a*(1-esq/4-3*esq*esq/64-5*esq*esq*esq/256));\n");
    sb.append(
        "phi1=mu+(3*e1/2-27*e1*e1*e1/32)*Math.sin(2*mu)+(21*e1*e1/16-55*e1*e1*e1*e1/32)*Math.sin(4*mu)+(151*e1*e1*e1/96)*Math.sin(6*mu);\n");
    sb.append("N1=a/Math.sqrt(1-esq*Math.sin(phi1)*Math.sin(phi1));\n");
    sb.append("T1=Math.tan(phi1)*Math.tan(phi1);\n");
    sb.append("C1=epsq*Math.cos(phi1)*Math.cos(phi1);\n");
    sb.append("R1=a*(1-esq)/Math.pow(1-esq*Math.sin(phi1)*Math.sin(phi1),1.5);\n");
    sb.append("D=x/(N1*scale);\n");
    sb.append(
        "phi=(phi1-(N1*Math.tan(phi1)/R1)*(D*D/2-(5+3*T1+10*C1-4*C1*C1-9*epsq)*D*D*D*D/24+(61+90*T1+298*C1+45*T1*T1-252*epsq-3*C1*C1)*D*D*D*D*D*D/720))*"
            + RAD2DEG + ";\n");
    sb.append(
        "lambda=(lambdaO+((D-(1+2*T1+C1)*D*D*D/6+(5-2*C1+28*T1-3*C1*C1+8*epsq+24*T1*T1)*D*D*D*D*D/120)/Math.cos(phi1)))*"
            + RAD2DEG + ";\n");
    sb.append("return new Array(lambda, phi);\n");
    sb.append("}\n");
    return sb.toString();
  }

  public static void main(String[] args) throws Exception {
    TransverseMercator merc = new TransverseMercator();
    GeoRange gr = new GeoRange(-180, 180, -90, 90);
    // GeoRange gr = new GeoRange(-90, 0, 0, 90);

    final int m = 1;
    final Image[] imgs = new Image[m];
    CodeTimer ctl = new CodeTimer("load");
    BufferedImage bi = ImageIO.read(new FileInputStream("c:\\mapdata\\nasa\\world.jpg"));
    ctl.mark("decode");
    int[] pix = bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), null, 0, bi.getWidth());
    for (int i = 0; i < pix.length; i++) {
      pix[i] = 0xff000000 | pix[i];
    }
    ctl.mark("toint");
    ctl.stopAndReport();
    System.out.println("pix.length: " + pix.length);

    merc.setOrigin(new Point2D.Double(175, -40));
    imgs[0] = merc.getProjectedImage(50, 1000, 1000, pix, 2700, 1350, gr, -2000000, 2000000,
        -2000000, 2000000);

    JFrame f = new JFrame("GeoImageSet Test, Projected") {
      public static final long serialVersionUID = -1;
      int cycle = 0;

      public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (cycle == 0)
          g2.fillRect(0, 0, 1200, 1000);
        g2.drawImage(imgs[++cycle % m], 0, 0, null);
      }
    };

    f.setSize(1200, 1000);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);

    while (true) {
      f.repaint();
      try {
        Thread.sleep(50);
      } catch (Exception e) {
      }
    }
  }

}
