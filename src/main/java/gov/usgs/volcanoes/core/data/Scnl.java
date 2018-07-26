/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.data;

import gov.usgs.volcanoes.core.contrib.HashCodeUtil;
import gov.usgs.volcanoes.core.util.UtilException;

import java.util.regex.Pattern;

/**
 * An immutable class for holding SCNL data.
 *
 * @author Tom Parker
 */
public class Scnl implements Comparable<Scnl> {

  /** the default location code. */
  public static final String DEFAULT_LOCATION = "--";

  /** delimiter used in string representations of a SCNL. */
  public static final String DELIMITER = "$";

  /** my channel code. */
  public final String channel;

  /** my location code. */
  public final String location;

  /** my network code. */
  public final String network;

  /** my station code. */
  public final String station;

  /**
   * Construct a SCNL object with a default location field.
   *
   * @param station station
   * @param channel channel
   * @param network network
   */
  public Scnl(String station, String channel, String network) {
    this(station, channel, network, DEFAULT_LOCATION);
  }

  /**
   * Construct a SCNL object.
   *
   * @param station station
   * @param channel channel
   * @param network network
   * @param location location
   */
  public Scnl(String station, String channel, String network, String location) {
    this.station = station;
    this.channel = channel;
    this.network = network;

    if ("".equals(location.trim())) {
      this.location = DEFAULT_LOCATION;
    } else {
      this.location = location;
    }
  }

  /**
   * Compare this SCNL to another. Sorted by: network, station, channel, location.
   * 
   * @param scnl the object to compare
   * @return an int following compareTo conventions
   */
  public int compareTo(Scnl scnl) {
    final String me = String.format("%s$%s$%s$%s", network, station, channel, location);
    final String other =
        String.format("%s$%s$%s$%s", scnl.network, scnl.station, scnl.channel, scnl.location);

    return (me.compareTo(other));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    final Scnl other = (Scnl) obj;

    if ((station == null) ? (other.station != null) : !station.equals(other.station)) {
      return false;
    }
    if ((channel == null) ? (other.channel != null) : !channel.equals(other.channel)) {
      return false;
    }
    if ((network == null) ? (other.network != null) : !network.equals(other.network)) {
      return false;
    }
    if ((location == null) ? (other.location != null) : !location.equals(other.location)) {
      return false;
    }
    return true;
  }


  @Override
  public int hashCode() {
    int hash = HashCodeUtil.SEED;

    hash = HashCodeUtil.hash(hash, station);
    hash = HashCodeUtil.hash(hash, channel);
    hash = HashCodeUtil.hash(hash, network);
    hash = HashCodeUtil.hash(hash, location);

    return hash;
  }

  @Override
  public String toString() {
    return toString(DELIMITER);
  }

  /**
   * Return a string representation of the SCNL using the provided delimiter.
   * 
   * @param delimiter The string to use to seperate SCNL components
   * @return A string represenetation of this SCNL
   */
  public String toString(String delimiter) {
    final StringBuffer sb = new StringBuffer();
    sb.append(station).append(delimiter).append(channel).append(delimiter).append(network)
        .append(delimiter).append(location);
    return sb.toString();
  }

  /**
   * Parse a Scnl string.
   * 
   * @param scnlString standard delimited string
   * @return Scnl object
   * @throws UtilException when string cannot be parsed
   */
  public static Scnl parse(String scnlString) throws UtilException {
    if (scnlString.indexOf("$") == -1) {
      return Scnl.parse(scnlString, " ");
    }
    return Scnl.parse(scnlString, DELIMITER);
  }

  /**
   * Parse a Scnl string.
   * 
   * @param scnlString delimited string
   * @param delimiter String passed to String.split()
   * @return Scnl object
   * @throws UtilException when string cannot be parsed
   */
  public static Scnl parse(String scnlString, String delimiter) throws UtilException {
    String[] parts = scnlString.split(Pattern.quote(delimiter));
    if (parts.length > 3) {
      return new Scnl(parts[0], parts[1], parts[2], parts[3]);
    } else if (parts.length > 2) {
      return new Scnl(parts[0], parts[1], parts[2]);
    } else {
      throw new UtilException("Cannot parse '" + scnlString + "'");
    }
  }

}
