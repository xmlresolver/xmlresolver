package org.xmlresolver.catalog.entry;

import java.net.URI;

public class EntrySgmldecl extends EntryResource {
    public EntrySgmldecl(URI baseURI, String id, String uri) {
        super(baseURI, id, uri);
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
