/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Holder for QuakeML magnitude.
 *
 * @author Tom Parker
 *
 */
public class Magnitude {

  public final String publicId;
  private RealQuantity magnitude;
  private String type = "M";
  private int stationCount = -1;

  /**
   * Default constructor.
   * @param publicId public id
   * @param magnitude magnitude value 
   */
  public Magnitude(String publicId, double magnitude) {
    this.publicId = publicId;
    this.magnitude = new RealQuantity(magnitude);
  }

  /**
   * Constructor from XML.
   *
   * @param magnitudeElement XML element
   */
  public Magnitude(Element magnitudeElement) {
    publicId = magnitudeElement.getAttribute("publicID");

    final Element magElement = (Element) magnitudeElement.getElementsByTagName("mag").item(0);
    magnitude = new RealQuantity(magElement);

    type = magnitudeElement.getElementsByTagName("type").item(0).getTextContent();
  }

  /**
   * To XML element.
   * @param doc XML document
   * @return XML element
   */
  public Element toElement(Document doc) {
    Element element = doc.createElement("magnitude");
    element.setAttribute("publicID", publicId);
    element.appendChild(magnitude.toElement(doc, "mag"));

    Element typeElement = doc.createElement("type");
    element.appendChild(typeElement);
    typeElement.appendChild(doc.createTextNode(type));

    Element scElement = doc.createElement("stationCount");
    element.appendChild(scElement);
    scElement.appendChild(doc.createTextNode(Integer.toString(stationCount)));

    return element;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("PublicId: " + publicId + "\n");
    sb.append("Magnitude: " + magnitude.toString() + "\n");
    sb.append("Type: " + type + "\n");
    sb.append("Station count: " + stationCount + "\n");
    return sb.toString();
  }

  public RealQuantity getMagnitude() {
    return magnitude;
  }

  public void setMagnitude(RealQuantity magnitude) {
    this.magnitude = magnitude;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getStationCount() {
    return stationCount;
  }

  public void setStationCount(int stationCount) {
    this.stationCount = stationCount;
  }

}
