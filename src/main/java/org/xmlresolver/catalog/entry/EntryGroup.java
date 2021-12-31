package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;

import java.net.URI;

public class EntryGroup extends Entry {
    public final boolean preferPublic;

    public EntryGroup(ResolverConfiguration config, URI baseURI, String id, boolean prefer) {
        super(config, baseURI, id);
        this.preferPublic = prefer;
    }

    @Override
    public Type getType() {
        return Type.GROUP;
    }

    @Override
    public String toString() {
        return "group prefer=" + (preferPublic ? "public" : "system");
    }
}
