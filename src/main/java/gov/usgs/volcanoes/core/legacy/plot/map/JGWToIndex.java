package gov.usgs.volcanoes.core.legacy.plot.map;

import gov.usgs.volcanoes.core.util.ResourceReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * <p>Scans dir for .jgw files and send to stdout strings as follow:</p>
 * <p>name, width, height, west_coord, east_coord, south_coord, north_coord</p>
 * <p>Syntax is: JGWToIndex [dir_name]</p>
 * <p>Default is current directory.</p>
 * 
 * @author Dan Cervelli
 */
public class JGWToIndex {
  public static void main(String[] args) throws IOException {
    String dir = ".";
    if (args.length > 0)
      dir = args[0];
    String[] files = new File(dir).list();
    if (files == null) {
      throw new RuntimeException("No file list.");
    }
    for (String fn : files) {
      if (!fn.endsWith(".jgw"))
        continue;
      ResourceReader rr = ResourceReader.getResourceReader(fn);
      double[] jgw = new double[6];
      for (int i = 0; i < 6; i++)
        jgw[i] = Double.parseDouble(rr.nextLine());
      String jpg = fn.substring(0, fn.length() - 2) + "pg";
      BufferedImage image = ImageIO.read(new File(jpg));
      double w = jgw[4];
      double e = w + image.getWidth() * jgw[0];
      double n = jgw[5];
      double s = n + image.getHeight() * jgw[3];
      System.out.printf("%s, %d, %d, %f, %f, %f, %f\n", jpg, image.getWidth(), image.getHeight(), w,
          e, s, n);
    }
  }
}
