package gov.usgs.volcanoes.core.legacy.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some routines for simplifying communications over channels.
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2005/08/29 15:54:56  dcervelli
 * New logging changes.
 *
 * Revision 1.1  2005/08/26 17:32:44  uid879
 * Initial avosouth commit.
 *
 * Revision 1.3  2005/04/10 00:28:56  cervelli
 * Added proper logging; added writeCharBuffer().
 *
 * @author Dan Cervelli
 */
public class NetTools
{
  private int currentBufferSize = 2048;

  public CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();
  public CharsetDecoder decoder = Charset.forName("US-ASCII").newDecoder();

  private CharBuffer charBuffer;
  private ByteBuffer outBuffer;

  private static final Logger LOGGER = LoggerFactory.getLogger(NetTools.class);

  private Server server;

  public NetTools()
  {
    reallocate();
  }

  public void setServer(Server s)
  {
    server = s;
  }

  private void reallocate()
  {
    currentBufferSize *= 2;
    charBuffer = CharBuffer.allocate(currentBufferSize);
    outBuffer = ByteBuffer.allocateDirect(currentBufferSize * 2);
  }

  public int writeCharBuffer(CharBuffer cb, SocketChannel channel)
  {
    int totalBytes = 0;
    try
    {
      cb.flip();
      outBuffer.clear();
      while (outBuffer.capacity() < cb.limit())
        reallocate();

      outBuffer.put(encoder.encode(cb));
      outBuffer.flip();
      while (outBuffer.hasRemaining())
        totalBytes += channel.write(outBuffer);
    }
    catch (ClosedChannelException e)
    {}
    catch (Exception e)
    {
      LOGGER.error("Could not write character buffer.", e);
    }
    if (server != null)
      server.recordSent(channel, totalBytes);
    return totalBytes;
  }

  public int writeByteBuffer(ByteBuffer bb, SocketChannel channel)
  {
    int totalBytes = 0;
    SocketAddress remote = null;
    try
    {
      remote = channel.socket().getRemoteSocketAddress();
      while (bb.hasRemaining())
        totalBytes += channel.write(bb);
    }
    catch (ClosedChannelException e)
    {}
    catch (IOException e)
    {
      LOGGER.error("Could not write byte buffer for {}. Wrote {}, {} bytes remaining", remote, totalBytes, bb.remaining(), e);
    }
    if (server != null)
      server.recordSent(channel, totalBytes);
    return totalBytes;
  }

  public int writeString(String s, SocketChannel channel)
  {
    if (s == null || channel == null)
      return -1;
    int totalBytes = 0;
    try
    {
      while (s.length() >= charBuffer.capacity())
        reallocate();

      charBuffer.clear();
      outBuffer.clear();

      charBuffer.put(s);
      charBuffer.flip();
      outBuffer.put(encoder.encode(charBuffer));
      outBuffer.flip();
      while (outBuffer.hasRemaining())
        totalBytes += channel.write(outBuffer);
    }
    catch (ClosedChannelException e)
    {}
    catch (IOException e)
    {
      LOGGER.error("Could not write string.", e);
    }
    if (server != null)
      server.recordSent(channel, totalBytes);
    return totalBytes;
  }
}
