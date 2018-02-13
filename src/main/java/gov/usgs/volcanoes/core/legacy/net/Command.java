package gov.usgs.volcanoes.core.legacy.net;

import java.nio.channels.SocketChannel;

/**
 * An interface for a command to be handled.
 *
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 */
public interface Command
{
  public void doCommand(Object info, SocketChannel channel);
}

