package gov.usgs.volcanoes.util.args;

import static org.junit.Assert.*;

import org.junit.Test;

import com.martiansoftware.jsap.JSAPException;

import gov.usgs.volcanoes.core.args.ArgsFacade;

/**
 * 
 * @author Tom Parker
 *
 */
public class ArgsFacadeTest {

  /**
   * 
   * @throws Exception when things go wrong
   */
  @Test
  public void when_run_then_run() throws Exception {
    ArgsFacade.main();
    assertTrue(true);
  }

  /**
   * 
   * @throws Exception when things go wrong
   */
  @Test
  public void when_runWithArg_then_run() throws Exception {
    ArgsFacade.main("-k");
    assertTrue(true);
  }
}
