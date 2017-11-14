package gov.usgs.volcanoes.core.legacy.plot.map;



import gov.usgs.volcanoes.core.math.proj.GeoRange;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Manages lists of <code>GeoLabel</code>s</p>
 *
 * @author Dan Cervelli
 */
public class GeoLabelSet {
  private List<GeoLabel> geoLabels;

  /**
   * Default constructor
   */
  public GeoLabelSet() {
    geoLabels = new ArrayList<GeoLabel>();
  }

  /**
   * Constructor
   * @param list List of GeoLabels to initialize
   */
  public GeoLabelSet(List<GeoLabel> list) {
    geoLabels = list;
  }

  /**
   * Constructor
   * @param indexFilename file with list of GeoLabel descriptions, one per line
   */
  public GeoLabelSet(String indexFilename) {
    this();
    // TODO: replace with appendFile after converting Valve3 labels format
    try {
      BufferedReader in = new BufferedReader(new FileReader(indexFilename));
      String s = null;
      while ((s = in.readLine()) != null) {
        s = s.trim();
        if (s.length() > 0 && !s.startsWith("#")) {
          GeoLabel l = new GeoLabel(s);
          geoLabels.add(l);
        }
      }
      in.close();
    } catch (Exception e) {
      // TODO: log, convert to factory method
      e.printStackTrace();
    }
  }

  /**
   * Append labels described by index file to list
   * @param indexFilename name of index file
   */
  public void appendFile(String indexFilename) {
    try {
      BufferedReader in = new BufferedReader(new FileReader(indexFilename));
      String s = null;
      while ((s = in.readLine()) != null) {
        s = s.trim();
        if (s.length() > 0 && !s.startsWith("#")) {
          GeoLabel l = GeoLabel.fromString(s);
          if (l != null)
            geoLabels.add(l);
        }
      }
      in.close();
    } catch (Exception e) {
      // TODO: log, convert to factory method
      e.printStackTrace();
    }
  }

  /**
   * Add one GeoLabel
   * @param gl GeoLabel to add
   */
  public void add(GeoLabel gl) {
    geoLabels.add(gl);
  }

  /**
   * Getter for whole labels list
   * @return list of GeoLabels
   */
  public List<GeoLabel> getGeoLabels() {
    return geoLabels;
  }

  /**
   * Get subset of labels for given geographic range
   * @param range range to get labels forage
   * @return set of labels for range
   */
  public GeoLabelSet getSubset(GeoRange range) {
    ArrayList<GeoLabel> result = new ArrayList<GeoLabel>();
    for (GeoLabel l : geoLabels) {
      if (range.contains(l.location))
        result.add(l);
    }

    return new GeoLabelSet(result);
  }

}
