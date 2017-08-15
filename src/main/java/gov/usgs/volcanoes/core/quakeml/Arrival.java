/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import gov.usgs.volcanoes.core.quakeml.Pick.Onset;
import gov.usgs.volcanoes.core.quakeml.Pick.Polarity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Comparator;
import java.util.Map;

/**
 * Holder for QuakeML arrival.
 *
 * @author Tom Parker
 *
 */
public class Arrival {
  private static final Logger LOGGER = LoggerFactory.getLogger(Arrival.class);

  /**
   * Sort arrival by distance from origin.
   *
   * @return compare value
   */
  public static Comparator<Arrival> distanceComparator() {
    return new Comparator<Arrival>() {
      @Override
      public int compare(final Arrival e1, final Arrival e2) {
        int result = Double.compare(e1.distance, e2.distance);
        if (result == 0) {
          return e1.phase.compareTo(e2.phase);
        } else {
          return result;
        }
      }
    };
  }


  public String publicId;
  private Pick pick;
  private String phase;
  private double azimuth = Double.NaN;
  private double distance = Double.NaN;
  private double takeoffAngle = Double.NaN;
  private double timeResidual = Double.NaN;
  private double timeWeight = Double.NaN;

  /**
   * Constructor for newly created Arrival.
   * 
   * @param publicId resource identifier of the arrival
   * @param pick associated pick
   * @param phase phase identification 
   */
  public Arrival(String publicId, Pick pick, String phase) {
    this.publicId = publicId;
    this.pick = pick;
    this.phase = phase;
  }

  /**
   * Constructor from XML.
   *
   * @param arrivalElement XML element representing arrival
   * @param picks picks associated with event
   */
  public Arrival(Element arrivalElement, Map<String, Pick> picks) {
    publicId = arrivalElement.getAttribute("publicID");
    LOGGER.debug("new arrival {}", publicId);

    // this.phase = arrivalElement.getAttribute("phase");
    pick = picks.get(arrivalElement.getElementsByTagName("pickID").item(0).getTextContent());
    phase = arrivalElement.getElementsByTagName("phase").item(0).getTextContent();

    NodeList azimuthElement = arrivalElement.getElementsByTagName("azimuth");
    if (azimuthElement.getLength() > 0) {
      azimuth = Double.parseDouble(azimuthElement.item(0).getTextContent());
    }

    NodeList distanceElement = arrivalElement.getElementsByTagName("distance");
    if (distanceElement.getLength() > 0) {
      distance = Double.parseDouble(distanceElement.item(0).getTextContent());
    }

    NodeList takeoffAngleElement = arrivalElement.getElementsByTagName("takeoffAngle");
    if (takeoffAngleElement.getLength() > 0) {
      takeoffAngle = Double.parseDouble(takeoffAngleElement.item(0).getTextContent());
    }

    NodeList timeResidualElement = arrivalElement.getElementsByTagName("timeResidual");
    if (timeResidualElement.getLength() > 0) {
      timeResidual = Double.parseDouble(timeResidualElement.item(0).getTextContent());
    }

    NodeList timeWeightElement = arrivalElement.getElementsByTagName("timeWeight");
    if (timeWeightElement.getLength() > 0) {
      timeWeight = Double.parseDouble(timeWeightElement.item(0).getTextContent());
    }
  }

  public String getPhase() {
    return phase;
  }

  public Pick getPick() {
    return pick;
  }

  public Double getTimeWeight() {
    return timeWeight;
  }

