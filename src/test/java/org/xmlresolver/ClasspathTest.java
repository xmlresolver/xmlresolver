/*
 * ResourceResolverTest.java
 * JUnit based test
 *
 * Created on December 30, 2006, 1:32 AM
 */

package org.xmlresolver;


import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlresolver.tools.ResolvingXMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author ndw
 */
public class ClasspathTest {
    public static final List<String> catalogs = Arrays.asList("classpath:path/catalog.xml", "src/test/resources/cpcatalog.xml");
    public static XMLResolverConfiguration config = null;
    public static CatalogManager manager = null;
    private static ResourceResolverImpl resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, catalogs);
        manager = new CatalogManager(config);
        resolver = new ResourceResolverImpl(config);
    }

    @Test
    public void testLookup() {
        URI res = manager.lookupURI("http://example.com/example.xml");
        assertEquals(URI.create("classpath:path/example-doc.xml"), res);
    }

    @Test
    public void testResource() {
        Resolver resolver = new Resolver(config);
        ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        try {
            reader.parse("src/test/resources/classpath.xml");
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void testClasspath() {
        String href = "classpath:path/example-doc.xml";

        String line = null;
        try {
            ResolvedResource result = resolver.resolveURI(href, null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>test</doc>", line);
    }

    @Test
    public void testClasspathSlash() {
        String href = "classpath:/path/example-doc.xml";

        String line = null;
        try {
            ResolvedResource result = resolver.resolveURI(href, null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>test</doc>", line);
    }

    @Test
    public void testClasspathText() {
        String href = "test.txt";
        String base = "http://example.com/";

        String line = null;
        try {
            ResolvedResource result = resolver.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("This is a test.", line);
    }

    @Test
    public void testClasspathXml() {
        String href = "example.xml";
        String base = "http://example.com/";

        String line = null;
        try {
            ResolvedResource result = resolver.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>test</doc>", line);
    }

    @Test
    public void testClasspathCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration("classpath:org/xmlresolver/test/no-such-catalog.xml;classpath:path/catalog.xml");
        //CatalogManager cx = new CatalogManager(config);
        ResourceResolverImpl cpres = new ResourceResolverImpl(config);

        String href = "example.xml";
        String base = "http://example.com/";

        String line = null;
        try {
            ResolvedResource result = cpres.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>test</doc>", line);

    }

}
