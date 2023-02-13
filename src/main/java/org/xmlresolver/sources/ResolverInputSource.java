package org.xmlresolver.sources;

import org.xml.sax.InputSource;
import org.xmlresolver.ResolvedResource;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** A {@link InputSource} with a <code>resolvedURI</code>.
 *
 */
public class ResolverInputSource extends InputSource {
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

    /** Returns the status code associated with the request.
     *
     * <p>If the response included a status code, that value will be returned. For protocols
     * that don't have a status code (such as file:), 200 is returned for convenience.</p>
     */
    public int getStatusCode() {
        return statusCode;
    }

    /** Return the headers associated with this resource.
     *
     * <p>Returns the headers, if any, associated with this resource. For example,
     * an HTTP resource might include the headers returned by the server.</p>
     *
     * @return the headers
     */
    public Map<String, List<String>> getHeaders() {
        return resolvedHeaders;
    }

    /** Get the value of a header field.
     *
     * <p>Returns the first value of a header witht he specified name. This is a convenience
     * method because header names have to be compared without case sensitivity. If the header
     * has more than one value, only the first is returned.
     *
     * @param headerName the name of the header whose value should be returned.
     * @return the (first value) of the named header.
     */
    public String getHeader(String headerName) {
        if (resolvedHeaders == null) {
            return null;
        }

        for (String name : resolvedHeaders.keySet()) {
            if (name.equalsIgnoreCase(headerName)) {
                return resolvedHeaders.get(name).get(0);
            }
        }

        return null;
    }
}
