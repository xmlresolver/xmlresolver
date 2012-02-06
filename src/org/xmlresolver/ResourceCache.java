/*
 * ResourceCache.java
 *
 * Created on December 30, 2006, 1:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;
import org.xmlresolver.helpers.DOMUtils;

/** Implements a cache for web resources.
 * 
 * <p>Resources added to the cache are stored locally. Access to the cache is
 * through an OASIS XML Catalog returned by the <code>catalog()</code> method.
 * After a catalog search that includes the cache catalog, you can test if the
 * local copy of the resource is expired. If it is, it will be removed from
 * the cache.</p>
 * 
 * <div>
 * <h2>Implementation details</h2>
 * 
 * <p>The underlying cache is implemented using the filesystem. A cache directory
 * must be given when the cache is created. The <code>ResourceCache</code> must
 * have write access to the directory. The directory specified should be used
 * <em>exclusively</em> for the cache in order to avoid filename collisions and
 * other errors.</p>
 * 
 * <p>The cache uses three subdirectories and two files:</p>
 * 
 * <ul>
 * <li>Catalog entries for files in the cache are stored in the
 * <code>entry</code> directory.</li>
 * <li>The actual resources are stored in the <code>data</code>
 * directory.</li>
 * <li>Expired entries are stored in <code>expired</code>.</li>
 * <li>The <code>lock</code> file is used to maintain synchronized access
 * to the cache directory (multiple threads and even multiple applications
 * running perhaps in different VMs can share the same cache directory).
 * </li>
 * <li>The <code>control.xml</code> file can be used to configure the
 * cache.</li>
 * </ul>
 * 
 * <p>When a resource is added to the cache, the data is stored in the
 * <code>data</code> directory
 * and a catalog entry is created in the <code>entry</code> directory.
 * After the entry has been added, the next request for the catalog will
 * include this entry.</p>
 * 
 * <p>When a resource expires, its catalog entry file is moved to the
 * <code>expired</code> directory. The actual data is not removed right away
 * because other caches may still have that entry in their catalog.</p>
 * 
 * <p>During catalog initialization, all of the the expired entries are scanned.
 * If any of them are older than a "delete wait" threshold, they are deleted
 * along with their data.</p>
 * 
 * <p>The cache control file contains caching parameters:</p>
 * 
 * <pre>&lt;cache-control xmlns="http://xmlresolver.org/ns/cache"
 * 	       max-age="86400" delete-wait="86400"
 * 	       size="1500" space="10m">
 * &lt;cache uri="http://www.w3.org/" max-age="172800" space="500k"/>
 * &lt;no-cache uri="http://localhost/"/>
 * &lt;cache uri="http://www.flickr.com/" max-age="0"/>
 * &lt;cache uri="http://flickr.com/" max-age="0"/>
 * &lt;/cache-control></pre>
 *
 * <p>If definitive information about the age of a resource cannot be
 * determined and it is older than "max-age", it will be treated as
 * out-of-date and its cached entry will be updated.</p>
 *
 * <p>Entries in the cache are sorted by age. If the cache exceeds "size"
 * entries or "space" bytes of storage, then the oldest entries are deleted.</p>
 *
 * <p>The <code>cache</code> and <code>no-cache</code> elements match URIs
 * by regular expression. If a <code>no-cache</code> entry matches, then the
 * URI is not cached. If no entry matches, the default is to cache with the
 * parameters specified on the <code>cache-control</code> element. Any paramters
 * not specified are inherited from the preceding <code>cache</code> element.</p>
 * </div>
 *
 * @author ndw
 */
public class ResourceCache {
    /** The XML Namespace name of XML Resolver cache file, "http://xmlresolver.org/ns/cache". */
    public static final String NS_CACHE = "http://xmlresolver.org/ns/cache";

    private static Logger logger = Logger.getLogger(ResourceCache.class.getName());
    private static GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    private static GregorianCalendar now = new GregorianCalendar();
    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder builder = null;
    
    private Document cache = null;
    private Element catalog = null;
    private File cacheDir = null;
    private File dataDir = null;
    private File entryDir = null;
    private File expiredDir = null;
    private CacheInfo defaultCacheInfo = null;
    private Vector<CacheInfo> cacheInfo = new Vector<CacheInfo> ();
    private long deleteWait = 60*60*24*7; // 1 week
    private long cacheSize = 1000;
    private long cacheSpace = 10240;
    private long maxAge = -1;
    
