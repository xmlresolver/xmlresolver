package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class CMNextTest {
    private final URI baseURI = URI.create("file:///tmp/");
    private CatalogManager manager = null;

    @BeforeEach
    public void setup() throws URISyntaxException {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.PREFER_PUBLIC, true);
        config.addCatalog("src/test/resources/cm/nextroot.xml");
        config.addCatalog("src/test/resources/cm/following.xml");
        manager = new CatalogManager(config);
    }

    @Test
    public void nextTest1() {
        URI expected = baseURI.resolve("public.dtd");
        URI result = manager.lookupPublic("http://example.com/system-next.dtd", "-//EXAMPLE//DTD Example//EN");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void nextTest2() {
        // no next required
        URI expected = baseURI.resolve("system.dtd");
        URI result = manager.lookupPublic("http://example.com/system.dtd", "-//EXAMPLE//DTD Example//EN");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void nextTest3() {
        URI expected = baseURI.resolve("system-next.dtd");
        URI result = manager.lookupSystem("http://example.com/system-next.dtd");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void nextTest4() {
        URI expected = baseURI.resolve("system-next.dtd");
        URI result = manager.lookupPublic("http://example.com/system-next.dtd", "-//EXAMPLE//DTD Example Next//EN");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void nextTest5() {
        URI expected = baseURI.resolve("public-next.dtd");
        URI result = manager.lookupPublic("http://example.com/miss.dtd", "-//EXAMPLE//DTD Example Next//EN");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void nextTest6() {
        URI expected = baseURI.resolve("found-in-one.xml");
        URI result = manager.lookupURI("http://example.com/document.xml");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void nextTest7() {
        // After looking in the next catalogs, continue in the following catalogs
        URI result = manager.lookupSystem("http://example.com/found-in-following.dtd");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.toString().endsWith("cm/following.dtd"));
    }

    @Test
    public void nextTest8() {
        // After looking in the delegated catalogs, do not return to the following catalogs
        URI result = manager.lookupSystem("http://example.com/delegated/but/not/found/in/delegated/catalogs.dtd");
        Assertions.assertNull(result);
    }

    @Test
    public void delegateSystemTest1() {
        URI expected = baseURI.resolve("delegated-to-one.dtd");
        URI result = manager.lookupSystem("http://example.com/delegated/one/system.dtd");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void delegateSystemTest2() {
        URI expected = baseURI.resolve("delegated-to-two.dtd");
        URI result = manager.lookupSystem("http://example.com/delegated/two/system.dtd");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void delegateSystemTest3() {
        URI expected = baseURI.resolve("three-from-two.dtd");
        URI result = manager.lookupSystem("http://example.com/delegated/three/system.dtd");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void delegateSystemTest4() {
        URI expected = baseURI.resolve("test-from-two.dtd");
        URI result = manager.lookupSystem("http://example.com/delegated/one/test/system.dtd");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void delegateSystemTest5() {
        // This URI is in nextone.xml, but because nextroot.xml delegates to different catalogs,
        // it's never seen by the resolver.
        URI result = manager.lookupSystem("http://example.com/delegated/four/system.dtd");
        Assertions.assertNull(result);
    }

    @Test
    public void delegateSystemTest6() {
        URI expected = baseURI.resolve("five-from-two.dtd");
        URI result = manager.lookupSystem("http://example.com/delegated/five/system.dtd");
        Assertions.assertEquals(expected, result);
    }


}
