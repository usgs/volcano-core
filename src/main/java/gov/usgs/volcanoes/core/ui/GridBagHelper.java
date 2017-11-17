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
 * <p>a=n|e|s|w|ne|nw|se|sw|c 
 * anchor north, east, south, west, northeast, northwest, southeast, southwest, center</p>
 * 
 * @author Dan Cervelli
 */
public class GridBagHelper {
  /**
   * Default constructor .
   */
  private GridBagHelper() {}

  /**
   * Parse.
   * @param constraints GridBagConstraints to process
   * @param cmd command of 'key=value' type
   */
  private static void parseCommand(GridBagConstraints constraints, String cmd) {
    String key = cmd.substring(0, cmd.indexOf("="));
    String value = cmd.substring(cmd.indexOf("=") + 1);
    if (key.equals("x")) {
      constraints.gridx = Integer.parseInt(value);
    } else if (key.equals("y")) {
      constraints.gridy = Integer.parseInt(value);
    } else if (key.equals("w")) {
      constraints.gridwidth = Integer.parseInt(value);
    } else if (key.equals("h")) {
      constraints.gridheight = Integer.parseInt(value);
    } else if (key.equals("wx")) {
      constraints.weightx = Double.parseDouble(value);
    } else if (key.equals("wy")) {
      constraints.weighty = Double.parseDouble(value);
    } else if (key.equals("ix")) {
      constraints.ipadx = Integer.parseInt(value);
    } else if (key.equals("iy")) {
      constraints.ipady = Integer.parseInt(value);
    } else if (key.equals("i")) {
      StringTokenizer st2 = new StringTokenizer(value, ",");
      int i1 = Integer.parseInt(st2.nextToken());
      int i2 = Integer.parseInt(st2.nextToken());
      int i3 = Integer.parseInt(st2.nextToken());
      int i4 = Integer.parseInt(st2.nextToken());
      constraints.insets = new Insets(i1, i2, i3, i4);
    } else if (key.equals("f")) {
      if (value.equals("b")) {
        constraints.fill = GridBagConstraints.BOTH;
      } else if (value.equals("h")) {
        constraints.fill = GridBagConstraints.HORIZONTAL;
      } else if (value.equals("v")) {
        constraints.fill = GridBagConstraints.VERTICAL;
      } else if (value.equals("n")) {
        constraints.fill = GridBagConstraints.NONE;
      } else if (key.equals("a")) {
        if (value.equals("n")) {
          constraints.anchor = GridBagConstraints.NORTH;
        } else if (value.equals("e")) {
          constraints.anchor = GridBagConstraints.EAST;
        }
      } else if (value.equals("s")) {
        constraints.anchor = GridBagConstraints.SOUTH;
      } else if (value.equals("w")) {
        constraints.anchor = GridBagConstraints.WEST;
      } else if (value.equals("ne")) {
        constraints.anchor = GridBagConstraints.NORTHEAST;
      } else if (value.equals("nw")) {
        constraints.anchor = GridBagConstraints.NORTHWEST;
      } else if (value.equals("se")) {
        constraints.anchor = GridBagConstraints.SOUTHEAST;
      } else if (value.equals("sw")) {
        constraints.anchor = GridBagConstraints.SOUTHWEST;
      } else if (value.equals("c")) {
        constraints.anchor = GridBagConstraints.CENTER;
      }
    }
  }

  /**
   * Processing method.
   * @param constraints GridBagConstraints to process
   * @param cmdStr command string to parse
   * @return initialized GridBagConstraints
   */
  public static GridBagConstraints set(GridBagConstraints constraints, String cmdStr) {
    StringTokenizer st = new StringTokenizer(cmdStr, ";");
    while (st.hasMoreTokens()) {
      String cmd = st.nextToken();
      parseCommand(constraints, cmd);
    }
    return constraints;
  }
}
