package gov.usgs.volcanoes.core.math;


public enum BinSize {
  MINUTE("Minute"), TENMINUTE("TenMinute"), HOUR("Hour"), DAY("Day"), WEEK("Week"), MONTH(
      "Month"), YEAR("Year");

  private String string;

  private BinSize(String s) {
    string = s;
  }

  public String toString() {
    return string;
  }

  public static BinSize fromString(String s) {
    if (s == null)
      return null;
    switch (s.charAt(0)) {
      case 'I':
        return MINUTE;
      case 'a':
        return TENMINUTE;
      case 'H':
        return HOUR;
      case 'D':
        return DAY;
      case 'W':
        return WEEK;
      case 'M':
        return MONTH;
      case 'Y':
        return YEAR;
      default:
        return null;
    }
  }

  public int toSeconds() {
    switch (string.charAt(0)) {
      case 'I':
        return 60;
      case 'a':
        return 600;
      case 'H':
        return 60 * 60;
      case 'D':
        return 60 * 60 * 24;
      case 'W':
        return 60 * 60 * 24 * 7;
      case 'M':
        return 60 * 60 * 24 * 7 * 4;
      case 'Y':
        return 60 * 60 * 24 * 7 * 52;
      default:
        return -1;
    }
  }

}
