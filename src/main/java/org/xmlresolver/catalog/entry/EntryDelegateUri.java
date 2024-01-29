package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A delegate URI catalog entry.
 */
public class EntryDelegateUri extends Entry {
    /** The URI prefix to match. */
    public final String uriStart;
    /** The delegated catalog. */
    public final URI catalog;

    /**
     * EntryDelegateUri constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param startString The URI prefix to match.
     * @param catalog the delegated catalog.
     */
    public EntryDelegateUri(ResolverConfiguration config, URI baseURI, String id, String startString, String catalog) {
        super(config, baseURI, id);

        if (startString.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            uriStart = "classpath:" + startString.substring(11);
        } else {
            uriStart = startString;
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
