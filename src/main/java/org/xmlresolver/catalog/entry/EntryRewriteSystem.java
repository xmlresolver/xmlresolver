package org.xmlresolver.catalog.entry;

import org.xmlresolver.utils.URIUtils;

import java.net.URI;

public class EntryRewriteSystem extends Entry {
    public final String systemIdStart;
    public final URI rewritePrefix;

    public EntryRewriteSystem(URI baseURI, String id, String start, String rewrite) {
        super(baseURI, id);

        if (start.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            systemIdStart = "classpath:" + start.substring(11);
        } else {
            systemIdStart = start;
        }

        rewritePrefix = URIUtils.resolve(baseURI, rewrite);
    }

    @Override
    public Type getType() {
        return Type.REWRITE_SYSTEM;
    }
}
