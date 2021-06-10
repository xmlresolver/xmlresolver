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
import org.xml.sax.ext.DefaultHandler2;
import org.xmlresolver.sources.ResolverInputSource;
import org.xmlresolver.utils.URIUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
}
