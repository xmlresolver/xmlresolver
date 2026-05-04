package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A doctype catalog entry.
 */
public class EntryDoctype extends Entry {
    /** The doctype name. */
    public final String name;
    /** The entry URI. */
    public final URI uri;

    /**
     * EntryDoctype constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param name The doctype name.
     * @param uri The entry URI.
     */
    public EntryDoctype(ResolverConfiguration config, URI baseURI, String id, String name, String uri) {
        super(config, baseURI, id);
        this.name = name;
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.DOCTYPE;
    }

    @Override
    public String toString() {
        return "doctype " + name + Entry.rarr + uri;
    }
}
