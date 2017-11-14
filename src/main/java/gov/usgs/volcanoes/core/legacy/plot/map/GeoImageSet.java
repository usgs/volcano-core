package gov.usgs.volcanoes.core.legacy.plot.map;

import gov.usgs.volcanoes.core.CodeTimer;
import gov.usgs.volcanoes.core.legacy.plot.Plot;
import gov.usgs.volcanoes.core.math.proj.GeoRange;
import gov.usgs.volcanoes.core.math.proj.Mercator;
import gov.usgs.volcanoes.core.math.proj.Projection;
import gov.usgs.volcanoes.core.math.proj.TransverseMercator;
import gov.usgs.volcanoes.core.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JFrame;

/**
 * <p>A class for managing a set of <code>GeoImage</code>s.</p>
 * 
 * @author Dan Cervelli
 */
public class GeoImageSet {
  private static final int ONE_MEGABYTE = 1024 * 1024;
  private static final double AREAL_THRESHOLD = 0.08;
  private List<GeoImage> images;
  private List<GeoImageCacheEntry> loadedImages;

  private int maxLoadedImagesSize = 32 * ONE_MEGABYTE;
  protected static final Logger LOGGER = LoggerFactory.getLogger(GeoImageSet.class);

  private boolean arealCacheSort = true;

  /**
   * Default constructor
   */
  public GeoImageSet() {
    images = new ArrayList<GeoImage>();
    loadedImages = new LinkedList<GeoImageCacheEntry>();
  }

