package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A rewrite system catalog entry.
 */
public class EntryRewriteSystem extends Entry {
    /** The system identifier start string to match. */
    public final String systemIdStart;
    /** The prefix to use when rewriting the system identifier. */
    public final URI rewritePrefix;

    /**
     * EntryRewriteSystem constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param startString The system identifier start string to match.
     * @param rewrite The prefix to use when rewriting the system identifier.
     */
    public EntryRewriteSystem(ResolverConfiguration config, URI baseURI, String id, String startString, String rewrite) {
        super(config, baseURI, id);

        if (startString.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            systemIdStart = "classpath:" + startString.substring(11);
        } else {
            systemIdStart = startString;
        }

        rewritePrefix = URIUtils.resolve(baseURI, rewrite);
    }

    @Override
    public Type getType() {
        return Type.REWRITE_SYSTEM;
    }

    @Override
    public String toString() {
        return "rewriteSystem " + systemIdStart + Entry.rarr + rewritePrefix;
    }
}
