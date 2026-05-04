package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xmlresolver.utils.PublicId;

import java.net.URI;
import java.net.URISyntaxException;

public class CMPreferTest {
    private final URI baseURI = URI.create("file:///tmp/");
    private CatalogManager manager = null;

    @BeforeEach
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
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void publicTest2() {
        URI expected = baseURI.resolve("system.dtd");
        URI result = manager.lookupPublic("http://example.com/system.dtd", "-//EXAMPLE//DTD Example//EN");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void publicTest3() {
        URI expected = baseURI.resolve("prefer-system.dtd");
        URI result = manager.lookupNotation("irrelevant", null, "-//EXAMPLE//DTD Example//EN");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void publicTest4() {
        URI expected = baseURI.resolve("prefer-system.dtd");
        URI result = manager.lookupPublic(PublicId.encodeURN("-//EXAMPLE//DTD Example//EN").toString(), null);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void publicTest5() {
        URI expected = baseURI.resolve("prefer-system.dtd");
        URI result = manager.lookupPublic(PublicId.encodeURN("-//EXAMPLE//DTD Different//EN").toString(), "-//EXAMPLE//DTD Example//EN");
        Assertions.assertEquals(expected, result);
    }

}
