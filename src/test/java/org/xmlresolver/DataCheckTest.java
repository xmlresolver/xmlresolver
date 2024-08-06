package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;

public class DataCheckTest {
    public static XMLResolverConfiguration config = null;
    public static CatalogManager manager = null;

    @BeforeEach
    public void setup() {
        String catalog = "classpath:org/xmlresolver/data/catalog.xml";
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList(catalog));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
    }

    @Test
    public void lookupCheckUri() {
        URI result = manager.lookupURI("https://xmlresolver.org/data/resolver/succeeded/test/check.xml");
        Assertions.assertNotNull(result);
    }

    @Test
    public void lookupCheckUriFail() {
        XMLResolverConfiguration localConfig = null;
        CatalogManager localManager = null;

        String catalog = "src/test/resources/catalog.xml";
        localConfig = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        localConfig.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        localConfig.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList(catalog));
        localConfig.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        localManager = localConfig.getFeature(ResolverFeature.CATALOG_MANAGER);

        URI result = localManager.lookupURI("https://xmlresolver.org/data/resolver/succeeded/test/check.xml");
        Assertions.assertNull(result);
    }

}
