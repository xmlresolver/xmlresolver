package org.xmlresolver;

import java.io.InputStream;
import java.net.URI;

public class ResolvedResourceImpl extends ResolvedResource {
    private final URI resolvedURI;
    private final URI localURI;
    private final InputStream inputStream;
    private final String contentType;

    public ResolvedResourceImpl(URI resolvedURI, URI localURI, InputStream stream, String contentType) {
        this.resolvedURI = resolvedURI;
        this.localURI = localURI;
        this.inputStream = stream;
        this.contentType = contentType;
    }

    @Override
    public URI getResolvedURI() {
        return resolvedURI;
    }

    @Override
    public URI getLocalURI() {
        return localURI;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String getContentType() {
        return contentType;
    }
}
