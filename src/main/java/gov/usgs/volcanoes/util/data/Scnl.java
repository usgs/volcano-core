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
        String me = String.format("%s$%s$%s$%s", network, station, channel, location);
        String other = String.format("%s$%s$%s$%s", scnl.network, scnl.station, scnl.channel, scnl.location);
                
        return (me.compareTo(other));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final Scnl other = (Scnl) obj;
 
        if ((this.station == null) ? (other.station != null) : !this.station.equals(other.station)) {
            return false;
        }
        if ((this.channel == null) ? (other.channel != null) : !this.channel.equals(other.channel)) {
            return false;
        }
        if ((this.network == null) ? (other.network != null) : !this.network.equals(other.network)) {
            return false;
        }
        if ((this.location == null) ? (other.location != null) : !this.location.equals(other.location)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        
        hash = 31 * hash + (this.station != null ? this.station.hashCode() : 0);
        hash = 31 * hash + (this.channel != null ? this.channel.hashCode() : 0);
        hash = 31 * hash + (this.network != null ? this.network.hashCode() : 0);
        hash = 31 * hash + (this.location != null ? this.location.hashCode() : 0);
 
        return hash;
    }
}
