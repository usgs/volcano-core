package gov.usgs.volcanoes.core.legacy.plot.color;

import java.awt.Color;
import java.awt.image.IndexColorModel;

/**
 * <p>Jet is a color spectrum taken directly for Jim Luetgert's sgram earthworm
 * module.</p>
 *
 * <p>There are no threading issues because all of this data is read only.</p>
 *
 * $Log: not supported by cvs2svn $ 
 * @author Dan Cervelli
 */
public class Jet extends Spectrum {
  private static Jet self;

  private Jet() {
    buildColors();
  }

  /** Gets the singleton instance of Jet.
   * @return the Jet instance
   */
  public static Spectrum getInstance() {
    if (self == null)
      self = new Jet();

    return self;
  }

  private void buildColors() {
    colors = new Color[] {new Color(0, 0, 159), new Color(0, 0, 132), new Color(0, 0, 137),
        new Color(0, 0, 142), new Color(0, 0, 147), new Color(0, 0, 153), new Color(0, 0, 158),
        new Color(0, 0, 163), new Color(0, 0, 168), new Color(0, 0, 173), new Color(0, 0, 178),
        new Color(0, 0, 183), new Color(0, 0, 188), new Color(0, 0, 193), new Color(0, 0, 198),
        new Color(0, 0, 203), new Color(0, 0, 209), new Color(0, 0, 214), new Color(0, 0, 219),
        new Color(0, 0, 224), new Color(0, 0, 229), new Color(0, 0, 234), new Color(0, 0, 239),
        new Color(0, 0, 244), new Color(0, 0, 249), new Color(0, 0, 255), new Color(0, 5, 255),
        new Color(0, 10, 255), new Color(0, 15, 255), new Color(0, 20, 255), new Color(0, 25, 255),
        new Color(0, 30, 255), new Color(0, 35, 255), new Color(0, 40, 255), new Color(0, 45, 255),
        new Color(0, 50, 255), new Color(0, 56, 255), new Color(0, 61, 255), new Color(0, 66, 255),
        new Color(0, 71, 255), new Color(0, 76, 255), new Color(0, 81, 255), new Color(0, 86, 255),
        new Color(0, 91, 255), new Color(0, 96, 255), new Color(0, 102, 255),
        new Color(0, 107, 255), new Color(0, 112, 255), new Color(0, 117, 255),
        new Color(0, 122, 255), new Color(0, 127, 255), new Color(0, 132, 255),
        new Color(0, 137, 255), new Color(0, 142, 255), new Color(0, 147, 255),
        new Color(0, 153, 255), new Color(0, 158, 255), new Color(0, 163, 255),
        new Color(0, 168, 255), new Color(0, 173, 255), new Color(0, 178, 255),
        new Color(0, 183, 255), new Color(0, 188, 255), new Color(0, 193, 255),
        new Color(0, 198, 255), new Color(0, 204, 255), new Color(0, 209, 255),
        new Color(0, 214, 255), new Color(0, 219, 255), new Color(0, 224, 255),
        new Color(0, 229, 255), new Color(0, 234, 255), new Color(0, 239, 255),
        new Color(0, 244, 255), new Color(0, 249, 255), new Color(0, 255, 255),
        new Color(5, 255, 249), new Color(10, 255, 244), new Color(15, 255, 239),
        new Color(20, 255, 234), new Color(25, 255, 229), new Color(30, 255, 224),
        new Color(35, 255, 219), new Color(40, 255, 214), new Color(45, 255, 209),
        new Color(50, 255, 204), new Color(56, 255, 198), new Color(61, 255, 193),
        new Color(66, 255, 188), new Color(71, 255, 183), new Color(76, 255, 178),
        new Color(81, 255, 173), new Color(86, 255, 168), new Color(91, 255, 163),
        new Color(96, 255, 158), new Color(101, 255, 153), new Color(107, 255, 147),
        new Color(112, 255, 142), new Color(117, 255, 137), new Color(122, 255, 132),
        new Color(127, 255, 127), new Color(132, 255, 122), new Color(137, 255, 117),
        new Color(142, 255, 112), new Color(147, 255, 107), new Color(153, 255, 101),
        new Color(158, 255, 96), new Color(163, 255, 91), new Color(168, 255, 86),
        new Color(173, 255, 81), new Color(178, 255, 76), new Color(183, 255, 71),
        new Color(188, 255, 66), new Color(193, 255, 61), new Color(198, 255, 56),
        new Color(203, 255, 51), new Color(209, 255, 45), new Color(214, 255, 40),
        new Color(219, 255, 35), new Color(224, 255, 30), new Color(229, 255, 25),
        new Color(234, 255, 20), new Color(239, 255, 15), new Color(244, 255, 10),
        new Color(249, 255, 5), new Color(255, 255, 0), new Color(255, 249, 0),
        new Color(255, 244, 0), new Color(255, 239, 0), new Color(255, 234, 0),
        new Color(255, 229, 0), new Color(255, 224, 0), new Color(255, 219, 0),
        new Color(255, 214, 0), new Color(255, 209, 0), new Color(255, 203, 0),
        new Color(255, 198, 0), new Color(255, 193, 0), new Color(255, 188, 0),
        new Color(255, 183, 0), new Color(255, 178, 0), new Color(255, 173, 0),
        new Color(255, 168, 0), new Color(255, 163, 0), new Color(255, 158, 0),
        new Color(255, 153, 0), new Color(255, 147, 0), new Color(255, 142, 0),
        new Color(255, 137, 0), new Color(255, 132, 0), new Color(255, 127, 0),
        new Color(255, 122, 0), new Color(255, 117, 0), new Color(255, 112, 0),
        new Color(255, 107, 0), new Color(255, 101, 0), new Color(255, 96, 0),
        new Color(255, 91, 0), new Color(255, 86, 0), new Color(255, 81, 0), new Color(255, 76, 0),
        new Color(255, 71, 0), new Color(255, 66, 0), new Color(255, 61, 0), new Color(255, 56, 0),
        new Color(255, 51, 0), new Color(255, 45, 0), new Color(255, 40, 0), new Color(255, 35, 0),
        new Color(255, 30, 0), new Color(255, 25, 0), new Color(255, 20, 0), new Color(255, 15, 0),
        new Color(255, 10, 0), new Color(255, 5, 0), new Color(255, 0, 0), new Color(249, 0, 0),
        new Color(244, 0, 0), new Color(239, 0, 0), new Color(234, 0, 0), new Color(229, 0, 0),
        new Color(224, 0, 0), new Color(219, 0, 0), new Color(214, 0, 0), new Color(209, 0, 0),
        new Color(203, 0, 0), new Color(198, 0, 0), new Color(193, 0, 0), new Color(188, 0, 0),
        new Color(183, 0, 0), new Color(178, 0, 0), new Color(173, 0, 0), new Color(168, 0, 0),
        new Color(163, 0, 0), new Color(158, 0, 0), new Color(153, 0, 0), new Color(147, 0, 0),
        new Color(142, 0, 0), new Color(137, 0, 0), new Color(132, 0, 0), new Color(159, 0, 0),};

    paletteBytes = new byte[] {(byte) 255, (byte) 255, (byte) 255, (byte) 0, (byte) 0, (byte) 0,
        (byte) 255, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 255, (byte) 0, (byte) 105,
        (byte) 0, (byte) 125, (byte) 125, (byte) 125, (byte) 125, (byte) 125, (byte) 0, (byte) 0,
        (byte) 255, (byte) 255, (byte) 200, (byte) 0, (byte) 200, (byte) 0, (byte) 0, (byte) 159,
        (byte) 0, (byte) 0, (byte) 132, (byte) 0, (byte) 0, (byte) 137, (byte) 0, (byte) 0,
        (byte) 142, (byte) 0, (byte) 0, (byte) 147, (byte) 0, (byte) 0, (byte) 153, (byte) 0,
        (byte) 0, (byte) 158, (byte) 0, (byte) 0, (byte) 163, (byte) 0, (byte) 0, (byte) 168,
        (byte) 0, (byte) 0, (byte) 173, (byte) 0, (byte) 0, (byte) 178, (byte) 0, (byte) 0,
        (byte) 183, (byte) 0, (byte) 0, (byte) 188, (byte) 0, (byte) 0, (byte) 193, (byte) 0,
        (byte) 0, (byte) 198, (byte) 0, (byte) 0, (byte) 203, (byte) 0, (byte) 0, (byte) 209,
        (byte) 0, (byte) 0, (byte) 214, (byte) 0, (byte) 0, (byte) 219, (byte) 0, (byte) 0,
        (byte) 224, (byte) 0, (byte) 0, (byte) 229, (byte) 0, (byte) 0, (byte) 234, (byte) 0,
        (byte) 0, (byte) 239, (byte) 0, (byte) 0, (byte) 244, (byte) 0, (byte) 0, (byte) 249,
        (byte) 0, (byte) 0, (byte) 255, (byte) 0, (byte) 5, (byte) 255, (byte) 0, (byte) 10,
        (byte) 255, (byte) 0, (byte) 15, (byte) 255, (byte) 0, (byte) 20, (byte) 255, (byte) 0,
        (byte) 25, (byte) 255, (byte) 0, (byte) 30, (byte) 255, (byte) 0, (byte) 35, (byte) 255,
        (byte) 0, (byte) 40, (byte) 255, (byte) 0, (byte) 45, (byte) 255, (byte) 0, (byte) 50,
        (byte) 255, (byte) 0, (byte) 56, (byte) 255, (byte) 0, (byte) 61, (byte) 255, (byte) 0,
        (byte) 66, (byte) 255, (byte) 0, (byte) 71, (byte) 255, (byte) 0, (byte) 76, (byte) 255,
        (byte) 0, (byte) 81, (byte) 255, (byte) 0, (byte) 86, (byte) 255, (byte) 0, (byte) 91,
        (byte) 255, (byte) 0, (byte) 96, (byte) 255, (byte) 0, (byte) 102, (byte) 255, (byte) 0,
        (byte) 107, (byte) 255, (byte) 0, (byte) 112, (byte) 255, (byte) 0, (byte) 117, (byte) 255,
        (byte) 0, (byte) 122, (byte) 255, (byte) 0, (byte) 127, (byte) 255, (byte) 0, (byte) 132,
        (byte) 255, (byte) 0, (byte) 137, (byte) 255, (byte) 0, (byte) 142, (byte) 255, (byte) 0,
        (byte) 147, (byte) 255, (byte) 0, (byte) 153, (byte) 255, (byte) 0, (byte) 158, (byte) 255,
        (byte) 0, (byte) 163, (byte) 255, (byte) 0, (byte) 168, (byte) 255, (byte) 0, (byte) 173,
        (byte) 255, (byte) 0, (byte) 178, (byte) 255, (byte) 0, (byte) 183, (byte) 255, (byte) 0,
        (byte) 188, (byte) 255, (byte) 0, (byte) 193, (byte) 255, (byte) 0, (byte) 198, (byte) 255,
        (byte) 0, (byte) 204, (byte) 255, (byte) 0, (byte) 209, (byte) 255, (byte) 0, (byte) 214,
        (byte) 255, (byte) 0, (byte) 219, (byte) 255, (byte) 0, (byte) 224, (byte) 255, (byte) 0,
        (byte) 229, (byte) 255, (byte) 0, (byte) 234, (byte) 255, (byte) 0, (byte) 239, (byte) 255,
        (byte) 0, (byte) 244, (byte) 255, (byte) 0, (byte) 249, (byte) 255, (byte) 0, (byte) 255,
        (byte) 255, (byte) 5, (byte) 255, (byte) 249, (byte) 10, (byte) 255, (byte) 244, (byte) 15,
        (byte) 255, (byte) 239, (byte) 20, (byte) 255, (byte) 234, (byte) 25, (byte) 255,
        (byte) 229, (byte) 30, (byte) 255, (byte) 224, (byte) 35, (byte) 255, (byte) 219, (byte) 40,
        (byte) 255, (byte) 214, (byte) 45, (byte) 255, (byte) 209, (byte) 50, (byte) 255,
        (byte) 204, (byte) 56, (byte) 255, (byte) 198, (byte) 61, (byte) 255, (byte) 193, (byte) 66,
        (byte) 255, (byte) 188, (byte) 71, (byte) 255, (byte) 183, (byte) 76, (byte) 255,
        (byte) 178, (byte) 81, (byte) 255, (byte) 173, (byte) 86, (byte) 255, (byte) 168, (byte) 91,
        (byte) 255, (byte) 163, (byte) 96, (byte) 255, (byte) 158, (byte) 101, (byte) 255,
        (byte) 153, (byte) 107, (byte) 255, (byte) 147, (byte) 112, (byte) 255, (byte) 142,
        (byte) 117, (byte) 255, (byte) 137, (byte) 122, (byte) 255, (byte) 132, (byte) 127,
        (byte) 255, (byte) 127, (byte) 132, (byte) 255, (byte) 122, (byte) 137, (byte) 255,
        (byte) 117, (byte) 142, (byte) 255, (byte) 112, (byte) 147, (byte) 255, (byte) 107,
        (byte) 153, (byte) 255, (byte) 101, (byte) 158, (byte) 255, (byte) 96, (byte) 163,
        (byte) 255, (byte) 91, (byte) 168, (byte) 255, (byte) 86, (byte) 173, (byte) 255, (byte) 81,
        (byte) 178, (byte) 255, (byte) 76, (byte) 183, (byte) 255, (byte) 71, (byte) 188,
        (byte) 255, (byte) 66, (byte) 193, (byte) 255, (byte) 61, (byte) 198, (byte) 255, (byte) 56,
        (byte) 203, (byte) 255, (byte) 51, (byte) 209, (byte) 255, (byte) 45, (byte) 214,
        (byte) 255, (byte) 40, (byte) 219, (byte) 255, (byte) 35, (byte) 224, (byte) 255, (byte) 30,
        (byte) 229, (byte) 255, (byte) 25, (byte) 234, (byte) 255, (byte) 20, (byte) 239,
        (byte) 255, (byte) 15, (byte) 244, (byte) 255, (byte) 10, (byte) 249, (byte) 255, (byte) 5,
        (byte) 255, (byte) 255, (byte) 0, (byte) 255, (byte) 249, (byte) 0, (byte) 255, (byte) 244,
        (byte) 0, (byte) 255, (byte) 239, (byte) 0, (byte) 255, (byte) 234, (byte) 0, (byte) 255,
        (byte) 229, (byte) 0, (byte) 255, (byte) 224, (byte) 0, (byte) 255, (byte) 219, (byte) 0,
        (byte) 255, (byte) 214, (byte) 0, (byte) 255, (byte) 209, (byte) 0, (byte) 255, (byte) 203,
        (byte) 0, (byte) 255, (byte) 198, (byte) 0, (byte) 255, (byte) 193, (byte) 0, (byte) 255,
        (byte) 188, (byte) 0, (byte) 255, (byte) 183, (byte) 0, (byte) 255, (byte) 178, (byte) 0,
        (byte) 255, (byte) 173, (byte) 0, (byte) 255, (byte) 168, (byte) 0, (byte) 255, (byte) 163,
        (byte) 0, (byte) 255, (byte) 158, (byte) 0, (byte) 255, (byte) 153, (byte) 0, (byte) 255,
        (byte) 147, (byte) 0, (byte) 255, (byte) 142, (byte) 0, (byte) 255, (byte) 137, (byte) 0,
        (byte) 255, (byte) 132, (byte) 0, (byte) 255, (byte) 127, (byte) 0, (byte) 255, (byte) 122,
        (byte) 0, (byte) 255, (byte) 117, (byte) 0, (byte) 255, (byte) 112, (byte) 0, (byte) 255,
        (byte) 107, (byte) 0, (byte) 255, (byte) 101, (byte) 0, (byte) 255, (byte) 96, (byte) 0,
        (byte) 255, (byte) 91, (byte) 0, (byte) 255, (byte) 86, (byte) 0, (byte) 255, (byte) 81,
        (byte) 0, (byte) 255, (byte) 76, (byte) 0, (byte) 255, (byte) 71, (byte) 0, (byte) 255,
        (byte) 66, (byte) 0, (byte) 255, (byte) 61, (byte) 0, (byte) 255, (byte) 56, (byte) 0,
        (byte) 255, (byte) 51, (byte) 0, (byte) 255, (byte) 45, (byte) 0, (byte) 255, (byte) 40,
        (byte) 0, (byte) 255, (byte) 35, (byte) 0, (byte) 255, (byte) 30, (byte) 0, (byte) 255,
        (byte) 25, (byte) 0, (byte) 255, (byte) 20, (byte) 0, (byte) 255, (byte) 15, (byte) 0,
        (byte) 255, (byte) 10, (byte) 0, (byte) 255, (byte) 5, (byte) 0, (byte) 255, (byte) 0,
        (byte) 0, (byte) 249, (byte) 0, (byte) 0, (byte) 244, (byte) 0, (byte) 0, (byte) 239,
        (byte) 0, (byte) 0, (byte) 234, (byte) 0, (byte) 0, (byte) 229, (byte) 0, (byte) 0,
        (byte) 224, (byte) 0, (byte) 0, (byte) 219, (byte) 0, (byte) 0, (byte) 214, (byte) 0,
        (byte) 0, (byte) 209, (byte) 0, (byte) 0, (byte) 203, (byte) 0, (byte) 0, (byte) 198,
        (byte) 0, (byte) 0, (byte) 193, (byte) 0, (byte) 0, (byte) 188, (byte) 0, (byte) 0,
        (byte) 183, (byte) 0, (byte) 0, (byte) 178, (byte) 0, (byte) 0, (byte) 173, (byte) 0,
        (byte) 0, (byte) 168, (byte) 0, (byte) 0, (byte) 163, (byte) 0, (byte) 0, (byte) 158,
        (byte) 0, (byte) 0, (byte) 153, (byte) 0, (byte) 0, (byte) 147, (byte) 0, (byte) 0,
        (byte) 142, (byte) 0, (byte) 0, (byte) 137, (byte) 0, (byte) 0, (byte) 132, (byte) 0,
        (byte) 0, (byte) 159, (byte) 0, (byte) 0, (byte) 255, (byte) 101, (byte) 0, (byte) 255,
        (byte) 96, (byte) 0, (byte) 255, (byte) 91, (byte) 0, (byte) 255, (byte) 86, (byte) 0,
        (byte) 255, (byte) 81, (byte) 0, (byte) 255, (byte) 76, (byte) 0, (byte) 255, (byte) 71,
        (byte) 0, (byte) 255, (byte) 66, (byte) 0, (byte) 255, (byte) 61, (byte) 0, (byte) 255,
        (byte) 56, (byte) 0, (byte) 255, (byte) 51, (byte) 0, (byte) 255, (byte) 45, (byte) 0,
        (byte) 255, (byte) 40, (byte) 0, (byte) 255, (byte) 35, (byte) 0, (byte) 255, (byte) 30,
        (byte) 0, (byte) 255, (byte) 25, (byte) 0, (byte) 255, (byte) 20, (byte) 0, (byte) 255,
        (byte) 15, (byte) 0, (byte) 255, (byte) 10, (byte) 0, (byte) 255, (byte) 5, (byte) 0,
        (byte) 255, (byte) 0, (byte) 0, (byte) 249, (byte) 0, (byte) 0, (byte) 244, (byte) 0,
        (byte) 0, (byte) 239, (byte) 0, (byte) 0, (byte) 234, (byte) 0, (byte) 0, (byte) 229,
        (byte) 0, (byte) 0, (byte) 224, (byte) 0, (byte) 0, (byte) 219, (byte) 0, (byte) 0,
        (byte) 214, (byte) 0, (byte) 0, (byte) 209, (byte) 0, (byte) 0, (byte) 203, (byte) 0,
        (byte) 0, (byte) 198, (byte) 0, (byte) 0, (byte) 193, (byte) 0, (byte) 0, (byte) 188,
        (byte) 0, (byte) 0, (byte) 183, (byte) 0, (byte) 0, (byte) 178, (byte) 0, (byte) 0,
        (byte) 173, (byte) 0, (byte) 0, (byte) 168, (byte) 0, (byte) 0, (byte) 163, (byte) 0,
        (byte) 0, (byte) 158, (byte) 0, (byte) 0, (byte) 153, (byte) 0, (byte) 0, (byte) 147,
        (byte) 0, (byte) 0, (byte) 142, (byte) 0, (byte) 0, (byte) 137, (byte) 0, (byte) 0,
        (byte) 132, (byte) 0, (byte) 0, (byte) 159, (byte) 0, (byte) 0};

    palette = new IndexColorModel(8, 255, paletteBytes, 0, false);
  }
}
