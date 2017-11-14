package gov.usgs.volcanoes.core.util;

/**
 * Listener for stream reading event in ProgressInputStream.
 * 
 * @see ProgressInputStream
 * 
 * @author Dan Cervelli
 */
public interface ProgressListener {
  /**
   * This method is called after each stream reading event.
   * @param portionDone part of overall progress (0 ... 1) 
   *     relative whole ProgressInputStream's length
   */
  public void progressDone(float portionDone);
}
