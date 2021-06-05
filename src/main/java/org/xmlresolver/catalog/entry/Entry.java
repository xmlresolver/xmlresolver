package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverLogger;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Entry {
    public static ResolverLogger logger = new ResolverLogger(Entry.class);
    public enum Type {
        NULL, CATALOG, DELEGATE_PUBLIC, DELEGATE_SYSTEM, DELEGATE_URI,
        DOCTYPE, DOCUMENT, DTD_DECL,ENTITY, GROUP, LINKTYPE, NEXT_CATALOG,
        NOTATION, PUBLIC, REWRITE_SYSTEM, REWRITE_URI, SGML_DECL,
        SYSTEM, SYSTEM_SUFFIX,
        URI, URI_SUFFIX
    }

    // Cheap and cheerful NCNAME test
    public static final Pattern NCNAME_RE = Pattern.compile("^[A-Za-z0-9_]+$");

    public final URI baseURI;
    public final String id;
    public final HashMap<String,String> extra = new HashMap<>();

    public Entry(URI baseURI, String id) {
        this.id = id;
        if (baseURI.isAbsolute()) {
            this.baseURI = baseURI;
        } else {
            throw new IllegalArgumentException("Base URI of catalog entry must be absolute");
        }
    }

    public void setProperty(String name, String value) {
        if (NCNAME_RE.matcher(name).matches()) {
            extra.put(name, value);
        } else {
            logger.log(ResolverLogger.ERROR, "Property name invalid: " + name);
        }
    }

    public void setProperties(Map<String,String> props) {
        for (String key : props.keySet()) {
            setProperty(key, props.get(key));
        }
    }
    public String getProperty(String name) {
        return extra.get(name);
    }

    public Map<String,String> getProperties() {
        return extra;
    }

    public abstract Type getType();
}
