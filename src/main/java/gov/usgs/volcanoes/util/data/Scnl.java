package gov.usgs.volcanoes.util.data;

/**
 * A class for holding SCNL data.
 * 
 * @author Tom Parker
 */
public class Scnl {

	public static final String DELIMITER="$";
	public static final String DEFAULT_LOCATION="--";
	
	public final String station;
	public final String channel;
	public final String network;
	public final String location;
	
	public Scnl(String s, String c, String n, String l)
	{
		station = s;
		channel = c;
		network = n;
		location = l;
	}
	
	public Scnl(String s, String c, String n)
	{
		station = s;
		channel = c;
		network = n;
		location = DEFAULT_LOCATION;
	}
		

	/**
	 * Compare another SCNL to this SCNL. A null location is different than a "--" location
	 * @param scnl
	 * @return Are they equal?
	 */
	public boolean equals(Scnl scnl)
	{
		if (!station.equals(scnl.station))
			return false;
		if (!channel.equals(scnl.channel))
			return false;
		if (!network.equals(scnl.network))
			return false;
		if (!location.equals(scnl.location))
			return false;
		
		return true;
	}
	
	public String toString() {
		return toString(DELIMITER);
	}
	
	public String toString(String delimiter)
	{
		return String.join(delimiter, station, channel, network, location);
	}
}
