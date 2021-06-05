package org.xmlresolver;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CMNextTest {
    private final URI baseURI = URI.create("file:///tmp/");
    private CatalogManager manager = null;

    @Before
    public void setup() throws URISyntaxException {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.PREFER_PUBLIC, true);
        config.addCatalog("src/test/resources/cm/nextroot.xml");
        manager = new CatalogManager(config);
    }

    @Test
    public void nextTest1() {
        URI expected = baseURI.resolve("public.dtd");
        URI result = manager.lookupPublic("http://example.com/system-next.dtd", "-//EXAMPLE//DTD Example//EN");
        assertEquals(expected, result);
    }

    @Test
    public void nextTest2() {
        // no next required
        URI expected = baseURI.resolve("system.dtd");
        URI result = manager.lookupPublic("http://example.com/system.dtd", "-//EXAMPLE//DTD Example//EN");
        assertEquals(expected, result);
    }

    @Test
    public void nextTest3() {
        URI expected = baseURI.resolve("system-next.dtd");
        URI result = manager.lookupSystem("http://example.com/system-next.dtd");
        assertEquals(expected, result);
    }

    @Test
    public void nextTest4() {
        URI expected = baseURI.resolve("system-next.dtd");
        URI result = manager.lookupPublic("http://example.com/system-next.dtd", "-//EXAMPLE//DTD Example Next//EN");
        assertEquals(expected, result);
    }

    @Test
    public void nextTest5() {
        URI expected = baseURI.resolve("public-next.dtd");
        URI result = manager.lookupPublic("http://example.com/miss.dtd", "-//EXAMPLE//DTD Example Next//EN");
        assertEquals(expected, result);
    }

    @Test
    public void nextTest6() {
        URI expected = baseURI.resolve("found-in-one.xml");
        URI result = manager.lookupURI("http://example.com/document.xml");
        assertEquals(expected, result);
    }

    @Test
    public void delegateSystemTest1() {
        URI expected = baseURI.resolve("delegated-to-one.dtd");
        URI result = manager.lookupSystem("http://example.com/delegated/one/system.dtd");
        assertEquals(expected, result);
    }

    @Test
    public void delegateSystemTest2() {
        URI expected = baseURI.resolve("delegated-to-two.dtd");
        URI result = manager.lookupSystem("http://example.com/delegated/two/system.dtd");
        assertEquals(expected, result);
    }

    @Test
    public void delegateSystemTest3() {
        URI expected = baseURI.resolve("three-from-two.dtd");
        URI result = manager.lookupSystem("http://example.com/delegated/three/system.dtd");
        assertEquals(expected, result);
    }

    @Test
    public void delegateSystemTest4() {
        URI expected = baseURI.resolve("test-from-two.dtd");
        URI result = manager.lookupSystem("http://example.com/delegated/one/test/system.dtd");
        assertEquals(expected, result);
    }

    @Test
    public void delegateSystemTest5() {
        // This URI is in nextone.xml, but because nextroot.xml delegates to different catalogs,
        // it's never seen by the resolver.
        URI result = manager.lookupSystem("http://example.com/delegated/four/system.dtd");
        assertNull(result);
    }

    @Test
    public void delegateSystemTest6() {
        URI expected = baseURI.resolve("five-from-two.dtd");
        URI result = manager.lookupSystem("http://example.com/delegated/five/system.dtd");
        assertEquals(expected, result);
    }


}
