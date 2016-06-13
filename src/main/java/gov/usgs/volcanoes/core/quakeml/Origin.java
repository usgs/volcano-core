/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Holder for QuakeML origin.
 *
 * @author Tom Parker
 *
 */
public class Origin {
  public static enum EvaluationMode {
    AUTOMATIC, MANUAL;
  }

  public static enum EvaluationStatus {
    CONFIRMED, FINAL, PRELIMINARY, REJECTED, REVIEWED
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(Origin.class);

  private final Map<String, Arrival> arrivals;
  private double azimuthalGap;
  private final double depth;
  private EvaluationMode evaluationMode;
  private EvaluationStatus evaluationStatus;
  private final double latitude;
  private final double longitude;
  private double minimumDistance;
  private int phaseCount;
  public final String publicId;
  private double standardError;
  private long time;

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
    longitude =
        Double.parseDouble(lonElement.getElementsByTagName("value").item(0).getTextContent());

    final Element latElement = (Element) originElement.getElementsByTagName("latitude").item(0);
    latitude =
        Double.parseDouble(latElement.getElementsByTagName("value").item(0).getTextContent());

    final Element depthElement = (Element) originElement.getElementsByTagName("depth").item(0);
    depth = Double.parseDouble(depthElement.getElementsByTagName("value").item(0).getTextContent());

    final Element timeElement = (Element) originElement.getElementsByTagName("time").item(0);
    time = 0;
    time =
        QuakeMlUtils.parseTime(timeElement.getElementsByTagName("value").item(0).getTextContent());

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
      final Element errorElement =
          (Element) qualityElement.getElementsByTagName("standardError").item(0);
      if (errorElement != null) {
        standardError = Double.parseDouble(errorElement.getTextContent());
      } else {
        standardError = Double.NaN;
      }

      final Element gapElement =
          (Element) qualityElement.getElementsByTagName("azimuthalGap").item(0);
      if (gapElement != null) {
        LOGGER.debug("GAP: {}", gapElement.getTextContent());
        azimuthalGap = Double.parseDouble(gapElement.getTextContent());
      } else {
        azimuthalGap = Double.NaN;
      }

      final Element phaseCountElement =
          (Element) qualityElement.getElementsByTagName("usedPhaseCount").item(0);
      if (gapElement != null) {
        phaseCount = Integer.parseInt(phaseCountElement.getTextContent());
      } else {
        phaseCount = -1;
      }

      final Element distanceElement =
          (Element) qualityElement.getElementsByTagName("minimumDistance").item(0);
      if (distanceElement != null) {
        minimumDistance = Double.parseDouble(distanceElement.getTextContent());
      } else {
        minimumDistance = Double.NaN;
      }

    }

    parseArrivals(originElement.getElementsByTagName("arrival"), picks);
  }

  public Collection<Arrival> getArrivals() {
    return arrivals.values();
  }

  public double getAzimuthalGap() {
    return azimuthalGap;
  }

  public double getDepth() {
    return depth;
  }

  public EvaluationMode getEvaluationMode() {
    return evaluationMode;
  }

  public EvaluationStatus getEvaluationStatus() {
    return evaluationStatus;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getMinimumDistance() {
    return minimumDistance;
  }

  public int getPhaseCount() {
    return phaseCount;
  }

  public double getStandardError() {
    return standardError;
  }

  public long getTime() {
    return time;
  }

  private void parseArrivals(NodeList arrivalElements, Map<String, Pick> picks) {
    final int arrivalCount = arrivalElements.getLength();
    for (int idx = 0; idx < arrivalCount; idx++) {
      final Arrival arrival = new Arrival((Element) arrivalElements.item(idx), picks);
      arrivals.put(arrival.publicId, arrival);
    }
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();

    sb.append("PublicId: " + publicId + "\n");
    sb.append("Time: " + new Date(time) + "\n");
    sb.append("Gap: " + azimuthalGap + "°\n");
    sb.append("Depth: " + depth + "m\n");
    sb.append("Evaluation mode: " + evaluationMode + "\n");
    sb.append("Evalutaion status: " + evaluationStatus + "\n");
    sb.append("Location: " + latitude + "°, " + longitude + "° at " + depth + "m depth\n");
    sb.append("Minimum distance: " + minimumDistance + "°\n");
    sb.append("Error: " + standardError + "s\n");
    sb.append("Phase count: " + phaseCount + "\n");
    for (Arrival arrival : arrivals.values()) {
      sb.append("Arrival: " + arrival.toString() + "\n");
    }
    
    return sb.toString();
  }
}
