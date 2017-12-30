package gov.usgs.volcanoes.core.legacy.plot;

import gov.usgs.volcanoes.core.contrib.PngEncoder;
import gov.usgs.volcanoes.core.contrib.PngEncoderB;
import gov.usgs.volcanoes.core.legacy.plot.render.Renderer;
import gov.usgs.volcanoes.core.legacy.plot.render.TextRenderer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.SimpleDoc;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Plot is the top level class that contains a Valve plot. The Valve plotting
 * engine is simple in theory: every Plot has a list of Renderers and each one
 * of those renders itself however it wants. Of course, it gets more complicated
 * because most Renderers themselves have a list of Renderers. It is the
 * responsibility of each of these Renderers to leave the Graphics2D object that
 * they render upon in the same state in which they got it. This way a Renderer
 * only has to worry about itself.
 * </p>
 * 
 * <p>
 * A FrameRenderer (the class that all Valve graphs wrap their data in) can in
 * theory be put anywhere on the plot. But because Valve allows users to overlay
 * plots there is a mechanism called the Default Frame Renderer Location that
 * specifies where in the plot the Frame Renderer should be placed. The Valve
 * grapher sets this before each of the GraphLines are rendered depending on the
 * size of the graph and other factors. Each frame renderer then places itself
 * where the plot tells it to via setFrameRendererLocation(FrameRenderer). Also
 * set by the grapher is the default number of ticks on each axis.
 * </p>
 * 
 * @author Dan Cervelli
 */
public class Plot implements Printable {
  protected static final Logger LOGGER = LoggerFactory.getLogger(Plot.class);
  private Dimension size; // plot size in pixels
  private Color backgroundColor;
  protected List<Renderer> renderers;

  /**
   * Creates a new 0x0 empty plot.
   */
  public Plot() {
    this(0, 0);
  }

  /**
   * Creates a new wxh empty plot.
   * 
   * @param w width
   * @param h height
   */
  public Plot(int w, int h) {
    size = new Dimension(w, h);
    renderers = new ArrayList<Renderer>();
    backgroundColor = Color.WHITE;
  }

  /**
   * Clears all of the renderers.
   */
  public void clear() {
    renderers.clear();
  }

  /**
   * Gets the list of Renderers.
   * 
   * @return the Renderers
   */
  public List<Renderer> getRenderers() {
    return renderers;
  }

  /**
   * Adds a Renderer.
   * 
   * @param dr
   *            the Renderer
   */
  public void addRenderer(Renderer dr) {
    renderers.add(dr);
  }

  /**
   * Sets the background color.
   * 
   * @param c
   *            the background color.
   */
  public void setBackgroundColor(Color c) {
    backgroundColor = c;
  }

