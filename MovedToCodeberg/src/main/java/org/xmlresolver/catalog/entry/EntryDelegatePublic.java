package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A delegate public catalog entry.
 */
public class EntryDelegatePublic extends Entry {
    /** Are public identifiers preferred? */
    public final boolean preferPublic;
    /** The public identifier prefix to match. */
    public final String publicIdStart;
    /** The delegated catalog. */
    public final URI catalog;

    /**
     * EntryDelegatePublic constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param startString The public identifier prefix to match.
     * @param catalog the delegated catalog.
     * @param prefer Are public identifiers preferred?
     */
    public EntryDelegatePublic(ResolverConfiguration config, URI baseURI, String id, String startString, String catalog, boolean prefer) {
        super(config, baseURI, id);
        this.publicIdStart = startString;
        this.catalog = URIUtils.resolve(baseURI, catalog);

        this.preferPublic = prefer;
    }

    @Override
    public Type getType() {
        return Type.DELEGATE_PUBLIC;
    }

    @Override
    public String toString() {
        return "delegatePublic " + publicIdStart + Entry.rarr + catalog;
    }
}
