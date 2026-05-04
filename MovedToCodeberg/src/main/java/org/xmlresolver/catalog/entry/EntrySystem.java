package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * A system catalog entry.
 */
public class EntrySystem extends Entry {
    /** The system identifier. */
    public final String systemId;
    /** The entry URI. */
    public final URI uri;

    /**
     * EntrySystem constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param systemId The system identifier.
     * @param uri The entry URI.
     */
    public EntrySystem(ResolverConfiguration config, URI baseURI, String id, String systemId, String uri) {
        super(config, baseURI, id);

        if (systemId.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            this.systemId = "classpath:" + systemId.substring(11);
        } else {
            this.systemId = systemId;
        }

        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.SYSTEM;
    }

    @Override
    public String toString() {
        return "system " + systemId + Entry.rarr + uri;
    }
}
