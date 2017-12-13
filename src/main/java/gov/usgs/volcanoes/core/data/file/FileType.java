package gov.usgs.volcanoes.core.data.file;

import java.io.File;
import java.util.ArrayList;

/*
 * A class to enumerate known seismic file types. When adding a new FileType, also add it to
 * SeismicDataFile.
 * 
 * @author Tom Parker
 */
public enum FileType {
  SAC(".sac", ".*[_\\.](sac|SAC)", "SAC file", false), SEED(".mseed", ".*\\.m?seed",
      "SEED/miniSEED file", true), TEXT(".txt", ".*\\.(txt|mat)", "Matlab-readable text file",
          false), SEISAN(".MAN", ".*\\.(MAN|seisan).*", "Seisan file", true), WIN(".win",
              ".*\\.(win|WIN)", "WIN file",
              true), UNKNOWN(".unknown", ".ukn", "Unknown file type", false);

  public final String extensionRe;
  public final String extension;
  public final String description;
  public final boolean isCollective;
  protected static FileType[] knownTypes;

  static {
    ArrayList<FileType> types = new ArrayList<FileType>();
    for (FileType type : FileType.values()) {
      if (type != UNKNOWN) {
        types.add(type);
      }
    }
    knownTypes = types.toArray(new FileType[0]);
  }

  private FileType(String extension, String extensionRe, String description, boolean isCollective) {
    this.extension = extension;
    this.extensionRe = extensionRe;
    this.description = description;
    this.isCollective = isCollective;
  }

  public String toString() {
    return description;
  }

  /**
   * Get file type based on filename extension.
   * @param fileName name of file
   * @return file type
   */
  public static FileType fromFileName(String fileName) {
    for (FileType t : FileType.values()) {
      if (fileName.matches(t.extensionRe)) {
        return t;
      }
    }
    return UNKNOWN;
  }

  public static FileType fromFile(File f) {
    return fromFileName(f.getPath().toLowerCase());
  }

  public static FileType[] getKnownTypes() {
    return knownTypes;
  }
}
