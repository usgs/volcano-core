package gov.usgs.volcanoes.core.xml;

import java.util.Map;

/**
 * XML event handler for {@link SimpleXmlParser}. 
 * User should extend this interface with concrete methods processing
 * xml actions, and pass it to parser. Parser will call appropriate 
 * method while event happens.
 * 
 * <P>Reformatted and Java 1.5-ized from article:
 * http://www.javaworld.com/javatips/jw-javatip128_p.html
 * 
 * @author Steven R. Brandt, Dan Cervelli
 */
public interface XmlDocHandler {
  /**
   * Element started.
   * @param tag Tag name
   * @param hh map of tag attributes and values
   * @throws Exception when things go wrong
   */
  public void startElement(String tag, Map<String, String> hh) throws Exception;

  /**
   * Element ended.
   * @param tag Tag name
   * @throws Exception when things go wrong
   */
  public void endElement(String tag) throws Exception;

  /**
   * Document started.
   * @throws Exception when things go wrong
   */
  public void startDocument() throws Exception;

  /**
   * Document ended.
   * @throws Exception when things go wrong
   */
  public void endDocument() throws Exception;

  /**
   * Text or CDATA found.
   * @param str input string
   * @throws Exception when things go wrong
   */
  public void text(String str) throws Exception;
}
