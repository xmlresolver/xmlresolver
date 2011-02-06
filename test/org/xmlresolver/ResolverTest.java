/*
 * ResolverTest.java
 * JUnit based test
 *
 * Created on January 2, 2007, 9:14 AM
 */

package org.xmlresolver;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import junit.framework.*;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author ndw
 */
public class ResolverTest extends TestCase {
    public ResolverTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of resolve method, of class org.xmlresolver.Resolver.
     */
    public void testResolver1() throws Exception {
        System.out.println("testResolver1");

        SAXParserFactory spfactory = SAXParserFactory.newInstance();
        spfactory.setValidating(true);
        SAXParser parser = spfactory.newSAXParser();
        parser.parse("documents/dtdtest.xml", new DevNullHandler(new Resolver(new Catalog("documents/catalog.xml"))));
        // If we didn't get an exception, we passed!
    }
    
    public void testPerformance() throws Exception {
        Resolver resolver = new Resolver(new Catalog("catalogs/catalog.xml"));

        resolver.resolve("http://docbook.sourceforge.net/release/xsl/current/html/docbook.xsl","file:/tmp/test.xsl");
        resolver.resolve("../VERSION","http://docbook.sourceforge.net/release/xsl/current/html/docbook.xsl");
    }

    class DevNullHandler extends DefaultHandler {
        private Resolver resolver = null;

        public DevNullHandler(Resolver resolver) {
            this.resolver = resolver;
        }
        
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return resolver.resolveEntity(publicId, systemId);
        }
    }
}
