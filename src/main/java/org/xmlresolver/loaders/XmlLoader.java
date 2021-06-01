package org.xmlresolver.loaders;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlresolver.CatalogManager;
import org.xmlresolver.ResolverConstants;
import org.xmlresolver.ResolverLogger;
import org.xmlresolver.Resource;
import org.xmlresolver.catalog.entry.Entry;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.catalog.entry.EntryNull;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class XmlLoader implements CatalogLoader {
    protected static ResolverLogger logger = new ResolverLogger(CatalogManager.class);

    protected final HashMap<URI, EntryCatalog> catalogMap;
    private boolean preferPublic = true;

    public XmlLoader() {
        catalogMap = new HashMap<>();
    }

    public void setPreferPublic(boolean prefer) {
        preferPublic = prefer;
    }

    public boolean getPreferPublic() {
        return preferPublic;
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
        } catch (IOException | URISyntaxException ex) {
            logger.log(ResolverLogger.ERROR, "Failed to load catalog: %s: %s", catalog, ex.getMessage());
            catalogMap.put(catalog, new EntryCatalog(catalog, null, false));
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

        synchronized (catalogMap) {
            try {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                spf.setValidating(false);
                spf.setXIncludeAware(false);
                SAXParser parser = spf.newSAXParser();
                CatalogContentHandler handler = new CatalogContentHandler(catalog, preferPublic);
                parser.parse(source, handler);
                EntryCatalog entry = handler.catalog();
                catalogMap.put(catalog, entry);
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                logger.log(ResolverLogger.ERROR, "Failed to load catalog: " + catalog + ": " + ex.getMessage());
                catalogMap.put(catalog, new EntryCatalog(catalog, null, false));
            }
        }

        return catalogMap.get(catalog);
    }

    private static class CatalogContentHandler extends DefaultHandler {
        public static ResolverLogger logger = new ResolverLogger(CatalogContentHandler.class);

        private static final HashSet<String> CATALOG_ELEMENTS
                = new HashSet<>(Arrays.asList(
                "group", "public", "system", "rewriteSystem",
                "delegatePublic", "delegateSystem", "uri", "rewriteURI", "delegateURI",
                "nextCatalog", "doctype", "document", "dtddecl", "entity", "linktype",
                "notation", "sgmldecl", "uriSuffix", "systemSuffix"));

        private final Stack<Entry> parserStack = new Stack<>();
        private final Stack<Boolean> preferPublicStack = new Stack<>();
        private final Stack<URI> baseURIStack = new Stack<>();
        private EntryCatalog catalog = null;

        protected CatalogContentHandler(URI uri, boolean preferPublic) {
            preferPublicStack.push(preferPublic);
            baseURIStack.push(uri);
        }

        public EntryCatalog catalog() {
            return catalog;
        }

        @Override
        public void setDocumentLocator (Locator locator) {
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
                            logger.log(ResolverLogger.ERROR, "Prefer on " + localName + " is neither 'system' nor 'public': " + prefer);
                        }
                    }
                    catalog = new EntryCatalog(baseURIStack.peek(), id, preferPublicStack.peek());
                    parserStack.push(catalog);
                } else {
                    logger.log(ResolverLogger.ERROR, "Catalog document is not an XML Catalog (ignored): " + qName);
                    parserStack.push(new EntryNull());
                }
                baseURIStack.push(baseURIStack.peek());
                preferPublicStack.push(preferPublicStack.peek());
                return;
            }

            Entry top = parserStack.peek();
            if (top.getType() == Entry.Type.NULL) {
                pushNull();
            } else {
                if (ResolverConstants.CATALOG_NS.equals(uri)) {
                    if (CATALOG_ELEMENTS.contains(localName)) {
                        catalogElement(localName, attributes);
                    } else {
                        logger.log(ResolverLogger.ERROR, "Unexpected catalog element (ignored): " + localName);
                        pushNull();
                    }
                } else {
                    pushNull();
                }
            }
        }

        private void pushNull() {
            parserStack.push(new EntryNull());
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
                baseURI = baseURI.resolve(attributes.getValue("xml:base"));
            }

            boolean preferPublic = preferPublicStack.peek();

            Entry entry = new EntryNull();

            switch (localName) {
                case "group":
                    String prefer = attributes.getValue("", "prefer");
                    if (prefer != null) {
                        preferPublic = "public".equals(prefer);
                        if (!"public".equals(prefer) && !"system".equals(prefer)) {
                            logger.log(ResolverLogger.ERROR, "Prefer on " + localName + " is neither 'system' nor 'public': " + prefer);
                        }
                    }
                    entry = catalog.addGroup(baseURI, id, preferPublic);
                    break;
                case "public":
                    // In XML, there will always be a system identifier.
                    publicId = attributes.getValue("", "publicId");
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
                    start = attributes.getValue("", "publicIdStartString");
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
    }
}
