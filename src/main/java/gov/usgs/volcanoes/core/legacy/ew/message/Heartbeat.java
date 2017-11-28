package gov.usgs.volcanoes.core.legacy.ew.message;

import java.io.DataInputStream;
import java.io.IOException;


/**
 * A Java implementation of the TYPE_HEARTBEAT earthworm message.
 * 
 * Implementation continues...
 *  
 * @author Tom Parker
 */
public class Heartbeat extends Message {

  String text; // alive text

  public Heartbeat() {
    super();
  }

  public Heartbeat(String t) {
    text = t;
  }

  protected void processBytes(DataInputStream in) throws IOException {}

}
