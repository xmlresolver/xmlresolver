/*
 * DOMResolverTest.java
 *
 * Created on February 12, 2007, 4:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import org.junit.Test;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSResourceResolver;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author ndw
 */
public class DOMResolverTest {
    /* Test of resolve method, of class org.xmlresolver.Resolver. */
    @Test
    public void testResolver() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImpl = builder.getDOMImplementation();
        DOMImplementationLS domLSImpl = (DOMImplementationLS) domImpl.getFeature("LS","3.0");
        LSParser parser = domLSImpl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, "http://www.w3.org/TR/REC-xml");
        DOMConfiguration config = parser.getDomConfig();

        XMLResolverConfiguration rconfig = new XMLResolverConfiguration("src/test/resources/domresolver.xml");
        Resolver resolver = new Resolver(rconfig);

        CatalogManager manager = rconfig.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI result = manager.lookupSystem("foo:ent.xml");
        assertEquals(URI.create("http://example.com/entity/ent.xml"), result);

        try {
            config.setParameter("resource-resolver", new DOMLSResolver(resolver));
            parser.parseURI("src/test/resources/documents/dtdtest.xml");
        } catch (Exception ex) {
            fail();
        }
    }
    
    private class DOMLSResolver implements LSResourceResolver {
        private Resolver resolver = null;

        public DOMLSResolver(Resolver resolver) {
            this.resolver = resolver;
        }

        public LSInput resolveResource(String type, String namespace, String publicId, String systemId, String baseURI) {
            //System.out.println("LS resolve: " + type + "," + namespace + "," + publicId + "," + systemId + "," + baseURI);
            return resolver.resolveResource(type, namespace, publicId, systemId, baseURI);
        }
    }
}
