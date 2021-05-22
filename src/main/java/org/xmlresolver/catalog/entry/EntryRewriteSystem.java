package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryRewriteSystem extends Entry {
    public final String systemIdStart;
    public final URI rewritePrefix;

    public EntryRewriteSystem(URI baseURI, String id, String start, String rewrite) {
        super(baseURI, id);
        systemIdStart = start;
        rewritePrefix = baseURI.resolve(rewrite);
    }

    @Override
    public Type getType() {
        return Type.REWRITE_SYSTEM;
    }
}
