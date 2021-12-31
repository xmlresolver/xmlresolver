package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.ResolverFeature;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Entry {
    protected final ResolverLogger logger;
    protected final ResolverConfiguration config;

    public enum Type {
        NULL, CATALOG, DELEGATE_PUBLIC, DELEGATE_SYSTEM, DELEGATE_URI,
        DOCTYPE, DOCUMENT, DTD_DECL,ENTITY, GROUP, LINKTYPE, NEXT_CATALOG,
        NOTATION, PUBLIC, REWRITE_SYSTEM, REWRITE_URI, SGML_DECL,
        SYSTEM, SYSTEM_SUFFIX,
        URI, URI_SUFFIX
    }

    // Cheap and cheerful NCNAME test
    public static final Pattern NCNAME_RE = Pattern.compile("^[A-Za-z0-9_]+$");
    public static String rarr = " → ";

    public final URI baseURI;
    public final String id;
    public final HashMap<String,String> extra = new HashMap<>();

    public Entry(ResolverConfiguration config, URI baseURI, String id) {
        this.id = id;
        if (baseURI.isAbsolute()) {
            this.baseURI = baseURI;
        } else {
            throw new IllegalArgumentException("Base URI of catalog entry must be absolute: " + baseURI);
        }
        this.config = config;
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    public void setProperty(String name, String value) {
        if (NCNAME_RE.matcher(name).matches()) {
            extra.put(name, value);
        } else {
            logger.log(AbstractLogger.ERROR, "Property name invalid: " + name);
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
