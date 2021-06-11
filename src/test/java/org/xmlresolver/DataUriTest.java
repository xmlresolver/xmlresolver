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

/**
 *
 * @author ndw
 */
public class DataUriTest {
    private static CatalogResolver resolver = null;

    @Before
    public void setUp() throws Exception {
        XMLResolverConfiguration config = new XMLResolverConfiguration("src/test/resources/datauri.xml");
        resolver = new CatalogResolver(config);
    }

    @Test
    public void testDataURItext() {
        String href = "example.txt";
        String base = "http://example.com/";

        String line = null;
        try {
            ResolvedResource result = resolver.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("A short note.", line);
    }

    @Test
    public void testDataURIcharset() {
        String href = "greek.txt";
        String base = "http://example.com/";

        String line = null;
        try {
            ResolvedResource result = resolver.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("ΎχΎ", line);
    }

    @Test
    public void testDataURIencoded() {
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
        assertEquals("<doc>I was a data URI</doc>", line);
    }
}
