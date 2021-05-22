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

import static org.junit.Assert.fail;

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
        XMLResolverConfiguration config = new XMLResolverConfiguration("src/test/resources/domresolver.xml");
        StAXResolver resolver = new StAXResolver(config);
        factory.setXMLResolver(new SResolver(resolver));
        
        String xmlFile = "src/test/resources/documents/dtdtest.xml";
        XMLStreamReader reader = factory.createXMLStreamReader(xmlFile, new FileInputStream(xmlFile));

        try {
            while (reader.hasNext()) {
                reader.next();
            }
            reader.close();
        } catch (Exception ex) {
            fail();
        }
    }
    
    private static class SResolver implements XMLResolver {
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
