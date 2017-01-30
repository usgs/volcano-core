/**
 * I waive copyright and related rights in the this work worldwide
 * through the CC0 1.0 Universal public domain dedication.
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.core.quakeml;

/**
 * Possible event types.
 * 
 * @author Tom Parker
 *
 */
public enum EventType {
  NOT_EXISTING("not existing"),
  NOT_REPORTED("not reported"),
  EARTHQUAKE("earthquake"),
  ANTHROPOGENIC_EVENT("anthropogenic event"),
  COLLAPSE("collapse"),
  CAVITY_COLLAPSE("cavity collapse"),
  MINE_COLLAPSE("mine collapse"),
  BUILDING_COLLAPSE("building collapse"),
  EXPLOSION("explosion"),
  ACCIDENTAL_EXPLOSION("accidental explosion"),
  CHEMICAL_EXPLOSION("chemical explosion"),
  CONTROLLED_EXPLOSION("controlled explosion"),
  EXPERIMENTAL_EXPLOSION("experimental explosion"),
  INDUSTRIAL_EXPLOSION("industrial explosion"),
  MINING_EXPLOSION("mining explosion"),
  QUARRY_BLAST("quarry blast"),
  ROAD_CUT("road cut"),
  BLASTING_LEVEE("blasting levee"),
  NUCLEAR_EXPLOSION("nuclear explosion"),
  INDUCED_OR_TRIGGERED_EVENT("induced or triggered event"),
  ROCK_BURST("rock burst"),
  RESERVOIR_LOADING("reservoir loading"),
  FLUID_INJECTION("fluid injection"),
  FLUID_EXTRACTION("fluid extraction"),
  CRASH("crash"),
  PLANE_CRASH("plane crash"),
  TRAIN_CRASH("train crash"),
  BOAT_CRASH("boat crash"),
  OTHER_EVENT("other event"),
  ATMOSPHERIC_EVENT("atmospheric event"),
  SONIC_BOOM("sonic boom"),
  SONIC_BLAST("sonic blast"),
  ACOUSTIC_NOISE("acoustic noise"),
  THUNDER("thunder"),
  AVALANCHE("avalanche"),
  SNOW_AVALANCHE("snow avalanche"),
  DEBRIS_AVALANCHE("debris avalanche"),
  HYDROACOUSTIC_EVENT("hydroacoustic event"),
  ICE_QUAKE("ice quake"),
  SLIDE("slide"),
  LANDSLIDE("landslide"),
  ROCKSLIDE("rockslide"),
  METEORITE("meteorite"),
  VOLCANIC_ERUPTION("volcanic eruption");
      
  private final String description;

  private EventType(String description) {
    this.description = description;
  }
  
  public String toString() {
    return description;
  }

  /**
   * Parse a string.
   * 
   * @param inString String to parse
   * @return matching EventType
   */
  public static EventType parse(String inString) {
    for (EventType type : EventType.values()) {
      if (type.description.equals(inString.toLowerCase())) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid type " + inString);

  }

}
