package gov.usgs.volcanoes.core.legacy.plot.transform;

import java.awt.geom.Point2D;

/**
 * A class used to help plot hypocenters by projecting them onto a line drawn 
 * between 2 arbitrary points on the earth
 * 
 * @author sid hellman
 *
 */
public class ArbDepthCalculator {

  /** 
   * The maximum distance from the projection line for points 
   * to be included in plot; default is 10 km.
   */
  double width = 10.0;

  /**
   * Longitude of first corner
   */
  double x1 = 0.0;

  /**
   * Latitude of first corner
   */
  double y1 = 0.0;

  /**
   * Longitude of second corner
   */
  double x2 = 0.0;

  /**
   * Latitude of second corner
   */
  double y2 = 0.0;

  /**
   * Angle of line from corner to corner
   */
  double theta = 0.0;

  private double maxDistance;
  private double progDistance;
  private double scale;

  /**
   * define the line for projecting
   * @param lat1 - the latitude of corner 1
   * @param lon1 - the longitude of corner 1
   * @param lat2 - the latitude of corner 2
   * @param lon2 - the longitude of corner 2
   * default width is 10km.
   */
  public ArbDepthCalculator(double lat1, double lon1, double lat2, double lon2) {
    this.x1 = lon1;
    this.x2 = lon2;
    this.y1 = lat1;
    this.y2 = lat2;
    setupAngles();
  }


  /**
   * define the line for projecting, and the width limit used for projection
   * @param lat1 - the latitude of corner 1
   * @param lon1 - the longitude of corner 1
   * @param lat2 - the latitude of corner 2
   * @param lon2 - the longitude of corner 2
   * @param width - how far away from the center line we should allow projections on.
   */
  public ArbDepthCalculator(double lat1, double lon1, double lat2, double lon2, double width) {
    this(lat1, lon1, lat2, lon2);
    this.setWidth(width);
  }

  /**
   * decide if a lat/lon is inside my box or not.
   * @param lat
   * @param lon
   * @return "lat/lon is inside my box"
   */
  public boolean isInsideArea(double lat, double lon) {


    // double d = this.distanceTo(lat, lon);
    double d = this.getScaledProjectedDistance(lat, lon);

    if (d < 0) {
      return false;
    }

    if (d > this.getMaxDist()) {
      return false;
    }

    // test to see if we set width to zero do we just show all events
    if (width > 0.0) {
      if (this.getScalePojectedWidth(lat, lon) > width) {
        return false;
      }
    }

    return true;
  }

  /**
   * we need to know the angle of the line to do our projections.
   */
  private void setupAngles() {

    // double lt2, ln2;

    // lt2 = y2 - y1;
    // ln2 = x2 - x1;

    theta = Math.atan2((x2 - x1), (y2 - y1));

    maxDistance = this.getMaxDist();
    progDistance = this.getProjectedDistance(y2, x2);

    scale = maxDistance / progDistance;

  }

  private double getProjectedDistance(double lat, double lon) {
    double dist = 0.0;

    dist = (lat - y1) * Math.cos(theta) + (lon - x1) * Math.sin(theta);

    return dist;
  }

  /**
   * Yield the scaled projected distance to this point from the start of our line.
   * @param lat
   * @param lon
   * @return scaled projected distance
   */
  public double getScaledProjectedDistance(double lat, double lon) {

    double dist = this.getProjectedDistance(lat, lon);
    dist *= scale;
    return dist;

  }

  /**
   * return the distance from the line
   * @param lat
   * @param lon
   * @return distance from the line
   */
  public double getScalePojectedWidth(double lat, double lon) {
    double wid = 0.0;

    wid = this.getPojectedWidth(lat, lon);
    wid *= scale;

    return Math.abs(wid);

  }

  /**
   * return unscaled distance from the line
   * @param lat
   * @param lon
   * @return unscaled distance from the line
   */
  private double getPojectedWidth(double lat, double lon) {
    double wid = 0.0;

    wid = (lon - x1) * Math.cos(theta) - (lat - y1) * Math.sin(theta);



    return wid;
  }

  /*
   * private double getProjectedLat(double dist) {
   * 
   * double lat;
   * double lon;
   * 
   * 
   * dist = (lat - y1) * Math.cos(theta) + (lon - x1) * Math.sin(theta);
   * 
   * return dist;
   * }
   */
  /**
   * this method will return the latitude of a point kms kilometers south of the latitude
   * @param kms
   */
  static public double getLatDiff(double kms) {
    // double scaledDistance = kms/scale;

    double d = 0.0;
    double latInc = 0.001;
    double tlat2 = 0.0;
    double tlat1 = 0.0;
    double tlon2 = 0.0;
    double tlon1 = 0.0;
    // tlat2 = tlat1;

    while (d < kms) {
      tlat2 += latInc;
      d = distFrom(tlat1, tlon1, tlat2, tlon2);
    }

    return tlat2;
  }

