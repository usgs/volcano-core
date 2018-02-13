package gov.usgs.volcanoes.core.legacy.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A class for simplifying and standardizing dealing with the Java
 * Logging API.  This overrides the default log output with a single line output
 * set to log all levels.  Simply use Log.getLogger() instead of the JDK
 * Logger.getLogger().
 *
 * TODO: eliminate SystemOutHandler either entirely or by subsuming into this class.
 * TODO: create day based file logger.
 *
 * @author Dan Cervelli
 */
public class Log
{
  /**
   * Default constructor
   */
  private Log()
  {}

  static
  {
    Logger logger = Logger.getLogger("gov.usgs");
    logger.setUseParentHandlers(false);
    Handler[] hs = logger.getHandlers();
    for (Handler h : hs)
      logger.removeHandler(h);

    attachSystemErrLogger(logger);
  }

  /**
   * Does nothing, return standard logger
   * @param key identifier of desired logger
   */
  public static Logger getLogger(String key)
  {
    return Logger.getLogger(key);
  }

  /**
   * Yield stack trace for an exception
   * @param t Exception to process
   * @return String contained stack trace for given exception or error
   */
  public static String getStackTraceString(Throwable t)
  {
    StackTraceElement[] stack = t.getStackTrace();
    StringBuffer sb = new StringBuffer(500);
    for (int i = 0; i < stack.length; i++)
    {
      sb.append("  ^---- Stack Trace: ");
      int ln = stack[i].getLineNumber();
      sb.append(stack[i].getClassName());
      sb.append(".");
      sb.append(stack[i].getMethodName());
      sb.append("/");
      sb.append(stack[i].getFileName());
      sb.append(":");
      if (ln > 0)
        sb.append(ln);
      else
        sb.append("???");
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Configure logger output to System.err stream
   * @param logger Logger
   */
  public static void attachSystemErrLogger(Logger logger)
  {
    ConsoleHandler errHandler = new ConsoleHandler();
    errHandler.setLevel(Level.ALL);
    errHandler.setFormatter(new LogFormatter());
    logger.addHandler(errHandler);
  }

  /**
   * Configure logger output to cycling set of files
   * @param log logger to output
   * @param fn the pattern for naming the output file
   * @param size the maximum number of bytes to write to any one file
   * @param count the number of files to use
   * @param append specifies append mode
   * @see FileHandler
   */
  public static void attachFileLogger(Logger log, String fn, int size, int count, boolean append)
  {
    try
    {
      FileHandler fh = new FileHandler(fn, size, count, append);
      fh.setLevel(findLevel(log, Level.ALL));
      fh.setFormatter(new LogFormatter());
      log.addHandler(fh);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }


  /*
   * find current logging level. There must be a better way.
   * @param l Logger
   * @param d default Level
   * @return level
   */
  public static Level findLevel(Logger l, Level d)
  {
    Logger ll = l;
    while (ll.getLevel() == null && ll.getParent() != null)
      ll = ll.getParent();
    return ll.getLevel() == null ? d : ll.getLevel();
  }

  /**
   * Utility class to format log output string
   *
   */
  private static class LogFormatter extends Formatter
  {
    private SimpleDateFormat dateOut;
    private Date date;

    public LogFormatter()
    {
      if (dateOut == null)
      {
        dateOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = new Date();
      }
    }

    public String format(LogRecord logRecord)
    {
      StringBuffer sb = new StringBuffer(100);
      date.setTime(logRecord.getMillis());
      sb.append(dateOut.format(date));
      sb.append(": (" + logRecord.getLevel() + ") ");
      sb.append(logRecord.getMessage() + "\n");
      Throwable t = logRecord.getThrown();
      Object[] params = logRecord.getParameters();
      String param = null;
      if (params != null && params.length > 0)
        param = (String)params[0];
      if (t != null)
      {
        sb.append(" ^------- Exception: " + t.getClass().getName() + "/" + t.getMessage() + "\n");
        sb.append(getStackTraceString(t));
      }
      else if (param != null)
      {
        sb.append(" ^------- Exception: " + param + "\n");
      }
      return sb.toString();
    }
  }
}

