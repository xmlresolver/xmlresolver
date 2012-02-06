/*
 * Catalog.java
 *
 * Created on December 25, 2006, 9:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlresolver.helpers.DOMUtils;
import org.xmlresolver.helpers.PublicId;
import org.xmlresolver.helpers.URIUtils;

/**
 *Implements the OASIS XML Catalog Standard.
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
 * <p>Many aspects of catalog processing can be configured when the
 * <code>Catalog</code> class is instantiated. The <code>Catalog</code>
 * class examines both system properties and the properties specified in
 * a separate resource. The initial list of catalog files can be provided
 * as a property or directly when the <code>Catalog</code> is
 * created.</p>
 * 
 * <p>If the list of property files is not specified, the default list is
 * "<code>XMLResolver.properties;CatalogManager.properties</code>".
 * </p>
 * 
 * <p>The following properties are recognized:</p>
 * 
 * <dl>
 * <dt><code>cache</code> (system property <code>xml.catalog.cache</code>)</dt>
 * <dd>Identifies a directory where caching will be performed. If not specified,
 * no caching is performed. The directory specified must be writable by the application.
 * The default is not to cache.
 * </dd>
 * <dt><code>cacheUnderHome</code> (system property <code>xml.catalog.cacheUnderHome</code>)</dt>
 * <dd>If set to "true/yes/1" and no cache directory was specified then the cache
 * directory "&lt;home&gt;/.xmlresolver/cache" is used.
 * </dd>
 * <dt><code>catalogs</code> (system property <code>xml.catalog.files</code>)</dt>
 * <dd>A semi-colon delimited list of catalog files. Each of these files will be
 * loaded, in turn and as necessary, when searching for entries. Additional files
 * may be loaded if referenced from the initial files. The default is
 * "<code>./catalog.xml</code>".
 * </dd>
 * <dt><code>relative-catalogs</code></dt>
 * <dd>This property only applies when loaded from a property file. If set to
 * "<code>true</code>" or "<code>yes</code>" then relative file names 
 * in the property file will be used. Otherwise, they will be made absolute with
 * respect to the property file. The default is "<code>yes</code>".
 * </dd>
 * <dt><code>prefer</code> (system property <code>xml.catalog.prefer</code>)</dt>
 * <dd>Sets the default value of the XML Catalogs "prefer" setting.
 * </dd>
 * <dt><code>cache-<em>scheme</em>-uri</code> (system property <code>xml.catalog.cache.<em>scheme</em></code>)</dt>
 * <dd>Determines whether URIs of a particular <em>scheme</em> will be cached.
 * If nothing is said about a particular scheme then the default is "false" for
 * <code>file</code>-scheme URIs and "true" for everything else.
 * </dd>
 * </dl>
 *
 * @author ndw
 */
public class Catalog {
    /** The XML Namespace name of OASIS XML Catalog files, "urn:oasis:names:tc:entity:xmlns:xml:catalog". */
    public static final String NS_CATALOG = "urn:oasis:names:tc:entity:xmlns:xml:catalog";
    /** The XML Namespace name of RDDL, "http://www.rddl.org/". */
    public static final String NS_RDDL = "http://www.rddl.org/";
    /** The XML Namespace name of XLink, "http://www.w3.org.1999/xlink". */
    public static final String NS_XLINK = "http://www.w3.org/1999/xlink";
    /** The XML Namespace name of XML Resolver Catalog extensions, "http://xmlresolver.org/ns/catalog". */
    public static final String NS_XMLRESOURCE_EXT = "http://xmlresolver.org/ns/catalog";
    
    public static Logger logger = Logger.getLogger("org.xmlresolver");
    
    private static String defaultPropertiesFiles() {
        // Yes, this is XMLResolver.properties even though the class is Catalog; that's because this
        // is the XML Resolver project.
        String propfile = System.getProperty("xmlresolver.properties");
        return (propfile != null ? propfile + ";" : "") + "XMLResolver.properties;CatalogManager.properties";
    }
    
    private Configuration conf;
    
    private Vector<CatalogSource> catalogList = new Vector<CatalogSource>();
    private Vector<Document> documentList = new Vector<Document>();
    private ResourceCache cache = null;

