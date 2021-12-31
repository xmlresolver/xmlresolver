package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;

import java.net.URI;

public class EntryDtddecl extends EntryResource {
    public final String publicId;

    public EntryDtddecl(ResolverConfiguration config, URI baseURI, String id, String publicId, String uri) {
        super(config, baseURI, id, uri);
        this.publicId = publicId;
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
