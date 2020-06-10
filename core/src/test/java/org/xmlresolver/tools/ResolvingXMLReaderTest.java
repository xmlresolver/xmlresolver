/*
 * ResolvingXMLReaderTest.java
 * JUnit based test
 *
 * Created on January 11, 2007, 9:57 AM
 */

package org.xmlresolver.tools;

import junit.framework.TestCase;
import org.xml.sax.SAXException;
import org.xmlresolver.Catalog;
import org.xmlresolver.Configuration;
import org.xmlresolver.Resolver;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Properties;

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
        assertNotNull(resolver.resolveEntity("", "pi.dtd"));
    }

    public void testForbiddenPI() throws IOException, SAXException {
        Properties prop = new Properties();
        prop.setProperty("allow-oasis-xml-catalog-pi", "false");
        Configuration config = new Configuration(prop, null);
        Catalog catalog = new Catalog(config, "dummy.cat");
        Resolver resolver = new Resolver(catalog);

        ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        reader.parse("src/test/resources/documents/pitest-pass.xml");

        resolver = reader.getResolver();
        catalog = resolver.getCatalog();
        String catalogList = catalog.catalogList();
        assert(!catalogList.contains("/doesnotexist.xml"));
        assert(!catalogList.contains("/picat.xml"));
        assertNull(resolver.resolveEntity("", "pi.dtd"));
    }
}
