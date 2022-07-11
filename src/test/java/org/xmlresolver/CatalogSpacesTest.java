package org.xmlresolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xmlresolver.sources.ResolverInputSource;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CatalogSpacesTest {
    public static final String catalog = "src/test/resources/spaces-catalog.xml";

    XMLResolverConfiguration config = null;
    Resolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(catalog);
        config.setFeature(ResolverFeature.CACHE_DIRECTORY, null);
        config.setFeature(ResolverFeature.CACHE_UNDER_HOME, false);
        config.setFeature(ResolverFeature.CACHE_ENABLED, false);
        resolver = new Resolver(config);

        // Make sure the Docker container is running where we expect.
        ResourceConnection conn = new ResourceConnection(config, "http://localhost:8222/docs/sample/sample.dtd", true);
        Assert.assertEquals(200, conn.getStatusCode());
    }

    @Test
    public void resolveSystem() {
        try {
            InputSource is = resolver.resolveEntity(null, "https://xmlresolver.org/ns/sample/sample.dtd");
            assertNotNull(is);
            ResolverInputSource ris = (ResolverInputSource) is;
            assertTrue(ris.resolvedURI.toString().contains("/Sample%2010/"));
        } catch (Exception ex) {
            fail();
        }
    }
}

