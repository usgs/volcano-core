package gov.usgs.volcanoes.core.quakeml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class EventSet extends HashMap<String, Event> {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventSet.class);
  private static final long serialVersionUID = 1L;

  /**
   * Parse QuakeML.
   * 
   * @param inStream QuakeML source
   * @return populated set
   * @throws SAXException when QuakeML cannot be parsed.
   * @throws IOException when source cannot be read.
   * @throws ParserConfigurationException when things go wrong.
   */
  public static EventSet parseQuakeml(InputStream inStream)
      throws IOException, ParserConfigurationException, SAXException {

    EventSet eventSet = new EventSet();
    if (inStream.available() > 0) {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
      LOGGER.debug("Available: {}", inStream.available());
      Document doc = docBuilder.parse(inStream);
      doc.getDocumentElement().normalize();

      NodeList eventElements = doc.getElementsByTagName("event");
      LOGGER.debug("Got {} events.", eventElements.getLength());
      int eventCount = eventElements.getLength();
      for (int idx = 0; idx < eventCount; idx++) {
        Event event = new Event((Element) eventElements.item(idx));
        eventSet.put(event.publicId, event);
      }
      LOGGER.debug("Parsed {} events.", eventSet.size());
    } else {
      LOGGER.debug("Received empty QuakeML.");
    }

    return eventSet;
  }

  /**
   * Parse QuakeML.
   * 
   * @param url QuakeML source
   * @return populated set
   * @throws SAXException when QuakeML cannot be parsed.
   * @throws IOException when source cannot be read.
   * @throws ParserConfigurationException when things go wrong.
   */
  public static EventSet parseQuakeml(URL url)
      throws SAXException, IOException, ParserConfigurationException {
    return parseQuakeml(url.openStream());
  }
}
