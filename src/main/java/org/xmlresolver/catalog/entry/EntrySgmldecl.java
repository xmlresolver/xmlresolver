package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;

import java.net.URI;

public class EntrySgmldecl extends EntryResource {
    public EntrySgmldecl(ResolverConfiguration config, URI baseURI, String id, String uri) {
        super(config, baseURI, id, uri);
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