  /**
   * To XML Element.
   * @param doc xml document
   * @return xml element
   */
  public Element toElement(Document doc) {

    Element arrival = doc.createElement("arrival");
    arrival.setAttribute("publicID", publicId);

    Element pickElement = doc.createElement("pickID");
    pickElement.appendChild(doc.createTextNode(pick.publicId));
    arrival.appendChild(pickElement);

    Element phaseElement = doc.createElement("phase");
    phaseElement.appendChild(doc.createTextNode(phase));
    arrival.appendChild(phaseElement);

    if (!Double.isNaN(azimuth)) {
      Element element = doc.createElement("azimuth");
      element.appendChild(doc.createTextNode(Double.toString(azimuth)));
      arrival.appendChild(element);
    }

    if (!Double.isNaN(distance)) {
      Element element = doc.createElement("distance");
      element.appendChild(doc.createTextNode(Double.toString(distance)));
      arrival.appendChild(element);
    }

    if (!Double.isNaN(takeoffAngle)) {
      Element element = doc.createElement("takeoffAngle");
      element.appendChild(doc.createTextNode(Double.toString(takeoffAngle)));
      arrival.appendChild(element);
    }

    if (!Double.isNaN(timeResidual)) {
      Element element = doc.createElement("timeResidual");
      element.appendChild(doc.createTextNode(Double.toString(timeResidual)));
      arrival.appendChild(element);
    }

    if (!Double.isNaN(timeWeight)) {
      Element element = doc.createElement("timeWeight");
      element.appendChild(doc.createTextNode(Double.toString(timeWeight)));
      arrival.appendChild(element);
    }

    return arrival;
  }

  /**
   * Return a phase tag.
   *
   * @return phase tag
   */
  public String getTag() {
    final StringBuilder sb = new StringBuilder();

    sb.append(String.format("%.2f", timeWeight));

    final Onset onset = pick.getOnset();
    if (onset == Pick.Onset.EMERGENT) {
      sb.append("e");
    } else if (onset == Pick.Onset.IMPULSIVE) {
      sb.append("i");
    }

    sb.append(phase.charAt(0));

    final Polarity polarity = pick.getPolarity();
    if (polarity == Pick.Polarity.NEGATIVE) {
      sb.append("-");
    } else if (polarity == Pick.Polarity.POSITIVE) {
      sb.append("+");
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("PublicId: " + publicId + "\n");
    sb.append("Distance: " + distance + "°\n");
    sb.append("Phase: " + phase + "\n");
    sb.append("Pick: " + pick);
    return sb.toString();
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /*
   * public boolean equals(Object o) {
   * if (!(o instanceof Arrival)) {
   * return false;
   * }
   * Arrival other = (Arrival) o;
   * if (!other.publicId.equals(publicId)) {
   * return false;
   * }
   * if (!other.pick.publicId.equals(pick.publicId)) {
   * return false;
   * }
   * if (!other.phase.equals(phase)) {
   * return false;
   * }
   * return true;
   * }
   */

  public double getTimeResidual() {
    return timeResidual;
  }

  /**
   * Get epicentral distance.
   * @return the distance in degrees
   */
  public double getDistance() {
    return distance;
  }

  /**
   * Set epicentral distance.
   * @param distance the distance to set in degrees
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }

  /**
   * Set pick.
   * @param pick the pick to set
   */
  public void setPick(Pick pick) {
    this.pick = pick;
  }

  /**
   * Set phase identification.
   * @param phase the phase to set
   */
  public void setPhase(String phase) {
    this.phase = phase;
  }

  /**
   * Set the time residual between observed and expected arrival time.
   * @param timeResidual the timeResidual to set in seconds
   */
  public void setTimeResidual(double timeResidual) {
    this.timeResidual = timeResidual;
  }

  /**
   * Set the time weight of the arrival.
   * @param timeWeight the timeWeight to set
   */
  public void setTimeWeight(double timeWeight) {
    this.timeWeight = timeWeight;
  }

  /**
   * Get azimuth of station as seen from the epicenter.
   * @return the azimuth in degrees
   */
  public double getAzimuth() {
    return azimuth;
  }

  /**
   * Set azimuth of station as seen from the epicenter.
   * @param azimuth the azimuth to set in degrees
   */
  public void setAzimuth(double azimuth) {
    this.azimuth = azimuth;
  }

  /**
   * Get the angle of emerging ray at the source, 
   * measured against the downward normal direction.
   * @return the takeoffAngle in degrees
   */
  public double getTakeoffAngle() {
    return takeoffAngle;
  }

  /**
   * Set the angle of emerging ray at the source, 
   * measured against the downward normal direction.
   * @param takeoffAngle the takeoffAngle to set in degrees
   */
  public void setTakeoffAngle(double takeoffAngle) {
    this.takeoffAngle = takeoffAngle;
  }

}
