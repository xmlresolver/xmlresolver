package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * An entity catalog entry.
 */
public class EntryEntity extends Entry {
    /** The entity name. */
    public final String name;
    /** The entry URI. */
    public final URI uri;

    /**
     * EntryEntity constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param name The entity name.
     * @param uri The entity URI.
     */
    public EntryEntity(ResolverConfiguration config, URI baseURI, String id, String name, String uri) {
        super(config, baseURI, id);
        this.name = name;
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.ENTITY;
    }

    @Override
    public String toString() {
        return "entity " + name + Entry.rarr + uri;
    }
}
