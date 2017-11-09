/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0
 * Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.configfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for dealing with key-value pair based configuration files.
 *
 * @author Dan Cervelli
 */
public final class ConfigFile {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFile.class);
  private boolean successfullyRead = false;


  /**
   * Find the first readable file in a list of names.
   *
   * @param fileNames list of names to search
   * @return a string suitable to be passed to File()
   *
   */
  public static String findConfig(List<String> fileNames) {
    String fileName = null;
    String name;

    final Iterator<String> nameIter = fileNames.iterator();
    while (fileName == null && nameIter.hasNext()) {
      name = nameIter.next();
      if (new File(name).canRead()) {
        fileName = name;
      }
    }

    return fileName;
  }

  private final Map<String, List<String>> config;

  private String name;

  /**
   * Default constructor.
   */
  public ConfigFile() {
    config = new HashMap<String, List<String>>();
  }

  /**
   * Constructor.
   *
   * @param map map of configured parameters
   */
  public ConfigFile(Map<String, List<String>> map) {
    config = map;
  }

  /**
   * Constructor.
   *
   * @param fn name of file to init class
   * @throws FileNotFoundException thrown when I cannot find the provided file
   */
  public ConfigFile(String fn) {
    setName(fn);

    config = new HashMap<String, List<String>>();
    try {
      readConfigFile(fn);
    } catch (FileNotFoundException ex) {
      LOGGER.debug("Cannot read config file. ({})", ex.getLocalizedMessage());
    }
  }

  /**
   * Create a defensive copy of this object.
   *
   * @return A safe copy of this config
   */
  public ConfigFile deepCopy() {
    final ConfigFile copy = new ConfigFile();

    for (final Map.Entry<String, List<String>> entry : config.entrySet()) {
      final List<String> value = entry.getValue();
      final String key = entry.getKey();

      if (value != null) {
        final List<String> newList = new ArrayList<String>(value);
        copy.putList(key, newList);
      }
    }

    return copy;
  }

  /**
   * Check reading of file.
   * 
   * @return true if configuration file was successfully read
   */
  public boolean wasSuccessfullyRead() {
    return successfullyRead;
  }

  /**
   * Get parameter value as boolean
   *
   * @param key parameter name
   * @return value of given parameter
   * @throws RuntimeException if parameter absent or can't be parsed.
   */
  public boolean getBoolean(String key) {
    final String value = getString(key);
    if (value == null) {
      throw new RuntimeException("Illegal " + key + ":null");
    }
    if (!(value.toLowerCase().equals("true") || value.toLowerCase().equals("t")
        || value.toLowerCase().equals("false") || value.toLowerCase().equals("f")
        || value.equals("1") || value.equals("0"))) {
      throw new RuntimeException("Illegal " + key + ":" + value);
    }

    boolean pv = false;
    if (value.toLowerCase().equals("true") || value.equals("1")
        || value.toLowerCase().equals("t")) {
      pv = true;
    }
    return pv;
  }

  /**
   * Get parameter value as boolean
   *
   * @param key parameter name
   * @param defaultValue defaultValue
   * @return value of given parameter
   * @throws RuntimeException if parameter absent or can't be parsed.
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    boolean value;
    try {
      value = getBoolean(key);
    } catch (final RuntimeException ex) {
      put(key, String.valueOf(defaultValue));
      value = defaultValue;
    }

    return value;
  }

  /**
   * Accessor for configuration map.
   *
   * @return map parameter name - list of parameter's values
   */
  public Map<String, List<String>> getConfig() {
    return config;
  }

  /**
   * Get parameter value as boolean
   *
   * @param key parameter name
   * @return value of given parameter
   * @throws RuntimeException if parameter absent or can't be parsed.
   */
  public double getDouble(String key) {
    final String value = getString(key);
    if (value == null) {
      throw new RuntimeException("Illegal " + key + ":null");
    }

    final double d = Double.parseDouble(value);

    return d;
  }

  /**
   * Get parameter value as boolean
   *
   * @param key parameter name
   * @param defaultValue defaultValue
   * @return value of given parameter
   * @throws RuntimeException if parameter absent or can't be parsed.
   */
  public double getDouble(String key, double defaultValue) {
    double value;
    try {
      value = getDouble(key);
    } catch (final RuntimeException ex) {
      put(key, String.valueOf(defaultValue));
      value = defaultValue;
    }

    return value;
  }

  /**
   * Get parameter value as boolean
   *
   * @param key parameter name
   * @return value of given parameter
   * @throws RuntimeException if parameter absent or can't be parsed.
   */
  public long getLong(String key) {
    final String value = getString(key);
    if (value == null) {
      throw new RuntimeException("Illegal parameter " + key + " = null");
    }

    final long i = Long.parseLong(value);

    return i;
  }

  /**
   * Get parameter value as long
   *
   * @param key parameter name
   * @param defaultValue defaultValue
   * @return value of given parameter
   * @throws RuntimeException if parameter absent or can't be parsed.
   */
  public long getLong(String key, long defaultValue) {
    long value;
    try {
      value = getLong(key);
    } catch (final RuntimeException ex) {
      put(key, String.valueOf(defaultValue));
      value = defaultValue;
    }

    return value;
  }

  /**
   * Get parameter value as boolean
   *
   * @param key parameter name
   * @return value of given parameter
   * @throws RuntimeException if parameter absent or can't be parsed.
   */
  public int getInt(String key) {
    final String value = getString(key);
    if (value == null) {
      throw new RuntimeException("Illegal parameter " + key + " = null");
    }

    final int i = Integer.parseInt(value);

    return i;
  }

  /**
   * Get parameter value as boolean
   *
   * @param key parameter name
   * @param defaultValue defaultValue
   * @return value of given parameter
   * @throws RuntimeException if parameter absent or can't be parsed.
   */
  public int getInt(String key, int defaultValue) {
    int value;
    try {
      value = getInt(key);
    } catch (final Exception ex) {
      put(key, String.valueOf(defaultValue));
      value = defaultValue;
    }

    return value;
  }

  /**
   * Get value named key as a list of strings.
   *
   * @param key parameter name
   * @return parameter value list
   */
  public List<String> getList(String key) {
    return config.get(key);
  }

  /**
   * Accessor for configuration file name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Return an object found by the provided parser.
   *
   * @param key config key to search
   * @param parser parser that will do the parsing
   * @return object found by parser
   * @throws ParseException when value is not null and cannon be parsed.
   */
  public <T> T getObject(String key, Parser<T> parser) throws ParseException {
    return parser.parse(getString(key));
  }

  /**
   * same as get(String key), but create configuration for item if it absent.
   *
   * @param key name of parameter
   * @return parameter value by its name
   */
  private List<String> getOrCreateList(String key) {
    List<String> ss = config.get(key);
    if (ss == null) {
      ss = new ArrayList<String>();
      config.put(key, ss);
    }
    return ss;
  }

  /**
   * Get value named key as a string.
   *
   * @param key parameter name
   * @return first item from parameter value list, null if nothing defined
   */
  public String getString(String key) {
    final List<String> ss = config.get(key);
    if (ss == null || ss.size() == 0) {
      return null;
    } else {
      return ss.get(0);
    }
  }

  /**
   * Get value names key as a string. If key has no value set provided default and return it.
   *
   * @param key parameter name
   * @param defaultValue default parameter value
   * @return first item from parameter list
   *
   */
  public String getString(String key, String defaultValue) {
    String value = getString(key);
    if (value == null) {
      put(key, defaultValue);
      value = defaultValue;
    }

    return value;
  }

  /**
   * Get subconfiguration named prefix.
   *
   * @param prefix prefix to filter parameters
   * @return ConfigFile containing part of parameters beginning with given prefix
   */
  public ConfigFile getSubConfig(String prefix) {
    return getSubConfig(prefix, false);
  }

  /**
   * Get subconfiguration named prefix. Possibly seeded with values from this config
   *
   * @param prefix prefix to filter parameters
   * @param inherit if true inherit values from this config
   * @return ConfigFile containing part of parameters beginning with given prefix
   */
  public ConfigFile getSubConfig(String prefix, boolean inherit) {
    ConfigFile newConfig;

    if (inherit) {
      newConfig = deepCopy();
    } else {
      newConfig = new ConfigFile();
    }

    newConfig.name = prefix;
    final Map<String, List<String>> configMap = newConfig.getConfig();

    for (final Map.Entry<String, List<String>> entry : config.entrySet()) {
      final String key = entry.getKey();
      final List<String> value = entry.getValue();
      if (key.startsWith(prefix) && key.length() > prefix.length()) {
        final String newKey = key.substring(prefix.length() + 1);
        final List<String> newList = new ArrayList<String>(value);
        configMap.put(newKey, newList);
      }
    }

    return newConfig;
  }

  /**
   * Adds configuration parameter for item.
   *
   * @param key item name
   * @param val value as string
   */
  public void put(String key, String val) {
    getOrCreateList(key).add(val);
  }

  /**
   * Add with optional replacement.
   *
   * @param key parameter name
   * @param val parameter value
   * @param replace flag, if false force parameter value replacing
   */
  public void put(String key, String val, boolean replace) {
    if (!replace) {
      remove(key);
    }

    put(key, val);
  }

  /**
   * Add with optional replacement.
   *
   * @param cf In Config File
   * @param preserve flag, if false force parameter value replacing
   */
  public void putConfig(ConfigFile cf, boolean preserve) {
    final Map<String, List<String>> configIn = cf.getConfig();

    for (final Map.Entry<String, List<String>> entry : configIn.entrySet()) {
      final String key = entry.getKey();
      final List<String> value = entry.getValue();

      if (!preserve) {
        remove(key);
      }

      putList(key, value);
    }
  }

  /**
   * Adds parameter configuration.
   *
   * @param key parameter name
   * @param list of parameter's values
   */
  public void putList(String key, List<String> list) {
    config.put(key, list);
  }

  /**
   * Loads configuration from disk file.
   *
   * @param file name of file to read
   * @throws FileNotFoundException thrown when I cannot find the provided file
   */
  public void readConfigFile(File file) throws FileNotFoundException {
    readConfigFile(file, true);
  }

  /**
   * Loads configuration from disk file.
   *
   * @param file name of file to read
   * @param replace flag, if false force parameter value replacing
   * @throws FileNotFoundException thrown when I cannot find the provided file
   */
  public void readConfigFile(File file, boolean replace) throws FileNotFoundException {
    try {
      // File f = new File(fn);
      final Reader reader =
          new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
      final BufferedReader in = new BufferedReader(reader);
      String line;
      while ((line = in.readLine()) != null) {
        line = line.trim();
        // skip whitespace and comments
        if (line.length() != 0 && !line.startsWith("#") && !line.startsWith("@")) {
          final String key = line.substring(0, line.indexOf('=')).trim();
          String val = line.substring(line.indexOf('=') + 1).trim();

          if (val.toLowerCase().equals("@begin-multiline")) {
            final StringBuffer sb = new StringBuffer(2048);
            boolean done = false;
            while (!done) {
              final String is = in.readLine();
              if (is != null && is.toLowerCase().equals("@end-multiline")) {
                done = true;
              } else {
                sb.append(is);
                sb.append('\n');
              }
            }
            val = sb.toString();
          }

          if (!replace) {
            remove(key);
          }

          final List<String> ss = getOrCreateList(key);
          ss.add(val);
        }

        if (line.toLowerCase().startsWith("@include ")) {
          // TODO: deal with absolute paths
          String ifn = line.substring(line.indexOf(" ") + 1);
          ifn = file.getAbsoluteFile().getParent() + File.separator + ifn;
          readConfigFile(ifn);
        }
      }
      successfullyRead = true;
      in.close();
    } catch (final FileNotFoundException ex) {
      LOGGER.debug("Unable to read {}, skipping it.", file);
    } catch (final Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Loads configuration from disk file.
   *
   * @param fn name of file to read
   * @throws FileNotFoundException thrown when I cannot find the provided file
   */
  public void readConfigFile(String fn) throws FileNotFoundException {
    readConfigFile(fn, true);
  }

  /**
   * Loads configuration from disk file.
   *
   * @param fn name of file to read
   * @param replace Replace, rather than augment, existing values
   * @throws FileNotFoundException thrown when I cannot find the provided file
   */
  public void readConfigFile(String fn, boolean replace) throws FileNotFoundException {
    readConfigFile(new File(fn), replace);
  }

  /**
   * Remove parameter.
   *
   * @param key parameter name to remove
   */
  public void remove(String key) {
    config.remove(key);
  }

  /**
   * Remove item from parameter value list.
   *
   * @param key parameter name
   * @param val item from values list to delete
   */
  public void remove(String key, String val) {
    final List<String> ss = config.get(key);
    if (ss == null) {
      return;
    } else {
      ss.remove(val);
      if (ss.size() == 0) {
        config.remove(key);
      }
    }
  }

  /**
   * Mutator for configuration file name.
   *
   * @param name name
   */
  public void setName(String name) {
    if (name.endsWith(".config")) {
      this.name = name.substring(0, name.indexOf(".config"));
    } else {
      this.name = name;
    }
  }

  /**
   * Create a string representation of configuration.
   *
   * @return string representation of configuration
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (final Map.Entry<String, List<String>> entry : config.entrySet()) {
      final String key = entry.getKey();
      final List<String> value = entry.getValue();
      if (value.size() == 1) {
        sb.append(key + "=" + value.get(0) + "\n");
      } else {
        sb.append(key + "=[list]\n");
        for (final String s : value) {
          sb.append("\t" + s + "\n");
        }
      }
    }

    return sb.toString();
  }


  /**
   * Writes configuration to file.
   *
   * @param fn file name to dump
   */
  public void writeToFile(String fn) {
    // TODO: cleanup
    setName(fn);
    final Set<String> keySet = config.keySet();
    final Iterator<String> it = keySet.iterator();
    final ArrayList<String> v = new ArrayList<String>();
    while (it.hasNext()) {
      v.add(it.next());
    }

    final String[] keys = new String[v.size()];
    for (int i = 0; i < keys.length; i++) {
      keys[i] = v.get(i);
    }

    Arrays.sort(keys);

    try {
      final Path file = FileSystems.getDefault().getPath(fn);
      final Path bak = FileSystems.getDefault().getPath(fn + ".bak");

      if (Files.exists(bak)) {
        LOGGER.debug("Removing old backup config. ({})", bak);
        Files.delete(bak);
      }

      if (Files.exists(file)) {
        LOGGER.debug("Making config backup. ({})", bak);
        Files.move(file, bak, StandardCopyOption.REPLACE_EXISTING);
      }

      LOGGER.debug("Writing config. ({})", fn);
      final OutputStream outStream = new FileOutputStream(new File(fn));
      final Writer writer = new OutputStreamWriter(outStream, StandardCharsets.UTF_8);
      final PrintWriter out = new PrintWriter(writer);

      for (int i = 0; i < keys.length; i++) {
        final String k = keys[i];
        final Object o = config.get(k);
        @SuppressWarnings("unchecked")
        final List<String> vals = (List<String>) o;
        final Iterator<String> it2 = vals.iterator();
        while (it2.hasNext()) {
          out.println(k + "=" + it2.next());
        }
        out.println();
      }
      out.close();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }

}
