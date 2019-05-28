package gov.usgs.volcanoes.core.data.file;


import gov.usgs.volcanoes.core.data.Wave;
import gov.usgs.volcanoes.core.quakeml.Pick;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An abstract class representing a file containing seismic data.
 * 
 * @author Tom Parker
 */
public abstract class SeismicDataFile {

  protected final String groupName;
  protected final String fileName;
  protected Map<String, Wave> waves;
  protected Map<String, ArrayList<Pick>> picks;
  protected String network;
  protected String station;
  protected String channel;
  protected String location;


  protected SeismicDataFile(String fileName, String groupName) {
    this.fileName = fileName;
    this.groupName = groupName;
    waves = new HashMap<String, Wave>();
    picks = new HashMap<String, ArrayList<Pick>>();
  }

  public abstract void read() throws IOException;

  public abstract void write() throws IOException;

  public void setNetwork(String network) {
    this.network = network;
  }

  public void setStation(String station) {
    this.station = station;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getGroup() {
    return groupName + fileName;
  }

  public Set<String> getChannels() {
    return waves.keySet();
  }

  public ArrayList<Pick> getPicks(String channel) {
    return picks.get(channel);
  }

  public Wave getWave(String channel) {
    return waves.get(channel);
  }

  public String getFileName() {
    return fileName;
  }

  public void putWave(String channel, Wave wave) {
    waves.put(channel, wave);
  }

  /**
   * Put pick.
   * @param channel channel associated with pick
   * @param pick pick 
   */
  public void putPick(String channel, Pick pick) {
    ArrayList<Pick> pickList = picks.get(channel);
    if (pickList == null) {
      pickList = new ArrayList<Pick>();
      pickList.add(pick);
      picks.put(channel, pickList);
    } else {
      pickList.add(pick);
    }
  }

  public static SeismicDataFile getFile(File file, FileType fileType) {
    return getFile(file.getPath(), fileType);
  }


  public static SeismicDataFile getFile(File file) {
    return getFile(file.getPath());
  }

  public static SeismicDataFile getFile(String fileName) {
    return getFile(fileName, FileType.fromFileName(fileName));
  }

  /**
   * Get seismic data file object from file.
   * @param fileName name of file to read data from
   * @param fileType format of the file
   * @return seismic data file object
   */
  public static SeismicDataFile getFile(String fileName, FileType fileType) {
    switch (fileType) {
      case SAC:
        return new SacDataFile(fileName);
      case SEED:
        return new SeedDataFile(fileName);
      case TEXT:
        return new TextDataFile(fileName);
      case SEISAN:
        return new SeisanDataFile(fileName);
      case WIN:
        return new WinDataFile(fileName);
      default:
        return null;
    }
  }
}
