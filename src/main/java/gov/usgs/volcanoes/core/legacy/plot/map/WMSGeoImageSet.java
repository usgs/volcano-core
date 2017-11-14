package gov.usgs.volcanoes.core.legacy.plot.map;

import gov.usgs.volcanoes.core.math.proj.GeoRange;
import gov.usgs.volcanoes.core.util.Retriable;
import gov.usgs.volcanoes.core.util.UtilException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

// import com.sun.image.codec.jpeg.JPEGCodec;
// import com.sun.image.codec.jpeg.JPEGImageDecoder;

/**
 * <p>
 * <code>GeoImageSet</code> to retrieve maps from Web Map Service Server via
 * Internet
 * </p>
 * 
 * @author Dan Cervelli
 */
public class WMSGeoImageSet extends GeoImageSet {
  private static final Logger LOGGER = LoggerFactory.getLogger(WMSGeoImageSet.class);

  public static final String DEFAULT_SERVER = "http://wms.jpl.nasa.gov/wms.cgi";
  public static final String DEFAULT_LAYER = "global_mosaic";
  public static final String DEFAULT_STYLE = "visual";
  public static final ExceptionType DEFAULT_EXCEPTION_TYPE = ExceptionType.XML;
  public static final ImageType DEFAULT_IMAGE_TYPE = ImageType.JPEG;

  public static final String EPSG_4326 = "EPSG:4326";

  public enum ImageType {
    JPEG("image/jpeg"), PNG("image/png");

    public String mime;

    private ImageType(String s) {
      mime = s;
    }
  }

  public enum ExceptionType {
    XML("application/vnd.ogc.se_xml"), IN_IMAGE("application/vnd.ogc.se_inimage"), BLANK(
        "application/vnd.ogc.se_blank");

    public String mime;

    private ExceptionType(String s) {
      mime = s;
    }
  }

  protected String server;
  protected String layer;
  protected String styles;
  protected ExceptionType exceptionType;
  protected ImageType imageType;

  protected BufferedReader in;
  protected BufferedInputStream inputStream;

  /**
   * Default constructor
   */
  public WMSGeoImageSet() {
    server = DEFAULT_SERVER;
    layer = DEFAULT_LAYER;
    styles = DEFAULT_STYLE;
    exceptionType = DEFAULT_EXCEPTION_TYPE;
    imageType = DEFAULT_IMAGE_TYPE;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String svr) {
    server = svr;
  }

  public void setLayer(String l) {
    layer = l;
  }

  public void setStyle(String s) {
    styles = s;
  }

  public void addStyle(String s) {
    if (styles == null || styles.length() == 0)
      styles = s;
    else
      styles += "," + s;
  }

  private void getReader(String resource) throws MalformedURLException, IOException {
    if (resource.indexOf("://") != -1) {
      URL url = new URL(resource);
      inputStream = new BufferedInputStream(url.openStream());
      in = new BufferedReader(new InputStreamReader(inputStream));
    } else {
      in = new BufferedReader(new FileReader(resource));
    }
  }

  public void getCapabilities() {
    char c = server.indexOf('?') == -1 ? '?' : '&';
    String request = server + c + "REQUEST=GetCapabilities";

    try {
      getReader(request);
      String s;
      while ((s = in.readLine()) != null)
        LOGGER.info(s);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public synchronized GeoImage getCompositeImage(final GeoRange range, final int ppdLon,
      final int ppdLat, final double scale) {
    GeoImage ret = null;
    try {
      Retriable<GeoImage> rt = new Retriable<GeoImage>("WMS", 3) {
        @Override
        public void attemptFix() {
          try {
            Thread.sleep(500);
          } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }

        @Override
        public boolean attempt() throws UtilException {
          GeoImage image = null;
          double width = range.getLonRange() * (double) ppdLon;
          double height = range.getLatRange() * (double) ppdLat;
          double w = range.getWest();
          double e = range.getEast();
          if (w > e) {
            e += 360;
          }

          char c = server.indexOf('?') == -1 ? '?' : '&';
          String request = String.format(
              "%s%cSERVICE=WMS" + "&VERSION=1.1.1" + "&REQUEST=GetMap" + "&LAYERS=%s" + "&STYLES=%s"
                  + "&WIDTH=%d" + "&HEIGHT=%d" + "&FORMAT=%s" + "&EXCEPTIONS=%s" + "&SRS=%s"
                  + "&BBOX=%.5f,%.5f,%.5f,%.5f",
              server, c, layer, // "BMNG"
              styles, (int) width, (int) height, imageType.mime, exceptionType.mime, EPSG_4326, w,
              range.getSouth(), e, range.getNorth());

          LOGGER.info(request);
          try {
            getReader(request);
            inputStream.mark(16384);
            // JPEGImageDecoder codec =
            // JPEGCodec.createJPEGDecoder(inputStream);
            // BufferedImage im = codec.decodeAsBufferedImage();

            // TODO: make sure this works.
            BufferedImage im = ImageIO.read(inputStream);
            BufferedImage copy =
                new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_ARGB);
            copy.getGraphics().drawImage(im, 0, 0, null);
            image = GeoImage.createMemoryImage(copy, range);
          } catch (Exception ex) {
            try {
              inputStream.reset();
              String s;
              LOGGER.warn("WMS failure: {}", ex);
              while ((s = in.readLine()) != null)
                LOGGER.warn("\t{}", s);
            } catch (IOException ioex) {
              ioex.printStackTrace();
            }
          }
          result = image;
          return result != null;
        }
      };

      ret = rt.go();
    } catch (UtilException e) {
      // Do nothing
    }
    return ret;
  }
}
