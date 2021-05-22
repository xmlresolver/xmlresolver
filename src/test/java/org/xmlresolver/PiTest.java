package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlresolver.tools.ResolvingXMLReader;

import java.io.IOException;

import static org.junit.Assert.fail;

public class PiTest {
    public static final String catalog = "src/test/resources/empty.xml";

    XMLResolverConfiguration config = null;
    Resolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(catalog);
        resolver = new Resolver(config);
        config.setFeature(ResolverFeature.ALLOW_CATALOG_PI, true);
    }

    @Test
    public void piTest() {
        ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        try {
            reader.parse("src/test/resources/xml/pi.xml");
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void piTestTemporary() {
        config.setFeature(ResolverFeature.ALLOW_CATALOG_PI, true);
        ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        try {
            reader.parse("src/test/resources/xml/pi.xml");
        } catch (IOException | SAXException ex) {
            fail();
        }

        // The catalog added by the PI in the previous parse should not
        // still be in the catalog list now.
        config.setFeature(ResolverFeature.ALLOW_CATALOG_PI, false);
        reader = new ResolvingXMLReader(resolver);
        try {
            reader.parse("src/test/resources/xml/pi.xml");
            fail();
        } catch (IOException | SAXException ex) {
            // This is a pass
        }

        // But it should still be possible to do it again (make sure we haven't broken the parser)
        config.setFeature(ResolverFeature.ALLOW_CATALOG_PI, true);
        reader = new ResolvingXMLReader(resolver);
        try {
            reader.parse("src/test/resources/xml/pi.xml");
        } catch (IOException | SAXException ex) {
            fail();
        }
    }
}

