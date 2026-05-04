package org.xmlresolver.adapters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlresolver.ResolverFeature;
import org.xmlresolver.XMLResolver;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.fail;

public class XmlStreamAdapterTest {
    private XMLResolver resolver;

    @BeforeEach
    void setUp() throws ParserConfigurationException, SAXException {
        resolver = new XMLResolver();

        ArrayList<String> catalogs = new ArrayList<>();
        catalogs.add("src/test/resources/saxadapter-catalog.xml");
        resolver.getConfiguration().setFeature(ResolverFeature.CATALOG_FILES, catalogs);
    }

    @Test
    void resolveEntity() {
        try {
            XmlStreamAdapter adapter = new XmlStreamAdapter(resolver);
            Object source = adapter.resolveEntity(null, "/path/to/saxchapter.xml", null, null);
            Assertions.assertNotNull(source);
        } catch (Exception ex) {
            fail();
        }
    }
}
