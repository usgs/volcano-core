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

/**
 * Holder for QuakeML origin quality.
 *
 * @author Diana Norgaard
 *
 */
public class OriginQuality {
  private static final Logger LOGGER = LoggerFactory.getLogger(OriginQuality.class);

  private int associatedPhaseCount = -1;
  private int usedPhaseCount = -1;
  private int associatedStationCount = -1;
  private int usedStationCount = -1;
  private int depthPhaseCount = -1;
  private double standardError = Double.NaN;
  private double azimuthalGap = Double.NaN;
  private double secondaryAzimuthalGap = Double.NaN;
  private String groundTruthLevel = null;
  private double minimumDistance = Double.NaN;
  private double maximumDistance = Double.NaN;
  private double medianDistance = Double.NaN;

  /**
   * Constructor for newly created origin quality.
   */
  public OriginQuality() {}

  /**
   * Constructor from XML.
   *
   * @param qualityElement XML element
   */
  public OriginQuality(Element qualityElement) {

    final Element associatedPhaseCountElement =
        (Element) qualityElement.getElementsByTagName("associatedPhaseCount").item(0);
    if (associatedPhaseCountElement != null) {
      associatedPhaseCount = Integer.parseInt(associatedPhaseCountElement.getTextContent());
    }

    final Element usedPhaseCountElement =
        (Element) qualityElement.getElementsByTagName("usedPhaseCount").item(0);
    if (usedPhaseCountElement != null) {
      usedPhaseCount = Integer.parseInt(usedPhaseCountElement.getTextContent());
    }

    final Element associatedStationCountElement =
        (Element) qualityElement.getElementsByTagName("associatedStationCount").item(0);
    if (associatedStationCountElement != null) {
      associatedStationCount = Integer.parseInt(associatedStationCountElement.getTextContent());
    }

    final Element usedStationCountElement =
        (Element) qualityElement.getElementsByTagName("usedStationCount").item(0);
    if (usedStationCountElement != null) {
      usedStationCount = Integer.parseInt(usedStationCountElement.getTextContent());
    }

    final Element depthPhaseCountElement =
        (Element) qualityElement.getElementsByTagName("depthPhaseCount").item(0);
    if (depthPhaseCountElement != null) {
      depthPhaseCount = Integer.parseInt(depthPhaseCountElement.getTextContent());
    }

    final Element errorElement =
        (Element) qualityElement.getElementsByTagName("standardError").item(0);
    if (errorElement != null) {
      standardError = Double.parseDouble(errorElement.getTextContent());
    }

    final Element azimuthalGapElement =
        (Element) qualityElement.getElementsByTagName("azimuthalGap").item(0);
    if (azimuthalGapElement != null) {
      LOGGER.debug("GAP: {}", azimuthalGapElement.getTextContent());
      azimuthalGap = Double.parseDouble(azimuthalGapElement.getTextContent());
    }

    final Element secondaryAzimuthalGapElement =
        (Element) qualityElement.getElementsByTagName("secondaryAzimuthalGap").item(0);
    if (secondaryAzimuthalGapElement != null) {
      LOGGER.debug("GAP: {}", secondaryAzimuthalGapElement.getTextContent());
      secondaryAzimuthalGap = Double.parseDouble(secondaryAzimuthalGapElement.getTextContent());
    }

    final Element groundTruthLevelElement =
        (Element) qualityElement.getElementsByTagName("groundTruthLevel").item(0);
    if (groundTruthLevelElement != null) {
      LOGGER.debug("GAP: {}", groundTruthLevelElement.getTextContent());
      groundTruthLevel = groundTruthLevelElement.getTextContent();
    }

    final Element minimumDistanceElement =
        (Element) qualityElement.getElementsByTagName("minimumDistance").item(0);
    if (minimumDistanceElement != null) {
      LOGGER.debug("GAP: {}", minimumDistanceElement.getTextContent());
      minimumDistance = Double.parseDouble(minimumDistanceElement.getTextContent());
    }

    final Element maximumDistanceElement =
        (Element) qualityElement.getElementsByTagName("maximumDistance").item(0);
    if (maximumDistanceElement != null) {
      LOGGER.debug("GAP: {}", maximumDistanceElement.getTextContent());
      maximumDistance = Double.parseDouble(maximumDistanceElement.getTextContent());
    }

    final Element medianDistanceElement =
        (Element) qualityElement.getElementsByTagName("medianDistance").item(0);
    if (medianDistanceElement != null) {
      LOGGER.debug("GAP: {}", medianDistanceElement.getTextContent());
      medianDistance = Double.parseDouble(medianDistanceElement.getTextContent());
    }
  }

