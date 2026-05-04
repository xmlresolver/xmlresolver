package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class RewriteTest {
    public static final String catalog1 = "src/test/resources/rewrite.xml";
    XMLResolverConfiguration config = null;
    XMLResolver resolver = null;

    @BeforeEach
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        resolver = new XMLResolver(config);
    }

    @Test
    public void rewriteUriPath() {
        ResourceResponse resp = resolver.lookupUri("http://www.example.org/path/to/thing");
        Assertions.assertEquals("file:/path/to/thing", resp.getResolvedURI().toString());
    }

    @Test
    public void rewriteWholeUri() {
        ResourceResponse resp = resolver.lookupUri("http://www.example.org/schema/location.xsd");
        Assertions.assertEquals("file:/my/schema/location.xsd", resp.getResolvedURI().toString());
    }

    @Test
    public void rewriteSystemIdPath() {
        ResourceResponse resp = resolver.lookupEntity(null, "http://www.example.org/path/to/thing");
        Assertions.assertEquals("file:/path/to/thing", resp.getResolvedURI().toString());
    }

    @Test
    public void rewriteWholeSystemId() {
        ResourceResponse resp = resolver.lookupEntity(null,"http://www.example.org/schema/location.dtd");
        Assertions.assertEquals("file:/my/schema/location.dtd", resp.getResolvedURI().toString());
    }


}