    /** Creates a new instance of ResourceCache.
     *
     * <p>Creates a new instance of the ResourceCache using the specified directory for the cache.
     * In order to succeed, the user must have write access to that directory.</p>
     *
     * @param dir The name of a directory to use for the cache.
     */
    public ResourceCache(String dir) {
        init(dir);
    }
    
    private void init(String dir) {
        synchronized (dbFactory) {
            if (builder == null) {
                dbFactory.setNamespaceAware(true);
                dbFactory.setValidating(false);

                try {
                    builder = dbFactory.newDocumentBuilder();
                } catch (ParserConfigurationException pce) {
                    throw new UnsupportedOperationException(pce);
                }
            }
        }

        // In case there is no control.xml file...
        defaultCacheInfo = new CacheInfo(".*", true, deleteWait, cacheSize, cacheSpace, maxAge);

        try {
            FileInputStream controlFile = new FileInputStream(dir + "/control.xml");
            Document cacheControl = builder.parse(controlFile);
            Element control = cacheControl.getDocumentElement();
            if (NS_CACHE.equals(control.getNamespaceURI()) && "cache-control".equals(control.getLocalName())) {
                deleteWait = parseTimeLong(control, "delete-wait", deleteWait);
                cacheSize = parseLong(control, "size", cacheSize);
                cacheSpace = parseSizeLong(control, "space", cacheSpace);
                maxAge = parseTimeLong(control, "max-age", maxAge);
                
                defaultCacheInfo = new CacheInfo(".*", true, deleteWait, cacheSize, cacheSpace, maxAge);

                Element child = DOMUtils.getFirstElement(control);
                while (child != null) {
                    if ("cache".equals(child.getLocalName())) {
                        CacheInfo info = new CacheInfo(child.getAttribute("uri"), true,
                                parseLong(child,"delete-wait", deleteWait),
                                parseLong(child,"size", cacheSize),
                                parseSizeLong(child,"space", cacheSpace),
                                parseLong(child,"max-age",maxAge));
                        cacheInfo.add(info);
                    } else if ("no-cache".equals(child.getLocalName())) {
                        CacheInfo info = new CacheInfo(child.getAttribute("uri"), false,
                                parseLong(child,"delete-wait", deleteWait),
                                parseLong(child,"size", cacheSize),
                                parseSizeLong(child,"space", cacheSpace),
                                parseLong(child,"max-age",maxAge));
                        cacheInfo.add(info);
                    }
                
                    child = DOMUtils.getNextElement(child);
                }
            }
        } catch (SAXException ex) {
            // nop;
        } catch (FileNotFoundException ex) {
            // nop;
        } catch (IOException ex) {
            // nop;
        }
        
        File fDir = new File(dir);
        try {
            cacheDir = fDir.getCanonicalFile();
            logger.fine("Cache: " + cacheDir);
        } catch (IOException ioe) {
            cacheDir = null;
        }

    }

    private long parseLong(Element node, String attr, long defVal) {
        if (!node.hasAttribute(attr)) {
            return defVal;
        }
        
        try {
            long val = Long.parseLong(node.getAttribute(attr));
            return val;
        } catch (NumberFormatException nfe) {
            logger.warning("Bad numeric value in cache control file: " + node.getAttribute(attr));
            return defVal;
        }
    }

