package gov.usgs.plot.data;

import gov.usgs.util.Util;

import java.util.LinkedHashSet;

/**
 * <p>Data is a class that holds time series data that can be plotted by the 
 * Valve plotting engine.  A variety of simple tools are available to manipulate
 * and analyze the data, e.g. statistical tools, detrending, etc.</p>
 *
 * <p>The data are stored in a 2-D double array (somewhat inefficient in Java, 
 * should be 1-D or Colt matrices).  The first column is always time, in j2ksec.
 * The subsequent columns are data associated with that time.  The visible array
 * is used to specify which columns should be displayed.</p>
 *
 * <p>Technically, you do not have to have time-series data in this object.
 * Any 2-D data set is fine.  However, many of the functions that deal with
 * time would become nonsensical.</p>
 * 
 * <p>This class should probably not be part of the gov.usgs.plot package.
 * If it is repackaged the visibility functionality should be moved to a 
 * different class.</p>
 *
 * TODO: use Colt (or make a GenericDataMatrix plotter)
 *
 * @author Dan Cervelli
 */
public class Data
{
	/** A flag to indicate that this is not a valid data point.
	*/
	// public static double NO_DATA = Double.MAX_VALUE;
	public static double NO_DATA = Double.NaN;
	
	/** Shortcut for natural log of 10.
	 */
    public static double LN10 = Math.log(10);
	/** Shortcut for natural log of 10.
	 */
	public static double LOG10 = Math.log(10);
	
	/** The data.
	 */
    private double[][] data;
	
	/** A flag that specifies whether the data are time-sorted.  If so, some 
	 * optimizations might be made.
	 */
    private boolean sorted;
	
	/** The visibility array that specifies whether a column should be 
	 * displayed.
	 */
    private boolean[] visible;
    
    /** Constructor from a 2-D double array.
	 * @param d the data
	 */
    public Data(double[][] d)
    {
        data = d;
		if (d == null)
			return;
        visible = new boolean[d[0].length - 2];
        for (int i = 0; i < visible.length; i++)
            visible[i] = true;
    }
    
    public Data subset(int[] columns)
    {
    	double[][] d = new double[data.length][columns.length];
    	for (int i = 0; i < data.length; i++)
    	{
    		for (int j = 0; j < columns.length; j++)
    		{
    			d[i][j] = data[i][columns[j]];
    		}
    	}
    	return new Data(d);
    }
    
	/** Gets the size of the data (number of rows).
	 * @return the number of rows of data
	 */
    public int size()
    {
        return data.length;
    }
    
	/** Sets the visibility of each of the columns.  This isn't very safe 
	 * because it doesn't check the size of the incoming array.  This should 
	 * be replaced with a method that sets a specific index to a value.
	 * @param b the new visiblity array
	 */
    public void setVisible(boolean[] b)
    {
        visible = b;
    }
    
	/** Gets the visibility of a column.
	 * @param column the column index to check
	 * @return whether or not the column is visible
	 */
    public boolean isVisible(int column)
    {
        return visible[column];
    }
    
	/** Gets the data array.
	 * @return the data
	 */
    public double[][] getData()
    {
        return data;
    }
    
	/** Gets the number of rows of data. Synonym for size().
	 * @return the number of rows of data
	 */
    public int getNumRows()
    {
        return data.length;
    }
    
	/** Gets the number of columns.
	 * @return the number of columns
	 */
    public int getNumColumns()
    {
        if (data == null || data.length == 0)
            return 0;
        else
            return data[0].length;
    }
    
	/** Gets the minimum time for this data.
	 * @return the minimum time, in j2ksec
	 */
    public double getMinTime()
    {
        if (data == null || data.length == 0)
            return Double.NaN;
        if (sorted)
            return data[0][0];
        else
        {
            double min = 1E300;
            for (int i = 0; i < data.length; i++)
                if (data[i][0] < min)
                    min = data[i][0];
                
            return min;
        }
    }
    
