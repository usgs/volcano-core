package gov.usgs.volcanoes.core.legacy.math.proj;

import gov.usgs.volcanoes.core.math.proj.Projection;

import java.awt.geom.Point2D;
import java.util.ArrayList;
/**
 * Geographic Filter.
 * Defines circular and rectangular geographic filters and tests whether given points lie within them.
 *
 *
 * @author Peter Cervelli
 */
public class GeographicFilter {

  private static ArrayList<double[]> Filters = new ArrayList<double[]>();

  private static boolean inBox(double minLon, double maxLon, double minLat, double maxLat, double longitude, double latitude) {

    return longitude >= minLon & longitude <= maxLon & latitude >= minLat & latitude <= maxLat;

  }

  private static boolean inCircle(double radius, double lambda, double phi, double longitude, double latitude) {

    return radius * 1000 > Projection.distanceBetween(new Point2D.Double(lambda,phi),new Point2D.Double(longitude, latitude));

  }

  public void addBox(double minLon, double maxLon, double minLat, double maxLat) {

    double[] Box;

    Box = new double[5];
    Box[0] = 1;
    Box[1] = minLon;
    Box[2] = maxLon;
    Box[3] = minLat;
    Box[4] = maxLat;
    Filters.add(Box);

  }

  public void addCircle(double radius, double lambda, double phi) {

    double[] Circle;

    Circle = new double[4];
    Circle[0] = 0;
    Circle[1] = radius;
    Circle[2] = lambda;
    Circle[3] = phi;

    Filters.add(Circle);

  }

  public boolean test(double longitude, double latitude) {

    int k;
    boolean result = true;
    if (Filters.size() != 0)
      for (k=0; k<Filters.size(); k++) {
        double[] parameters = Filters.get(k);
        switch ((int)parameters[0]) {
          case 0: result = inCircle(parameters[1],parameters[2],parameters[3],longitude,latitude); break;

          case 1: result = inBox(parameters[1],parameters[2],parameters[3],parameters[4],longitude,latitude); break;
        }
        if (result)
          return true;
      }
    else
      return true;

    return false;
  }

  public GeographicFilter() {

  }

}
