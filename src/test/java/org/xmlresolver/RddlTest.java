package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;

public class RddlTest {
    public static final String catalog = "src/test/resources/docker.xml";
    private static final String relativeCacheDir = "build/rddl-cache";

    XMLResolverConfiguration config = null;
    XMLResolver resolver = null;

    @BeforeEach
    public void setup() {
        config = new XMLResolverConfiguration(catalog);
        resolver = new XMLResolver(config);

        // Make sure the Docker container is running where we expect.
        ResourceConnection conn = new ResourceConnection(URI.create("http://localhost:8222/docs/sample/sample.dtd"));
        conn.get(config, true);
        Assertions.assertEquals(200, conn.getStatusCode());
    }

    @Test
    public void xsdTest() {
        resolver.getConfiguration().setFeature(ResolverFeature.PARSE_RDDL, true);
        ResourceRequest req = resolver.getRequest("http://localhost:8222/docs/sample",
                null,
                "http://www.w3.org/2001/XMLSchema",
                "http://www.rddl.org/purposes#schema-validation");
        ResourceResponse xsd = resolver.resolve(req);
        Assertions.assertTrue(xsd.isResolved());
        Assertions.assertEquals("application/xml", xsd.getContentType());
    }

    @Test
    public void xslTest() {
        resolver.getConfiguration().setFeature(ResolverFeature.PARSE_RDDL, true);
        ResourceRequest req = resolver.getRequest("http://localhost:8222/docs/sample",
                null,
                "http://www.w3.org/1999/XSL/Transform",
                "http://www.rddl.org/purposes#transformation");
        ResourceResponse xsl = resolver.resolve(req);
        Assertions.assertTrue(xsl.isResolved());
        Assertions.assertEquals("application/xml", xsl.getContentType());
    }

    @Test
    public void xslTestBaseURI() {
        resolver.getConfiguration().setFeature(ResolverFeature.PARSE_RDDL, true);
        ResourceRequest req = resolver.getRequest("sample",
                "http://localhost:8222/docs/",
                "http://www.w3.org/1999/XSL/Transform",
                "http://www.rddl.org/purposes#transformation");
        ResourceResponse xsl = resolver.resolve(req);
        Assertions.assertTrue(xsl.isResolved());
        Assertions.assertEquals("application/xml", xsl.getContentType());
    }

    @Test
    public void xsdTestNoRddl() {
        resolver.getConfiguration().setFeature(ResolverFeature.PARSE_RDDL, false);
        resolver.getConfiguration().setFeature(ResolverFeature.ALWAYS_RESOLVE, true);
        ResourceRequest req = resolver.getRequest("http://localhost:8222/docs/sample",
                null,
                "http://www.w3.org/2001/XMLSchema",
                "http://www.rddl.org/purposes#schema-validation");
        ResourceResponse xsd = resolver.resolve(req);
        Assertions.assertTrue(xsd.isResolved());
        // Extra "/" because Apache redirects to the directory listing
        Assertions.assertEquals("http://localhost:8222/docs/sample/", xsd.getURI().toString());

        resolver.getConfiguration().setFeature(ResolverFeature.ALWAYS_RESOLVE, false);
        req = resolver.getRequest("http://localhost:8222/docs/sample",
                null,
                "http://www.w3.org/2001/XMLSchema",
                "http://www.rddl.org/purposes#schema-validation");
        xsd = resolver.resolve(req);
        Assertions.assertFalse(xsd.isResolved());
    }

    @Test
    public void xslTestNoRddl() {
        resolver.getConfiguration().setFeature(ResolverFeature.PARSE_RDDL, false);
        resolver.getConfiguration().setFeature(ResolverFeature.ALWAYS_RESOLVE, true);
        ResourceRequest req = resolver.getRequest("http://localhost:8222/docs/sample",
                null,
                "http://www.w3.org/1999/XSL/Transform",
                "http://www.rddl.org/purposes#transformation");
        ResourceResponse xsl = resolver.resolve(req);
        Assertions.assertTrue(xsl.isResolved());
        // Extra "/" because Apache redirects to the directory listing
        Assertions.assertEquals("http://localhost:8222/docs/sample/", xsl.getURI().toString());

        resolver.getConfiguration().setFeature(ResolverFeature.ALWAYS_RESOLVE, false);
        req = resolver.getRequest("http://localhost:8222/docs/sample",
                null,
                "http://www.w3.org/1999/XSL/Transform",
                "http://www.rddl.org/purposes#transformation");
        xsl = resolver.resolve(req);
        Assertions.assertFalse(xsl.isResolved());
    }

    @Disabled
    public void xmlTest() {
        // This test is ignored because getting the XSD file from the W3C server takes ten seconds
        // and the test doesn't really prove anything anyway.
        resolver.getConfiguration().setFeature(ResolverFeature.PARSE_RDDL, true);
        ResourceRequest req = resolver.getRequest("http://www.w3.org/2001/xml.xsd",
                null,
                "http://www.w3.org/2001/XMLSchema",
                "http://www.rddl.org/purposes#schema-validation");
        ResourceResponse xsd = resolver.resolve(req);
        Assertions.assertFalse(xsd.isResolved());
    }

    @Test
    public void xmlTestResolved() {
        // This test gets the xml.xsd file from the catalog, so it runs quickly and proves
        // that we parse the resolved resource, not the original URI.
        XMLResolverConfiguration config = new XMLResolverConfiguration("src/test/resources/catalog.xml");
        config.setFeature(ResolverFeature.PARSE_RDDL, true);
        XMLResolver resolver = new XMLResolver(config);

        ResourceRequest req = resolver.getRequest("http://www.w3.org/XML/1998/namespace",
                null,
                "http://www.w3.org/2001/XMLSchema",
                "http://www.rddl.org/purposes#validation");
        ResourceResponse xsd = resolver.resolve(req);
        Assertions.assertTrue(xsd.isResolved());
        Assertions.assertTrue(xsd.getUnmaskedURI().toString().endsWith("/xml.xsd"));
    }
}

