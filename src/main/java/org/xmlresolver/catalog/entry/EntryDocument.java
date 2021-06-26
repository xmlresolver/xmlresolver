package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryDocument extends EntryResource {
    public EntryDocument(URI baseURI, String id, String uri) {
        super(baseURI, id, uri);
    }

    @Override
    public Type getType() {
        return Type.DOCUMENT;
    }

    @Override
    public String toString() {
        return "document " + Entry.rarr + uri;
    }
}
