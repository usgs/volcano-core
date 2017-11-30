package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.legacy.plot.transform.IdentityTransformer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.text.AttributedString;

/**
 * <p>A class that renders a string at a point.  Orientation and justification 
 * can be specified.  Access the public member variables directly to set 
 * renderer specifications.</p>
 *
 * TODO: test background colors with rotated labels
 * 
 * @author Dan Cervelli
 */
public class TextRenderer extends PointRenderer {
  public static final Font DEFAULT_FONT = Font.decode("dialog-PLAIN-11");
  public static final Font SMALL_FONT = Font.decode("dialog-PLAIN-7");

  /** Vertical and horizontal justification flag: none.
   */
  public static final int NONE = 0;

  /** Vertical and horizatonal justification flag: center.
   */
  public static final int CENTER = 1;

  /** Horizontal justification flag: left.
   */
  public static final int LEFT = 2;

  /** Horizontal justification flag: right.
   */
  public static final int RIGHT = 3;

  /** Vertical jutification flag: top.
   */
  public static final int TOP = 4;

  /** Vertical justification flag: bottom.
   */
  public static final int BOTTOM = 5;

  /** The text string.
   */
  public String text;

  /** The font to use.
   */
  public Font font = DEFAULT_FONT;

  /** The orientation in degrees.
   */
  public float orientation;
  // public float x, y;

  /** The vertical justification flag.
   */
  public int vertJustification;
  /** The horizontal justification flag.
   */
  public int horizJustification;

  public boolean antiAlias = true;

  public int xBump;
  public int yBump;

  public Color backgroundColor;
  public int backgroundWidth = -1;
  public int backgroundHeight = -1;

  /** 
   * Constructs an empty text renderer
   */
  public TextRenderer() {
    vertJustification = NONE;
    horizJustification = NONE;
    orientation = 0;
  }

  /**
   * Constructor
   * @param xx X text location
   * @param yy Y text location
   * @param t text itself
   */
  public TextRenderer(double xx, double yy, String t) {
    this();
    x = xx;
    y = yy;
    text = t;
  }

  /**
   * Constructor
   * @param xx X text link point location
   * @param yy Y text link point location
   * @param t text itself
   * @param c text color
   */
  public TextRenderer(double xx, double yy, String t, Color c) {
    this(xx, yy, t);
    color = c;
  }

  /**
   * Constructor
   * @param xx X text link point location
   * @param yy Y text link point location
   * @param t text itself
   * @param c text color
   * @param f text font
   */
  public TextRenderer(double xx, double yy, String t, Color c, Font f) {
    this(xx, yy, t, c);
    font = f;
  }

  /**
   * Compute top left coordinate of text surrounding box location, depends from justification and orientation 
   */
  public Point getPixelLocation(Graphics2D g) {
    if (transformer == null)
      transformer = new IdentityTransformer();

    FontMetrics fm = g.getFontMetrics(g.getFont());
    double ax = transformer.getXPixel(x);
    double ay = transformer.getYPixel(y);
    switch (horizJustification) {
      case NONE:
        break;
      case LEFT:
        break;
      case CENTER:
        ax -= fm.stringWidth(text) / 2;
        break;
      case RIGHT:
        ax -= fm.stringWidth(text);
        break;
    }
    switch (vertJustification) {
      case NONE:
        break;
      case BOTTOM:
        ay -= 2;
        break;
      case TOP:
        ay += fm.getHeight();
        break;
      case CENTER:
        ay += fm.getHeight() / 4;
        break;
    }

    return new Point(xBump + (int) Math.round(ax + fm.stringWidth(text) / 2),
        yBump + (int) Math.round(ay - fm.getHeight() / 2));
  }

  /**
   * Compute text surrounding box position and size, depend from link point location, justification, orientation etc
   */
  public Rectangle getBoundingBox(Graphics2D g) {
    Font origFont = g.getFont();
    g.setFont(font);
    FontMetrics fm = g.getFontMetrics(g.getFont());
    Point loc = getPixelLocation(g);
    Rectangle rect = new Rectangle(loc.x - fm.stringWidth(text) / 2 - 2,
        loc.y - fm.getHeight() / 2 + 2, fm.stringWidth(text) + 2, fm.getHeight());
    g.setFont(origFont);
    return rect;
  }

  /** Renders the text string
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {
    Font origFont = g.getFont();
    AffineTransform origAT = g.getTransform();
    Color origColor = g.getColor();
    Object origRenderingHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

    if (antiAlias)
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setFont(font);

    if (text != null) {
      Point loc = getPixelLocation(g);

      if (backgroundColor != null) {
        g.setColor(backgroundColor);
        Rectangle r = getBoundingBox(g);
        if (backgroundWidth != -1)
          r.width = backgroundWidth;
        if (backgroundHeight != -1)
          r.height = backgroundHeight;
        Rectangle fr = new Rectangle(r);
        fr.x += 1;
        fr.y += 1;
        fr.width--;
        fr.height--;
        g.fill(fr);
        g.setColor(Color.BLACK);
        g.draw(r);
      }

      g.translate(loc.x, loc.y);
      FontMetrics fm = g.getFontMetrics(g.getFont());
      int height = fm.getHeight();
      int width = fm.stringWidth(text);
      g.rotate(Math.toRadians(orientation));
      g.setColor(color);

      int caret = text.indexOf("^");
      if (caret != -1) {
        text = text.replace("^", "");
        AttributedString as1 = new AttributedString(text);

        int endindex = text.length();
        int closesuper = text.indexOf(" ", caret);
        if (closesuper != -1) {
          text = text.substring(0, closesuper) + text.substring(closesuper + 1);
          endindex = closesuper;
        }
        as1.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, caret,
            endindex);
        g.drawString(as1.getIterator(), -width / 2, height / 2);
      } else {
        g.drawString(text, -width / 2, height / 2);
      }
    }

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, origRenderingHint);
    g.setColor(origColor);
    g.setFont(origFont);
    g.setTransform(origAT);
  }

}
