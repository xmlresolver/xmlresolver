package org.xmlresolver.catalog.entry;

import org.xmlresolver.utils.URIUtils;

import java.net.URI;

public class EntryDelegateSystem extends Entry {
    public final String systemIdStart;
    public final URI catalog;

    public EntryDelegateSystem(URI baseURI, String id, String start, String catalog) {
        super(baseURI, id);

        if (start.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            systemIdStart = "classpath:" + start.substring(11);
        } else {
            systemIdStart = start;
        }

        this.catalog = URIUtils.resolve(baseURI, catalog);
    }

    @Override
    public Type getType() {
        return Type.DELEGATE_SYSTEM;
    }
}
