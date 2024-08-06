package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Issue31Test {
    private EntityResolver2 maskingResolver;
    private EntityResolver2 nonMaskingResolver;
    private String exp;

    @BeforeEach
    public void setUp() {
        URL rsrc = getClass().getClassLoader().getResource("path/catalog.xml");
        Assertions.assertNotNull(rsrc);

        exp = rsrc.toString();

        String catalog = "<catalog xmlns='urn:oasis:names:tc:entity:xmlns:xml:catalog'>\n" +
                "  <system systemId='urn:oasis:names:tc:dita:rng:topic.rng'\n" +
                "          uri='" + exp + "'/>\n" +
                "  <system systemId='https://example.com/topic.rng'\n" +
                "          uri='" + exp + "'/>\n" +
                "</catalog>\n";

        final XMLResolverConfiguration configuration = new XMLResolverConfiguration();
        configuration.setFeature(ResolverFeature.MASK_JAR_URIS, true);
        InputStream is = new ByteArrayInputStream(catalog.getBytes(StandardCharsets.UTF_8));
        configuration.addCatalog(URI.create("http://xmlresolver.org/test"), new InputSource(is));
        maskingResolver = new XMLResolver(configuration).getEntityResolver2();

        final XMLResolverConfiguration nonMaskingConfiguration = new XMLResolverConfiguration();
        nonMaskingConfiguration.setFeature(ResolverFeature.MASK_JAR_URIS, false);
        is = new ByteArrayInputStream(catalog.getBytes(StandardCharsets.UTF_8));
        nonMaskingConfiguration.addCatalog(URI.create("http://xmlresolver.org/test"), new InputSource(is));
        nonMaskingResolver = new XMLResolver(nonMaskingConfiguration).getEntityResolver2();
    }

    @Test
    public void urnUnMasked() throws IOException, SAXException {
        final InputSource act = nonMaskingResolver.resolveEntity(null, null, "/", "urn:oasis:names:tc:dita:rng:topic.rng");
        Assertions.assertEquals(exp, act.getSystemId());
    }

    @Test
    public void urnMasked() throws IOException, SAXException {
        final InputSource act = maskingResolver.resolveEntity(null, null, "/", "urn:oasis:names:tc:dita:rng:topic.rng");
        Assertions.assertEquals("urn:oasis:names:tc:dita:rng:topic.rng", act.getSystemId());
    }

    @Test
    public void httpsUnMasked() throws IOException, SAXException {
        final InputSource act = nonMaskingResolver.resolveEntity(null, null, "/", "https://example.com/topic.rng");
        Assertions.assertEquals(exp, act.getSystemId());
    }

    @Test
    public void httpsMasked() throws IOException, SAXException {
        final InputSource act = maskingResolver.resolveEntity(null, null, "/", "https://example.com/topic.rng");
        Assertions.assertEquals("https://example.com/topic.rng", act.getSystemId());
    }
}
