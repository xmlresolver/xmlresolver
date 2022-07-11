/*
 * ResolverTest.java
 * JUnit based test
 *
 * Created on January 2, 2007, 9:14 AM
 */

package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 *
 * @author ndw
 */
public class ResolverTestLocalhost extends CacheManager {
    /* Test of resolve method, of class org.xmlresolver.Resolver. */
    XMLResolverConfiguration config = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration("src/test/resources/domresolver.xml");
        config.setFeature(ResolverFeature.CACHE_DIRECTORY, null);
        config.setFeature(ResolverFeature.CACHE_UNDER_HOME, false);

        // Make sure the Docker container is running where we expect.
        ResourceConnection conn = new ResourceConnection(config, "http://localhost:8222/docs/sample/sample.dtd", true);
        assertEquals(200, conn.getStatusCode());
    }

    @Test
    public void testResolver1() throws Exception {
        SAXParserFactory spfactory = SAXParserFactory.newInstance();
        spfactory.setValidating(true);
        SAXParser parser = spfactory.newSAXParser();
        try {
            parser.parse("src/test/resources/documents/dtdtest.xml", new DevNullHandler(new Resolver(config)));
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testPerformance() throws Exception {
        Resolver resolver = new Resolver(config);

        // These aren't found in the catalog

        Source source = resolver.resolve("http://localhost:8222/docs/sample/sample.xsl","file:/tmp/test.xsl");
        System.err.println("SOURCE1: " + source);
        assertNull(source);
        source = resolver.resolve("../helloworld.xml","http://localhost:8222/docs/sample/sample.xsl");
        System.err.println("SOURCE1: " + source);
        assertNull(source);
    }

    private static class DevNullHandler extends DefaultHandler {
        private Resolver resolver = null;

        public DevNullHandler(Resolver resolver) {
            this.resolver = resolver;
        }
        
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return resolver.resolveEntity(publicId, systemId);
        }
    }
}
