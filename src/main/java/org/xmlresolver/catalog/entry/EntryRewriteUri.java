package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntryRewriteUri extends Entry {
    public final String uriStart;
    public final URI rewritePrefix;

    public EntryRewriteUri(URI baseURI, String id, String start, String rewrite) {
        super(baseURI, id);

        if (start.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            uriStart = "classpath:" + start.substring(11);
        } else {
            uriStart = start;
        }

        rewritePrefix = baseURI.resolve(rewrite);
    }

    @Override
    public Type getType() {
        return Type.REWRITE_URI;
    }
}
