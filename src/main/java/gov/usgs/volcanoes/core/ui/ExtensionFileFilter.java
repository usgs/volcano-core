package gov.usgs.volcanoes.core.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A simple extension file filter for a <code>JFileChooser</code>.
 *
 * @author Dan Cervelli
 */
public class ExtensionFileFilter extends FileFilter {
  protected String description;
  protected String extension;

  /**
   * Constructor.
   * 
   * @param extenstion file extension for filtering, in lower-case
   * @param description filter description string
   */
  public ExtensionFileFilter(String extenstion, String description) {
    this.extension = extenstion;
    this.description = description;
  }

  /**
   * Decide if file is accepted.
   * 
   * @param file File
   * @return true if accepted, false otherwise
   */
  @Override
  public boolean accept(File file) {
    return (!file.isDirectory() && file.getPath().toLowerCase().endsWith(extension));
  }

  /**
   * Long description.
   * 
   * @return information string - "description(extension)"
   */
  @Override
  public String getDescription() {
    return description + " (" + extension + ")";
  }

  /**
   * Extension accessor.
   * 
   * @return extension
   */
  public String getExtension() {
    return extension;
  }

  /**
   * Description accessor.
   * 
   * @return filter description
   */
  public String getExtensionDescription() {
    return description;
  }
}
