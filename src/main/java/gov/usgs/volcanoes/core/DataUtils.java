package gov.usgs.volcanoes.core;

public final class DataUtils {

  /**
   * Register.
   *
   * @param num1 fist number
   * @param num2 second number
   * @return register
   */
  public static double register(double num1, double num2) {
    final double dif = num1 % num2;
    if (dif >= num2 / 2) {
      return num1 + (num2 - dif);
    } else {
      return num1 - dif;
    }
  }

  /** uninstantiatable. */
  private DataUtils() {}

}
