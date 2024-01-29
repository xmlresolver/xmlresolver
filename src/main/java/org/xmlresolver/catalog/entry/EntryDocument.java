package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A document catalog entry.
 */
public class EntryDocument extends Entry {
    /** The entry URI. */
    public final URI uri;

    /**
     * EntryDocument constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param uri The document URI.
     */
    public EntryDocument(ResolverConfiguration config, URI baseURI, String id, String uri) {
        super(config, baseURI, id);
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.DOCUMENT;
    }

    @Override
    public String toString() {
        return "document " + Entry.rarr + uri;
    }
}
