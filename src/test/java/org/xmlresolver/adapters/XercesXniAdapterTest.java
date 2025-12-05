package org.xmlresolver.adapters;

import org.apache.xerces.impl.XMLEntityDescription;
import org.apache.xerces.impl.dtd.XMLDTDDescription;
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.util.XMLEntityDescriptionImpl;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlresolver.ResolverFeature;
import org.xmlresolver.XMLResolver;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.fail;

public class XercesXniAdapterTest {
    private XMLResolver resolver;

    @BeforeEach
    void setUp() throws ParserConfigurationException, SAXException {
        resolver = new XMLResolver();

        ArrayList<String> catalogs = new ArrayList<>();
        catalogs.add("src/test/resources/saxadapter-catalog.xml");
        resolver.getConfiguration().setFeature(ResolverFeature.CATALOG_FILES, catalogs);
    }

    @Test
    void resolvEntityDescriptionName() {
        try {
            XercesXniAdapter adapter = new XercesXniAdapter(resolver);
            XMLEntityDescription resId = new XMLEntityDescriptionImpl("chapter.xml", null, "/path/to/chapter.xml", null, null);
            XMLInputSource source = adapter.resolveEntity(resId);
            Assertions.assertNotNull(source);
            Assertions.assertNotNull(source.getSystemId());
            Assertions.assertTrue(source.getSystemId().contains("saxchapter.xml"));
            Assertions.assertNotNull(source.getByteStream());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void resolvEntityDescriptionSystemId() {
        try {
            XercesXniAdapter adapter = new XercesXniAdapter(resolver);
            XMLEntityDescription resId = new XMLEntityDescriptionImpl(null, null, "/path/to/chapter.xml", null, null);
            XMLInputSource source = adapter.resolveEntity(resId);
            Assertions.assertNotNull(source);
            Assertions.assertNotNull(source.getSystemId());
            Assertions.assertTrue(source.getSystemId().contains("saxchapter.xml"));
            Assertions.assertNotNull(source.getByteStream());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void resolvDTDDescription() {
        try {
            XercesXniAdapter adapter = new XercesXniAdapter(resolver);
            XMLDTDDescription resId = new XMLDTDDescription(null, "book.dtd", null, null, "book");
            XMLInputSource source = adapter.resolveEntity(resId);
            Assertions.assertNotNull(source);
            Assertions.assertNotNull(source.getSystemId());
            Assertions.assertTrue(source.getSystemId().contains("saxadapter.dtd"));
            Assertions.assertNotNull(source.getByteStream());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void resolvXSDDescription() {
        try {
            XercesXniAdapter adapter = new XercesXniAdapter(resolver);
            XSDDescription resId = new XSDDescription();
            resId.setLiteralSystemId("sample.xsd");
            XMLInputSource source = adapter.resolveEntity(resId);
            Assertions.assertNotNull(source);
            Assertions.assertNotNull(source.getSystemId());
            Assertions.assertTrue(source.getSystemId().contains("10/sample.xsd"));
            Assertions.assertNotNull(source.getByteStream());
        } catch (Exception e) {
            fail();
        }
    }
}
