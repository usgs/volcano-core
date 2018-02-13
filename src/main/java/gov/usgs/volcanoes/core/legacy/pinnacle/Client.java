package gov.usgs.volcanoes.core.legacy.pinnacle;

import gov.usgs.volcanoes.core.legacy.net.InternetClient;

/**
 * <p>Abstract client to communicate with Pinnacle tiltmeters.</p>
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 */
abstract public class Client extends InternetClient
{
  /**
   * <p>Constructor</p>
   * @param h host name
   * @param p port
   */
  public Client(String h, int p)
  {
    super(h, p);
  }

  /**
   * <p>Connects to the server, reads a string, checks if the read response is a status block and process it.</p>
   * <p>Close connection and exit on error.</p>
   */
  public void startListening()
  {
    while (true)
    {
      try
      {
        if (!connected())
          connect();

        String s = readString();
        if (s.startsWith("SB: "))
        {
          String sub = s.substring(4);
          int n = sub.length() / 2;
          byte[] buf = new byte[n];
          for (int i = 0; i < n; i++)
          {
            String ss = sub.substring(i * 2, i * 2 + 2);
            int j = Integer.parseInt(ss, 16);
            buf[i] = (byte)j;
          }
          StatusBlock sb = new StatusBlock(buf);
          handleStatusBlock(sb);
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
        close();
      }
    }
  }

  /**
   * <p>Abstract method to process a status block received from the server</p>
   * @param sb sb - Status block to process
   */
  abstract public void handleStatusBlock(StatusBlock sb);

  /**
   * <p>Main method. Calls startListening() to connect. Sends received status blocks to stdout.</p>
   * <p>Syntax is:</p>
   * <p>Client host port</p>
   */
  public static void main(String[] args)
  {
    Client pc = new Client(args[0], Integer.parseInt(args[1]))
    {
      public void handleStatusBlock(StatusBlock sb)
      {
        System.out.println(sb);
      }
    };
    pc.startListening();
  }
}

