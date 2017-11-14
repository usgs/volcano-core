package gov.usgs.volcanoes.core.legacy.plot.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 * <p>A class that renderers an image as data. 
 * It extends FrameRenderer, which specifies the min/max data extents, and also holds 
 * values for the extents of the image.  Assuming the min/max data extents are
 * within the data extents of the image it can display any subset of the image.</p>
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/08/26 18:05:04  uid879
 * Initial avosouth commit.
 * 
 * @author Dan Cervelli
 */
public class ImageDataRenderer extends FrameRenderer {
  private double dataMinX, dataMaxX, dataMinY, dataMaxY;
  private Image image;

  /**
   * Empty constructor.
   */
  public ImageDataRenderer() {}

  /** Constructor that specifies the image. 
   * @param img the image
   */
  public ImageDataRenderer(Image img) {
    image = img;
  }

  /**
   * Sets the image.
   * @param img the image
   */
  public void setImage(Image img) {
    image = img;
  }

  /** Sets the data extents of this image.
   * @param minX the minimum x
   * @param maxX the maximum x
   * @param minY the minimum y
   * @param maxY the maximum y
   */
  public void setDataExtents(double minX, double maxX, double minY, double maxY) {
    dataMinX = minX;
    dataMaxX = maxX;
    dataMinY = minY;
    dataMaxY = maxY;
  }

  /** Renderers the portion of the image that is visible (based on the data
   * extents).
   * @param g the graphics object upon which to render
   */
  public void render(Graphics2D g) {
    AffineTransform origAT = g.getTransform();
    Shape origClip = g.getClip();

    if (axis != null)
      axis.render(g);

    if (image != null) {
      g.clip(new Rectangle(graphX + 1, graphY + 1, graphWidth - 1, graphHeight - 1));
      AffineTransform at = new AffineTransform();
      int imageWidth = image.getWidth(null);
      int imageHeight = image.getHeight(null);
      double dataWidth = dataMaxX - dataMinX;
      double ratioWidth = dataWidth / getWidth();
      double dataHeight = dataMaxY - dataMinY;
      double ratioHeight = dataHeight / getHeight();
      at.scale(ratioWidth * (double) graphWidth / (double) imageWidth,
          ratioHeight * (double) graphHeight / (double) imageHeight);
      g.translate(getXPixel(dataMinX), getYPixel(dataMaxY));
      g.drawImage(image, at, null);
      g.translate(-getXPixel(dataMinX), -getYPixel(dataMaxY));
    }

    g.setClip(origClip);
    g.setTransform(origAT);

    if (getLegendRenderer() != null)
      getLegendRenderer().render(g);

    if (axis != null)
      axis.postRender(g);
  }
}
