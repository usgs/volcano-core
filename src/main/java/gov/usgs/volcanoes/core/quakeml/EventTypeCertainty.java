/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

/**
 * Possible event type certainties.
 * 
 * @author Tom Parker
 *
 */
public enum EventTypeCertainty {
  KNOWN("known"),
  SUSPECTED("suspected");
  
  private final String description;
  
  private EventTypeCertainty(String description) {
    this.description = description;
  }
  
  public String toString() {
    return description;
  }
}
