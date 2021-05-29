package org.xmlresolver;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CatalogLookupTest {
    public static final String catalog1 = "src/test/resources/lookup1.xml";
    public static final String catalog2 = "src/test/resources/lookup2.xml";
    public static final @NotNull URI catloc = URIUtils.cwd().resolve(catalog1);

    public static XMLResolverConfiguration config = null;
    public static CatalogManager manager = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Arrays.asList(catalog1, catalog2));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, false);
        manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
    }

    @Test
    public void lookupSystem() {
        URI result = manager.lookupSystem("https://example.com/sample/1.0/sample.dtd");
        assertEquals(URIUtils.cwd().resolve(catalog1).resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupSystemMiss() {
        URI result = manager.lookupSystem("https://xmlresolver.org/ns/sample/sample.rng");
        assertNull(result);
    }

    // ============================================================
    // See https://www.oasis-open.org/committees/download.php/14809/xml-catalogs.html#attrib.prefer
    // Note that the N/A entries in column three are a bit misleading.

    @Test
    public void lookupPublic_prefer_public_nosystem_public1() {
        // Catalog contains a matching public entry, but not a matching system entry
        URI result = manager.lookupPublic(null, "-//Sample//DTD Sample 1.0//EN");
        assertEquals(catloc.resolve("sample10/sample-public.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_public_nosystem_public2() {
        // Catalog contains a matching system entry, but not a matching public entry
        assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_public_nosystem_public3() {
        // Catalog contains both a matching system entry and a matching public entry
        assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_public_system_nopublic1() {
        // Catalog contains a matching public entry, but not a matching system entry
        assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_public_system_nopublic2() {
        // Catalog contains a matching system entry, but not a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", null);
        assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_public_system_nopublic3() {
        // Catalog contains both a matching system entry and a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", null);
        assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_public_system_public1() {
        // Catalog contains a matching public entry, but not a matching system entry
        URI result = manager.lookupPublic("https://example.com/not-sample/1.0/sample.dtd", "-//Sample//DTD Sample 1.0//EN");
        assertEquals(catloc.resolve("sample10/sample-public.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_public_system_public2() {
        // Catalog contains a matching system entry, but not a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", "-//Sample//DTD Not Sample 1.0//EN");
        assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_public_system_public3() {
        // Catalog contains both a matching system entry and a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", "-//Sample//DTD Sample 1.0//EN");
        assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_system_nosystem_public1() {
        // Catalog contains a matching public entry, but not a matching system entry
        URI result = manager.lookupPublic(null, "-//Sample//DTD Sample 1.0//EN");
        assertEquals(catloc.resolve("sample10/sample-public.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_system_nosystem_public2() {
        // Catalog contains a matching system entry, but not a matching public entry
        assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_system_nosystem_public3() {
        // Catalog contains both a matching system entry and a matching public entry
        assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_system_system_nopublic1() {
        // Catalog contains a matching public entry, but not a matching system entry
        assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_system_system_nopublic2() {
        // Catalog contains a matching system entry, but not a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", null);
        assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_system_system_nopublic3() {
        // Catalog contains both a matching system entry and a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", null);
        assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_system_system_public1() {
        // Catalog contains a matching public entry, but not a matching system entry
        URI result = manager.lookupPublic("https://example.com/not-sample/1.0/sample.dtd", "-//Sample//DTD Prefer Sample 1.0//EN");
        assertNull(result);
    }

    @Test
    public void lookupPublic_prefer_system_system_public2() {
        // Catalog contains a matching system entry, but not a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", "-//Sample//DTD Not Sample 1.0//EN");
        assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_system_system_public3() {
        // Catalog contains both a matching system entry and a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", "-//Sample//DTD Prefer Sample 1.0//EN");
        assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    // ============================================================

    @Test
    public void rewriteSystem() {
        URI result = manager.lookupSystem("https://example.com/path1/sample/3.0/sample.dtd");
        URI expected = URI.create("https://example.com/path2/sample/3.0/sample.dtd");
        assertEquals(expected, result);
    }

    @Test
    public void systemSuffix() {
        URI result = manager.lookupSystem("https://example.com/whatever/you/want/suffix.dtd");
        assertEquals(catloc.resolve("sample20/sample-suffix.dtd"), result);
    }

    @Test
    public void delegatePublicLong() {
        URI result = manager.lookupPublic(null, "-//Sample Delegated//DTD Sample 1.0//EN");
        assertEquals(catloc.resolve("sample10/sample-delegated.dtd"), result);
    }

    @Test
    public void delegatePublicShort() {
        URI result = manager.lookupPublic(null, "-//Sample Delegated//DTD Sample 2.0//EN");
        assertEquals(catloc.resolve("sample20/sample-shorter.dtd"), result);
    }

    @Test
    public void delegatePublicFail() {
        URI result = manager.lookupPublic(null, "-//Sample Delegated//DTD Sample 3.0//EN");
        assertNull(result);
    }

    @Test
    public void delegateSystemLong() {
        URI result = manager.lookupPublic("https://example.com/delegated/sample/1.0/sample.dtd", null);
        assertEquals(catloc.resolve("sample10/sample-delegated.dtd"), result);
    }

    @Test
    public void delegateSystemShort() {
        URI result = manager.lookupPublic("https://example.com/delegated/sample/2.0/sample.dtd", null);
        assertEquals(catloc.resolve("sample20/sample-shorter.dtd"), result);
    }
    @Test
    public void delegateSystemFail() {
        URI result = manager.lookupPublic("https://example.com/delegated/sample/3.0/sample.dtd", null);
        assertNull(result);
    }

    @Test
    public void undelegated() {
        // If there aren't any delegate entries, the entries in lookup2.xml really do match.
        XMLResolverConfiguration uconfig = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        uconfig.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList(catalog2));
        CatalogManager umanager = new CatalogManager(uconfig);

        URI result = umanager.lookupPublic(null, "-//Sample Delegated//DTD Sample 3.0//EN");
        assertEquals(catloc.resolve("sample30/fail.dtd"), result);

        result = umanager.lookupPublic("https://example.com/delegated/sample/3.0/sample.dtd", null);
        assertEquals(catloc.resolve("sample30/fail.dtd"), result);
    }

    // ============================================================

    @Test
    public void lookupUri() {
        URI result = manager.lookupURI("https://xmlresolver.org/ns/sample/sample.rng");
        assertEquals(URIUtils.cwd().resolve(catalog1).resolve("sample/sample.rng"), result);
    }

    @Test
    public void rewriteUri() {
        URI result = manager.lookupURI("https://example.com/path1/sample/sample.rng");
        URI expected = URI.create("https://example.com/path2/sample/sample.rng");
        assertEquals(expected, result);
    }

    @Test
    public void uriSuffix() {
        URI result = manager.lookupURI("https://example.com/whatever/you/want/suffix.rnc");
        assertEquals(catloc.resolve("sample20/sample-suffix.rnc"), result);
    }

    @Test
    public void delegateUriLong() {
        URI result = manager.lookupURI("https://example.com/delegated/sample/1.0/sample.rng");
        assertEquals(catloc.resolve("sample10/sample-delegated.rng"), result);
    }

    @Test
    public void delegateUriShort() {
        URI result = manager.lookupURI("https://example.com/delegated/sample/2.0/sample.rng");
        assertEquals(catloc.resolve("sample20/sample-shorter.rng"), result);
    }

    @Test
    public void delegateUriFail() {
        URI result = manager.lookupURI("https://example.com/delegated/sample/3.0/sample.rng");
        assertNull(result);
    }

    @Test
    public void undelegatedUri() {
        // If there aren't any delegate entries, the entries in lookup2.xml really do match.
        XMLResolverConfiguration uconfig = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        uconfig.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList(catalog2));
        CatalogManager umanager = new CatalogManager(uconfig);

        URI result = umanager.lookupURI("https://example.com/delegated/sample/3.0/sample.rng");
        assertEquals(catloc.resolve("sample30/fail.rng"), result);
    }


}

