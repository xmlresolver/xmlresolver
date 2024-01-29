package org.xmlresolver.sources;

import org.xml.sax.InputSource;
import org.xmlresolver.ResourceResponse;
import org.xmlresolver.utils.RsrcUtils;

import javax.xml.transform.sax.SAXSource;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** A {@link javax.xml.transform.sax.SAXSource} with a <code>resolvedURI</code>. */
public class ResolverSAXSource extends SAXSource implements ResolverResourceInfo {
    /** The underlying, resolved URI. */
    public final URI resolvedURI;
    private final int statusCode;
    private final Map<String, List<String>> resolvedHeaders;

    /** Construct a {@link javax.xml.transform.sax.SAXSource} while preserving the local URI.
     *
     * @param localURI The local URI.
     * @param source The input source to return for this source.
     * */
    public ResolverSAXSource(URI localURI, InputSource source) {
        super(source);
        resolvedURI = localURI;
        statusCode = 200;
        resolvedHeaders = Collections.emptyMap();
    }

    public ResolverSAXSource(ResourceResponse resp) {
        super(new InputSource(resp.getInputStream()));
        resolvedURI = resp.getResolvedURI();
        statusCode = resp.getStatusCode();
        resolvedHeaders = resp.getHeaders();
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
