package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;

import java.net.URI;

/**
 * A "null" catalog entry.
 * <p>The null catalog entry is used to represent any element that isn't an
 * XML catalog entry or that is syntactically invalid.
 * It's ignored during catalog lookup.</p>
 */
public class EntryNull extends Entry {
    /**
     * EntryNull constructor.
     * @param config The configuration.
     */
    public EntryNull(ResolverConfiguration config) {
        super(config, URI.create("http://xmlresolver.org/irrelevant"), null);
    }

    @Override
    public Type getType() {
        return Type.NULL;
    }

    @Override
    public String toString() {
        return "null entry (not a catalog element)";
    }
}
