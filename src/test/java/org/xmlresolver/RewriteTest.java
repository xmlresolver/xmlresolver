package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import java.util.Collections;

import static org.junit.Assert.*;

public class RewriteTest {
    public static final String catalog1 = "src/test/resources/rewrite.xml";
    XMLResolverConfiguration config = null;
    Resolver resolver = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        resolver = new Resolver(config);
    }

    @Test
    public void rewriteUriPath() {
        try {
            Source source = resolver.resolve("http://www.example.org/path/to/thing", null);
            assertNotNull(source);
            assertTrue(source.getSystemId().endsWith("/src/test/resources/to/thing"));
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void rewriteWholeUri() {
        try {
            Source source = resolver.resolve("http://www.example.org/schema/location.xsd", null);
            assertNotNull(source);
            assertTrue(source.getSystemId().endsWith("/src/test/resources/to/location.xsd"));
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void rewriteSystemIdPath() {
        try {
            InputSource source = resolver.resolveEntity(null, "http://www.example.org/path/to/thing");
            assertNotNull(source);
            assertTrue(source.getSystemId().endsWith("/src/test/resources/to/thing"));
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void rewriteWholeSystemId() {
        try {
            InputSource source = resolver.resolveEntity(null, "http://www.example.org/schema/location.dtd");
            assertNotNull(source);
            assertTrue(source.getSystemId().endsWith("/src/test/resources/to/location.dtd"));
        } catch (Exception ex) {
            fail();
        }
    }
}
