package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xmlresolver.catalog.query.QueryResult;

import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MergeHttpsTest {
    public static CatalogManager mergeManager = null;
    public static CatalogManager noMergeManager = null;
    @Before
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
        assertEquals("classpath:path/to/thing", noMergeManager.normalizedForComparison("classpath:/path/to/thing"));
    }

    @Test
    public void equivalentcp2() {
        assertEquals("classpath:path/to/thing", noMergeManager.normalizedForComparison("classpath:path/to/thing"));
    }

    @Test
    public void equivalentcp1m() {
        assertEquals("classpath:path/to/thing", mergeManager.normalizedForComparison("classpath:/path/to/thing"));
    }

    @Test
    public void equivalentcp2m() {
        assertEquals("classpath:path/to/thing", mergeManager.normalizedForComparison("classpath:path/to/thing"));
    }

    @Test
    public void equivalenthttp1() {
        assertEquals("https://localhost/path/to/thing", noMergeManager.normalizedForComparison("https://localhost/path/to/thing"));
    }

    @Test
    public void equivalenthttp2() {
        assertEquals("http://localhost/path/to/thing", noMergeManager.normalizedForComparison("http://localhost/path/to/thing"));
    }

    @Test
    public void equivalenthttp3() {
        assertEquals("ftp://localhost/path/to/thing", noMergeManager.normalizedForComparison("ftp://localhost/path/to/thing"));
    }

    @Test
    public void equivalenthttp1m() {
        assertEquals("https://localhost/path/to/thing", mergeManager.normalizedForComparison("https://localhost/path/to/thing"));
    }

    @Test
    public void equivalenthttp2m() {
        assertEquals("https://localhost/path/to/thing", mergeManager.normalizedForComparison("http://localhost/path/to/thing"));
    }

    @Test
    public void equivalenthttp3m() {
        assertEquals("ftp://localhost/path/to/thing", mergeManager.normalizedForComparison("ftp://localhost/path/to/thing"));
    }


    @Test
    public void lookupHttpUri() {
        URI result = mergeManager.lookupURI("http://www.w3.org/2001/xml.xsd");
        assertNotNull(result);
    }

    @Test
    public void lookupHttpSystem() {
        URI result = mergeManager.lookupSystem("http://www.w3.org/MarkUp/DTD/xhtml-basic11.dtd");
        assertNotNull(result);
    }

    @Test
    public void lookupHttpsSystem() {
        URI result = mergeManager.lookupSystem("https://www.rddl.org/rddl-xhtml.dtd");
        assertNotNull(result);
    }

    @Test
    public void lookupHttpUriNoMerge() {
        URI result = noMergeManager.lookupURI("http://www.w3.org/2001/xml.xsd");
        assertNull(result);
    }

    @Test
    public void lookupHttpSystemNoMerge() {
        URI result = noMergeManager.lookupSystem("http://www.w3.org/MarkUp/DTD/xhtml-basic11.dtd");
        assertNull(result);
    }

    @Test
    public void lookupHttpsSystemNoMerge() {
        URI result = noMergeManager.lookupSystem("https://www.rddl.org/rddl-xhtml.dtd");
        assertNull(result);
    }
}