    private long parseSizeLong(Element node, String attr, long defVal) {
        if (!node.hasAttribute(attr)) {
            return defVal;
        }

        String longStr = node.getAttribute(attr);
        long units = 1;
        if (Pattern.matches("^[0-9]+[kK]$", longStr)) {
            units = 1024;
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (Pattern.matches("^[0-9]+[mM]$", longStr)) {
            units = 1024*1000;
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (Pattern.matches("^[0-9]+[gG]$", longStr)) {
            units = 1024*1000*1000;
            longStr = longStr.substring(0,longStr.length()-1);
        }
        
        try {
            long val = Long.parseLong(longStr);
            return val * units;
        } catch (NumberFormatException nfe) {
            logger.warning("Bad numeric value in cache control file: " + longStr);
            return defVal;
        }
    }

    private long parseTimeLong(Element node, String attr, long defVal) {
        if (!node.hasAttribute(attr)) {
            return defVal;
        }

        String longStr = node.getAttribute(attr);
        long units = 1;
        if (Pattern.matches("^[0-9]+[sS]$", longStr)) {
            units = 1;
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (Pattern.matches("^[0-9]+[mM]$", longStr)) {
            units = 60;
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (Pattern.matches("^[0-9]+[hH]$", longStr)) {
            units = 3600;
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (Pattern.matches("^[0-9]+[dD]$", longStr)) {
            units = 3600*24;
            longStr = longStr.substring(0,longStr.length()-1);
        } else if (Pattern.matches("^[0-9]+[wW]$", longStr)) {
            units = 3600*24*7;
            longStr = longStr.substring(0,longStr.length()-1);
        }

        try {
            long val = Long.parseLong(longStr);
            return val * units;
        } catch (NumberFormatException nfe) {
            logger.warning("Bad numeric value in cache control file: " + longStr);
            return defVal;
        }
    }

    /** Returns an OASIS XML Catalog document for the resources in the cache. */
    public synchronized Element catalog() {
        if (!cacheDir.exists() || !cacheDir.isDirectory()) {
            return null;
        }

        if (cache == null) {
            loadCatalog();
            cleanupCache();
        }

        return catalog;
    }

    private synchronized void cleanupCache() {
        DirectoryLock lock = new DirectoryLock();
        if (!lock.locked()) {
            return;
        }

        // if there are any expired entries that are too old, remove them
        File files[] = expiredDir.listFiles();
        for (int pos = 0; pos < files.length; pos++) {
            long age = calendar.getTimeInMillis() - files[pos].lastModified();
            if (age > deleteWait * 1000) {
                logger.info("Deleting expired entry: " + files[pos].getName());
                files[pos].delete();
            }
        }

        // if there are any files in the data dir that don't have an entry, remove them
        files = dataDir.listFiles();
        for (int pos = 0; pos < files.length; pos++) {
            String entryName = files[pos].getName();
            entryName = entryName.substring(0,entryName.lastIndexOf("."));
            File entry = new File(entryDir + "/" + entryName + ".xml");
            if (!entry.exists()) {
                entry = new File(expiredDir + "/" + entryName + ".xml");
                if (!entry.exists()) {
                    logger.info("Deleting expired data: " + files[pos].getName());
                    files[pos].delete();
                }
            }
        }

        // if there are any entries that don't have data associated with them, remove them
        files = entryDir.listFiles();
        for (int pos = 0; pos < files.length; pos++) {
            String entryName = files[pos].getName();
            entryName = entryName.substring(0,entryName.lastIndexOf("."));

            boolean found = false;
            File dfiles[] = dataDir.listFiles();
            for (int dpos = 0; !found && dpos < dfiles.length; dpos++) {
                String dataName = dfiles[dpos].getName();
                dataName = dataName.substring(0,dataName.lastIndexOf("."));
                found = dataName.equals(entryName);
            }

            if (!found) {
                logger.info("Deleting dangling entry: " + files[pos].getName());
                files[pos].delete();
            }
        }

        lock.unlock();
    }
    
    private synchronized void loadCatalog() {
        cache = builder.newDocument();
        catalog = cache.createElementNS(Catalog.NS_CATALOG, "catalog");
        cache.appendChild(catalog);

        dataDir = new File(cacheDir.toString()+"/data");
        entryDir = new File(cacheDir.toString()+"/entry");
        expiredDir = new File(cacheDir.toString()+"/expired");

        if ((!dataDir.exists() && !dataDir.mkdir())
            || (!entryDir.exists() && !entryDir.mkdir())
            || (!expiredDir.exists() && !expiredDir.mkdir())) {
            return;
        }

        DirectoryLock lock = new DirectoryLock();
        if (!lock.locked()) {
            return;
        }
        
        File files[] = dataDir.listFiles();
        for (int pos = 0; pos < files.length; pos++) {
            if (files[pos].canRead()) {
                String name = files[pos].getName();
                long lastModified = files[pos].lastModified();
                name = name.substring(0, name.lastIndexOf("."))+".xml";
                File entryFile = new File(entryDir.toString()+"/"+name);
                if (entryFile.exists() && entryFile.canRead()) {
                    try {
                        Document dom = builder.parse(entryFile.toString());
                        Element root = dom.getDocumentElement();
                        if (DOMUtils.catalogElement(root, "catalog")) {
                            Element cEntry = DOMUtils.getFirstElement(root);
                            while (cEntry != null) {
                                Element entry = (Element) cache.importNode(cEntry, true);
                                insertSorted(catalog, entry);
                                cEntry = DOMUtils.getNextElement(cEntry);
                            }
                        } else {
                            Element entry = (Element) cache.importNode(root, true);
                            insertSorted(catalog, entry);
                        }
                    } catch (SAXException se) {
                        // nop;
                    } catch (IOException ioe) {
                        // nop;
                    }
                }
            }
        }
        
        lock.unlock();
    }

    private void insertSorted(Element catalog, Element entry) {
        Element node = DOMUtils.getFirstElement(catalog);
        if (node == null) {
            catalog.appendChild(entry);
            return;
        }
        
        long cacheTime = -1;
        if (entry.hasAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "time")) {
            cacheTime = Long.parseLong(entry.getAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "time"));
        }

        long nodeTime = -1;
        if (node.hasAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "time")) {
            nodeTime = Long.parseLong(node.getAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "time"));
        }

        while (node != null && nodeTime >= cacheTime) {
            node = DOMUtils.getNextElement(node);
            if (node != null && node.hasAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "time")) {
                nodeTime = Long.parseLong(node.getAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "time"));
            }
        }

