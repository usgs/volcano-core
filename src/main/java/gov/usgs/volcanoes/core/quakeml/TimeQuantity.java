package gov.usgs.volcanoes.core.quakeml;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Date;

/**
 * This class represents a point in time, with optional symmetric or asymmetric uncertainties given
 * in seconds.
 * <p>
 * Modeled after QuakeML TimeQuanity element.
 * 
 * @author Diana Norgaard
 *
 */
public class TimeQuantity {

  private Date value; // UTC
  private double uncertainty;

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

  public Date getValue() {
    return value;
  }

  public void setValue(Date value) {
    this.value = value;
  }

  public double getUncertainty() {
    return uncertainty;
  }

  public void setUncertainty(double uncertainty) {
    this.uncertainty = uncertainty;
  }

}
