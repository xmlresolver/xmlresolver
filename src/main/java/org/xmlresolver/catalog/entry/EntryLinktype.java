package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryLinktype extends EntryResource {
    public final String name;

    public EntryLinktype(URI baseURI, String id, String name, String uri) {
        super(baseURI, id, uri);
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
