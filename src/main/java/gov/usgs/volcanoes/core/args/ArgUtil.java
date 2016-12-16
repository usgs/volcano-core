package gov.usgs.volcanoes.core.args;

import com.martiansoftware.jsap.JSAP;

public class ArgUtil {

  /**
   * Decide is an argument is required.
   * 
   * @param defaultArg The potential default
   * 
   * @return JSAP.REQUIRED if no valid default is provided
   */
  public static final boolean isRequired(String defaultArg) {
    if (defaultArg == null || "".equals(defaultArg)) {
      return JSAP.REQUIRED;
    } else {
      return JSAP.NOT_REQUIRED;
    }
  }
}
