package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.utils.URIUtils;

import java.net.URI;

/**
 * An SGML declaration catalog entry.
 * <p>This is an extension; sgmldecl catalog entries are defined in SGML Catalogs but not
 * in XML Catalogs.</p>
 */
public class EntrySgmldecl extends Entry {
    /** The entry URI. */
    public final URI uri;

    /**
     * EntrySgmlDecl constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     * @param uri The declaration URI.
     */
    public EntrySgmldecl(ResolverConfiguration config, URI baseURI, String id, String uri) {
        super(config, baseURI, id);
        this.uri = URIUtils.resolve(baseURI, uri);
    }

    @Override
    public Type getType() {
        return Type.SGML_DECL;
    }

    @Override
    public String toString() {
        return "sgmldecl " + uri;
    }

}
