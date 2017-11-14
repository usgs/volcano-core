package gov.usgs.volcanoes.core.legacy.plot.render;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Frame renderer with list of renderers to process internal frame areas</p>
 * 
 * @author Dan Cervelli
 */
public class BasicFrameRenderer extends FrameRenderer {
  private List<Renderer> renderers;

  /** Constructor that accepts a LineData object.
   */
  public BasicFrameRenderer() {
    renderers = new ArrayList<Renderer>();
  }

  /** Adds a generic renderer.
   * @param r the Renderer
   */
  public void addRenderer(Renderer r) {
    renderers.add(r);
  }

  /** Renderers the axis and the list of renderers.
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {
    if (axis != null)
      axis.render(g);

    for (Renderer renderer : renderers)
      renderer.render(g);
  }
}
