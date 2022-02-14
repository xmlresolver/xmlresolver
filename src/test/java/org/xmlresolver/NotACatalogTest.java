package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlresolver.Resolver;
import org.xmlresolver.ResolverFeature;
import org.xmlresolver.XMLResolverConfiguration;
import org.xmlresolver.sources.ResolverInputSource;
import org.xmlresolver.utils.URIUtils;

import javax.xml.transform.Source;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.*;

public class NotACatalogTest {
    public static final String catalog1 = "src/test/resources/notcatalog.xml";
    public static final String catalog2 = "src/test/resources/notcatalog2.xml";

    @Test
    public void lookupSystem() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));;
            Resolver resolver = new Resolver(config);
            config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);

            // Test for https://github.com/xmlresolver/xmlresolver/issues/59
            // It doesn't matter what we look up; the catalog isn't an XML catalog.
            // But it should return null, not throw an NPE.
            InputSource source = resolver.resolveEntity(null, "https://example.com/sample/1.0/sample.dtd");
            assertNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupSystem2() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog2));;
            Resolver resolver = new Resolver(config);
            config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);

            Source source = resolver.resolve("test.xml", null);
            assertNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

}
