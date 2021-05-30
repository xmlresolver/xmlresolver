package org.xmlresolver.sources;

import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.URI;

public class ResolverInputSource extends InputSource {
    public final URI resolvedURI;

    public ResolverInputSource(URI localURI, InputStream stream) {
        super(stream);
        resolvedURI = localURI;
    }
}
