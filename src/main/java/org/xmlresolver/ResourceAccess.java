package org.xmlresolver;

import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.utils.URIUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Static methods for accessing resources.
 *
 * <p>These methods support <code>data:</code> and <code>classpath:</code> URIs directly.
 * All other schemes are delegated to {@link ResourceConnection}.
 */
public class ResourceAccess {
    /**
     * Attempt to get the requested resource.
     * <p>This method uses a {@link ResourceRequest} to formulate the request.</p>
     *
     * @param request The request.
     * @return The response.
     * @throws URISyntaxException if the request URI is syntactically invalid.
     * @throws IOException if the resource cannot be accessed.
     */
    public static ResourceResponse getResource(ResourceRequest request) throws URISyntaxException, IOException {
        URI uri = request.getAbsoluteURI();
        if (uri == null && request.getURI() != null) {
            uri = new URI(request.getURI());
        }

        if (uri == null) {
            throw new NullPointerException("URI must not be null in getResource");
        }

        if (!uri.isAbsolute()) {
            uri = URIUtils.resolve(URIUtils.cwd(), uri.toString());
        }

        switch (uri.getScheme()) {
            case "data":
                return getDataResource(request, uri);
            case "classpath":
                return getClasspathResource(request, uri);
            case "jar":
                return getJarResource(request, uri);
            default:
                // If it's not data: or classpath:, let's hope Java's URLConnection class will read it...
                return getNetResource(request, uri);
        }
    }

    /**
     * Attempt to get the requested resource.
     * <p>This method uses a {@link ResourceResponse} to formulate the request, for example the response
     * from a catalog lookup request.</p>
     *
     * @param response The response.
     * @return The response.
     * @throws URISyntaxException if the request URI is syntactically invalid.
     * @throws IOException if the resource cannot be accessed.
     */
    public static ResourceResponse getResource(ResourceResponse response) throws URISyntaxException, IOException {
        URI uri = response.getUnmaskedURI();
        if (uri == null && response.request != null) {
            uri = response.request.getAbsoluteURI();
            if (uri == null && response.request.getURI() != null) {
                uri = new URI(response.request.getURI());
            }
        }

        if (uri == null) {
            throw new NullPointerException("URI must not be null in getResource");
        }

        if (!uri.isAbsolute()) {
            uri = URIUtils.resolve(URIUtils.cwd(), uri.toString());
        }

        switch (uri.getScheme()) {
            case "data":
                return getDataResource(response.request, uri);
            case "classpath":
                return getClasspathResource(response.request, uri);
            case "jar":
                return getJarResource(response.request, uri);
            default:
                // If it's not data:, classpath:, or jar:, let's hope Java's URLConnection class will read it...
                return getNetResource(response.request, uri);
        }
    }

    private static ResourceResponse getDataResource(ResourceRequest request, URI resourceURI) throws URISyntaxException {
        // This is a little bit crude; see RFC 2397
        InputStream inputStream = null;
        String contentType = null;

        // Can't use URI accessors because they percent decode the string incorrectly.
        String path = resourceURI.toString().substring(5);
        int pos = path.indexOf(",");
        if (pos >= 0) {
            String mediatype = path.substring(0, pos);
            String data = path.substring(pos + 1);
            if (mediatype.endsWith(";base64")) {
                // Base64 decode it
                inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
                contentType = mediatype.substring(0, mediatype.length() - 7);
            } else {
                // URL decode it
                String charset = "UTF-8";
                pos = mediatype.indexOf(";charset=");
                if (pos > 0) {
                    charset = mediatype.substring(pos + 9);
                    pos = charset.indexOf(";");
                    if (pos >= 0) {
                        charset = charset.substring(0, pos);
                    }
                }
                try {
                    data = URLDecoder.decode(data, charset);
                } catch (UnsupportedEncodingException ex) {
                    throw new IllegalArgumentException(ex);
                }
                inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
                contentType = mediatype.isEmpty() ? null : mediatype;
            }

            ResourceResponse resp = new ResourceResponse(request, resourceURI);
            resp.setInputStream(inputStream);
            resp.setContentType(contentType);
            return resp;
        } else {
            boolean throwExceptions = request.config.getFeature(ResolverFeature.THROW_URI_EXCEPTIONS);
            ResolverLogger logger = request.config.getFeature(ResolverFeature.RESOLVER_LOGGER);
            logger.log(AbstractLogger.REQUEST, "Comma separator missing in data: URI");
            if (throwExceptions) {
                throw new URISyntaxException(resourceURI.toString(), "Comma separator missing in data: URI");
            }
            return new ResourceResponse(request);
        }
    }

