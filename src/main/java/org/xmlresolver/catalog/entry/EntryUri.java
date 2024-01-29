package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A URI catalog entry.
 */
public class EntryUri extends Entry {
    /** The entry name, the URI of the resource to be matched. */
    public final String name;
    /** The RDDL nature of the entry. */
    public final String nature;
    /** The RDDL purpose of the entry. */
    public final String purpose;
    /** The entry URI. */
    public final URI uri;

    /**
     * EntryUri constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param name The entry name.
     * @param uri The entry URI.
     * @param nature The RDDL nature of the resource, may be null.
     * @param purpose The RDDL purpose of the resource, may be null.
     */
    public EntryUri(ResolverConfiguration config, URI baseURI, String id, String name, String uri, String nature, String purpose) {
        super(config, baseURI, id);

        if (name.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            this.name = "classpath:" + name.substring(11);
        } else {
            this.name = name;
        }

        this.nature = nature;
        this.purpose = purpose;
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.URI;
    }

    @Override
    public String toString() {
        String str = "uri " + name + Entry.rarr + uri;
        if (nature != null || purpose != null) {
            str += " (";
            if (nature != null) {
                str += "nature=" + nature;
            }
            if (nature != null && purpose != null) {
                str += "; ";
            }
            if (purpose != null) {
                str += "purpose=" + purpose;
            }
            str += ")";
        }
        return str;
    }
}
