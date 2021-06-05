package org.xmlresolver;

import org.junit.Test;
import org.xmlresolver.exceptions.CatalogUnavailableException;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class LoaderTest {

    @Test
    public void nonValidatingValidCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.XmlLoader");
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI rsrc = manager.lookupSystem("https://xmlresolver.com/ns/sample/sample.dtd");
        assertNotNull(rsrc);
    }

    @Test
    public void nonValidatingInvalidCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/invalid-catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.XmlLoader");
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI rsrc = manager.lookupSystem("https://xmlresolver.com/ns/sample/sample.dtd");
        assertNotNull(rsrc);
    }

    @Test
    public void validatingValidCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.ValidatingXmlLoader");
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI rsrc = manager.lookupSystem("https://xmlresolver.com/ns/sample/sample.dtd");
        assertNotNull(rsrc);
    }

    @Test
    public void validatingInvalidCatalog() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/invalid-catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.ValidatingXmlLoader");
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        try {
            URI rsrc = manager.lookupSystem("https://xmlresolver.com/ns/sample/sample.dtd");
            assertNotNull(rsrc);
            fail();
        } catch (CatalogUnavailableException ex) {
            // pass
        }
    }

    @Test
    public void validatingMissingCatalog() {
        // File not found isn't a validation error
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Arrays.asList("./not-a-catalog-in-sight.xml", "classpath:/catalog.xml"));
        config.setFeature(ResolverFeature.CATALOG_LOADER_CLASS, "org.xmlresolver.loaders.ValidatingXmlLoader");
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        try {
            URI rsrc = manager.lookupSystem("https://xmlresolver.com/ns/sample/sample.dtd");
            assertNotNull(rsrc);
        } catch (CatalogUnavailableException ex) {
            fail();
        }
    }

}
