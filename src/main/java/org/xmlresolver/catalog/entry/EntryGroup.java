package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryGroup extends Entry {
    public final boolean preferPublic;

    public EntryGroup(URI baseURI, String id, boolean prefer) {
        super(baseURI, id);
        // FIXME: warn if prefer is neither public nor system
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
