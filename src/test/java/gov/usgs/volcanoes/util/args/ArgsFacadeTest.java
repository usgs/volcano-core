package gov.usgs.volcanoes.util.args;

import static org.junit.Assert.*;

import org.junit.Test;

import com.martiansoftware.jsap.JSAPException;

public class ArgsFacadeTest {

	@Test
	public void when_run_then_run() throws Exception {
		ArgsFacade.main();
		assertTrue(true);
	}

	@Test
	public void when_runWithArg_then_run() throws Exception {
		ArgsFacade.main("-k");
		assertTrue(true);
	}

}
