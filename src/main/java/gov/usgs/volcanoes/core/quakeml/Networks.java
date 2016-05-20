/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Translate FDSN network codes into organizational names.
 *
 * @author Tom Parker
 *
 */
public class Networks {
  private static class NetworksHolder {
    public static Networks networks = new Networks();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(Networks.class);
  private static final String NETWORKS_FILE = "networks.csv";

  public static Networks getInstance() {
    return NetworksHolder.networks;
  }

  /**
   * Adapted from https://agiletribe.wordpress.com/2012/11/23/the-only-class-you-need-for-csv-files/
   *
   * @param rd reader
   * @return one row of tokens
   * @throws Exception when things go wrong
   */
  public static List<String> parseLine(Reader rd) throws Exception {
    int ch = rd.read();
    while (ch == '\r') {
      // ignore linefeed chars wherever, particularly just before end of file
      ch = rd.read();
    }
    if (ch < 0) {
      return null;
    }
    final Vector<String> store = new Vector<String>();
    StringBuffer curVal = new StringBuffer();
    boolean inquotes = false;
    boolean started = false;
    while (ch >= 0) {
      if (inquotes) {
        started = true;
        if (ch == '\"') {
          inquotes = false;
        } else {
          curVal.append((char) ch);
        }
      } else {
        if (ch == '\"') {
          inquotes = true;
          if (started) {
            // if this is the second quote in a value, add a quote
            // this is for the double quote in the middle of a value
            curVal.append('\"');
          }
        } else if (ch == ',') {
          store.add(curVal.toString());
          curVal = new StringBuffer();
          started = false;
        } else if (ch == '\r') {
          // ignore LF characters
        } else if (ch == '\n') {
          // end of a line, break out
          break;
        } else {
          curVal.append((char) ch);
        }
      }
      ch = rd.read();
    }
    store.add(curVal.toString());
    return store;
  }

  private final Map<String, String> networks;

  private Networks() {
    networks = new HashMap<String, String>();
    Reader reader = null;
    try {
      reader = new InputStreamReader(ClassLoader.getSystemResource(NETWORKS_FILE).openStream());

      try {
        List<String> fields = Networks.parseLine(reader);
        while (fields != null) {
          networks.put(fields.get(0), fields.get(2));
          fields = Networks.parseLine(reader);
        }
      } catch (final Exception ex) {
        // TODO Auto-generated catch block
        ex.printStackTrace();
      }
    } catch (final IOException ex) {
      LOGGER.info("Unable to read networks", ex);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (final IOException ignored) {
          // do nothing
        }
      }
    }

  }

  public String getName(String code) {
    return networks.get(code);
  }
}
