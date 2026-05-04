package org.xmlresolver.spi;

import org.xmlresolver.ResourceRequest;
import org.xmlresolver.ResourceResponse;

import java.io.IOException;
import java.net.URI;

/**
 * A scheme-specific resource resolver.
 */
public interface SchemeResolver {
    /**
     * Get the requested resource.
     * <p>A {@code SchemaResolver} is registered for a particular URI scheme. The {@code getResource} method
     * will only ever be called on a particular {@code SchemaResolver} if the {@code uri} has that scheme.</p>
     * <p>The resolver should use whatever mechanisms are appropriate to resolve the URI. Typically
     * something like this:</p>
     * <pre>
     *     ResourceConnection conn = someSchemeAppropriateConnection(uri)
     *     ResourceResponse response = new ResourceResponse(request, uri);
     *     response.setConnection(conn);
     *     return response
     * </pre>
     * <p>The method should return {@code null} if it cannot resolve the resource. (More than one resolver
     * may be assigned for a given scheme, they will each be attempted in turn until one succeeds, or resolution
     * completely fails.)</p>
     * @param request The resource request.
     * @param uri The URI to resolve.
     * @return The resource response.
     * @throws IOException If an I/O error occurs resolving the resource.
     */
    ResourceResponse getResource(ResourceRequest request, URI uri) throws IOException;
}
