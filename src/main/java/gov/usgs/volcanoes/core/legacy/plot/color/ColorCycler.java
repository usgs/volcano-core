package gov.usgs.volcanoes.core.legacy.plot.color;

import java.awt.Color;

/**
 * A simple utility class to cycle through colors. This is used by the Valve
 * grapher to cycle through colors of graph lines.
 *
 * @author  Dan Cervelli
 */
public class ColorCycler {
  /** 
   * Custom color declarations.
   */
  private static Color VALVE_BROWN = new Color(165, 42, 42);
  private static Color VALVE_GOLD = new Color(218, 165, 32);
  private static Color VALVE_CORAL = new Color(255, 114, 86);

  /** The colors in the cycle.
   */
  private static final Color[] colorCycle =
      new Color[] {Color.BLUE, Color.GREEN, VALVE_GOLD, VALVE_BROWN, VALVE_CORAL, Color.RED};

  private int currentColor;

  /** Generic constructor.
   */
  public ColorCycler() {
    currentColor = 0;
  }

  /**
   * Constructor.
   * @param start Starting color number (see colorCycle array)
   */
  public ColorCycler(int start) {
    currentColor = start;
  }

  /** Gets the next color in the cycle.
   * @return the next color
   */
  public Color getNextColor() {
    return colorCycle[currentColor++ % colorCycle.length];
  }

  /** 
   * Resets the color cycler.
   */
  public void reset() {
    currentColor = 0;
  }
}
