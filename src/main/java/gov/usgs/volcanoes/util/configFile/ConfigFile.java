package gov.usgs.volcanoes.util.configFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.usgs.volcanoes.util.types.Types;

/**
 * A class for dealing with key-value pair based configuration files.
 * 
 * @author Dan Cervelli
 */
public class ConfigFile implements Cloneable {
	private Map<String, List<String>> config;
	private String name;

	/**
	 * Default constructor
	 */
	public ConfigFile() {
		config = new HashMap<String, List<String>>();
	}

	/**
	 * Constructor
	 * 
	 * @param map
	 *            map of configured parameters
	 */
	public ConfigFile(Map<String, List<String>> map) {
		config = map;
	}

	/**
	 * Constructor
	 * 
	 * @param fn
	 *            name of file to init class
	 * @throws FileNotFoundException 
	 */
	public ConfigFile(String fn) throws FileNotFoundException {
		setName(fn);

		config = new HashMap<String, List<String>>();
		readConfigFile(fn);
	}

	/**
	 * Loads configuration from disk file
	 * 
	 * @param fn
	 *            name of file to read
	 * @throws FileNotFoundException 
	 */
	public void readConfigFile(String fn) throws FileNotFoundException {
		readConfigFile(fn, true);
	}

	/**
	 * Loads configuration from disk file
	 * 
	 * @param fn
	 *            name of file to read
	 * @throws FileNotFoundException 
	 */
	public void readConfigFile(String fn, boolean l) throws FileNotFoundException {
		readConfigFile(new File(fn), l);
	}

	/**
	 * Loads configuration from disk file
	 * 
	 * @param fn
	 *            name of file to read
	 * @throws FileNotFoundException 
	 */
	public void readConfigFile(File f) throws FileNotFoundException {
		readConfigFile(f, true);
	}

