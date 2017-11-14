package gov.usgs.volcanoes.core.legacy.plot.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * A simple Renderer that renders a Shape object with a particular color and stroke.
 *
 * @author  Dan Cervelli
 */
public class ShapeRenderer implements Renderer {
  /** The Color.
   */
  public Color color;

  /** The Shape.
   */
  public Shape shape;

  /** The Stroke.
   */
  public Stroke stroke = new BasicStroke(1.0f);

  public boolean antiAlias;

  /** Generic constructor.
   */
  public ShapeRenderer(Shape s) {
    shape = s;
  }

  /** Renders the Shape.
   * @param g the Graphics2D object to render to
   */
  public void render(Graphics2D g) {
    Stroke origStroke = g.getStroke();
    Color origColor = g.getColor();
    Object origRenderingHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

    if (antiAlias)
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (stroke != null)
      g.setStroke(stroke);

    if (color != null)
      g.setColor(color);

    if (shape != null)
      g.draw(shape);

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, origRenderingHint);
    g.setColor(origColor);
    g.setStroke(origStroke);
  }

}
