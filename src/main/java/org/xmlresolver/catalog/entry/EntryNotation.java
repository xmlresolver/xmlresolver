package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryNotation extends EntryResource {
    public final String name;

    public EntryNotation(URI baseURI, String id, String name, String uri) {
        super(baseURI, id, uri);
        this.name = name;
    }

    @Override
    public Type getType() {
        return Type.NOTATION;
    }
}
