package gov.usgs.volcanoes.core.math.proj;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 * 
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 * @version $Id: EllipsoidMercator.java,v 1.1 2007-04-24 21:22:46 dcervelli Exp $
 */
public class EllipsoidMercator extends Projection {
  private Point2D.Double origin;

  public EllipsoidMercator() {
    name = "EllipsoidMercator";
    origin = new Point2D.Double(0, 0);
    ellipsoid = Ellipsoid.ELLIPSOIDS[11];
  }

  @Override
  public Double forward(Double lonLat) {
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
    // double y = r * Math.log(Math.tan(Math.PI / 4 + phi / 2));
    double sphi = Math.sin(phi);
    double e = Math.sqrt(ellipsoid.eccentricitySquared);
    double t2 = Math.pow((1 - e * sphi) / (1 + e * sphi), e);
    double y = r * 0.5 * Math.log((1 + sphi) / (1 - sphi) * t2);

    return new Point2D.Double(x, y);
  }

  @Override
  public double getScale(Double lonLat) {
    double e2 = ellipsoid.eccentricitySquared;
    double sp = Math.sin(Math.toRadians(lonLat.y));
    double top = Math.sqrt(1 - e2 * sp * sp);
    double bottom = Math.cos(Math.toRadians(lonLat.y));
    return top / bottom;
  }

  @Override
  public Double inverse(Double xy) {
    double r = ellipsoid.equatorialRadius;
    double e = Math.sqrt(ellipsoid.eccentricitySquared);
    double lambdaO = origin.getX() * DEG2RAD;
    double lambda = lambdaO + xy.x / r;

    double t = Math.exp(-xy.y / r);
    double phi = Math.PI / 2 - 2 * Math.atan(t);
    for (int i = 0; i < 5; i++) {
      double sphi = Math.sin(phi);
      double t2 = (1 - e * sphi) / (1 + e * sphi);
      double t3 = t * Math.pow(t2, e / 2);
      phi = Math.PI / 2 - 2 * Math.atan(t3);
    }

    return new Point2D.Double(lambda * RAD2DEG, phi * RAD2DEG);
  }

  public static void main(String[] args) throws Exception {
    EllipsoidMercator merc = new EllipsoidMercator();
    System.out.println(merc.forward(new Point2D.Double(-121.5, 35.5)));
    System.out.println(merc.inverse(new Point2D.Double(-13525318, 4207225)));
  }
}
