package org.xmlresolver;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A resource request.
 * <p>A resource request identifies what should be requested, either looked up in the catalog or looked up
 * in the catalog and then retrieved.</p>
 */
public interface ResourceRequest {
    /**
     * Accessor for the underlying configuration.
     * @return the configuration
     */
    ResolverConfiguration getConfiguration();

    /**
     * Get the always resolve setting.
     * <p>If always resolve is true, then the original URI will be retrieved if the lookup
     * fails to find a local resource. It's initialized to the value of the
     * {@link org.xmlresolver.ResolverFeature#ALWAYS_RESOLVE} feature.</p>
     * @return true if always resolve is enabled
     */
    boolean isAlwaysResolve();

    /**
     * Set the always resolve setting.
     * <p>If this flag is true, then the original URI will be retrieved if the lookup
     * fails to find a local resource.</p>
     * @param always sets always resolve
     */
    void setAlwaysResolve(boolean always);

    /**
     * The RDDL nature of the request.
     * @return the nature
     */
    String getNature();

    /**
     * The RDDL purpose of the request.
     * @return the purpose
     */
    String getPurpose();

    /**
     * Get the request URI.
     * See {@link #setURI(String)}.
     * @return the request URI.
     */
    String getURI();

    /**
     * Sets the URI (and system identifier) of the request.
     * <p>The request has only a single URI. For convenience, it can be retrieved with either
     * the {@link #getURI()} or {@link #getSystemId()} methods.</p>
     * @param uri the request URI
     */
    void setURI(String uri);

    /**
     * Sets the URI (and system identifier) of the request.
     * <p>The request has only a single URI. For convenience, it can be retrieved with either
     * the {@link #getURI()} or {@link #getSystemId()} methods.</p>
     * @param uri the request URI
     */
    void setURI(URI uri);

    /**
     * Get the base URI.
     * @return the base URI
     */
    String getBaseURI();

    /**
     * Set the base URI.
     * <p>The base URI should be an absolute URI.</p>
     * @param baseURI the request base URI
     */
    void setBaseURI(String baseURI);

    /**
     * Set the base URI.
     * <p>The base URI should be an absolute URI.</p>
     * @param baseURI the request base URI
     */
    void setBaseURI(URI baseURI);

    /**
     * Get the absolute URI.
     * <p>This method combines the URI and base URI, returning an absolute URI.</p>
     * <p>If the request has a base URI, and that URI is absolute, the URI returned will
     * be the request URI made absolute with respect to the base URI.</p>
     * <p>If the request doesn't have a base URI (or if the base URI isn't absolute),
     * the request URI will be returned if it's an absolute URI. Otherwise, {@code null} is returned.</p>
     * @return the absolute URI
     * @throws URISyntaxException if the URI or base URI are syntactically invalid
     */
    URI getAbsoluteURI() throws URISyntaxException;

    /**
     * Get the entity name
     * @return the request entity name
     */
    String getEntityName();

    /**
     * Set the entity name.
     * <p>The entity name is only relevant for some kinds of requests.</p>
     * @param name the entity name
     */
    void setEntityName(String name);

    /**
     * Get the system identifier of the request.
     * <p>This method returns the request URI, it's synonymous with {@link #getURI()}.</p>
     * @return the request URI
     */
    String getSystemId();

    /**
     * Get the public identifier
     * @return the request public identifier
     */
    String getPublicId();

    /**
     * Set the request public identifier.
     * <p>Public identifiers only apply to requests to resolve system identifiers.</p>
     * @param publicId the public identifier
     */
    void setPublicId(String publicId);

    /**
     * Get the requested encoding.
     * @return the request encoding
     */
    String getEncoding();

    /**
     * Set the requested encoding.
     * <p>Not all protocols or systems provide the ability to make the encoding part of the request,
     * but on those that do, this encoding will be used.</p>
     * @param encoding the request encoding
     */
    void setEncoding(String encoding);

    /**
     * Are we expecting to resolve this request to obtain an entity?
     * <p>There's a (not often especially useful) distinction maintained in the catalog between
     * URI resolution and system identifier resolution. If the request has a DTD or entity nature,
     * an entity name, or a public identifier, it's assumed to be resolving an entity. Otherwise,
     * it's resolving a URI.</p>
     * @return True if the request will be resolved as an entity.
     */
    boolean isResolvingAsEntity();

    /**
     * Specify whether resolution should be for entities or URIs.
     * See {@link #isResolvingAsEntity()}.
     * @param asEntity Resolve the request as an entity?
     */
    void setResolvingAsEntity(boolean asEntity);

    /**
     * Will a readable stream be returned?
     * @return the open stream setting
     */
    boolean isOpenStream();

    /**
     * Return a readable stream?
     * <p>If open stream is true, a resource response will include a stream that can be used
     * to read the response. If you don't need the stream, you can set this flag to false to
     * prevent it from being returned.</p>
     * <p>Setting this flag to false <em>does not</em> guarantee that no requests will
     * be issued. For example, the resolver may follow redirects to find the final URI, even if
     * it doesn't return a stream.</p>
     * @param open true if the open stream should be returned
     */
    void setOpenStream(boolean open);
}
