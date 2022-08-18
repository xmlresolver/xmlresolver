package org.xmlresolver.loaders;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlresolver.*;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryNull;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.utils.PublicId;
import org.xmlresolver.utils.URIUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.*;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** An XML catalog loader.
 *
 * <p>This loader understands the XML Catalogs 1.1 specification XML catalog syntax.</p>
 */
public class XmlLoader implements CatalogLoader {
    protected final ResolverConfiguration config;
    protected final ResolverLogger logger;
    protected final HashMap<URI, EntryCatalog> catalogMap;

    private static Resolver loaderResolver = null;
    private boolean preferPublic = true;
    private boolean archivedCatalogs = true;

    public XmlLoader(ResolverConfiguration config) {
        this.config = config;
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        catalogMap = new HashMap<>();
    }

    /** Set the default "prefer public" status for this catalog.
     *
     * @param prefer True if public identifiers are to be preferred.
     */
    @Override
    public void setPreferPublic(boolean prefer) {
        preferPublic = prefer;
    }

    /** Return the current "prefer public" status.
     *
     * @return The current "prefer public" status of this catalog loader.
     */
    @Override
    public boolean getPreferPublic() {
        return preferPublic;
    }

    @Override
    public void setArchivedCatalogs(boolean allow) {
        archivedCatalogs = allow;
    }

    @Override
    public boolean getArchivedCatalogs() {
        return archivedCatalogs;
    }

    public static synchronized Resolver getLoaderResolver() {
        if (loaderResolver == null) {
            XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
            config.setFeature(ResolverFeature.PREFER_PUBLIC, true);
            config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/org/xmlresolver/catalog.xml"));
            config.setFeature(ResolverFeature.CACHE_DIRECTORY, null);
            config.setFeature(ResolverFeature.CACHE_UNDER_HOME, false);
            config.setFeature(ResolverFeature.ALLOW_CATALOG_PI, false);
            config.setFeature(ResolverFeature.CLASSPATH_CATALOGS, false);
            loaderResolver = new Resolver(config);
        }

        return loaderResolver;
    }

