package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryUri extends EntryResource {
    public final String name;
    public final String nature;
    public final String purpose;

    public EntryUri(URI baseURI, String id, String name, String uri, String nature, String purpose) {
        super(baseURI, id, uri);
        this.name = name;
        this.nature = nature;
        this.purpose = purpose;
    }

    @Override
    public Type getType() {
        return Type.URI;
    }
}
