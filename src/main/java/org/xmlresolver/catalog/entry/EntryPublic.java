package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;

import java.net.URI;

public class EntryPublic extends EntryResource {
    public final String publicId;
    public final boolean preferPublic;

    public EntryPublic(ResolverConfiguration config, URI baseURI, String id, String publicId, String uri, boolean prefer) {
        super(config, baseURI, id, uri);
        this.publicId = publicId;
        this.preferPublic = prefer;
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
