package gov.usgs.volcanoes.util.data;

/**
 * A class for holding SCNL data.
 * 
 * @author Tom Parker
 */
public class Scnl implements Comparable<Scnl> {

    public static final String DELIMITER = "$";
    public static final String DEFAULT_LOCATION = "--";

    public final String station;
    public final String channel;
    public final String network;
    public final String location;

    public Scnl(String s, String c, String n, String l) {
        station = s;
        channel = c;
        network = n;
        location = l;
    }

    public Scnl(String s, String c, String n) {
        station = s;
        channel = c;
        network = n;
        location = DEFAULT_LOCATION;
    }

    public String toString() {
        return toString(DELIMITER);
    }

    public String toString(String delimiter) {
        StringBuffer sb = new StringBuffer();
        sb.append(station).append(delimiter).append(channel).append(delimiter).append(network).append(delimiter)
                .append(location);
        return sb.toString();
    }

    public int compareTo(Scnl scnl) {
        return (this.toString().compareTo(scnl.toString()));
    }
}
