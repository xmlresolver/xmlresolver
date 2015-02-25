/*
 * ResolvingXMLReaderTest.java
 * JUnit based test
 *
 * Created on January 11, 2007, 9:57 AM
 */

package org.xmlresolver.tools;

import java.io.IOException;
import junit.framework.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xmlresolver.Catalog;
import org.xmlresolver.Resolver;

/**
 *
 * @author ndw
 */
public class ResolvingXMLReaderTest extends TestCase {
    
    public ResolvingXMLReaderTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testReader() throws IOException, SAXException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(true);
        
        ResolvingXMLReader reader = new ResolvingXMLReader(spf);
        reader.parse("src/test/resources/documents/pitest.xml");
        
        Resolver resolver = reader.getResolver();
        Catalog catalog = resolver.getCatalog();
        String catalogList = catalog.catalogList();
        assert(catalogList.contains("/doesnotexist.xml"));
        assert(catalogList.contains("/picat.xml"));
    }
    
    
}
