package gov.usgs.volcanoes.core.legacy.plot.map;

import gov.usgs.volcanoes.core.legacy.plot.render.DataPointRenderer;
import gov.usgs.volcanoes.core.legacy.plot.render.PointRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

/**
 * <p>
 * Labels point on the map.
 * </p>
 * 
 * TODO: add more constructors TODO: privatize
 * 
 * @author Dan Cervelli
 */
public class GeoLabel {
  public String text;
  public Color color = Color.BLACK;
  public Font font = new Font("Arial", Font.PLAIN, 10);
  public boolean shadow = true;
  public Point2D.Double location;
  public PointRenderer marker;

  // public Stroke stroke = new BasicStroke(1.0f);

  /**
   * Default constructor
   */
  public GeoLabel() {}

  /**
   * Creates <code>GeoLabel</code> from tab-separated string (name\tlon\tlat).
   * Renders point as 6-pixel size triangle.
   * 
   * @param s
   *            the source string
   */
  public GeoLabel(String s) {
    String[] ss = s.split("\t");
    text = ss[0];
    location = new Point2D.Double(Double.parseDouble(ss[1]), Double.parseDouble(ss[2]));
    marker = new DataPointRenderer('t', 6);
    ((DataPointRenderer) marker).antiAlias = true;
    ((DataPointRenderer) marker).stroke = new BasicStroke(1.0f);
  }

  /**
   * Constructor. Renders point as 3x3 square.
   * 
   * @param s
   *            label text
   * @param lon
   *            longitude
   * @param lat
   *            latitude
   */
  public GeoLabel(String s, double lon, double lat) {
    text = s;
    location = new Point2D.Double(lon, lat);
    marker = new DataPointRenderer('s', 3);
    ((DataPointRenderer) marker).antiAlias = true;
  }

  /**
   * Create GeoLabel from semicolon-separated string text/options. Options
   * itself is comma-separated string longitude/latitude/shape type. Size of
   * point is 6.
   * 
   * Three formats are known. 
   * 
   * labelText;lon,lat 
   * 
   * labelText;lon,lat,type
   * 
   * labelText;lon,lat,type,size,color,stroke,fontName,fontStyle,fontSize,color
   */
  public static GeoLabel fromString(String s) {
    char pType = 's';
    float pSize = 8;
    Color pColor = Color.BLUE;
    float pStroke = 1.0f;
    Font font = new Font("Arial", Font.PLAIN, 10);
    Color fColor = Color.BLACK;

    GeoLabel gl = new GeoLabel();
    String[] ss = s.split(";");
    String[] options = ss[1].split(",");

    gl.text = ss[0];
    gl.location = new Point2D.Double(Double.parseDouble(options[0].trim()),
        Double.parseDouble(options[1].trim()));

    if (options.length > 2)
      pType = options[2].trim().charAt(0);

    if (options.length > 3) {
      pSize = Float.parseFloat(options[3].trim());
      try {
        pColor = (Color) Color.class.getDeclaredField(options[4].trim()).get(null);
        pStroke = Float.parseFloat(options[5].trim());
        font = new Font(options[6].trim(),
            (int) Font.class.getDeclaredField(options[8].trim()).getInt(null),
            Integer.parseInt(options[7].trim()));
        fColor = (Color) Color.class.getDeclaredField(options[9].trim()).get(null);
      } catch (Exception e) {
        System.err.println("Can't parse geolable string for " + gl.text + " using defaults.");
      }
    }

    DataPointRenderer dr = new DataPointRenderer(pType, pSize);
    dr.color = pColor;
    dr.stroke = new BasicStroke(pStroke);
    dr.antiAlias = true;

    gl.font = font;
    gl.color = fColor;
    gl.shadow = false;
    gl.marker = dr;

    return gl;
  }
}
