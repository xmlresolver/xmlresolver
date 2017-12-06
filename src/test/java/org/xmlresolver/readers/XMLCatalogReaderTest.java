/*
 * XMLCatalogReaderTest.java
 * JUnit based test
 *
 * Created on December 11, 2006, 1:55 PM
 */

package org.xmlresolver.readers;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *
 * @author ndw
 */
public class XMLCatalogReaderTest extends TestCase {
    
    public XMLCatalogReaderTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /*
     * Test of readCatalog method, of class org.xmlresolver.readers.XMLCatalogReader.
     */
    public void testReadCatalog() throws ParserConfigurationException, SAXException, IOException {
        System.out.println("readCatalog");

        String xml = "<catalog xmlns='urn:oasis:names:tc:entity:xmlns:xml:catalog'/>";
        ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(stream);

        DOMSource source = new DOMSource(doc);

        XMLCatalogReader instance = new XMLCatalogReader();
        
        Element result = instance.readCatalog(source);

        assertNotNull(result);
    }
    
}
