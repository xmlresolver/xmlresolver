package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.fail;

public class NotACatalogTest {
    public static final String catalog1 = "src/test/resources/notcatalog.xml";
    public static final String catalog2 = "src/test/resources/notcatalog2.xml";

    @Test
    public void lookupSystem() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog1));;
            XMLResolver resolver = new XMLResolver(config);
            config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
            config.setFeature(ResolverFeature.ALWAYS_RESOLVE, false);

            // Test for https://github.com/xmlresolver/xmlresolver/issues/59
            // It doesn't matter what we look up; the catalog isn't an XML catalog.
            // But it should return null, not throw an NPE.
            InputSource source = resolver.getEntityResolver().resolveEntity(null, "https://example.com/sample/1.0/sample.dtd");
            Assertions.assertNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void lookupSystem2() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.singletonList(catalog2));;
            XMLResolver resolver = new XMLResolver(config);
            config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);

            Source source = resolver.getURIResolver().resolve("test.xml", null);
            Assertions.assertNull(source);
        } catch (Exception ex) {
            fail();
        }
    }

}
