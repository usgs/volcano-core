package gov.usgs.volcanoes.core.ui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.StringTokenizer;

/**
 * <p>A class to help with the arduous task of using the <code>GridBagLayout</code>.
 * Parses semicolon-separated list of commands 'key=value' and construct 
 * GridBagConstraints object.</p>
 * 
 * <p>Command syntax is:</p>
 * <p>x=... gridx value </p>
 * <p>y=... gridy value</p>
 * <p>w=... gridwidth value</p>
 * <p>h=... gridheight value</p>
 * <p>wx=...weightx value</p>
 * <p>wy=... weighty value</p>
 * <p>ix=... ipadx value</p>
 * <p>iy=... ipady value</p>
 * <p>i=...,...,...,...  top inset, left inset, bottom inset, right inset</p>
 * <p>f=b|h|v|n fill both, horisontal, vertical, none</p>
 * <p>a=n|e|s|w|ne|nw|se|sw|c anchor north, east, south, west, northeast, northwest, southeast, southwest, center</p>
 * 
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/04/15 04:44:38  cervelli
 * Comments.
 *
 * @author Dan Cervelli
 */
public class GridBagHelper
{
	/**
	 * Default constructor 
	 */
	private GridBagHelper() {}
	
	/**
	 * @param c GridBagConstraints to process
	 * @param cmd command of 'key=value' type
	 */
	private static void parseCommand(GridBagConstraints c, String cmd)
	{
		String key = cmd.substring(0, cmd.indexOf("="));
		String value = cmd.substring(cmd.indexOf("=") + 1);
		if (key.equals("x"))
			c.gridx = Integer.parseInt(value);
		else if (key.equals("y"))
			c.gridy = Integer.parseInt(value);
		else if (key.equals("w"))
			c.gridwidth = Integer.parseInt(value);
		else if (key.equals("h"))
			c.gridheight = Integer.parseInt(value);
		else if (key.equals("wx"))
			c.weightx = Double.parseDouble(value);
		else if (key.equals("wy"))
			c.weighty = Double.parseDouble(value);
		else if (key.equals("ix"))
			c.ipadx = Integer.parseInt(value);
		else if (key.equals("iy"))
			c.ipady = Integer.parseInt(value);
		else if (key.equals("i"))
		{
			StringTokenizer st2 = new StringTokenizer(value, ",");
			int i1 = Integer.parseInt(st2.nextToken());
			int i2 = Integer.parseInt(st2.nextToken());
			int i3 = Integer.parseInt(st2.nextToken());
			int i4 = Integer.parseInt(st2.nextToken());
			c.insets = new Insets(i1, i2, i3, i4);
		}
		else if (key.equals("f"))
		{
			if (value.equals("b"))
				c.fill = GridBagConstraints.BOTH;
			else if (value.equals("h"))
				c.fill = GridBagConstraints.HORIZONTAL;
			else if (value.equals("v"))
				c.fill = GridBagConstraints.VERTICAL;
			else if (value.equals("n"))
				c.fill = GridBagConstraints.NONE;
		}
		else if (key.equals("a"))
		{
			if (value.equals("n"))
				c.anchor = GridBagConstraints.NORTH;
			else if (value.equals("e"))
				c.anchor = GridBagConstraints.EAST;
			else if (value.equals("s"))
				c.anchor = GridBagConstraints.SOUTH;
			else if (value.equals("w"))
				c.anchor = GridBagConstraints.WEST;
			else if (value.equals("ne"))
				c.anchor = GridBagConstraints.NORTHEAST;
			else if (value.equals("nw"))
				c.anchor = GridBagConstraints.NORTHWEST;
			else if (value.equals("se"))
				c.anchor = GridBagConstraints.SOUTHEAST;
			else if (value.equals("sw"))
				c.anchor = GridBagConstraints.SOUTHWEST;
			else if (value.equals("c"))
				c.anchor = GridBagConstraints.CENTER;
		}
	}

	/**
	 * Processing method
	 * @param c GridBagConstraints to process
	 * @param s command string to parse
	 * @return initialized GridBagConstraints
	 */
	public static GridBagConstraints set(GridBagConstraints c, String s)
	{
		StringTokenizer st = new StringTokenizer(s, ";");
		while (st.hasMoreTokens())
		{
			String cmd = st.nextToken();
			parseCommand(c, cmd);
		}
		return c;
	}
}