package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;
import org.xmlresolver.utils.PublicId;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class CMPreferTest {
    private final URI baseURI = URI.create("file:///tmp/");
    private CatalogManager manager = null;

    @Before
    public void setup() throws URISyntaxException {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.PREFER_PUBLIC, true);
        config.addCatalog("src/test/resources/cm/pref-public.xml");
        manager = new CatalogManager(config);
    }

    @Test
    public void publicTest1() {
        URI expected = baseURI.resolve("prefer-public.dtd");
        URI result = manager.lookupPublic("http://example.com/miss", "-//EXAMPLE//DTD Example//EN");
        assertEquals(expected, result);
    }

    @Test
    public void publicTest2() {
        URI expected = baseURI.resolve("system.dtd");
        URI result = manager.lookupPublic("http://example.com/system.dtd", "-//EXAMPLE//DTD Example//EN");
        assertEquals(expected, result);
    }

    @Test
    public void publicTest3() {
        URI expected = baseURI.resolve("prefer-system.dtd");
        URI result = manager.lookupNotation("irrelevant", null, "-//EXAMPLE//DTD Example//EN");
        assertEquals(expected, result);
    }

    @Test
    public void publicTest4() {
        URI expected = baseURI.resolve("prefer-system.dtd");
        URI result = manager.lookupPublic(PublicId.encodeURN("-//EXAMPLE//DTD Example//EN").toString(), null);
        assertEquals(expected, result);
    }

    @Test
    public void publicTest5() {
        URI expected = baseURI.resolve("prefer-system.dtd");
        URI result = manager.lookupPublic(PublicId.encodeURN("-//EXAMPLE//DTD Different//EN").toString(), "-//EXAMPLE//DTD Example//EN");
        assertEquals(expected, result);
    }

}
