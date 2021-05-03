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
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author ndw
 */
public class DataUriTest {
    private static Catalog catalog = null;
    private static ResourceResolver resolver = null;
    private static Resolver entityResolver = null;

    @Before
    public void setUp() throws Exception {
        catalog = new Catalog("src/test/resources/catalogs/datauri.xml");
        resolver = new ResourceResolver(catalog);
    }

    @Test
    public void testDataURItext() {
        String href = "example.txt";
        String base = "http://example.com/";

        String line = null;
        try {
            Resource result = resolver.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.body()));
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
            Resource result = resolver.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.body()));
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
            Resource result = resolver.resolveURI(href, base);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.body()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        assertEquals("<doc>I was a data URI</doc>", line);
    }
}
