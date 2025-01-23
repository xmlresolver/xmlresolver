package org.xmlresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlresolver.utils.URIUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.fail;

public class ResourceAccessTest {

    @Test
    public void testRelativeFileAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            ResourceRequest request = new ResourceRequestImpl(config);

            String relativeURI = "src/test/resources/rescat.xml";

            request.setBaseURI(URIUtils.cwd());
            request.setURI(relativeURI);
            ResourceResponse resp = config.getResource(request);
            Assertions.assertNotNull(resp);
            Assertions.assertTrue(resp.isResolved());
            Assertions.assertNotNull(resp.getInputStream());
            Assertions.assertEquals(URIUtils.cwd().resolve(relativeURI), resp.getURI());
            Assertions.assertEquals(URIUtils.cwd().resolve(relativeURI), resp.getResolvedURI());
            Assertions.assertNotNull(resp.getURI());
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

    @Test
    public void testAbsoluteFileAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            ResourceRequest request = new ResourceRequestImpl(config);

            String relativeURI = "src/test/resources/rescat.xml";
            URI absURI = URIUtils.cwd().resolve(relativeURI);

            request.setBaseURI("file:///tmp/");
            request.setURI(absURI);
            ResourceResponse resp = config.getResource(request);
            Assertions.assertNotNull(resp);
            Assertions.assertTrue(resp.isResolved());
            Assertions.assertNotNull(resp.getInputStream());
            Assertions.assertEquals(absURI, resp.getURI());
            Assertions.assertEquals(absURI, resp.getResolvedURI());
            Assertions.assertNotNull(resp.getURI());
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

    @Test
    public void testClasspathAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            // If jar (and classpath) URIs are masked, I get back the original classpath URI
            // that's not what's being tested here.
            config.setFeature(ResolverFeature.MASK_JAR_URIS, false);
            ResourceRequest request = new ResourceRequestImpl(config);

            // n.b., leading "/" is not how classpath: URIs should be constructed
            String uri = "classpath:/org/xmlresolver/schemas/catalog.xml";

            request.setURI(uri);
            ResourceResponse resp = config.getResource(request);
            Assertions.assertNotNull(resp);
            Assertions.assertTrue(resp.isResolved());
            Assertions.assertNotNull(resp.getInputStream());
            Assertions.assertEquals(URI.create("classpath:org/xmlresolver/schemas/catalog.xml"), resp.getURI());
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

    @Test
    public void testJarAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            config.setFeature(ResolverFeature.MASK_JAR_URIS, false);
            ResourceRequest request = new ResourceRequestImpl(config);

            URI jarURI = URIUtils.cwd().resolve("src/test/resources/data1.jar");
            String uri = "jar:" + jarURI + "!/path/test.txt";

            request.setURI(uri);
            ResourceResponse resp = config.getResource(request);
            Assertions.assertNotNull(resp);
            Assertions.assertTrue(resp.isResolved());
            Assertions.assertNotNull(resp.getInputStream());
            Assertions.assertEquals(URI.create(uri), resp.getURI());
            Assertions.assertEquals(URI.create(uri), resp.getResolvedURI());
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

    @Test
    public void testHttpAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            ResourceRequest request = new ResourceRequestImpl(config);

            String uri = "http://localhost:8222/docs/iso-8859-1.txt";
            request.setURI(uri);
            ResourceResponse resp = config.getResource(request);
            Assertions.assertNotNull(resp);
            Assertions.assertTrue(resp.isResolved());
            Assertions.assertNotNull(resp.getInputStream());
            Assertions.assertEquals(200, resp.getStatusCode());
            Assertions.assertEquals(URI.create(uri), resp.getURI());
            Assertions.assertEquals("iso-8859-1", resp.getEncoding());
            Assertions.assertFalse(resp.getHeaders().isEmpty());
            Assertions.assertEquals("74", resp.getHeader("content-length"));
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

    @Test
    public void testHttpFollowRedirectAccess() {
        try {
            XMLResolverConfiguration config = new XMLResolverConfiguration();
            ResourceRequest request = new ResourceRequestImpl(config);

            String uri = "http://localhost:8222/docs/foo";
            request.setURI(uri);
            ResourceResponse resp = config.getResource(request);
            Assertions.assertNotNull(resp);
            Assertions.assertTrue(resp.isResolved());
            Assertions.assertNotNull(resp.getInputStream());
            Assertions.assertEquals(200, resp.getStatusCode());
            Assertions.assertEquals(URI.create("http://localhost:8222/docs/redirected.txt"), resp.getURI());
            Assertions.assertFalse(resp.getHeaders().isEmpty());
        } catch (IOException | URISyntaxException ex) {
            fail();
        }
    }

}
