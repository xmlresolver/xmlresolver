package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class CMSimpleTest {
    private final URI baseURI = URI.create("file:///tmp/");
    private CatalogManager manager = null;

    @BeforeEach
    public void setup() throws URISyntaxException {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.PREFER_PUBLIC, true);
        config.addCatalog("src/test/resources/cm/simple.xml");
        manager = new CatalogManager(config);
    }

    @Test
    public void publicTest1() {
        URI expected = baseURI.resolve("public.dtd");
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
    public void systemTest() {
        URI expected = baseURI.resolve("system.dtd");
        URI result = manager.lookupSystem("http://example.com/system.dtd");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void rewriteSystemTest() {
        URI expected = baseURI.resolve("local/path/system.dtd");
        URI result = manager.lookupSystem("http://example.com/rewrite/path/system.dtd");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void systemSuffixTest1() {
        URI expected = baseURI.resolve("suffix/base-long.dtd");
        URI result = manager.lookupSystem("http://example.com/path/base.dtd");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void systemSuffixTest2() {
        URI expected = baseURI.resolve("suffix/base-short.dtd");
        URI result = manager.lookupSystem("http://example.com/alternate/base.dtd");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void uriTest1() {
        URI expected = baseURI.resolve("/path/document.xml");
        URI result = manager.lookupURI("http://example.com/document.xml");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void uriTest2() {
        URI expected = baseURI.resolve("/path/rddl.xml");
        URI result = manager.lookupURI("http://example.com/rddl.xml");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void uriTest3() {
        URI expected = baseURI.resolve("/path/rddl.xml");
        URI result = manager.lookupNamespaceURI("http://example.com/rddl.xml",
                "nature", "purpose");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void uriTest4() {
        URI result = manager.lookupNamespaceURI("http://example.com/rddl.xml",
                "not-nature", "not-purpose");
        Assertions.assertNull(result);
    }

    @Test
    public void rewriteUriTest() {
        URI expected = baseURI.resolve("/path/local/docs/document.xml");
        URI result = manager.lookupURI("http://example.com/rewrite/docs/document.xml");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void uriSuffixTest1() {
        URI expected = baseURI.resolve("suffix/base-long.xml");
        URI result = manager.lookupURI("http://example.com/path/base.xml");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void uriSuffixTest2() {
        URI expected = baseURI.resolve("suffix/base-short.xml");
        URI result = manager.lookupURI("http://example.com/alternate/base.xml");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void bookTest1() {
        URI expected = baseURI.resolve("path/docbook.dtd");
        URI result = manager.lookupDoctype("book", null, null);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void bookTest2() {
        URI expected = baseURI.resolve("system.dtd");
        URI result = manager.lookupDoctype("book",
                "http://example.com/system.dtd", "-//EXAMPLE//DTD Example//EN");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void bookTest3() {
        URI expected = baseURI.resolve("public.dtd");
        URI result = manager.lookupDoctype("book",
                "http://example.com/miss.dtd", "-//EXAMPLE//DTD Example//EN");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void documentTest() {
        URI expected = baseURI.resolve("path/default.xml");
        URI result = manager.lookupDocument();
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void entityTest1() {
        URI expected = baseURI.resolve("chap01.xml");
        URI result = manager.lookupEntity("chap01", null, null);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void entityTest2() {
        URI expected = baseURI.resolve("system.dtd");
        URI result = manager.lookupEntity("chap01",
                "http://example.com/system.dtd", "-//EXAMPLE//DTD Example//EN");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void entityTest3() {
        URI expected = baseURI.resolve("public.dtd");
        URI result = manager.lookupEntity("chap01",
                "http://example.com/miss.dtd", "-//EXAMPLE//DTD Example//EN");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void notationTest1() {
        URI expected = baseURI.resolve("notation.xml");
        URI result = manager.lookupNotation("notename", null, null);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void notationTest2() {
        URI expected = baseURI.resolve("system.dtd");
        URI result = manager.lookupNotation("notename",
                "http://example.com/system.dtd", "-//EXAMPLE//DTD Example//EN");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void notationTest3() {
        URI expected = baseURI.resolve("public.dtd");
        URI result = manager.lookupNotation("notename",
                "http://example.com/miss.dtd", "-//EXAMPLE//DTD Example//EN");
        Assertions.assertEquals(expected, result);
    }

}
