package org.xmlresolver;

import org.junit.Test;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class URIUtilsTest {
    @Test
    public void test0() throws URISyntaxException {
        URI uri = URIUtils.newURI("file:path/to/thing");
        assertEquals("file:///path/to/thing", uri.toASCIIString());
    }

    @Test
    public void test1() throws URISyntaxException {
        URI uri = URIUtils.newURI("file:/path/to/thing");
        assertEquals("file:///path/to/thing", uri.toASCIIString());
    }

    @Test
    public void test2() throws URISyntaxException {
        URI uri = URIUtils.newURI("file://path/to/thing");
        assertEquals("file:///path/to/thing", uri.toASCIIString());
    }

    @Test
    public void test3() throws URISyntaxException {
        URI uri = URIUtils.newURI("file:///path/to/thing");
        assertEquals("file:///path/to/thing", uri.toASCIIString());
    }

    @Test
    public void test4() throws URISyntaxException {
        URI uri = URIUtils.newURI("file:////path/to/thing");
        assertEquals("file:///path/to/thing", uri.toASCIIString());
    }

    @Test
    public void test5() throws URISyntaxException {
        URI uri = URIUtils.newURI("http://example.com/path/to/thing");
        assertEquals("http://example.com/path/to/thing", uri.toASCIIString());
    }

    @Test
    public void test6() throws URISyntaxException {
        URI uri = URIUtils.newURI("file://path/to/thing#foo");
        assertEquals("file:///path/to/thing#foo", uri.toASCIIString());
    }

    @Test
    public void test7() throws URISyntaxException {
        URI uri = URIUtils.newURI("/path/to/thing");
        assertEquals("file:///path/to/thing", uri.toASCIIString());
    }

    @Test
    public void test8() throws URISyntaxException {
        URI uri = URIUtils.newURI("/path/to/thing#foo");
        assertEquals("file:///path/to/thing#foo", uri.toASCIIString());
    }

    @Test
    public void test9() throws URISyntaxException {
        URI uri = URIUtils.newURI("file:///path/to/thing?query=5#foo");
        assertEquals("file:///path/to/thing%3Fquery=5#foo", uri.toASCIIString());
    }

    @Test
    public void test10() throws URISyntaxException {
        URI uri = URIUtils.newURI("/path/to/thing?query=5#foo");
        assertEquals("file:///path/to/thing%3Fquery=5#foo", uri.toASCIIString());
    }

    @Test
    public void test11() throws URISyntaxException {
        URI uri = URIUtils.newURI("#testing");
        assertEquals("#testing", uri.toASCIIString());
    }

    @Test
    public void test12() throws URISyntaxException {
        URI uri = URIUtils.newURI("testing");
        assertEquals("testing", uri.toASCIIString());
    }

    @Test
    public void test13() throws URISyntaxException {
        URI uri = URIUtils.newURI("testing#foo");
        assertEquals("testing#foo", uri.toASCIIString());
    }


}
