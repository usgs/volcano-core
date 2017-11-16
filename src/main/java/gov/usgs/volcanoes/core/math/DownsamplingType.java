package gov.usgs.volcanoes.core.math;


public enum DownsamplingType {
  NONE("N"), DECIMATE("D"), MEAN("M");

  public String code;

  /**
   * Constructor
   * @param string rep of downsampling type
   */
  private DownsamplingType(String c) {
    code = c;
  }

  /**
   * Yield downsampling type from String
   * @param s string representation of downsampling type
   * @return downsampling type
   */
  public static DownsamplingType fromString(String s) {
    if (s == null)
      return null;

    if (s.equals("N") || s.equals("None"))
      return NONE;
    else if (s.equals("D") || s.equals("Decimation"))
      return DECIMATE;
    else if (s.equals("M") || s.equals("Mean filter"))
      return MEAN;
    else
      return null;
  }

  /**
   * Yield string representation of a downsampling type
   * @return string rep of downsampling type
   */
  public String toString() {
    return code;
  }
}
