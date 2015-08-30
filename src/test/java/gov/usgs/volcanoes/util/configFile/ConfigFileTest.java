package gov.usgs.volcanoes.util.configFile;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

public class ConfigFileTest {

	private static final String CONFIG_FILENAME = "config.config";
	
	private ConfigFile configFile;
	
	@Before
	public void setUp() {
	}

	@Test(expected = FileNotFoundException.class)
	public void test() throws FileNotFoundException {
		configFile = new ConfigFile("does not exist");
	}

}
