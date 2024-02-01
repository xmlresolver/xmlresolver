package org.xmlresolver;

import org.junit.Assert;
import org.junit.Test;
import org.xmlresolver.utils.URIUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.fail;

public class ResourceAccessTest {

    @Test
    public void testRelativeFileAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            ResourceRequest request = new ResourceRequest(config);

            String relativeURI = "src/test/resources/rescat.xml";

            request.setBaseURI(URIUtils.cwd());
            request.setURI(relativeURI);
            ResourceResponse resp = ResourceAccess.getResource(request);
            Assert.assertNotNull(resp);
            Assert.assertTrue(resp.isResolved());
            Assert.assertNotNull(resp.getInputStream());
            Assert.assertEquals(URIUtils.cwd().resolve(relativeURI), resp.getURI());
            Assert.assertEquals(URIUtils.cwd().resolve(relativeURI), resp.getResolvedURI());
            Assert.assertNotNull(resp.getURI());
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

    @Test
    public void testAbsoluteFileAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            ResourceRequest request = new ResourceRequest(config);

            String relativeURI = "src/test/resources/rescat.xml";
            URI absURI = URIUtils.cwd().resolve(relativeURI);

            request.setBaseURI("file:///tmp/");
            request.setURI(absURI);
            ResourceResponse resp = ResourceAccess.getResource(request);
            Assert.assertNotNull(resp);
            Assert.assertTrue(resp.isResolved());
            Assert.assertNotNull(resp.getInputStream());
            Assert.assertEquals(absURI, resp.getURI());
            Assert.assertEquals(absURI, resp.getResolvedURI());
            Assert.assertNotNull(resp.getURI());
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

    @Test
    public void testClasspathAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            ResourceRequest request = new ResourceRequest(config);

            // n.b., leading "/" is not how classpath: URIs should be constructed
            String uri = "classpath:/org/xmlresolver/schemas/catalog.xml";

            request.setURI(uri);
            ResourceResponse resp = ResourceAccess.getResource(request);
            Assert.assertNotNull(resp);
            Assert.assertTrue(resp.isResolved());
            Assert.assertNotNull(resp.getInputStream());
            Assert.assertEquals(URI.create("classpath:org/xmlresolver/schemas/catalog.xml"), resp.getURI());
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

    @Test
    public void testJarAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            config.setFeature(ResolverFeature.MASK_JAR_URIS, false);
            ResourceRequest request = new ResourceRequest(config);

            URI jarURI = URIUtils.cwd().resolve("src/test/resources/data1.jar");
            String uri = "jar:" + jarURI + "!/path/test.txt";

            request.setURI(uri);
            ResourceResponse resp = ResourceAccess.getResource(request);
            Assert.assertNotNull(resp);
            Assert.assertTrue(resp.isResolved());
            Assert.assertNotNull(resp.getInputStream());
            Assert.assertEquals(URI.create(uri), resp.getURI());
            Assert.assertEquals(URI.create(uri), resp.getResolvedURI());
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

    @Test
    public void testHttpAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            ResourceRequest request = new ResourceRequest(config);

            String uri = "http://localhost:8222/docs/iso-8859-1.txt";
            request.setURI(uri);
            ResourceResponse resp = ResourceAccess.getResource(request);
            Assert.assertNotNull(resp);
            Assert.assertTrue(resp.isResolved());
            Assert.assertNotNull(resp.getInputStream());
            Assert.assertEquals(200, resp.getStatusCode());
            Assert.assertEquals(URI.create(uri), resp.getURI());
            Assert.assertEquals("iso-8859-1", resp.getEncoding());
            Assert.assertFalse(resp.getHeaders().isEmpty());
            Assert.assertEquals("74", resp.getHeader("content-length"));
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

    @Test
    public void testHttpFollowRedirectAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            ResourceRequest request = new ResourceRequest(config);

            String uri = "http://localhost:8222/docs/foo";
            request.setURI(uri);
            ResourceResponse resp = ResourceAccess.getResource(request);
            Assert.assertNotNull(resp);
            Assert.assertTrue(resp.isResolved());
            Assert.assertNotNull(resp.getInputStream());
            Assert.assertEquals(200, resp.getStatusCode());
            Assert.assertEquals(URI.create("http://localhost:8222/docs/redirected.txt"), resp.getURI());
            Assert.assertFalse(resp.getHeaders().isEmpty());
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

}
