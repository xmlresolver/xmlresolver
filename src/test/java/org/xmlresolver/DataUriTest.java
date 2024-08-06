/*
 * ResourceResolverTest.java
 * JUnit based test
 *
 * Created on December 30, 2006, 1:32 AM
 */

package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author ndw
 */
public class DataUriTest {
    private static XMLResolver resolver = null;

    @BeforeEach
    public void setup() throws Exception {
        XMLResolverConfiguration config = new XMLResolverConfiguration("src/test/resources/datauri.xml");
        resolver = new XMLResolver(config);
    }

    @Test
    public void testDataURItext() {
        String href = "example.txt";
        String base = "http://example.com/";

        String line = null;
        try {
            ResourceRequest request = resolver.getRequest(href, base);
            ResourceResponse result = resolver.resolve(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        Assertions.assertEquals("A short note.", line);
    }

    @Test
    public void testDataURIcharset() {
        String href = "greek.txt";
        String base = "http://example.com/";

        String line = null;
        try {
            ResourceRequest request = resolver.getRequest(href, base);
            ResourceResponse result = resolver.resolve(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        Assertions.assertEquals("ΎχΎ", line);
    }

    @Test
    public void testDataURIencoded() {
        String href = "example.xml";
        String base = "http://example.com/";

        String line = null;
        try {
            ResourceRequest request = resolver.getRequest(href, base);
            ResourceResponse result = resolver.resolve(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream()));
            line = reader.readLine();
        } catch (IOException ex) {
            // ignore
        }
        Assertions.assertEquals("<doc>I was a data URI</doc>", line);
    }
}
