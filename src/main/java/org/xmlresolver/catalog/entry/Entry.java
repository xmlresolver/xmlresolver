package org.xmlresolver.catalog.entry;

import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.ResolverFeature;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * An (abstract) entry in an XML Catalog.
 * <p>All entries have a base URI, may have an ID, and have a (possibly empty) set of extension attributes.</p>
 */
public abstract class Entry {
    protected final ResolverLogger logger;
    protected final ResolverConfiguration config;

    /** Catalog entries have a type. */
    public enum Type {
        NULL, CATALOG, DELEGATE_PUBLIC, DELEGATE_SYSTEM, DELEGATE_URI,
        DOCTYPE, DOCUMENT, DTD_DECL,ENTITY, GROUP, LINKTYPE, NEXT_CATALOG,
        NOTATION, PUBLIC, REWRITE_SYSTEM, REWRITE_URI, SGML_DECL,
        SYSTEM, SYSTEM_SUFFIX,
        URI, URI_SUFFIX
    }

    // Cheap and cheerful NCNAME test
    private static final Pattern NCNAME_RE = Pattern.compile("^[A-Za-z0-9_]+$");
    protected static String rarr = " → ";

    /** The base URI of the catalog entry. */
    public final URI baseURI;

    /** The (XML) id of the catalog entry. */
    public final String id;

    private final HashMap<String,String> extra = new HashMap<>();

    /**
     * Entry constructor.
     * @param config The configuration.
     * @param baseURI The base URI.
     * @param id The (XML) ID of this element in the XML catalog.
     */
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

    /**
     * Set an extension property.
     * @param name The property name (must be an NCName).
     * @param value The property value (will be the empty string if null is provided).
     * @throws NullPointerException if the name is null.
     */
    public void setProperty(String name, String value) {
        if (name == null) {
            throw new NullPointerException("Cannot set a property with a null name");
        }
        if (NCNAME_RE.matcher(name).matches()) {
            extra.put(name, value == null ? "" : value);
        } else {
            logger.error("Property name invalid: " + name);
        }
    }

    /**
     * Get the value of an extension property.
     * @param name The property name
     * @return The value of the property or null if no such property exists.
     * @throws NullPointerException if the name is null.
     */
    public String getProperty(String name) {
        if (name == null) {
            throw new NullPointerException("Cannot get a property with a null name");
        }
        return extra.getOrDefault(name, null);
    }

    /**
     * The entry type.
     * @return the entry {@link Type}.
     */
    public abstract Type getType();
}
