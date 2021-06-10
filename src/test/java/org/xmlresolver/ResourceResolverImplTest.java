package org.xmlresolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ResourceResolverImplTest {
    public static final String catalog = "src/test/resources/catalog.xml";

    XMLResolverConfiguration config = null;
    Resolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(catalog);
        config.setFeature(ResolverFeature.CACHE_DIRECTORY, "/tmp/y/cache");
        resolver = new Resolver(config);

        // Make sure the Docker container is running where we expect.
        ResourceConnection conn = new ResourceConnection("http://localhost:8222/docs/sample/sample.dtd", true);
        Assert.assertEquals(200, conn.getStatusCode());
    }

    @Test
    public void uriForSystemFail() {
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, false);
        try {
            InputSource is = resolver.resolveEntity(null, "https://xmlresolver.org/ns/sample-as-uri/sample.dtd");
            assertNull(is);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void uriForSystemSuccess() {
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        try {
            InputSource is = resolver.resolveEntity(null, "https://xmlresolver.org/ns/sample-as-uri/sample.dtd");
            assertEquals("http://localhost:8222/docs/sample/sample.dtd", is.getSystemId());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

}

