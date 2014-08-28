/*
 * CatalogLookupTest.java
 *
 * Created on December 30, 2006, 9:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlresolver.helpers.DOMUtils;

/**
 *
 * @author ndw
 */
public class CatalogLookupTest extends TestCase {
    private Catalog catalog = null;
    private String method = null;    // Not thread safe! Not particularly well designed! But convenient!
    private String expected = null;
    private DocumentBuilderFactory dbfactory = null;
    
    public CatalogLookupTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        dbfactory = DocumentBuilderFactory.newInstance();
        dbfactory.setNamespaceAware(true);
    }

    protected void tearDown() throws Exception {
    }

    @Test
    public void testCatalog1() {
        System.out.println("testCatalog1");

        runCatalogTests("tests/catalogs/catalog.xml");
    }

    @Test
    public void testCatalog2() {
        System.out.println("testCatalog2");

        runCatalogTests("tests/catalogs/prefer-public.xml");
    }

    @Test
    public void testCatalog3() {
        System.out.println("testCatalog3");

        runCatalogTests("tests/catalogs/prefer-system.xml");
    }

    @Test
    public void testCatalog4() {
        System.out.println("testCatalog4");

        runCatalogTests("tests/catalogs/sgmlcatalog.xml");
    }

    @Test
    public void runCatalogTests(String catalogFile) {
        catalog = new Catalog(catalogFile);
        DocumentBuilder builder = null;
        Document doc = null;
        
        try {
            builder = dbfactory.newDocumentBuilder();
            doc = builder.parse(catalogFile);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            fail("Parser configuration exception? What the ...!?");
        } catch (SAXException ex) {
            ex.printStackTrace();
            fail("SAX exception? What the ...!?");
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("I/O exception? What the ...!?");
        }
        
        NodeList tests = doc.getElementsByTagNameNS(Catalog.NS_XMLRESOURCE_EXT, "testCase");
        for (int pos = 0; pos < tests.getLength(); pos++) {
            Element test = (Element) tests.item(pos);
            method = test.getAttribute("method");
            expected = test.getAttribute("expected");
            
            if ("lookupEntity".equals(method)) {
                runLookupEntity(DOMUtils.attr(test,"name"),DOMUtils.attr(test,"systemId"),DOMUtils.attr(test,"publicId"));
            } else if ("lookupURI".equals(method)) {
                runLookupURI(DOMUtils.attr(test,"uri"));
            } else if ("lookupNamespaceURI".equals(method)) {
                runLookupNamespaceURI(DOMUtils.attr(test,"uri"), DOMUtils.attr(test,"nature"), DOMUtils.attr(test,"purpose"));
            } else if ("lookupPublic".equals(method)) {
                runLookupPublic(DOMUtils.attr(test,"systemId"), DOMUtils.attr(test,"publicId"));
            } else if ("lookupSystem".equals(method)) {
                runLookupSystem(DOMUtils.attr(test,"systemId"));
            } else if ("lookupDoctype".equals(method)) {
                runLookupDoctype(DOMUtils.attr(test,"name"), DOMUtils.attr(test,"systemId"), DOMUtils.attr(test,"publicId"));
            } else if ("lookupNotation".equals(method)) {
                runLookupNotation(DOMUtils.attr(test,"name"), DOMUtils.attr(test,"systemId"), DOMUtils.attr(test,"publicId"));
            } else if ("lookupDocument".equals(method)) {
                runLookupDocument();
            } else {
                fail("Unexpected method name: " + method);
            }
        }
    }

    private void runLookupEntity(String name, String sysid, String pubid) {
        CatalogResult result = catalog.lookupEntity(name,sysid,pubid);
        checkEqual("lookupEntity", name + "," + sysid + "," + pubid, result.uri());
    }

    private void runLookupURI(String name) {
        CatalogResult result = catalog.lookupURI(name);
        checkEqual("lookupURI", name, result.uri());
    }
    
    private void runLookupNamespaceURI(String uri, String nature, String purpose) {
        CatalogResult result = catalog.lookupNamespaceURI(uri, nature, purpose);
        checkEqual("lookupNamespaceURI", uri + "," + nature + "," + purpose, result.uri());
    }

    private void runLookupPublic(String systemId, String publicId) {
        CatalogResult result = catalog.lookupPublic(systemId, publicId);
        checkEqual("lookupPublic", systemId + "," + publicId, result.uri());
    }

    private void runLookupSystem(String systemId) {
        CatalogResult result = catalog.lookupSystem(systemId);
        checkEqual("lookupSystem", systemId, result.uri());
    }

    private void runLookupDoctype(String name, String systemId, String publicId) {
        CatalogResult result = catalog.lookupDoctype(name, systemId, publicId);
        checkEqual("lookupDoctype", name + "," + systemId + "," + publicId, result.uri());
    }

    private void runLookupNotation(String name, String systemId, String publicId) {
        CatalogResult result = catalog.lookupNotation(name, systemId, publicId);
        checkEqual("lookupNotation", name + "," + systemId + "," + publicId, result.uri());
    }

    private void runLookupDocument() {
        CatalogResult result = catalog.lookupDocument();
        checkEqual("lookupDocument", null, result.uri());
    }
    
    private void checkEqual(String name, String args, String result) {
        if ( ! ((expected == null && result == null) || (expected != null && expected.equals(result)))) {
            System.out.println("Test failed: " + name + "(" + args + ")");
            System.out.println("  => " + expected + " =/= " + result);
        }

        assertEquals(expected, result);
    }
}
