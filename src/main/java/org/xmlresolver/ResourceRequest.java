package org.xmlresolver;

import org.xmlresolver.utils.URIUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A resource request.
 * <p>A resource request identifies what should be requested, either looked up in the catalog or looked up
 * in the catalog and then retrieved.</p>
 */
public class ResourceRequest {
    /** The resolver configuration. */
    public final ResolverConfiguration config;
    /** The RDDL nature of the request. */
    public final String nature;
    /** The RDDL purpose of the request. */
    public final String purpose;
    private String uri = null;
    private String baseURI = null;
    private String entityName = null;
    private String publicId = null;
    private String encoding = null;
    private boolean openStream = true;
    private boolean resolveAsEntity = false;

    /**
     * ResourceRequest constructor.
     * <p>This constructor returns a request that accepts any RDDL nature or purpose.
     * It has no other features; setters must be used to provide at least one feature.</p>
     * @param config The resolver configuration.
     */
    public ResourceRequest(ResolverConfiguration config) {
        this(config, ResolverConstants.ANY_NATURE, ResolverConstants.ANY_PURPOSE);
    }

    /**
     * ResourceRequest constructor.
     * <p>This constructor returns a request that accepts the requested RDDL nature or purpose.
     * It has no other features; setters must be used to provide at least one feature.</p>
     * @param config The resolver configuration.
     * @param nature The RDDL nature.
     * @param purpose The RDDL purpose.
     */
    public ResourceRequest(ResolverConfiguration config, String nature, String purpose) {
        this.config = config;
        this.nature = nature;
        this.purpose = purpose;

        resolveAsEntity = ResolverConstants.EXTERNAL_ENTITY_NATURE.equals(nature)
                || ResolverConstants.DTD_NATURE.equals(nature);
    }

    /**
     * Sets the URI (and system identifier) of the request.
     * <p>The request has only a single URI. For convenience, it can be retrieved with either
     * the {@link #getURI()} or {@link #getSystemId()} methods.</p>
     * @param uri the request URI
     */
    public void setURI(String uri) {
        if (uri != null) {
            if (URIUtils.isWindows() && config.getFeature(ResolverFeature.FIX_WINDOWS_SYSTEM_IDENTIFIERS)) {
                this.uri = URIUtils.windowsPathURI(uri);
            } else {
                this.uri = uri;
            }
        }
    }

    /**
     * Sets the URI (and system identifier) of the request.
     * <p>The request has only a single URI. For convenience, it can be retrieved with either
     * the {@link #getURI()} or {@link #getSystemId()} methods.</p>
     * @param uri the request URI
     */
    public void setURI(URI uri) {
        this.uri = uri.toString();
    }

    /**
     * Set the base URI.
     * <p>The base URI should be an absolute URI.</p>
     * @param baseURI the request base URI
     */
    public void setBaseURI(String baseURI) {
        if (baseURI != null) {
            if (URIUtils.isWindows() && config.getFeature(ResolverFeature.FIX_WINDOWS_SYSTEM_IDENTIFIERS)) {
                this.baseURI = URIUtils.windowsPathURI(baseURI);
            } else {
                this.baseURI = baseURI;
            }
        }
    }

    /**
     * Set the base URI.
     * <p>The base URI should be an absolute URI.</p>
     * @param baseURI the request base URI
     */
    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI.toString();
    }

    /**
     * Set the entity name.
     * <p>The entity name is only relevant for some kinds of requests.</p>
     * @param name the entity name
     */
    public void setEntityName(String name) {
        this.entityName = name;
        resolveAsEntity = true;
    }

    /**
     * Get the request URI.
     * See {@link #setURI(String)}.
     * @return the request URI.
     */
    public String getURI() {
        return uri;
    }

    /**
     * Get the base URI.
     * @return the base URI
     */
    public String getBaseURI() {
        return baseURI;
    }

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
    public URI getAbsoluteURI() throws URISyntaxException {
        if (baseURI != null) {
            URI abs = new URI(baseURI);
            if (abs.isAbsolute()) {
                if (uri == null || uri.isEmpty()) {
                    return abs;
                }

                return URIUtils.resolve(abs, uri);
            }
        }

        if (uri != null) {
            URI abs = new URI(uri);
            if (abs.isAbsolute()) {
                return abs;
            }
        }

        return null;
    }

    /**
     * Get the system identifier of the request.
     * <p>This method returns the request URI, it's synonymous with {@link #getURI()}.</p>
     * @return the request URI
     */
    public String getSystemId() {
        return uri;
    }

    /**
     * Set the request public identifier.
     * <p>Public identifiers only apply to requests to resolve system identifiers.</p>
     * @param publicId the public identifier
     */
    public void setPublicId(String publicId) {
        this.publicId = publicId;
        resolveAsEntity = true;
    }

    /**
     * Get the public identifier
     * @return the request public identifier
     */
    public String getPublicId() {
        return publicId;
    }

    /**
     * Get the entity name
     * @return the request entity name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Set the requested encoding.
     * <p>Not all protocols or systems provide the ability to make the encoding part of the request,
     * but on those that do, this encoding will be used.</p>
     * @param encoding the request encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Get the requested encoding.
     * @return the request encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Are we expecting to resolve this request to obtain an entity?
     * <p>There's a (not often especially useful) distinction maintained in the catalog between
     * URI resolution and system identifier resolution. If the request has a DTD or entity nature,
     * an entity name, or a public identifier, it's assumed to be resolving an entity. Otherwise,
     * it's resolving a URI.</p>
     * @return True if the request will be resolved as an entity.
     */
    public boolean isResolvingAsEntity() {
        return resolveAsEntity;
    }

    /**
     * Specify whether resolution should be for entities or URIs.
     * See {@link #isResolvingAsEntity()}.
     * @param asEntity Resolve the request as an entity?
     */
    public void setResolvingAsEntity(boolean asEntity) {
        resolveAsEntity = asEntity;
    }

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
    public void setOpenStream(boolean open) {
        openStream = open;
    }

    /**
     * Will a readable stream be returned?
     * @return the open stream setting
     */
    public boolean openStream() {
        return openStream;
    }

    @Override
    public String toString() {
        String str = entityName == null ? "" : entityName + ": ";
        str += uri;
        if (baseURI != null) {
            str += " (" +baseURI + ")";
        }
        return str;
    }
}
