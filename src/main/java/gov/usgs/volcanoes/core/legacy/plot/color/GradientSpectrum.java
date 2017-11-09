package gov.usgs.volcanoes.core.legacy.plot.color;

import java.awt.Color;

/**
 * <p>Spectra with gradient color filling.</p>
 * 
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 */
public class GradientSpectrum extends Spectrum {
  /**
   * Constructor. Initial parameters is RGB color components for starting and ending color
   * @param size count of colors in the spectra
   */
  public GradientSpectrum(int size, double r1, double g1, double b1, double r2, double g2,
      double b2) {
    colors = new Color[size];
    double dr = (r2 - r1) / (double) size;
    double dg = (g2 - g1) / (double) size;
    double db = (b2 - b1) / (double) size;
    for (int i = 0; i < size; i++)
      colors[i] = new Color((float) (r1 + dr * i), (float) (g1 + dg * i), (float) (b1 + db * i));

    paletteBytes = null;
    palette = null;
  }
}
