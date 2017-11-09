package gov.usgs.volcanoes.core.legacy.plot.decorate;

import gov.usgs.volcanoes.core.legacy.plot.render.FrameRenderer;

/**
 * <p>Basic abstract class to configure FrameRenderer and tell him how to process it's standard elements.</p>
 * 
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/06/15 14:29:15  dcervelli
 * Swarm 1.3.4 changes.
 *
 * @author Dan Cervelli
 */
abstract public class FrameDecorator {
  /**
   * Process FrameRenderer and adds configured renderers for axises and title
   * @param fr Frame renderer to process
   */
  abstract public void decorate(FrameRenderer fr);

  /**
   * Refresh data
   */
  public void update() {}
}
