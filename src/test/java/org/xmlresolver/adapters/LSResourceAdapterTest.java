package org.xmlresolver.adapters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.SAXException;
import org.xmlresolver.*;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

public class LSResourceAdapterTest {
    private XMLResolver resolver;

    @BeforeEach
    void setUp() throws ParserConfigurationException, SAXException {
        resolver = new XMLResolver();
        ArrayList<String> catalogs = new ArrayList<>();
        catalogs.add("src/test/resources/saxadapter-catalog.xml");
        resolver.getConfiguration().setFeature(ResolverFeature.CATALOG_FILES, catalogs);
        resolver.getConfiguration().setFeature(ResolverFeature.ALWAYS_RESOLVE, false);
    }

    @Test
    void resolveResourceDTD() {
        LSResourceAdapter adapter = new LSResourceAdapter(resolver);
        LSInput source = adapter.resolveResource("http://www.w3.org/TR/REC-xml", "", "", "book.dtd", null);
        Assertions.assertNotNull(source);
        Assertions.assertNotNull(source.getSystemId());
        Assertions.assertTrue(source.getSystemId().contains("saxadapter.dtd"));
        Assertions.assertNotNull(source.getByteStream());
    }

    @Test
    void resolveResourceSchema() {
        LSResourceAdapter adapter = new LSResourceAdapter(resolver);
        LSInput source = adapter.resolveResource("http://www.w3.org/2001/XMLSchema", "", "", "sample.xsd", null);
        Assertions.assertNotNull(source);
        Assertions.assertNotNull(source.getSystemId());
        Assertions.assertTrue(source.getSystemId().contains("10/sample.xsd"));
        Assertions.assertNotNull(source.getByteStream());
    }
}
