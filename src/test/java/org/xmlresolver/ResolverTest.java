/*
 * ResolverTest.java
 * JUnit based test
 *
 * Created on January 2, 2007, 9:14 AM
 */

package org.xmlresolver;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

import junit.framework.TestCase;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author ndw
 */
public class ResolverTest {
    /**
     * Test of resolve method, of class org.xmlresolver.Resolver.
     */
    @Test
    public void testResolver1() throws Exception {
        SAXParserFactory spfactory = SAXParserFactory.newInstance();
        spfactory.setValidating(true);
        SAXParser parser = spfactory.newSAXParser();
        Catalog c = new Catalog("src/test/resources/documents/catalog.xml");
        parser.parse("src/test/resources/documents/dtdtest.xml", new DevNullHandler(new Resolver(c)));
        // If we didn't get an exception, we passed!
    }

    @Test
    public void testPerformance() throws Exception {
        Resolver resolver = new Resolver(new Catalog("src/test/resources/catalogs/catalog.xml"));

        resolver.resolve("http://docbook.sourceforge.net/release/xsl/current/html/docbook.xsl","file:/tmp/test.xsl");
        resolver.resolve("../VERSION","http://docbook.sourceforge.net/release/xsl/current/html/docbook.xsl");
    }

    private class DevNullHandler extends DefaultHandler {
        private Resolver resolver = null;

        public DevNullHandler(Resolver resolver) {
            this.resolver = resolver;
        }
        
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return resolver.resolveEntity(publicId, systemId);
        }
    }
}
