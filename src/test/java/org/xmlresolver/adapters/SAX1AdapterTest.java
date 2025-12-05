package org.xmlresolver.adapters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.xml.sax.*;
import org.xmlresolver.ResolverFeature;
import org.xmlresolver.XMLResolver;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.fail;

class SAX1AdapterTest {

    private XMLResolver resolver;
    private XMLReader reader;

    @BeforeEach
    void setUp() throws ParserConfigurationException, SAXException {
        resolver = new XMLResolver();

        ArrayList<String> catalogs = new ArrayList<>();
        catalogs.add("src/test/resources/saxadapter-catalog.xml");
        resolver.getConfiguration().setFeature(ResolverFeature.CATALOG_FILES, catalogs);

        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setEntityResolver(resolver.getEntityResolver());
    }

    @Test
    void resolveEntity1() {
        try {
            SAX2Adapter adapter = new SAX2Adapter(resolver);
            InputSource source = adapter.resolveEntity(null, "/path/to/chapter.xml");
            Assertions.assertNotNull(source);
            Assertions.assertNotNull(source.getSystemId());
            Assertions.assertTrue(source.getSystemId().endsWith("saxchapter.xml"));
            Assertions.assertNotNull(source.getByteStream());
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void testInParser() {
        try {
            TestContentHandler tester = new TestContentHandler();
            reader.setContentHandler(tester);
            reader.parse(new InputSource("src/test/resources/saxadapter1.xml"));
            Assertions.assertTrue(tester.smokeTest);
            Assertions.assertTrue(tester.pass);
        } catch (Exception ex) {
            fail();
        }
    }
}