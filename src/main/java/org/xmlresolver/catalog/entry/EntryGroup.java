package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;

import java.net.URI;

/**
 * A group catalog entry.
 * <p>A group is a wrapper around other entries. It's largely transparent, but allows the
 * "prefer public" default to be changed.</p>
 */
public class EntryGroup extends Entry {
    /** Are public identifiers preferred? */
    public final boolean preferPublic;

    /**
     * EntryGroup constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param prefer Prefer public identifiers?
     */
    public EntryGroup(ResolverConfiguration config, URI baseURI, String id, boolean prefer) {
        super(config, baseURI, id);
        this.preferPublic = prefer;
    }

    @Override
    public Type getType() {
        return Type.GROUP;
    }

    @Override
    public String toString() {
        return "group prefer=" + (preferPublic ? "public" : "system");
    }
}
