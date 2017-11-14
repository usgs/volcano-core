package gov.usgs.volcanoes.core.legacy.ew.message;
//package gov.usgs.earthworm.message;
//
//import gov.usgs.util.Util;
//import gov.usgs.vdx.data.wave.Wave;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//
//
///**
// * A Java implementation of the TYPE_TRACEBUF2 earthworm message.
// * <code>startTime</code> and <code>endTime</code> are stored in their native
// * format: decimal seconds since Jan 1. 1970.<br>
// * <br>
// * The data fields in this class were derived from <code>trace_buf.h</code>
// * in the earthworm source.<br>
// *
// * TRACEBUF2 is similar but distinct from TRACEBUF. The work to separate 
// * them continues
// *  
// * @author Tom Parker
// */
//public class TraceBuf2 extends TraceBuf {
//
//	/** Location code. */
//	public String location;
//
//	/** version */
//	public String version;
//
//	/** Generic constructor. */
//	public TraceBuf2() 
//	{
//		super();
//	} 
//
//	public TraceBuf2(byte[] b) throws IOException
//	{
//		super(b);
//		processBytes(new DataInputStream(new ByteArrayInputStream(b)));
//	}
//
//	/** 
//	 * Constructs a <code>TraceBuf</code> from an array of bytes.
//	 * @param b the bytes 
//	 * @param i the number of bytes in the message (!= b.length)
//	 * @throws IOException
//	 */
//	protected TraceBuf2(byte[] b, int i, int seq) throws IOException
//	{
//		super(b, i, seq);
//	}
//
//
//	/**
//	 * Creates a TraceBuf from a Wave.  This is useful for putting non-
//	 * Earthworm data in a Winston database.
//	 * @param code '$' separated SCNL
//	 * @param sw the wave
//	 */
//	public TraceBuf2(String code, Wave sw)
//	{
//		super(code, sw);
//		location = code.split("\\$")[3];
//	}
//
//	public void createBytes()
//	{
//
//		try
//		{
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			DataOutputStream out = new DataOutputStream(baos);
//			out.writeInt(pin);
//			out.writeInt(numSamples);
//			out.writeDouble(firstSampleTime);
//			out.writeDouble(lastSampleTime());
//			out.writeDouble(samplingRate());
//
//			int p = 7 - station.length();
//			out.writeBytes(station);
//			for (int i = 0; i < p; i++)
//				out.write((byte)0);
//
//			p = 9 - network.length();
//			out.writeBytes(network);
//			for (int i = 0; i < p; i++)
//				out.write((byte)0);
//
//			p = 4 - channel.length();
//			out.writeBytes(channel);
//			for (int i = 0; i < p; i++)
//				out.write((byte)0);
//
//			p = 5 - location.length();
//			out.writeBytes(location);
//			for (int i = 0; i < p; i++)
//				out.write((byte)0);
//
//			out.writeBytes("s4");
//			for (int i = 0; i < 5; i++)
//				out.write((byte)0);
//
//			for (int i = 0; i < data.length; i++)
//				out.writeInt(data[i]);
//			out.write((byte)0);
//			bytes = baos.toByteArray();
//		}
//		catch (IOException e)
//		{}
//	}
//
//	/**
//	 * 
//	 * @param b
//	 * @param i
//	 * @return
//	 * @throws IOException
//	 */
//	public static Message createFromBytes(byte[] b, int i, int seq) throws IOException
//	{
//		TraceBuf2 tb = new TraceBuf2(b, i, seq);
//		tb.processBytes(new DataInputStream(new ByteArrayInputStream(tb.bytes)));
//		return tb;
//	}
//	
//	/**
//	 * Fills the fields based on raw message data.
//	 *   
//	 * Adapted from TRACE2_HEADER in trace_buf.h
//	 *  
//	 * @param in the input stream
//	 * @throws IOException
//	 */
//
//	public void processBytes(DataInputStream in, boolean traceBuf2) throws IOException
//	{
//		pin = in.readInt();
//		numSamples = in.readInt();
//		double startTime = in.readDouble();
//		in.readDouble(); // discard lastSampleTime; calculate it instead
//		double samplingRate = in.readDouble();
//		
//		byte[] buf = new byte[32];
//		in.read(buf, 0, 32);
//		station = Util.bytesToString(buf, 0, 7).trim();
//		network = Util.bytesToString(buf, 7, 9).trim();
//		channel = Util.bytesToString(buf, 16, 4).trim();
//		location = Util.bytesToString(buf, 20, 3).trim();
//		version = Util.bytesToString(buf, 23, 3).trim();
//		dataType = Util.bytesToString(buf, 25, 3).trim();
//		quality = Util.bytesToString(buf, 28, 2).trim();
//		pad = Util.bytesToString(buf, 30, 2).trim();
//
//		if (dataType.charAt(0) == 'i') {
//			pin = Util.swap(pin);
//			numSamples = Util.swap(numSamples);
//			startTime = (long) Util.swap(startTime);
//			samplingRate = Util.swap(samplingRate);
//		}
//		firstSampleTime = Math.round(startTime * USEC);
//		samplingPeriod = Math.round(USEC / samplingRate);
//
//		data = readData(in);
//	}
//
//	/**
//	 * Fills the fields based on raw message data.  There is a flag to specify
//	 * whether this is a TRACEBUF or a TRACEBUF2.  In theory, this could be avoided
//	 * if the version field in TRACEBUF2 was checked but there are some issues
//	 * with this, so I leave it to the user to choose.
//	 *  
//	 * @param in the input stream
//	 * @param traceBuf2 whether this is a TRACEBUF2
//	 * @throws IOException
//	 */
//	public void processBytes(DataInputStream in) throws IOException
//	{
//		pin = in.readInt();
//		numSamples = in.readInt();
//		double startTime = in.readDouble();
//		in.readDouble(); // discard lastSampleTime; calculate it instead
//		double samplingRate = in.readDouble();
//		
//		byte[] buf = new byte[32];
//		in.read(buf, 0, 32);
//		station = Util.bytesToString(buf, 0, 7).trim();
//		network = Util.bytesToString(buf, 7, 9).trim();
//		channel = Util.bytesToString(buf, 16, 4).trim();
//		location = Util.bytesToString(buf, 20, 3).trim();
//
//		dataType = Util.bytesToString(buf, 25, 3).trim();
//		quality = Util.bytesToString(buf, 28, 2).trim();
//		boolean swap = dataType.charAt(0) == 'i';
//		if (swap)
//		{
//			pin = Util.swap(pin);
//			numSamples = Util.swap(numSamples);
//			firstSampleTime = Util.swap(firstSampleTime);
//			samplingRate = Util.swap(samplingRate);
//		}
//
//		boolean isShort = (dataType.charAt(1) == '2');
//		data = new int[numSamples];
//		for (int i = 0; i < numSamples; i++)
//		{
//			if (isShort)
//				data[i] = (int)(swap ? Util.swap(in.readShort()) : in.readShort());
//			else
//				data[i] = swap ? Util.swap(in.readInt()) : in.readInt();
//		}
//	}
//	/** 
//	 * Gets a string describing this TYPE_TRACEBUF2 message.
//	 * @return the string
//	 */
//	public String toString()
//	{
//		return "TYPE_TRACEBUF2" + ": " + station + " " + channel + " " + network + ", " + firstSampleTime + "->" + lastSampleTime() + "," + dataType + " " + quality + " " + numSamples;
//	}
//
//	public String toLogString()
//	{
//		return String.format("TRACEBUF2: %s %s %s %s, %d, %s, %.2f", 
//				station, channel, network, location, 
//				numSamples, dataType, firstSampleTime);
//	}
//
//	/** 
//	 * Gets the channel name formatted for Winston.
//	 * @return the formatted channel name
//	 */
//	public String toWinstonString()
//	{
//		return station + "$" + channel + "$" + network + "$" + location;
//	}
//
//}