    /** Creates a catalog using properties read from the default property file.
     *
     * <p>The default property file is <code>XMLResolver.properties</code>.</p>
     */
    public Catalog() {
        this(defaultPropertiesFiles(), (String)null);
    }
    
    /** Creates a catalog using the specified list of catalog files.
     *
     * <p>Reads all other properties from the default property file, <code>XMLResolver.properties</code>.</p>
     *
     * @param catalogList A semicolon delimited list of catalog files. If {@code null} is specified then
     * the default "./catalog.xml" is used.
     */
    public Catalog(String catalogList) {
        this(defaultPropertiesFiles(), catalogList);
    }

    /** Creates a catalog using the specified property file and list of catalog files.
     *
     * <p>Uses the specified catalog files and reads all other properties from the first property file
     * that it can find. The class path is searched for each property file in turn and the first
     * file that can be found is used. The list of property files must be delimited with semicolons (";").
     *
     * @param propertyFileList The name of one or more property files.
     * @param catalogList A semicolon delimited list of catalog files. If {@code null} is specified then
     * the default "./catalog.xml" is used.
     */
    public Catalog(String propertyFileList, String catalogList) {
      this(Configuration.create(propertyFileList), catalogList);
    }
    
    public Catalog(Configuration conf, String catalogList) {
        this(conf, createSources(conf, catalogList));
    }
    
    public Catalog(Configuration conf, Vector<CatalogSource> catalogList) {
      this.conf = conf;
      this.catalogList = catalogList;
      setCacheDir(conf.queryCache());
    }
    
    public final void setCacheDir(String cacheDir) {
        if (cacheDir != null && !cacheDir.equals("")) {
            cache = new ResourceCache(cacheDir);
        } else {
            cache = null;
        }
    }

    // ======================================================================================================
    
    /** Returns the list of known catalog files.
     *
     * <p>Note that catalog processing is optimistic. Only the necessary files are read.
     * Additional catalogs may be added to this list as <code>nextCatalog</code> entries
     * are processed. Note also that delegation replaces this list with a new list.</p>
     *
     * @return A semicolon delimited list of known catalog files.
     */
    public String catalogList() {
        String list = "";
        for (CatalogSource catalog : catalogList) {
            if (list.length() > 0) {
                list = list + ";";
            }
            list = list + catalog;
        }
        
        return list;
    }

    /** Returns the resource cache associated with this catalog, if there is one. */
    public ResourceCache cache() {
        return cache;
    }

    /** Checks if the specified URI scheme is cached.
     *
     * <p>The system property "<code>xml.catalog.cache.<em>scheme</em></code>" or the property
     * "<code>cache-<em>scheme</em>-uri</code>" in the property file can be used to specify
     * which schemes should be cached.</p>
     *
     * <p>By default, all schemes except <code>file</code> are cached.</p>
     *
     * @param scheme The name of a URI scheme, for example "http" or "file".
     *
     * @return true if and only if URIs in the requested scheme should be cached.
     */
    public boolean cacheSchemeURI(String scheme) {
      return conf.queryCacheSchemeURI(scheme);
    }

    private synchronized Document loadCatalog(int index) {
        if (index < documentList.size()) {
            return documentList.get(index);
        }
        
        CatalogSource catalog = catalogList.get(index);

        Document doc = catalog.parse();
        
        while (documentList.size() <= index) {
            documentList.add(null);
        }
        documentList.set(index, doc);

        int offset = 1;
        if (doc != null) {
            Element root = doc.getDocumentElement();
                        
            if (catalogElement(root, "catalog")) {
                Element child = DOMUtils.getFirstElement(root);
                while (child != null) {
                    if (catalogElement(child, "nextCatalog")) {
                        Element nextCat = (Element) child;
                        String nextCatalog = DOMUtils.makeAbsolute(nextCat, nextCat.getAttribute("catalog"));
                        logger.finer("Next catalog: " + nextCat.getAttribute("catalog") + " (" + nextCatalog + ")");
                        
                        if (index+offset >= catalogList.size()) {
                            catalogList.add(new CatalogSource.UriCatalogSource(nextCatalog));
                        } else {
                            catalogList.insertElementAt(new CatalogSource.UriCatalogSource(nextCatalog), index+offset);
                        }
                        offset++;
                    }
                    child = DOMUtils.getNextElement(child);
                }
            }
        }
        
        return doc;
    }

