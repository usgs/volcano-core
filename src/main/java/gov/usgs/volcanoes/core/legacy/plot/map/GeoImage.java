package gov.usgs.volcanoes.core.legacy.plot.map;

import gov.usgs.volcanoes.core.CodeTimer;
import gov.usgs.volcanoes.core.math.proj.GeoRange;
import gov.usgs.volcanoes.core.math.proj.Projection;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

// import com.sun.image.codec.jpeg.JPEGCodec;
// import com.sun.image.codec.jpeg.JPEGImageDecoder;

/**
 * <p>
 * Holds information about earth surface image: edge coordinates, storing
 * method, processing priority, generated image itself.
 * </p>
 * 
 * @author Dan Cervelli
 */
public class GeoImage implements Comparable<GeoImage> {
  private String filename;
  private GeoRange range;
  private int pixelWidth;
  private int pixelHeight;
  private double minScale;
  private double maxScale;
  private int priority = 0;

  private BufferedImage image;

  /**
   * Default constructor
   */
  public GeoImage() {}

  /**
   * Constructor
   * 
   * @param is
   *            comma-separated parameter string: pixel width and height, and
   *            coordinates for west, east, south and north edge.
   */
  public GeoImage(String is) {
    String[] ss = is.split(",");
    filename = ss[0].trim();
    pixelWidth = Integer.parseInt(ss[1].trim());
    pixelHeight = Integer.parseInt(ss[2].trim());

    double w = Double.parseDouble(ss[3].trim());
    double e = Double.parseDouble(ss[4].trim());
    double s = Double.parseDouble(ss[5].trim());
    double n = Double.parseDouble(ss[6].trim());
    range = new GeoRange(w, e, s, n);
    image = null;
    if (ss.length > 7) {
      minScale = Double.parseDouble(ss[7].trim());
      maxScale = Double.parseDouble(ss[8].trim());
    } else {
      minScale = Double.NaN;
      maxScale = Double.NaN;
    }
    if (ss.length == 10) {
      priority = Integer.parseInt(ss[9].trim());
    }
  }

  /**
   * Constructor
   * 
   * @param root
   *            base directory to store images
   * @param is
   *            comma-separated parameter string: pixel width and height, and
   *            coordinates for west, east, south and north edge.
   */
  public GeoImage(String root, String is) {
    this(is);
    filename = root + File.separatorChar + filename;
  }

