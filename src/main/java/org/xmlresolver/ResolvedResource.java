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

    public ResolvedResource(ResourceResponse response) {
        this.response = response;
    }

    public URI getResolvedURI() {
        return response.getResolvedURI();
    }

    public URI getLocalURI() {
        return response.getUnmaskedURI();
    }

    public InputStream getInputStream() {
        return response.getInputStream();
    }

    public String getContentType() {
        return response.getContentType();
    }

    public int getStatusCode() {
        return response.getStatusCode();
    }

    public Map<String, List<String>> getHeaders() {
        return response.getHeaders();
    }
}
