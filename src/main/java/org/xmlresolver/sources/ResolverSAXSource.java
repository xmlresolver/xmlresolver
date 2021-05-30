package org.xmlresolver.sources;

import org.xml.sax.InputSource;

import javax.xml.transform.sax.SAXSource;
import java.net.URI;

public class ResolverSAXSource extends SAXSource {
    public final URI resolvedURI;

    public ResolverSAXSource(URI localURI, InputSource source) {
        super(source);
        resolvedURI = localURI;
    }
}
