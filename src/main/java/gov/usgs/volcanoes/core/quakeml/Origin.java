/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Holder for QuakeML origin.
 *
 * @author Tom Parker
 *
 */
public class Origin {
  private static final Logger LOGGER = LoggerFactory.getLogger(Origin.class);

  public String publicId;
  private TimeQuantity time;
  private RealQuantity latitude;
  private RealQuantity longitude;
  private RealQuantity depth;
  private OriginQuality quality;
  private EvaluationMode evaluationMode;
  private EvaluationStatus evaluationStatus;
  private Map<String, Arrival> arrivals = new HashMap<String, Arrival>();

  /**
   * Constructor for newly created origin.
   * @param publicId resource identifier of origin
   * @param time focal time
   * @param longitude hypocenter longitude (WGS84)
   * @param latitude hypocenter latitude (WGS84)
   */
  public Origin(String publicId, long time, double longitude, double latitude) {
    this.publicId = publicId;
    this.time = new TimeQuantity(time);
    this.longitude = new RealQuantity(longitude);
    this.latitude = new RealQuantity(latitude);
  }

  /**
   * Constructor.
   *
   * @param originElement XML element
   * @param picks associated picks
   */
  public Origin(Element originElement, Map<String, Pick> picks) {
    publicId = originElement.getAttribute("publicID");
    arrivals = new HashMap<String, Arrival>();

    LOGGER.debug("new origin {}", publicId);

    final Element lonElement = (Element) originElement.getElementsByTagName("longitude").item(0);
    longitude = new RealQuantity(lonElement);

    final Element latElement = (Element) originElement.getElementsByTagName("latitude").item(0);
    latitude = new RealQuantity(latElement);

    final Element depthElement = (Element) originElement.getElementsByTagName("depth").item(0);
    depth = new RealQuantity(depthElement);

    final Element timeElement = (Element) originElement.getElementsByTagName("time").item(0);
    time = new TimeQuantity(timeElement);

    Element evaluationElement =
        (Element) originElement.getElementsByTagName("evaluationStatus").item(0);
    if (evaluationElement != null) {
      evaluationStatus = EvaluationStatus.valueOf(evaluationElement.getTextContent().toUpperCase());
    }

    evaluationElement = (Element) originElement.getElementsByTagName("evaluationMode").item(0);
    if (evaluationElement != null) {
      evaluationMode = EvaluationMode.valueOf(evaluationElement.getTextContent().toUpperCase());
    }

    final Element qualityElement = (Element) originElement.getElementsByTagName("quality").item(0);
    if (qualityElement != null) {
      quality = new OriginQuality(qualityElement);
    }

    parseArrivals(originElement.getElementsByTagName("arrival"), picks);
  }

  public Collection<Arrival> getArrivals() {
    return arrivals.values();
  }

  public double getDepth() {
    return depth.getValue();
  }

  public EvaluationMode getEvaluationMode() {
    return evaluationMode;
  }

  public EvaluationStatus getEvaluationStatus() {
    return evaluationStatus;
  }

  public double getLatitude() {
    return latitude.getValue();
  }

  public double getLongitude() {
    return longitude.getValue();
  }

  public long getTime() {
    return time.getValue().getTime();
  }

  private void parseArrivals(NodeList arrivalElements, Map<String, Pick> picks) {
    final int arrivalCount = arrivalElements.getLength();
    for (int idx = 0; idx < arrivalCount; idx++) {
      final Arrival arrival = new Arrival((Element) arrivalElements.item(idx), picks);
      arrivals.put(arrival.publicId, arrival);
    }
  }

  /**
   * To XML Element.
   * @param doc xml document
   * @return xml element
   */
  public Element toElement(Document doc) {
    Element origin = doc.createElement("origin");
    origin.setAttribute("publicID", publicId);
    origin.appendChild(time.toElement(doc));
    origin.appendChild(longitude.toElement(doc, "longitude"));
    origin.appendChild(latitude.toElement(doc, "latitude"));
    if (depth != null) {
      origin.appendChild(depth.toElement(doc, "depth"));
    }
    if (quality != null) {
      origin.appendChild(quality.toElement(doc));
    }
    for (Arrival arrival : arrivals.values()) {
      origin.appendChild(arrival.toElement(doc));
    }
    if (evaluationMode != null) {
      Element evalModeElement = doc.createElement("evaluationMode");
      evalModeElement.appendChild(doc.createTextNode(evaluationMode.toString().toLowerCase()));
      origin.appendChild(evalModeElement);
    }
    if (evaluationStatus != null) {
      Element evalStatusElement = doc.createElement("evaluationStatus");
      evalStatusElement.appendChild(doc.createTextNode(evaluationStatus.toString().toLowerCase()));
      origin.appendChild(evalStatusElement);
    }
    return origin;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("PublicId: " + publicId + "\n");
    sb.append("Time: " + time.toString() + "\n");
    sb.append("Location: " + latitude + "°, " + longitude + "°\n");
    sb.append("Depth: " + depth + "m\n");
    sb.append(quality);
    return sb.toString();
  }

  public String getPublicId() {
    return publicId;
  }

  public void setPublicId(String publicId) {
    this.publicId = publicId;
  }

  public void setArrivals(Map<String, Arrival> arrivals) {
    this.arrivals = arrivals;
  }

  public void setEvaluationMode(EvaluationMode evaluationMode) {
    this.evaluationMode = evaluationMode;
  }

  public void setEvaluationStatus(EvaluationStatus evaluationStatus) {
    this.evaluationStatus = evaluationStatus;
  }

  public void setDepth(double depth) {
    this.depth = new RealQuantity(depth);
  }

  public void setDepth(RealQuantity depth) {
    this.depth = depth;
  }

  public void setLatitude(double latitude) {
    this.latitude = new RealQuantity(latitude);
  }

  public void setLatitude(RealQuantity latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = new RealQuantity(longitude);
  }

  public void setLongitude(RealQuantity longitude) {
    this.longitude = longitude;
  }

  public void setTime(long time) {
    this.time = new TimeQuantity(time);
  }

  public void setTime(TimeQuantity time) {
    this.time = time;
  }

  public OriginQuality getQuality() {
    return quality;
  }

  public void setQuality(OriginQuality quality) {
    this.quality = quality;
  }
}
