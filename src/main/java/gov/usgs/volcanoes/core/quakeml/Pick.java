/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.ParseException;

/**
 * Holder for QuakeML pick.
 *
 * @author Tom Parker
 *
 */
public class Pick {
  /**
   * Flag that roughly characterizes the sharpness of the onset.
   */
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

  /**
   * Polarity of first motion, usually from impulsive onsets.
   */
  public static enum Polarity {
    UNDECIDABLE, NEGATIVE, POSITIVE;

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

  public String publicId;
  private TimeQuantity time;
  private String channel;
  private Onset onset;
  private Polarity polarity;
  private String phaseHint;
  private EvaluationMode evaluationMode;

  /**
   * Constructor from manually created pick.
   * 
   * @param publicId public id
   * @param time pick time quantity
   * @param channel waveform identifier
   */
  public Pick(String publicId, TimeQuantity time, String channel) {
    this.publicId = publicId;
    this.time = time;
    this.channel = channel.replaceAll("\\s", "\\$");
    evaluationMode = EvaluationMode.MANUAL;
  }

  /**
   * Constructor from manually created pick.
   * 
   * @param publicId public id
   * @param time time in millis from 1970
   * @param channel waveform identifier
   */
  public Pick(String publicId, long time, String channel) {
    this(publicId, new TimeQuantity(time), channel);
  }

  /**
   * Constructor from XML pick element.
   * 
   * @param pickElement XML pick element
   */
  public Pick(Element pickElement) {
    publicId = pickElement.getAttribute("publicID");
    LOGGER.debug("new Pick {}", publicId);

    final Element timeElement = (Element) pickElement.getElementsByTagName("time").item(0);
    time = new TimeQuantity(timeElement);

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

    final NodeList phaseHintList = pickElement.getElementsByTagName("phaseHint");
    if (phaseHintList.getLength() > 0) {
      phaseHint = phaseHintList.item(0).getTextContent();
    }

    final Element waveformId = (Element) pickElement.getElementsByTagName("waveformID").item(0);
    final String station = waveformId.getAttribute("stationCode");
    final String chan = waveformId.getAttribute("channelCode");
    final String net = waveformId.getAttribute("networkCode");
    final String loc = waveformId.getAttribute("locationCode");

    channel = station + "$" + chan + "$" + net + "$" + loc;
  }

  /**
   * To XML element.
   * 
   * @param doc xml document
   * @return xml element
   */
  public Element toElement(Document doc) {
    Element pick = doc.createElement("pick");
    pick.setAttribute("publicID", publicId);

    pick.appendChild(time.toElement(doc));

    Element waveformId = doc.createElement("waveformID");
    String[] scnl = channel.split("\\$");
    waveformId.setAttribute("stationCode", scnl[0]);
    waveformId.setAttribute("channelCode", scnl[1]);
    waveformId.setAttribute("networkCode", scnl[2]);
    if (scnl.length >= 4) {
      waveformId.setAttribute("locationCode", scnl[3]);
    }
    pick.appendChild(waveformId);

    if (onset != null) {
      Element onsetElement = doc.createElement("onset");
      onsetElement.appendChild(doc.createTextNode(onset.toString().toLowerCase()));
      pick.appendChild(onsetElement);
    }

    if (polarity != null) {
      Element polarityElement = doc.createElement("polarity");
      polarityElement.appendChild(doc.createTextNode(polarity.toString().toLowerCase()));
      pick.appendChild(polarityElement);
    }

    if (phaseHint != null) {
      Element phaseElement = doc.createElement("phaseHint");
      phaseElement.appendChild(doc.createTextNode(phaseHint.toString()));
      pick.appendChild(phaseElement);
    }

    if (evaluationMode != null) {
      Element evalModeElement = doc.createElement("evaluationMode");
      evalModeElement.appendChild(doc.createTextNode(evaluationMode.toString().toLowerCase()));
      pick.appendChild(evalModeElement);
    }

    return pick;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("PublicId: " + publicId + "\n");
    sb.append("Time: " + time.getValue() + "\n");
    sb.append("Channel: " + channel + "\n");
    sb.append("Onset: " + onset + "\n");
    sb.append("Polarity: " + polarity);
    return sb.toString();
  }

  /**
   * Get label to display in pick marker.
   * 
   * @return text
   */
  public String getTag() {
    String label = "";
    if (onset != null) {
      label += onset.toString().toLowerCase().substring(0, 1);
    }
    label += phaseHint;
    if (polarity != null) {
      switch (polarity) {
        case NEGATIVE:
          label += "-";
          break;
        case POSITIVE:
          label += "+";
          break;
        default:
      }
    }
    return label;
  }

  /**
   * Get pick time.
   * 
   * @return milliseconds
   */
  public long getTime() {
    return time.getValue().getTime();
  }

  public TimeQuantity getTimeQuantity() {
    return time;
  }

  public void setTimeQuantity(TimeQuantity timeQuantity) {
    this.time = timeQuantity;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public Onset getOnset() {
    return onset;
  }

  public void setOnset(Onset onset) {
    this.onset = onset;
  }

  public Polarity getPolarity() {
    return polarity;
  }

  public void setPolarity(Polarity polarity) {
    this.polarity = polarity;
  }

  public String getPhaseHint() {
    return phaseHint;
  }

  public void setPhaseHint(String phaseHint) {
    this.phaseHint = phaseHint;
  }

  public EvaluationMode getEvaluationMode() {
    return evaluationMode;
  }

  public void setEvaluationMode(EvaluationMode evaluationMode) {
    this.evaluationMode = evaluationMode;
  }

}