    private boolean catalogElement(Node node, String localName) {
        return (node.getNodeType() == Element.ELEMENT_NODE
                && localName.equals(node.getLocalName())
                && NS_CATALOG.equals(node.getNamespaceURI()));
    }

    private Vector<Element> entries(Element group, String entry) {
        Vector<Element> matchingEntries = new Vector<Element> ();
        findMatches(matchingEntries, group, entry, null, null, null, null);
        return matchingEntries;
    }

    private Vector<Element> entries(Element group, String entry, String attr, String value, String nature, String purpose) {
        Vector<Element> matchingEntries = new Vector<Element> ();
        findMatches(matchingEntries, group, entry, attr, value, nature, purpose);
        return matchingEntries;
    }

    private void findMatches(Vector<Element> matching, Element group, String entry, String attr, String value, String nature, String purpose) {
        if (catalogElement(group,"group") || catalogElement(group, "catalog")) {
            Element child = DOMUtils.getFirstElement(group);
            while (child != null) {
                if (catalogElement(child, entry) && (attr == null || value.equals(child.getAttribute(attr)))) {
                    String uriNature = child.hasAttributeNS(NS_RDDL, "nature") ? child.getAttributeNS(NS_RDDL, "nature") : null;
                    String uriPurpose = child.hasAttributeNS(NS_RDDL, "purpose") ? child.getAttributeNS(NS_RDDL, "purpose") : null;

                    if ((nature == null || nature.equals(uriNature))
                        && (purpose == null || purpose.equals(uriPurpose))) {
                        matching.add(child);
                    }
                }
                
                if (catalogElement(child, "group")) {
                    findMatches(matching, child, entry, attr, value, nature, purpose);
                }

                child = DOMUtils.getNextElement(child);
            }
        }
    }
    
