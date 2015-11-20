package gov.usgs.volcanoes.core.configFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import gov.usgs.volcanoes.core.configfile.ConfigFile;

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
  public void setUp() throws FileNotFoundException {
    configFile = new ConfigFile(CONFIG_FILENAME);
  }

  /**
   * 
   * @throws FileNotFoundException when things go right
   */
  @Test(expected = FileNotFoundException.class)
  public void when_configDoesnNotExist_then_throwHelpfulException() throws FileNotFoundException {
    configFile = new ConfigFile("does not exist");
  }

  /**
   * 
   */
  @Test
  public void when_askedForDouble_then_returnDouble() {
    double d = configFile.getDouble("double");
    assertEquals(d, 3.14);
  }


  /**
   * 
   */
  @Test
  public void when_askedForInt_then_returnInt() {
    int anInt = configFile.getInt("int");
    assertEquals(anInt, 5);
  }

  /**
   * 
   */
  @Test
  public void when_askedForDouble_then_returnDefault() {
    double aDouble = configFile.getDouble("absent", 2.1);
    assertEquals(aDouble, 2.1);
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
  @Test
  public void when_askedForInt_then_returnDefault() {
    int anInt = configFile.getInt("absent", 1);    
    assertEquals(anInt, 1);
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
}