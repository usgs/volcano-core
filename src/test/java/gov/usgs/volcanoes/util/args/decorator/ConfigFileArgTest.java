package gov.usgs.volcanoes.util.args.decorator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;

import gov.usgs.volcanoes.util.args.Args;
import gov.usgs.volcanoes.util.args.Arguments;

public class ConfigFileArgTest {

	private static final String DEFAULT_FILENAME = "defaultFilename.config";
	Arguments arg;

	@Before
	public void setUp() throws JSAPException {
		arg = new ConfigFileArg(DEFAULT_FILENAME, new Args(null, null, new Parameter[0]));
	}

	@Test
	public void when_filenameGiven_then_filenameSet() throws ParseException {
		String configFile = "configFile.config";
		String[] commandLine = { configFile };
		JSAPResult jsapResult = arg.parse(commandLine);
		assertEquals(jsapResult.getString("config-filename"), configFile);
	}

	@Test
	public void when_filenameNotGiven_then_defaultSet() throws ParseException {
		JSAPResult jsapResult = arg.parse(new String[0]);
		assertEquals(jsapResult.getString("config-filename"), DEFAULT_FILENAME);
	}

}