    private static ResourceResponse getClasspathResource(ResourceRequest request, URI resourceURI) {
        String path = resourceURI.getSchemeSpecificPart();
        if (path.startsWith("/")) {
            path = path.substring(1);
            resourceURI = URI.create("classpath:" + path);
        }

        // The URI class throws exceptions if you attempt to manipulate
        // classpath: URIs. Fair enough, given their ad hoc invention
        // by the Spring framework. We're going to cheat a little bit here
        // and replace the classpath: URI with the actual URI of the resource
        // found (if one is found). That means downstream processes will
        // have a "useful" URI. It still might not work, due to class loaders and
        // such, but at least it won't immediately blow up.
        URL rsrc = request.config.getFeature(ResolverFeature.CLASSLOADER).getResource(path);
        if (rsrc == null) {
            return new ResourceResponse(request);
        } else {
            try {
                ResourceResponse resp = new ResourceResponse(request, resourceURI);
                resp.setInputStream(rsrc.openStream());
                return resp;
            } catch (IOException ex) {
                boolean throwExceptions = request.config.getFeature(ResolverFeature.THROW_URI_EXCEPTIONS);
                ResolverLogger logger = request.config.getFeature(ResolverFeature.RESOLVER_LOGGER);
                logger.log(AbstractLogger.REQUEST, "I/O error reading %s", resourceURI.toString());
                if (throwExceptions) {
                    throw new IllegalArgumentException("I/O error reading " + resourceURI);
                }
                return new ResourceResponse(request);
            }
        }
    }

    private static ResourceResponse getJarResource(ResourceRequest request, URI resourceURI) {
        try {
            ResourceResponse resp = new ResourceResponse(request, resourceURI);
            JarURLConnection conn = (JarURLConnection) resourceURI.toURL().openConnection();
            resp.setUri(request.getAbsoluteURI());
            resp.setInputStream(conn.getInputStream());
            resp.setEncoding(conn.getContentEncoding());
            resp.setContentType(conn.getContentType());
            return resp;
        } catch (MalformedURLException|URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        } catch (IOException ex) {
            boolean throwExceptions = request.config.getFeature(ResolverFeature.THROW_URI_EXCEPTIONS);
            ResolverLogger logger = request.config.getFeature(ResolverFeature.RESOLVER_LOGGER);
            logger.log(AbstractLogger.REQUEST, "I/O error reading %s", resourceURI.toString());
            if (throwExceptions) {
                throw new IllegalArgumentException("I/O error reading " + resourceURI);
            }
            return new ResourceResponse(request);
        }
    }

    private static ResourceResponse getNetResource(ResourceRequest request, URI resourceURI) {
        ResolverConfiguration config = request.config;
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);

        final boolean mergeHttps = config.getFeature(ResolverFeature.MERGE_HTTPS);
        final String accessList;
        if (request.isResolvingAsEntity()) {
            accessList = config.getFeature(ResolverFeature.ACCESS_EXTERNAL_ENTITY);
        } else {
            accessList = config.getFeature(ResolverFeature.ACCESS_EXTERNAL_DOCUMENT);
        }

        // We want to test the request URI, and if it's relative to some base URI, we want
        // to make sure we're considering the absolute URI, not just the relative part.
        URI requestURI = null;
        requestURI = resourceURI;
        if (!requestURI.isAbsolute()) {
            requestURI = URIUtils.cwd().resolve(resourceURI);
        }

        if (URIUtils.forbidAccess(accessList.concat(",file"), requestURI.toString(), mergeHttps)) {
            if (request.isResolvingAsEntity()) {
                logger.log(AbstractLogger.REQUEST, "resolveEntity, access denied: " + requestURI);
            } else {
                logger.log(AbstractLogger.REQUEST, "resolveURI, access denied: " + requestURI);
            }
            return new ResourceResponse(request, true);
        }

        ResourceConnection connx = new ResourceConnection(request.config, resourceURI, !request.openStream());
        URI redirect = connx.getRedirect();
        URI uri = redirect == null ? resourceURI : redirect;
        ResourceResponse resp = new ResourceResponse(request, uri);
        resp.setResolved(connx.isConnected());
        resp.setResolvedURI(uri);
        resp.setConnection(connx);
        resp.setInputStream(connx.getStream());
        resp.setHeaders(connx.getHeaders());
        resp.setContentType(connx.getContentType());
        resp.setEncoding(connx.getEncoding());
        resp.setStatusCode(connx.getStatusCode());
        if (connx.getStatusCode() >= 400) {
            resp.setResolved(false);
        }
        return resp;
    }
}