  /*
   * this method will return the latitude of a point kms kilometers north of the latitude
   * 
   * @param kms
   *
   * static public double getLatNorth(double kms, double tlat1) {
   * //double scaledDistance = kms/scale;
   * 
   * double d = 0.0;
   * double latInc = 0.001;
   * double tlat2 =0.0;
   * double tlon2 = 0.0;
   * double tlon1 = 0.0;
   * tlat2 = tlat1;
   * 
   * while (d<kms) {
   * tlat2 += latInc;
   * d = distFrom(tlat1, tlon1, tlat2, tlon2);
   * }
   * 
   * return tlat2;
   * }
   */

  /**
   * this method will return the longitude difference of a point kms kilometers east at the given latitude
   * @param kms
   */
  static public double getLonDiff(double kms, double tlat1) {
    // double scaledDistance = kms/scale;

    double d = 0.0;
    double lonInc = 0.001;
    double tlat2 = 0.0;
    double tlon2 = 0.0;
    double tlon1 = 0.0;
    tlat2 = tlat1;

    while (d < kms) {
      tlon2 += lonInc;
      d = distFrom(tlat1, tlon1, tlat2, tlon2);
    }

    return tlon2;
  }

  /**
   * get my max width for plotting (how far my hypocenter can be before I don't care about it.)
   */
  public double getWidth() {
    return width;
  }

  /**
   * set my max width for plotting (how far my hypocenter can be before I don't care about it.)
   * @param width
   */
  public void setWidth(double width) {
    this.width = width;
  }

  /**
   * get the first corner pot.
   * @return first corner point
   */
  public Point2D getCorner1() {
    Point2D pt = new Point2D.Double(x1, y1);
    return pt;
  }

  /**
   * get the second corner pt.
   * @return second corner point
   */
  public Point2D getCorner2() {
    Point2D pt = new Point2D.Double(x2, y2);
    return pt;
  }

  /**
   * get latitude of first corner point
   * @return latitude of first corner point
   */
  public double getLat1() {
    return x1;
  }

  /**
   * get longitude of first corner point
   * @return longitude of first corner point
   */
  public double getLon1() {
    return y1;
  }

  /**
   * get latitude of second corner point
   * @return latitude of second corner point
   */
  public double getLat2() {
    return x2;
  }

  /**
   * get longitude of second corner point
   * @return longitude of second corner point
   */
  public double getLon2() {
    return y2;
  }


  /**
   * calc the distance from pt1 to my new pt
   * @param pt 
   * @return distance
   */
  public double distanceTo(Point2D pt) {

    return distanceTo(pt.getY(), pt.getX());

  }

  /**
   * check to see if this point is inside the area I'm interested
   * @param lat
   * @param lon
   * @return true if inside
   */
  public boolean insidePlot(double lat, double lon) {
    boolean inside = true;

    // if outside of plotting area, throw out hypocenter

    return inside;
  }

  /**
   * calc the distance from pt1 to my new pt
   * @param lat
   * @param lon
   * @return distance
   */
  public double distanceTo(double lat, double lon) {
    double dist = 0.0;

    dist = distFrom(y1, x1, lat, lon);

    return dist;
  }

  /**
   * calc the distance from pt1 to pt2
   * @return distance
   */
  public double getMaxDist() {
    // double ret = getScaledProjectedDistance(y2, x2);
    double ret = distanceTo(y2, x2);
    return ret;
  }

  /**
   * return the distance from the start point to the start point. should be zero, I hope.
   * @return distance
   */
  public double getMinDist() {
    return 0.0;
  }

  /**
   * Haversine formula for the distance between 2 points on a sphere from the internet.
   * @param lat1
   * @param lng1
   * @param lat2
   * @param lng2
   * @return distance in KM.
   */
  private static double distFrom(double lat1, double lng1, double lat2, double lng2) {
    double earthRadius = 6371.01;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double dist = earthRadius * c;

    return dist;
  }
  /*
   * private static double latFrom(double lat1, double dist) {
   * double lng1 = 0.0;
   * double lat2 = 0.0;
   * double lng2 = 0.0;
   * 
   * double earthRadius = 6371.01;
   * double dLat = Math.toRadians(lat2-lat1);
   * double dLng = Math.toRadians(lng2-lng1);
   * double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
   * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
   * Math.sin(dLng/2) * Math.sin(dLng/2);
   * double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
   * double dist = earthRadius * c;
   * 
   * return dist;
   * }
   */

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    double lat1, lat2, lon1, lon2;

