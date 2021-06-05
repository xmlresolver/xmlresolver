package org.xmlresolver.sources;

import org.xml.sax.InputSource;

import javax.xml.transform.sax.SAXSource;
import java.net.URI;

/** A {@link javax.xml.transform.sax.SAXSource} with a <code>resolvedURI</code>. */
public class ResolverSAXSource extends SAXSource {
    /** The underlying, resolved URI. */
    public final URI resolvedURI;

    /** Construct a {@link javax.xml.transform.sax.SAXSource} while preserving the local URI. */
    public ResolverSAXSource(URI localURI, InputSource source) {
        super(source);
        resolvedURI = localURI;
    }
}
