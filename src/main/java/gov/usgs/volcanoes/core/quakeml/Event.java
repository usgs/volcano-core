/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import gov.usgs.volcanoes.core.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Holder for QuakeML Event. Not all QuakeML elements have been implemented.
 *
 * @author Tom Parker
 *
 */
public class Event {

  private static final Logger LOGGER = LoggerFactory.getLogger(Event.class);

  private Map<String, Magnitude> magnitudes = new HashMap<String, Magnitude>();
  private Map<String, Origin> origins = new HashMap<String, Origin>();
  private Map<String, Pick> picks = new HashMap<String, Pick>();
  private Map<String, StationMagnitude> stationMagnitudes = new HashMap<String, StationMagnitude>();

  public String publicId;
  private String eventSource;
  private String eventId;

  private Magnitude preferredMagnitude;
  private Origin preferredOrigin;
  private EventType type;
  private EventTypeCertainty typeCertainty;
  private String description;
  private String comment;

  private final ArrayList<EventObserver> observers = new ArrayList<EventObserver>();

  /**
   * Constructor.
   *
   * @param event template event
   */
  public Event(Element event) {
    this(event.getAttribute("publicID"));
    parseEvent(event);
  }

  /**
   * Constructor.
   *
   * @param publicId event id
   */
  public Event(String publicId) {
    this.publicId = publicId;
    LOGGER.debug("New event ({}}", publicId);
  }

  /**
   * To XML element.
   * 
   * @param doc xml document
   * @return xml element
   */
  public Element toElement(Document doc) {

    Element event = doc.createElement("event");
    event.setAttribute("publicID", publicId);
    event.setAttribute("catalog:eventid", eventId);
    event.setAttribute("catalog:eventsource", eventSource);

    if (preferredOrigin != null) {
      Element prefOriginId = doc.createElement("preferredOriginID");
      prefOriginId.appendChild(doc.createTextNode(preferredOrigin.publicId));
      event.appendChild(prefOriginId);
    }

    if (preferredMagnitude != null) {
      Element prefMagId = doc.createElement("preferredMagnitudeID");
      prefMagId.appendChild(doc.createTextNode(preferredMagnitude.publicId));
      event.appendChild(prefMagId);
    }

    if (type != null) {
      Element typeElement = doc.createElement("type");
      typeElement.appendChild(doc.createTextNode(type.toString()));
      event.appendChild(typeElement);
    }

    if (typeCertainty != null) {
      Element typeCertaintyElement = doc.createElement("typeCertainty");
      typeCertaintyElement.appendChild(doc.createTextNode(typeCertainty.toString()));
      event.appendChild(typeCertaintyElement);
    }

    if (description != null) {
      Element descriptionElement = doc.createElement("description");
      Element text = doc.createElement("text");
      descriptionElement.appendChild(text);
      text.appendChild(doc.createTextNode(this.description));
      event.appendChild(descriptionElement);
    }

    if (comment != null) {
      Element commentElement = doc.createElement("comment");
      Element text = doc.createElement("text");
      commentElement.appendChild(text);
      text.appendChild(doc.createTextNode(this.comment));
      event.appendChild(commentElement);
    }

    for (Origin origin : origins.values()) {
      event.appendChild(origin.toElement(doc));
    }

    for (Magnitude magnitude : magnitudes.values()) {
      event.appendChild(magnitude.toElement(doc));
    }

    for (Pick pick : picks.values()) {
      event.appendChild(pick.toElement(doc));
    }

    for (StationMagnitude magnitude : stationMagnitudes.values()) {
      event.appendChild(magnitude.toElement(doc));
    }

    return event;
  }

  public void addObserver(EventObserver observer) {
    observers.add(observer);
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

  private void parseStationMagnitudes(NodeList magnitudeElements) {
    stationMagnitudes.clear();
    final int magnitudeCount = magnitudeElements.getLength();
    for (int idx = 0; idx < magnitudeCount; idx++) {
      StationMagnitude magnitude = new StationMagnitude((Element) magnitudeElements.item(idx));
      LOGGER.debug("Adding station mag {} {}", idx, magnitude.publicId);
      stationMagnitudes.put(magnitude.publicId, magnitude);
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
  public void parseEvent(Element event) {

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

    parseStationMagnitudes(event.getElementsByTagName("stationMagnitude"));

    eventSource =
        StringUtils.stringToString(event.getAttribute("catalog:eventsource"), eventSource);
    eventId = StringUtils.stringToString(event.getAttribute("catalog:eventid"), eventId);

    final Element descriptionElement = (Element) event.getElementsByTagName("description").item(0);
    if (descriptionElement != null) {
      description = StringUtils.stringToString(
          descriptionElement.getElementsByTagName("text").item(0).getTextContent(), description);
    }

    final Element commentElement = (Element) event.getElementsByTagName("comment").item(0);
    if (commentElement != null) {
      comment = StringUtils.stringToString(
          commentElement.getElementsByTagName("text").item(0).getTextContent(), comment);
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
        } else if ("typeCertainty".equals(element.getTagName())) {
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
    sb.append("Evid: " + eventId + "\n");
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

  public Map<String, Magnitude> getMagnitudes() {
    return magnitudes;
  }

  public void setMagnitudes(Map<String, Magnitude> magnitudes) {
    this.magnitudes = magnitudes;
  }

  public Map<String, Origin> getOrigins() {
    return origins;
  }

  public void setOrigins(Map<String, Origin> origins) {
    this.origins = origins;
  }

  public Map<String, Pick> getPicks() {
    return picks;
  }

  public void setPicks(Map<String, Pick> picks) {
    this.picks = picks;
  }

  public String getPublicId() {
    return publicId;
  }

  public void setPublicId(String publicId) {
    this.publicId = publicId;
  }

  public String getEventSource() {
    return eventSource;
  }

  public void setEventSource(String eventSource) {
    this.eventSource = eventSource;
  }

  @Deprecated
  public String getEvid() {
    return eventId;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public Magnitude getPreferredMagnitude() {
    return preferredMagnitude;
  }

  public void setPreferredMagnitude(Magnitude preferredMagnitude) {
    this.preferredMagnitude = preferredMagnitude;
  }

  public Origin getPreferredOrigin() {
    return preferredOrigin;
  }

  public void setPreferredOrigin(Origin preferredOrigin) {
    this.preferredOrigin = preferredOrigin;
  }

  public EventType getType() {
    return type;
  }

  public void setType(EventType type) {
    this.type = type;
  }

  public EventTypeCertainty getTypeCertainty() {
    return typeCertainty;
  }

  public void setTypeCertainty(EventTypeCertainty typeCertainty) {
    this.typeCertainty = typeCertainty;
  }

  public String getDescription() {
    return description;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * @return the stationMagnitudes
   */
  public Map<String, StationMagnitude> getStationMagnitudes() {
    return stationMagnitudes;
  }

  /**
   * @param stationMagnitudes the stationMagnitudes to set
   */
  public void setStationMagnitudes(Map<String, StationMagnitude> stationMagnitudes) {
    this.stationMagnitudes = stationMagnitudes;
  }


}
