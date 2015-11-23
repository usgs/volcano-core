/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.util;

import java.util.Comparator;

/**
 * Utility class containing methods for working with Strings.
 * 
 * @author Tom Parker
 */
public final class StringUtils {
  /**
   * Constructs comparator to compare strings with ignore case mean.
   * 
   * @return std comparison result
   */
  public static Comparator<String> getCaseInsensitiveStringComparator() {
    return new Comparator<String>() {
      public int compare(String o1, String o2) {
        return o1.compareToIgnoreCase(o2);
      }
    };
  }

  private StringUtils() {}
}
