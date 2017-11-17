package gov.usgs.volcanoes.core.xml;

import java.util.Map;

/**
 * XML event handler for {@link SimpleXMLParser}. 
 * User should extend this interface with concrete methods processing
 * xml actions, and pass it to parser. Parser will call appropriate 
 * method while event happens.
 * 
 * Reformatted and Java 1.5-ized from article:
 * http://www.javaworld.com/javatips/jw-javatip128_p.html
 * 
 * @author Steven R. Brandt, Dan Cervelli
 */
public interface XMLDocHandler {
  /**
   * Element started.
   * @param tag Tag name
   * @param h map of tag attributes and values
   * @throws Exception
   */
  public void startElement(String tag, Map<String, String> h) throws Exception;

  /**
   * Element ended.
   * @param tag Tag name
   * @throws Exception
   */
  public void endElement(String tag) throws Exception;

  /**
   * Document started.
   * @throws Exception
   */
  public void startDocument() throws Exception;

  /**
   * Document ended.
   * @throws Exception
   */
  public void endDocument() throws Exception;

  /**
   * Text or CDATA found.
   * @param str
   * @throws Exception
   */
  public void text(String str) throws Exception;
}
