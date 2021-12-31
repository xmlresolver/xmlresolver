package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;

import java.net.URI;

public class EntryDocument extends EntryResource {
    public EntryDocument(ResolverConfiguration config, URI baseURI, String id, String uri) {
        super(config, baseURI, id, uri);
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
