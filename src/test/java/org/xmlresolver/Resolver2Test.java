/*
 * Resolver2Test.java
 * JUnit based test
 *
 * Created on January 2, 2007, 9:14 AM
 */

package org.xmlresolver;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 *
 * @author ndw
 */
public class Resolver2Test {
    /* Test of resolve method, of class org.xmlresolver.Resolver.
     */
    @Test
    public void testResolver2() throws Exception {
        SAXParserFactory spfactory = SAXParserFactory.newInstance();
        spfactory.setValidating(true);
        SAXParser parser = spfactory.newSAXParser();

        XMLResolverConfiguration config = new XMLResolverConfiguration("src/test/resources/domresolver.xml");
        //Catalog c = new Catalog(config);
        //CatalogManager cx = new CatalogManager(config); // FIXME:
        parser.parse("src/test/resources/documents/dtdtest.xml", new DevNullHandler(new XMLResolver(config)));
        // If we didn't get an exception, we passed!
    }
    
    private static class DevNullHandler extends DefaultHandler2 {
        private XMLResolver resolver = null;

        public DevNullHandler(XMLResolver resolver) {
            this.resolver = resolver;
        }
        
        public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
            System.out.println("Called resolveEntity: " + name + ", " + publicId + ", " + baseURI + ", " + systemId);
            return resolver.getEntityResolver2().resolveEntity(name, publicId, baseURI, systemId);
        }
    }
}
