package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

public class EntryRewriteUri extends Entry {
    public final String uriStart;
    public final URI rewritePrefix;

    public EntryRewriteUri(ResolverConfiguration config, URI baseURI, String id, String start, String rewrite) {
        super(config, baseURI, id);

        if (start.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            uriStart = "classpath:" + start.substring(11);
        } else {
            uriStart = start;
        }

        rewritePrefix = URIUtils.resolve(baseURI, rewrite);
    }

    @Override
    public Type getType() {
        return Type.REWRITE_URI;
    }

    @Override
    public String toString() {
        return "rewriteURI " + uriStart + Entry.rarr + rewritePrefix;
    }
}
