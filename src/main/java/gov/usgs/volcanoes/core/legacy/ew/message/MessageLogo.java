package gov.usgs.volcanoes.core.legacy.ew.message;

import gov.usgs.volcanoes.core.util.ByteUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A class for dealing with Earthworm message logos.
 * 
 * @author Dan Cervelli
 */
public class MessageLogo {
  private MessageType type;
  private byte module;
  private byte installationID;


  public MessageLogo() {}

  public MessageType getType() {
    return type;
  }

  public void setType(MessageType t) {
    type = t;

  }

  public void setModule(byte b) {
    module = b;
  }

  public byte getModule() {
    return module;
  }

  public void setInstallationId(byte b) {
    installationID = b;
  }

  public byte getInstallationId() {
    return installationID;
  }

  public MessageLogo(byte[] b) {
    this(b, 0);
  }

  public MessageLogo(byte[] b, int offset) {
    String s = new String(b, offset, 9);
    installationID = ByteUtil.intToByte(Integer.parseInt(s.substring(0, 3).trim()));
    module = ByteUtil.intToByte(Integer.parseInt(s.substring(3, 6).trim()));
    type = MessageType.fromInt(Integer.parseInt(s.substring(6, 9).trim()));
  }

  private NumberFormat numberFormat;

  public byte[] toDataStreamBytes() {
    if (numberFormat == null)
      numberFormat = new DecimalFormat("000");

    String message = numberFormat.format(ByteUtil.byteToInt(installationID))
        + numberFormat.format(ByteUtil.byteToInt(module)) + numberFormat.format(type.getType());

    byte[] b = new byte[9];
    for (int i = 0; i < b.length; i++)
      b[i] = ByteUtil.intToByte((int) message.charAt(i));

    return b;
  }

  public String toString() {
    return "Message: type=" + type + " module=" + module + " inst=" + installationID;
  }

}
