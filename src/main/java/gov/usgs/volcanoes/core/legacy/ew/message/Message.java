package gov.usgs.volcanoes.core.legacy.ew.message;


/**
 * A class for Earthworm messages.
 * 
 * @author Dan Cervelli, Tom Parker
 */

// TODO: make abstract
public class Message {
  public MessageLogo logo;
  public int seq;
  public boolean sendAck;
  public byte[] bytes;

  public Message() {}

  public Message(byte[] b, int i) {
    int logoLength = 9;

    logo = new MessageLogo(b);

    bytes = new byte[i - logoLength];
    System.arraycopy(b, logoLength, bytes, 0, bytes.length);
    sendAck = false;
  }

  public Message(byte[] b, int i, int s) {
    int logoLength = 9;
    seq = s;

    sendAck = (s != Integer.MIN_VALUE) ? true : false;

    logo = new MessageLogo(b);

    bytes = new byte[i - logoLength];
    System.arraycopy(b, logoLength, bytes, 0, bytes.length);

  }

  public String toString() {
    return logo.toString() + " length=" + bytes.length;
  }

  public String bytesToString() {
    return new String(bytes);
  }
}
