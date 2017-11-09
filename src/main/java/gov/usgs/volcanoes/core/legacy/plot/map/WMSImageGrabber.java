package gov.usgs.volcanoes.core.legacy.plot.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

// import com.sun.image.codec.jpeg.JPEGCodec;
// import com.sun.image.codec.jpeg.JPEGImageDecoder;

/**
 * <p>
 * Request whole earth image from Web Map Service Server 1000x800 size in jpeg
 * format
 * </p>
 * 
 * @author Dan Cervelli
 */
public class WMSImageGrabber {
  protected String server = "http://wms.jpl.nasa.gov/wms.cgi";
  protected final static Logger LOGGER = LoggerFactory.getLogger(WMSImageGrabber.class);
  protected BufferedReader in;
  protected InputStream inputStream;

  private void getReader(String resource) throws MalformedURLException, IOException {
    if (resource.indexOf("://") != -1) {
      URL url = new URL(resource);
      inputStream = url.openStream();
      in = new BufferedReader(new InputStreamReader(inputStream));
    } else {
      in = new BufferedReader(new FileReader(resource));
    }
  }

  public void getCapabilities() {
    String request = server + "?REQUEST=GetCapabilities";
    try {
      getReader(request);
      String s;
      while ((s = in.readLine()) != null)
        LOGGER.info(s);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public BufferedImage getImage() {
    BufferedImage image = null;
    String request = server + "?REQUEST=GetMap&LAYERS=BMNG&STYLES=Sep&WIDTH=1000&HEIGHT=800"
        + "&EXCEPTIONS=application/vnd.ogc.se_inimage&SRS=EPSG%3A4326&BBOX=-180,-90,180,90&FORMAT=image/jpeg";
    try {
      getReader(request);
      // JPEGImageDecoder codec =
      // JPEGCodec.createJPEGDecoder(inputStream);
      // image = codec.decodeAsBufferedImage();

      // TODO: make sure this works
      image = ImageIO.read(inputStream);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return image;
  }

  /**
   * Main method. Open screen frame with generated image.
   * 
   * @param args
   *            none
   */
  public static void main(String[] args) {
    WMSImageGrabber w = new WMSImageGrabber();
    // w.getCapabilities();
    final BufferedImage bi = w.getImage();

    JFrame f = new JFrame("Test");
    f.setSize(1200, 1000);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setLocationRelativeTo(null);
    f.setContentPane(new JButton(new ImageIcon(bi)));
    f.setVisible(true);
  }
}