  /**
   * Constructor
   * @param indexFilename file with <code>GeoImage</code> parameter strings list, one per line
   */
  public GeoImageSet(String indexFilename) {
    this();
    try {
      BufferedReader in = new BufferedReader(new FileReader(indexFilename));
      String s = null;
      while ((s = in.readLine()) != null) {
        s = s.trim();
        if (s.length() > 0 && !s.startsWith("#")) {
          GeoImage gi = new GeoImage(s);
          images.add(gi);
        }
      }
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // TODO: move some of this to GeoLabelSet
  /**
   * Load map pack descriptions from filesystem, using 'MapPack.txt' 
   * for GeoImages and 'Labels.txt' for labels 
   * @param root base directory to search
   */
  public static Pair<GeoImageSet, GeoLabelSet> loadMapPacks(String root) {
    File[] files = new File(root).listFiles();
    if (files == null)
      return null;

    GeoImageSet gis = new GeoImageSet();
    GeoLabelSet gls = new GeoLabelSet();

    for (File f : files) {
      if (f.isDirectory()) {
        File mp = new File(f.getPath() + File.separatorChar + "MapPack.txt");
        if (mp.exists()) {
          LOGGER.info("loading MapPack: {}", mp.getPath());
          try {
            BufferedReader in = new BufferedReader(new FileReader(mp));
            String s = null;
            while ((s = in.readLine()) != null) {
              s = s.trim();
              if (s.length() > 0 && !s.startsWith("#")) {
                GeoImage gi = new GeoImage(f.getPath(), s);
                gis.images.add(gi);
              }
            }
            in.close();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        File lf = new File(f.getPath() + File.separatorChar + "Labels.txt");
        if (lf.exists()) {
          gls.appendFile(lf.getPath());
        }
      }
    }
    return new Pair<GeoImageSet, GeoLabelSet>(gis, gls);
  }

  /**
   * Class is used to cashing GeoImages. Contains link
   * to GeoImage and it's last access time. Also provide sorting capabilities.
   *
   */
  private class GeoImageCacheEntry implements Comparable<GeoImageCacheEntry> {
    public GeoImage image;
    public long lastAccess;

    public GeoImageCacheEntry(GeoImage img) {
      image = img;
      touch();
    }

    /**
     * Sorts by rectangular lon/lat area; if the difference is negligible then
     * sorts by last accessed time.
     * @param oce the other cache entry
     * @return the comparison result
     */
    public int compareTo(GeoImageCacheEntry oce) {
      if (arealCacheSort) {
        double a1 = image.getLonLatArea();
        double a2 = oce.image.getLonLatArea();
        int val = (int) ((a1 - a2) * 100000);
        if (val > 100)
          return val;
      }

      return (int) (lastAccess - oce.lastAccess);
    }

    public int getMemorySize() {
      return image.getPixelWidth() * image.getPixelHeight();
    }

    public void touch() {
      lastAccess = System.currentTimeMillis();
    }

    public boolean equals(Object o) {
      if (o instanceof GeoImageCacheEntry) {
        GeoImageCacheEntry ce = (GeoImageCacheEntry) o;
        return (image.getFilename().equals(ce.image.getFilename()));
      } else if (o instanceof GeoImage) {
        GeoImage gi = (GeoImage) o;
        return (image.getFilename().equals(gi.getFilename()));
      } else
        return false;
    }
  }

  public void setArealCacheSort(boolean b) {
    arealCacheSort = b;
  }

  /**
   * Sets the maximum size of cached images.
   * 
   * @param mp maximum loaded images size in megapixels (approximately)
   */
  public void setMaxLoadedImagesSize(int mp) {
    maxLoadedImagesSize = mp * ONE_MEGABYTE;
  }

  /**
   * Gets the size of cached images.
   */
  private int getLoadedImagesSize() {
    int size = 0;
    for (Iterator<GeoImageCacheEntry> it = loadedImages.iterator(); it.hasNext();)
      size += (it.next()).getMemorySize();

    return size;
  }

  /**
   * Clears cache
   */
  private void purgeLoadedImages(List<GeoImage> avoid) {
    Collections.sort(loadedImages);

    int needToDelete = getLoadedImagesSize() - maxLoadedImagesSize;

    Iterator<GeoImageCacheEntry> it = loadedImages.iterator();
    while (it.hasNext() && needToDelete > 0) {
      GeoImageCacheEntry ce = it.next();
      if (avoid == null || !avoid.contains(ce.image)) {
        LOGGER.debug("GeoImageSet Purge: {}", ce.image);
        it.remove();
        needToDelete -= ce.getMemorySize();
        ce.image.disposeImage();
      } else
        LOGGER.debug("SKIPPED DUE TO AVOID");
    }

    if (needToDelete > 0) {
      LOGGER.info("overfull GeoImageCache by {} bytes", needToDelete);
    }
  }

  private void addLoadedImage(GeoImage image, List<GeoImage> avoidPurging) {
    GeoImageCacheEntry ce = null;
    for (Iterator<?> it = loadedImages.iterator(); it.hasNext();) {
      Object o = it.next();
      if (o.equals(image)) {
        ce = (GeoImageCacheEntry) o;
        break;
      }
    }
    if (ce == null) {
      ce = new GeoImageCacheEntry(image);
      loadedImages.add(ce);
    } else
      ce.touch();

    if (getLoadedImagesSize() > maxLoadedImagesSize)
      purgeLoadedImages(avoidPurging);
  }

  /**
   * Create one composite image using list as data source
   */
  public synchronized GeoImage getCompositeImage(GeoRange range, int ppdLon, int ppdLat) {
    return getCompositeImage(range, ppdLon, ppdLat, Double.NaN);
  }

  private class ImageTranslation implements Comparable<ImageTranslation> {
    public GeoImage image;
    public Rectangle2D.Double rect;
    public double tx;
    public double ty;
    public double sx;
    public double sy;

    public ImageTranslation(GeoImage im, Rectangle2D.Double r, double tx, double ty, double sx,
        double sy) {
      image = im;
      rect = r;
      this.tx = tx;
      this.ty = ty;
      this.sx = sx;
      this.sy = sy;
    }

    public int compareTo(ImageTranslation o) {
      return image.compareTo(o.image);
    }
  }

  /**
   * Create one composite image using list as data source
   */
  public synchronized GeoImage getCompositeImage(GeoRange range, int ppdLon, int ppdLat,
      double scale) {
    CodeTimer ct = new CodeTimer("getCompositeImage");
    double width = range.getLonRange() * (double) ppdLon;
    double height = range.getLatRange() * (double) ppdLat;
    double area = width * height;
    Rectangle2D.Double mask = new Rectangle2D.Double(0, 0, (int) width, (int) height);
    BufferedImage buffer =
        new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);

    ArrayList<ImageTranslation> txs = new ArrayList<ImageTranslation>();
    for (GeoImage gi : images) {
      GeoRange imageRange = gi.getRange();
      if (imageRange.overlaps(range)) {
        double xs = imageRange.getLonRange() * ppdLon;
        double ys = imageRange.getLatRange() * ppdLat;
        double subsetArea = xs * ys;

        double a = subsetArea / area;
        boolean added = false;
        if (Double.isNaN(scale)) {
          if (a > AREAL_THRESHOLD)
            added = true;
        } else {
          if (gi.inScale(scale, a))
            added = true;
        }

        if (added) {
          double lr = (imageRange.getWest() - range.getWest());
          double tx = lr * ppdLon;
          double ty = (range.getNorth() - imageRange.getNorth()) * ppdLat;
          double sx = 1.0 / (gi.getPixelsPerLon() / ppdLon);
          double sy = 1.0 / (gi.getPixelsPerLat() / ppdLat);
          double w = gi.getPixelWidth() * sx;
          double h = gi.getPixelHeight() * sy;
          Rectangle2D.Double r1 = new Rectangle2D.Double(tx, ty, w, h);
          r1 = (Rectangle2D.Double) r1.createIntersection(mask);
          if (r1.width > 0) {
            ImageTranslation it1 = new ImageTranslation(gi, r1, tx, ty, sx, sy);
            txs.add(it1);
          }

          lr += 360;
          tx = lr * ppdLon;
          Rectangle2D.Double r2 = new Rectangle2D.Double(tx, ty, w, h);
          r2 = (Rectangle2D.Double) r2.createIntersection(mask);
          if (r2.width > 0) {
            ImageTranslation it2 = new ImageTranslation(gi, r2, tx, ty, sx, sy);
            txs.add(it2);
          }
        }
      }
    }

    Area a = new Area();
    Collections.sort(txs);
    ListIterator<ImageTranslation> lit = txs.listIterator(txs.size());
    while (lit.hasPrevious()) {
      ImageTranslation it = lit.previous();
      if (a.contains(it.rect))
        lit.remove();
      else
        a.add(new Area(it.rect));
    }



    List<GeoImage> gis = new ArrayList<GeoImage>(txs.size());
    for (ImageTranslation it : txs) {
      gis.add(it.image);
    }

    ct.mark("preload");
    Graphics2D g2 = (Graphics2D) buffer.getGraphics();
    for (ImageTranslation it : txs) {
      GeoImage gi = it.image;
      AffineTransform at = new AffineTransform();
      at.translate(it.tx, it.ty);
      at.scale(it.sx, it.sy);
      g2.drawRenderedImage(gi.getImage(), at);

      gis.remove(gi);
      addLoadedImage(gi, gis);
    }
    g2.dispose();

    GeoRange newRange = new GeoRange(range);
    GeoImage result = GeoImage.createMemoryImage(buffer, newRange);
    ct.stopAndReport();
    return result;
  }

  /** Shortcut for getMapBackground(proj, range, width, Double.NaN)
   * @param proj the Projection
   * @param range a GeoRange
   * @param width the width of the image
   * @return the map background as a rendered image
   */
  public synchronized RenderedImage getMapBackground(Projection proj, GeoRange range, int width) {
    return getMapBackground(proj, range, width, Double.NaN);
  }

  /** Get a map background as a rendered image
   * @param proj the Projection
   * @param range a GeoRange
   * @param width the width of the image
   * @param scale scale of image
   * @return the map background as a rendered image
   */
  public synchronized RenderedImage getMapBackground(Projection proj, GeoRange range, int width,
      double scale) {
    int grid = 20;
    width += grid - width % grid;
    LOGGER.debug("Range: {}", range);
    double[] extents = range.getProjectedExtents(proj);
    double aspect = (extents[3] - extents[2]) / (extents[1] - extents[0]);

    int height = (int) (width * aspect);
    height += grid - height % grid;
    int ppdLon = (int) ((double) width / range.getLonRange() * 1.2);
    int ppdLat = (int) ((double) height / range.getLatRange() * 1.2);
    ppdLon = (int) Math.max(ppdLon, 5);
    ppdLat = (int) Math.max(ppdLat, 5);

    // can't use padLon/padLat unless the map frame is used to clip the image
    double padLon = range.getLonRange() * 0.00;
    double padLat = range.getLatRange() * 0.00;
    GeoRange padRange = new GeoRange(range.getWest() - padLon, range.getEast() + padLon,
        range.getSouth() - padLat, range.getNorth() + padLat);

    GeoImage gi = getCompositeImage(padRange, ppdLon, ppdLat, scale);
    BufferedImage im = null;
    if (gi != null) {
      im = proj.getProjectedImage(10, width, height, gi.getImage(), padRange, extents[0],
          extents[1], extents[2], extents[3]);
    }
    return im;
  }

  /**
   * <p>Main method</p>
   * <p>Syntax is: GeoImageSet map_pack_root_dir west east south north</p>
   * <p>Create new frame and shows maps</p>
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    GeoImageSet is = GeoImageSet.loadMapPacks(args[0]).item1;
    GeoRange range = new GeoRange(Double.parseDouble(args[1]), Double.parseDouble(args[2]),
        Double.parseDouble(args[3]), Double.parseDouble(args[4]));
    Mercator m = new Mercator();
    m.setOrigin(range.getCenter());
    TransverseMercator tm = new TransverseMercator();
    tm.setup(range.getCenter(), 0, 0);
    // GeoImage gi = is.getCompositeImage(range, 200, 200);
    // final RenderedImage ci = gi.getImage();
    // System.out.println("ci: " + ci.getWidth() + " " + ci.getHeight());
    // Raster r = ci.getData();
    // DataBufferInt dbi = (DataBufferInt)r.getDataBuffer();
    // System.out.println(dbi.getNumBanks() + " " + dbi.getSize());
    // int[] pix = dbi.getData();
    // for (int i = 0; i < pix.length; i++)
    // {
    // pix[i] = 0xff000000 | pix[i];
    // }
    /*
     * DataBufferByte dbb = (DataBufferByte)r.getDataBuffer();
     * System.out.println(dbb.getNumBanks() + " " + dbb.getSize());
     * int[] pix = new int[ci.getWidth() * ci.getHeight()];
     * byte[] data = dbb.getData();
     * for (int i = 0; i < data.length; i += 4)
     * {
     * pix[i / 4] = (0xff000000) |
     * (data[i + 1] & 0xff) << 0 |
     * (data[i + 2] & 0xff) << 8 |
     * (data[i + 3] & 0xff) << 16;
     * }
     */

    // final Image image = tm.getProjectedImage(50,
    // 800, 800,
    // pix, ci.getWidth(), ci.getHeight(), range,
    // -500000, 500000, -500000, 500000);

    // BufferedImage bi = new
    // BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);
    // Graphics bg = bi.getGraphics();
    // bg.drawImage(image, 0, 0, null);
    // bg.dispose();

    MapRenderer mr = new MapRenderer(range, tm);
    double scale = 22000;
    int width = 600;
    RenderedImage image = is.getMapBackground(tm, range, width, scale);
    // image = is.getMapBackground(tm, range, width, scale);
    int INSET = 50;
    // mr.setLocation(INSET, INSET, width);
    mr.setLocation(INSET, INSET, image.getWidth());
    mr.setMapImage(image);
    // mr.setGeoLabelSet(labels);
    mr.createGraticule(6, true);
    mr.createBox(6);
    // mr.createScaleRenderer(1 / projection.getScale(center), INSET, 14);
    // TextRenderer tr = new TextRenderer(mapImagePanel.getWidth() - INSET, 14, projection.getName()
    // + " Projection");
    // tr.antiAlias = false;
    // tr.font = new Font("Arial", Font.PLAIN, 10);
    // tr.horizJustification = TextRenderer.RIGHT;
    // mr.addRenderer(tr);
    // renderer = mr;

    final Plot plot = new Plot();
    plot.setSize(800, 800);
    // plot.setBackgroundColor(Color.BLUE);
    plot.addRenderer(mr);



    // proj.setOrigin(new Point2D.Double(range.getCenter().x, 0));
    // final RenderedImage ri = is.getMapBackground(proj, range, 900);
    JFrame f = new JFrame("GeoImageSet Test, Projected") {
      public static final long serialVersionUID = -1;

      public void paint(Graphics g) {
        try {
          super.paint(g);
          Graphics2D g2 = (Graphics2D) g;
          g2.setColor(Color.RED);
          g2.fillRect(0, 0, 1200, 1000);
          // System.out.println(proj.getName());
          // Graphics2D g2 = (Graphics2D)g;
          // g2.drawImage(image, 50, 50, null);
          plot.render(g2);
          // AffineTransform at = AffineTransform.getScaleInstance(1, 1);
          // at.translate(50, 50);
          // g2.drawRenderedImage(ri, at);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    f.setSize(1200, 1000);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);
    //
    // JFrame f2 = new JFrame("GeoImageSet Test, Composite")
    // {
    // public static final long serialVersionUID = -1;
    // public void paint(Graphics g)
    // {
    //
    // AffineTransform at = AffineTransform.getScaleInstance(1, 1);
    // at.translate(50, 50);
    // g2.drawRenderedImage(ci, at);
    // }
    // };
    // f2.setSize(1200, 1000);
    // f2.setLocation(1281, 0);
    // f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // f2.setVisible(true);
  }
}
