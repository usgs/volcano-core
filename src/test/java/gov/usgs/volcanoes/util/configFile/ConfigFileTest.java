package gov.usgs.volcanoes.util.configFile;

import static org.junit.Assert.assertEquals;

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

public class ConfigFileTest {

	private static final String CONFIG_FILENAME = "config.config";
	
	private ConfigFile configFile;
	
	@BeforeClass
	public static void setUpClass() throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(CONFIG_FILENAME);
		Path defaultPath = new File(CONFIG_FILENAME).toPath();
		Files.copy(is, defaultPath, StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Before
	public void setUp() throws FileNotFoundException {
		configFile = new ConfigFile(CONFIG_FILENAME);
	}

	@Test(expected = FileNotFoundException.class)
	public void when_configDoesnNotExist_then_throwHelpfulException() throws FileNotFoundException {
		configFile = new ConfigFile("does not exist");
	}
	
	@Test
	public void when_askedForDouble_then_returnDouble() {
		double d = configFile.getDouble("double");
		assertEquals(d, 3.14);
	}

	@Test(expected = NumberFormatException.class)
	public void when_askedForDouble_then_returnError() {
		configFile.getDouble("string");
	}

	@Test
	public void when_askedForInt_then_returnInt() {
		int i = configFile.getInt("int");
		assertEquals(i, 5);
	}

	@Test(expected = NumberFormatException.class)
	public void when_askedForInt_then_returnError() {
		configFile.getDouble("string");
	}

}
