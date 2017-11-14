package gov.usgs.volcanoes.core.quakeml;

import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class represents a point in time, with optional symmetric or asymmetric uncertainties given
 * in seconds. Modeled after QuakeML TimeQuanity element.
 * 
 * @author Diana Norgaard
 *
 */
public class TimeQuantity {

  private Date value; // UTC
  private double uncertainty = Double.NaN; // in seconds

  /**
   * TimeQuantity from date.
   * 
   * @param time date object
   */
  public TimeQuantity(Date time) {
    this.value = time;
  }

  /**
   * TimeQuantity from time.
   * 
   * @param time millis
   */
  public TimeQuantity(long time) {
    this.value = new Date(time);
  }

  /**
   * TimeQuantity from QuakeML.
   * 
   * @param timeElement QuakeML time element
   */
  public TimeQuantity(Element timeElement) {
    value =
        QuakeMlUtils.parseDate(timeElement.getElementsByTagName("value").item(0).getTextContent());

    NodeList uncertaintyElement = timeElement.getElementsByTagName("uncertainty");
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
  public Element toElement(Document doc) {
    Element timeElement = doc.createElement("time");
    Element valueElement = doc.createElement("value");
    timeElement.appendChild(valueElement);
    valueElement.appendChild(doc.createTextNode(QuakeMlUtils.formatDate(value.getTime())));
    if (!Double.isNaN(uncertainty)) {
      Element uncertaintyElement = doc.createElement("uncertainty");
      timeElement.appendChild(uncertaintyElement);
      uncertaintyElement.appendChild(doc.createTextNode(Double.toString(uncertainty)));
    }
    return timeElement;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    String text = value.toString();
    if (!Double.isNaN(uncertainty)) {
      text += " (" + uncertainty + ")";
    }
    return text;
  }

  /**
   * Get value.
   * 
   * @return date
   */
  public Date getValue() {
    return value;
  }

  /**
   * Set value.
   * 
   * @param value date
   */
  public void setValue(Date value) {
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
   * @param uncertainty seconds
   */
  public void setUncertainty(double uncertainty) {
    this.uncertainty = uncertainty;
  }

}