  /**
   * Gets the background color.
   * 
   * @return the background color.
   */
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Perform rendering to buffered image.
   * 
   * @param alpha type of generated image: true - TYPE_INT_ARGB, false - TYPE_INT_RGB
   * @return generated image
   */
  public BufferedImage getAsBufferedImage(boolean alpha) throws PlotException {
    int type = (alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
    BufferedImage image = new BufferedImage(size.width, size.height, type);
    Graphics2D imageGraphics = (Graphics2D) image.getGraphics();
    this.render(imageGraphics);
    return image;
  }

  /**
   * Outputs the plot to a png file.
   * 
   * @param fn the output filename
   */
  public void writePNG(String fn) throws PlotException {
    BufferedImage image = getAsBufferedImage(true);
    PngEncoderB png = new PngEncoderB(image, false, PngEncoder.FILTER_NONE, 7);
    try {
      FileOutputStream out = new FileOutputStream(fn);
      byte[] bytes = png.pngEncode();
      out.write(bytes);
      out.close();
    } catch (FileNotFoundException e) {
      LOGGER.error(e.getMessage());
      throw new PlotException(e.getMessage());
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      throw new PlotException(e.getMessage());
    }

  }

  /**
   * Outputs the plot to a jpeg file.
   * 
   * @param fn the output filename
   */
  public void writeJPEG(String fn) throws PlotException {
    BufferedImage image = getAsBufferedImage(false);
    try {
      ImageIO.write(image, "JPEG", new File(fn));
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      throw new PlotException(e.getMessage());
    }
  }

  /**
   * Gets the bytes of an encoded PNG representation of this plot.
   * 
   * @return the bytes
   */
  public byte[] getPNGBytes() throws PlotException {
    BufferedImage image = getAsBufferedImage(true);
    try {
      PngEncoderB png = new PngEncoderB(image, false, PngEncoder.FILTER_NONE, 7);
      byte[] bytes = png.pngEncode();
      return bytes;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Outputs the plot as PostScript.
   * 
   * @param fn the output filename
   */
  public void writePS(String fn) {
    DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
    String psMimeType = DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType();

    StreamPrintServiceFactory[] factories =
        StreamPrintServiceFactory.lookupStreamPrintServiceFactories(flavor, psMimeType);

    if (factories.length == 0) {
      LOGGER.info("No suitable factories");
      return;
    }

    try {
      PageFormat pf = new PageFormat();
      Paper p = new Paper();

      // page size 300 dpi in 1/72nds
      double w = size.width * .70;
      double h = size.height * .70;

      p.setSize(w, h);
      p.setImageableArea(0, 0, w, h);
      pf.setPaper(p);
      Book book = new Book();
      book.append(this, pf);

      Doc doc = new SimpleDoc(book, flavor, null);

      FileOutputStream fos = new FileOutputStream(fn);
      StreamPrintService sps = factories[0].getPrintService(fos);
      DocPrintJob pj = sps.createPrintJob();
      PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
      pj.print(doc, aset);
      fos.close();
    } catch (PrintException pe) {
      LOGGER.warn("{}", pe);
    } catch (IOException ie) {
      LOGGER.warn("{}", ie);
    }
  }

  /**
   * Implementation of the Printable interface; used for outputting PostScript
   * -- should not be called directly.
   * 
   * @param g
   *            the Graphics object
   * @param pf
   *            the PageFormat object
   * @param pageIndex
   *            the page index
   * @return the result
   */
  public int print(Graphics g, PageFormat pf, int pageIndex) {
    if (pageIndex == 0) {
      Graphics2D g2 = (Graphics2D) g;
      AffineTransform at = g2.getTransform();
      // double xScale = 792 / (double)size.width;
      // double yScale = 612 / (double)size.height;
      // double scale = Math.min(xScale, yScale);
      g2.scale(.70, .70);
      try {
        render(g2);
      } catch (Exception e) {
        LOGGER.error("{}", e);
      }
      g2.setTransform(at);
      return Printable.PAGE_EXISTS;
    } else {
      return Printable.NO_SUCH_PAGE;
    }
  }

  /**
   * Renders the plot. This simply paints the background color and iterates
   * through the Renderers.
   * 
   * @param g
   *            the Graphics2D object to plot upon
   */
  public void render(Graphics2D g) throws PlotException {
    // g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
    // RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    // g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
    // RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    // g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
    // RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    // g.setRenderingHint(RenderingHints.KEY_RENDERING,
    // RenderingHints.VALUE_RENDER_QUALITY);
    if (backgroundColor != null) {
      Color origColor = g.getColor();
      g.setColor(backgroundColor);
      g.fillRect(0, 0, size.width, size.height);
      g.setColor(origColor);
    }
    g.setColor(Color.black);
    AffineTransform origAt = g.getTransform();

    for (Renderer renderer : renderers) {
      if (renderer instanceof TextRenderer) {
        ((TextRenderer) renderer).antiAlias = true;
      }
      renderer.render(g);
    }

    g.setTransform(origAt);
  }

  /**
   * Sets the size of the plot in pixels.
   * 
   * @param w the width
   * @param h the height
   */
  public void setSize(int w, int h) {
    size.width = w;
    size.height = h;
  }

  /**
   * Sets the size of the plot in pixels.
   * 
   * @param d
   *            the dimensions
   */
  public void setSize(Dimension d) {
    size = d;
  }

  /**
   * Gets the size of the plot in pixels.
   * 
   * @return the dimensions
   */
  public Dimension getSize() {
    return size;
  }

  /**
   * Gets the width of the plot in pixels.
   * 
   * @return the width
   */
  public int getWidth() {
    return size.width;
  }

  /**
   * Gets the height of the plot in pixels.
   * 
   * @return the height
   */
  public int getHeight() {
    return size.height;
  }

  /**
   * Shows the plot on the screen at 0,0 location. This is typically used for
   * debugging purposes.
   */
  public void quickShow() {
    quickShow(0, 0);
  }

  /**
   * Shows the plot on the screen. This is typically used for debugging
   * purposes.
   * 
   * @param x
   *            x-coord of location of plot
   * @param y
   *            y-coord of location of plot
   */
  public void quickShow(int x, int y) {
    JFrame frame = new JFrame("Plot");
    frame.setSize(size.width + 30, size.height + 50);
    frame.setLocation(x, y);
    frame.setContentPane(new Container() {
      private static final long serialVersionUID = -1;

      public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(10, 10);
        try {
          render(g2);
        } catch (Exception e) {
          LOGGER.error("{}", e);
        }
      }
    });
    frame.setVisible(true);
  }

  /**
   * Utility function for iterating through an array of Renderers and
   * rendering them while checking for nulls.
   * 
   * @param g
   *            the Graphic2D to render upon
   * @param renderers
   *            the array of Renderers
   */
  public static void renderArray(Graphics2D g, Renderer[] renderers) {
    if (renderers != null) {
      for (int i = 0; i < renderers.length; i++) {
        if (renderers[i] != null) {
          renderers[i].render(g);
        }
      }
    }
  }
}
