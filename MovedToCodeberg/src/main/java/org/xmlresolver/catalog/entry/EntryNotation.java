package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A notation catalog entry.
 */
public class EntryNotation extends Entry {
    /** The notation name. */
    public final String name;
    /** The entry URI. */
    public final URI uri;

    /**
     * EntryNotation constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param name The notation name.
     * @param uri The notation URI.
     */
    public EntryNotation(ResolverConfiguration config, URI baseURI, String id, String name, String uri) {
        super(config, baseURI, id);
        this.name = name;
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.NOTATION;
    }

    @Override
    public String toString() {
        return "notation " + name + Entry.rarr + uri;
    }
}
