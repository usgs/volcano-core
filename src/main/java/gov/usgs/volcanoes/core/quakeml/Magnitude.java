/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import org.w3c.dom.Element;

/**
 * Holder for QuakeML magnitude.
 *
 * @author Tom Parker
 *
 */
public class Magnitude {

  private final double mag;

  public final String publicId;
  private final String type;
  private String uncertainty;

  /**
   * Constructor.
   *
   * @param magnitudeElement XML element
   */
  public Magnitude(Element magnitudeElement) {
    publicId = magnitudeElement.getAttribute("publicID");
    type = magnitudeElement.getElementsByTagName("type").item(0).getTextContent();

    final Element magElement = (Element) magnitudeElement.getElementsByTagName("mag").item(0);
    mag = Double.parseDouble(magElement.getElementsByTagName("value").item(0).getTextContent());

    final Element uncertaintyElement =
        (Element) magElement.getElementsByTagName("uncertainty").item(0);
    if (uncertaintyElement != null) {
      uncertainty = '±' + uncertaintyElement.getTextContent();
    }
  }

  public double getMag() {
    return mag;
  }

  public String getType() {
    return type;
  }

  public String getUncertainty() {
    return uncertainty;
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("magnitude: " + mag + " " + type + " " + uncertainty + "\n");
    sb.append("publicId: " + publicId + "\n");
    
    return sb.toString();
  }
}
