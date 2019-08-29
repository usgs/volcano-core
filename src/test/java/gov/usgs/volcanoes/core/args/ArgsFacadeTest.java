package gov.usgs.volcanoes.core.args;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
