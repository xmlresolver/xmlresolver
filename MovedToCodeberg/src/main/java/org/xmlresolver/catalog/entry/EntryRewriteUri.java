package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A rewrite URI catalog entry.
 */
public class EntryRewriteUri extends Entry {
    /** The URI start string to match. */
    public final String uriStart;
    /** The prefix to use when rewriting the URI. */
    public final URI rewritePrefix;

    /**
     * EntryRewriteUri constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param startString The URI start string to match.
     * @param rewrite The prefix to use when rewriting the URI.
     */
    public EntryRewriteUri(ResolverConfiguration config, URI baseURI, String id, String startString, String rewrite) {
        super(config, baseURI, id);

        if (startString.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            uriStart = "classpath:" + startString.substring(11);
        } else {
            uriStart = startString;
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