    boolean doRealCities = true;

    if (doRealCities) {
      // nyc
      lat1 = 40.7700;
      lon1 = -73.00;

      // sf
      lat2 = lat1;
      lon2 = lon1;

      ArbDepthCalculator adc = new ArbDepthCalculator(lat1, lon1, lat2, lon2, 10.0);


      double d = 0.0;
      double latInc = 0.01;
      double latans = 0.0;


      d = 0.0;

      lat2 = lat1 = 0.0;
      // ArbDepthCalculator adc = new ArbDepthCalculator(lat1, lon1, lat2, lon2);

      for (int j = 0; j < 30; j++) {
        latans = ArbDepthCalculator.getLatDiff(j * 10.0);
        System.out.println(
            "target lat is " + latans + " , for " + (j * 10) + " km south of lat: " + lat1);
      }


      for (int i = 0; i < 90; i += 10) {
        System.out.println("\n\n");
        d = 0.0;

        lat2 = lat1 = 1.0 * i;
        // ArbDepthCalculator adc = new ArbDepthCalculator(lat1, lon1, lat2, lon2);

        for (int j = 0; j < 30; j++) {
          latans = ArbDepthCalculator.getLonDiff(j * 10.0, lat1);
          System.out.println(
              "target lon is " + latans + " , for " + (j * 10) + " km East of lat: " + lat1);
        }
      }

      while (d < 10) {
        lat2 += latInc;
        d = ArbDepthCalculator.distFrom(lat1, lon1, lat2, lon2);
        System.out.println("dist is " + d + " km, for lat diff of: " + (lat2 - lat1));
      }


      System.out.println("\n\n");
      d = 0.0;
      double lonInc = 0.01;
      lat2 = lat1;
      lon2 = lon1;

      while (d < 10) {
        lon2 += lonInc;
        d = ArbDepthCalculator.distFrom(lat1, lon1, lat2, lon2);
      }
      System.out.println(
          "dist is " + d + " km, for lon diff of: " + (lon2 - lon1) + " for a lat of " + lat1);

      System.out.println("\n\n");
      d = 0.0;
      lonInc = 0.01;
      lat2 = lat1 = 80.0;
      lon2 = lon1;

      while (d < 10) {
        lon2 += lonInc;
        d = ArbDepthCalculator.distFrom(lat1, lon1, lat2, lon2);
      }
      System.out.println(
          "dist is " + d + " km, for lon diff of: " + (lon2 - lon1) + " for a lat of " + lat1);

      for (int i = 0; i < 90; i += 10) {
        System.out.println("\n\n");
        d = 0.0;
        lonInc = 0.01;
        lat2 = lat1 = 1.0 * i;
        lon2 = lon1;

        while (d < 10) {
          lon2 += lonInc;
          d = ArbDepthCalculator.distFrom(lat1, lon1, lat2, lon2);
        }
        System.out.println(
            "dist is " + d + " km, for lon diff of: " + (lon2 - lon1) + " for a lat of " + lat1);
      }

      System.out.println("total distance in .0016 degrees: " + adc.getMaxDist());

      lat1 = 40.77;
      lon1 = -73.98;

      // sf
      lat2 = 37.73;
      lon2 = -122.68;

      adc = new ArbDepthCalculator(lat1, lon1, lat2, lon2, 10.0);

      System.out.println("total distance is from NY to SF: " + adc.getMaxDist());

      /*
       * double dist = adc.getProjectedDistance(lat2, lon2);
       * double wid = adc.getPojectedWidth(lat2, lon2);
       * System.out.println("dist = " + dist);
       * System.out.println("wid  = " + wid);
       */

      double dist = adc.getScaledProjectedDistance(lat2, lon2);
      double wid = adc.getScalePojectedWidth(lat2, lon2);
      System.out.println("dist = " + dist);
      System.out.println("wid  = " + wid);

      System.out.println("\nchicago");

      // chicago
      double lat3 = 41.90;
      double lon3 = -87.65;

      /*
       * dist = adc.getProjectedDistance(lat3, lon3);
       * wid = adc.getPojectedWidth(lat3, lon3);
       * 
       * System.out.println("dist = " + dist);
       * System.out.println("wid  = " + wid);
       */
      dist = adc.getScaledProjectedDistance(lat3, lon3);
      wid = adc.getScalePojectedWidth(lat3, lon3);
      System.out.println("dist = " + dist);
      System.out.println("wid  = " + wid);

      // atlanta
      lat3 = 33.65;
      lon3 = -84.42;

      System.out.println("atlanta");
      /*
       * dist = adc.getProjectedDistance(lat3, lon3);
       * wid = adc.getPojectedWidth(lat3, lon3);
       * 
       * System.out.println("dist = " + dist);
       * System.out.println("wid  = " + wid);
       */
      dist = adc.getScaledProjectedDistance(lat3, lon3);
      wid = adc.getScalePojectedWidth(lat3, lon3);
      System.out.println("dist = " + dist);
      System.out.println("wid  = " + wid);


      System.out.println("trenton");
      lat3 = 40.28;
      lon3 = -74.82;

      /*
       * 
       * dist = adc.getProjectedDistance(lat3, lon3);
       * wid = adc.getPojectedWidth(lat3, lon3);
       * 
       * System.out.println("dist = " + dist);
       * System.out.println("wid  = " + wid);
       */

      dist = adc.getScaledProjectedDistance(lat3, lon3);
      wid = adc.getScalePojectedWidth(lat3, lon3);
      System.out.println("dist = " + dist);
      System.out.println("wid  = " + wid);


      System.out.println("oakland");
      lat3 = 37.73;
      lon3 = -122.22;


      /*
       * dist = adc.getProjectedDistance(lat3, lon3);
       * wid = adc.getPojectedWidth(lat3, lon3);
       * 
       * System.out.println("dist = " + dist);
       * System.out.println("wid  = " + wid);
       */

      dist = adc.getScaledProjectedDistance(lat3, lon3);
      wid = adc.getScalePojectedWidth(lat3, lon3);
      System.out.println("dist = " + dist);
      System.out.println("wid  = " + wid);


      System.out.println("albany");
      lat3 = 42.75;
      lon3 = -73.80;



      dist = adc.getScaledProjectedDistance(lat3, lon3);
      wid = adc.getScalePojectedWidth(lat3, lon3);
      System.out.println("dist = " + dist);
      System.out.println("wid  = " + wid);

    } else {


      double lt1 = 0.0;
      double ln1 = 0.0;


      double lt2 = 6.0;
      double ln2 = 8.0;

      ArbDepthCalculator adc = new ArbDepthCalculator(lt1, ln1, lt2, ln2, 10.0);

      double c = adc.getMaxDist();

      double w = 0.0;
      System.out.println("dist = " + c);


      double x = 6.0;
      double y = 8.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);

