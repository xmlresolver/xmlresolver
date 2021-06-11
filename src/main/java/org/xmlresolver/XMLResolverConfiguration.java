package org.xmlresolver;

import org.xml.sax.InputSource;
import org.xmlresolver.cache.ResourceCache;
import org.xmlresolver.utils.URIUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Configures an XML resolver.
 *
 * <p>Many aspects of catalog processing can be configured. This
 * class examines both system properties and the properties specified in
 * a separate properties file. The initial list of catalog files can be provided
 * as a property or directly when the configuration is
 * created.</p>
 *
 * <p>The following table lays out the features recognized by this class
 * and the system properties and configuration file properties that can be
 * used to specify them.</p>
 *
 * <table>
 * <caption>Resolver features and properties that set them</caption>
 * <thead>
 * <tr>
 * <th>Feature</th>
 * <th>System property</th>
 * <th>File property</th>
 * <th>Type</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr><th>{@link ResolverFeature#ALLOW_CATALOG_PI}</th>
 * <td>xml.catalog.allowPI</td>
 * <td>allow-oasis-xml-catalog-pi</td>
 * <td>Boolean¹</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#CACHE_DIRECTORY}</th>
 * <td>xml.catalog.cache</td>
 * <td>cache</td>
 * <td>String</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#CACHE_UNDER_HOME}</th>
 * <td>xml.catalog.cacheUnderHome</td>
 * <td>cache-under-home</td>
 * <td>Boolean¹</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#CATALOG_ADDITIONS}</th>
 * <td>xml.catalog.additions</td>
 * <td>catalog-additions</td>
 * <td>List of strings²</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#CATALOG_FILES}</th>
 * <td>xml.catalog.files</td>
 * <td>catalogs</td>
 * <td>List of strings²</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#CATALOG_LOADER_CLASS}</th>
 * <td>xml.catalog.catalogLoaderClass</td>
 * <td>catalog-loader-class</td>
 * <td>String</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#CLASSPATH_CATALOGS}</th>
 * <td>xml.catalog.classpathCatalogs</td>
 * <td>classpath-catalogs</td>
 * <td>Boolean¹</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#MASK_JAR_URIS}</th>
 * <td>xml.catalog.maskJarUris</td>
 * <td>mask-jar-uris</td>
 * <td>Boolean¹</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#MERGE_HTTPS}</th>
 * <td>xml.catalog.mergeHttps</td>
 * <td>merge-https</td>
 * <td>Boolean¹</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#PARSE_RDDL}</th>
 * <td>xml.catalog.parseRddl</td>
 * <td>parse-rddl</td>
 * <td>Boolean¹</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#PREFER_PROPERTY_FILE}</th>
 * <td>xml.catalog.preferPropertyFile</td>
 * <td>prefer-property-file</td>
 * <td>Boolean¹</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#PREFER_PUBLIC}</th>
 * <td>xml.catalog.prefer</td>
 * <td>prefer</td>
 * <td>“<code>public</code>” or “<code>system</code>”³</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#URI_FOR_SYSTEM}</th>
 * <td>xml.catalog.uriForSystem</td>
 * <td>uri-for-system</td>
 * <td>Boolean¹</td>
 * </tr>
 * </tbody>
 * <tfoot>
 * <tr>
 * <td colspan=”4”>¹ Any of “true”, “yes”, or “1” is true; everything else is false.<td>
 * </tr>
 * <tr>
 * <td colspan=”4”>² The list of strings is semicolon delimited<td>
 * </tr>
 * <tr>
 * <td colspan=”4”>³ Public is preferred if the value is “public”, any other value
 * is equivalent to “system”.</td>
 * </tr>
 * </tfoot>
 * </table>
 */

public class XMLResolverConfiguration implements ResolverConfiguration {
    private static final ResolverLogger logger = new ResolverLogger(XMLResolverConfiguration.class);
    private static final ResolverFeature<?>[] knownFeatures = { ResolverFeature.CATALOG_FILES,
            ResolverFeature.PREFER_PUBLIC, ResolverFeature.PREFER_PROPERTY_FILE,
            ResolverFeature.ALLOW_CATALOG_PI, ResolverFeature.CATALOG_ADDITIONS,
            ResolverFeature.CACHE_DIRECTORY, ResolverFeature.CACHE_UNDER_HOME,
            ResolverFeature.CACHE, ResolverFeature.MERGE_HTTPS, ResolverFeature.MASK_JAR_URIS,
            ResolverFeature.CATALOG_MANAGER, ResolverFeature.URI_FOR_SYSTEM,
            ResolverFeature.CATALOG_LOADER_CLASS, ResolverFeature.PARSE_RDDL,
            ResolverFeature.CLASSPATH_CATALOGS};
    private static List<String> classpathCatalogList = null;

    private final List<String> catalogs;
    private Boolean preferPublic = ResolverFeature.PREFER_PUBLIC.getDefaultValue();
    private Boolean preferPropertyFile = ResolverFeature.PREFER_PROPERTY_FILE.getDefaultValue();
    private Boolean allowCatalogPI = ResolverFeature.ALLOW_CATALOG_PI.getDefaultValue();
    private String cacheDirectory = ResolverFeature.CACHE_DIRECTORY.getDefaultValue();
    private Boolean cacheUnderHome = ResolverFeature.CACHE_UNDER_HOME.getDefaultValue();
    private ResourceCache cache = ResolverFeature.CACHE.getDefaultValue(); // null
    private CatalogManager manager = ResolverFeature.CATALOG_MANAGER.getDefaultValue(); // also null
    private Boolean uriForSystem = ResolverFeature.URI_FOR_SYSTEM.getDefaultValue();
    private Boolean mergeHttps = ResolverFeature.MERGE_HTTPS.getDefaultValue();
    private Boolean maskJarUris = ResolverFeature.MASK_JAR_URIS.getDefaultValue();
    private String catalogLoader = ResolverFeature.CATALOG_LOADER_CLASS.getDefaultValue();
    private Boolean parseRddl = ResolverFeature.PARSE_RDDL.getDefaultValue();
    private Boolean classpathCatalogs = ResolverFeature.CLASSPATH_CATALOGS.getDefaultValue();
    private Boolean showConfigChanges = false; // make the config process a bit less chatty

    /** Construct a default configuration.
     *
     * <p>The default configuration uses system properties and searches the classpath for
     * an <code>xmlcatalog.properties</code> file. It uses the settings found there to
     * configure the resolver.</p>
     */
    public XMLResolverConfiguration() {
        this(null, null);
    }

    /** Construct a configuration from a delimited string of catalog files.
     *
     * <p>The default configuration uses system properties and searches the classpath for
     * an <code>xmlcatalog.properties</code> file. It uses the settings found there to
     * configure the resolver, but replaces any list of catalog files found there with the
     * catalog files provided in the constructor.</p>
     *
     * @param catalogFiles A semi-colon (;) delimited list of catalog files
     */
    public XMLResolverConfiguration(String catalogFiles) {
        this(null, Arrays.asList(catalogFiles.split("\\s*;\\s*")));
    }

    /** Construct a configuration from a list of catalog files.
     *
     * <p>The default configuration uses system properties and searches the classpath for
     * an <code>xmlcatalog.properties</code> file. It uses the settings found there to
     * configure the resolver, but replaces any list of catalog files found there with the
     * catalog files provided in the constructor.</p>
     *
     * @param catalogFiles A list of catalog files.
     */
    public XMLResolverConfiguration(List<String> catalogFiles) {
        this(null, catalogFiles);
    }

    /** Construct a resolver configuration with specific properties and catalog files.
     *
     * <p>The default configuration uses system properties and the properties found
     * in the first <code>propertyFiles</code> property file that it can read. (It uses at
     * most one property file.) It uses those settings to
     * configure the resolver, but replaces any list of catalog files found there with the
     * catalog files provided in the constructor.</p>
     *
     * @param propertyFiles A list of property files from which to attempt to load configuration properties.
     * @param catalogFiles A list of catalog files.
     */
    public XMLResolverConfiguration(List<URL> propertyFiles, List<String> catalogFiles) {
        logger.log(ResolverLogger.CONFIG, "XMLResolver version %s", BuildConfig.VERSION);
        showConfigChanges = false;
        catalogs = new ArrayList<>();
        loadConfiguration(propertyFiles, catalogFiles);
        showConfigChanges = true;
    }

    /** A copying constructor.
     *
     * <p>This constructor creates a new resolver configuration with the same properties
     * as an existing configuration. It gets its own copy of the catalog file list and
     * {@link CatalogManager}.</p>
     *
     * @param current The configuration to copy.
     */
    public XMLResolverConfiguration(XMLResolverConfiguration current) {
        catalogs = new ArrayList<>(current.catalogs);
        preferPublic = current.preferPublic;
        preferPropertyFile = current.preferPropertyFile;
        allowCatalogPI = current.allowCatalogPI;
        cacheDirectory = current.cacheDirectory;
        cacheUnderHome = current.cacheUnderHome;
        cache = current.cache;
        if (current.manager == null) {
            manager = null;
        } else {
            manager = new CatalogManager(current.manager, this);
        }
        uriForSystem = current.uriForSystem;
        mergeHttps = current.mergeHttps;
        maskJarUris = current.maskJarUris;
        catalogLoader = current.catalogLoader;
        parseRddl = current.parseRddl;
        classpathCatalogs = current.classpathCatalogs;
        showConfigChanges = current.showConfigChanges;
    }

    private void loadConfiguration(List<URL> propertyFiles, List<String> catalogFiles) {
        loadSystemPropertiesConfiguration();

        ArrayList<URL> propertyFilesList = new ArrayList<>();
        if (propertyFiles == null) {
            // Do default initialization
            String propfn = System.getProperty("xmlresolver.properties");
            if (propfn == null) {
                propfn = System.getenv("XMLRESOLVER_PROPERTIES");
            }

            // Hack: you can set the xmlresolver.properties to the empty string
            // to avoid loading the XMLRESOLVER_PROPERTIES environment. This is
            // an edge case more-or-less exclusively for testing.
            if (propfn == null || "".equals(propfn)) {
                URL propurl = XMLResolverConfiguration.class.getResource("/xmlresolver.properties");
                if (propurl != null) {
                    propertyFilesList.add(propurl);
                }
            } else {
                URI baseURI = URIUtils.cwd();
                for (String fn : propfn.split("\\s*;\\s*")) {
                    try {
                        propertyFilesList.add(baseURI.resolve(fn).toURL());
                    } catch (MalformedURLException ex) {
                        // nevermind
                    }
                }
            }
        } else {
            propertyFilesList.addAll(propertyFiles);
        }

        URL propurl = null;
        Properties properties = new Properties();
        for (URL url : propertyFilesList) {
            try {
                InputStream in = url.openStream();
                if (in != null) {
                    properties.load(in);
                    propurl = url;
                    break;
                }
            } catch (IOException ex) {
                // nevermind
            }
        }

        if (propurl != null) {
            loadPropertiesConfiguration(propurl, properties);
            if (!preferPropertyFile) {
                loadSystemPropertiesConfiguration();
            }
        }

        if (catalogFiles == null) {
            if (catalogs.isEmpty()) {
                catalogs.add("./catalog.xml");
            }
        } else {
            catalogs.clear();
            for (String fn : catalogFiles) {
                if ("".equals(fn.trim())) {
                    continue;
                }
                catalogs.add(fn);
            }
        }

        showConfig();
        showConfigChanges = true;
    }

    private void loadSystemPropertiesConfiguration() {
        String property = System.getProperty("xml.catalog.files");
        if (property != null) {
            StringTokenizer tokens = new StringTokenizer(property, ";");
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Catalog list cleared");
            }
            catalogs.clear();
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!"".equals(token.trim())) {
                    if (showConfigChanges) {
                        logger.log(ResolverLogger.CONFIG, "Catalog: %s", token);
                    }
                    catalogs.add(token);
                }
            }
        }

        property = System.getProperty("xml.catalog.additions");
        if (property != null) {
            StringTokenizer tokens = new StringTokenizer(property, ";");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!"".equals(token.trim())) {
                    if (showConfigChanges) {
                        logger.log(ResolverLogger.CONFIG, "Catalog: %s", token);
                    }
                    catalogs.add(token);
                }
            }
        }

        property = System.getProperty("xml.catalog.prefer");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Prefer public: %s", property);
            }
            preferPublic = "public".equals(property);
        }

        property = System.getProperty("xml.catalog.preferPropertyFile");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Prefer propertyFile: %s", property);
            }
            preferPropertyFile = isTrue(property);
        }

        property = System.getProperty("xml.catalog.allowPI");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Allow catalog PI: %s", property);
            }
            allowCatalogPI = isTrue(property);
        }

        property = System.getProperty("xml.catalog.cache");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Cache directory: %s", property);
            }
            cacheDirectory = property;
        }

        property = System.getProperty("xml.catalog.cacheUnderHome");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Cache under home: %s", property);
            }
            cacheUnderHome = isTrue(property);
        }

        property = System.getProperty("xml.catalog.uriForSystem");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "URI-for-system: %s", property);
            }
            uriForSystem = isTrue(property);
        }

        property = System.getProperty("xml.catalog.mergeHttps");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Merge-https: %s", property);
            }
            mergeHttps = isTrue(property);
        }

        property = System.getProperty("xml.catalog.maskJarUris");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Mask-jar-URIs: %s", property);
            }
            maskJarUris = isTrue(property);
        }

        property = System.getProperty("xml.catalog.catalogLoaderClass");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Catalog loader: %s", property);
            }
            catalogLoader = property;
        }

        property = System.getProperty("xml.catalog.parseRddl");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Use RDDL: %s", property);
            }
            parseRddl = isTrue(property);
        }

        property = System.getProperty("xml.catalog.classpathCatalogs");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Classpath catalogs: %s", property);
            }
            classpathCatalogs = isTrue(property);
        }
    }

    private void loadPropertiesConfiguration(URL propertiesURL, Properties properties) {
        boolean relative = true;
        String allow = properties.getProperty("relative-catalogs");
        if (allow != null) {
            relative = isTrue(allow);
        }
        if (showConfigChanges) {
            logger.log(ResolverLogger.CONFIG, "Relative catalogs: %s", relative);
        }

        String property = properties.getProperty("catalogs");
        if (property != null) {
            StringTokenizer tokens = new StringTokenizer(property, ";");
            catalogs.clear();
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Catalog list cleared");
            }
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!"".equals(token.trim())) {
                    if (!relative && propertiesURL != null) {
                        try {
                            token = new URL(propertiesURL, token).toString();
                        } catch (MalformedURLException e) {
                            logger.log(ResolverLogger.ERROR, "Cannot make absolute: " + token);
                        }
                    }
                    if (showConfigChanges) {
                        logger.log(ResolverLogger.CONFIG, "Catalog: %s", token);
                    }
                    catalogs.add(token);
                }
            }
        }

        property = properties.getProperty("catalog-additions");
        if (property != null) {
            StringTokenizer tokens = new StringTokenizer(property, ";");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!"".equals(token.trim())) {
                    if (!relative && propertiesURL != null) {
                        try {
                            token = new URL(propertiesURL, token).toURI().toString();
                        } catch (URISyntaxException | MalformedURLException e) {
                            logger.log(ResolverLogger.ERROR, "Cannot make absolute: " + token);
                        }
                    }
                    if (showConfigChanges) {
                        logger.log(ResolverLogger.CONFIG, "Catalog: %s", token);
                    }
                    catalogs.add(token);
                }
            }
        }

        property = properties.getProperty("prefer");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Prefer public: %s", property);
            }
            preferPublic = "public".equals(property);
        }

        property = properties.getProperty("prefer-property-file");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Prefer propertyFile: %s", property);
            }
            preferPropertyFile = isTrue(property);
        }

        property = properties.getProperty("allow-oasis-xml-catalog-pi");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Allow catalog PI: %s", property);
            }
            allowCatalogPI = isTrue(property);
        }

        property = properties.getProperty("cache");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Cache directory: %s", property);
            }
            cacheDirectory = property;
        }

        property = properties.getProperty("cacheUnderHome");
        if (property == null) {
            property = properties.getProperty("cache-under-home");
        }
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Cache under home: %s", property);
            }
            cacheUnderHome = isTrue(property);
        }

        property = properties.getProperty("uri-for-system");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "URI-for-system: %s", property);
            }
            uriForSystem = isTrue(property);
        }

        property = properties.getProperty("merge-https");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Merge-https: %s", property);
            }
            mergeHttps = isTrue(property);
        }

        property = properties.getProperty("mask-jar-uris");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Mask-jar-URIs: %s", property);
            }
            maskJarUris = isTrue(property);
        }

        property = properties.getProperty("catalog-loader-class");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Catalog loader: %s", property);
            }
            catalogLoader = property;
        }

        property = properties.getProperty("parse-rddl");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Parse RDDL: %s", property);
            }
            parseRddl = isTrue(property);
        }

        property = properties.getProperty("classpath-catalogs");
        if (property != null) {
            if (showConfigChanges) {
                logger.log(ResolverLogger.CONFIG, "Classpath catalogs: %s", property);
            }
            classpathCatalogs = isTrue(property);
        }
    }

    private void showConfig() {
        logger.log(ResolverLogger.CONFIG, "Logging: %s", System.getProperty("xml.catalog.logging"));
        logger.log(ResolverLogger.CONFIG, "Prefer public: %s", preferPublic);
        logger.log(ResolverLogger.CONFIG, "Prefer property file: %s", preferPropertyFile);
        logger.log(ResolverLogger.CONFIG, "Allow catalog PI: %s", allowCatalogPI);
        logger.log(ResolverLogger.CONFIG, "Parse RDDL: %s", parseRddl);
        logger.log(ResolverLogger.CONFIG, "URI for system: %s", uriForSystem);
        logger.log(ResolverLogger.CONFIG, "Merge http/https: %s", mergeHttps);
        logger.log(ResolverLogger.CONFIG, "Mask jar URIs: %s", maskJarUris);
        logger.log(ResolverLogger.CONFIG, "Cache under home: %s", cacheUnderHome);
        logger.log(ResolverLogger.CONFIG, "Cache directory: %s", cacheDirectory);
        logger.log(ResolverLogger.CONFIG, "Catalog loader: %s", catalogLoader);
        logger.log(ResolverLogger.CONFIG, "Classpath catalogs: %s", classpathCatalogs);
        for (String catalog: catalogs) {
            logger.log(ResolverLogger.CONFIG, "Catalog: %s", catalog);
        }
        if (classpathCatalogs) {
            for (String catalog : findClasspathCatalogFiles()) {
                logger.log(ResolverLogger.CONFIG, "Catalog: %s", catalog);
            }
        }
    }

    /** Add a catalog file to the list of catalogs.
     *
     * <p>This adds a catalog file to the end of the list of catalogs. This file will be
     * loaded by opening the specified file.</p>
     *
     * @param catalog The catalog file.
     */
    public void addCatalog(String catalog) {
        if (catalog != null) {
            synchronized (catalogs) {
                catalogs.add(catalog);
            }
        }
    }

    /** Add a catalog file to the list of catalogs.
     *
     * <p>This adds a catalog file to the end of the list of catalogs. This file will be loaded
     * by reading from the specified input source.</p>
     *
     * @param catalog The catalog file.
     * @throws NullPointerException if either catalog or data is null.
     */
    public void addCatalog(URI catalog, InputSource data) {
        if (catalog == null) {
            throw new NullPointerException("null provided for catalog URI");
        }
        if (data == null) {
            throw new NullPointerException("null provided for catalog data");
        }
        URI uri = URIUtils.cwd().resolve(catalog);
        synchronized (catalogs) {
            catalogs.add(uri.toString());
            if (manager == null) {
                manager = getFeature(ResolverFeature.CATALOG_MANAGER);
            }
            manager.loadCatalog(uri, data);
        }
    }

    /** Remove a catalog from the list of catalogs.
     *
     * <p>Removes the specified catalog from the list of catalogs (if it was present in the list).</p>
     *
     * @param catalog The catalog file.
     * @return True if the catalog was removed.
     */
    public boolean removeCatalog(String catalog) {
        synchronized (catalogs) {
            return catalogs.remove(catalog);
        }
    }

    /** Set a configuration feature.
     *
     * <p>Sets the specified feature to the specified value. Unknown features are ignored.</p>
     *
     * @param feature The feature.
     * @param value The new value.
     * @param <T> A type appropriate for the feature.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> void setFeature(ResolverFeature<T> feature, T value) {
        if (feature == ResolverFeature.CATALOG_FILES) {
            synchronized (catalogs) {
                catalogs.clear();
                catalogs.addAll((List<String>) value);
            }
        } else if (feature == ResolverFeature.PREFER_PUBLIC) {
            preferPublic = (Boolean) value;
        } else if (feature == ResolverFeature.PREFER_PROPERTY_FILE) {
            preferPropertyFile = (Boolean) value;
        } else if (feature == ResolverFeature.ALLOW_CATALOG_PI) {
            allowCatalogPI = (Boolean) value;
        } else if (feature == ResolverFeature.CACHE_DIRECTORY) {
            cacheDirectory = (String) value;
            cache = null;
        } else if (feature == ResolverFeature.CACHE_UNDER_HOME) {
            cacheUnderHome = (Boolean) value;
            cache = null;
        } else if (feature == ResolverFeature.CACHE) {
            cache = (ResourceCache) value;
        } else if (feature == ResolverFeature.CATALOG_MANAGER) {
            manager = (CatalogManager) value;
        } else if (feature == ResolverFeature.URI_FOR_SYSTEM) {
            uriForSystem = (Boolean) value;
        } else if (feature == ResolverFeature.MERGE_HTTPS) {
            mergeHttps = (Boolean) value;
        } else if (feature == ResolverFeature.MASK_JAR_URIS) {
            maskJarUris = (Boolean) value;
        } else if (feature == ResolverFeature.CATALOG_LOADER_CLASS) {
            catalogLoader = (String) value;
        } else if (feature == ResolverFeature.PARSE_RDDL) {
            parseRddl = (Boolean) value;
        } else if (feature == ResolverFeature.CLASSPATH_CATALOGS) {
            classpathCatalogs = (Boolean) value;
        } else {
            logger.log(ResolverLogger.ERROR, "Ignoring unknown feature: %s", feature.getName());
        }
    }

    private List<String> findClasspathCatalogFiles() {
        if (classpathCatalogList == null) {
            logger.log(ResolverLogger.CONFIG, "Searching for catalogs on classpath:");
            for (URL url : ((URLClassLoader) XMLResolverConfiguration.class.getClassLoader()).getURLs()) {
                logger.log(ResolverLogger.CONFIG, "    " + url.toString());
            }
            ArrayList<String> catalogs = new ArrayList<>();
            try {
                Enumeration<URL> resources = XMLResolverConfiguration.class.getClassLoader().getResources("org/xmlresolver/catalog.xml");
                while (resources.hasMoreElements()) {
                    URL catalog = resources.nextElement();
                    logger.log(ResolverLogger.CONFIG, "Catalog: " + catalog.toString());
                    catalogs.add(catalog.toString());
                }
            } catch (IOException ex) {
                // nevermind
            }

            classpathCatalogList = Collections.unmodifiableList(catalogs);
        }

        return classpathCatalogList;
    }

    /** Return the value of a feature.
     *
     * <p>Returns the value of the specified feature.</p>
     *
     * @param feature The feature or null if the feature is unknown.
     * @param <T> A type appropriate to the feature.
     * @return The feature value.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getFeature(ResolverFeature<T> feature) {
        if (feature == ResolverFeature.CATALOG_MANAGER) {
            // Delay construction of the default catalog manager until it's
            // requested. If the user sets it before asking for it, we'll
            // never have to construct it.
            if (manager == null) {
                manager = new CatalogManager(this);
            }
            return (T) manager;
        } else if (feature == ResolverFeature.CATALOG_FILES) {
            List<String> cats = new ArrayList<>(catalogs);
            if (classpathCatalogs) {
                cats.addAll(findClasspathCatalogFiles());
            }
            return (T) cats;
        } else if (feature == ResolverFeature.PREFER_PUBLIC) {
            return (T) preferPublic;
        } else if (feature == ResolverFeature.PREFER_PROPERTY_FILE) {
            return (T) preferPropertyFile;
        } else if (feature == ResolverFeature.ALLOW_CATALOG_PI) {
            return (T) allowCatalogPI;
        } else if (feature == ResolverFeature.CACHE_DIRECTORY) {
            return (T) cacheDirectory;
        } else if (feature == ResolverFeature.URI_FOR_SYSTEM) {
            return (T) uriForSystem;
        } else if (feature == ResolverFeature.MERGE_HTTPS) {
            return (T) mergeHttps;
        } else if (feature == ResolverFeature.MASK_JAR_URIS) {
            return (T) maskJarUris;
        } else if (feature == ResolverFeature.CATALOG_LOADER_CLASS) {
            return (T) catalogLoader;
        } else if (feature == ResolverFeature.PARSE_RDDL) {
            return (T) parseRddl;
        } else if (feature == ResolverFeature.CLASSPATH_CATALOGS) {
            return (T) classpathCatalogs;
        } else if (feature == ResolverFeature.CACHE) {
            if (cache == null) {
                cache = new ResourceCache(this);
            }
            return (T) cache;
        } else if (feature == ResolverFeature.CACHE_UNDER_HOME) {
            return (T) cacheUnderHome;
        } else {
            logger.log(ResolverLogger.ERROR, "Ignoring unknown feature: %s", feature.getName());
            return null;
        }
    }

    /** Iterate over all the known features.
     *
     * @return An iterator over all the known features.
     */
    @Override
    public Iterator<ResolverFeature<?>> getFeatures() {
         return Arrays.stream(knownFeatures).iterator();
    }

    private static boolean isTrue(String aString) {
        if (aString == null) {
            return false;
        } else {
            return "true".equalsIgnoreCase(aString) || "yes".equalsIgnoreCase(aString) || "1".equalsIgnoreCase(aString);
        }
    }
}
