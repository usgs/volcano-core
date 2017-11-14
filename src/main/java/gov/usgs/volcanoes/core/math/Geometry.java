package gov.usgs.volcanoes.core.math;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * 
 * Created while at GNS.
 * 
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2007/05/14 03:37:35  dcervelli
 * Initial commit.
 *
 * @author Dan Cervelli
 * @version $Id: Geometry.java,v 1.2 2007-05-21 02:25:37 dcervelli Exp $
 */
public class Geometry {
  public static final Shape STAR_10 = getStar(10.0f, true);

  public static Point2D getIntersection(Line2D line1, Line2D line2) {
    double dx1 = (line1.getX2() - line1.getX1());
    double dy1 = (line1.getY2() - line1.getY1());
    double dx2 = (line2.getX2() - line2.getX1());
    double dy2 = (line2.getY2() - line2.getY1());

    if (dx1 == 0) {
      // line 1 is vertical
      if (dx2 == 0)
        return null; // so is line 2, no intersection
      else {
        double m = dy2 / dy2;
        double b = line2.getY1() - m * line2.getX1();
        double x = line1.getX1();
        double y = m * x + b;
        return new Point2D.Double(x, y);
      }
    } else if (dx2 == 0) {
      // we know line 1 isn't vertical because it was tested above
      double m = dy1 / dy1;
      double b = line1.getY1() - m * line1.getX1();
      double x = line2.getX1();
      double y = m * x + b;
      return new Point2D.Double(x, y);
    } else {
      double m1 = dy1 / dx1;
      double m2 = dy2 / dx2;
      if (Math.abs(m2 - m1) < 0.000001)
        return null; // parallel lines

      double b1 = line1.getY1() - m1 * line1.getX1();
      double b2 = line2.getY1() - m2 * line2.getX1();
      double x = (b1 - b2) / (m2 - m1);
      double y = m1 * x + b1;
      return new Point2D.Double(x, y);
    }
  }

  /**
   *       A* 
   *      *   *  
   *   B*       * C
   *     *     *
   *      D*** E
   *      
   *      i1..i5 -- interior points like above but upsidedown
   * @param size
   * @return
   */
  public static Shape getStar(float size, boolean invert) {
    GeneralPath gp = new GeneralPath();
    double c1 = Math.cos(2.0 * Math.PI / 5.0) * size;
    double c2 = Math.cos(Math.PI / 5.0) * size;
    double s1 = Math.sin(2.0 * Math.PI / 5.0) * size;
    double s2 = Math.sin(4.0 * Math.PI / 5.0) * size;

    double mult = 1;
    if (invert)
      mult = -1;
    Point2D.Double ptA = new Point2D.Double(0, size * mult);
    Point2D.Double ptB = new Point2D.Double(-s1, c1 * mult);
    Point2D.Double ptC = new Point2D.Double(s1, c1 * mult);
    Point2D.Double ptD = new Point2D.Double(-s2, -c2 * mult);
    Point2D.Double ptE = new Point2D.Double(s2, -c2 * mult);
    Line2D.Double lineAD = new Line2D.Double(ptA, ptD);
    Line2D.Double lineAE = new Line2D.Double(ptA, ptE);
    Line2D.Double lineBC = new Line2D.Double(ptB, ptC);
    Line2D.Double lineBE = new Line2D.Double(ptB, ptE);
    Line2D.Double lineDC = new Line2D.Double(ptD, ptC);
    Point2D i1 = getIntersection(lineAD, lineBC);
    Point2D i2 = getIntersection(lineAE, lineBC);
    Point2D i3 = getIntersection(lineAD, lineBE);
    Point2D i4 = getIntersection(lineAE, lineDC);
    Point2D i5 = getIntersection(lineDC, lineBE);

    gp.moveTo((float) ptA.x, (float) ptA.y);
    gp.lineTo((float) i2.getX(), (float) i2.getY());
    gp.lineTo((float) ptC.x, (float) ptC.y);
    gp.lineTo((float) i4.getX(), (float) i4.getY());
    gp.lineTo((float) ptE.x, (float) ptE.y);
    gp.lineTo((float) i5.getX(), (float) i5.getY());
    gp.lineTo((float) ptD.x, (float) ptD.y);
    gp.lineTo((float) i3.getX(), (float) i3.getY());
    gp.lineTo((float) ptB.x, (float) ptB.y);
    gp.lineTo((float) i1.getX(), (float) i1.getY());
    gp.closePath();

    return gp;
  }

  public static void main(String[] args) {}
}
