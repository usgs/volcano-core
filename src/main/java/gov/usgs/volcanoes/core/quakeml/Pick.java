/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.util.Date;

/**
 * Holder for QuakeML pick.
 *
 * @author Tom Parker
 *
 */
public class Pick {
  public static enum Onset {
    EMERGENT, IMPULSIVE, QUESTIONABLE;

    /**
     * Parse an Onset from a String.
     * 
     * @param string onset
     * @return onset object
     * @throws ParseException when things go wrong
     */
    public static Onset parse(String string) throws ParseException {
      if ("emergent".equals(string)) {
        return EMERGENT;
      } else if ("impulsive".equals(string)) {
        return IMPULSIVE;
      } else if ("questionable".equals(string)) {
        return QUESTIONABLE;
      } else {
        throw new ParseException("Cannot parse " + string, 12);
      }

    }
  }

  public static enum Polarity {
    NEGATIVE, POSITIVE, UNDECIDABLE;

    /**
     * Parse polarity from a String.
     *
     * @param string polarity
     * @return polarity object
     * @throws ParseException when things go wrong.
     */
    public static Polarity parse(String string) throws ParseException {
      if ("positive".equals(string)) {
        return POSITIVE;
      } else if ("negative".equals(string)) {
        return NEGATIVE;
      } else if ("undecidable".equals(string)) {
        return UNDECIDABLE;
      } else {
        throw new ParseException("Cannot parse " + string, 12);
      }
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(Pick.class);
  private final String channel;
  private Onset onset;
  private Polarity polarity;


  public final String publicId;

  private final long time;

  /**
   * Constructor.
   * 
   * @param pickElement XML pick element
   */
  public Pick(Element pickElement) {
    publicId = pickElement.getAttribute("publicID");
    LOGGER.debug("new Pick {}", publicId);

    final Element timeElement = (Element) pickElement.getElementsByTagName("time").item(0);
    time =
        QuakeMlUtils.parseTime(timeElement.getElementsByTagName("value").item(0).getTextContent());

    final NodeList onsetList = pickElement.getElementsByTagName("onset");
    if (onsetList != null && onsetList.getLength() > 0) {
      try {
        onset = Onset.parse(onsetList.item(0).getTextContent());
      } catch (final DOMException ex) {
        ex.printStackTrace();
      } catch (final ParseException ex) {
        ex.printStackTrace();
      }
    }

    final NodeList polarityList = pickElement.getElementsByTagName("polarity");
    if (polarityList != null && polarityList.getLength() > 0) {
      try {
        polarity =
            Polarity.parse(pickElement.getElementsByTagName("polarity").item(0).getTextContent());
      } catch (final DOMException ex) {
        ex.printStackTrace();
      } catch (final ParseException ex) {
        ex.printStackTrace();
      }
    }
    final Element waveformId = (Element) pickElement.getElementsByTagName("waveformID").item(0);
    final String station = waveformId.getAttribute("stationCode");
    final String chan = waveformId.getAttribute("channelCode");
    final String net = waveformId.getAttribute("networkCode");
    final String loc = waveformId.getAttribute("locationCode");

    channel = station + "$" + chan + "$" + net + "$" + loc;
  }

  public String getChannel() {
    return channel;
  }

  public Onset getOnset() {
    return onset;
  }

  public Polarity getPolarity() {
    return polarity;
  }

  public long getTime() {
    return time;
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("PublicId: " + publicId + "\n");
    sb.append("Time: " + new Date(time) + "\n");
    sb.append("Channel: " + channel + "\n");
    sb.append("Onset: " + onset + "\n");
    sb.append("Polarity: " + polarity + "\n");
    
    return sb.toString();
  }
}
