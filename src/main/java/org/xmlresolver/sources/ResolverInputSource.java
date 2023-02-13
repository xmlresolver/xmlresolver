package org.xmlresolver.sources;

import org.xml.sax.InputSource;
import org.xmlresolver.ResolvedResource;
import org.xmlresolver.utils.RsrcUtils;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** A {@link InputSource} with a <code>resolvedURI</code>.
 *
 */
public class ResolverInputSource extends InputSource implements ResolverResourceInfo {
    /** The underlying, resolved URI. */
    public final URI resolvedURI;
    private final int statusCode;
    private final Map<String,List<String>> resolvedHeaders;

    /** Construct the {@link org.xml.sax.InputSource} while preserving the local URI.
     *
     * @param localURI The local URI.
     * @param stream The stream to return for this source.
     * */
    public ResolverInputSource(URI localURI, InputStream stream) {
        super(stream);
        this.resolvedURI = localURI;
        this.statusCode = 200;
        this.resolvedHeaders = Collections.emptyMap();
    }

    /** Construct the @link org.xml.sax.InputSource} directly from a ResolvedResource
     *
     * @param rsrc the resolved resource
     * */
    public ResolverInputSource(ResolvedResource rsrc) {
        super(rsrc.getInputStream());
        resolvedURI = rsrc.getLocalURI();
        statusCode = rsrc.getStatusCode();
        resolvedHeaders = rsrc.getHeaders();
    }

    public URI getResolvedURI() {
        return resolvedURI;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, List<String>> getHeaders() {
        return resolvedHeaders;
    }

    public String getHeader(String headerName) {
        return RsrcUtils.getHeader(headerName, resolvedHeaders);
    }
}
