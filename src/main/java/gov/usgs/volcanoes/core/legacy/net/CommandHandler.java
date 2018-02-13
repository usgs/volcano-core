package gov.usgs.volcanoes.core.legacy.net;

import gov.usgs.volcanoes.core.CodeTimer;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that handles server requests on a separate thread.
 *
 * @author Dan Cervelli
 */
abstract public class CommandHandler extends Thread {
  protected static final int COMMAND_BUFFER_SIZE = 2048;
  protected Server server;

  protected Map<String, Command> commands;
  protected Command executeCommand;
  protected String executeCommandInfo;
  protected SocketChannel channel;
  protected SelectionKey selectionKey;
  protected int slowCommandTime;

  protected static Connections connections = Connections.getInstance();
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

  public CommandHandler(Server svr, String name) {
    server = svr;
    commands = new HashMap<String, Command>();
    slowCommandTime = 0;

    this.setName(name);
    this.start();
  }

  abstract protected void setupCommandHandlers();

  protected void addCommand(String id, Command cmd) {
    commands.put(id, cmd);
  }

  protected Command getCommand(String cmd) {
    Command command = commands.get(cmd);
    return command;
  }

  protected synchronized void doCommand(SocketChannel ch, SelectionKey key, String cmd) {
    // logger.log(Level.INFO, "Command: " + cmd);
    channel = ch;
    selectionKey = key;
    String cmdName = "";
    int indexSpace = cmd.indexOf(' ');
    int indexColon = cmd.indexOf(':');
    if (indexSpace == -1 && indexColon == -1) {
      cmdName = cmd.trim();
    } else {

      int index = -1;
      if (indexSpace == -1 || indexColon == -1) {
        index = indexSpace;
        if (index == -1)
          index = indexColon;
      } else
        index = Math.min(indexSpace, indexColon);

      if (index == -1)
        index = indexColon;
      cmdName = cmd.substring(0, index);
    }

    Command command = getCommand(cmdName);
    if (command != null) {
      executeCommand = command;
      executeCommandInfo = cmd;
    }
    // quit is special, it has no Command class.
    else if (cmdName.equalsIgnoreCase("quit")) {
      closeConnection();
    }

    notify();
  }

  public void closeConnection() {
    server.closeConnection(channel, selectionKey);
  }

  public synchronized void run() {
    while (true) {
      try {
        wait();
      } catch (InterruptedException ignoreException) {
      }

      if (executeCommand != null) {
        try {
          String commandLine = executeCommandInfo;
          int eol = commandLine.indexOf('\n');
          if (eol != -1)
            commandLine = commandLine.substring(0, eol);

          CodeTimer ct = new CodeTimer(commandLine);
          executeCommand.doCommand(executeCommandInfo, channel);
          ct.stop();
          if (slowCommandTime > 0 && ct.getRunTimeMillis() > slowCommandTime)
            LOGGER.info("{}", String.format(Server.getHost(channel) + "/slow command (%1.2f ms) "
                    + commandLine, ct.getRunTimeMillis()));
        } catch (Exception e) {
          e.printStackTrace();
          LOGGER.error("Unhandled exception in main CommandHandler loop.", e);
        } finally {
          executeCommand = null;
          connections.endCommand(channel);
          server.addCommandHandler(this);
        }
      }
    }
  }
}
