package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryNull extends Entry {
    public EntryNull() {
        super(URI.create("http://xmlresolver.org/irrelevant"), null);
    }

    @Override
    public Type getType() {
        return Type.NULL;
    }

    @Override
    public String toString() {
        return "null entry (not a catalog element)";
    }
}
