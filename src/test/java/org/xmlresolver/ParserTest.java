package org.xmlresolver;

import net.sf.saxon.s9api.SaxonApiException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlresolver.tools.ResolvingXMLReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.fail;

public class ParserTest {
    public static final String catalog = "src/test/resources/empty.xml";

    XMLResolverConfiguration config = null;
    Resolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(catalog);
        resolver = new Resolver(config);
    }

    @Test
    public void piTest() {
        ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        String xml = "<doc><title>Spoon</title></doc>";
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        InputSource source = new InputSource(bais);
        try {
            reader.parse(source);
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

    @Test
    public void parseWithRedirect() {
        ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        try {
            reader.parse("src/test/resources/http.xml");
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

}