    public EntryCatalog loadCatalog(URI catalog) {
        if (catalogMap.containsKey(catalog)) {
            return catalogMap.get(catalog);
        }

        try {
            Resource rsrc = new Resource(catalog.toString());
            InputSource source = new InputSource(rsrc.body());
            source.setSystemId(catalog.toString());
            return loadCatalog(catalog, source);
        } catch (FileNotFoundException fex) {
            logger.log(AbstractLogger.WARNING, "Failed to load catalog: %s: %s", catalog, fex.getMessage());
            catalogMap.put(catalog, new EntryCatalog(config, catalog, null, false));
            return catalogMap.get(catalog);
        } catch (IOException | URISyntaxException ex) {
            logger.log(AbstractLogger.ERROR, "Failed to load catalog: %s: %s", catalog, ex.getMessage());
            catalogMap.put(catalog, new EntryCatalog(config, catalog, null, false));
            return catalogMap.get(catalog);
        }
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
    public EntryCatalog loadCatalog(URI catalog, InputSource source) {
        if (catalogMap.containsKey(catalog)) {
            return catalogMap.get(catalog);
        }

        if (!catalog.isAbsolute()) {
            throw new IllegalArgumentException("Catalog URIs must be absolute: " + catalog);
        }

        URI zipcatalog = null;

        synchronized (catalogMap) {
            try {
                CatalogContentHandler handler = new CatalogContentHandler(config, catalog, preferPublic);

                Supplier<XMLReader> supplier = config.getFeature(ResolverFeature.XMLREADER_SUPPLIER);
                if (supplier != null) {
                    XMLReader reader = supplier.get();
                    reader.setContentHandler(handler);
                    reader.parse(source);
                } else {
                    // Wat?
                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    spf.setNamespaceAware(true);
                    spf.setValidating(false);
                    spf.setXIncludeAware(false);
                    SAXParser parser = spf.newSAXParser();
                    parser.parse(source, handler);
                }

                EntryCatalog entry = handler.catalog();
                catalogMap.put(catalog, entry);
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                logger.log(AbstractLogger.ERROR, "Failed to load catalog: " + catalog + ": " + ex.getMessage());
                catalogMap.put(catalog, new EntryCatalog(config, catalog, null, false));

                if (archivedCatalogs) {
                    zipcatalog = archiveCatalog(catalog);
                }
            }
        }

        if (zipcatalog != null) {
            EntryCatalog zipcat = loadCatalog(zipcatalog);
            catalogMap.put(catalog, zipcat);
        }

        return catalogMap.get(catalog);
    }

    private URI archiveCatalog(URI catalog) {
        if (!"file".equals(catalog.getScheme())) {
            // For the moment, let's limit this to file: URIs
            return null;
        }

        // Archive files come in two basic flavors, "top level" and "directory".
        // In a top level archive, all the files unpack into the current working directory.
        // In a directory archive, all of the files unpack into a directory under the current working directory
        // (In other words, they all have a leading directory/ component in their names.)
        //
        // We want to support either flavor.
        //
        // In a top level archive, if org/xmlresolver/catalog.xml exists, use it. If catalog.xml exists,
        // use it. If both exist, use org/xmlresolver/catalog.xml
        //
        // In a directory archive, if directory/org/xmlresolver/catalog.xml exists, use it.
        // If directory/catalog.xml exists, use it. If both exist, use directory/org/xmlresolver/catalog.xml
        //
        // If none of these conditions apply, there's no catalog for us here.

        HashSet<String> catalogSet = new HashSet<> ();
        boolean firstEntry = true;
        String leadingDir = null;

        try {
            URLConnection conn = catalog.toURL().openConnection();
            ZipInputStream zip = new ZipInputStream(conn.getInputStream());
            ZipEntry entry = zip.getNextEntry();
            while (entry != null) {
                if (firstEntry) {
                    int pos = entry.getName().indexOf("/");
                    if (pos >= 0) {
                        leadingDir = entry.getName().substring(0, pos);
                    }
                    firstEntry = false;
                } else {
                    if (leadingDir != null) {
                        int pos = entry.getName().indexOf("/");
                        if (pos < 0  || !leadingDir.equals(entry.getName().substring(0, pos))) {
                            leadingDir = null;
                        }
                    }
                }

                if (!entry.isDirectory() && entry.getName().endsWith("catalog.xml")) {
                    catalogSet.add(entry.getName());
                }

                entry = zip.getNextEntry();
            }
            zip.close();

            String catpath = null;
            if (leadingDir != null) {
                if (catalogSet.contains(leadingDir + "/catalog.xml")) {
                    catpath = "/" + leadingDir + "/catalog.xml";
                }
                if (catalogSet.contains(leadingDir + "/org/xmlresolver/catalog.xml")) {
                    catpath = "/" + leadingDir + "/org/xmlresolver/catalog.xml";
                }
            } else {
                if (catalogSet.contains("catalog.xml")) {
                    catpath = "/catalog.xml";
                }
                if (catalogSet.contains("org/xmlresolver/catalog.xml")) {
                    catpath = "/org/xmlresolver/catalog.xml";
                }
            }

            if (catpath != null) {
                return new URI("jar:file://" +  catalog.getPath() + "!" + catpath);
            }

            logger.log(AbstractLogger.ERROR, "Failed to find catalog in archived catalog: " + catalog);
        } catch (IOException|URISyntaxException ex) {
            logger.log(AbstractLogger.ERROR, "Failed to load archived catalog: " + catalog + ": " + ex.getMessage());
        }

        return null;
    }

    private static class CatalogContentHandler extends DefaultHandler {
        public final ResolverLogger logger;

        private static final HashSet<String> CATALOG_ELEMENTS
                = new HashSet<>(Arrays.asList("group", "public", "system", "rewriteSystem",
                "delegatePublic", "delegateSystem", "uri", "rewriteURI", "delegateURI",
                "nextCatalog", "uriSuffix", "systemSuffix"));

        private static final HashSet<String> TR9401_ELEMENTS
                = new HashSet<>(Arrays.asList("doctype", "document", "dtddecl", "entity",
                "linktype", "notation", "sgmldecl"));

        private Locator locator = null;
        private final ResolverConfiguration config;
        private final Stack<Entry> parserStack = new Stack<>();
        private final Stack<Boolean> preferPublicStack = new Stack<>();
        private final Stack<URI> baseURIStack = new Stack<>();
        private EntryCatalog catalog = null;

        protected CatalogContentHandler(ResolverConfiguration config, URI uri, boolean preferPublic) {
            this.config = config;
            logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
            preferPublicStack.push(preferPublic);
            baseURIStack.push(uri);
        }

        public EntryCatalog catalog() {
            return catalog;
        }

        @Override
        public void setDocumentLocator (Locator locator) {
            this.locator = locator;
            if (catalog != null) {
                catalog.setLocator(locator);
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (parserStack.isEmpty()) {
                if (ResolverConstants.CATALOG_NS.equals(uri) && "catalog".equals(localName)) {
                    String id = attributes.getValue("", "id");
                    String prefer = attributes.getValue("", "prefer");
                    if (prefer != null) {
                        preferPublicStack.push("public".equals(prefer));
                        if (!"public".equals(prefer) && !"system".equals(prefer)) {
                            logger.log(AbstractLogger.ERROR, "Prefer on " + localName + " is neither 'system' nor 'public': " + prefer);
                        }
                    }
                    catalog = new EntryCatalog(config, baseURIStack.peek(), id, preferPublicStack.peek());
                    parserStack.push(catalog);

                    if (locator != null) {
                        catalog.setLocator(locator);
                    }
                } else {
                    logger.log(AbstractLogger.ERROR, "Catalog document is not an XML Catalog (ignored): " + qName);
                    catalog = new EntryCatalog(config, baseURIStack.peek(), null, false);
                    parserStack.push(new EntryNull(config));
                }

                URI baseURI = baseURIStack.peek();
                if (attributes.getValue("xml:base") != null) {
                    baseURI = URIUtils.resolve(baseURI, attributes.getValue("xml:base"));
                }

                baseURIStack.push(baseURI);
                preferPublicStack.push(preferPublicStack.peek());
                return;
            }

            Entry top = parserStack.peek();
            if (top.getType() == Entry.Type.NULL) {
                pushNull();
            } else {
                if (ResolverConstants.CATALOG_NS.equals(uri)) {
                    // Technically, the TR9401 extension elements should be in the TR9401 namespace,
                    // but I'm willing to bet lots of folks get that wrong. Be liberal in what mumble mumble...
                    if (CATALOG_ELEMENTS.contains(localName) || TR9401_ELEMENTS.contains(localName)) {
                        catalogElement(localName, attributes);
                    } else {
                        logger.log(AbstractLogger.ERROR, "Unexpected catalog element (ignored): " + localName);
                        pushNull();
                    }
                } else if (ResolverConstants.TR9401_NS.equals(uri)) {
                    if (TR9401_ELEMENTS.contains(localName)) {
                        catalogElement(localName, attributes);
                    } else {
                        logger.log(AbstractLogger.ERROR, "Unexpected catalog element (ignored): " + localName);
                        pushNull();
                    }
                } else {
                    pushNull();
                }
            }
        }

        private void pushNull() {
            parserStack.push(new EntryNull(config));
            baseURIStack.push(baseURIStack.peek());
            preferPublicStack.push(preferPublicStack.peek());
        }

        private void catalogElement(String localName, Attributes attributes) {
            String id = attributes.getValue("", "id");
            String name = attributes.getValue("", "name");
            String uri = attributes.getValue("", "uri");
            String caturi = attributes.getValue("", "catalog");
            String start, prefix, suffix, publicId;

            URI baseURI = baseURIStack.peek();
            if (attributes.getValue("xml:base") != null) {
                baseURI = URIUtils.resolve(baseURI, attributes.getValue("xml:base"));
            }

            boolean preferPublic = preferPublicStack.peek();

            Entry entry = new EntryNull(config);

            switch (localName) {
                case "group":
                    String prefer = attributes.getValue("", "prefer");
                    if (prefer != null) {
                        preferPublic = "public".equals(prefer);
                        if (!"public".equals(prefer) && !"system".equals(prefer)) {
                            logger.log(AbstractLogger.ERROR, "Prefer on " + localName + " is neither 'system' nor 'public': " + prefer);
                        }
                    }
                    entry = catalog.addGroup(baseURI, id, preferPublic);
                    break;
                case "public":
                    // In XML, there will always be a system identifier.
                    publicId = PublicId.normalize(attributes.getValue("", "publicId"));
                    entry = catalog.addPublic(baseURI, id, publicId, uri, preferPublic);
                    break;
                case "system":
                    String systemId = attributes.getValue("", "systemId");
                    entry = catalog.addSystem(baseURI, id, systemId, uri);
                    break;
                case "rewriteSystem":
                    start = attributes.getValue("", "systemIdStartString");
                    prefix = attributes.getValue("", "rewritePrefix");
                    entry = catalog.addRewriteSystem(baseURI, id, start, prefix);
                    break;
                case "systemSuffix":
                    suffix = attributes.getValue("", "systemIdSuffix");
                    entry = catalog.addSystemSuffix(baseURI, id, suffix, uri);
                    break;
                case "delegatePublic":
                    start = PublicId.normalize(attributes.getValue("", "publicIdStartString"));
                    entry = catalog.addDelegatePublic(baseURI, id, start, caturi, preferPublic);
                    break;
                case "delegateSystem":
                    start = attributes.getValue("", "systemIdStartString");
                    entry = catalog.addDelegateSystem(baseURI, id, start, caturi);
                    break;
                case "uri":
                    String nature = attributes.getValue(ResolverConstants.RDDL_NS, "nature");
                    String purpose = attributes.getValue(ResolverConstants.RDDL_NS, "purpose");
                    entry = catalog.addUri(baseURI, id, name, uri, nature, purpose);
                    break;
                case "uriSuffix":
                    suffix = attributes.getValue("", "uriSuffix");
                    entry = catalog.addUriSuffix(baseURI, id, suffix, uri);
                    break;
                case "rewriteURI":
                    start = attributes.getValue("", "uriStartString");
                    prefix = attributes.getValue("", "rewritePrefix");
                    entry = catalog.addRewriteUri(baseURI, id, start, prefix);
                    break;
                case "delegateURI":
                    start = attributes.getValue("", "uriStartString");
                    entry = catalog.addDelegateUri(baseURI, id, start, caturi);
                    break;
                case "nextCatalog":
                    entry = catalog.addNextCatalog(baseURI, id, caturi);
                    break;
                case "doctype":
                    entry = catalog.addDoctype(baseURI, id, name, uri);
                    break;
                case "document":
                    entry = catalog.addDocument(baseURI, id, uri);
                    break;
                case "dtddecl":
                    publicId = attributes.getValue("", "publicId");
                    entry = catalog.addDtdDecl(baseURI, id, publicId, uri);
                    break;
                case "entity":
                    entry = catalog.addEntity(baseURI, id, name, uri);
                    break;
                case "linktype":
                    entry = catalog.addLinktype(baseURI, id, name, uri);
                    break;
                case "notation":
                    entry = catalog.addNotation(baseURI, id, name, uri);
                    break;
                case "sgmldecl":
                    entry = catalog.addSgmlDecl(baseURI, id, uri);
                    break;
                default:
                    // This shouldn't happen!
                    break;
            }

            for (int pos = 0; pos < attributes.getLength(); pos++) {
                if (ResolverConstants.XMLRESOURCE_EXT_NS.equals(attributes.getURI(pos))) {
                    entry.setProperty(attributes.getLocalName(pos), attributes.getValue(pos));
                }
            }

            parserStack.push(entry);
            baseURIStack.push(baseURI);
            preferPublicStack.push(preferPublic);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            parserStack.pop();
            baseURIStack.pop();
            preferPublicStack.pop();
        }

        @Override
        public InputSource resolveEntity (String publicId,
                                          String systemId)
                throws SAXException, IOException {
            return getLoaderResolver().resolveEntity(publicId, systemId);
        }
    }
}
