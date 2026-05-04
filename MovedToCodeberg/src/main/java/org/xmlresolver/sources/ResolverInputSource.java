package org.xmlresolver.sources;

import org.xml.sax.InputSource;
import org.xmlresolver.ResourceResponse;
import org.xmlresolver.utils.RsrcUtils;

import java.net.URI;
import java.util.List;
import java.util.Map;

/** A {@link InputSource} with a <code>resolvedURI</code>.
 *
 */
public class ResolverInputSource extends InputSource implements ResolverResourceInfo {
    private final ResourceResponse response;
    private final URI resolvedURI;
    private final int statusCode;
    private final Map<String,List<String>> resolvedHeaders;

    /** Construct the @link org.xml.sax.InputSource} directly from a ResolvedResource
     *
     * @param rsrc the resolved resource
     * */
    public ResolverInputSource(ResourceResponse rsrc) {
        super(rsrc.getInputStream());
        if (rsrc.getURI() != null) {
            setSystemId(rsrc.getURI().toString());
        }
        setPublicId(rsrc.getRequest().getPublicId());
        this.response = rsrc;
        resolvedURI = rsrc.getResolvedURI();
        statusCode = rsrc.getStatusCode();
        resolvedHeaders = rsrc.getHeaders();
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
