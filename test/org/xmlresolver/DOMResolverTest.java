/*
 * DOMResolverTest.java
 *
 * Created on February 12, 2007, 4:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSResourceResolver;

/**
 *
 * @author ndw
 */
public class DOMResolverTest extends TestCase {
    /**
     * Test of resolve method, of class org.xmlresolver.Resolver.
     */
    @Test
    public void testResolver() throws Exception {
        System.out.println("testResolver");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImpl = builder.getDOMImplementation();
        DOMImplementationLS domLSImpl = (DOMImplementationLS) domImpl.getFeature("LS","3.0");
        LSParser parser = domLSImpl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, "http://www.w3.org/TR/REC-xml");
        DOMConfiguration config = parser.getDomConfig();
        //config.setParameter("resource-resolver", new DOMLSResolver(new Resolver(new Catalog())));
        parser.parseURI("tests/documents/dtdtest.xml");
        
        // If we didn't get an exception, we passed!
    }
    
    private class DOMLSResolver implements LSResourceResolver {
        private Resolver resolver = null;

        public DOMLSResolver(Resolver resolver) {
            this.resolver = resolver;
        }

        public LSInput resolveResource(String type, String namespace, String publicId, String systemId, String baseURI) {
            System.out.println("resolve: " + type + "," + namespace + "," + publicId + "," + systemId + "," + baseURI);
            return resolver.resolveResource(type, namespace, publicId, systemId, baseURI);
        }
        
    }
}
