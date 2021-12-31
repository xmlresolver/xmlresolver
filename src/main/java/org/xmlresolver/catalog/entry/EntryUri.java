package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;

import java.net.URI;

public class EntryUri extends EntryResource {
    public final String name;
    public final String nature;
    public final String purpose;

    public EntryUri(ResolverConfiguration config, URI baseURI, String id, String name, String uri, String nature, String purpose) {
        super(config, baseURI, id, uri);

        if (name.startsWith("classpath:/")) {
            // classpath:/path/to/thing is the same as classpath:path/to/thing
            // normalize without the leading slash.
            this.name = "classpath:" + name.substring(11);
        } else {
            this.name = name;
        }

        this.nature = nature;
        this.purpose = purpose;
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
