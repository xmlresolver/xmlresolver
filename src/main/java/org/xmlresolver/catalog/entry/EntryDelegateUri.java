package org.xmlresolver.catalog.entry;

import org.xmlresolver.utils.URIUtils;

import java.net.URI;

public class EntryDelegateUri extends Entry {
    public final String uriStart;
    public final URI catalog;

    public EntryDelegateUri(URI baseURI, String id, String start, String catalog) {
        super(baseURI, id);

        if (start.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            uriStart = "classpath:" + start.substring(11);
        } else {
            uriStart = start;
        }

        this.catalog = URIUtils.resolve(baseURI, catalog);
    }

    @Override
    public Type getType() {
        return Type.DELEGATE_URI;
    }

    @Override
    public String toString() {
        return "delegateURI " + uriStart + Entry.rarr + catalog;
    }
}