    /**
     * Lookup the specified URI in the catalog.
     *
     * <p>If a URI entry exists in the catalog
     * for the URI specified, return the mapped value.</p>
     *
     * <p>URI comparison is case sensitive.</p>
     *
     * @param uri The URI to locate in the catalog.
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public CatalogResult lookupURI(String uri) {
        logger.fine("lookupURI(" + uri + ")");
        return _lookupNamespaceURI(uri, null, null);
    }

    /**
     * Lookup the specified namespace URI in the catalog.
     *
     * <p>If a URI entry exists in the catalog
     * for the URI specified and with the specified nature and purpose, return the mapped value.</p>
     *
     * <p>URI comparison is case sensitive.</p>
     *
     * @param uri The URI to locate in the catalog.
     * @param nature The RDDL nature of the requested resource
     * @param purpose The RDDL purpose of the requested resource
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public CatalogResult lookupNamespaceURI(String uri, String nature, String purpose) {
        logger.fine("lookupNamespaceURI(" + uri + "," + nature + "," + purpose + ")");
        return _lookupNamespaceURI(uri, nature, purpose);
    }

    private CatalogResult _lookupNamespaceURI(String uri, String nature, String purpose) {
        if (uri == null) {
            throw new NullPointerException("Null uri passed to Catalog.");
        }

        uri = URIUtils.normalizeURI(uri);

        if (uri.startsWith("urn:publicid:")) {
            return lookupPublic(PublicId.decodeURN(uri), null);
        }

        int index = 0;
        while (index < catalogList.size()) {
            loadCatalog(index);
            Document doc = documentList.get(index);
            if (doc != null) {
                logger.finer("  Looking in " + doc.getBaseURI());
                CatalogResult resolved = lookupURI(doc.getDocumentElement(), uri, nature, purpose);
                if (resolved != null) {
                    logger.fine("  Found: " + resolved);
                    return resolved;
                }
            }
            index++;
        }

        if (cache != null && cache.catalog() != null) {
            logger.finer("  Looking in " + cache.catalog().getBaseURI());
            CatalogResult resolved = lookupURI(cache.catalog().getDocumentElement(), uri, nature, purpose);
            if (resolved != null) {
                logger.fine("  Found: " + resolved);
                return resolved;
            }
        }
        
        logger.fine("  Not found");
        return null;
    }

    protected CatalogResult lookupURI(Element group, String uri, String nature, String purpose) {
        for (Element child : entries(group, "uri", "name", uri, nature, purpose)) {
            String entry_uri = child.getAttribute("uri");
            String entry_redir = child.getAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "redir");
            return new CatalogResult(entry_redir == null ? entry_uri : entry_redir, DOMUtils.makeAbsolute(child, entry_uri), child, cache);
        }

        // If there's a REWRITE_URI entry in this catalog, use it
        String startString = null;
        Element node = null;
        for (Element child : entries(group, "rewriteURI", null, uri, nature, purpose)) {
            String p = child.getAttribute("uriStartString");
            if (p.length() <= uri.length() && p.equals(uri.substring(0, p.length()))) {
                // Is this the longest prefix?
                if (startString == null || p.length() > startString.length()) {
                    startString = p;
                    node = child;
                }
            }
        }

        if (node != null) {
            // return the uri with the new prefix
            return new CatalogResult(uri,
                    DOMUtils.makeAbsolute(node, node.getAttribute("rewritePrefix") + uri.substring(startString.length())),
                    node, cache);
        }

        // If there's a SYSTEM_SUFFIX entry in this catalog, use it
        String suffixString = null;
        node = null;
        for (Element child : entries(group, "uriSuffix", null, uri, nature, purpose)) {
            String p = child.getAttribute("uriSuffix");
            if (p.length() <= uri.length() && uri.endsWith(p)) {
                // Is this the longest prefix?
                if (suffixString == null || p.length() > suffixString.length()) {
                    suffixString = p;
                    node = child;
                }
            }
        }
        
        if (node != null) {
            // return the uri for the suffix
            return new CatalogResult(uri, DOMUtils.makeAbsolute(node, node.getAttribute("uri")), node, cache);
        }

        // If there's a DELEGATE_URI entry in this catalog, use it
        Vector<String> delegated = new Vector<String> ();
        for (Element child : entries(group, "delegateURI", null, uri, nature, purpose)) {
            String p = child.getAttribute("uriStartString");
            if (p.length() <= uri.length()
                && p.equals(uri.substring(0, p.length()))) {
                // delegate this match to the other catalog
                delegated.add(DOMUtils.makeAbsolute(child, child.getAttribute("catalog")));
            }
        }

        if (!delegated.isEmpty()) {
            Catalog dResolver = new Catalog(conf, toCatalogSources(delegated));
            CatalogResult resolved = null;
            if (nature != null || purpose != null) {
                resolved = dResolver.lookupNamespaceURI(uri, nature, purpose);
            } else {
                resolved = dResolver.lookupURI(uri);
            }
            return resolved;
        }
        
        return null;
    }
    
    /**
     * Lookup the specified system and public identifiers in the catalog.
     *
     * <p>If a SYSTEM or PUBLIC entry exists in the catalog for
     * the system and public identifiers specified, return the mapped
     * value.</p>
     *
     * <p>On Windows-based operating systems, the comparison between
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
    public CatalogResult lookupPublic(String systemId, String publicId) {
        logger.fine("lookupPublic(" + systemId + "," + publicId + ")");

        if (systemId != null) {
            systemId = URIUtils.normalizeURI(systemId);
        }

        if (publicId != null && publicId.startsWith("urn:publicid:")) {
            publicId = PublicId.decodeURN(publicId);
        }

        if (systemId != null && systemId.startsWith("urn:publicid:")) {
            systemId = PublicId.decodeURN(systemId);
            if (publicId != null && !publicId.equals(systemId)) {
                logger.warning("urn:publicid: system identifier differs from public identifier; using public identifier");
                systemId = null;
            } else {
                publicId = systemId;
                systemId = null;
            }
        }

        int index = 0;
        while (index < catalogList.size()) {
            loadCatalog(index);
            Document doc = documentList.get(index);
            if (doc != null) {
                logger.finer("  Looking in " + doc.getBaseURI());
                CatalogResult resolved = lookupPublic(doc.getDocumentElement(), systemId, publicId);
                if (resolved != null) {
                    logger.fine("  Found: " + resolved);
                    return resolved;
                }
            }
            index++;
        }

        if (cache != null && cache.catalog() != null) {
            logger.finer("  Looking in " + cache.catalog().getBaseURI());
            CatalogResult resolved = lookupPublic(cache.catalog().getDocumentElement(), systemId, publicId);
            if (resolved != null) {
                logger.fine("  Found: " + resolved);
                return resolved;
            }
        }
        
        logger.fine("  Not found");
        return null;
    }        
        
    protected CatalogResult lookupPublic(Element group, String systemId, String publicId) {
        if (catalogElement(group,"group") || catalogElement(group, "catalog")) {
            // If there's a SYSTEM entry in this catalog, use it
            if (systemId != null) {
                CatalogResult resolved = lookupLocalSystem(group, systemId);
                if (resolved != null) {
                    return resolved;
                }
            }

            if (publicId != null) {
                CatalogResult resolved = lookupLocalPublic(group, systemId, publicId);
                if (resolved != null) {
                    return resolved;
                }
            }
        }
        
        return null;
    }

    protected CatalogResult lookupLocalPublic(Element group, String systemId, String publicId) {
        // Always normalize the public identifier before attempting a match
        publicId = PublicId.normalize(publicId);

        // If there's a SYSTEM entry in this catalog, use it
        if (systemId != null) {
            CatalogResult resolved = lookupLocalSystem(group, systemId);
            if (resolved != null) {
                return resolved;
            }
        }
        
        // If there's a PUBLIC entry in this catalog, use it
        for (Element child : entries(group, "public", "publicId", publicId, null, null)) {
            // What's the prefer setting for this entry?
            boolean preferpublic = conf.queryPreferPublic();
            Node node = child;
            while (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                Element p = (Element) node;
                if (p.hasAttribute("prefer")) {
                    preferpublic = "public".equals(p.getAttribute("prefer"));
                    node = null;
                } else {
                    node = node.getParentNode();
                }
            }

            if (preferpublic || systemId == null) {
                String localURI = DOMUtils.makeAbsolute(child, child.getAttribute("uri"));
                String origURI = systemId;
                if (origURI == null) {
                    origURI = localURI;
                }
                return new CatalogResult(origURI, localURI, child, cache);
            }
        }            

        // If there's a DELEGATE_PUBLIC entry in this catalog, use it
        Vector<String> delegated = new Vector<String> ();
        for (Element child : entries(group, "delegatePublic", null, null, null, null)) {
            String p = child.getAttribute("publicIdStartString");
            if (p.length() <= systemId.length()
                && p.equals(systemId.substring(0, p.length()))) {
                // delegate this match to the other catalog
                delegated.add(DOMUtils.makeAbsolute(child, child.getAttribute("catalog")));
            }
        }

        if (!delegated.isEmpty()) {
            Catalog dResolver = new Catalog(conf, toCatalogSources(delegated));
            CatalogResult resolved = dResolver.lookupSystem(systemId);
            return resolved;
        }
            
        return null;
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
    public CatalogResult lookupSystem(String systemId) {
        logger.fine("lookupSystem(" + systemId + ")");

        systemId = URIUtils.normalizeURI(systemId);

        if (systemId != null && systemId.startsWith("urn:publicid:")) {
            return lookupPublic(PublicId.decodeURN(systemId), null);
        }

        int index = 0;
        while (index < catalogList.size()) {
            loadCatalog(index);
            Document doc = documentList.get(index);
            if (doc != null) {
                logger.finer("  Looking in " + doc.getBaseURI());
                CatalogResult resolved = lookupLocalSystem(doc.getDocumentElement(), systemId);
                if (resolved != null) {
                    logger.fine("  Found: " + resolved);
                    return resolved;
                }
            }
            index++;
        }

        if (cache != null && cache.catalog() != null) {
            logger.finer("  Looking in " + cache.catalog().getBaseURI());
            CatalogResult resolved = lookupLocalSystem(cache.catalog().getDocumentElement(), systemId);
            if (resolved != null) {
                logger.fine("  Found: " + resolved);
                return resolved;
            }
        }

        logger.fine("  Not found");
        return null;
    }
    
    protected CatalogResult lookupLocalSystem(Element group, String systemId) {
        String osname = System.getProperty("os.name");
        boolean windows = (osname.indexOf("Windows") >= 0);

        for (Element child : entries(group, "system", null, null, null, null)) {
            if (systemId.equals(child.getAttribute("systemId"))
                || (windows && systemId.equalsIgnoreCase(child.getAttribute("systemId")))) {
                return new CatalogResult(systemId, DOMUtils.makeAbsolute(child, child.getAttribute("uri")), child, cache);
            }
        }

        // If there's a REWRITE_SYSTEM entry in this catalog, use it
        String startString = null;
        Element node = null;
        for (Element child : entries(group, "rewriteSystem", null, null, null, null)) {
            String p = child.getAttribute("uriStartString");
            if (p.length() <= systemId.length() && p.equals(systemId.substring(0, p.length()))) {
                // Is this the longest prefix?
                if (startString == null || p.length() > startString.length()) {
                    startString = p;
                    node = child;
                }
            }
        }

        if (node != null) {
            // return the systemId with the new prefix
            return new CatalogResult(systemId, DOMUtils.makeAbsolute(node, node.getAttribute("rewritePrefix") + systemId.substring(startString.length())), node, cache);
        }

        // If there's a SYSTEM_SUFFIX entry in this catalog, use it
        String suffixString = null;
        node = null;
        for (Element child : entries(group, "systemSuffix", null, null, null, null)) {
            String p = child.getAttribute("systemIdSuffix");
            if (p.length() <= systemId.length() && systemId.endsWith(p)) {
                // Is this the longest prefix?
                if (suffixString == null || p.length() > suffixString.length()) {
                    suffixString = p;
                    node = child;
                }
            }
        }
        
        if (node != null) {
            // return the systemId for the suffix
            return new CatalogResult(systemId, DOMUtils.makeAbsolute(node, node.getAttribute("uri")), node, cache);
        }

        // If there's a DELEGATE_SYSTEM entry in this catalog, use it
        Vector<String> delegated = new Vector<String> ();
        for (Element child : entries(group, "delegateSystem", null, null, null, null)) {
            String p = child.getAttribute("systemIdStartString");
            if (p.length() <= systemId.length()
                && p.equals(systemId.substring(0, p.length()))) {
                // delegate this match to the other catalog
                delegated.add(DOMUtils.makeAbsolute(child, child.getAttribute("catalog")));
            }
        }

        if (!delegated.isEmpty()) {
            Catalog dResolver = new Catalog(conf, toCatalogSources(delegated));
            CatalogResult resolved = dResolver.lookupSystem(systemId);
            return resolved;
        }
            
        return null;
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
    public CatalogResult lookupDoctype(String entityName, String systemId, String publicId) {
        logger.fine("lookupDoctype(" + entityName + "," + publicId + "," + systemId + ")");
  
        systemId = URIUtils.normalizeURI(systemId);

        if (publicId != null && publicId.startsWith("urn:publicid:")) {
            publicId = PublicId.decodeURN(publicId);
        }

        if (systemId != null && systemId.startsWith("urn:publicid:")) {
            systemId = PublicId.decodeURN(systemId);
            if (publicId != null && !publicId.equals(systemId)) {
                logger.warning("urn:publicid: system identifier differs from public identifier; using public identifier");
                systemId = null;
            } else {
                publicId = systemId;
                systemId = null;
            }
        }

        int index = 0;
        while (index < catalogList.size()) {
            loadCatalog(index);
            Document doc = documentList.get(index);
            if (doc != null) {
                logger.finer("  Looking in " + doc.getBaseURI());
                CatalogResult resolved = lookupDoctype(doc.getDocumentElement(), entityName, systemId, publicId);
                if (resolved != null) {
                    logger.fine("  Found: " + resolved);
                    return resolved;
                }
            }
            index++;
        }

        logger.fine("  Not found");
        return null;
    }
  
    protected CatalogResult lookupDoctype(Element group, String entityName, String systemId, String publicId) {
        CatalogResult resolved = null;
      
        if (systemId != null) {
            // If there's a SYSTEM entry in this catalog, use it
            resolved = lookupLocalSystem(group, systemId);
            if (resolved != null) {
                return resolved;
            }
        }

        if (publicId != null) {
            // If there's a PUBLIC entry in this catalog, use it
            resolved = lookupLocalPublic(group, systemId, publicId);
            if (resolved != null) {
                return resolved;
            }
        }

        // If there's a DOCTYPE entry in this catalog, use it
        for (Element child : entries(group, "doctype", "name", entityName, null, null)) {
            // What's the prefer setting for this entry?
            boolean preferpublic = conf.queryPreferPublic();
            Node node = child;
            while (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                Element p = (Element) node;
                if (p.hasAttribute("prefer")) {
                    preferpublic = "public".equals(p.getAttribute("prefer"));
                    node = null;
                } else {
                    node = node.getParentNode();
                }
            }

            if (preferpublic || systemId == null) {
                String localURI = DOMUtils.makeAbsolute(child, child.getAttribute("uri"));
                String origURI = systemId;
                if (origURI == null) {
                    origURI = localURI;
                }
                return new CatalogResult(origURI, localURI, child, cache);
            }
        }

        return null;
    }
  
    /**
     * Lookup the default document in the catalog.
     *
     * <p>If a DOCUMENT entry exists in the catalog,
     * return the mapped value.</p>
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public CatalogResult lookupDocument() {
        logger.fine("lookupDocument()");
        int index = 0;
        while (index < catalogList.size()) {
            loadCatalog(index);
            Document doc = documentList.get(index);
            if (doc != null) {
                logger.finer("  Looking in " + doc.getBaseURI());
                CatalogResult resolved = lookupDocument(doc.getDocumentElement());
                if (resolved != null) {
                    logger.fine("  Found: " + resolved);
                    return resolved;
                }
            }
            index++;
        }

        logger.fine("  Not found");
        return null;
    }

    protected CatalogResult lookupDocument(Element group) {
        for (Element child : entries(group, "document", null, null, null, null)) {
            String localURI = DOMUtils.makeAbsolute(child, child.getAttribute("uri"));
            return new CatalogResult(localURI, localURI, child, cache);
        }
        
        return null;
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
    public CatalogResult lookupEntity(String entityName, String systemId, String publicId) {
        logger.fine("lookupEntity(" + entityName + "," + publicId + "," + systemId + ")");

        systemId = URIUtils.normalizeURI(systemId);

        if (publicId != null && publicId.startsWith("urn:publicid:")) {
            publicId = PublicId.decodeURN(publicId);
        }

        if (systemId != null && systemId.startsWith("urn:publicid:")) {
            systemId = PublicId.decodeURN(systemId);
            if (publicId != null && !publicId.equals(systemId)) {
                logger.warning("urn:publicid: system identifier differs from public identifier; using public identifier");
                systemId = null;
            } else {
                publicId = systemId;
                systemId = null;
            }
        }

        int index = 0;
        while (index < catalogList.size()) {
            loadCatalog(index);
            Document doc = documentList.get(index);
            if (doc != null) {
                logger.finer("  Looking in " + doc.getBaseURI());
                CatalogResult resolved = lookupEntity(doc.getDocumentElement(), entityName, systemId, publicId);
                if (resolved != null) {
                    logger.fine("  Found: " + resolved);
                    return resolved;
                }
            }
            index++;
        }

        logger.fine("  Not found");
        return null;
    }
  
    protected CatalogResult lookupEntity(Element group, String entityName, String systemId, String publicId) {
        CatalogResult resolved = null;

        if (systemId != null) {
            // If there's a SYSTEM entry in this catalog, use it
            resolved = lookupLocalSystem(group, systemId);
            if (resolved != null) {
                return resolved;
            }
        }

        if (publicId != null) {
            // If there's a PUBLIC entry in this catalog, use it
            resolved = lookupLocalPublic(group, systemId, publicId);
            if (resolved != null) {
                return resolved;
            }
        }

        // If there's a ENTITY entry in this catalog, use it
        for (Element child : entries(group, "entity", "name", entityName, null, null)) {
            // What's the prefer setting for this entry?
            boolean preferpublic = conf.queryPreferPublic();
            Node node = child;
            while (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                Element p = (Element) node;
                if (p.hasAttribute("prefer")) {
                    preferpublic = "public".equals(p.getAttribute("prefer"));
                    node = null;
                } else {
                    node = node.getParentNode();
                }
            }

            if (preferpublic || systemId == null) {
                String localURI = DOMUtils.makeAbsolute(child, child.getAttribute("uri"));
                String origURI = systemId;
                if (origURI == null) {
                    origURI = localURI;
                }
                return new CatalogResult(origURI, localURI, child, cache);
            }
        }

        return null;
    }

    /**
     * Lookup the specified notation in the catalog.
     *
     * <p>If a NOTATION entry exists in the catalog for
     * the specified arguments, return the mapped
     * value.</p>
     *
     * @param notName The name of the notation
     * @param systemId The nominal system identifier for the entity
     * @param publicId The nominal public identifier for the entity
     *
     * @return The mapped value, or <code>null</code> if no matching entry is found.
     */
    public CatalogResult lookupNotation(String notName, String systemId, String publicId) {
        logger.fine("lookupNotation(" + notName + "," + publicId + "," + systemId + ")");

        systemId = URIUtils.normalizeURI(systemId);

        if (publicId != null && publicId.startsWith("urn:publicid:")) {
            publicId = PublicId.decodeURN(publicId);
        }

        if (systemId != null && systemId.startsWith("urn:publicid:")) {
            systemId = PublicId.decodeURN(systemId);
            if (publicId != null && !publicId.equals(systemId)) {
                logger.warning("urn:publicid: system identifier differs from public identifier; using public identifier");
                systemId = null;
            } else {
                publicId = systemId;
                systemId = null;
            }
        }

        int index = 0;
        while (index < catalogList.size()) {
            loadCatalog(index);
            Document doc = documentList.get(index);
            if (doc != null) {
                logger.finer("  Looking in " + doc.getBaseURI());
                CatalogResult resolved = lookupNotation(doc.getDocumentElement(), notName, systemId, publicId);
                if (resolved != null) {
                    logger.fine("  Found: " + resolved);
                    return resolved;
                }
            }
            index++;
        }

        logger.fine("  Not found");
        return null;
    }
  