	/** Gets the maximum time for this data.
	 * @return the maximum time, in j2ksec
	 */
    public double getMaxTime()
    {
        if (data == null || data.length == 0)
            return Double.NaN;
        if (sorted)
            return data[0][data[0].length - 1];
        else
        {
            double max = -1E300;
            for (int i = 0; i < data.length; i++)
                if (data[i][0] > max)
                    max = data[i][0];
                
            return max;
        }
    }

	/** Gets the minimum data value in the data from visible columns.
	 * @return the minimum data
	 */
    public double getMinData()
    {
        if (data == null || data.length == 0)
            return Double.NaN;
        double min = 1E300;
        for (int i = 0; i < data.length; i++)
            for (int j = 1; j < data[0].length; j++)
                if (visible[j - 1] && data[i][j] != NO_DATA && data[i][j] < min)
                    min = data[i][j];
        
        //System.out.println("min: " + min);
        return min;
    }

	/** Gets the maximum data value in the data from visible columns.
	 * @return the maximum data
	 */
    public double getMaxData()
    {
        if (data == null || data.length == 0)
            return Double.NaN;
        double max = -1E300;
        for (int i = 0; i < data.length; i++)
            for (int j = 1; j < data[0].length; j++)
                if (visible[j - 1] && data[i][j] != NO_DATA && data[i][j] > max)
                    max = data[i][j];
        
        //System.out.println("max: " + max);
        return max;
    }
    
    /** Gets the distinct entries of a column
     * @return double array of distinct entries
     */
    public double[] getDistinctEntries(int column) {
    	
    	if (data == null || data.length == 0) {
    		return null;
    	}
    	  
    	Double value;
    	LinkedHashSet<Double> set	= new LinkedHashSet<Double>();
    	
    	for (int i = 0; i < data.length; i++) {
    		if (data[i][column] != NO_DATA) {
    		value	= data[i][1];
    		set.add(value);
    		}
    	}
    	
    	double[] values = new double[set.size()];
    	int cursor	= 0;
    	for (Double d: set) {
    		values[cursor++] = d;
    	}
    	
    	return values;
    }
    
	/** Gets the mean (average) of a column.
	 * @param column the column number
	 * @return the mean
	 */
    public double getMean(int column)
    {
        double sum = 0;
        int nodatas = 0;
        for (int i = 0; i < data.length; i++)
    	{
    		if (data[i][column] == NO_DATA)
    			nodatas++;
    		else
            	sum += data[i][column];
        }
        
        // shouldn't this be - nodatas ???
        return sum / (data.length - nodatas);
        // return sum / (data.length + nodatas);
    }
    
	/** Subtracts the mean of a column from that column.
	 * @param column the column index
	 */
    public void subtractMean(int column)
    {
        double mean = getMean(column);
        for (int i = 0; i < data.length; i++)
        	if (data[i][column] != NO_DATA)
            	data[i][column] -= mean;
    }
    
	/** Gets the minimum value from a column.
	 * @param column the column index
	 * @return the minimum value
	 */
    public double getMin(int column)
    {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < data.length; i++)
            if (data[i][column] != NO_DATA && data[i][column] < min)
                min = data[i][column];
        
