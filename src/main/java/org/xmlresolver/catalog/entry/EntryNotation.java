package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;

import java.net.URI;

public class EntryNotation extends EntryResource {
    public final String name;

    public EntryNotation(ResolverConfiguration config, URI baseURI, String id, String name, String uri) {
        super(config, baseURI, id, uri);
        this.name = name;
    }

    @Override
    public Type getType() {
        return Type.NOTATION;
    }

    @Override
    public String toString() {
        return "notation " + name + Entry.rarr + uri;
    }
}
