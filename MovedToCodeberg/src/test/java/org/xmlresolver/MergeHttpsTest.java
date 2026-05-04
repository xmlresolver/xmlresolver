package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;

public class MergeHttpsTest {
    public static CatalogManager mergeManager = null;
    public static CatalogManager noMergeManager = null;
    @BeforeEach
    public void setup() {
        String catalog = "classpath:org/xmlresolver/data/catalog.xml";
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList(catalog));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        config.setFeature(ResolverFeature.MERGE_HTTPS, true);
        mergeManager = config.getFeature(ResolverFeature.CATALOG_MANAGER);

        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList(catalog));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, true);
        config.setFeature(ResolverFeature.MERGE_HTTPS, false);
        noMergeManager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
    }

    // For convenience, this set of tests uses the data catalog because it's known to contain
    // both http: and https: URIs.

    @Test
    public void equivalentcp1() {
        Assertions.assertEquals("classpath:path/to/thing", noMergeManager.normalizedForComparison("classpath:/path/to/thing"));
    }

    @Test
    public void equivalentcp2() {
        Assertions.assertEquals("classpath:path/to/thing", noMergeManager.normalizedForComparison("classpath:path/to/thing"));
    }

    @Test
    public void equivalentcp1m() {
        Assertions.assertEquals("classpath:path/to/thing", mergeManager.normalizedForComparison("classpath:/path/to/thing"));
    }

    @Test
    public void equivalentcp2m() {
        Assertions.assertEquals("classpath:path/to/thing", mergeManager.normalizedForComparison("classpath:path/to/thing"));
    }

    @Test
    public void equivalenthttp1() {
        Assertions.assertEquals("https://localhost/path/to/thing", noMergeManager.normalizedForComparison("https://localhost/path/to/thing"));
    }

    @Test
    public void equivalenthttp2() {
        Assertions.assertEquals("http://localhost/path/to/thing", noMergeManager.normalizedForComparison("http://localhost/path/to/thing"));
    }

    @Test
    public void equivalenthttp3() {
        Assertions.assertEquals("ftp://localhost/path/to/thing", noMergeManager.normalizedForComparison("ftp://localhost/path/to/thing"));
    }

    @Test
    public void equivalenthttp1m() {
        Assertions.assertEquals("https://localhost/path/to/thing", mergeManager.normalizedForComparison("https://localhost/path/to/thing"));
    }

    @Test
    public void equivalenthttp2m() {
        Assertions.assertEquals("https://localhost/path/to/thing", mergeManager.normalizedForComparison("http://localhost/path/to/thing"));
    }

    @Test
    public void equivalenthttp3m() {
        Assertions.assertEquals("ftp://localhost/path/to/thing", mergeManager.normalizedForComparison("ftp://localhost/path/to/thing"));
    }


    @Test
    public void lookupHttpUri() {
        URI result = mergeManager.lookupURI("http://www.w3.org/2001/xml.xsd");
        Assertions.assertNotNull(result);
    }

    @Test
    public void lookupHttpSystem() {
        URI result = mergeManager.lookupSystem("http://www.w3.org/MarkUp/DTD/xhtml-basic11.dtd");
        Assertions.assertNotNull(result);
    }

    @Test
    public void lookupHttpsSystem() {
        URI result = mergeManager.lookupSystem("https://www.rddl.org/rddl-xhtml.dtd");
        Assertions.assertNotNull(result);
    }

    @Test
    public void lookupHttpUriNoMerge() {
        URI result = noMergeManager.lookupURI("http://www.w3.org/2001/xml.xsd");
        Assertions.assertNull(result);
    }

    @Test
    public void lookupHttpSystemNoMerge() {
        URI result = noMergeManager.lookupSystem("http://www.w3.org/MarkUp/DTD/xhtml-basic11.dtd");
        Assertions.assertNull(result);
    }

    @Test
    public void lookupHttpsSystemNoMerge() {
        URI result = noMergeManager.lookupSystem("https://www.rddl.org/rddl-xhtml.dtd");
        Assertions.assertNull(result);
    }
}
