package org.xmlresolver;

import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static org.junit.jupiter.api.Assertions.fail;

public class SAXResolverTest {
    @Test
    public void issue183_sax1() {
        try {
            XMLResolverConfiguration resolverConfig = new XMLResolverConfiguration();
            resolverConfig.setFeature(ResolverFeature.ALWAYS_RESOLVE, true);
            XMLResolver resolver = new XMLResolver(resolverConfig);

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            // This is the SAX1 API so won't have the EntityResolver2 APIs.
            reader.setEntityResolver(resolver.getEntityResolver());

            // Doesn't matter what we parse.
            reader.parse(new InputSource("src/test/resources/empty.xml"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void issue183_sax2() {
        try {
            XMLResolverConfiguration resolverConfig = new XMLResolverConfiguration();
            resolverConfig.setFeature(ResolverFeature.ALWAYS_RESOLVE, true);
            XMLResolver resolver = new XMLResolver(resolverConfig);

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setEntityResolver(resolver.getEntityResolver2());

            // Doesn't matter what we parse.
            reader.parse(new InputSource("src/test/resources/empty.xml"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }
}
