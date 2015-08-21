package gov.usgs.volcanoes.util.types;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * 
 * @author Tom Parker
 * @author Dan Cervelli
 */
public class Types
{
	public static final char DEGREE_SYMBOL = (char)0xb0;
	
	public static final long ONE_KB = 1024;
	public static final long ONE_MB = 1024 * ONE_KB;
	public static final long ONE_GB = 1024 * ONE_MB;
	public static final long ONE_TB = 1024 * ONE_GB;
	public static final long ONE_PB = 1024 * ONE_TB;
	
	/**
	 * Converts integer to byte value
	 * @param i integer to convert
	 * @return converted byte
	 */
	public static byte intToByte(int i)
	{
		return (byte)(i & 0xff);
	}

	/**
	 * Converts byte to integer value
	 * @param b byte to convert
	 * @return converted int
	 */
	public static int byteToInt(byte b)
	{
		return (int)b & 0xff;
	}

	/**
	 * Converts array of bytes into string on per-symbol basis, till first 0 value
	 * @param b byte array to convert
	 * @return converted string
	 */
	public static String bytesToString(byte[] b)
	{
		return bytesToString(b, 0, b.length);
	}
	
	/**
	 * Converts array of bytes into string on per-symbol basis, till first 0 value
	 * @param b byte array to convert
	 * @param o number of first byte to convert
	 * @param l length of converting part
	 * @return converted string
	 */
	public static String bytesToString(byte[] b, int o, int l)
	{
		int trunc = -1;
		for (int i = o; i < o + l; i++)
			if (b[i] == 0)
			{
				trunc = i;
				break;
			}
		if (trunc != -1)
			//return new String(b, o, trunc - o);
			return quickBytesToString(b, o, trunc - o);
		else
//			return new String(b, o, l);
			return quickBytesToString(b, o, l);
	}

	/**
	 * Converts array of bytes into string on per-symbol basis
	 * 
	 * @param b byte array to convert 
	 * @param ofs offset from first array member to start conversion
	 * @param len resulting string length
	 * @return converted string
	 */
	public static String quickBytesToString(byte[] b, int ofs, int len)
	{
		char[] chars = new char[len];
		for (int i = 0; i < chars.length; i++)
			chars[i] = (char)b[i + ofs];
		return new String(chars);
	}
	
	/**
	 * Converts a string to a boolean.  To avoid confusion there is no default
	 * value.  This simply returns true on a val of "1" or "true".
	 * 
	 * @param val the string that represents the boolean
	 * @return the boolean
	 */
	public static boolean stringToBoolean(String val)
	{
		if (val == null)
			return false;
		return (val.toLowerCase().equals("true") || val.equals("1") || val.toLowerCase().equals("t"));
	}
	
	/**
	 * Converts a string to a boolean.  Returns default value on null input.
	 * 
	 * @param val the string that represents the boolean
	 * @return the boolean
	 */
	public static boolean stringToBoolean(String val, boolean def)
	{
		if (val == null)
			return def;
		return (val.toLowerCase().equals("true") || val.equals("1") || val.toLowerCase().equals("t"));
	}
	
	/**
	 * Converts a string to an integer, sets to <code>Interger.MIN_VALUE</code>
	 * if there's an exception.
	 * 
	 * @param val the string that represents the integer
	 * @return the integer
	 */
	public static int stringToInt(String val)
	{
		return stringToInt(val, Integer.MIN_VALUE);
	}
	
	/**
	 * Converts a string to an integer, sets to user-specified default if 
	 * there's an exception.
	 * 
	 * @param val the string that represents the integer
	 * @param def the default value
	 * @return the integer
	 */
	public static int stringToInt(String val, int def)
	{
		int i = def;
		try
		{
			i = Integer.parseInt(val);
		}
		catch (Exception e)
		{}
		return i;
	}
	
	/**
	 * Converts a string to an integer, sets to <code>Interger.MIN_VALUE</code>
	 * if there's an exception.
	 * 
	 * @param val the string that represents the integer
	 * @return the integer
	 */
	public static Integer stringToInteger(String val)
	{
		return stringToInt(val, Integer.MIN_VALUE);
	}
	
	/**
	 * Converts a string to an integer, sets to user-specified default if 
	 * there's an exception.
	 * 
	 * @param val the string that represents the integer
	 * @param def the default value
	 * @return the integer
	 */
	public static Integer stringToInteger(String val, Integer def)
	{
		Integer i = def;
		try
		{
			i = Integer.valueOf(val);
		}
		catch (Exception e)
		{}
		return i;
	}
	
	/**
	 * Converts a string to a double, sets to <code>Double.NaN</code>
	 * if there's an exception.
	 * 
	 * @param val the string that represents the double
	 * @return the double
	 */
	public static double stringToDouble(String val)
	{
		return stringToDouble(val, Double.NaN);
	}
	
	/**
	 * Converts a string to a double, sets to user-specified default if 
	 * there's an exception.
	 * 
	 * @param val the string that represents the double
	 * @param def the default value
	 * @return the double
	 */
	public static double stringToDouble(String val, double def)
	{
		double d = def;
		try
		{
			d = Double.parseDouble(val);
		}
		catch (Exception e)
		{}
		return d;
	}
	
	/**
	 * Checks if a string is null and returns a default string if it is.
	 * 
	 * @param val the original string
	 * @param def the default in case of null
	 * @return the original string if != null, otherwise default string
	 */
	public static String stringToString(String val, String def)
	{
		return (val == null) ? def : val;
	}

	
	/**
	 * Converts map to string which consist from pairs key=value, divided by semicolon
	 * @param map to be made into string
	 * @return string representation
	 */
	public static String mapToString(Map<String, String> map)
	{
		String result = "";
		for (String key : map.keySet())
			result += key + "=" + map.get(key) + "; ";
		
		return result;
	}

	/**
	 * Converts string which consist from pairs key=value, divided by semicolon to map
	 * @param src string representation
	 * @return map
	 */
	public static Map<String, String> stringToMap(String src) 
	{
		Map<String, String> result = new HashMap<String, String>();
		String[] params = src.split(";");
		for (int i = 0; i < params.length; i++)
		{
			int ei = params[i].indexOf("=");
			if (ei == -1)
				continue;
			String k = params[i].substring(0, ei).trim();
			String v = params[i].substring(ei + 1);
			result.put(k, v);
		}
		return result;
	}
	
	/**
	 * Converts byte to hex string
	 * @param b byte
	 * @return hex string
	 */
	public static String byteToHex(byte b)
	{
		String h = Integer.toHexString((int)b & 0xff);
		if (h.length() == 1)
			h = "0" + h;
		return h;
	}

	/**
	 * Converts array of bytes to hex string
	 * @param buf byte array
	 * @return hex string
	 */
	public static String bytesToHex(byte[] buf)
	{
		StringBuilder sb = new StringBuilder(buf.length * 2 + 1);
		for (int i = 0; i < buf.length; i++)
			sb.append(byteToHex(buf[i]));
		
		return sb.toString();
	}

	/**
	 * Converts hex string to array of bytes
	 * @param s hex string
	 * @return array of bytes
	 */
	public static byte[] hexToBytes(String s)
	{
		int n = s.length() / 2;
		byte[] buf = new byte[n];
		for (int i = 0; i < n; i++)
		{
			String ss = s.substring(i * 2, i * 2 + 2);
			int j = Integer.parseInt(ss, 16);
			buf[i] = (byte)j;
		}
		return buf;
	}
}
