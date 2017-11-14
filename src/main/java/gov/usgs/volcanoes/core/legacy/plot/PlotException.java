package gov.usgs.volcanoes.core.legacy.plot;

public class PlotException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor
   * @param m error message
   */
  public PlotException(String m) {
    super(m);
  }

}
