package org.xmlresolver.sources;

import java.net.URI;
import java.util.List;
import java.util.Map;

/** Information about a resolved resource.
 *
 * <p>This interface exposes information about the resolved resource. For some resource types, only the
 * resolved URI is available. For others, for example, when HTTP URIs are retrieved, status code
 * and headers may also be available.</p>
 */
public interface ResolverResourceInfo {
    /** Returns the resolved URI associated with the request.
     *
     * @return the resolved URI.
     */
    URI getResolvedURI();

    /** Returns the status code associated with the request.
     *
     * <p>If the response included a status code, that value will be returned. For protocols
     * that don't have a status code (such as file:), 200 is returned for convenience.</p>
     *
     * @return the status code
     */
    int getStatusCode();

    /** Return the headers associated with this resource.
     *
     * <p>Returns the headers, if any, associated with this resource. For example,
     * an HTTP resource might include the headers returned by the server.</p>
     *
     * @return the headers
     */
    Map<String, List<String>> getHeaders();

    /** Get the value of a header field.
     *
     * <p>Returns the first value of a header witht he specified name. This is a convenience
     * method because header names have to be compared without case sensitivity. If the header
     * has more than one value, only the first is returned.
     *
     * @param headerName the name of the header whose value should be returned.
     * @return the (first value) of the named header.
     */
    String getHeader(String headerName);
}
