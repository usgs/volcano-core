package gov.usgs.volcanoes.core.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A simple extension file filter for a <code>JFileChooser</code>.
 * 
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2005/08/26 17:05:04  uid879
 * Initial avosouth commit.
 *
 * @author Dan Cervelli
 */
public class ExtensionFileFilter extends FileFilter
{
    protected String extension;
    protected String description;
 
    /**
     * Constructor
     * @param e file extension for filtering, in lower-case
     * @param d filter description string
     */
    public ExtensionFileFilter(String e, String d)
    {
        extension = e;
        description = d;
    }
    
    /**
     * Is file accepted?
     * @param f File
     * @return true if accepted, false otherwise
     */
    public boolean accept(File f)
	{
	    return (!f.isDirectory() && f.getPath().toLowerCase().endsWith(extension));
	}
	
    /**
     * Get description
     * @return information string - "description(extension)"
     */
	public String getDescription()
	{
	    return description + " (" + extension + ")";
	}
	
	/**
	 * Getter for filter description
	 * @return filter description
	 */
	public String getExtensionDescription()
	{
		return description;
	}

	/**
	 * Getter for extension
	 * @return extension
	 */
	public String getExtension()
	{
		return extension;
	}
}
