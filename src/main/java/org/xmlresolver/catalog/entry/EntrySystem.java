package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntrySystem extends EntryResource {
    public final String systemId;

    public EntrySystem(URI baseURI, String id, String systemId, String uri) {
        super(baseURI, id, uri);
        this.systemId = systemId;
    }

    @Override
    public Type getType() {
        return Type.SYSTEM;
    }
}