        return min;
    }
    
	/** Gets the maximum value from a column.
	 * @param column the column index
	 * @return the maximum value
	 */
    public double getMax(int column)
    {
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < data.length; i++)
            if (data[i][column] != NO_DATA && data[i][column] > max)
                max = data[i][column];
        
        return max;
    }
    
	/** Adds a value to a column.
	 * @param column the column index
	 * @param val the value
	 */
    public void add(int column, double val)
    {
        for (int i = 0; i < data.length; i++)
            if (data[i][column] != NO_DATA) {
            	data[i][column] += val;
            }
    }
    
	/** Removes the mean from a column.  NEEDS SUPPORT FOR NO_DATA!
	 * @param column the column index
	 */
    public void unbias(int column)
    {
        double ym = getMean(column);

        for (int i = 0; i < data.length; i++)
        	// Ask Asta about this approach
        	if (data[i][column] != NO_DATA) {
        		data[i][column] -= ym;
        	}
    }
    
	/** Removes the linear trend from a column.  NEEDS SUPPORT FOR NO_DATA!
	 * @param column the column index
	 */
    public void detrend(int column)
    {
        double ym = getMean(column);
        double xm = getMean(0);
        
        double ssxy = 0;
        double ssxx = 0;
        for (int i = 0; i < data.length; i++)
        {
        	// Ask Asta about this approach
        	if (data[i][column] != NO_DATA) {
	            ssxy += (data[i][0] - xm) * (data[i][column] - ym);
	            ssxx += (data[i][0] - xm) * (data[i][0] - xm);
        	}
        }
        double m = ssxy / ssxx;
        //System.out.println(m);
        double b = ym - m * xm;
        //System.out.println(b);
        for (int i = 0; i < data.length; i++)
            data[i][column] -= data[i][0] * m + b;
    }
    
	/** Returns the least squares fit line from a column.  Data are returned
	 * as a double array, first element slope, second element y-intercept.
	 * NEEDS SUPPORT FOR NO_DATA!
	 * @param column the column index
	 * @return the slope and y-intercept of the line
	 */
    public double[] leastSquares(int column)
    {
        double ym = getMean(column);
        double xm = getMean(0);
        
        double ssxy = 0;
        double ssxx = 0;
        for (int i = 0; i < data.length; i++)
        {
        	// Ask Asta about this approach
        	if (data[i][column] != NO_DATA) {
        		ssxy += (data[i][0] - xm) * (data[i][column] - ym);
        		ssxx += (data[i][0] - xm) * (data[i][0] - xm);
        	}
        }
        double m = ssxy / ssxx;
        //System.out.println(m);
        double b = ym - m * xm;
        return new double[] {m, b};
    }

	/** Applys the base 10 log to a column.
	 * @param column the column index
	 */
    public void log10(int column)
    {
        for (int i = 0; i < data.length; i++)
        	if (data[i][column] != NO_DATA)
            	data[i][column] = Math.log(data[i][column]) / LN10;
    }
    
	/** Adjusts the data values of the visible columns so that they can be
	 * stacked on the same graph.  This was once used on the GPS time series
	 * section but is no longer in use.
	 * @param interval the interval size to put between the columns
	 */
    public void stack(double interval)
    {
        int nv = 0;
        for (int i = 0; i < visible.length; i++)
            if (visible[i])
                nv++;
        
        if (nv <= 1)
            return;
        
        double add = (nv / 2) * interval;
           
        for (int i = 1; i < data[0].length; i++)
        {
            if (visible[i - 1])
            {
                add(i, add);
                add -= interval;
            }
        }
    }
    
	/** Gets whether or not this data is sorted.
	 * @return the sorted flag
	 */
    public boolean isSorted()
    {
        return sorted;
    }
    
	/** Sets the sorted flag.
	 * @param b the new sorted flag state
	 */
    public void setSorted(boolean b)
    {
        sorted = b;
    }

    /**
     * Prints content to stdout
     */
    public void output()
    {
    	for (int i = 0; i < data.length; i++)
    	{
    		for (int j = 0; j < data[i].length; j++)
    			System.out.print(data[i][j] + "\t");
    		System.out.println();
    	}
    }
    
    /**
     * Dumps content to CSV string
     */
    public String toCSV()
    {
    		StringBuffer sb = new StringBuffer();
    		for (int i = 0; i < data.length; i++)
    		{
    			sb.append(Util.j2KToDateString(data[i][0]) + ",");
    			for (int j = 1; j < data[0].length; j++)
    				sb.append(data[i][j] + ",");
    			if (sb.charAt(sb.length()-1) == ',')
    				sb.setCharAt(sb.length()-1, '\n');
    		}
    		return sb.toString();
    }
    
}
