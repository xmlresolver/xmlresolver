package org.xmlresolver.sources;

import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.URI;

/** A {@link InputSource} with a <code>resolvedURI</code>.
 *
 */
public class ResolverInputSource extends InputSource {
    /** The underlying, resolved URI. */
    public final URI resolvedURI;

    /** Construct the {@link org.xml.sax.InputSource} while preserving the local URI. */
    public ResolverInputSource(URI localURI, InputStream stream) {
        super(stream);
        resolvedURI = localURI;
    }
}
