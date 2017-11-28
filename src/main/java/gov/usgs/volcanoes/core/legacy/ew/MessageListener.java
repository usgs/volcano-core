package gov.usgs.volcanoes.core.legacy.ew;

import gov.usgs.volcanoes.core.legacy.ew.message.Message;

/**
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 */
public interface MessageListener {
  /**
   * This function is called whenever a message is received.
   *
   * @param msg the message
   */
  public void messageReceived(Message msg);
}
