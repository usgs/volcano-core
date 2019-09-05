package gov.usgs.volcanoes.core.legacy.plot.color;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Translate string color name to appropriate Color class.</p>
 * 
 * <p>@author Dan Cervelli</p>
 */
public class ColorParser {
  private static final Map<String, Color> colorMap = new HashMap<String, Color>();

  static {
    colorMap.put("red", Color.RED);
    colorMap.put("green", Color.GREEN);
    colorMap.put("blue", Color.BLUE);
    colorMap.put("yellow", Color.YELLOW);
    colorMap.put("orange", Color.ORANGE);
    colorMap.put("pink", Color.PINK);
    colorMap.put("white", Color.WHITE);
    colorMap.put("black", Color.BLACK);
    colorMap.put("cyan", Color.CYAN);
    colorMap.put("darkgray", Color.DARK_GRAY);
    colorMap.put("gray", Color.GRAY);
    colorMap.put("lightgray", Color.LIGHT_GRAY);
    colorMap.put("magenta", Color.MAGENTA);
  }

  /**
   * Translate string color name to appropriate Color class.
   * @param cs color name
   * @return Initialized class for color
   */
  public static Color getColor(String cs) {
    Color c = null;
    cs = cs.toLowerCase();

    if (cs.startsWith("#")) {
      // parse html color
      String hc = cs.substring(1);
      int r = -1;
      int g = -1;
      int b = -1;
      try {
        if (hc.length() == 3) {
          r = Integer.parseInt(hc.substring(0, 1), 16) * 16;
          g = Integer.parseInt(hc.substring(1, 2), 16) * 16;
          b = Integer.parseInt(hc.substring(2, 3), 16) * 16;
        } else if (hc.length() == 6) {
          r = Integer.parseInt(hc.substring(0, 2), 16);
          g = Integer.parseInt(hc.substring(2, 4), 16);
          b = Integer.parseInt(hc.substring(4, 6), 16);
        }
      } catch (Exception e) {
        // TODO
      }
      if (r == -1 || g == -1 || b == -1) {
        c = null;
      } else {
        c = new Color(r, g, b);
      }
    } else if (cs.indexOf(",") != -1) {
      // parse r,g,b
    } else {
      c = colorMap.get(cs);
    }

    return c;
  }

  /**
   * Main method, prints colos for names in the command line to stdout.
   */
  public static void main(String[] args) {
    for (String s : args) {
      System.out.println(getColor(s));
    }
  }
}
