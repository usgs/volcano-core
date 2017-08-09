/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Holder for QuakeML station magnitude.
 *
 */
public class StationMagnitude {

  public final String publicId;
  private String originId;
  private RealQuantity magnitude;
  private String type = "M";

  /**
   * Default constructor.
   * @param publicId public id
   * @param magnitude magnitude value 
   */
  public StationMagnitude(String publicId, String originId, double magnitude) {
    this.publicId = publicId;
    this.originId = originId;
    this.magnitude = new RealQuantity(magnitude);
  }

  /**
   * Constructor from XML.
   *
   * @param magnitudeElement XML element
   */
  public StationMagnitude(Element magnitudeElement) {
    publicId = magnitudeElement.getAttribute("publicID");

    originId = magnitudeElement.getElementsByTagName("originID").item(0).getTextContent();

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
    Element element = doc.createElement("stationMagnitude");
    element.setAttribute("publicID", publicId);
    Element originElement = doc.createElement("originID");
    element.appendChild(originElement);
    originElement.appendChild(doc.createTextNode(originId));

    element.appendChild(magnitude.toElement(doc, "mag"));

    Element typeElement = doc.createElement("type");
    element.appendChild(typeElement);
    typeElement.appendChild(doc.createTextNode(type));

    return element;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("PublicId: " + publicId + "\n");
    sb.append("Origin: " + originId + "\n");
    sb.append("Magnitude: " + magnitude.toString() + "\n");
    sb.append("Type: " + type + "\n");
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

}
