package org.xmlresolver;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@code ResourceResponse} is the return type for either an attempt to look up a resource
 * in the catalog or to resolve a resource. It encapsulates the results of the attempt.
 */
public class ResourceResponseImpl implements ResourceResponse {
    private final ResourceRequest request;
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

    @Override
    public ResourceRequest getRequest() {
        return request;
    }

    /**
     * The simplest constructor for an unsuccessful request.
     * @param request The request.
     */
    public ResourceResponseImpl(ResourceRequest request) {
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
    public ResourceResponseImpl(ResourceRequest request, boolean rejected) {
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
    public ResourceResponseImpl(ResourceRequest request, URI uri) {
        // If the URI is null, make the result the same as a constructor that didn't pass a URI.
        this.request = request;
        this.rejected = false;
        this.uri = uri;
        this.resolvedURI = uri;
        this.resolved = false;

        if (uri != null) {
            if (("jar".equals(uri.getScheme()) || "classpath".equals(uri.getScheme()))
                    && request.getConfiguration().getFeature(ResolverFeature.MASK_JAR_URIS)) {
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
    public void setConnection(ResourceConnection conn) {
        connection = conn;
    }

    @Override
    public ResourceConnection getConnection() {
        return connection;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setInputStream(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public InputStream getInputStream() {
        return stream;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    @Override
    public boolean isRejected() {
        return rejected;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    @Override
    public boolean isResolved() {
        return resolved;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    public void setResolvedURI(URI uri) {
        resolvedURI = uri;
    }

    @Override
    public URI getResolvedURI() {
        if (resolvedURI != null) {
            if ((resolvedURI.getScheme().equals("jar") || resolvedURI.getScheme().equals("classpath"))
                    && request.getConfiguration().getFeature(ResolverFeature.MASK_JAR_URIS)) {
                return uri;
            }
        }
        return resolvedURI;
    }

    @Override
    public URI getUnmaskedURI() {
        return resolvedURI;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    public void setHeaders(Map<String,List<String>> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    @Override
    public Map<String,List<String>> getHeaders() {
        return headers;
    }

    @Override
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

    public void setStatusCode(int code) {
        statusCode = code;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
}
