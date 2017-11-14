package gov.usgs.volcanoes.core.legacy.plot.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

/**
 * A renderer that renders a line
 * TODO: extend ShapeRenderer
 * 
 * @author Dan Cervelli
 */
public class LineRenderer implements Renderer {
  public static final Stroke DASHED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
      BasicStroke.JOIN_MITER, 10.0f, new float[] {1.0f, 2.0f}, 0.0f);

  // Some sort of Java bug causes non-dashed strokes to occasionally to
  // be offset by one pixel from a dashed stroke. Use this stroke
  // to line up perfectly with other dashed strokes.
  public static final Stroke TICK_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
      BasicStroke.JOIN_MITER, 10.0f, new float[] {1.0f, 0.0f}, 0.0f);

  /** The Line.
   */
  public Line2D.Double line;

  /** The Stroke.
   */
  public Stroke stroke;// = NORMAL_STROKE;

  /** The Color.
   */
  public Color color;

  /** Generic empty constructor.
   */
  public LineRenderer() {}

  /** Renders the line object using the internally stored stroke and color.  
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {

    Stroke origStroke = g.getStroke();
    Color origColor = g.getColor();

    if (stroke != null)
      g.setStroke(stroke);

    if (color != null)
      g.setColor(color);
    if (line != null && !(Double.isNaN(line.x1) || Double.isNaN(line.x2) || Double.isNaN(line.y1)
        || Double.isNaN(line.y2)))
      g.draw(line);

    g.setStroke(origStroke);
    g.setColor(origColor);
  }
}
