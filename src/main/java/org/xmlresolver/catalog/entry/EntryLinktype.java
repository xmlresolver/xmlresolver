package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A linktype catalog entry.
 * <p>This is an extension; linktype catalog entries are defined in SGML Catalogs but not
 * in XML Catalogs.</p>
 */
public class EntryLinktype extends Entry {
    /** The link type name. */
    public final String name;
    /** The entry URI. */
    public final URI uri;

    /**
     * EntryLinktype constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param name The name.
     * @param uri The URI.
     */
    public EntryLinktype(ResolverConfiguration config, URI baseURI, String id, String name, String uri) {
        super(config, baseURI, id);
        this.name = name;
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.LINKTYPE;
    }

    @Override
    public String toString() {
        return "linktype " + name + Entry.rarr + uri;
    }
}
