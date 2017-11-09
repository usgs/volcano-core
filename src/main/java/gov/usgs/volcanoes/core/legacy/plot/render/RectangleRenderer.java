package gov.usgs.volcanoes.core.legacy.plot.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>A simple Renderer that renders a Rectangle2D object.  This class exists
 * in case functionality to snazz up the rectangle is wanted (rounded corners,
 * double frames, etc).</p>
 *
 * TODO: extend ShapeRenderer
 * 
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/08/26 18:05:04  uid879
 * Initial avosouth commit.
 *
 * @author  Dan Cervelli
 */
public class RectangleRenderer implements Renderer {
  /** The Rectangle.
   */
  public Rectangle2D.Double rect;

  public Color backgroundColor = null;
  public Color color = null;

  /** Generic constructor.
   */
  public RectangleRenderer() {
    rect = new Rectangle2D.Double();
  }

  /** Renders the rectangle.
   * @param g the Graphics2D object to render to
   */
  public void render(Graphics2D g) {
    Color c = g.getColor();
    if (backgroundColor != null) {
      g.setColor(backgroundColor);
      g.fill(rect);
      if (color == null)
        g.setColor(c);
    }
    if (color != null)
      g.setColor(color);
    g.draw(rect);
    g.setColor(c);
  }

}
