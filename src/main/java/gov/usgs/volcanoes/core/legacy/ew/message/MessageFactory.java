package gov.usgs.volcanoes.core.legacy.ew.message;


import java.io.IOException;

/**
 * A factory for creating the correct subclass of <code>Message</code>.
 *
 * @author Dan Cervelli
 */
public class MessageFactory {
  /**
   * Creates the correct type of message from a buffer of bytes.
   * 
   * @param b byte buffer
   * @param i number of bytes that comprise message (!= b.length)
   * @param findSeq Search for sequence number in message?
   * @return the message
   * @throws IOException
   */
  public static Message createMessage(byte[] b, int i, int seq) throws IOException {
    MessageLogo logo = new MessageLogo(b);
    Message msg = null;
    switch (logo.getType()) {
      case TYPE_TRACEBUF:
        msg = TraceBuf.createFromBytes(b, i, seq);
        break;
      case TYPE_TRACEBUF2:
        msg = TraceBuf.createFromBytesAsTraceBuf2(b, i, seq);
        break;
      case TYPE_HEARTBEAT:
        msg = new Message(b, i, seq);
        break;
      default:
        System.out.println("Unknown logo type: " + logo.getType());
        msg = new Message(b, i, seq);
        break;
    }

    return msg;
  }
}
