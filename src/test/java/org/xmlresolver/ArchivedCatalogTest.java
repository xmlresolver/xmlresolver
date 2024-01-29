package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlresolver.sources.ResolverInputSource;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;

public class ArchivedCatalogTest {
    public static final String catalog1 = "src/test/resources/sample.zip";
    public static final String catalog2 = "src/test/resources/dir-sample.zip";
    public static final String catalog3 = "src/test/resources/sample-org.zip";

    @Before
    public void setup() {
    }

    @Test
    public void archivedCatalogAllowed() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
            config.setFeature(ResolverFeature.ARCHIVED_CATALOGS, true);
            config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
            config.setFeature(ResolverFeature.MASK_JAR_URIS, false);
            XMLResolver resolver = new XMLResolver(config);

            InputSource source = resolver.getEntityResolver().resolveEntity(null, "https://xmlresolver.org/ns/zipped/sample.dtd");
            assertNotNull(source.getByteStream());
            ResolverInputSource rsource = ((ResolverInputSource) source);
            assertTrue(rsource.resolvedURI.toString().startsWith("jar:file:"));
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void archivedCatalogForbidden() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
            config.setFeature(ResolverFeature.ARCHIVED_CATALOGS, false);
            config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
            XMLResolver resolver = new XMLResolver(config);

            resolver.getConfiguration().setFeature(ResolverFeature.ALWAYS_RESOLVE, false);
            InputSource source = resolver.getEntityResolver().resolveEntity(null, "https://xmlresolver.org/ns/zipped/sample.dtd");
            assertNull(source);
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void archivedDirectoryCatalog() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog2));
            config.setFeature(ResolverFeature.ARCHIVED_CATALOGS, true);
            config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
            config.setFeature(ResolverFeature.MASK_JAR_URIS, false);
            XMLResolver resolver = new XMLResolver(config);

            InputSource source = resolver.getEntityResolver().resolveEntity(null, "https://xmlresolver.org/ns/zipped/sample.dtd");
            assertNotNull(source.getByteStream());
            ResolverInputSource rsource = ((ResolverInputSource) source);
            assertTrue(rsource.resolvedURI.toString().startsWith("jar:file:"));
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void archivedDirectoryCatalogOrgXmlResolver() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog3));
            config.setFeature(ResolverFeature.ARCHIVED_CATALOGS, true);
            config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
            XMLResolver resolver = new XMLResolver(config);

            InputSource source = resolver.getEntityResolver().resolveEntity(null, "https://xmlresolver.org/ns/zipped/sample.dtd");
            assertNotNull(source.getByteStream());
            ResolverInputSource rsource = ((ResolverInputSource) source);
            assertTrue(rsource.resolvedURI.toString().startsWith("jar:file:"));
            assertEquals("https://xmlresolver.org/ns/zipped/sample.dtd", rsource.getSystemId());
        } catch (IOException | SAXException ex) {
            fail();
        }
    }

    @Test
    public void archivedDirectoryCatalogOrgXmlResolverUnmasked() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog3));
            config.setFeature(ResolverFeature.ARCHIVED_CATALOGS, true);
            config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
            config.setFeature(ResolverFeature.MASK_JAR_URIS, false);
            XMLResolver resolver = new XMLResolver(config);

            InputSource source = resolver.getEntityResolver().resolveEntity(null, "https://xmlresolver.org/ns/zipped/sample.dtd");
            assertNotNull(source.getByteStream());
            ResolverInputSource rsource = ((ResolverInputSource) source);
            assertTrue(rsource.resolvedURI.toString().startsWith("jar:file:"));
            assertTrue(rsource.getSystemId().startsWith("jar:file:"));
        } catch (IOException | SAXException ex) {
            fail();
        }
    }
}

