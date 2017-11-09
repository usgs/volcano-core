package gov.usgs.volcanoes.core.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Extension of InputStream, we provide expected stream length and
 * this class calls registered progress listeners after every read event 
 * with read percentage.
 * 
 * @see ProgressListener
 * 
 * @author Dan Cervelli
 * @version $Id: ProgressInputStream.java,v 1.1 2007-04-24 21:37:03 dcervelli Exp $
 */
public class ProgressInputStream extends InputStream implements Closeable {
  /**
   * Why, WHY do you need another InputStream as member. 
   * We already have one because this class extends it.
   */
  protected InputStream source;
  protected int length;
  protected int read;
  protected int lastProgress = -1;

  protected List<ProgressListener> listeners;

  /**
   * Constructor.
   * 
   * @param src Controlled input stream
   * @param len expected stream length
   */
  public ProgressInputStream(InputStream src, int len) {
    listeners = new ArrayList<ProgressListener>(2);
    source = src;
    length = len;
    read = 0;
  }

  /**
   * Adds progress listener to this class.
   * 
   * @param pl my listener
   */
  public void addProgressListener(ProgressListener pl) {
    listeners.add(pl);
  }

  /**
   * Add to read counter.
   * 
   * @param cnt Count of bytes to add to read counter
   */
  protected void addRead(int cnt) {
    if (cnt <= 0) {
      return;
    }

    read += cnt;
    float pct = ((float) read / (float) length);
    int pprogress = (int) (pct * 100);
    if (lastProgress != pprogress) {
      lastProgress = pprogress;
      for (ProgressListener pl : listeners) {
        pl.progressDone(pct);
      }
    }
  }

  /**
   * read.
   * 
   * @see InputStream#read()
   */
  public int read() throws IOException {
    int byteRead = source.read();
    addRead(1);
    return byteRead;
  }

  /**
   * read.
   * 
   * @see InputStream#read(byte[])
   */
  public int read(byte[] myByte) throws IOException {
    int byteRead = source.read(myByte);
    addRead(byteRead);
    return byteRead;
  }

  /**
   * read.
   * 
   * @see InputStream#read(byte[], int, int)
   */
  public int read(byte[] myByte, int off, int len) throws IOException {
    int numRead = source.read(myByte, off, len);
    addRead(numRead);
    return numRead;
  }

  /**
   * close.
   * 
   * @see InputStream#close()
   */
  public void close() throws IOException {
    listeners.clear();
    listeners = null;
    source.close();
  }
}
