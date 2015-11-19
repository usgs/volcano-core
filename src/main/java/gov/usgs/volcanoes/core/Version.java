/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */
 
package gov.usgs.volcanoes.core;

/**
 * Class built by maven to hold version tags. 
 *
 * @author Tom Parker
 */
public interface Version {

  /** my build time. */
  public static final String BUILD_TIME = "2015-11-18T23:56:49Z";

  /** version taken from the POM. */
  public static final String POM_VERSION = "1.2.0-SNAPSHOT";

  /** my version string. */
  public static final String VERSION_STRING = "Version: " + POM_VERSION + " Built: " + BUILD_TIME;
}