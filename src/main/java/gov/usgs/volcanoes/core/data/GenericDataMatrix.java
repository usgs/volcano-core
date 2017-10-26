package gov.usgs.plot.data;

import gov.usgs.math.Butterworth;
import gov.usgs.math.Filter;
import gov.usgs.util.Log;
import gov.usgs.util.Util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * BinaryDataSet to store cern's DoubleMatrix2D and meta information about matrix column's names
 *
 * @author Dan Cervelli
 */
public class GenericDataMatrix implements BinaryDataSet
{
	protected final static Logger logger = Log.getLogger("gov.usgs.vdx.data.GenericDataMatrix"); 
	protected DoubleMatrix2D data;
	protected HashMap<String, Integer> columnMap;
	
	/**
	 * Default constructor
	 */
	public GenericDataMatrix()
	{
		data = null;
		columnMap = new HashMap<String, Integer>();
		setColumnNames();
	}
	
	/**
	 * Construct GenericDataMatrix from given 2d matrix
	 * @param d 2d matrix
	 */
	public GenericDataMatrix(DoubleMatrix2D d)
	{
		this();
		data = d;
	}
	
	/**
	 * Create a GenericDataMatrix from a byte buffer.  
	 * @param bb the byte buffer
	 */
	public GenericDataMatrix(ByteBuffer bb)
	{
		this();
		fromBinary(bb);
	}

	/**
	 * Create a GenericDataMatrix from a List<double[]>.  
	 * @param list of double arrays
	 */
	public GenericDataMatrix(List<double[]> list)
	{
		this();
		if (list == null || list.size() == 0)
			return;
		
		int rows = list.size();
		int cols = list.get(0).length;
		
		data = DoubleFactory2D.dense.make(rows, cols);
		for (int i = 0; i < rows; i++)
		{
			double[] d = list.get(i);
			for (int j = 0; j < cols; j++)
				data.setQuick(i, j, d[j]);
		}
	}
	
