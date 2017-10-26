package gov.usgs.plot.data;

import java.nio.ByteBuffer;

/**
 * Represent set of binary data
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 */
public interface BinaryDataSet
{
	/**
	 * @return internal binary data as ByteBuffer
	 */
	public ByteBuffer toBinary();
	
	/**
	 * Init internal data from ByteBuffer
	 * @param bb ByteBuffer of internal data
	 */
	public void fromBinary(ByteBuffer bb);
}
