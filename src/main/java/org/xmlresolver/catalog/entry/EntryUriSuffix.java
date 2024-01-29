package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A URI suffix catalog entry.
 */
public class EntryUriSuffix extends Entry {
    /** The URI suffix to match. */
    public final String uriSuffix;
    /** The entry URI. */
    public final URI uri;

    /**
     * EntryUriSuffix constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param suffix The suffix to match.
     * @param uri The entry URI.
     */
    public EntryUriSuffix(ResolverConfiguration config, URI baseURI, String id, String suffix, String uri) {
        super(config, baseURI, id);
        this.uriSuffix = suffix;
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.URI_SUFFIX;
    }

    @Override
    public String toString() {
        return "uriSuffix " + uriSuffix + Entry.rarr + uri;
    }
}
