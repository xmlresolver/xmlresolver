/*
 * Resolver2Test.java
 * JUnit based test
 *
 * Created on January 2, 2007, 9:14 AM
 */

package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlresolver.sources.ResolverInputSource;
import org.xmlresolver.utils.URIUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author ndw
 */
public class ResolverTestJar {
    XMLResolverConfiguration config = null;
    XMLResolver resolver = null;

    @BeforeEach
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        resolver = new XMLResolver(config);
    }

    @Test
    public void lookupSystem() {
        String systemId = "https://xmlresolver.org/ns/sample/sample.dtd";

        try {
            config.setFeature(ResolverFeature.MASK_JAR_URIS, true);
            URI result = URIUtils.cwd().resolve("src/test/resources/sample10/sample.dtd");
            InputSource source = resolver.getEntityResolver().resolveEntity(null, systemId);
            Assertions.assertEquals(systemId, source.getSystemId());
            Assertions.assertNotNull(source.getByteStream());
            ResolverInputSource rsource = ((ResolverInputSource) source);

            URI actualURI = rsource.getResponse().getUnmaskedURI();
            Assertions.assertEquals("jar",actualURI.getScheme());
            Assertions.assertTrue(actualURI.getSchemeSpecificPart().startsWith("file:"));

            config.setFeature(ResolverFeature.MASK_JAR_URIS, false);
            source = resolver.getEntityResolver().resolveEntity(null, systemId);

            Assertions.assertTrue(source.getSystemId().startsWith("jar:file:"));
            Assertions.assertNotNull(source.getByteStream());
            rsource = ((ResolverInputSource) source);
            Assertions.assertEquals("jar", rsource.getResolvedURI().getScheme());
            Assertions.assertTrue(rsource.getResolvedURI().getSchemeSpecificPart().startsWith("file:"));
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void lookupSystemAsURI() {
        try {
            InputSource source = resolver.getEntityResolver().resolveEntity(null, "https://xmlresolver.org/ns/sample/uri.dtd");
            Assertions.assertTrue(source.getSystemId().endsWith("/sample/uri.dtd"));
            Assertions.assertNotNull(source.getByteStream());
            ResolverInputSource rsource = ((ResolverInputSource) source);

            String actualURI = rsource.getResponse().getUnmaskedURI().toString();
            Assertions.assertTrue(actualURI.startsWith("jar:file:/"));
            Assertions.assertTrue(actualURI.endsWith("data3.jar!/data/sample.dtd"));
        } catch (IOException | SAXException ex) {
            fail();
        }
    }
}
