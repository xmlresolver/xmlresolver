package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;

import java.net.URI;

public class EntryLinktype extends EntryResource {
    public final String name;

    public EntryLinktype(ResolverConfiguration config, URI baseURI, String id, String name, String uri) {
        super(config, baseURI, id, uri);
        this.name = name;
    }

    @Override
    public Type getType() {
        return Type.LINKTYPE;
    }

    @Override
    public String toString() {
        return "linktype " + name + Entry.rarr + uri;
    }
}
