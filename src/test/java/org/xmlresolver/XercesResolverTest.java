/*
 * Resolver2Test.java
 * JUnit based test
 *
 * Created on January 2, 2007, 9:14 AM
 */

package org.xmlresolver;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlresolver.sources.ResolverInputSource;
import org.xmlresolver.utils.URIUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * @author ndw
 */
public class XercesResolverTest {
    public static final String catalog1 = "src/test/resources/rescatxsd.xml";
    XMLResolverConfiguration config = null;
    XMLResolver resolver = null;
    XMLEntityResolver xresolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        resolver = new XMLResolver(config);
        xresolver = resolver.getXMLEntityResolver();
    }

    @Test
    public void lookupSystem() {
        try {
            URI result = URIUtils.cwd().resolve("src/test/resources/sample10/sample.dtd");
            XMLResourceIdentifier resid = new ResourceIdentifier("https://example.com/sample/1.0/sample.dtd");

            XMLInputSource source = xresolver.resolveEntity(resid);
            assertTrue(source.getSystemId().endsWith(result.getPath()));
            assertNotNull(source.getByteStream());
        } catch (IOException ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemAsURI() {
        try {
            URI result = URIUtils.cwd().resolve("src/test/resources/sample10/sample.dtd");
            XMLResourceIdentifier resid = new ResourceIdentifier("https://example.com/sample/1.0/uri.dtd");
            XMLInputSource source = xresolver.resolveEntity(resid);
            assertTrue(source.getSystemId().endsWith(result.getPath()));
            assertNotNull(source.getByteStream());
        } catch (IOException ex) {
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
            xmlReader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", xresolver);

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
            xmlReader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", xresolver);

            // parse
            xmlReader.parse("src/test/resources/sample10/sample.xml");

        } catch (SAXException | ParserConfigurationException | IOException ex) {
            fail();
        }
    }

    private static class ResourceIdentifier implements XMLResourceIdentifier {
        private String publicId = null;
        private String expandedSystemId = null;
        private String literalSystemId = null;
        private String baseSystemId = null;
        private String namespace = null;

        public ResourceIdentifier(String systemId) {
            expandedSystemId = systemId;
            literalSystemId = systemId;
            baseSystemId = systemId;
        }

        @Override
        public void setPublicId(String s) {
            publicId = s;
        }

        @Override
        public String getPublicId() {
            return publicId;
        }

        @Override
        public void setExpandedSystemId(String s) {
            expandedSystemId = s;
        }

        @Override
        public String getExpandedSystemId() {
            return expandedSystemId;
        }

        @Override
        public void setLiteralSystemId(String s) {
            literalSystemId = s;
        }

        @Override
        public String getLiteralSystemId() {
            return literalSystemId;
        }

        @Override
        public void setBaseSystemId(String s) {
            baseSystemId = s;
        }

        @Override
        public String getBaseSystemId() {
            return baseSystemId;
        }

        @Override
        public void setNamespace(String s) {
            namespace = s;

        }

        @Override
        public String getNamespace() {
            return namespace;
        }
    }

}