      x = 6.0;
      y = 8.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);


      x = 3.0;
      y = 4.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);

      x = 1.0;
      y = 3.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);

      x = 1.0;
      y = 2.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);

      x = 3.0;
      y = 0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);


      x = 4.0;
      y = 4.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);



      lt1 = 0.0;
      ln1 = 8.0;


      lt2 = 6.0;
      ln2 = 0.0;

      adc = new ArbDepthCalculator(lt1, ln1, lt2, ln2, 10.0);

      c = adc.getMaxDist();

      w = 0.0;
      System.out.println("dist = " + c);


      x = 6.0;
      y = 8.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);


      x = 6.0;
      y = 8.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);


      x = 3.0;
      y = 4.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);

      x = 7.0;
      y = 3.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);

      x = 7.0;
      y = 2.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);

      x = 3.0;
      y = 0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);


      x = 4.0;
      y = 4.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);


      lt1 = 0.0;
      ln1 = 0.0;


      lt2 = 6.0;
      ln2 = -8.0;

      adc = new ArbDepthCalculator(lt1, ln1, lt2, ln2, 10.0);

      c = adc.getMaxDist();

      w = 0.0;
      System.out.println("dist = " + c);


      x = 6.0;
      y = 0.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);


      x = 6.0;
      y = 0.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);


      x = 3.0;
      y = -4.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);

      x = 7.0;
      y = -5.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);

      x = 7.0;
      y = -6.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);

      x = 3.0;
      y = -8.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);


      x = 4.0;
      y = -8.0;
      c = adc.getProjectedDistance(x, y);
      w = adc.getPojectedWidth(x, y);
      System.out.println("for (" + x + "," + y + ") .. dist = " + c);
      System.out.println("for (" + x + "," + y + ") .. width = " + w);



    }
  }


}
