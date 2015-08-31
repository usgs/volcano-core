package gov.usgs.volcanoes.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class VersionTest {

    @Test
    public void testBuildTime() {
        assertNotNull(Version.BUILD_TIME);
    }

    @Test
    public void testPomVersion() {
        assertNotNull(Version.POM_VERSION);
    }

    @Test
    public void testVersionString() {
        assertNotNull(Version.VERSION_STRING);
    }
}
