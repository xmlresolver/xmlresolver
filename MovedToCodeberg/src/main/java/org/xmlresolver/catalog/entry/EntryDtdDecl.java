package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A DTD declaration catalog entry.
 */
public class EntryDtdDecl extends Entry {
    /** The entry URI. */
    public final URI uri;
    /** The public identifier of the DTD. */
    public final String publicId;

    /**
     * EntryDtdDecl constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param publicId The public identifier.
     * @param uri The DTD URI.
     */
    public EntryDtdDecl(ResolverConfiguration config, URI baseURI, String id, String publicId, String uri) {
        super(config, baseURI, id);
        this.publicId = publicId;
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.DTD_DECL;
    }

    @Override
    public String toString() {
        return "dtddecl " + publicId + Entry.rarr + uri;
    }
}
