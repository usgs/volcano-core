/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import gov.usgs.volcanoes.core.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holder for QuakeML Event. Not all QuakeML elements have been implemented.
 *
 * @author Tom Parker
 *
 */
public class Event {
  private static final Logger LOGGER = LoggerFactory.getLogger(Event.class);

  private final Map<String, Magnitude> magnitudes;
  private final Map<String, Origin> origins;
  private final Map<String, Pick> picks;

  public final String publicId;
  private Magnitude preferredMagnitude;
  private Origin preferredOrigin;
  private EventType type;
  private EventTypeCertainty typeCertainty;
  private String description;

  private String eventSource;
  private String evid;

  private final List<EventObserver> observers;


  /**
   * Constructor.
   *
   * @param event template event
   */
  public Event(Element event) {
    this(event.getAttribute("publicID"));

    updateEvent(event);
  }

  /**
   * Constructor.
   *
   * @param publicId event id
   */
  public Event(String publicId) {
    this.publicId = publicId;
    LOGGER.debug("New event ({}}", publicId);

    origins = new HashMap<String, Origin>();
    magnitudes = new HashMap<String, Magnitude>();
    picks = new HashMap<String, Pick>();
    observers = new ArrayList<EventObserver>();
  }

  public void addObserver(EventObserver observer) {
    observers.add(observer);
  }

  public String getDataid() {
    return null;
  }

  public String getDescription() {
    return description;
  }

  public String getEventSource() {
    return eventSource;
  }


  public String getEvid() {
    return evid;
  }

  public Magnitude getPerferredMagnitude() {
    return preferredMagnitude;
  }

  public Origin getPreferredOrigin() {

    return preferredOrigin;
  }

  public String getTypeDescription() {
    return type.toString();
  }

  public String getTypeCertaintyDescription() {
    return typeCertainty.toString();
  }

  private void notifyObservers() {
    for (final EventObserver observer : observers) {
      observer.eventUpdated();
    }
  }

  private void parseMagnitudes(NodeList magnitudeElements) {
    magnitudes.clear();
    final int magnitudeCount = magnitudeElements.getLength();
    for (int idx = 0; idx < magnitudeCount; idx++) {
      Magnitude magnitude = new Magnitude((Element) magnitudeElements.item(idx));
      LOGGER.debug("Adding mag {} {}", idx, magnitude.publicId);
      magnitudes.put(magnitude.publicId, magnitude);
    }
  }

  private void parseOrigins(NodeList originElements) {
    origins.clear();
    final int originCount = originElements.getLength();
    for (int idx = 0; idx < originCount; idx++) {
      final Origin origin = new Origin((Element) originElements.item(idx), picks);
      origins.put(origin.publicId, origin);
    }
  }

  private void parsePicks(NodeList pickElements) {
    picks.clear();
    final int pickCount = pickElements.getLength();
    for (int idx = 0; idx < pickCount; idx++) {
      final Pick pick = new Pick((Element) pickElements.item(idx));
      picks.put(pick.publicId, pick);
    }
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Set event values using arg as template.
   *
   * @param event template event
   */
  public void updateEvent(Element event) {

    // order matters.
    parsePicks(event.getElementsByTagName("pick"));

    parseOrigins(event.getElementsByTagName("origin"));
    preferredOrigin = origins.get(event.getAttribute("preferredOriginID"));
    if (preferredOrigin == null && origins.size() > 0) {
      preferredOrigin = (Origin) origins.values().toArray()[0];
    }

    parseMagnitudes(event.getElementsByTagName("magnitude"));
    preferredMagnitude = magnitudes.get(event.getAttribute("preferredMagnitudeID"));
    if (preferredMagnitude == null && magnitudes.size() > 0) {
      preferredMagnitude = (Magnitude) magnitudes.values().toArray()[0];
    }

    eventSource =
        StringUtils.stringToString(event.getAttribute("catalog:eventsource"), eventSource);
    evid = StringUtils.stringToString(event.getAttribute("catalog:eventid"), evid);

    final Element descriptionElement = (Element) event.getElementsByTagName("description").item(0);
    if (descriptionElement != null) {
      description = StringUtils.stringToString(
          descriptionElement.getElementsByTagName("text").item(0).getTextContent(), description);
    }

    // Element typeElement = (Element) event.getElementsByTagName("type").item(0);
    final NodeList childList = event.getChildNodes();
    int idx = 0;
    while ((type == null || typeCertainty == null) && idx < childList.getLength()) {
      final Node node = childList.item(idx);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        final Element element = (Element) node;
        if ("type".equals(element.getTagName())) {
          LOGGER.debug("Looking for type {}", element.getTextContent());
          type = EventType.parse(element.getTextContent());
        } else if ("typeCertanty".equals(element.getTagName())) {
          LOGGER.debug("Looking for typeCertanty {}", element.getTextContent());
          typeCertainty = EventTypeCertainty.valueOf(element.getTextContent().toUpperCase());
        }
      }
      idx++;
    }
    notifyObservers();
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();

    sb.append("Public ID: " + publicId + "\n");
    sb.append("Event type: " + type + "(" + typeCertainty + ")\n");
    sb.append("Description: " + description + "\n");
    sb.append("Source: " + eventSource + "\n");
    sb.append("Evid: " + evid + "\n");
    sb.append("Preferred magnitude of " + magnitudes.size() + ":\n");
    sb.append("\t" + preferredMagnitude + "\n");
    sb.append("All magnitudes: \n");
    for (Magnitude mag : magnitudes.values()) {
      sb.append("\t" + mag + "\n");
    }

    sb.append("Preferred origin of " + origins.size() + ":\n");
    sb.append(preferredOrigin + "\n");
    sb.append("All origins: ");
    for (Origin origin : origins.values()) {
      sb.append(origin + "\n");
    }

    sb.append("Pick count: " + picks.size() + "\n");
    for (Pick pick : picks.values()) {
      sb.append(pick + "\n");
    }

    return sb.toString();
  }
}
