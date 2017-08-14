package gov.usgs.volcanoes.core.quakeml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * This class represents a physical quantity that can be expressed numerically as double.
 * Modeled after QuakeML RealQuanity element.
 * 
 * @author Diana Norgaard
 *
 */
public class RealQuantity {

  private double value;
  private double uncertainty = Double.NaN;

  /**
   * RealQuantity from value.
   * 
   * @param value double value
   */
  public RealQuantity(double value) {
    this.value = value;
  }

  /**
   * TimeQuantity from QuakeML.
   * 
   * @param element QuakeML real quantity element
   */
  public RealQuantity(Element element) {
    value = Double.valueOf(element.getElementsByTagName("value").item(0).getTextContent());

    NodeList uncertaintyElement = element.getElementsByTagName("uncertainty");
    if (uncertaintyElement.getLength() > 0) {
      uncertainty = Double.valueOf(uncertaintyElement.item(0).getTextContent());
    }
  }

  /**
   * To XML element.
   * 
   * @param doc xml document
   * @return xml element
   */
  public Element toElement(Document doc, String tagName) {
    Element element = doc.createElement(tagName);
    Element valueElement = doc.createElement("value");
    element.appendChild(valueElement);
    valueElement.appendChild(doc.createTextNode(Double.toString(value)));
    if (!Double.isNaN(uncertainty)) {
      Element uncertaintyElement = doc.createElement("uncertainty");
      element.appendChild(uncertaintyElement);
      uncertaintyElement.appendChild(doc.createTextNode(Double.toString(uncertainty)));
    }
    return element;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    String text = String.format("%.3f", value);
    if (!Double.isNaN(uncertainty)) {
      text += " (" + String.format("%.3f", uncertainty) + ")";
    }
    return text;
  }

  /**
   * Get value.
   * 
   * @return value
   */
  public double getValue() {
    return value;
  }

  /**
   * Set value.
   * 
   * @param value value
   */
  public void setValue(double value) {
    this.value = value;
  }

  /**
   * Get uncertainty.
   * 
   * @return seconds
   */
  public double getUncertainty() {
    return uncertainty;
  }

  /**
   * Set uncertainty.
   * 
   * @param uncertainty uncertainty
   */
  public void setUncertainty(double uncertainty) {
    this.uncertainty = uncertainty;
  }

}
