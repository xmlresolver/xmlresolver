package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A delegate system catalog entry.
 */
public class EntryDelegateSystem extends Entry {
    /** The system identifier prefix to match. */
    public final String systemIdStart;
    /** The delegated catalog. */
    public final URI catalog;

    /**
     * EntryDelegateSystem constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param startString The system identifier prefix to match.
     * @param catalog the delegated catalog.
     */
    public EntryDelegateSystem(ResolverConfiguration config, URI baseURI, String id, String startString, String catalog) {
        super(config, baseURI, id);

        if (startString.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            systemIdStart = "classpath:" + startString.substring(11);
        } else {
            systemIdStart = startString;
        }

        this.catalog = URIUtils.resolve(baseURI, catalog);
    }

    @Override
    public Type getType() {
        return Type.DELEGATE_SYSTEM;
    }

    @Override
    public String toString() {
        return "delegateSystem " + systemIdStart + Entry.rarr + catalog;
    }
}
