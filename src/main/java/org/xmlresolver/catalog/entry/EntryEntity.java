package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;

import java.net.URI;

public class EntryEntity extends EntryResource {
    public final String name;

    public EntryEntity(ResolverConfiguration config, URI baseURI, String id, String name, String uri) {
        super(config, baseURI, id, uri);
        this.name = name;
    }

    @Override
    public Type getType() {
        return Type.ENTITY;
    }

    @Override
    public String toString() {
        return "entity " + name + Entry.rarr + uri;
    }
}