	/**
	 * Loads configuration from disk file
	 * 
	 * @param fn
	 *            name of file to read
	 * @param l
	 *            flag, if false force parameter value replacing
	 * @throws FileNotFoundException 
	 */
	public void readConfigFile(File f, boolean l) throws FileNotFoundException {
		try {
			// File f = new File(fn);
			BufferedReader in = new BufferedReader(new FileReader(f));
			String s;
			while ((s = in.readLine()) != null) {
				s = s.trim();
				// skip whitespace and comments
				if (s.length() != 0 && !s.startsWith("#") && !s.startsWith("@")) {
					String key = s.substring(0, s.indexOf('=')).trim();
					String val = s.substring(s.indexOf('=') + 1).trim();

					if (val.toLowerCase().equals("@begin-multiline")) {
						StringBuffer sb = new StringBuffer(2048);
						boolean done = false;
						while (!done) {
							String is = in.readLine();
							if (is != null && is.toLowerCase().equals("@end-multiline"))
								done = true;
							else {
								sb.append(is);
								sb.append('\n');
							}
						}
						val = sb.toString();
					}

					if (!l)
						remove(key);

					List<String> ss = getOrCreateList(key);
					ss.add(val);
				}

				if (s.toLowerCase().startsWith("@include ")) {
					// TODO: deal with absolute paths
					String ifn = s.substring(s.indexOf(" ") + 1);
					ifn = f.getAbsoluteFile().getParent() + File.separator + ifn;
					readConfigFile(ifn);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get value named key as a list of strings
	 * 
	 * @param key
	 *            parameter name
	 * @return parameter value list
	 */
	public List<String> getList(String key) {
		return config.get(key);
	}

	/**
	 * same as get(String key), but create configuration for item if it absent
	 * 
	 * @param key
	 *            name of parameter
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
	 * Adds parameter configuration
	 * 
	 * @param key
	 *            parameter name
	 * @param list
	 *            of parameter's values
	 */
	public void putList(String key, List<String> list) {
		config.put(key, list);
	}

	/**
	 * Adds configuration parameter for item
	 * 
	 * @param key
	 *            item name
	 * @param val
	 *            value as string
	 */
	public void put(String key, String val) {
		getOrCreateList(key).add(val);
	}

	/**
	 * Add with optional replacement
	 * 
	 * @param key
	 *            parameter name
	 * @param val
	 *            parameter value
	 * @param l
	 *            flag, if false force parameter value replacing
	 */
	public void put(String key, String val, boolean l) {
		if (!l)
			remove(key);

		put(key, val);
	}

	/**
	 * Add with optional replacement
	 * 
	 * @param cf
	 *            In Config File
	 * @param preserve
	 *            flag, if false force parameter value replacing
	 */
	public void putConfig(ConfigFile cf, boolean preserve) {
		Map<String, List<String>> configIn = cf.getConfig();
		for (String key : configIn.keySet()) {
			if (!preserve)
				remove(key);

			putList(key, configIn.get(key));
		}
	}

	/**
	 * Remove parameter
	 * 
	 * @param key
	 *            parameter name to remove
	 */
	public void remove(String key) {
		config.remove(key);
	}

	/**
	 * Remove item from parameter value list
	 * 
	 * @param key
	 *            parameter name
	 * @param val
	 *            item from values list to delete
	 */
	public void remove(String key, String val) {
		List<String> ss = config.get(key);
		if (ss == null)
			return;
		else {
			ss.remove(val);
			if (ss.size() == 0)
				config.remove(key);
		}
	}

	/**
	 * Get value named key as a string
	 * 
	 * @param key
	 *            parameter name
	 * @return first item from parameter value list, null if nothing defined
	 */
	public String getString(String key) {
		List<String> ss = config.get(key);
		if (ss == null || ss.size() == 0)
			return null;
		else
			return ss.get(0);
	}

	/**
	 * Get value names key as a string. If key has no value set provided default
	 * and return it.
	 * 
	 * @param key
	 *            parameter name
	 * @param defaultValue
	 *            default parameter value
	 * @return first item from parameter list
	 * 
	 */
	public String getString(String key, String defaultValue) {
		String s = getString(key);
		if (s == null) {
			put(key, defaultValue);
			s = defaultValue;
		}

		return s;
	}

	/**
	 * Get parameter value as boolean
	 * 
	 * @param key
	 *            parameter name
	 * @return value of given parameter
	 * @throws RuntimeException
	 *             if parameter absent or can't be parsed.
	 */
	public boolean getBoolean(String key) {
		String value = getString(key);
		if (value == null) {
			throw new RuntimeException("Illegal " + key + ":null");
		}
		if ((!value.toLowerCase().equals("true") && value.toLowerCase().equals("t")
				&& !value.toLowerCase().equals("false") && value.toLowerCase().equals("f") && !value.equals("1")
				&& !value.equals("0"))) {
			throw new RuntimeException("Illegal " + key + ":" + value);
		}
		boolean pv = Types.stringToBoolean(value);
		return pv;
	}

	/**
	 * Get parameter value as boolean
	 * 
	 * @param key
	 *            parameter name
	 * @param defaultValue
	 *            defaultValue
	 * @return value of given parameter
	 * @throws RuntimeException
	 *             if parameter absent or can't be parsed.
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		boolean b;
		try {
			b = getBoolean(key);
		} catch (RuntimeException e) {
			put(key, String.valueOf(defaultValue));
			b = defaultValue;
		}

		return b;
	}

	/**
	 * Get parameter value as boolean
	 * 
	 * @param key
	 *            parameter name
	 * @return value of given parameter
	 * @throws RuntimeException
	 *             if parameter absent or can't be parsed.
	 */
	public double getDouble(String key) {
		String value = getString(key);
		if (value == null) {
			throw new RuntimeException("Illegal " + key + ":null");
		}

		double d = Double.parseDouble(value);

		return d;
	}

	/**
	 * Get parameter value as boolean
	 * 
	 * @param key
	 *            parameter name
	 * @param defaultValue
	 *            defaultValue
	 * @return value of given parameter
	 * @throws RuntimeException
	 *             if parameter absent or can't be parsed.
	 */
	public double getDouble(String key, double defaultValue) {
		double d;
		try {
			d = getDouble(key);
		} catch (Exception e) {
			put(key, String.valueOf(defaultValue));
			d = defaultValue;
		}

		return d;
	}

	/**
	 * Get parameter value as boolean
	 * 
	 * @param key
	 *            parameter name
	 * @return value of given parameter
	 * @throws RuntimeException
	 *             if parameter absent or can't be parsed.
	 */
	public int getInt(String key) {
		String value = getString(key);
		if (value == null) {
			throw new RuntimeException("Illegal " + key + ":null");
		}

		int i = Integer.parseInt(value);

		return i;
	}

	/**
	 * Get parameter value as boolean
	 * 
	 * @param key
	 *            parameter name
	 * @param defaultValue
	 *            defaultValue
	 * @return value of given parameter
	 * @throws RuntimeException
	 *             if parameter absent or can't be parsed.
	 */
	public int getInt(String key, int defaultValue) {
		int i;
		try {
			i = getInt(key);
		} catch (Exception e) {
			put(key, String.valueOf(defaultValue));
			i = defaultValue;
		}

		return i;
	}

	/**
	 * Get subconfiguration named prefix
	 * 
	 * @param prefix
	 *            prefix to filter parameters
	 * @return ConfigFile containing part of parameters beginning with given
	 *         prefix
	 */
	public ConfigFile getSubConfig(String prefix) {
		return getSubConfig(prefix, false);
	}

	/**
	 * Get subconfiguration named prefix. Possibly seeded with values from this
	 * config
	 * 
	 * @param prefix
	 *            prefix to filter parameters
	 * @param inherit
	 *            if true inherit values from this config
	 * @return ConfigFile containing part of parameters beginning with given
	 *         prefix
	 */
	public ConfigFile getSubConfig(String prefix, boolean inherit) {
		ConfigFile result;

		if (inherit)
			result = this.clone();
		else
			result = new ConfigFile();

		result.name = prefix;
		for (String key : config.keySet()) {
			if (key.startsWith(prefix) && key.length() > prefix.length()) {
				String newKey = key.substring(prefix.length() + 1);
				result.getConfig().put(newKey, config.get(key));
			}
		}
		return result;
	}

	/**
	 * Getter for configuration map
	 * 
	 * @return map parameter name - list of parameter's values
	 */
	public Map<String, List<String>> getConfig() {
		return config;
	}

	/**
	 * Getter for configuration file name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for configuration file name
	 * 
	 * @param n
	 *            name
	 */
	public void setName(String n) {
		if (n.endsWith(".config"))
			name = n.substring(0, n.indexOf(".config"));
		else
			name = n;
	}

	/**
	 * Getter for string representation
	 * 
	 * @return string representation of configuration
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String key : config.keySet()) {
			List<String> list = config.get(key);
			if (list.size() == 1)
				sb.append(key + "=" + list.get(0) + "\n");
			else {
				sb.append(key + "=[list]\n");
				for (String s : list)
					sb.append("\t" + s + "\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Writes configuration to file
	 * 
	 * @param fn
	 *            file name to dump
	 */
	public void writeToFile(String fn) {
		// TODO: cleanup
		setName(fn);
		Set<String> keySet = config.keySet();
		Iterator<String> it = keySet.iterator();
		ArrayList<String> v = new ArrayList<String>();
		while (it.hasNext())
			v.add((String) it.next());

		String[] keys = new String[v.size()];
		for (int i = 0; i < keys.length; i++)
			keys[i] = v.get(i);

		Arrays.sort(keys);

		try {
			File f = new File(fn);
			File bak = new File(fn + ".bak");
			if (bak.exists())
				bak.delete();
			if (f.exists())
				f.renameTo(bak);

			PrintWriter out = new PrintWriter(new FileWriter(fn));
			for (int i = 0; i < keys.length; i++) {
				String k = keys[i];
				Object o = config.get(k);
				@SuppressWarnings("unchecked")
				List<String> vals = (List<String>) o;
				Iterator<String> it2 = vals.iterator();
				while (it2.hasNext())
					out.println(k + "=" + it2.next());
				out.println();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find the first readable file in a list of names
	 * 
	 * @param fileNames
	 * @return
	 * 
	 */
	public static String findConfig(List<String> fileNames) {
		String fileName = null;
		String name;

		Iterator<String> nameIter = fileNames.iterator();
		while (fileName == null && nameIter.hasNext()) {
			name = nameIter.next();
			if (new File(name).canRead())
				fileName = name;
		}

		return fileName;
	}


	public ConfigFile clone() {
		ConfigFile cf = new ConfigFile();

		for (String key : config.keySet())
			cf.putList(key, config.get(key));

		return cf;
	}
}
