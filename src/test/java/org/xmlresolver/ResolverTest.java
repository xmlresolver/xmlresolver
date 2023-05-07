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
import org.xmlresolver.loaders.CatalogLoader;
import org.xmlresolver.sources.ResolverInputSource;
import org.xmlresolver.tools.ResolvingXMLReader;
import org.xmlresolver.utils.URIUtils;

import javax.xml.transform.Source;
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

        assertNull("null expected if schema resource is requested w/o systemId", result);
    }

    @Test
    public void issue117() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("src/test/resources/projets en développement/catalog.xml"));
        resolver = new Resolver(config);
        ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        try {
            String fn = URIUtils.normalizeURI("src/test/resources/projets en développement/xml/instance.xml");
            reader.parse(URIUtils.cwd().resolve(fn).toString());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void issue115_noInternet_noResolver() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("src/test/resources/catalog-with-dtd.xml"));
        resolver = new Resolver(config);

        CatalogLoader loader = resolver.getConfiguration().getFeature(ResolverFeature.CATALOG_MANAGER).getCatalogLoader();
        loader.setEntityResolver(null);

        String phost = System.getProperty("http.proxyHost");
        String pport = System.getProperty("http.proxyPort");

        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "41414");

        try {
            // Parsing the catalog fails because the system identifier can't be resolved.
            InputSource source = resolver.resolveEntity(null, "urn:foo:bar");
            assertNull(source);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

        if (phost != null) {
            System.setProperty("http.proxyHost", phost);
        } else {
            System.clearProperty("http.proxyHost");
        }
        if (phost != null) {
            System.setProperty("http.proxyPort", pport);
        } else {
            System.clearProperty("http.proxyPort");
        }
    }

    @Test
    public void issue115_noInternet_resolver() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("src/test/resources/catalog-with-dtd.xml"));
        resolver = new Resolver(config);

        String phost = System.getProperty("http.proxyHost");
        String pport = System.getProperty("http.proxyPort");

        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "41414");

        URI result = URIUtils.cwd().resolve("src/test/resources/sample10/sample.dtd");
        try {
            // The catalog loader resolver handles the DTD, so the catalog is parsed even w/o internet
            InputSource source = resolver.resolveEntity(null, "urn:foo:bar");
            assertTrue(source.getSystemId().endsWith(result.getPath()));
            assertNotNull(source.getByteStream());
            ResolverInputSource rsource = ((ResolverInputSource) source);
            assertEquals(rsource.resolvedURI, result);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

        if (phost != null) {
            System.setProperty("http.proxyHost", phost);
        } else {
            System.clearProperty("http.proxyHost");
        }
        if (phost != null) {
            System.setProperty("http.proxyPort", pport);
        } else {
            System.clearProperty("http.proxyPort");
        }
    }

    @Test
    public void issue115_internet_resolver() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("src/test/resources/catalog-with-dtd.xml"));
        resolver = new Resolver(config);

        URI result = URIUtils.cwd().resolve("src/test/resources/sample10/sample.dtd");
        try {
            // In principle this test works just like the noInternet-noResolver test. If you
            // watch the internet traffic with some sort of sniffer, you can see that the
            // DTD is resolved locally, but I can't think of a way to test that.
            InputSource source = resolver.resolveEntity(null, "urn:foo:bar");
            assertTrue(source.getSystemId().endsWith(result.getPath()));
            assertNotNull(source.getByteStream());
            ResolverInputSource rsource = ((ResolverInputSource) source);
            assertEquals(rsource.resolvedURI, result);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void issue115_internet_noResolver() {
        // This test is just for completeness. It will pass or fail depending on whether the
        // OASIS http URI resolves for the catalog.
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("src/test/resources/catalog-with-dtd.xml"));
        resolver = new Resolver(config);

        CatalogLoader loader = resolver.getConfiguration().getFeature(ResolverFeature.CATALOG_MANAGER).getCatalogLoader();
        loader.setEntityResolver(null);

        URI result = URIUtils.cwd().resolve("src/test/resources/sample10/sample.dtd");
        try {
            InputSource source = resolver.resolveEntity(null, "urn:foo:bar");
            assertTrue(source.getSystemId().endsWith(result.getPath()));
            assertNotNull(source.getByteStream());
            ResolverInputSource rsource = ((ResolverInputSource) source);
            assertEquals(rsource.resolvedURI, result);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void issue140_always_resolve_off() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList());
        config.setFeature(ResolverFeature.ALWAYS_RESOLVE, false);
        resolver = new Resolver(config);

        try {
            String baseURI = URIUtils.cwd().resolve("src/test/resources/xml/ch01.xml").toString();
            Source result = resolver.resolve("", baseURI);
            assertNull(result);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void issue140_always_resolve_on() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList());
        config.setFeature(ResolverFeature.ALWAYS_RESOLVE, true);
        resolver = new Resolver(config);

        try {
            String baseURI = URIUtils.cwd().resolve("src/test/resources/xml/ch01.xml").toString();
            Source result = resolver.resolve("", baseURI);
            assertTrue(result.getSystemId().endsWith(".xml"));
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }


}
