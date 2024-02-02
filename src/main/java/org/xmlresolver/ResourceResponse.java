package org.xmlresolver;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@code ResourceResponse} is the return type for either an attempt to lookup a resource
 * in the catalog or to resolve a resource. It encapsulates the results of the attempt.
 */
public class ResourceResponse {
    /** The request for which this is the response. */
    public final ResourceRequest request;
    private URI uri;
    private URI resolvedURI;
    private boolean rejected = false;
    private boolean resolved = false;
    private final Map<String, List<String>> headers = new HashMap<>();
    private InputStream stream = null;
    private String contentType = null;
    private String encoding = null;
    private int statusCode = -1;
    private ResourceConnection connection = null;

    /**
     * The simplest constructor for an unsuccessful request.
     * @param request The request.
     */
    public ResourceResponse(ResourceRequest request) {
        this(request, false);
    }

    /**
     * This constructor is also for an unsuccessful request.
     * <p>This constructor can explicitly indicate that the request was rejected. For example,
     * if the {@link ResolverFeature#ACCESS_EXTERNAL_ENTITY} or {@link ResolverFeature#ACCESS_EXTERNAL_DOCUMENT}
     * settings did not allow requests for a particular URI scheme.</p>
     * @param request The request.
     * @param rejected Was this request rejected?
     */
    public ResourceResponse(ResourceRequest request, boolean rejected) {
        this.request = request;
        this.rejected = rejected;
        this.uri = null;
        this.resolvedURI = null;
        this.resolved = false;
    }

    /**
     * The constructor for a successful request (usually).
     * <p>The {@code request} was satisfied with the {@code uri}. If the {@code uri} is
     * null, the response will indicate that the request was unsuccessful.</p>
     * @param request The request.
     * @param uri The successfully resolved URI, or null.
     */
    public ResourceResponse(ResourceRequest request, URI uri) {
        // If the URI is null, make the result the same as a constructor that didn't pass a URI.
        this.request = request;
        this.rejected = false;
        this.uri = uri;
        this.resolvedURI = uri;
        this.resolved = false;

        if (uri != null) {
            if (("jar".equals(uri.getScheme()) || "classpath".equals(uri.getScheme()))
                    && request.config.getFeature(ResolverFeature.MASK_JAR_URIS)) {
                try {
                    this.uri = request.getAbsoluteURI();
                } catch (URISyntaxException ex) {
                    // nop
                }
            }

            this.resolved = true;
        }
    }

    /**
     * Set the resource connection.
     * <p>If an attempt is made to open a connection to the resource, that connection can be saved in the
     * response.</p>
     * @param conn The resource connection.
     */
    /* package */ void setConnection(ResourceConnection conn) {
        connection = conn;
    }

    /**
     * Get the resource connection.
     * <p>If this response was accessed with a resource connection, it will be saved in the response.</p>
     * @return The connection.
     */
    public ResourceConnection getConnection() {
        return connection;
    }

    /* package */ void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Get the content type.
     * @return The content type, or null if it's unknown.
     */
    public String getContentType() {
        return contentType;
    }

    /* package */ void setInputStream(InputStream stream) {
        this.stream = stream;
    }

    /**
     * Get the input stream.
     * <p>If this resource is opened for reading by the resolution attempt, this is its readable stream.</p>
     * <p>Attempts to resolve a resource will provide a stream if one is available. Requests that simply
     * inspect the catalog (with the lookup* methods) will not.</p>
     * @return The stream, or null if the resource wasn't opened.
     */
    public InputStream getInputStream() {
        return stream;
    }

    /* package */ void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    /**
     * Was this request rejected?
     * @return True if the request was rejected, false otherwise.
     */
    public boolean isRejected() {
        return rejected;
    }

    /* package */ void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    /**
     * Was this request successfully resolved?
     * @return True if the request was resolved, false otherwise.
     */
    public boolean isResolved() {
        return resolved;
    }

