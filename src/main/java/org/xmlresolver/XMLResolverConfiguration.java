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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

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

public class XMLResolverConfiguration implements ResolverConfiguration {
    private static final ResolverLogger logger = new ResolverLogger(XMLResolverConfiguration.class);
    private static final ResolverFeature<?>[] knownFeatures = { ResolverFeature.CATALOG_FILES,
            ResolverFeature.PREFER_PUBLIC, ResolverFeature.PREFER_PROPERTY_FILE,
            ResolverFeature.ALLOW_CATALOG_PI, ResolverFeature.CATALOG_ADDITIONS,
            ResolverFeature.CACHE_DIRECTORY, ResolverFeature.CACHE_UNDER_HOME,
            ResolverFeature.CACHE, ResolverFeature.MERGE_HTTPS,
            ResolverFeature.CATALOG_MANAGER, ResolverFeature.URI_FOR_SYSTEM };

    private List<String> catalogs = new ArrayList<>();
    private Boolean preferPublic = ResolverFeature.PREFER_PUBLIC.getDefaultValue();
    private Boolean preferPropertyFile = ResolverFeature.PREFER_PROPERTY_FILE.getDefaultValue();
    private Boolean allowCatalogPI = ResolverFeature.ALLOW_CATALOG_PI.getDefaultValue();
    private String cacheDirectory = ResolverFeature.CACHE_DIRECTORY.getDefaultValue();
    private Boolean cacheUnderHome = ResolverFeature.CACHE_UNDER_HOME.getDefaultValue();
    private ResourceCache cache = ResolverFeature.CACHE.getDefaultValue(); // null
    private CatalogManager manager = ResolverFeature.CATALOG_MANAGER.getDefaultValue(); // also null
    private Boolean uriForSystem = ResolverFeature.URI_FOR_SYSTEM.getDefaultValue();
    private Boolean mergeHttps = ResolverFeature.MERGE_HTTPS.getDefaultValue();
    private Boolean showConfigChanges = false; // make the config process a bit less chatty

    public XMLResolverConfiguration() {
        this(null, null);
    }

    public XMLResolverConfiguration(String catalogFiles) {
        this(null, Arrays.asList(catalogFiles.split("\\s*;\\s*")));
    }

    public XMLResolverConfiguration(List<URL> propertyFiles, List<String> catalogFiles) {
        logger.log(ResolverLogger.CONFIG, "XMLResolver version %s", BuildConfig.VERSION);
        showConfigChanges = false;
        loadConfiguration(propertyFiles, catalogFiles);
        showConfigChanges = true;
    }

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
                logger.log(ResolverLogger.CONFIG, "Catalog cleared");
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
    }

    /*
    private void loadPropertiesConfiguration(URL propertiesURL) {
        try {
            logger.log(ResolverLogger.CONFIG, "Load properties file: %s", propertiesURL);
            Properties props = new Properties();
            props.load(propertiesURL.openStream());
            loadPropertiesConfiguration(propertiesURL, props);
        } catch (IOException e) {
            logger.log(ResolverLogger.ERROR, "Failed to load properties from %s", propertiesURL);
        }
    }

    private void loadPropertiesConfiguration(Properties properties) {
        loadPropertiesConfiguration(null, properties);
    }
    */

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
                logger.log(ResolverLogger.CONFIG, "Catalog cleared");
            }
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!"".equals(token.trim())) {
                    if (relative && propertiesURL != null) {
                        try {
                            token = new URL(propertiesURL, token).toURI().toASCIIString();
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

        property = properties.getProperty("catalog-additions");
        if (property != null) {
            StringTokenizer tokens = new StringTokenizer(property, ";");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!"".equals(token.trim())) {
                    if (relative && propertiesURL != null) {
                        try {
                            token = new URL(propertiesURL, token).toURI().toASCIIString();
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
    }

    private void showConfig() {
        logger.log(ResolverLogger.CONFIG, "Logging: %s", System.getProperty("xml.catalog.logging"));
        logger.log(ResolverLogger.CONFIG, "Prefer public: %s", preferPublic);
        logger.log(ResolverLogger.CONFIG, "Prefer property file: %s", preferPropertyFile);
        logger.log(ResolverLogger.CONFIG, "Allow catalog PI: %s", allowCatalogPI);
        logger.log(ResolverLogger.CONFIG, "URI for system: %s", uriForSystem);
        logger.log(ResolverLogger.CONFIG, "Merge http/https: %s", mergeHttps);
        logger.log(ResolverLogger.CONFIG, "Cache under home: %s", cacheUnderHome);
        logger.log(ResolverLogger.CONFIG, "Cache directory: %s", cacheDirectory);
        for (String catalog: catalogs) {
            logger.log(ResolverLogger.CONFIG, "Catalog: %s", catalog);
        }
    }

    public void addCatalog(String catalog) {
        catalogs.add(catalog);
    }

    public void addCatalog(URI catalog, InputSource data) {
        URI uri = URIUtils.cwd().resolve(catalog);
        catalogs.add(uri.toString());
        if (manager == null) {
            manager = getFeature(ResolverFeature.CATALOG_MANAGER);
        }
        manager.loadCatalog(uri, data);
    }

    public boolean removeCatalog(String catalog) {
        return catalogs.remove(catalog);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void setFeature(ResolverFeature<T> feature, T value) {
        if (feature == ResolverFeature.CATALOG_FILES) {
            catalogs = (List<String>) value;
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
        } else {
            logger.log(ResolverLogger.ERROR, "Ignoring unknown feature: %s", feature.getName());
        }
    }

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
            return (T) catalogs;
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