	/**
	 * Returns content as ByteBuffer
	 * @return content as ByteBuffer
	 */
	public ByteBuffer toBinary()
	{
		int rows = rows();
		int cols = columns();
		ByteBuffer bb = ByteBuffer.allocate(4 + (rows * cols) * 8);
		bb.putInt(rows);
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
				bb.putDouble(data.getQuick(i, j));
		}
		return bb;
	}
	
	/**
	 * Init content from ByteBuffer
	 * @param bb content
	 */
	public void fromBinary(ByteBuffer bb)
	{
		int rows = bb.getInt();
		int cols = ((bb.limit() - 4) / rows) / 8;
		data = DoubleFactory2D.dense.make(rows, cols);
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
				data.setQuick(i, j, bb.getDouble());
		}		
	}
	
	/**
	 * Dumps content as CSV
	 * @return string w/ content in CSV format
	 */
	public String toCSV()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < rows(); i++)
		{
			sb.append(Util.j2KToDateString(data.getQuick(i, 0)) + ",");
			for (int j = 1; j < columns(); j++)
			{
				sb.append(data.getQuick(i, j));
				sb.append(",");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/** Sets the data matrix.
	 * @param d the data
	 */
	public void setData(DoubleMatrix2D d)
	{
		data = d;
	}

	/**
	 * Sets names of matrix columns
	 */
	public void setColumnNames()
	{}


	/**
	 * Sets names of matrix conumns
	 * @param s Array of strings - column names
	 */
	public void setColumnNames(String[] s)
	{
		int i = 0;
		for (String name : s)
			columnMap.put(name, i++);
	}

	/**
	 * Gets names of matrix conumns
	 * @return Array of strings - column names
	 */
	public String[] getColumnNames()
	{
		String[] c = new String[columnMap.size()];
		for (String s : columnMap.keySet())
			c[columnMap.get(s)] = s;
		
		return c;
	}
	
	/** 
	 * Gets the number of rows in the data.
	 * @return the row count
	 */
	public int rows()
	{
		if (data != null)
			return data.rows();
		else
			return 0;
	}

	/** 
	 * Gets the number of columns in the data.
	 * @return the column count
	 */
	public int columns()
	{
		if (data != null)
			return data.columns();
		else 
			return 0;
	}
	
	/**
	 * Perform user-defined arithmetic on a column
	 * @param c column name to perform operation on
	 * @param o operation to perform
	 * @param v value to add/subtract/multiply/divide
	 */
	public void doArithmetic(String c, String o, double v)
	{
		Integer i = columnMap.get(c);
		if (i != null)
			doArithmetic(i, o, v);
	}
	
	/**
	 * Perform user-defined arithmetic on a column
	 * @param c column to perform operation on
	 * @param o operation to perform
	 * @param v value to add/subtract/multiply/divide
	 */
	public void doArithmetic(int c, String o, double v)
	{
		if (o.equalsIgnoreCase("multiply")) {
			mult(c, v);
		} else if (o.equalsIgnoreCase("divide")) {
			mult(c, 1/v);
		}
	}

	/**
	 * Add double value to one column
	 * @param c column name to add
	 * @param v value to add
	 */
	public void add(String c, double v)
	{
		Integer i = columnMap.get(c);
		if (i != null)
			add(i, v);
	}
	
	/**
	 * Add double value to one column
	 * @param c column number to add
	 * @param v value to add
	 */
	public void add(int c, double v)
	{
		for (int i = 0; i < rows(); i++) {
			if (!Double.isNaN(data.getQuick(i, c))) {
				data.setQuick(i, c, data.getQuick(i, c) + v);
			}
		}
	}

	/**
	 * Multiply one column to double value
	 * @param c name of column to multiply
	 * @param v value to multiply
	 */
	public void mult(String c, double v)
	{
		Integer i = columnMap.get(c);
		if (i != null)
			mult(i, v);
	}

	/**
	 * Multiply one column to double value
	 * @param c number of column to multiply
	 * @param v value to multiply
	 */
	public void mult(int c, double v)
	{
		for (int i = 0; i < rows(); i++) {
			if (!Double.isNaN(data.getQuick(i, c))) {
				data.setQuick(i, c, data.getQuick(i, c) * v);
			}
		}
	}

	/**
	 * Sums column, value in resulting column is sum of all previous raws.
	 * @param c column to sum
	 */
	public void sum(int c)
	{
		for (int i=1; i<rows(); i++)
		{
			double d = data.getQuick(i-1,c);
			d += data.getQuick(i,c);
			data.setQuick(i,c,d);
		}
	}
	
	/**
	 * Accumulates columns, but only if current value is greater than previous value.  
	 * Useful for rainfall data
	 * @param c column to accumulate
	 */
	public void accumulate(int c) {
		
		if (data.rows() > 0) {

			double total	= 0;
			double r		= 0;
			double last		= data.getQuick(0, c);
			
			// set the initial amount of rainfall to be zero for this time period
			data.setQuick(0, c, 0);
			
			// iterate through all subsequent rows and assign a rainfall amount if the 
			// data increases.  Keep the total of the rainfall is less than the previous reading
			for (int i = 1; i < data.rows(); i++) {
				r = data.getQuick(i, c);
				if (!Double.isNaN(r)) {
					if (r < last) {
						last = 0;
					}
					total += (r - last);
					last = r;
					data.setQuick(i, c, total);
				}
			}
		}
	}

	/**
	 * Performs data detrending
	 * @param c number of column to detrend
	 */
	public void detrend(int c) {

        double xm	= mean(0);
		double ym	= mean(c);
        double ssxx	= 0;
        double ssxy	= 0;        
        for (int i = 0; i < rows(); i++) {
			if (!Double.isNaN(data.getQuick(i, c))) {
				ssxy += (data.getQuick(i, 0) - xm) * (data.getQuick(i, c) - ym);
				ssxx += (data.getQuick(i, 0) - xm) * (data.getQuick(i, 0) - xm);
			}
        }
        
        double m	= ssxy / ssxx;
        double b	= ym - m * xm;
        for (int i = 0; i < rows(); i++) {
			if (!Double.isNaN(data.getQuick(i, c))) {
				data.setQuick(i, c, data.getQuick(i, c) - (data.getQuick(i, 0) * m + b));
			}
        }
	}
	
	/**
	 * Despike data from column c using period p
	 * @param c column to despike
	 * @param p period used for despiking
	 */
	public void despike(int c, double p ) {
		set2mean( c, p );
	}
	/**
	 * Filter data
	 * 
	 * @param bw
	 *            Butterworth filter to apply
	 * @param zeroPhaseShift
	 *            flag for no phase shift
	 */
	public void filter(Butterworth bw, int columnIndex, boolean zeroPhaseShift) {
		
		double mean = mean(columnIndex);

		double[] dBuf = new double[data.rows() + (int) (data.rows() * 0.5)];
		Arrays.fill(dBuf, mean);
		int trueStart = (int) (data.rows() * 0.25);
		for (int i = 0; i < data.rows(); i++) {
			if (data.getQuick(i, columnIndex) != Double.NaN)
				dBuf[i + trueStart] = data.getQuick(i, columnIndex);
		}

		// bw.setSamplingRate(getSamplingRate());
		bw.create();
		Filter.filter(dBuf, bw.getSize(), bw.getXCoeffs(), bw.getYCoeffs(),
				bw.getGain(), 0, 0);
		if (zeroPhaseShift) {
			double[] dBuf2 = new double[dBuf.length];
			for (int i = 0, j = dBuf.length - 1; i < dBuf.length; i++, j--)
				dBuf2[j] = dBuf[i];

			Filter.filter(dBuf2, bw.getSize(), bw.getXCoeffs(),
					bw.getYCoeffs(), bw.getGain(), 0, 0);

			for (int i = 0, j = dBuf2.length - 1 - trueStart; i < data.rows(); i++, j--)
				data.setQuick(i, columnIndex, dBuf2[j]);
		} else {
			for (int i = 0; i < data.rows(); i++)
				data.setQuick(i, columnIndex, dBuf[i + trueStart]);
		}
		// invalidateStatistics();
	}

	/**
	 * Replace data in column c with rolling mean of period p
	 * @param c column to change
	 * @param p period used for rolling mean
	 */
	public void set2mean( int c, double p ) {
		int j = 0; // index of oldest value in window
		double jtime = data.getQuick(0,0); // time of oldest value in window
		List<Integer> nans = null;
		Meaner window = new Meaner();
		window.add( data.getQuick(0,c) );
		int r = rows();
		for ( int i=1; i<r; i++ ) {
			double itime = data.getQuick(i,0);
			double ival = data.getQuick(i,c);
			if (Double.isNaN(ival)) {
				if (nans == null)
					nans = new ArrayList<Integer>();
				nans.add(i);
				continue;
			}
			
			window.add(ival);
			// While oldest value is outside period, remove it
			while ( itime - jtime > p ) {
				window.removeOldest();
				boolean keepgoing = false;
				do {
					keepgoing = false;
					j++;
					if (nans != null && nans.size() > 0 && j == nans.get(0)) {
						nans.remove(0);
						keepgoing = true;
					}
				} while (keepgoing && j < i);
				jtime = data.getQuick(j,0);
			}
			data.setQuick(i,c,window.avg());
		}
	}

	/**
	 * Replace data in column c with rolling median of period p
	 * @param c column to change
	 * @param p period used for rolling median
	 */
	public void set2median( int c, double p ) {
		int j = 0; // index of oldest value in window
		double jtime = data.getQuick(0,0); // time of oldest value in window
		List<Integer> nans = null;
		Medianer window = new Medianer();
		window.add( data.getQuick(0,c) );
		int r = rows();
		for ( int i=1; i<r; i++ ) {
			double itime = data.getQuick(i,0);
			double ival = data.getQuick(i,c);
			if (Double.isNaN(ival)) {
				if (nans == null)
					nans = new ArrayList<Integer>();
				nans.add(i);
				continue;
			}
			
			window.add(ival);
			// While oldest value is outside period, remove it
			while ( itime - jtime > p ) {
				window.removeOldest();
				boolean keepgoing = false;
				do {
					keepgoing = false;
					j++;
					if (nans != null && nans.size() > 0 && j == nans.get(0)) {
						nans.remove(0);
						keepgoing = true;
					}
				} while (keepgoing && j < i);
				jtime = data.getQuick(j,0);
			}
			data.setQuick(i,c,window.avg());
		}
	}
	
	/** Class to maintain a FIFO of doubles & report its mean 
	 */
	private class Meaner {
		private LinkedList<Double> data;	// the values
		private double sum;					// their sum
		
		Meaner() {
			data = new LinkedList<Double>();
			sum = 0;
		}
		
		/** Add val to the queue 
		 * 
		 * @param val value to add
		 */
		public void add( double val ) {
			data.addLast( val );
			sum += val;
		}
		
		/** Remove the oldest value from the queue
		 * 
		 * @return value removed
		 */
		public double removeOldest() {
			if (data.size() > 0) {
				Double datum = data.removeFirst();
				sum -= datum;
				return datum;
			} else
				return Double.NaN;
		}
		
		/** Mean of values in queue
		 * 
		 * @return the mean
		 */
		public double avg() {
			return sum / data.size();
		}
	}
	
		
	/** Class to maintain a FIFO of doubles & report its median 
	 */
	private class Medianer {
		
		private class MultiMap {
			private TreeMap<Double,LinkedList<Integer>> mm;
			private TreeMap<Integer,Double>unmm;
			
			MultiMap() {
				mm = new TreeMap<Double,LinkedList<Integer>>();
				unmm = new TreeMap<Integer,Double>();
			}
			
			protected void put( Double key, Integer val ) {
				LinkedList<Integer> entry = mm.get( key );
				if ( entry == null ) {
					entry = new LinkedList<Integer>();
				}
				unmm.put( val, key );
				if ( entry.size() == 0 ) {
					entry.add( val );
					mm.put( key, entry );
					return;
				}
				if ( val < entry.getFirst() )
					entry.addFirst( val );
				else
					entry.addLast( val );
			}
			
			protected Double lastKey() {
				return mm.lastKey();
			}
			
			protected Integer removeLastIndex( Double key ) {
				LinkedList<Integer> entry = mm.get( key );
				// entry should not be null!
				Integer index = entry.removeLast();
				if ( entry.size() == 0 )
					mm.remove( key );
				unmm.remove( index );
				return index;
			}
			
			protected Double firstKey() {
				return mm.firstKey();
			}
			
			protected Integer removeFirstIndex( Double key ) {
				LinkedList<Integer> entry = mm.get( key );
				// entry should not be null!
				Integer index = entry.removeFirst();
				if ( entry.size() == 0 )
					mm.remove( key );
				unmm.remove( index );
				return index;
			}
			
			public int size() {
				return unmm.size();
			}
			
			public void addLo( Double key, Integer val, MultiMap other ) {
				if ( other.size() > 0 && key >= other.firstKey() ) {
					other.put( key, val );
					key = other.firstKey();
					val = other.removeFirstIndex( key );
				}
				put( key, val );
			}
						
			public void addHi( Double key, Integer val, MultiMap other ) {
				if ( other.size() > 0 && key <= other.lastKey() ) {
					other.put( key, val );
					key = other.lastKey();
					val = other.removeLastIndex( key );
				}
				put( key, val );
			}
			
			public boolean indexIsMember( Integer val ) {
				return unmm.containsKey( val );
			}
			
			public boolean removeIndex( Integer val ) {
				Double key = unmm.remove( val );
				if ( key == null )
					return false;
				List<Integer> entry = mm.get( key );
				if ( entry.size() == 1 )
					mm.remove( key );
				else
					entry.remove( val );
				return true;
			}
			
			public void dump() {
				for ( Double d: mm.keySet() ) {
					List<Integer> d_ids = mm.get(d);
					if ( d_ids == null || d_ids.size() == 0 )
						continue;
					System.out.print( d );
					if ( d_ids.size() > 1 )
						System.out.print( "x" + mm.get(d).size());
					System.out.print( " " );
				}
			}
		}		

		private MultiMap loHalf;	// values in lower half
		private MultiMap hiHalf;	// values in upper half
		private int idx1, idx2;	// values window have indices idx1..idx2
		
		Medianer() {
			// Invariant: |loHalf| - |hiHalf| = 0 or 1
			//				All keys in loHalf <= all keys in hiHalf
			loHalf = new MultiMap();
			hiHalf = new MultiMap();
			idx1 = 0;
			idx2 = -1;
		}
		
		/** Add val to the queue 
		 * 
		 * @param val value to add
		 */
		public void add( double val ) {
			idx2++;
			if ( loHalf.size() == hiHalf.size() )
				loHalf.addLo( val, idx2, hiHalf );
			else
				hiHalf.addHi( val, idx2, loHalf );
		}
		
		/**
		 * Dumps median data to stdout
		 */
		public void dump() {
			System.out.print( "[" );
			loHalf.dump();
			System.out.print( "]:[" );
			hiHalf.dump();
			System.out.println("]");
		}

		/** Remove the oldest value from the queue
		 * 
		 * @return value removed
		 */
		public void removeOldest() {
			if ( !loHalf.removeIndex( idx1 ) ) {
				hiHalf.removeIndex( idx1 );
			}
			idx1++;
			int loSize = loHalf.size();
			int hiSize = hiHalf.size();
			if ( loSize < hiSize ) {
				// Shift min of hi to lo
				Double d = hiHalf.firstKey();
				Integer ix = hiHalf.removeFirstIndex( d );
				loHalf.put( d, ix );
			} else if ( loSize > hiSize+1 ) {
				// Shift max of lo to hi
				Double d = loHalf.lastKey();
				Integer ix = loHalf.removeLastIndex( d );
				hiHalf.put( d, ix );
			}
		}
		
		/** Median of values in queue
		 * 
		 * @return the median
		 */
		public double avg() {
			if ( loHalf.size() == hiHalf.size() )
				return (loHalf.lastKey() + hiHalf.firstKey()) / 2;
			return loHalf.lastKey();
		}
	}

	/**
	 * Get first value in column
	 * @param c column number
	 */
	public double first(int c)
	{
		if ( rows() == 0 )
			return Double.NaN;
		return data.getQuick(0,c);
	}


	/**
	 * Get maximum value in column
	 * @param c column number
	 * @return maximum of column
	 */
	public double max(int c)
	{
		double m = -1E300;
		for (int i = 0; i < rows(); i++) {
			if (!Double.isNaN(data.getQuick(i, c))) {
				m = Math.max(m, data.getQuick(i, c));
			}
		}
		if (m == -1E300) m = 0;
		return m;
	}

	/**
	 * Get minimum value in column
	 * @param c column number
	 * @return minimum of column
	 */
	public double min(int c)
	{
		double m = 1E300;
		for (int i = 0; i < rows(); i++) {
			if (!Double.isNaN(data.getQuick(i, c))) {
				m = Math.min(m, data.getQuick(i, c));
			}
		}
		if (m == 1E300) m = 0;
		return m;
	}
	
	/**
	 * Get mean value in column
	 * @param c column number
	 * @return mean of column
	 */
	public double mean(int c)
	{
		double t = 0;
		double j = 0;
		int r = rows();
		for (int i = 0; i < r; i++) {
			double val = data.getQuick(i, c);
			if (!Double.isNaN(val)) {
				t += val;
				j++;
			}
		}
		if (j == 0) {
			return 0;
		} else {
			return t / j;
		}
	}
    
	/** Returns the least squares fit line from a column.  Data are returned
	 * as a double array, first element slope, second element y-intercept.
	 * NEEDS SUPPORT FOR NO_DATA!
	 * @param c the column index
	 * @return the slope and y-intercept of the line
	 */
    public double[] leastSquares(int c)
    {
        double ym = mean(c);
        double xm = mean(0);
        
        double ssxy = 0;
        double ssxx = 0;
        for (int i = 0; i < data.rows(); i++)
        {
        	// Ask Asta about this approach
        	if (!Double.isNaN(data.getQuick(i, c))) {
        		ssxy += (data.getQuick(i, 0) - xm) * (data.getQuick(i, c) - ym);
        		ssxx += (data.getQuick(i, 0) - xm) * (data.getQuick(i, 0) - xm);
        	}
        }
        double m = ssxy / ssxx;
        double b = ym - m * xm;
        return new double[] {m, b};
    }
	
	/**
	 * @return (0,0) value of matrix
	 */
	public double getStartTime()
	{		
		if (data == null || data.size() == 0)
			return Double.NaN;
		else if (data.rows() == 1 && Double.isNaN(data.get(0, 0)))
			return 0;
		else
			return data.get(0,0);
	}

	/**
	 * @return (rows()-1,0) value of matrix
	 */
	public double getEndTime()
	{
		if (data == null || data.size() == 0)
			return Double.NaN;
		else if (data.rows() == 1 && Double.isNaN(data.get(0, 0)))
			return 0;
		else
			return data.get(rows()-1,0);
	}
	
	/**
	 * Adds a value to the time column (for time zone management).
	 * @param adj the time adjustment
	 */
	public void adjustTime(double adj)
	{
		add(0, adj);
	}

	/** 
	 * Gets the time column (column 1) of the data.
	 * @return the time column
	 */
	public DoubleMatrix2D getTimes()
	{
		return data.viewPart(0, 0, rows(), 1);
	}

	/** Gets a data column. 
	 * @return the data column
	 */
	public DoubleMatrix2D getColumn(int c)
	{
		return data.viewPart(0, c, rows(), 1);
	}
	
	/**
	 * Gets a data column.
	 * @param c Column name
	 * @return the data column
	 */
	public DoubleMatrix2D getColumn(String c)
	{
		Integer i = columnMap.get(c);
		if (i != null)
			return getColumn(i);
		else 
			return null;
	}

	/** Gets the data matrix.
	 * @return the data
	 */
	public DoubleMatrix2D getData()
	{
		return data;
	}

	/**
	 * Contatenate two matrix
	 * @param dm matrix to concatenate with this one
	 */
	public void concatenate(GenericDataMatrix dm)
	{
		DoubleMatrix2D[][] ms = new DoubleMatrix2D[2][1];
		ms[0][0] = data;
		ms[1][0] = dm.getData();
		data = DoubleFactory2D.dense.compose(ms);
	}
	
	/**
	 * @return size of memory occuped by data matrix, in bytes
	 */
	public int getMemorySize()
	{
		return (data.rows() * data.columns() * 8);	
	}
}