    /* package */ void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * Get the URI.
     * <p>This is the initially resolved URI. The distinction between initial and final resolution arises
     * when redirects come into play. Suppose you request {@code http://example.com/some.dtd} and that resolves
     * in the catalog to {@code http://example.com/version2/some.dtd}. The URI returned by {@code getURI()} will
     * be {@code http://example.com/version2/some.dtd}. However, if an attempt to obtain that resource encounters
     * redirection at, for example, the HTTP layer, then the actual URI returned may differ, see {@link #getResolvedURI()}.</p>
     * @return The URI.
     */
    public URI getURI() {
        return uri;
    }

    /* package */ void setResolvedURI(URI uri) {
        resolvedURI = uri;
    }

    /**
     * Get the resolved URI.
     * <p>This is the finally resolved URI. The distinction between initial and final resolution arises
     * when redirects come into play. Suppose you request {@code http://example.com/some.dtd} and that resolves
     * in the catalog to {@code http://example.com/version2/some.dtd}. If an attempt to obtain that resource encounters
     * redirection at, for example, the HTTP layer, to {@code http://cdn.example.com/version2/some.dtd}, the
     * URI returned by {@link #getResolvedURI()} will be {@code http://cdn.example.com/version2/some.dtd}.
     * See {@link #getURI()}.</p>
     * <p>If {@link ResolverFeature#MASK_JAR_URIS} is true and the resolved URI is a jar: URI, the
     * {@link #getURI()} is returned instead.</p>
     * @return The resolved URI.
     */
    public URI getResolvedURI() {
        if ((resolvedURI.getScheme().equals("jar") || resolvedURI.getScheme().equals("classpath"))
                && request.config.getFeature(ResolverFeature.MASK_JAR_URIS)) {
            return uri;
        }
        return resolvedURI;
    }

    /**
     * Get the resolved URI, irrespective of masking.
     * <p>Where {@link #getResolvedURI()} will not return a {@code jar:} URI if the
     * {@link ResolverFeature#MASK_JAR_URIS} is true, this method always returns the actual, unmasked URI.
     * </p>
     * @return The resolved URI, unmasked.
     */
    public URI getUnmaskedURI() {
        return resolvedURI;
    }

    /* package */ void setEncoding(String encoding) {
        this.encoding = encoding;
    }


    /**
     * Get the encoding.
     * @return The resource encoding, or null if the encoding is unknown.
     */
    public String getEncoding() {
        return encoding;
    }

    /* package */ void setHeaders(Map<String,List<String>> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    /**
     * Get the headers.
     * <p>This method never returns null, it returns an empty map if no headers are available.</p>
     * @return The headers, if headers are available.
     */
    public Map<String,List<String>> getHeaders() {
        return headers;
    }

    /**
     * Get the value of a specific header.
     * <p>This is a convenience method. Headers can be repeated, and consequently have multiple values.
     * This method only returns the first value; that is, the value associated with the first header named {@code name}
     * encountered during resolution.</p>
     * @param name The header name.
     * @return The (first) value of that header, or null if no such header exists
     * @throws NullPointerException if name is null.
     */
    public String getHeader(String name) {
        // URLConnection stores the "HTTP/1.0 200 OK" status return line in the headers
        // with a null key value. ¯\_(ツ)_/¯
        if (name != null) {
            name = name.toLowerCase();
        }
        for (String key : headers.keySet()) {
            if ((name == null && key == null) || (name != null && name.equals(key.toLowerCase()))) {
                List<String> value = headers.get(key);
                if (value == null || value.isEmpty()) {
                    return null;
                }
                return value.get(0);
            }
        }
        return null;
    }

    /* package */ void setStatusCode(int code) {
        statusCode = code;
    }

    /**
     * Get the status code of the request.
     * <p>Successful requests return 200 irrespective of the URI scheme.</p>
     * @return the status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

}
