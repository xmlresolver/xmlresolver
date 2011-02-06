/*
 * Resolver2Test.java
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
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

/**
 *
 * @author ndw
 */
public class Resolver2Test extends TestCase {
    public Resolver2Test(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of resolve method, of class org.xmlresolver.Resolver.
     */
    public void testResolver2() throws Exception {
        System.out.println("testResolver2");

        SAXParserFactory spfactory = SAXParserFactory.newInstance();
        spfactory.setValidating(true);
        SAXParser parser = spfactory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        parser.parse("documents/dtdtest.xml", new DevNullHandler(new Resolver(new Catalog("documents/catalog.xml"))));
        // If we didn't get an exception, we passed!
    }
    
    class DevNullHandler extends DefaultHandler2 {
        private Resolver resolver = null;

        public DevNullHandler(Resolver resolver) {
            this.resolver = resolver;
        }
        
        public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
            System.out.println("Called resolveEntity: " + name + ", " + publicId + ", " + baseURI + ", " + systemId);
            return resolver.resolveEntity(name, publicId, baseURI, systemId);
        }
    }
}
