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
    private final ResourceResponse response;
    private final URI resolvedURI;
    private final int statusCode;
    private final Map<String, List<String>> resolvedHeaders;

    /**
     * Create an extended {@code SAXSource}.
     * @param resp the response.
     */
    public ResolverSAXSource(ResourceResponse resp) {
        super(new InputSource(resp.getInputStream()));
        this.response = resp;
        resolvedURI = resp.getResolvedURI();
        statusCode = resp.getStatusCode();
        resolvedHeaders = resp.getHeaders();
        setSystemId(resp.getResolvedURI().toString());
    }

    @Override
    public ResourceResponse getResponse() {
        return response;
    }

    @Override
    public URI getResolvedURI() {
        return resolvedURI;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return resolvedHeaders;
    }

    @Override
    public String getHeader(String headerName) {
        return RsrcUtils.getHeader(headerName, resolvedHeaders);
    }
}
