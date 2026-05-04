package org.xmlresolver;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * A {@code ResourceResponse} is the return type for either an attempt to lookup a resource
 * in the catalog or to resolve a resource. It encapsulates the results of the attempt.
 */
public interface ResourceResponse {
    /**
     * The request for which this is the response.
     * @return the request
     */
    ResourceRequest getRequest();

    /**
     * Get the resource connection.
     * <p>If this response was accessed with a resource connection, it will be saved in the response.</p>
     * @return The connection.
     */
    ResourceConnection getConnection();

    /**
     * Get the content type.
     * @return The content type, or null if it's unknown.
     */
    String getContentType();

    /**
     * Get the input stream.
     * <p>If this resource is opened for reading by the resolution attempt, this is its readable stream.</p>
     * <p>Attempts to resolve a resource will provide a stream if one is available. Requests that simply
     * inspect the catalog (with the lookup* methods) will not.</p>
     * @return The stream, or null if the resource wasn't opened.
     */
    InputStream getInputStream();

    /**
     * Was this request rejected?
     * @return True if the request was rejected, false otherwise.
     */
    boolean isRejected();

    /**
     * Was this request successfully resolved?
     * @return True if the request was resolved, false otherwise.
     */
    boolean isResolved();

    /**
     * Get the URI.
     * <p>This is the initially resolved URI. The distinction between initial and final resolution arises
     * when redirects come into play. Suppose you request {@code http://example.com/some.dtd} and that resolves
     * in the catalog to {@code http://example.com/version2/some.dtd}. The URI returned by {@code getURI()} will
     * be {@code http://example.com/version2/some.dtd}. However, if an attempt to obtain that resource encounters
     * redirection at, for example, the HTTP layer, then the actual URI returned may differ, see {@link #getResolvedURI()}.</p>
     * @return The URI.
     */
    URI getURI();

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
    URI getResolvedURI();

    /**
     * Get the resolved URI, irrespective of masking.
     * <p>Where {@link #getResolvedURI()} will not return a {@code jar:} URI if the
     * {@link ResolverFeature#MASK_JAR_URIS} is true, this method always returns the actual, unmasked URI.
     * </p>
     * @return The resolved URI, unmasked.
     */
    URI getUnmaskedURI();

    /**
     * Get the encoding.
     * @return The resource encoding, or null if the encoding is unknown.
     */
    String getEncoding();

    /**
     * Get the headers.
     * <p>This method never returns null, it returns an empty map if no headers are available.</p>
     * @return The headers, if headers are available.
     */
    Map<String,List<String>> getHeaders();

    /**
     * Get the value of a specific header.
     * <p>This is a convenience method. Headers can be repeated, and consequently have multiple values.
     * This method only returns the first value; that is, the value associated with the first header named {@code name}
     * encountered during resolution.</p>
     * @param name The header name.
     * @return The (first) value of that header, or null if no such header exists
     * @throws NullPointerException if name is null.
     */
    String getHeader(String name);

    /**
     * Get the status code of the request.
     * <p>Successful requests return 200 irrespective of the URI scheme.</p>
     * @return the status code.
     */
    int getStatusCode();
}
