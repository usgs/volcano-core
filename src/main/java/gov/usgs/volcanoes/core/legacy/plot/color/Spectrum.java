package gov.usgs.volcanoes.core.legacy.plot.color;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.image.IndexColorModel;

/**
 * <p>A utility class for working with color spectra.  This class is used by
 * all of the Valve plotting classes that deal with color spectra: spectrograms,
 * earthquake hypocenter times, etc.</p>
 *
 * <p>This class is meant to be subclassed by a class that contains the color
 * information and therefore has a protected constructor -- it cannot be 
 * instantiated directly.  See the Jet class for a further information regarding
 * the use of this class.</p>
 * 
 * <p>@author Dan Cervelli</p>
 */
public class Spectrum {
  /** 
   * The constructor is protected to avoid direct instances of Spectrum.
   */
  protected Spectrum() {}

  /** The Colors that make up this spectrum, used for cycling through the
   * colors.
   */
  public Color[] colors;

  /** 
   * The palette that makes up the colors, used for making image maps.
   */
  public IndexColorModel palette;

  /** 
   * The bytes (color values) that make up the palette.
   */
  public byte[] paletteBytes;

  /** Gets the number of colors in this spectrum.
   * @return the number of colors
   */
  public int getNumColors() {
    return colors.length;
  }

  /** Gets the correct color based on an input of [0,1].
   * @param d the color input
   * @return the color
   */
  public Color getColorByRatio(double d) {
    return colors[(int) (d * (double) colors.length)];
  }

  /** Gets the correct color index based on an input of [0,1].
   * @param d the color input
   * @return the color index
   */
  public int getColorIndexByRatio(double d) {
    return (int) (d * (double) colors.length);
  }

  /** Draws a not particularly intelligent color scale bar.
   * @param g the graphics object upon which to render
   * @param x the x-pixel coordinate of the scale bar
   * @param y the y-pixel coordinate of the scale bar
   * @param width the width of the scale bar
   * @param height the height of the scale bar
   * @param vertical whether or not the scale bar should be vertical
   * @param box whether or not to draw a box around the scale bar
   */
  public void renderScale(Graphics2D g, double x, double y, double width, double height,
      boolean vertical, boolean box) {
    Paint p = g.getPaint();
    Rectangle2D.Double rect = new Rectangle2D.Double();
    double step = (vertical ? height / colors.length : width / colors.length);
    for (int i = 0; i < colors.length; i++) {
      if (!vertical) {
        rect.setRect(x + i * step, y, step, height);
      } else {
        rect.setRect(x, y + i * step, width, step);
      }

      g.setPaint(colors[i]);
      g.fill(rect);
    }
    if (box) {
      g.setPaint(Color.BLACK);
      rect.setRect(x - 1, y - 1, width + 1, height + 1);
      g.draw(rect);
    }
    g.setPaint(p);
  }

}
