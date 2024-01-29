package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A next catalog entry.
 */
public class EntryNextCatalog extends Entry {
    /** The next catalog URI. */
    public final URI catalog;

    /**
     * EntryNextCatalog constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param catalog The next catalog.
     */
    public EntryNextCatalog(ResolverConfiguration config, URI baseURI, String id, String catalog) {
        super(config, baseURI, id);
        this.catalog = URIUtils.resolve(baseURI, catalog);
    }

    @Override
    public Type getType() {
        return Type.NEXT_CATALOG;
    }

    @Override
    public String toString() {
        return "nextCatalog " + catalog;
    }
}
