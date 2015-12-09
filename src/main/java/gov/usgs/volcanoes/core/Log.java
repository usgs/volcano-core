/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.IOException;

/**
 * A utility class to handle implementation-specific logging configuration. Most classes will not
 * need to configure the logger. Most classes that do need to configure the logger will do so
 * through a properties file. I'm here for those few remaining classes.
 * 
 * @author Tom Parker
 *
 */
public class Log {
  private static String LOG_PATTERN = "%d{yyyy-MM-dd hh:mm:ss} %5p - %m (%F:%L)%n";

  /**
   * Add a rolling file appender to the root logger.
   * 
   * @param name name of file
   * @throws IOException when file cannot be created or modified
   */
  public static void addFileAppender(String name) throws IOException {
    PatternLayout layout = new PatternLayout(LOG_PATTERN);
    RollingFileAppender fileAppender = new RollingFileAppender(layout, name);
    fileAppender.setMaxFileSize("1MB");
    fileAppender.setMaxBackupIndex(2);
    Logger.getRootLogger().addAppender(fileAppender);
  }

  /**
   * Set root logger level.
   * 
   * @param level minimum logging level
   */
  public static void setLevel(Level level) {
    Logger.getRootLogger().setLevel(level);
  }
}
