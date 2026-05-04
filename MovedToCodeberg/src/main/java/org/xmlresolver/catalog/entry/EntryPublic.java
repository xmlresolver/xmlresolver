package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A public catalog entry.
 */
public class EntryPublic extends Entry {
    /** The public identifier. */
    public final String publicId;
    /** Are public identifiers preferred? */
    public final boolean preferPublic;
    /** The entry URI. */
    public final URI uri;

    /**
     * EntryPublic constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param publicId The public identifier.
     * @param uri The entry URI.
     * @param prefer Are public identifiers preferred?
     */
    public EntryPublic(ResolverConfiguration config, URI baseURI, String id, String publicId, String uri, boolean prefer) {
        super(config, baseURI, id);
        this.publicId = publicId;
        this.preferPublic = prefer;
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.PUBLIC;
    }

    @Override
    public String toString() {
        return "public " + publicId + Entry.rarr + uri;
    }
}
