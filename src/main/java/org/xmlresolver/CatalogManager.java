package org.xmlresolver;

import org.xml.sax.InputSource;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.query.*;
import org.xmlresolver.loaders.CatalogLoader;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.utils.PublicId;
import org.xmlresolver.utils.URIUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>CatalogManager</code> manages a list of OASIS XML Catalogs
 * and performs lookup operations on those catalogs.
 *
 * <p>The manager's job is to implement the semantics of
 * <a href="https://www.oasis-open.org/committees/download.php/14809/xml-catalogs.html">XML
 * Catalogs</a>.</p>
 *
 * <p>This class loads OASIS XML Catalog files and provides methods for
 * searching the catalog. All of the XML Catalog entry types defined in
 * §6 (catalog, group, public, system, rewriteSystem, systemSuffix,
 * delegatePublic, delegateSystem, uri, rewriteURI, uriSuffix,
 * delegateURI, and nextCatalog) are supported. In addition, the
 * following TR9401 Catalog entry types from §D are supported: doctype,
 * document, entity, and notation. (The other types do not apply to
 * XML.)</p>
 *
 * <p>For each of the query methods, the manager either returns the mapping
 * indicated by the catalog, or <code>null</code> to indicate that no match was found.</p>
 */

public class CatalogManager implements XMLCatalogResolver {
    protected final ResolverLogger logger;
    protected final ResolverConfiguration resolverConfiguration;
    protected CatalogLoader catalogLoader;

