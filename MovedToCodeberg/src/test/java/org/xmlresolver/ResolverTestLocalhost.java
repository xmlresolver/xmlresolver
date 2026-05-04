/*
 * ResolverTest.java
 * JUnit based test
 *
 * Created on January 2, 2007, 9:14 AM
 */

package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author ndw
 */
public class ResolverTestLocalhost {
    /* Test of resolve method, of class org.xmlresolver.Resolver. */
    XMLResolverConfiguration config = null;

    @BeforeEach
    public void setup() {
        config = new XMLResolverConfiguration("src/test/resources/domresolver.xml");
        config.setFeature(ResolverFeature.ALWAYS_RESOLVE, false);

        // Make sure the Docker container is running where we expect.
        ResourceConnection conn = new ResourceConnection(URI.create("http://localhost:8222/docs/sample/sample.dtd"));
        conn.get(config, true);
        Assertions.assertEquals(200, conn.getStatusCode());
    }

    @Test
    public void testResolver1() throws Exception {
        SAXParserFactory spfactory = SAXParserFactory.newInstance();
        spfactory.setValidating(true);
        SAXParser parser = spfactory.newSAXParser();
        try {
            parser.parse("src/test/resources/documents/dtdtest.xml", new DevNullHandler(new XMLResolver(config)));
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testPerformance() throws Exception {
        XMLResolver resolver = new XMLResolver(config);

        // These aren't found in the catalog

        Source source = resolver.getURIResolver().resolve("http://localhost:8222/docs/sample/sample.xsl","file:/tmp/test.xsl");
        Assertions.assertNull(source);
        source = resolver.getURIResolver().resolve("../helloworld.xml","http://localhost:8222/docs/sample/sample.xsl");
        Assertions.assertNull(source);
    }

    private static class DevNullHandler extends DefaultHandler {
        private XMLResolver resolver = null;

        public DevNullHandler(XMLResolver resolver) {
            this.resolver = resolver;
        }
        
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return resolver.getEntityResolver().resolveEntity(publicId, systemId);
        }
    }
}