  /**
   * To XML Element.
   * @param doc xml document
   * @return xml element
   */
  public Element toElement(Document doc) {
    Element quality = doc.createElement("quality");
    if (associatedPhaseCount > -1) {
      Element element = doc.createElement("associatedPhaseCount");
      element.appendChild(doc.createTextNode(Integer.toString(associatedPhaseCount)));
      quality.appendChild(element);
    }
    if (usedPhaseCount > -1) {
      Element element = doc.createElement("usedPhaseCount");
      element.appendChild(doc.createTextNode(Integer.toString(usedPhaseCount)));
      quality.appendChild(element);
    }
    if (associatedStationCount > -1) {
      Element element = doc.createElement("associatedStationCount");
      element.appendChild(doc.createTextNode(Integer.toString(associatedStationCount)));
      quality.appendChild(element);
    }
    if (usedStationCount > -1) {
      Element element = doc.createElement("usedStationCount");
      element.appendChild(doc.createTextNode(Integer.toString(usedStationCount)));
      quality.appendChild(element);
    }
    if (depthPhaseCount > -1) {
      Element element = doc.createElement("depthPhaseCount");
      element.appendChild(doc.createTextNode(Integer.toString(depthPhaseCount)));
      quality.appendChild(element);
    }
    if (!Double.isNaN(standardError)) {
      Element element = doc.createElement("standardError");
      element.appendChild(doc.createTextNode(Double.toString(standardError)));
      quality.appendChild(element);
    }
    if (!Double.isNaN(azimuthalGap)) {
      Element element = doc.createElement("azimuthalGap");
      element.appendChild(doc.createTextNode(Double.toString(azimuthalGap)));
      quality.appendChild(element);
    }
    if (!Double.isNaN(secondaryAzimuthalGap)) {
      Element element = doc.createElement("secondaryAzimuthalGap");
      element.appendChild(doc.createTextNode(Double.toString(secondaryAzimuthalGap)));
      quality.appendChild(element);
    }
    if (groundTruthLevel != null) {
      Element element = doc.createElement("groundTruthLevel");
      element.appendChild(doc.createTextNode(groundTruthLevel));
      quality.appendChild(element);
    }
    if (!Double.isNaN(minimumDistance)) {
      Element element = doc.createElement("minimumDistance");
      element.appendChild(doc.createTextNode(Double.toString(minimumDistance)));
      quality.appendChild(element);
    }
    if (!Double.isNaN(maximumDistance)) {
      Element element = doc.createElement("maximumDistance");
      element.appendChild(doc.createTextNode(Double.toString(maximumDistance)));
      quality.appendChild(element);
    }
    if (!Double.isNaN(medianDistance)) {
      Element element = doc.createElement("medianDistance");
      element.appendChild(doc.createTextNode(Double.toString(medianDistance)));
      quality.appendChild(element);
    }
    return quality;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    if (associatedPhaseCount > -1) {
      sb.append("Associated Phase Count: " + associatedPhaseCount + "\n");
    }
    if (usedPhaseCount > -1) {
      sb.append("Used Phase Count: " + usedPhaseCount + "\n");
    }
    if (associatedStationCount > -1) {
      sb.append("Associated Station Count: " + associatedStationCount + "\n");
    }
    if (usedStationCount > -1) {
      sb.append("Used Station Count: " + usedStationCount + "\n");
    }
    if (depthPhaseCount > -1) {
      sb.append("Depth Phase Count: " + depthPhaseCount + "\n");
    }
    if (!Double.isNaN(standardError)) {
      sb.append("Standard Error: " + String.format("%.5f", standardError) + "\n");
    }
    if (!Double.isNaN(azimuthalGap)) {
      sb.append("Azimuthal Gap: " + String.format("%.5f", azimuthalGap) + "\n");
    }
    if (!Double.isNaN(secondaryAzimuthalGap)) {
      sb.append("Secondary Azimuthal Gap: " + String.format("%.5f", secondaryAzimuthalGap) + "\n");
    }
    if (groundTruthLevel != null) {
      sb.append(groundTruthLevel + "\n");
    }
    if (!Double.isNaN(minimumDistance)) {
      sb.append("Minimum Distance: " + String.format("%.5f", minimumDistance) + "\n");
    }
    if (!Double.isNaN(maximumDistance)) {
      sb.append("Maximum Distance: " + String.format("%.5f", maximumDistance) + "\n");
    }
    if (!Double.isNaN(medianDistance)) {
      sb.append("Median Distance: " + String.format("%.5f", medianDistance) + "\n");
    }
    return sb.toString();
  }

  public int getAssociatedPhaseCount() {
    return associatedPhaseCount;
  }

  public void setAssociatedPhaseCount(int associatedPhaseCount) {
    this.associatedPhaseCount = associatedPhaseCount;
  }

  public int getUsedPhaseCount() {
    return usedPhaseCount;
  }

  public void setUsedPhaseCount(int usedPhaseCount) {
    this.usedPhaseCount = usedPhaseCount;
  }

  public int getAssociatedStationCount() {
    return associatedStationCount;
  }

  public void setAssociatedStationCount(int associatedStationCount) {
    this.associatedStationCount = associatedStationCount;
  }

  public int getUsedStationCount() {
    return usedStationCount;
  }

  public void setUsedStationCount(int usedStationCount) {
    this.usedStationCount = usedStationCount;
  }

  public int getDepthPhaseCount() {
    return depthPhaseCount;
  }

  public void setDepthPhaseCount(int depthPhaseCount) {
    this.depthPhaseCount = depthPhaseCount;
  }

  public double getStandardError() {
    return standardError;
  }

  public void setStandardError(double standardError) {
    this.standardError = standardError;
  }

  public double getAzimuthalGap() {
    return azimuthalGap;
  }

  public void setAzimuthalGap(double azimuthalGap) {
    this.azimuthalGap = azimuthalGap;
  }

  public double getSecondaryAzimuthalGap() {
    return secondaryAzimuthalGap;
  }

  public void setSecondaryAzimuthalGap(double secondaryAzimuthalGap) {
    this.secondaryAzimuthalGap = secondaryAzimuthalGap;
  }

  public String getGroundTruthLevel() {
    return groundTruthLevel;
  }

  public void setGroundTruthLevel(String groundTruthLevel) {
    this.groundTruthLevel = groundTruthLevel;
  }

  public double getMinimumDistance() {
    return minimumDistance;
  }

  public void setMinimumDistance(double minimumDistance) {
    this.minimumDistance = minimumDistance;
  }

  public double getMaximumDistance() {
    return maximumDistance;
  }

  public void setMaximumDistance(double maximumDistance) {
    this.maximumDistance = maximumDistance;
  }

  public double getMedianDistance() {
    return medianDistance;
  }

  public void setMedianDistance(double medianDistance) {
    this.medianDistance = medianDistance;
  }

}

