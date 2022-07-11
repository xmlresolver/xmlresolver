/*
 * Resolver2Test.java
 * JUnit based test
 *
 * Created on January 2, 2007, 9:14 AM
 */

package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xmlresolver.sources.ResolverInputSource;
import org.xmlresolver.utils.URIUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * @author ndw
 */
public class XercesResolverTest {
    public static final String catalog1 = "src/test/resources/rescatxsd.xml";
    XMLResolverConfiguration config = null;
    Resolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        config.setFeature(ResolverFeature.CACHE, null);
        config.setFeature(ResolverFeature.CACHE_DIRECTORY, null);
        config.setFeature(ResolverFeature.CACHE_UNDER_HOME, false);
        config.setFeature(ResolverFeature.CACHE_ENABLED, false);
        resolver = new XercesResolver(config);
    }

    @Test
    public void lookupSystem() {
        try {
            URI result = URIUtils.cwd().resolve("src/test/resources/sample10/sample.dtd");
            InputSource source = resolver.resolveEntity(null, "https://example.com/sample/1.0/sample.dtd");
            assertTrue(source.getSystemId().endsWith(result.getPath()));
            assertNotNull(source.getByteStream());
            ResolverInputSource rsource = ((ResolverInputSource) source);
            assertEquals(rsource.resolvedURI, result);
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemAsURI() {
        try {
            URI result = URIUtils.cwd().resolve("src/test/resources/sample10/sample.dtd");
            InputSource source = resolver.resolveEntity(null, "https://example.com/sample/1.0/uri.dtd");
            assertTrue(source.getSystemId().endsWith(result.getPath()));
            assertNotNull(source.getByteStream());
            ResolverInputSource rsource = ((ResolverInputSource) source);
            assertEquals(rsource.resolvedURI, result);
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void xercesSchemaValidation() {
        try {
            final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            saxFactory.setNamespaceAware(true);  // Namespaces are Good
            saxFactory.setValidating(true);      // Yes to validation
            saxFactory.setFeature("http://apache.org/xml/features/validation/schema", true);
            final SAXParser saxParser = saxFactory.newSAXParser();
            final XMLReader xmlReader = saxParser.getXMLReader();

            // setup resolver
            final Resolver resolver = new XercesResolver(config);
            xmlReader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", resolver);

            // parse
            xmlReader.parse("src/test/resources/sample-xsd.xml");

        } catch (SAXException | ParserConfigurationException | IOException ex) {
            fail();
        }
    }

    @Test
    public void xercesSchemaValidationWithHints() {
        try {
            final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            saxFactory.setNamespaceAware(true);  // Namespaces are Good
            saxFactory.setValidating(true);      // Yes to validation
            saxFactory.setFeature("http://apache.org/xml/features/validation/schema", true);
            final SAXParser saxParser = saxFactory.newSAXParser();
            final XMLReader xmlReader = saxParser.getXMLReader();

            // setup resolver
            final Resolver resolver = new XercesResolver(config);
            xmlReader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", resolver);

            // parse
            xmlReader.parse("src/test/resources/sample10/sample.xml");

        } catch (SAXException | ParserConfigurationException | IOException ex) {
            fail();
        }
    }
}
