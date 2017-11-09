package gov.usgs.volcanoes.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads resources with ProgressInputStream.
 *
 * @author Dan Cervelli
 */
public class ResourceReader {
  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceReader.class);
  private BufferedReader in;
  private URLConnection conn;
  private InputStream inputStream;

  private ResourceReader(URL url, ProgressListener pl) throws IOException {
    conn = url.openConnection();
    inputStream = url.openStream();
    if (pl != null) {
      ProgressInputStream pis = new ProgressInputStream(inputStream, conn.getContentLength());
      pis.addProgressListener(pl);
      inputStream = pis;
    }
  }

  /**
   * Creates a resource reader for the given resource.
   * 
   * @param resource string resource url to read, if the resource has not
   *     "://" in it, it is assumed to be a local filename.
   * @return resource reader
   */
  public static ResourceReader getResourceReader(String resource) {
    return getResourceReader(resource, null);
  }

  /**
   * Creates a resource reader for the given resource.
   * @param url URL to read
   * @return resource reader
   */
  public static ResourceReader getResourceReader(URL url) {
    return getResourceReader(url, null);
  }

  /**
   * Creates a resource reader for the given resource.  If the resource has
   * "://" in it then the URL is retrieved, if not it is assumed to be a local
   * filename.
   * 
   * @param resource string resource url to read
   * @param pl ProgressListener which listen and proceed resource reading events while reading
   * @return resource reader
   */
  public static ResourceReader getResourceReader(String resource, ProgressListener pl) {
    try {
      URL url;
      if (resource.contains("://")) {
        url = new URL(resource);
      } else {
        url = new File(resource).toURI().toURL();
      }
      return getResourceReader(url, pl);
    } catch (Exception e) {
      LOGGER.warn("Could not open resource: {}, {}", resource, e);
    }
    return null;
  }

  /**
   * Creates a resource reader for the given resource.  If the resource has
   * has a local filename then it is read otherwise the class loader is used.
   * 
   * @param cl the class to use to get the class loader
   * @param name the resource name
   * @return resource reader
   */
  public static ResourceReader getResourceReader(Class<?> cl, String name) {
    return getResourceReader(cl, name, (ProgressListener) null);
  }

  /**
   * Creates a resource reader for the given resource.  If the resource has
   * has a local filename then it is read otherwise the class loader is used.
   * 
   * @param cl the class to use to get the class loader
   * @param name the resource name
   * @param pl ProgressListener which listen and proceed resource reading events while reading
   * @return resource reader
   */
  public static ResourceReader getResourceReader(Class<?> cl, String name, ProgressListener pl) {
    URL url;
    ResourceReader rr = null;
    try {
      // if local resource file exists
      final File file = new File(name);
      if (file.exists()) {
        url = file.toURI().toURL();
        rr = getResourceReader(url, pl);
      }
    } catch (Exception e) {
      LOGGER.warn("Could not open resource: {}, {}", name, e);
    }
    try {
      // if local file not read and class loader found the URL
      if (rr == null && (url = cl.getClassLoader().getResource(name)) != null) {
        rr = getResourceReader(url, pl);
      }
    } catch (Exception e) {
      LOGGER.warn("Could not open resource: {}, {}", name, e.getMessage());
    }
    return rr;
  }

  /**
   * Creates a resource reader for the given resource.
   * @param url URL to read
   * @param pl ProgressListener to be informed about resource reading events
   * @return resource reader
   */
  public static ResourceReader getResourceReader(URL url, ProgressListener pl) {
    ResourceReader rr = null;
    try {
      rr = new ResourceReader(url, pl);
    } catch (Exception e) {
      LOGGER.warn("Could not open resource: {}, {}", url, e);
    }
    return rr;
  }

  /**
   * Getter for input stream.
   * 
   * @return input stream
   * @throws IllegalStateException when things go wrong
   */
  public InputStream getInputStream() {
    if (in != null) {
      throw new IllegalStateException("Can not use input stream after the reader has been used.");
    }
    return inputStream;
  }

  /**
   * Yield reader, creating if necessary.
   * 
   * @return reader
   */
  public Reader getReader() {
    if (in == null) {
      in = new BufferedReader(new InputStreamReader(inputStream));
    }
    return in;
  }

  /**
   * Yield next read line.
   * 
   * @return line
   */
  public String nextLine() {
    if (in == null) {
      in = new BufferedReader(new InputStreamReader(inputStream));
    }
    String line = null;
    try {
      line = in.readLine();
    } catch (IOException e) {
      // ignore
    }
    return line;
  }

  /**
   * Close Reader.
   */
  public void close() {
    try {
      inputStream.close();
      if (in != null) {
        in.close();
      }
    } catch (IOException e) {
      // ignore
    }
  }
}
