package gov.usgs.volcanoes.core.legacy.plot.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

/**
 * <p>Render text as black font with grey shadow</p>
 * 
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 */
public class ShadowedTextRenderer extends TextRenderer {
  public Color shadowColor = Color.LIGHT_GRAY;
  public int shadowX = 1;
  public int shadowY = 1;

  /**
   * Default constructor, shadow shift is 1,1
   */
  public ShadowedTextRenderer() {
    super();
    color = Color.BLACK;
  }

  /**
   * Constructor
   * @param xx X shadow shift
   * @param yy Y shadow shift
   * @param t
   */
  public ShadowedTextRenderer(double xx, double yy, String t) {
    super(xx, yy, t);
    color = Color.BLACK;
  }

  /** Renders the shadowed text.
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {
    Font origFont = g.getFont();
    AffineTransform origAT = g.getTransform();
    Color origColor = g.getColor();

    Object aa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setFont(font);

    if (text != null) {
      Point loc = getPixelLocation(g);

      g.translate(loc.x, loc.y);
      g.rotate(Math.toRadians(orientation));

      FontMetrics fm = g.getFontMetrics(g.getFont());
      g.setColor(shadowColor);
      g.drawString(text, -fm.stringWidth(text) / 2 + shadowX, fm.getHeight() / 2 + shadowY);
      g.setColor(color);
      g.drawString(text, -fm.stringWidth(text) / 2, fm.getHeight() / 2);
    }

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa);
    g.setColor(origColor);
    g.setFont(origFont);
    g.setTransform(origAT);
  }
}
