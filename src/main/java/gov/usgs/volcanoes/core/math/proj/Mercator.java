package gov.usgs.volcanoes.core.math.proj;

import gov.usgs.volcanoes.core.CodeTimer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * Spherical mercator projection.
 * 
 * @author Dan Cervelli
 * @version $Id: Mercator.java,v 1.5 2007-08-06 04:48:06 dcervelli Exp $
 */
public class Mercator extends Projection {
  private Point2D.Double origin;

  public Mercator() {
    name = "Mercator";
    origin = new Point2D.Double(0, 0);
    ellipsoid = Ellipsoid.ELLIPSOIDS[0];
  }

  public int hashCode() {
    return origin.hashCode() + "Mercator".hashCode();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Mercator))
      return false;
    Mercator m = (Mercator) obj;
    return m.origin.equals(origin);
  }

  private static Mercator mercator = new Mercator();

  public static double getMaxWidth() {
    Point2D.Double pt = new Point2D.Double(-180, 0);
    Point2D.Double left = mercator.forward(pt);
    pt.x = 180;
    Point2D.Double right = mercator.forward(pt);
    return right.x - left.x;
  }

  public String getName() {
    return name;
  }

  public void setOrigin(Point2D.Double pt) {
    origin.x = pt.x;
  }

  public double getScale(Point2D.Double lonLat) {
    return 1.0 / Math.cos(Math.toRadians(lonLat.y));
  }

  public Point2D.Double forward(Point2D.Double lonLat) {
    double r = ellipsoid.equatorialRadius;
    double lambdaO = origin.getX() * DEG2RAD;

    double phi = lonLat.getY() * DEG2RAD;
    double lambda = lonLat.getX() * DEG2RAD;

    double l = (lambda - lambdaO);
    while (l > Math.PI)
      l -= Math.PI * 2;
    while (l < -Math.PI)
      l += Math.PI * 2;
    double x = r * l;
    double y = r * Math.log(Math.tan(Math.PI / 4 + phi / 2));

    return new Point2D.Double(x, y);
  }

  public Point2D.Double inverse(Point2D.Double xy) {
    double r = ellipsoid.equatorialRadius;
    double lambdaO = origin.getX() * DEG2RAD;

    double phi = Math.PI / 2 - 2 * Math.atan(Math.exp(-xy.y / r));
    double lambda = lambdaO + xy.x / r;

    return new Point2D.Double(lambda * RAD2DEG, phi * RAD2DEG);
  }

  public FastProjector getFastProjector() {
    return new FastProjector() {
      double r = ellipsoid.equatorialRadius;
      double lambdaO = origin.getX() * DEG2RAD;

      public void forward(Point2D.Double pt) {
        pt = Mercator.this.forward(pt);
      }

      public void inverse(Point2D.Double xy) {
        xy.y = (Math.PI / 2 - 2 * Math.atan(Math.exp(-xy.y / r))) * RAD2DEG;
        xy.x = (lambdaO + xy.x / r) * RAD2DEG;
      }
    };
  }

  public static void main(String[] args) throws Exception {
    Mercator merc = new Mercator();
    GeoRange gr = new GeoRange(-180, 180, -90, 90);

    final int m = 1;
    final Image[] imgs = new Image[m];
    CodeTimer ctl = new CodeTimer("load");
    BufferedImage bi = ImageIO.read(new FileInputStream("c:\\mapdata\\nasa\\world.jpg"));
    int[] pix = bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), null, 0, bi.getWidth());
    ctl.mark("decode");
    Raster r = bi.getData();
    DataBufferByte dbi = (DataBufferByte) r.getDataBuffer();
    System.out.println(dbi.getNumBanks() + " " + dbi.getSize());
    for (int i = 0; i < pix.length; i = i + 3) {
      pix[i] = 0xff000000 | pix[i];
    }
    ctl.mark("toint");
    ctl.stopAndReport();
    System.out.println("pix.length: " + pix.length);
    merc.setOrigin(new Point2D.Double(-63.033241220845554, 0));
    double[] extents =
        new double[] {-1577434.476903, 1577434.476903, -7277893.839260, -4288413.663448};

    imgs[0] = merc.getProjectedImage(5, 1020, 970, pix, 2700, 1350, gr, extents[0], extents[1],
        extents[2], extents[3]);

    JFrame f = new JFrame("GeoImageSet Test, Projected") {
      public static final long serialVersionUID = -1;
      int cycle = 0;


      @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "INT_BAD_REM_BY_1",
          justification = "Hardcoded testing value may be changed.")
      public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (cycle == 0) {
          g2.setColor(Color.RED);
          g2.fillRect(0, 0, 1200, 1100);

        }
        g2.drawImage(imgs[++cycle % m], 50, 40, null);
      }
    };

    f.setSize(1200, 1100);
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