    protected CatalogResult lookupNotation(Element group, String notName, String systemId, String publicId) {
        CatalogResult resolved = null;
      
        if (systemId != null) {
            // If there's a SYSTEM entry in this catalog, use it
            resolved = lookupLocalSystem(group, systemId);
            if (resolved != null) {
                return resolved;
            }
        }

        if (publicId != null) {
            // If there's a PUBLIC entry in this catalog, use it
            resolved = lookupLocalPublic(group, systemId, publicId);
            if (resolved != null) {
                return resolved;
            }
        }

        // If there's a NOTATION entry in this catalog, use it
        for (Element child : entries(group, "notation", "name", notName, null, null)) {
            // What's the prefer setting for this entry?
            boolean preferpublic = conf.queryPreferPublic();
            Node node = child;
            while (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                Element p = (Element) node;
                if (p.hasAttribute("prefer")) {
                    preferpublic = "public".equals(p.getAttribute("prefer"));
                    node = null;
                } else {
                    node = node.getParentNode();
                }
            }

            if (preferpublic || systemId == null) {
                String localURI = DOMUtils.makeAbsolute(child, child.getAttribute("uri"));
                String origURI = systemId;
                if (origURI == null) {
                    origURI = localURI;
                }
                return new CatalogResult(origURI, localURI, child, cache);
            }
        }

        return null;
    }
    
    public synchronized void addSource(CatalogSource aCatalogSource) {
      catalogList.add(aCatalogSource);
    }

    private static Vector<CatalogSource> createSources(Configuration aConf, String aCatalogFiles) {
        Vector<CatalogSource> res = new Vector<CatalogSource>();
        if (aCatalogFiles == null) {
            for (String s: aConf.queryCatalogFiles()) {
                res.add(new CatalogSource.UriCatalogSource(s));
            }
        } else {
            for (String s: aCatalogFiles.split(";")) {
                if (!"".equals(s)) res.add(new CatalogSource.UriCatalogSource(s));
            }
        }
        return res;
    }
    
    private static Vector<CatalogSource> toCatalogSources(Vector<String> aCatalogFiles) {
      Vector<CatalogSource> res = new Vector<CatalogSource>();
      for (String s: aCatalogFiles) {
        res.add(new CatalogSource.UriCatalogSource(s));
      }
      return res;
    }
}
