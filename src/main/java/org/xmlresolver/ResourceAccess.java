package org.xmlresolver;

import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.spi.SchemeResolver;
import org.xmlresolver.spi.SchemeResolverManager;
import org.xmlresolver.spi.SchemeResolverProvider;
import org.xmlresolver.utils.URIUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Static methods for accessing resources.
 *
 * <p>These methods support <code>data:</code> and <code>classpath:</code> URIs directly.
 * All other schemes are delegated to {@link ResourceConnection}.
 */
public class ResourceAccess {
    protected final ResolverLogger logger;
    final Map<String, ArrayList<SchemeResolver>> schemeResolvers = new HashMap<>();
    private boolean loadedSPI = false;

    /**
     * Make a new ResourceAccess object with the specified configuration.
     * <p>The configuration is used to determine the appropriate {@link ResolverLogger}.</p>
     * @param config The XML Resolver configuration to use.
     */
    public ResourceAccess(XMLResolverConfiguration config) {
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    /**
     * Attempt to get the requested resource.
     * <p>This method uses a {@link ResourceRequest} to formulate the request.</p>
     *
     * @param request The request.
     * @return The response.
     * @throws URISyntaxException if the request URI is syntactically invalid.
     * @throws IOException if the resource cannot be accessed.
     */
    public ResourceResponse getResource(ResourceRequest request) throws URISyntaxException, IOException {
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

        return getResourceFromURI(request, uri);
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
    public ResourceResponse getResource(ResourceResponse response) throws URISyntaxException, IOException {
        URI uri = response.getUnmaskedURI();
        if (uri == null && response.getRequest() != null) {
            uri = response.getRequest().getAbsoluteURI();
            if (uri == null && response.getRequest().getURI() != null) {
                uri = new URI(response.getRequest().getURI());
            }
        }

        if (uri == null) {
            throw new NullPointerException("URI must not be null in getResource");
        }

        if (!uri.isAbsolute()) {
            uri = URIUtils.resolve(URIUtils.cwd(), uri.toString());
        }

        return getResourceFromURI(response.getRequest(), uri);
    }

    private ResourceResponse getResourceFromURI(ResourceRequest request, URI uri) throws URISyntaxException, IOException {
        switch (uri.getScheme()) {
            case "data":
                return getDataResource(request, uri);
            case "classpath":
                return getClasspathResource(request, uri);
            case "jar":
                return getJarResource(request, uri);
            default:
                if (!loadedSPI) {
                    // Only do this once. There's a tradeoff here. We're going to hit this code over and over
                    // again. And many requests will be satisfied without asking providers. So to avoid
                    // looping through the providers unnecessarily, we do it once and build a map from
                    // scheme to provider so that subsequent requests can skip this work. The cost is that
                    // we construct SchemeResolvers we may never use. But there aren't expected to be many of them.
                    loadedSPI = true;
                    synchronized (schemeResolvers) {
                        ServiceLoader<SchemeResolverProvider> loader = ServiceLoader.load(SchemeResolverProvider.class);
                        for (SchemeResolverProvider provider : loader) {
                            SchemeResolverManager manager = provider.create();
                            for (String scheme : manager.getKnownSchemes()) {
                                if (!schemeResolvers.containsKey(scheme)) {
                                    schemeResolvers.put(scheme, new ArrayList<>());
                                }
                                try {
                                    schemeResolvers.get(scheme).add(manager.getSchemeResolver(scheme));
                                } catch (Exception ex) {
                                    // just ignore it
                                }
                            }
                        }
                    }
                }
                String scheme = uri.getScheme();
                synchronized (schemeResolvers) {
                    for (SchemeResolver schemeResolver : schemeResolvers.computeIfAbsent(scheme, k -> new ArrayList<>())) {
                        ResourceResponse resp = schemeResolver.getResource(request, uri);
                        if (resp != null) {
                            return resp;
                        }
                    }
                }

                // Fallback to the URL resolver
                return getNetResource(request, uri);
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

            ResourceResponseImpl resp = new ResourceResponseImpl(request, resourceURI);
            resp.setInputStream(inputStream);
            resp.setContentType(contentType);
            return resp;
        } else {
            boolean throwExceptions = request.getConfiguration().getFeature(ResolverFeature.THROW_URI_EXCEPTIONS);
            ResolverLogger logger = request.getConfiguration().getFeature(ResolverFeature.RESOLVER_LOGGER);
            logger.log(AbstractLogger.REQUEST, "Comma separator missing in data: URI");
            if (throwExceptions) {
                throw new URISyntaxException(resourceURI.toString(), "Comma separator missing in data: URI");
            }
            return new ResourceResponseImpl(request);
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
        URL rsrc = request.getConfiguration().getFeature(ResolverFeature.CLASSLOADER).getResource(path);
        if (rsrc == null) {
            return new ResourceResponseImpl(request);
        } else {
            try {
                ResourceResponseImpl resp = new ResourceResponseImpl(request, resourceURI);
                resp.setInputStream(rsrc.openStream());
                return resp;
            } catch (IOException ex) {
                boolean throwExceptions = request.getConfiguration().getFeature(ResolverFeature.THROW_URI_EXCEPTIONS);
                ResolverLogger logger = request.getConfiguration().getFeature(ResolverFeature.RESOLVER_LOGGER);
                logger.log(AbstractLogger.REQUEST, "I/O error reading %s", resourceURI.toString());
                if (throwExceptions) {
                    throw new IllegalArgumentException("I/O error reading " + resourceURI);
                }
                return new ResourceResponseImpl(request);
            }
        }
    }

    private static ResourceResponse getJarResource(ResourceRequest request, URI resourceURI) {
        try {
            ResourceResponseImpl resp = new ResourceResponseImpl(request, resourceURI);
            JarURLConnection conn = (JarURLConnection) resourceURI.toURL().openConnection();
            resp.setUri(request.getAbsoluteURI());
            resp.setInputStream(conn.getInputStream());
            resp.setEncoding(conn.getContentEncoding());
            resp.setContentType(conn.getContentType());
            return resp;
        } catch (MalformedURLException|URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        } catch (IOException ex) {
            boolean throwExceptions = request.getConfiguration().getFeature(ResolverFeature.THROW_URI_EXCEPTIONS);
            ResolverLogger logger = request.getConfiguration().getFeature(ResolverFeature.RESOLVER_LOGGER);
            logger.log(AbstractLogger.REQUEST, "I/O error reading %s", resourceURI.toString());
            if (throwExceptions) {
                throw new IllegalArgumentException("I/O error reading " + resourceURI);
            }
            return new ResourceResponseImpl(request);
        }
    }

    private static ResourceResponse getNetResource(ResourceRequest request, URI resourceURI) {
        ResolverConfiguration config = request.getConfiguration();
        ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);

        final boolean mergeHttps = config.getFeature(ResolverFeature.MERGE_HTTPS);
        final String accessList;
        if (request.isResolvingAsEntity()) {
            accessList = config.getFeature(ResolverFeature.ACCESS_EXTERNAL_ENTITY);
        } else {
            accessList = config.getFeature(ResolverFeature.ACCESS_EXTERNAL_DOCUMENT);
        }

        // We come through this code path when a resource was successfully resolved,
        // but it wasn't a data:, jar: or, classpath: URI. We still want to retrieve it,
        // so we'll try http:, https:, or anything supported by Java's URLConnection.
        // Note that the resourceURI will be absolute at this point.
        if (URIUtils.forbidAccess(accessList.concat(",file"), resourceURI.toString(), mergeHttps)) {
            if (request.isResolvingAsEntity()) {
                logger.log(AbstractLogger.REQUEST, "resolveEntity, access denied: " + resourceURI);
                throw new IllegalArgumentException("resolveEntity, access denied: " + resourceURI);
            } else {
                logger.log(AbstractLogger.REQUEST, "resolveURI, access denied: " + resourceURI);
                throw new IllegalArgumentException("resolveURI, access denied: " + resourceURI);
            }
        }

        ResourceConnection connx = new ResourceConnection(resourceURI);
        connx.get(request.getConfiguration(), !request.isOpenStream());
        URI redirect = connx.getRedirect();
        URI uri = redirect == null ? resourceURI : redirect;
        ResourceResponseImpl resp = new ResourceResponseImpl(request, uri);
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