  /**
   * I've tried the following methods of loading images (order by quickness):
   * 0. JPEGCodec 
   * 1. ImageIO.read() 
   * 2(t). JAI.create("stream") 
   * 2(t). JAI.create("fileload") 
   * 4. Toolkit.createImage()
   * 
   * TJP adds: nonetheless, this happens once per mouse click and does not 
   * need to be highly optimized to maintain acceptable responsiveness. 
   * Compatibility and maintainability trumps.
   */
  private void loadImage() {
    if (filename.equals("memory"))
      return;

    CodeTimer ct = new CodeTimer("imageLoad: " + filename);

    try {

      /*
       * I'm pulling com.sun.image.codec.jpeg
       * 
       * Using it is in poor form. Sun continues to threaten to remove the
       * classes, and they are unlikely to be supported in non-sun JVM's.
       * Yes, it's faster. However, I'm not sure it matters. Image loads
       * are infrequent, and adding even 100ms to the time taken by a map
       * load is tolerable.
       * 
       * Code left behind in case I'm mistaken.
       */
      // if (filename.toLowerCase().endsWith("jpg"))
      // {
      // // use specialized JPEG loader, a bit less than twice as fast
      // // as the standard one below.
      // // Retested with JDK1.6, still faster.
      // JPEGImageDecoder codec = JPEGCodec.createJPEGDecoder(new
      // FileInputStream(filename));
      // image = codec.decodeAsBufferedImage();
      // }
      // else
      {
        image = ImageIO.read(new File(filename));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    ct.stopAndReport();
  }

  /**
   * If image stored in memory, remove links to it
   */
  public void disposeImage() {
    if (!filename.equals("memory"))
      image = null;
  }

  /**
   * Create GeoImage stored in RAM
   * 
   * @param pi
   *            image to draw
   * @param r
   *            edge coordinates
   */
  public static GeoImage createMemoryImage(BufferedImage pi, GeoRange r) {
    GeoImage result = new GeoImage();
    result.filename = "memory";
    result.range = new GeoRange(r);
    result.image = pi;
    result.pixelWidth = pi.getWidth();
    result.pixelHeight = pi.getHeight();
    return result;
  }

  /**
   * Get filename to store image, "memory" in case of RAM
   */
  public String getFilename() {
    return filename;
  }

  /**
   * Get image to draw
   */
  public BufferedImage getImage() {
    if (image == null)
      loadImage();

    return image;
  }

  /**
   * Getter for priority
   */
  public int getPriority() {
    return priority;
  }

  /**
   * Getter for image pixel width
   */
  public int getPixelWidth() {
    return pixelWidth;
  }

  /**
   * Getter for image pixel height
   */
  public int getPixelHeight() {
    return pixelHeight;
  }

  /**
   * Get count of pixel in the drawing
   */
  public int getPixelArea() {
    return pixelWidth * pixelHeight;
  }

  /**
   * Get longitude range per one pixel
   */
  public double getLonPerPixel() {
    return range.getLonRange() / (double) pixelWidth;
  }

  /**
   * Get latitude range per one pixel
   */
  public double getLatPerPixel() {
    return range.getLatRange() / (double) pixelHeight;
  }

  public double getLonLatArea() {
    return range.getLonRange() * range.getLatRange();
  }

  /**
   * Get pixel count in one longitude degree
   */
  public double getPixelsPerLon() {
    return 1.0 / getLonPerPixel();
  }

  /**
   * Get pixel count in one latitude degree
   */
  public double getPixelsPerLat() {
    return 1.0 / getLatPerPixel();
  }

  public double getMinScale() {
    return minScale;
  }

  public double getMaxScale() {
    return maxScale;
  }

  public boolean inScale(double sc, double area) {
    if (Double.isNaN(minScale) && Double.isNaN(maxScale)) {
      return area > 0.8;
    } else
      return sc >= minScale && sc < maxScale;
  }

  public String toString() {
    return filename + ": " + range.toString();
  }

  public double[] getProjectedExtents(Projection proj) {
    return range.getProjectedExtents(proj);
  }

  public GeoRange getRange() {
    return range;
  }

  /*
   * public RenderedImage project(Projection proj, int gridSize, int
   * outputWidth, int outputHeight) { return project(proj, gridSize,
   * outputWidth, outputHeight, range); }
   * 
   * 
   * public RenderedImage project(Projection proj, int gridSize, int
   * outputWidth, int outputHeight, GeoRange outRange) { loadImage(); float[]
   * pts = new float[(gridSize + 1) * (gridSize + 1) * 2];
   * 
   * double[] extents = outRange.getProjectedExtents(proj); Point2D.Double pt
   * = new Point2D.Double();
   * 
   * double dx = extents[1] - extents[0]; double dy = extents[3] - extents[2];
   * double cx = extents[0] + dx / 2.0; double cy = extents[2] + dy / 2.0;
   * 
   * for (int yi = 0; yi <= gridSize; yi++) { for (int xi = 0; xi <= gridSize;
   * xi++) { int index = (yi * (gridSize + 1) + xi) * 2; int ox = xi -
   * (gridSize / 2); int oy = yi - (gridSize / 2); double x = ox * dx /
   * (double)gridSize + cx; double y = -oy * dy / (double)gridSize + cy; pt.x
   * = x; pt.y = y; Point2D.Double ppt = proj.inverse(pt); ppt.x =
   * GeoRange.normalize(ppt.x);
   * 
   * // if these values go crazy weirdness ensues, potentially // check to see
   * if they go very negative or very large, relative // to pixel size of
   * source image.
   * 
   * double lr = GeoRange.getLonRange(range.getWest(), ppt.x); if
   * (range.getLonRange() <= 180) lr = GeoRange.normalize(lr); else { if (lr <
   * -180) lr += 360; double dr = lr - range.getLonRange(); if (dr > 0.0001)
   * lr -= 360; } pts[index] = (float)((lr) * getPixelsPerLon()); pts[index +
   * 1] = (float)((range.getNorth() - ppt.y) * getPixelsPerLat());
   * 
   * 
   * // was needed for non-nearest-neighbor interpolation // if (pts[index] <
   * 5) // pts[index] = 5; // if (pts[index] >= pixelWidth - 4) // pts[index]
   * = pixelWidth - 5; // if (pts[index + 1] < 5) // pts[index + 1] = 5; // if
   * (pts[index + 1] >= pixelHeight - 4) // pts[index + 1] = pixelHeight - 5;
   * 
   * 
   * } }
   * 
   * // xStart, yStart - must be 1 WarpGrid warp = new WarpGrid(1, outputWidth
   * / gridSize, gridSize, 1, outputHeight / gridSize, gridSize, pts);
   * Interpolation interp = new InterpolationNearest();
   * 
   * ParameterBlock pb = new ParameterBlock(); pb.addSource(image);
   * pb.add(warp); pb.add(interp); RenderedImage ri =
   * (RenderedImage)JAI.create("warp", pb);
   * 
   * ParameterBlock pb2 = new ParameterBlock(); pb2.addSource(ri); // first
   * two values must be 1. pb2.add((float)1.0); pb2.add((float)1.0); //
   * pb2.add((float)0.0); // pb2.add((float)0.0); pb2.add((float)outputWidth);
   * pb2.add((float)outputHeight); // TODO: there is a bug here return
   * (RenderedImage)JAI.create("crop", pb2); }
   */

  /**
   * Compare GeoImages by priority
   */
  public int compareTo(GeoImage o) {
    return priority - o.priority;
  }
}
