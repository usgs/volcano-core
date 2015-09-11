package gov.usgs.volcanoes.util.args.decorator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;

import gov.usgs.volcanoes.core.args.Args;
import gov.usgs.volcanoes.core.args.Arguments;
import gov.usgs.volcanoes.core.args.decorator.ConfigFileArg;
import gov.usgs.volcanoes.core.args.decorator.CreateConfigArg;

/**
 * 
 * @author Tom Parker
 *
 */
public class CreateConfigArgTest {

  private static final String DEFAULT_FILENAME = "defaultFilename.config";
  private static final String EXAMPLE_FILENAME = "Version.java.template";
  private Arguments arg;
  private File file;

  /**
   * 
   * @throws JSAPException when things go wrong
   */
  @Before
  public void setUp() throws JSAPException {
    arg = new ConfigFileArg(DEFAULT_FILENAME, new Args(null, null, new Parameter[0]));
    arg = new CreateConfigArg(EXAMPLE_FILENAME, arg);

    file = new File(DEFAULT_FILENAME);
  }

  /**
   * 
   */
  @After
  public void tearDown() {
    if (file.exists())
      file.delete();
  }

  /**
   * 
   * @throws JSAPException when things go wrong
   */
  @Test(expected = JSAPException.class)
  public void when_configFileNotCalled_then_trowHelpfulException() throws JSAPException {
    arg = new CreateConfigArg(EXAMPLE_FILENAME, new Args(null, null, new Parameter[0]));
  }

  /**
   * 
   * @throws Exception when things go wrong
   */
  @Test
  public void when_givenFlag_then_fileCreated() throws Exception {
    arg.parse(new String[] {"--create-config"});

    assertTrue(file.exists());
  }

  /**
   * 
   * @throws Exception when things go wrong
   */
  @Test
  public void when_notGivenFlag_then_noFileCreated() throws Exception {
    arg.parse(new String[0]);

    assertFalse(file.exists());
  }


}
