package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryPublic extends EntryResource {
    public final String publicId;
    public final boolean preferPublic;

    public EntryPublic(URI baseURI, String id, String publicId, String uri, boolean prefer) {
        super(baseURI, id, uri);
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
