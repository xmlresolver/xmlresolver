package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Issue31Test {

    private static final String NS = "urn:oasis:names:tc:entity:xmlns:xml:catalog";

    private Resolver resolver;
    private String exp;

    @Before
    public void setUp() {
        URL rsrc = getClass().getClassLoader().getResource("org/xmlresolver/test/catalog.xml");
        assertNotNull(rsrc);

        exp = rsrc.toString();

        String catalog = "<catalog xmlns='urn:oasis:names:tc:entity:xmlns:xml:catalog'>\n" +
                "  <system systemId='urn:oasis:names:tc:dita:rng:topic.rng'\n" +
                "          uri='" + exp + "'/>\n" +
                "  <system systemId='https://example.com/topic.rng'\n" +
                "          uri='" + exp + "'/>\n" +
                "</catalog>\n";

        final XMLResolverConfiguration configuration = new XMLResolverConfiguration();
        InputStream is = new ByteArrayInputStream(catalog.getBytes(StandardCharsets.UTF_8));
        configuration.addCatalog(URI.create("http://xmlresolver.org/test"), new InputSource(is));
        resolver = new Resolver(configuration);
    }

    @Test
    public void urn() throws IOException, SAXException {
        final InputSource act = resolver.resolveEntity(null, null, "/", "urn:oasis:names:tc:dita:rng:topic.rng");
        // This fails because of the toURL() conversion
        assertEquals(exp, act.getSystemId());
    }

    @Test
    public void https() throws IOException, SAXException {
        final InputSource act = resolver.resolveEntity(null, null, "/", "https://example.com/topic.rng");
        assertEquals(exp, act.getSystemId());
    }
}
