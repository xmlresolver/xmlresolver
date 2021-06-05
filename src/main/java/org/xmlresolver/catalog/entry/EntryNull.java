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
}
