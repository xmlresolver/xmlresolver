package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CatalogQuerierResponseTest {
    public static final String catalog1 = "src/test/resources/lookup1.xml";
    public static final String catalog2 = "src/test/resources/lookup2.xml";
    public static final URI catloc = URIUtils.cwd().resolve(catalog1);

    public static XMLResolverConfiguration config = null;
    public static CatalogManager manager = null;

    @BeforeEach
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Arrays.asList(catalog1, catalog2));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, false);
        manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
    }

    @Test
    public void lookupSystem() {
        URI result = manager.lookupSystem("https://example.com/sample/1.0/sample.dtd");
        Assertions.assertEquals(URIUtils.cwd().resolve(catalog1).resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupSystemMiss() {
        URI result = manager.lookupSystem("https://xmlresolver.org/ns/sample/sample.rng");
        Assertions.assertNull(result);
    }

    @Test
    public void lookupAbsoluteCatalogFile() {
        // This test checks that a catalog file passed in that starts at the root of the filesystem
        // works correctly. Specifically, that on Windows, C:\path\catalog.xml doesn't get mangled
        // into C:%2Fpath%2Fcatalog.xml or something worse.

        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());

        String cwd = System.getProperty("user.dir");
        if (!cwd.endsWith("/") && !cwd.endsWith("\\")) {
            cwd = cwd + "/";
        }

        config.setFeature(ResolverFeature.CATALOG_FILES, Arrays.asList(cwd + catalog1, cwd + catalog2));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, false);
        manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI result = manager.lookupSystem("https://example.com/sample/1.0/sample.dtd");
        Assertions.assertEquals(URIUtils.cwd().resolve(catalog1).resolve("sample10/sample-system.dtd"), result);
    }

    // ============================================================
    // See https://www.oasis-open.org/committees/download.php/14809/xml-catalogs.html#attrib.prefer
    // Note that the N/A entries in column three are a bit misleading.

    @Test
    public void lookupPublic_prefer_public_nosystem_public1() {
        // Catalog contains a matching public entry, but not a matching system entry
        URI result = manager.lookupPublic(null, "-//Sample//DTD Sample 1.0//EN");
        Assertions.assertEquals(catloc.resolve("sample10/sample-public.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_public_nosystem_public2() {
        // Catalog contains a matching system entry, but not a matching public entry
        Assertions.assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_public_nosystem_public3() {
        // Catalog contains both a matching system entry and a matching public entry
        Assertions.assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_public_system_nopublic1() {
        // Catalog contains a matching public entry, but not a matching system entry
        Assertions.assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_public_system_nopublic2() {
        // Catalog contains a matching system entry, but not a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", null);
        Assertions.assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_public_system_nopublic3() {
        // Catalog contains both a matching system entry and a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd",
                "-//Sample//DTD Sample Prefer System 1.0//EN");
        Assertions.assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_public_system_public1() {
        // Catalog contains a matching public entry, but not a matching system entry
        URI result = manager.lookupPublic("https://example.com/not-sample/1.0/sample.dtd", "-//Sample//DTD Sample 1.0//EN");
        Assertions.assertEquals(catloc.resolve("sample10/sample-public.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_public_system_public2() {
        // Catalog contains a matching system entry, but not a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", "-//Sample//DTD Not Sample 1.0//EN");
        Assertions.assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_public_system_public3() {
        // Catalog contains both a matching system entry and a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", "-//Sample//DTD Sample 1.0//EN");
        Assertions.assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_system_nosystem_public1() {
        // Catalog contains a matching public entry, but not a matching system entry
        URI result = manager.lookupPublic(null, "-//Sample//DTD Sample 1.0//EN");
        Assertions.assertEquals(catloc.resolve("sample10/sample-public.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_system_nosystem_public2() {
        // Catalog contains a matching system entry, but not a matching public entry
        Assertions.assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_system_nosystem_public3() {
        // Catalog contains both a matching system entry and a matching public entry
        Assertions.assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_system_system_nopublic1() {
        // Catalog contains a matching public entry, but not a matching system entry
        Assertions.assertTrue(true); // N/A
    }

    @Test
    public void lookupPublic_prefer_system_system_nopublic2() {
        // Catalog contains a matching system entry, but not a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", null);
        Assertions.assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_system_system_nopublic3() {
        // Catalog contains both a matching system entry and a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", null);
        Assertions.assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_system_system_public1() {
        // Catalog contains a matching public entry, but not a matching system entry
        URI result = manager.lookupPublic("https://example.com/not-sample/1.0/sample.dtd", "-//Sample//DTD Prefer Sample 1.0//EN");
        Assertions.assertNull(result);
    }

    @Test
    public void lookupPublic_prefer_system_system_public2() {
        // Catalog contains a matching system entry, but not a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", "-//Sample//DTD Not Sample 1.0//EN");
        Assertions.assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_system_system_public3() {
        // Catalog contains both a matching system entry and a matching public entry
        URI result = manager.lookupPublic("https://example.com/sample/1.0/sample.dtd", "-//Sample//DTD Prefer Sample 1.0//EN");
        Assertions.assertEquals(catloc.resolve("sample10/sample-system.dtd"), result);
    }

    // ============================================================

    @Test
    public void rewriteSystem() {
        URI result = manager.lookupSystem("https://example.com/path1/sample/3.0/sample.dtd");
        URI expected = URI.create("https://example.com/path2/sample/3.0/sample.dtd");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void systemSuffix() {
        URI result = manager.lookupSystem("https://example.com/whatever/you/want/suffix.dtd");
        Assertions.assertEquals(catloc.resolve("sample20/sample-suffix.dtd"), result);
    }

    @Test
    public void delegatePublicLong() {
        URI result = manager.lookupPublic(null, "-//Sample Delegated//DTD Sample 1.0//EN");
        Assertions.assertEquals(catloc.resolve("sample10/sample-delegated.dtd"), result);
    }

    @Test
    public void delegatePublicShort() {
        URI result = manager.lookupPublic(null, "-//Sample Delegated//DTD Sample 2.0//EN");
        Assertions.assertEquals(catloc.resolve("sample20/sample-shorter.dtd"), result);
    }

    @Test
    public void delegatePublicFail() {
        URI result = manager.lookupPublic(null, "-//Sample Delegated//DTD Sample 3.0//EN");
        Assertions.assertNull(result);
    }

    @Test
    public void delegateSystemLong() {
        URI result = manager.lookupPublic("https://example.com/delegated/sample/1.0/sample.dtd", null);
        Assertions.assertEquals(catloc.resolve("sample10/sample-delegated.dtd"), result);
    }

    @Test
    public void delegateSystemShort() {
        URI result = manager.lookupPublic("https://example.com/delegated/sample/2.0/sample.dtd", null);
        Assertions.assertEquals(catloc.resolve("sample20/sample-shorter.dtd"), result);
    }
    @Test
    public void delegateSystemFail() {
        URI result = manager.lookupPublic("https://example.com/delegated/sample/3.0/sample.dtd", null);
        Assertions.assertNull(result);
    }

    @Test
    public void undelegated() {
        // If there aren't any delegate entries, the entries in lookup2.xml really do match.
        XMLResolverConfiguration uconfig = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        uconfig.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList(catalog2));
        CatalogManager umanager = new CatalogManager(uconfig);

        URI result = umanager.lookupPublic(null, "-//Sample Delegated//DTD Sample 3.0//EN");
        Assertions.assertEquals(catloc.resolve("sample30/fail.dtd"), result);

        result = umanager.lookupPublic("https://example.com/delegated/sample/3.0/sample.dtd", null);
        Assertions.assertEquals(catloc.resolve("sample30/fail.dtd"), result);
    }

    // ============================================================

    @Test
    public void lookupUri() {
        URI result = manager.lookupURI("https://xmlresolver.org/ns/sample/sample.rng");
        Assertions.assertEquals(URIUtils.cwd().resolve(catalog1).resolve("sample/sample.rng"), result);
    }

    @Test
    public void rewriteUri() {
        URI result = manager.lookupURI("https://example.com/path1/sample/sample.rng");
        URI expected = URI.create("https://example.com/path2/sample/sample.rng");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void uriSuffix() {
        URI result = manager.lookupURI("https://example.com/whatever/you/want/suffix.rnc");
        Assertions.assertEquals(catloc.resolve("sample20/sample-suffix.rnc"), result);
    }

    @Test
    public void delegateUriLong() {
        URI result = manager.lookupURI("https://example.com/delegated/sample/1.0/sample.rng");
        Assertions.assertEquals(catloc.resolve("sample10/sample-delegated.rng"), result);
    }

    @Test
    public void delegateUriShort() {
        URI result = manager.lookupURI("https://example.com/delegated/sample/2.0/sample.rng");
        Assertions.assertEquals(catloc.resolve("sample20/sample-shorter.rng"), result);
    }

    @Test
    public void delegateUriFail() {
        URI result = manager.lookupURI("https://example.com/delegated/sample/3.0/sample.rng");
        Assertions.assertNull(result);
    }

    @Test
    public void undelegatedUri() {
        // If there aren't any delegate entries, the entries in lookup2.xml really do match.
        XMLResolverConfiguration uconfig = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        uconfig.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList(catalog2));
        CatalogManager umanager = new CatalogManager(uconfig);

        URI result = umanager.lookupURI("https://example.com/delegated/sample/3.0/sample.rng");
        Assertions.assertEquals(catloc.resolve("sample30/fail.rng"), result);
    }

    @Test
    public void baseUriRootTest() {
        // Make sure an xml:base attribute on the root element works
        List<String> catalog = Collections.singletonList(catloc.resolve("lookup-test.xml").toString());
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), catalog);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI result = manager.lookupPublic("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd", "-//W3C//DTD SVG 1.1//EN");
        Assertions.assertEquals("/usr/local/DTDs/svg11/system-svg11.dtd", result.getPath());
    }

    @Test
    public void baseUriGroupTest() {
        // Make sure an xml:base attribute on a group element works
        List<String> catalog = Collections.singletonList(catloc.resolve("lookup-test.xml").toString());
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), catalog);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI result = manager.lookupPublic("https://www.w3.org/Graphics/SVG/1.1/DTD/svg11-basic.dtd", "-//W3C//DTD SVG 1.1 Basic//EN");
        Assertions.assertEquals("/usr/local/nested/DTDs/svg11/system-svg11-basic.dtd", result.getPath());
    }

    @Test
    public void baseUriOnElementTest() {
        // Make sure an xml:base attribute on the actual element works
        List<String> catalog = Collections.singletonList(catloc.resolve("lookup-test.xml").toString());
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), catalog);
        config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
        CatalogManager manager = config.getFeature(ResolverFeature.CATALOG_MANAGER);
        URI result = manager.lookupSystem("https://example.com/test.dtd");
        Assertions.assertEquals("/usr/local/on/DTDs/test.dtd", result.getPath());
    }
}

