/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */
 
package gov.usgs.volcanoes.util;

/**
 * Class built by maven to hold version tags. 
 *
 * @author Tom Parker
 */
public interface Version {

  /** my build time. */
  public static final String BUILD_TIME = "2015-09-09T18:40:02Z";

  /** version taken from the POM. */
  public static final String POM_VERSION = "1.1.7-SNAPSHOT";

  /** my version string. */
  public static final String VERSION_STRING = "Version: " + POM_VERSION + " Built: " + BUILD_TIME;
}