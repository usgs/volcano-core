package gov.usgs.volcanoes.core.legacy.plot.decorate;

import gov.usgs.volcanoes.core.legacy.plot.render.FrameRenderer;

/**
 * <p>Basic abstract class to configure FrameRenderer and 
 * tell him how to process it's standard elements.</p>
 * 
 * <p>@author Dan Cervelli</p>
 */
public abstract class FrameDecorator {

  /**
   * Process FrameRenderer and adds configured renderers for axises and title.
   * @param fr Frame renderer to process
   */
  public abstract void decorate(FrameRenderer fr);

  /**
   * Refresh data.
   */
  public void update() {}
}