        // Ok, if we have a node, we need to go before it, otherwise we go at the end
        if (node == null) {
            catalog.appendChild(entry);
        } else {
            catalog.insertBefore(entry, node);
        }
    }
    
    /** Add a URI to the cache.
     *
     * <p>This method reads the supplied InputStream, storing the resource locally, and returns the name
     * of the local file where that body may be retrieved.</p>
     *
     * @param connection The connection.
     *
     * @return The filename of the cached resource.
     */
    public String addURI(ResourceConnection connection) throws IOException {
        logger.info("Caching URI: " + connection.getURI());
        return _addNamespaceURI(connection, null, null);
    }

    /** Add a Namespace URI to the cache.
     *
     * <p>This method reads the supplied InputStream, storing the resource locally, and returns the name
     * of the local file where that body may be retrieved.</p>
     *
     * @param connection The URL connection.
     * @param nature The RDDL nature of the resource.
     * @param purpose the RDDL purpose of the resource.
     *
     * @return The filename of the cached resource.
     */
    public synchronized String addNamespaceURI(ResourceConnection connection, String nature, String purpose) throws IOException {
        String name = connection.getURI();
        logger.info("Caching resource for namespace: " + name + " (nature: " + nature + "; purpose: " + purpose + ")");
        return _addNamespaceURI(connection, nature, purpose);
    }

    private synchronized String _addNamespaceURI(ResourceConnection connection, String nature, String purpose) throws IOException {
        if (cache == null) {
            loadCatalog();
            if (cache == null) {
                throw new UnsupportedOperationException("No underlying cache");
            }
        }

        String name = connection.getURI();
        String contentType = connection.getContentType();
        InputStream resource = connection.getStream();
        
        logger.info("Caching uri: " + name);

        DirectoryLock lock = new DirectoryLock();
        if (!lock.locked()) {
            return null;
        }

        String uri = null;
        File localFile = null;

        try {
            // Make addNamespaceURI() idempotent
            Element entry = DOMUtils.getFirstElement(catalog);
            while (entry != null && localFile == null) {
                if (DOMUtils.catalogElement(entry, "uri")) {
                    String eName = DOMUtils.attr(entry, "name");
                    String eUri = DOMUtils.attr(entry, "uri");
                    String eNature = DOMUtils.attr(entry, "nature");
                    String ePurpose = DOMUtils.attr(entry, "purpose");
                    String eRedir = DOMUtils.attr(entry, "redir");

                    if ((eName != null && eName.equals(name))
                            && ((eNature == null && nature == null)
                            || (eNature != null && eNature.equals(nature)))
                            && ((ePurpose == null && purpose == null)
                            || (ePurpose != null && ePurpose.equals(purpose)))
                            && eUri != null) {
                        localFile = new File(eUri);
                        uri = eRedir == null ? eUri : eRedir;
                    }
                }
                entry = DOMUtils.getNextElement(entry);
            }

            if (localFile == null) {
                String suffix = pickSuffix(name, contentType);
                localFile = File.createTempFile("xrc", suffix, dataDir);
                uri = localFile.getPath();
            }

            FileOutputStream fos = new FileOutputStream(localFile);
            
            // Now copy the input stream into our cache copy
            byte[] buf = new byte[4096];
            int read = resource.read(buf);
            while (read > 0) {
                fos.write(buf, 0, read);
                read = resource.read(buf);
            }
            fos.close();
            resource.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            lock.unlock();
            throw new UnsupportedOperationException("Cannot create file in cache directory");
        }
        
        Element root = cache.getDocumentElement();
        Element newuri = cache.createElementNS(Catalog.NS_CATALOG, "uri");
        newuri.setAttribute("name", name);
        newuri.setAttribute("uri", uri);
        if (nature != null) {
            newuri.setAttributeNS(Catalog.NS_RDDL, "nature", nature);
        }
        if (purpose != null) {
            newuri.setAttributeNS(Catalog.NS_RDDL, "purpose", purpose);
        }
        if (contentType != null) {
            newuri.setAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "xr:contentType", contentType);
        }
        
        String redirName = connection.getRedirect();
        if (redirName != null) {
            newuri.setAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "xr:redir", redirName);
        }
        newuri.setAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "xr:time", "" + calendar.getTimeInMillis());

        String etag = connection.getEtag();
        if (etag != null) {
            newuri.setAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "xr:etag", etag);
        }

        root.appendChild(cache.createTextNode("\n"));
        root.appendChild(newuri);
        root.appendChild(cache.createTextNode("\n"));

        try {
            Document wrapper = builder.newDocument();
            wrapper.appendChild(wrapper.importNode(newuri, true));
            
            String entryfn = localFile.getName();
            entryfn = entryfn.substring(0,entryfn.lastIndexOf("."));
            File entryFile = new File(entryDir.toString() + "/" + entryfn + ".xml");
            
            FileOutputStream fos = new FileOutputStream(entryFile);
            DOMImplementationLS domimpl = (DOMImplementationLS) cache.getImplementation();
            LSSerializer serializer = domimpl.createLSSerializer();
            LSOutput output = domimpl.createLSOutput();
            output.setEncoding("utf-8");
            output.setByteStream(fos);
            serializer.write(wrapper, output);
            fos.close();
        } catch (IOException ioe) {
            lock.unlock();
            throw new UnsupportedOperationException("Failed to create entry file");
        }

        lock.unlock();
        
        return uri;
    }
    
    /** Add a system identifier to the cache.
     *
     * <p>This method reads the supplied InputStream, storing the resource locally, and returns the name
     * of the local file where that body may be retrieved.</p>
     *
     * @param connection The URL connection.
     * @param publicId The public identifier associated with the resource.
     *
     * @return The filename of the cached resource.
     */
    public String addSystem(ResourceConnection connection, String publicId) throws IOException {
        if (cache == null) {
            loadCatalog();
            if (cache == null) {
                throw new UnsupportedOperationException("No underlying cache");
            }
        }

        String contentType = connection.getContentType();
        InputStream resource = connection.getStream();
        String systemId = connection.getURI();

        logger.info("Caching system identifier: " + systemId);

        DirectoryLock lock = new DirectoryLock();
        if (!lock.locked()) {
            return null;
        }

        String uri = null;
        File localFile = null;

        try {
            // Make addSystem() idempotent
            Element entry = DOMUtils.getFirstElement(catalog);
            while (entry != null && localFile == null) {
                if (DOMUtils.catalogElement(entry, "system")) {
                    String eName = DOMUtils.attr(entry, "systemId");
                    String eUri = DOMUtils.attr(entry, "uri");

                    if (eName != null && eName.equals(systemId) && eUri != null) {
                        localFile = new File(eUri);
                        uri = eUri;
                    }
                }
                entry = DOMUtils.getNextElement(entry);
            }

            if (localFile == null) {
                String suffix = pickSuffix(systemId, contentType);
                localFile = File.createTempFile("xrc", suffix, dataDir);
                uri = localFile.getPath();
            }

            FileOutputStream fos = new FileOutputStream(localFile);
            
            // Now copy the input stream into our cache copy
            byte[] buf = new byte[4096];
            int read = resource.read(buf);
            while (read > 0) {
                fos.write(buf, 0, read);
                read = resource.read(buf);
            }
            fos.close();
            resource.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            lock.unlock();
            throw new UnsupportedOperationException("Cannot create file in cache directory");
        }
        
        Element root = cache.getDocumentElement();
        long curTime = calendar.getTimeInMillis();

        Element newuri = cache.createElementNS(Catalog.NS_CATALOG, "system");
        newuri.setAttribute("systemId", systemId);
        newuri.setAttribute("uri", uri);
        if (contentType != null) {
            newuri.setAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "xr:contentType", contentType);
        }
        newuri.setAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "xr:time", "" + curTime);
        
        String etag = connection.getEtag();
        if (etag != null) {
            newuri.setAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "xr:etag", etag);
        }
 
        Element newpub = null;
        if (publicId != null) {
            newpub = cache.createElementNS(Catalog.NS_CATALOG, "public");
            newpub.setAttribute("publicId", publicId);
            newpub.setAttribute("uri", uri);
            if (contentType != null) {
                newpub.setAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "xr:contentType", contentType);
            }
            newpub.setAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "xr:time", "" + curTime);
            newpub.setAttributeNS(Catalog.NS_CATALOG, "xr:systemId", systemId);
        }
        
        root.appendChild(cache.createTextNode("\n"));
        root.appendChild(newuri);
        root.appendChild(cache.createTextNode("\n"));
        if (newpub != null) {
            root.appendChild(newpub);
            root.appendChild(cache.createTextNode("\n"));
        }
        
        try {
            Document wrapper = builder.newDocument();
            Element wcat = wrapper.createElementNS(Catalog.NS_CATALOG, "catalog");
            wrapper.appendChild(wcat);
            wcat.appendChild(wrapper.createTextNode("\n"));

            wcat.appendChild(wrapper.importNode(newuri, true));
            wcat.appendChild(wrapper.createTextNode("\n"));
            if (newpub != null) {
                wcat.appendChild(wrapper.importNode(newpub, true));
                wcat.appendChild(wrapper.createTextNode("\n"));
            }
            
            String entryfn = localFile.getName();
            entryfn = entryfn.substring(0,entryfn.lastIndexOf("."));
            File entryFile = new File(entryDir.toString() + "/" + entryfn + ".xml");
            
            FileOutputStream fos = new FileOutputStream(entryFile);
            DOMImplementationLS domimpl = (DOMImplementationLS) cache.getImplementation();
            LSSerializer serializer = domimpl.createLSSerializer();
            LSOutput output = domimpl.createLSOutput();
            output.setEncoding("utf-8");
            output.setByteStream(fos);
            serializer.write(wrapper, output);
            fos.close();
        } catch (IOException ioe) {
            lock.unlock();
            throw new UnsupportedOperationException("Failed to create entry file");
        }

        lock.unlock();
        
        return uri;
    }

    /** Returns true if the specified absolute URI should be cached.
     */
    public boolean cacheURI(String uri) {
        // Find the cache info record for this entry
        
        CacheInfo info = null;
        for (int count = 0; info == null && count < cacheInfo.size(); count++) {
            CacheInfo chk = cacheInfo.get(count);
            if (Pattern.matches(chk.pattern() + ".*", uri)) {
                info = chk;
            }
        }

        if (info == null) {
            info = defaultCacheInfo;
        }
         
        if (!info.cache()) {
            logger.info("Not caching: " + uri);
        }
        return info.cache();
    }

    /** Attempts to determine if the local copy is out of date.
     *
     * <p>If the URI is an <code>http:</code> URI, a HEAD request is made and the
     * <code>cachedTime()</code> is compared to the last modified time. If the resource
     * on the web is more recent, <code>true</code> is returned.</p>
     *
     * <p>If the resource isn't cached, isn't an <code>http:</code> URI, or an error
     * occurs attempting to get the HEAD, then <code>false</code> is returned.</p>
     *
     * @param origURI The original URI.
     * @param uri The URI of the local resource.
     * @param entry The catalog entry that was the source of the URI.
     *
     * @return True if and only if the resource is expired.
     */
    public boolean expired(String origURI, String uri, Element entry) {
        // If we're explicitly offline, then nothing is expired.
        String offline = System.getProperty("xmlresolver.offline");
        if (offline != null) {
            if ("true".equals(offline) || "1".equals(offline) || "yes".equals(offline)) {
                return false;
            }
        }

        // Find the cache info record for this entry
        CacheInfo info = null;
        for (int count = 0; info == null && count < cacheInfo.size(); count++) {
            CacheInfo chk = cacheInfo.get(count);
            if (Pattern.matches(chk.pattern() + ".*", origURI)) {
                info = chk;
            }
        }

        if (info == null) {
            info = defaultCacheInfo;
        }

        // If this entry isn't supposed to be cached, then it's definitely expired.
        if (!info.cache()) {
            return true;
        }

        // Find out how many matching entries are in the cache and how much space they occupy
        Pattern uriPattern = Pattern.compile(info.pattern() + ".*");
        int cacheCount = 0;
        long cacheSize = 0;
        long cacheTime = -1;
        String cachedEtag = null;
        boolean found = false;
        Element node = DOMUtils.getFirstElement(catalog);
        while (node != null) {
            found = found || (node == entry);
            String srcuri = null;
            if (node.hasAttribute("publicId")) {
                // They must also have a system entry; skip the public one so we don't count it twice
                node = DOMUtils.getNextElement(node);
                continue;
            }

            if (node.hasAttribute("systemId")) {
                srcuri = node.getAttribute("systemId");
            } else {
                srcuri = node.getAttribute("name");
            }
            
            if (uriPattern.matcher(srcuri).matches()) {
                cacheCount++;
                File lclFile = new File(node.getAttribute("uri"));
                cacheSize += lclFile.length();
            }
            node = DOMUtils.getNextElement(node);
        }

        // If this entry isn't in the catalog, it's not expired. This should never happen.
        if (!found) {
            return false;
        }

        // Flush oldest entries...
        if (cacheCount > info.cacheSize() || cacheSize > info.cacheSpace()) {
            flushCache(uriPattern, info.cacheSize(), info.cacheSpace());
        }
        
        cacheTime = -1;
        if (entry.hasAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "time")) {
            cacheTime = Long.parseLong(entry.getAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "time"));
        }
        
        if (entry.hasAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "etag")) {
            cachedEtag = entry.getAttributeNS(Catalog.NS_XMLRESOURCE_EXT, "etag");
        }

        String etag = null;
        long lastModified = 0;
        try {
            URL url = new URL(origURI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            lastModified = conn.getLastModified();
            etag = conn.getHeaderField("etag");
            if (etag != null && "".equals(etag)) { etag = null; }
            if (lastModified == 0 && (etag == null || cachedEtag == null)) {
                // Hmm. We're not sure when it was last modified?
                long maxAge = info.maxAge();
                if (maxAge >= 0) {
                    long oldest = now.getTimeInMillis() - (maxAge * 1000);
                    if (maxAge == 0 || cacheTime < oldest) {
                        return true;
                    }
                }

                lastModified = conn.getDate();
            }
            
            if (conn.getResponseCode() != 200) {
                logger.fine("Not expired: " + origURI + " (HTTP " + conn.getResponseCode() + ")");
                return false;
            }
        } catch (MalformedURLException mue) {
            logger.fine("Not expired: " + origURI + " (MalformedURLException)");
            return false;
        } catch (IOException ioe) {
            logger.fine("Not expired: " + origURI + " (IOException)");
            return false;
        }

        boolean etagsDiffer = (etag != null && cachedEtag != null && !etag.equals(cachedEtag));
        
        if (lastModified == 0) {
            logger.fine("Expired: " + origURI + " (no last-modified header)");
            return true;
        } else if (lastModified > cacheTime || etagsDiffer) {
            logger.fine("Expired: " + origURI);
            expire(uri);
            return true;
        } else {
            logger.fine("Not expired: " + origURI);
            return false;
        }
    }

    private synchronized void flushCache(Pattern uriPattern, long maxCount, long maxSize) {
        int cacheCount = 0;
        long cacheSize = 0;
        Vector<String> expired = new Vector<String>();
        
        // N.B. The entries in the cache are sorted in reverse date order, so as soon as we pass
        // a threshold, we burn everything that follows...
        Element node = DOMUtils.getFirstElement(catalog);
        while (node != null) {
            String srcuri = null;
            if (node.hasAttribute("publicId")) {
                // They must also have a system entry; skip the public one so we don't count it twice
                node = DOMUtils.getNextElement(node);
                continue;
            }

            if (node.hasAttribute("systemId")) {
                srcuri = node.getAttribute("systemId");
            } else {
                srcuri = node.getAttribute("name");
            }

            if (uriPattern.matcher(srcuri).matches()) {
                cacheCount++;
                File lclFile = new File(node.getAttribute("uri"));
                cacheSize += lclFile.length();

                if (cacheCount > maxCount || cacheSize > maxSize) {
                    expired.add(node.getAttribute("uri"));
                }
            }
            node = DOMUtils.getNextElement(node);
        }
        
        expire(expired);
    }

    private synchronized void expire(String localFile) {
        Vector<String> expired = new Vector<String>();
        expired.add(localFile);
        expire(expired);
    }

    private synchronized void expire(Vector<String> expired) {
        DirectoryLock lock = new DirectoryLock();
        if (!lock.locked()) {
            return;
        }

        for (String localFile : expired) {
            String name = localFile;
            int pos = name.lastIndexOf("/");
            if (pos >= 0) {
                name = name.substring(pos+1);
            }
            pos = name.lastIndexOf(".");
            if (pos >= 0) {
                name = name.substring(0, pos);
            }

            logger.finer("Expiring: " + name);

            File entry = new File(entryDir + "/" + name + ".xml");
            if (entry.exists() && entry.isFile()) {
                File renamed = new File(expiredDir + "/" + name + ".xml");
                entry.renameTo(renamed);
                renamed.setLastModified(calendar.getTimeInMillis());
            }
        }

        cache = null; // Force the catalog to be reloaded...
        lock.unlock();
    }
    
    private String pickSuffix(String uri, String contentType) {
        if (contentType == null) {
            return ".bin";
        }

        if (contentType.contains("application/xml")) {
            return ".xml";
        }

        if (contentType.contains("text/html") || contentType.contains("application/html+xml")) {
            return ".html";
        }

        if (contentType.contains("text/plain")) {
            if (uri.endsWith(".dtd")) {
                return ".dtd";
            }
            return ".txt";
        }

        return ".bin";
    }
    
    class DirectoryLock {
        private File lockF = null;
        private RandomAccessFile lockFile = null;
        private FileChannel lockChannel = null;
        private FileLock lock = null;
        private boolean locked = false;
        
        public DirectoryLock() {
            try {
                lockF = new File(cacheDir.toString()+"/lock");
                lockFile = new RandomAccessFile(lockF, "rw");
                lockChannel = lockFile.getChannel();
                lock = lockChannel.lock();
                locked = true;
            } catch (IOException ioe) {
                // nop;
            }
        }

        public boolean locked() {
            return locked;
        }

        public void unlock() {
            try {
                lock.release();
                locked = false;
                lockFile.close();
            } catch (IOException ioe) {
                // nop;
            }
        }
    }
    
    class CacheInfo {
        private boolean cache = true;
        private String pattern = "";
        private long deleteWait = -1;
        private long cacheSize = -1;
        private long cacheSpace = -1;
        private long maxAge = -1;

        public CacheInfo(String uriPattern, boolean cache, long deleteWait, long cacheSize, long cacheSpace, long maxAge) {
            this.pattern = uriPattern;
            this.cache = cache;
            this.deleteWait = deleteWait;
            this.cacheSize = cacheSize;
            this.cacheSpace = cacheSpace;
            this.maxAge = maxAge;
        }
        
        public String pattern() {
            return pattern;
        }

        public boolean cache() {
            return cache;
        }
        
        public long deleteWait() {
            return deleteWait;
        }
        
        public long cacheSize() {
            return cacheSize;
        }

        public long cacheSpace() {
            return cacheSpace;
        }
        
        public long maxAge() {
            return maxAge;
        }
    }
}
