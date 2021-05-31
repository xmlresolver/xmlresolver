package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xmlresolver.utils.URIUtils;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RddlTest extends CacheManager {
    public static final String catalog = "src/test/resources/docker.xml";
    public static final URI catloc = URIUtils.cwd().resolve(catalog);

    XMLResolverConfiguration config = null;
    ResourceResolver resolver = null;

    @Before
    public void setup() {
        File cache = clearCache("build/rddl-cache");

        config = new XMLResolverConfiguration(catalog);
        config.setFeature(ResolverFeature.CACHE_DIRECTORY, cache.getAbsolutePath());
        resolver = new ResourceResolver(config);

        // Make sure the Docker container is running where we expect.
        ResourceConnection conn = new ResourceConnection("http://localhost:8222/docs/sample/sample.dtd", true);
        assertEquals(200, conn.getStatusCode());
    }

    @Test
    public void cacheTest() {
        // Not yet cached
        Resource dtd = resolver.resolveNamespaceURI("http://localhost:8222/docs/sample",
                "http://www.isi.edu/in-notes/iana/assignments/media-types/application/xml-dtd",
                "http://www.rddl.org/purposes#validation");
        assertNotNull(dtd);
        assertEquals("application/xml-dtd", dtd.contentType());

        // Should now be cached
        dtd = resolver.resolveNamespaceURI("http://localhost:8222/docs/sample",
                "http://www.isi.edu/in-notes/iana/assignments/media-types/application/xml-dtd",
                "http://www.rddl.org/purposes#validation");
        assertNotNull(dtd);
        assertEquals("application/xml-dtd", dtd.contentType());
    }

    @Test
    public void xsdTest() {
        resolver.getConfiguration().setFeature(ResolverFeature.PARSE_RDDL, true);
        Resource xsd = resolver.resolveNamespaceURI("http://localhost:8222/docs/sample",
                "http://www.w3.org/2001/XMLSchema",
                "http://www.rddl.org/purposes#schema-validation");
        assertNotNull(xsd);
        assertEquals("application/xml", xsd.contentType());
    }

    @Test
    public void xslTest() {
        resolver.getConfiguration().setFeature(ResolverFeature.PARSE_RDDL, true);
        Resource xsl = resolver.resolveNamespaceURI("http://localhost:8222/docs/sample",
                "http://www.w3.org/1999/XSL/Transform",
                "http://www.rddl.org/purposes#transformation");
        assertNotNull(xsl);
        assertEquals("application/xml", xsl.contentType());
    }

    @Test
    public void xsdTestNoRddl() {
        resolver.getConfiguration().setFeature(ResolverFeature.PARSE_RDDL, false);
        Resource xsd = resolver.resolveNamespaceURI("http://localhost:8222/docs/sample",
                "http://www.w3.org/2001/XMLSchema",
                "http://www.rddl.org/purposes#schema-validation");
        assertNull(xsd);
    }

    @Test
    public void xslTestNoRddl() {
        resolver.getConfiguration().setFeature(ResolverFeature.PARSE_RDDL, false);
        Resource xsl = resolver.resolveNamespaceURI("http://localhost:8222/docs/sample",
                "http://www.w3.org/1999/XSL/Transform",
                "http://www.rddl.org/purposes#transformation");
        assertNull(xsl);
    }
}

