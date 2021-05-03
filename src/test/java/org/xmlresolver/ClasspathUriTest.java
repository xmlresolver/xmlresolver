/*
 * ResourceResolverTest.java
 * JUnit based test
 *
 * Created on December 30, 2006, 1:32 AM
 */

package org.xmlresolver;


import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author ndw
 */
public class ClasspathUriTest {
    private static Catalog catalog = null;
    private static ResourceResolver resolver = null;
    private static Resolver entityResolver = null;

    @Before
    public void setUp() throws Exception {
        catalog = new Catalog("src/test/resources/catalogs/classpathuri.xml");
        resolver = new ResourceResolver(catalog);
    }

    @Test
    public void testClasspathText() {
        String href = "test.txt";
        String base = "http://example.com/";

        String line = null;
        try {
            Resource result = resolver.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.body()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("This is a test.", line);
    }

    @Test
    public void testClasspathStarText() {
        String href = "all.txt";
        String base = "http://example.com/";

        String line = null;
        try {
            Resource result = resolver.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.body()));
            line = reader.readLine();
            assertEquals("This is a test.", line);
            line = reader.readLine();
            assertEquals("This is a another test.", line);
        } catch (IOException ex) {
            fail();
        }
    }

    @Test
    public void testClasspathXml() {
        String href = "example.xml";
        String base = "http://example.com/";

        String line = null;
        try {
            Resource result = resolver.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.body()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>test</doc>", line);
    }

    @Test
    public void testClasspathCatalog() {
        Catalog cpcat = new Catalog("classpath:org/xmlresolver/test/no-such-catalog.xml;classpath:org/xmlresolver/test/catalog.xml");
        ResourceResolver cpres = new ResourceResolver(cpcat);

        String href = "example.xml";
        String base = "http://example.com/";

        String line = null;
        try {
            Resource result = cpres.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.body()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>test</doc>", line);

    }
}
