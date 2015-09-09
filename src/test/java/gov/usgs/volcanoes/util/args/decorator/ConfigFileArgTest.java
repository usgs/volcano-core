package gov.usgs.volcanoes.util.args.decorator;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;

import gov.usgs.volcanoes.core.args.Args;
import gov.usgs.volcanoes.core.args.Arguments;
import gov.usgs.volcanoes.core.args.decorator.ConfigFileArg;

/**
 * 
 * @author tparker
 *
 */
public class ConfigFileArgTest {

  private static final String DEFAULT_FILENAME = "defaultFilename.config";
  Arguments arg;

  /**
   * 
   * @throws JSAPException when things go wrong
   */
  @Before
  public void setUp() throws JSAPException {
    arg = new ConfigFileArg(DEFAULT_FILENAME, new Args(null, null, new Parameter[0]));
  }

  /**
   * 
   * @throws Exception when things go wrong
   */
  @Test
  public void when_filenameGiven_then_filenameSet() throws Exception {
    String configFile = "configFile.config";
    String[] commandLine = {configFile};
    JSAPResult jsapResult = arg.parse(commandLine);
    assertEquals(jsapResult.getString("config-filename"), configFile);
  }

  /**
   * 
   * @throws Exception when things go wrong
   */
  @Test
  public void when_filenameNotGiven_then_defaultSet() throws Exception {
    JSAPResult jsapResult = arg.parse(new String[0]);
    assertEquals(jsapResult.getString("config-filename"), DEFAULT_FILENAME);
  }
}
