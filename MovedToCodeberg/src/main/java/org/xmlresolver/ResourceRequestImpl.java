package org.xmlresolver;

import org.xmlresolver.utils.URIUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A resource request implementation.
 * <p>A resource request identifies what should be requested, either looked up in the catalog or looked up
 * in the catalog and then retrieved.</p>
 */
public class ResourceRequestImpl implements ResourceRequest {
    private final ResolverConfiguration config;
    /** The RDDL nature of the request. */
    private final String nature;
    /** The RDDL purpose of the request. */
    private final String purpose;
    private String uri = null;
    private String baseURI = null;
    private String entityName = null;
    private String publicId = null;
    private String encoding = null;
    private boolean openStream = true;
    private boolean resolveAsEntity = false;
    private boolean alwaysResolve = false;

    /**
     * ResourceRequest constructor.
     * <p>This constructor returns a request that accepts any RDDL nature or purpose.
     * It has no other features; setters must be used to provide at least one feature.</p>
     * @param config The resolver configuration.
     */
    public ResourceRequestImpl(ResolverConfiguration config) {
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
    public ResourceRequestImpl(ResolverConfiguration config, String nature, String purpose) {
        this.config = config;
        this.nature = nature;
        this.purpose = purpose;

        resolveAsEntity = ResolverConstants.EXTERNAL_ENTITY_NATURE.equals(nature)
                || ResolverConstants.DTD_NATURE.equals(nature);
        alwaysResolve = config.getFeature(ResolverFeature.ALWAYS_RESOLVE);
    }

    @Override
    public ResolverConfiguration getConfiguration() {
        return config;
    }

    @Override
    public void setAlwaysResolve(boolean always) {
        alwaysResolve = always;
    }

    @Override
    public boolean isAlwaysResolve() {
        return alwaysResolve;
    }

    @Override
    public String getNature() {
        return nature;
    }

    @Override
    public String getPurpose() {
        return purpose;
    }

    @Override
    public void setURI(String uri) {
        if (uri != null) {
            if (config.getFeature(ResolverFeature.FIX_WINDOWS_SYSTEM_IDENTIFIERS)) {
                this.uri = URIUtils.windowsPathURI(uri);
            } else {
                this.uri = uri;
            }
        }
    }

    @Override
    public void setURI(URI uri) {
        this.uri = uri.toString();
    }

    @Override
    public void setBaseURI(String baseURI) {
        if (baseURI != null) {
            if (config.getFeature(ResolverFeature.FIX_WINDOWS_SYSTEM_IDENTIFIERS)) {
                this.baseURI = URIUtils.windowsPathURI(baseURI);
            } else {
                this.baseURI = baseURI;
            }
        }
    }

    @Override
    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI.toString();
    }

    @Override
    public void setEntityName(String name) {
        this.entityName = name;
        resolveAsEntity = true;
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public String getBaseURI() {
        return baseURI;
    }

    @Override
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

    @Override
    public String getSystemId() {
        return uri;
    }

    @Override
    public void setPublicId(String publicId) {
        this.publicId = publicId;
        resolveAsEntity = true;
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public String getEntityName() {
        return entityName;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public boolean isResolvingAsEntity() {
        return resolveAsEntity;
    }

    @Override
    public void setResolvingAsEntity(boolean asEntity) {
        resolveAsEntity = asEntity;
    }

    @Override
    public void setOpenStream(boolean open) {
        openStream = open;
    }

    @Override
    public boolean isOpenStream() {
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
