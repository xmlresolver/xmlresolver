package org.xmlresolver.adapters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlresolver.ResolverFeature;
import org.xmlresolver.XMLResolver;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.fail;

class SAX2AdapterTest {

    private XMLResolver resolver;
    private XMLReader reader;

    @BeforeEach
    void setUp() throws ParserConfigurationException, SAXException {
        resolver = new XMLResolver();
        ArrayList<String> catalogs = new ArrayList<>();
        catalogs.add("src/test/resources/saxadapter-catalog.xml");
        resolver.getConfiguration().setFeature(ResolverFeature.CATALOG_FILES, catalogs);
        resolver.getConfiguration().setFeature(ResolverFeature.ALWAYS_RESOLVE, false);
        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setEntityResolver(resolver.getEntityResolver2());
    }

    @Test
    void getExternalSubset() throws IOException, SAXException {
        try {
            SAX2Adapter adapter = new SAX2Adapter(resolver);
            InputSource source = adapter.getExternalSubset("book", "irrelevant.xml");
            Assertions.assertNotNull(source);
            Assertions.assertNotNull(source.getByteStream());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    void getExternalSubsetFail() throws IOException, SAXException {
        SAX2Adapter adapter = new SAX2Adapter(resolver);
        InputSource source = adapter.getExternalSubset("not-a-book", "irrelevant.xml");
        Assertions.assertNull(source);
    }

    @Test
    void resolveEntity2() {
        try {
            SAX2Adapter adapter = new SAX2Adapter(resolver);
            InputSource source = adapter.resolveEntity("chapter", null, "irrelevant.xml", null);
            Assertions.assertNotNull(source);
            Assertions.assertNotNull(source.getSystemId());
            Assertions.assertTrue(source.getSystemId().endsWith("saxchapter.xml"));
            Assertions.assertNotNull(source.getByteStream());
        } catch (Exception ex) {
            fail();
        }
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
            reader.parse(new InputSource("src/test/resources/saxadapter.xml"));
            Assertions.assertTrue(tester.smokeTest);
            Assertions.assertTrue(tester.pass);
            Assertions.assertTrue(tester.chapter);
        } catch (Exception ex) {
            fail();
        }
    }
}