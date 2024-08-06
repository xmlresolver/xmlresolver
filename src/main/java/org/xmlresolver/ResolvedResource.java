package org.xmlresolver;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * This class is a shim that provides a degree of compatibility with XML Resolver 5.0.
 * @deprecated 6.0.0
 */
public class ResolvedResource {
    private final ResourceResponse response;

    /**
     * Construct a resolved resource for the specified response.
     * @param response the response.
     */
    public ResolvedResource(ResourceResponse response) {
        this.response = response;
    }

    /**
     * Get the resolved URI.
     * @return the resolved URI.
     */
    public URI getResolvedURI() {
        return response.getResolvedURI();
    }

    /**
     * Get the local URI.
     * <p>The local URI may differ from the resolved URI if catalog resolution provided a local copy.</p>
     * @return the local URI.
     */
    public URI getLocalURI() {
        return response.getUnmaskedURI();
    }

    /**
     * Get the input stream for the resource.
     * @return the input stream, or {@code null} if no stream was opened.
     */
    public InputStream getInputStream() {
        return response.getInputStream();
    }

    /**
     * Get the content type.
     * @return the content type, or {@code null} if no content type is available.
     */
    public String getContentType() {
        return response.getContentType();
    }

    /**
     * Get the return code.
     * @return the return code.
     */
    public int getStatusCode() {
        return response.getStatusCode();
    }

    /**
     * Get the headers.
     * @return A list of headers.
     */
    public Map<String, List<String>> getHeaders() {
        return response.getHeaders();
    }
}