    /** Construct a catalog manager for the specified configuration.
     *
     * @param config The resolver configuration for this catalog manager.
     */
    protected CatalogManager(ResolverConfiguration config) {
        resolverConfiguration = config;
        String loaderClassName = config.getFeature(ResolverFeature.CATALOG_LOADER_CLASS);
        if (loaderClassName == null || "".equals(loaderClassName)) {
            loaderClassName = ResolverFeature.CATALOG_LOADER_CLASS.getDefaultValue();
        }
        try {
            Class<?> loaderClass = Class.forName(loaderClassName);
            Constructor<?> constructor = loaderClass.getConstructor(ResolverConfiguration.class);
            catalogLoader = (CatalogLoader) constructor.newInstance(config);
        } catch (ClassNotFoundException|NoSuchMethodException|InstantiationException
                 |IllegalAccessException| InvocationTargetException ex) {
            throw new IllegalArgumentException("Failed to instantiate catalog loader: " + loaderClassName + ": " + ex.getMessage());
        }
        catalogLoader.setPreferPublic(config.getFeature(ResolverFeature.PREFER_PUBLIC));
        catalogLoader.setArchivedCatalogs(config.getFeature(ResolverFeature.ARCHIVED_CATALOGS));
        logger = resolverConfiguration.getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    /** Constructs a catalog manager from the current one and a configuration.
     *
     * <p>This constructor creates a new catalog manager using the catalog loader of the current
     * manager and the specified configuration as its resolver configuration.</p>
     *
     * @param current The manager to copy.
     * @param newConfig The resolver configuration of the copied constructor.
     */
    protected CatalogManager(CatalogManager current, ResolverConfiguration newConfig) {
        catalogLoader = current.catalogLoader;
        resolverConfiguration = newConfig;
        logger = resolverConfiguration.getFeature(ResolverFeature.RESOLVER_LOGGER);
    }

    public ResolverConfiguration getResolverConfiguration() {
        return resolverConfiguration;
    }

    public List<URI> catalogs() {
        ArrayList<URI> catlist = new ArrayList<>();
        for (String cat : resolverConfiguration.getFeature(ResolverFeature.CATALOG_FILES)) {
            catlist.add(URIUtils.cwd().resolve(cat));
        }
        return catlist;
    }

    /**
     * Load the specified catalog.
     *
     * <p>The manager maintains a set of the catalogs that it has loaded. If an attempt is
     * made to load a catalog twice, the previously loaded catalog is returned.</p>
     *
     * @param catalog The catalog URI.
     * @return The parsed catalog.
     */
    public EntryCatalog loadCatalog(URI catalog) {
        return catalogLoader.loadCatalog(catalog);
    }

    /**
     * Load the specified catalog from a given input source.
     *
     * <p>This method exists so that a catalog can be loaded even if it doesn't have a URI
     * that can be dereferenced. It must still have a URI.</p>
     *
     * <p>The manager maintains a set of the catalogs that it has loaded. If an attempt is
     * made to load a catalog twice, the previously loaded catalog is returned.</p>
     *
     * @param catalog The catalog URI.
     * @param source The input source.
     * @return The parsed catalog.
     */
    public EntryCatalog loadCatalog(URI catalog, InputSource source)  {
        return catalogLoader.loadCatalog(catalog, source);
    }

    /**
     * Lookup the specified URI in the catalog.
     *
     * <p>If a URI entry exists in the catalog for the URI specified, return the mapped value.</p>
     *
     * @param uri The URI to locate in the catalog.
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public URI lookupURI(String uri) {
        return lookupNamespaceURI(uri, null, null);
    }

    /**
     * Lookup the specified namespace URI in the catalog.
     *
     * <p>If a URI entry exists in the catalog for the URI specified, and with the specified
     * nature and purpose, return the mapped value. </p>
     *
     * @param uri The URI to locate in the catalog.
     * @param nature The RDDL nature of the requested resource
     * @param purpose The RDDL purpose of the requested resource
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public URI lookupNamespaceURI(String uri, String nature, String purpose) {
        return new QueryUri(uri, nature, purpose).search(this).uri();
    }

    /**
     * Lookup the specified system and public identifiers in the catalog.
     *
     * <p>If a SYSTEM or PUBLIC entry exists in the catalog for
     * the system and public identifiers specified, return the mapped
     * value.</p>
     *
     * <p>On Windows and MacOS operating systems, the comparison between
     * the system identifier provided and the SYSTEM entries in the
     * Catalog is case-insensitive.</p>
     *
     * @param systemId The nominal system identifier for the entity
     * in question (as provided in the source document).
     * @param publicId The public identifier to locate in the catalog.
     * Public identifiers are normalized before comparison.
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public URI lookupPublic(String systemId, String publicId) {
        ExternalIdentifiers external = normalizeExternalIdentifiers(systemId, publicId);
        return new QueryPublic(external.systemId, external.publicId).search(this).uri();
    }

    /**
     * Lookup the specified system identifier in the catalog.
     *
     * <p>If a SYSTEM entry exists in the catalog for
     * the system identifier specified, return the mapped
     * value.</p>
     *
     * <p>On Windows-based operating systems, the comparison between
     * the system identifier provided and the SYSTEM entries in the
     * Catalog is case-insensitive.</p>
     *
     * @param systemId The system identifier to locate in the catalog.
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public URI lookupSystem(String systemId) {
        ExternalIdentifiers external = normalizeExternalIdentifiers(systemId, null);
        if (external.systemId == null) {
            return null;
        }

        return new QuerySystem(systemId).search(this).uri();
    }

    /**
     * Lookup the specified document type in the catalog.
     *
     * <p>If a DOCTYPE entry exists in the catalog for
     * the specified arguments, return the mapped
     * value.</p>
     *
     * @param entityName The name of the entity (element) for which
     * a doctype is required.
     * @param publicId The nominal public identifier for the doctype
     * (as provided in the source document).
     * @param systemId The nominal system identifier for the doctype
     * (as provided in the source document).
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public URI lookupDoctype(String entityName, String systemId, String publicId) {
        ExternalIdentifiers external = normalizeExternalIdentifiers(systemId, publicId);
        return new QueryDoctype(entityName, external.systemId, external.publicId).search(this).uri();
    }

    /**
     * Lookup the default document in the catalog.
     *
     * <p>If a DOCUMENT entry exists in the catalog,
     * return the mapped value.</p>
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public URI lookupDocument() {
        return new QueryDocument().search(this).uri();
    }

    /**
     * Lookup the specified entity in the catalog.
     *
     * <p>If an ENTITY entry exists in the catalog for
     * the specified arguments, return the mapped
     * value.</p>
     *
     * @param entityName The name of the entity
     * @param systemId The nominal system identifier for the entity
     * @param publicId The nominal public identifier for the entity
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public URI lookupEntity(String entityName, String systemId, String publicId) {
        ExternalIdentifiers external = normalizeExternalIdentifiers(systemId, publicId);
        return new QueryEntity(entityName, external.systemId, external.publicId).search(this).uri();
    }

    /**
     * Lookup the specified notation in the catalog.
     *
     * <p>If a NOTATION entry exists in the catalog for
     * the specified arguments, return the mapped
     * value.</p>
     *
     * @param notationName The name of the notation
     * @param systemId The nominal system identifier for the entity
     * @param publicId The nominal public identifier for the entity
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public URI lookupNotation(String notationName, String systemId, String publicId) {
        ExternalIdentifiers external = normalizeExternalIdentifiers(systemId, publicId);
        return new QueryNotation(notationName, external.systemId, external.publicId).search(this).uri();
    }

    private static class ExternalIdentifiers {
        private final String systemId;
        private final String publicId;
        private ExternalIdentifiers(String systemId, String publicId) {
            this.systemId = systemId;
            this.publicId = publicId;
        }
    }

    private ExternalIdentifiers normalizeExternalIdentifiers(String systemId, String publicId) {
        if (systemId != null) {
            systemId = URIUtils.normalizeURI(systemId);
        }

        if (publicId != null && publicId.startsWith("urn:publicid:")) {
            publicId = PublicId.decodeURN(publicId);
        }

        if (systemId != null && systemId.startsWith("urn:publicid:")) {
            String decodedSysId = PublicId.decodeURN(systemId);
            if (publicId != null && !publicId.equals(decodedSysId)) {
                logger.log(AbstractLogger.ERROR, "urn:publicid: system identifier differs from public identifier; using public identifier");
            } else {
                publicId = decodedSysId;
            }
            systemId = null;
        }

        return new ExternalIdentifiers(systemId, publicId);
    }

    public String normalizedForComparison(String uri) {
        if (uri == null) {
            return null;
        }

        if (uri.startsWith("classpath:/")) {
            return "classpath:" + uri.substring(11);
        }

        if (resolverConfiguration.getFeature(ResolverFeature.MERGE_HTTPS) && uri.startsWith("http:")) {
            return "https:" + uri.substring(5);
        }

        return uri;
    }
}
