/*
 * Resolver2Test.java
 * JUnit based test
 *
 * Created on January 2, 2007, 9:14 AM
 */

package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import org.xmlresolver.sources.ResolverInputSource;
import org.xmlresolver.utils.URIUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 *
 * @author ndw
 */
public class ResolverTest {
    public static final String catalog1 = "src/test/resources/rescat.xml";
    XMLResolverConfiguration config = null;
    Resolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        resolver = new Resolver(config);
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
    public void throwUriExceptions() {
        boolean throwExceptions = config.getFeature(ResolverFeature.THROW_URI_EXCEPTIONS);
        try {
            config.setFeature(ResolverFeature.THROW_URI_EXCEPTIONS, false);
            URIUtils.cwd().resolve("src/test/resources/sample10/sample.dtd");
            InputSource source = resolver.resolveEntity(null, "blort%gg");
            assertNull(source);
        } catch (IOException | SAXException ex) {
            fail();
        }

        try {
            config.setFeature(ResolverFeature.THROW_URI_EXCEPTIONS, true);
            URIUtils.cwd().resolve("src/test/resources/sample10/sample.dtd");
            resolver.resolveEntity(null, "blort%gg");
            fail();
        } catch (IOException | SAXException | IllegalArgumentException ex) {
            // pass;
        }

        config.setFeature(ResolverFeature.THROW_URI_EXCEPTIONS, throwExceptions);
    }

    @Test
    public void sequenceTest() {
        try {
            XMLResolverConfiguration lconfig = new XMLResolverConfiguration(Collections.emptyList(),
                    Arrays.asList("src/test/resources/seqtest1.xml", "src/test/resources/seqtest2.xml"));
            lconfig.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
            Resolver lresolver = new Resolver(lconfig);
            InputSource source = lresolver.resolveEntity(null, "https://xmlresolver.org/ns/sample-as-uri/sample.dtd");
            ResolverInputSource rsource = ((ResolverInputSource) source);
            assertNotNull(rsource.getByteStream());
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void testSchemaWithoutSystemIdReturnsNull() {
        URI baseURI = URIUtils.cwd().resolve("src/test/resources/sample10/sample.xsd");
        XMLResolverConfiguration rconfig = new XMLResolverConfiguration("src/test/resources/rescatxsd.xml");
        Resolver resolver = new Resolver(rconfig);

        LSInput result = resolver.resolveResource(
                "http://www.w3.org/2001/XMLSchema",
                "http://xmlresolver.org/some/custom/namespace",
                null,
                null,
                baseURI.toASCIIString()
        );

        assertNull("null expected if neither systemId nor publicId are specified", result);
    }

}
