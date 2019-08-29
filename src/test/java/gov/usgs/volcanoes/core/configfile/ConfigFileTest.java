package gov.usgs.volcanoes.core.configfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Tom Parker
 *
 */
public class ConfigFileTest {
  private static final String CONFIG_FILENAME = "config.config";

  private ConfigFile configFile;

  /**
   * 
   * @throws IOException when things go wrong
   */
  @BeforeClass
  public static void setUpClass() throws IOException {
    InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(CONFIG_FILENAME);
    Path defaultPath = new File(CONFIG_FILENAME).toPath();
    Files.copy(is, defaultPath, StandardCopyOption.REPLACE_EXISTING);
  }

  /**
   * 
   * @throws FileNotFoundException when things go wrong
   */
  @Before
  public void setUp() {
    configFile = new ConfigFile(CONFIG_FILENAME);
  }


  @Test
  public void when_askedForValue_then_returnValue() {
    double d = configFile.getDouble("double");
    assertEquals(d, 3.14, 0);

    int anInt = configFile.getInt("int");
    assertEquals(anInt, 5);

    long aLong = configFile.getLong("long");
    assertEquals(aLong, 6L);
  }


  /**
   * 
   */
  @Test
  public void when_askedForValue_then_returnDefault() {
    double aDouble = configFile.getDouble("absent", 2.1);
    assertEquals(aDouble, 2.1, 0);

    int anInt = configFile.getInt("absent", 1);
    assertEquals(anInt, 1);

    long aLong = configFile.getLong("absent", 4L);
    assertEquals(aLong, 4L);
  }


  @Test(expected = NumberFormatException.class)
  public void when_askedForDouble_then_returnError() {
    configFile.getDouble("string");
  }


  /**
   * 
   */
  @Test(expected = NumberFormatException.class)
  public void when_askedForInt_then_returnError() {
    configFile.getInt("string");
  }

  /**
   * 
   */
  @Test(expected = NumberFormatException.class)
  public void when_askedForLong_then_returnError() {
    configFile.getLong("string");
  }



  /**
   * 
   */
  @Test
  public void when_askedForString_then_returnString() {
    configFile.getString("string");
  }

  /**
   * 
   */
  @Test
  public void when_askedForString_then_returnDefault() {
    String aString = configFile.getString("absent", "default");
    assertTrue("default".equals(aString));
  }


  /**
   * 
   */
  @Test
  public void when_askedSubconfig_then_returnSubconfig() {
    ConfigFile config = configFile.getSubConfig("first");
    assertEquals(config.getString("second"), "secondLevelKey");
  }

  /**
   * 
   */
  @Test
  public void when_askedForBoolean_then_returnBoolean() {
    configFile.getBoolean("yes");
  }

  /**
   * 
   */
  @Test
  public void when_askedInheritSubconfig_then_returnSubconfig() {
    ConfigFile config = configFile.getSubConfig("first", true);
    assertEquals(config.getString("first"), "firstLevelKey");
  }

  /**
   * 
   */
  @Test
  public void when_askedConfig_then_returnConfig() {
    ConfigFile config = configFile.getSubConfig("first", true);
    assertEquals(config.getString("first"), "firstLevelKey");
  }

  /**
   * 
   */
  @Test
  public void when_writeConfig_then_writeConfig() {
    String writeFile = "writeTest";

    configFile.writeToFile(writeFile);
    assertTrue(new File(writeFile).exists());
  }

  /**
   * 
   */
  @Test
  public void when_stringRepresentationRequested_then_stringRepresentationReturned() {
    assertNotNull(configFile.toString());
  }

  /**
   * 
   */
  @Test
  public void when_givenNonexistantFile_then_returnEmptyObject() {
    ConfigFile config = new ConfigFile("iDontExist");
    assertEquals(config.getConfig().size(), 0);
  }

  /**
   * 
   */
  @Test(expected = RuntimeException.class)
  public void when_booleanIsNull_then_returnException() {
    ConfigFile config = new ConfigFile();
    config.put("null", null);
    config.getBoolean("null");
  }

  /**
   * 
   */
  @Test(expected = RuntimeException.class)
  public void when_booleanIsNotBoolean_then_returnException() {
    ConfigFile config = new ConfigFile();
    config.put("notbool", "5");
    config.getBoolean("notbool");
  }


  /**
   * 
   */
  @Test
  public void when_booleanIsBoolean_then_returnBoolean() {
    ConfigFile config = new ConfigFile();
    config.put("bool", "t");
    assertEquals(config.getBoolean("bool", false), true);
  }

  /**
   * 
   */
  @Test
  public void when_booleanIsNotBoolean_then_returnDefault() {
    ConfigFile config = new ConfigFile();
    config.put("bool", "other");
    assertEquals(config.getBoolean("bool", false), false);
  }
}
