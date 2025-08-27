package org.xmlresolver.adapters;

import org.junit.jupiter.api.AfterEach;
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

class SAXAdapterTest {

    private XMLReader reader;

    @BeforeEach
    void setUp() throws ParserConfigurationException, SAXException {
        XMLResolver resolver = new XMLResolver();

        ArrayList<String> catalogs = new ArrayList<>();
        catalogs.add("src/test/resources/saxadapter-catalog.xml");
        resolver.getConfiguration().setFeature(ResolverFeature.CATALOG_FILES, catalogs);

        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setEntityResolver(resolver.getEntityResolver());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getExternalSubset() throws IOException, SAXException {
        reader.parse(new InputSource("src/test/resources/saxadapter.xml"));
    }

    @Test
    void resolveEntity() {
        // TODO
    }

    @Test
    void testResolveEntity() {
        // TODO
    }
}