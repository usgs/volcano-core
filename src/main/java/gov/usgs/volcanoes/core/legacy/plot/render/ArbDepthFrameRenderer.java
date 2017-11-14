package gov.usgs.volcanoes.core.legacy.plot.render;

import gov.usgs.volcanoes.core.legacy.plot.transform.ArbDepthCalculator;

/**
 * A class used to render hypocenters by projecting them onto a line drawn 
 * between 2 arbitrary points on the earth
 * 
 * @author sid hellman
 *
 */

public class ArbDepthFrameRenderer extends InvertedFrameRenderer {

  ArbDepthCalculator arbDepthCalc = null;

  /**
   * Yield this ArbDepthCalculator
   * @return this ArbDepthCalculator
   */
  public ArbDepthCalculator getArbDepthCalc() {
    return arbDepthCalc;
  }

  /**
   * Set this ArbDepthCalculator
   * @param adCalc this ArbDepthCalculator
   */
  public void setArbDepthCalc(ArbDepthCalculator adCalc) {
    this.arbDepthCalc = adCalc;
  }

  /**
   * Yield the X pixel corresponding to the location (lat,lon)
   * @param lat
   * @param lon
   * @return X pixel for (lat,lon)
   */
  public double getXPixel(double lat, double lon) {

    double ret = 0.0;

    ret = arbDepthCalc.getScaledProjectedDistance(lat, lon);

    return ret;

  }

}
