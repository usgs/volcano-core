package gov.usgs.volcanoes.core.legacy.plot.color;

import java.awt.Color;
import java.awt.image.IndexColorModel;

/**
 * <p>Jet2 is a color spectrum derived from Jim Luetgert's sgram earthworm
 * module.</p>
 *
 * <p>There are no threading issues because all of this data is read only.</p>
 *
 * <p>@author Peter Cervelli</p>
 */
public class Jet2 extends Spectrum {
  private static Jet2 self;

  private Jet2() {
    buildColors();
  }

  /** Gets the singleton instance of Jet.
   * @return the Jet instance
   */
  public static Spectrum getInstance() {
    if (self == null) {
      self = new Jet2();
    }

    return self;
  }

  private void buildColors() {
    colors = new Color[] {new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0),
        new Color(0, 0, 0), new Color(0, 0, 0),};

    paletteBytes = new byte[] {(byte) 0, (byte) 0, (byte) 131, (byte) 0, (byte) 0, (byte) 135,
        (byte) 0, (byte) 0, (byte) 139, (byte) 0, (byte) 0, (byte) 143, (byte) 0, (byte) 0,
        (byte) 147, (byte) 0, (byte) 0, (byte) 151, (byte) 0, (byte) 0, (byte) 155, (byte) 0,
        (byte) 0, (byte) 159, (byte) 0, (byte) 0, (byte) 163, (byte) 0, (byte) 0, (byte) 167,
        (byte) 0, (byte) 0, (byte) 171, (byte) 0, (byte) 0, (byte) 175, (byte) 0, (byte) 0,
        (byte) 179, (byte) 0, (byte) 0, (byte) 183, (byte) 0, (byte) 0, (byte) 187, (byte) 0,
        (byte) 0, (byte) 191, (byte) 0, (byte) 0, (byte) 195, (byte) 0, (byte) 0, (byte) 199,
        (byte) 0, (byte) 0, (byte) 203, (byte) 0, (byte) 0, (byte) 207, (byte) 0, (byte) 0,
        (byte) 211, (byte) 0, (byte) 0, (byte) 215, (byte) 0, (byte) 0, (byte) 219, (byte) 0,
        (byte) 0, (byte) 223, (byte) 0, (byte) 0, (byte) 227, (byte) 0, (byte) 0, (byte) 231,
        (byte) 0, (byte) 0, (byte) 235, (byte) 0, (byte) 0, (byte) 239, (byte) 0, (byte) 0,
        (byte) 243, (byte) 0, (byte) 0, (byte) 247, (byte) 0, (byte) 0, (byte) 251, (byte) 0,
        (byte) 0, (byte) 255, (byte) 0, (byte) 3, (byte) 255, (byte) 0, (byte) 7, (byte) 255,
        (byte) 0, (byte) 11, (byte) 255, (byte) 0, (byte) 15, (byte) 255, (byte) 0, (byte) 19,
        (byte) 255, (byte) 0, (byte) 23, (byte) 255, (byte) 0, (byte) 27, (byte) 255, (byte) 0,
        (byte) 31, (byte) 255, (byte) 0, (byte) 35, (byte) 255, (byte) 0, (byte) 39, (byte) 255,
        (byte) 0, (byte) 43, (byte) 255, (byte) 0, (byte) 47, (byte) 255, (byte) 0, (byte) 51,
        (byte) 255, (byte) 0, (byte) 55, (byte) 255, (byte) 0, (byte) 59, (byte) 255, (byte) 0,
        (byte) 63, (byte) 255, (byte) 0, (byte) 67, (byte) 255, (byte) 0, (byte) 71, (byte) 255,
        (byte) 0, (byte) 75, (byte) 255, (byte) 0, (byte) 79, (byte) 255, (byte) 0, (byte) 83,
        (byte) 255, (byte) 0, (byte) 87, (byte) 255, (byte) 0, (byte) 91, (byte) 255, (byte) 0,
        (byte) 95, (byte) 255, (byte) 0, (byte) 99, (byte) 255, (byte) 0, (byte) 103, (byte) 255,
        (byte) 0, (byte) 107, (byte) 255, (byte) 0, (byte) 111, (byte) 255, (byte) 0, (byte) 115,
        (byte) 255, (byte) 0, (byte) 119, (byte) 255, (byte) 0, (byte) 123, (byte) 255, (byte) 0,
        (byte) 127, (byte) 255, (byte) 0, (byte) 131, (byte) 255, (byte) 0, (byte) 135, (byte) 255,
        (byte) 0, (byte) 139, (byte) 255, (byte) 0, (byte) 143, (byte) 255, (byte) 0, (byte) 147,
        (byte) 255, (byte) 0, (byte) 151, (byte) 255, (byte) 0, (byte) 155, (byte) 255, (byte) 0,
        (byte) 159, (byte) 255, (byte) 0, (byte) 163, (byte) 255, (byte) 0, (byte) 167, (byte) 255,
        (byte) 0, (byte) 171, (byte) 255, (byte) 0, (byte) 175, (byte) 255, (byte) 0, (byte) 179,
        (byte) 255, (byte) 0, (byte) 183, (byte) 255, (byte) 0, (byte) 187, (byte) 255, (byte) 0,
        (byte) 191, (byte) 255, (byte) 0, (byte) 195, (byte) 255, (byte) 0, (byte) 199, (byte) 255,
        (byte) 0, (byte) 203, (byte) 255, (byte) 0, (byte) 207, (byte) 255, (byte) 0, (byte) 211,
        (byte) 255, (byte) 0, (byte) 215, (byte) 255, (byte) 0, (byte) 219, (byte) 255, (byte) 0,
        (byte) 223, (byte) 255, (byte) 0, (byte) 227, (byte) 255, (byte) 0, (byte) 231, (byte) 255,
        (byte) 0, (byte) 235, (byte) 255, (byte) 0, (byte) 239, (byte) 255, (byte) 0, (byte) 243,
        (byte) 255, (byte) 0, (byte) 247, (byte) 255, (byte) 0, (byte) 251, (byte) 255, (byte) 0,
        (byte) 255, (byte) 255, (byte) 3, (byte) 255, (byte) 251, (byte) 7, (byte) 255, (byte) 247,
        (byte) 11, (byte) 255, (byte) 243, (byte) 15, (byte) 255, (byte) 239, (byte) 19, (byte) 255,
        (byte) 235, (byte) 23, (byte) 255, (byte) 231, (byte) 27, (byte) 255, (byte) 227, (byte) 31,
        (byte) 255, (byte) 223, (byte) 35, (byte) 255, (byte) 219, (byte) 39, (byte) 255,
        (byte) 215, (byte) 43, (byte) 255, (byte) 211, (byte) 47, (byte) 255, (byte) 207, (byte) 51,
        (byte) 255, (byte) 203, (byte) 55, (byte) 255, (byte) 199, (byte) 59, (byte) 255,
        (byte) 195, (byte) 63, (byte) 255, (byte) 191, (byte) 67, (byte) 255, (byte) 187, (byte) 71,
        (byte) 255, (byte) 183, (byte) 75, (byte) 255, (byte) 179, (byte) 79, (byte) 255,
        (byte) 175, (byte) 83, (byte) 255, (byte) 171, (byte) 87, (byte) 255, (byte) 167, (byte) 91,
        (byte) 255, (byte) 163, (byte) 95, (byte) 255, (byte) 159, (byte) 99, (byte) 255,
        (byte) 155, (byte) 103, (byte) 255, (byte) 151, (byte) 107, (byte) 255, (byte) 147,
        (byte) 111, (byte) 255, (byte) 143, (byte) 115, (byte) 255, (byte) 139, (byte) 119,
        (byte) 255, (byte) 135, (byte) 123, (byte) 255, (byte) 131, (byte) 127, (byte) 255,
        (byte) 127, (byte) 131, (byte) 255, (byte) 123, (byte) 135, (byte) 255, (byte) 119,
        (byte) 139, (byte) 255, (byte) 115, (byte) 143, (byte) 255, (byte) 111, (byte) 147,
        (byte) 255, (byte) 107, (byte) 151, (byte) 255, (byte) 103, (byte) 155, (byte) 255,
        (byte) 99, (byte) 159, (byte) 255, (byte) 95, (byte) 163, (byte) 255, (byte) 91, (byte) 167,
        (byte) 255, (byte) 87, (byte) 171, (byte) 255, (byte) 83, (byte) 175, (byte) 255, (byte) 79,
        (byte) 179, (byte) 255, (byte) 75, (byte) 183, (byte) 255, (byte) 71, (byte) 187,
        (byte) 255, (byte) 67, (byte) 191, (byte) 255, (byte) 63, (byte) 195, (byte) 255, (byte) 59,
        (byte) 199, (byte) 255, (byte) 55, (byte) 203, (byte) 255, (byte) 51, (byte) 207,
        (byte) 255, (byte) 47, (byte) 211, (byte) 255, (byte) 43, (byte) 215, (byte) 255, (byte) 39,
        (byte) 219, (byte) 255, (byte) 35, (byte) 223, (byte) 255, (byte) 31, (byte) 227,
        (byte) 255, (byte) 27, (byte) 231, (byte) 255, (byte) 23, (byte) 235, (byte) 255, (byte) 19,
        (byte) 239, (byte) 255, (byte) 15, (byte) 243, (byte) 255, (byte) 11, (byte) 247,
        (byte) 255, (byte) 7, (byte) 251, (byte) 255, (byte) 3, (byte) 255, (byte) 255, (byte) 0,
        (byte) 255, (byte) 251, (byte) 0, (byte) 255, (byte) 247, (byte) 0, (byte) 255, (byte) 243,
        (byte) 0, (byte) 255, (byte) 239, (byte) 0, (byte) 255, (byte) 235, (byte) 0, (byte) 255,
        (byte) 231, (byte) 0, (byte) 255, (byte) 227, (byte) 0, (byte) 255, (byte) 223, (byte) 0,
        (byte) 255, (byte) 219, (byte) 0, (byte) 255, (byte) 215, (byte) 0, (byte) 255, (byte) 211,
        (byte) 0, (byte) 255, (byte) 207, (byte) 0, (byte) 255, (byte) 203, (byte) 0, (byte) 255,
        (byte) 199, (byte) 0, (byte) 255, (byte) 195, (byte) 0, (byte) 255, (byte) 191, (byte) 0,
        (byte) 255, (byte) 187, (byte) 0, (byte) 255, (byte) 183, (byte) 0, (byte) 255, (byte) 179,
        (byte) 0, (byte) 255, (byte) 175, (byte) 0, (byte) 255, (byte) 171, (byte) 0, (byte) 255,
        (byte) 167, (byte) 0, (byte) 255, (byte) 163, (byte) 0, (byte) 255, (byte) 159, (byte) 0,
        (byte) 255, (byte) 155, (byte) 0, (byte) 255, (byte) 151, (byte) 0, (byte) 255, (byte) 147,
        (byte) 0, (byte) 255, (byte) 143, (byte) 0, (byte) 255, (byte) 139, (byte) 0, (byte) 255,
        (byte) 135, (byte) 0, (byte) 255, (byte) 131, (byte) 0, (byte) 255, (byte) 127, (byte) 0,
        (byte) 255, (byte) 123, (byte) 0, (byte) 255, (byte) 119, (byte) 0, (byte) 255, (byte) 115,
        (byte) 0, (byte) 255, (byte) 111, (byte) 0, (byte) 255, (byte) 107, (byte) 0, (byte) 255,
        (byte) 103, (byte) 0, (byte) 255, (byte) 99, (byte) 0, (byte) 255, (byte) 95, (byte) 0,
        (byte) 255, (byte) 91, (byte) 0, (byte) 255, (byte) 87, (byte) 0, (byte) 255, (byte) 83,
        (byte) 0, (byte) 255, (byte) 79, (byte) 0, (byte) 255, (byte) 75, (byte) 0, (byte) 255,
        (byte) 71, (byte) 0, (byte) 255, (byte) 67, (byte) 0, (byte) 255, (byte) 63, (byte) 0,
        (byte) 255, (byte) 59, (byte) 0, (byte) 255, (byte) 55, (byte) 0, (byte) 255, (byte) 51,
        (byte) 0, (byte) 255, (byte) 47, (byte) 0, (byte) 255, (byte) 43, (byte) 0, (byte) 255,
        (byte) 39, (byte) 0, (byte) 255, (byte) 35, (byte) 0, (byte) 255, (byte) 31, (byte) 0,
        (byte) 255, (byte) 27, (byte) 0, (byte) 255, (byte) 23, (byte) 0, (byte) 255, (byte) 19,
        (byte) 0, (byte) 255, (byte) 15, (byte) 0, (byte) 255, (byte) 11, (byte) 0, (byte) 255,
        (byte) 7, (byte) 0, (byte) 255, (byte) 3, (byte) 0, (byte) 255, (byte) 0, (byte) 0,
        (byte) 251, (byte) 0, (byte) 0, (byte) 247, (byte) 0, (byte) 0, (byte) 243, (byte) 0,
        (byte) 0, (byte) 239, (byte) 0, (byte) 0, (byte) 235, (byte) 0, (byte) 0, (byte) 231,
        (byte) 0, (byte) 0, (byte) 227, (byte) 0, (byte) 0, (byte) 223, (byte) 0, (byte) 0,
        (byte) 219, (byte) 0, (byte) 0, (byte) 215, (byte) 0, (byte) 0, (byte) 211, (byte) 0,
        (byte) 0, (byte) 207, (byte) 0, (byte) 0, (byte) 203, (byte) 0, (byte) 0, (byte) 199,
        (byte) 0, (byte) 0, (byte) 195, (byte) 0, (byte) 0, (byte) 191, (byte) 0, (byte) 0,
        (byte) 187, (byte) 0, (byte) 0, (byte) 183, (byte) 0, (byte) 0, (byte) 179, (byte) 0,
        (byte) 0, (byte) 175, (byte) 0, (byte) 0, (byte) 171, (byte) 0, (byte) 0, (byte) 167,
        (byte) 0, (byte) 0, (byte) 163, (byte) 0, (byte) 0, (byte) 159, (byte) 0, (byte) 0,
        (byte) 155, (byte) 0, (byte) 0, (byte) 151, (byte) 0, (byte) 0, (byte) 147, (byte) 0,
        (byte) 0, (byte) 143, (byte) 0, (byte) 0, (byte) 139, (byte) 0, (byte) 0, (byte) 135,
        (byte) 0, (byte) 0, (byte) 131, (byte) 0, (byte) 0, (byte) 127, (byte) 0, (byte) 0};

    palette = new IndexColorModel(8, 255, paletteBytes, 0, false);
  }
}
