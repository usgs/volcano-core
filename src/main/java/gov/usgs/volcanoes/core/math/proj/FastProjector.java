package gov.usgs.volcanoes.core.math.proj;

import java.awt.geom.Point2D;

/**
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 */
public interface FastProjector {
  public void forward(Point2D.Double pt);

  public void inverse(Point2D.Double pt);
}
