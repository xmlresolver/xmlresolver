package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryDtddecl extends EntryResource {
    public final String publicId;

    public EntryDtddecl(URI baseURI, String id, String publicId, String uri) {
        super(baseURI, id, uri);
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
