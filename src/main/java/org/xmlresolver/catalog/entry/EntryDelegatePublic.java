package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryDelegatePublic extends Entry {
    public final boolean preferPublic;
    public final String publicIdStart;
    public final URI catalog;

    public EntryDelegatePublic(URI baseURI, String id, String start, String catalog, boolean prefer) {
        super(baseURI, id);
        this.publicIdStart = start;
        this.catalog = baseURI.resolve(catalog);
        this.preferPublic = prefer;
    }

    @Override
    public Type getType() {
        return Type.DELEGATE_PUBLIC;
    }
}
