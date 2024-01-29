package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xmlresolver.utils.PublicId;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class UrnPublicTest {
    public static final String catalog1 = "src/test/resources/lookup1.xml";
    public static final String catalog2 = "src/test/resources/lookup2.xml";
    public static final URI catloc = URIUtils.cwd().resolve(catalog1);
    public static String urnPublicId = PublicId.encodeURN("-//Sample//DTD Sample 1.0//EN").toString();
    public static String urnNotPublicId = PublicId.encodeURN("-//Sample//DTD Not Sample 1.0//EN").toString();

    XMLResolverConfiguration config = null;
    public static CatalogManager manager = null;

    @Before
    public void setup() {
        config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.CATALOG_FILES, Arrays.asList(catalog1, catalog2));
        config.setFeature(ResolverFeature.URI_FOR_SYSTEM, false);
        manager = new CatalogManager(config);
    }

    @Test
    public void lookupPublic_prefer_public_nosystem_public1() {
        // Catalog contains a matching public entry, but not a matching system entry
        URI result = manager.lookupPublic(urnPublicId, null);
        assertEquals(catloc.resolve("sample10/sample-public.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_public_system_public1() {
        // Catalog contains a matching public entry, but not a matching system entry
        URI result = manager.lookupPublic(urnPublicId, null);
        assertEquals(catloc.resolve("sample10/sample-public.dtd"), result);
    }

    @Test
    public void lookupPublic_prefer_system_nosystem_public1() {
        // Catalog contains a matching public entry, but not a matching system entry
        URI result = manager.lookupPublic(urnPublicId, null);
        assertEquals(catloc.resolve("sample10/sample-public.dtd"), result);
    }
}

