/*
 * StAXResolverTest.java
 * JUnit based test
 *
 * Created on January 2, 2007, 9:14 AM
 */

package org.xmlresolver;

import org.junit.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;

/**
 *
 * @author ndw
 */
public class StAXResolverTest {
    /* Test of resolve method, of class org.xmlresolver.Resolver.
     */
    @Test
    public void testResolver() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        Catalog catalog = new Catalog("src/test/resources/catalogs/resolver2.xml");
        StAXResolver resolver = new StAXResolver(catalog);
        factory.setXMLResolver(new SResolver(resolver));
        
        String xmlFile = "src/test/resources/documents/dtdtest.xml";
        XMLStreamReader reader = factory.createXMLStreamReader(xmlFile, new FileInputStream(xmlFile));

        while (reader.hasNext()) {
            int event = reader.next();
        }
        reader.close();
        
        // If we didn't get an exception, we passed!
    }
    
    private class SResolver implements XMLResolver {
        private StAXResolver resolver = null;

        public SResolver(StAXResolver resolver) {
            this.resolver = resolver;
        }

        public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
            System.out.println("resolveEntity: " + publicID + "," + systemID + "," + baseURI + ": " + namespace);
            return resolver.resolveEntity(publicID, systemID, baseURI, namespace);
        }
        
    }
}
