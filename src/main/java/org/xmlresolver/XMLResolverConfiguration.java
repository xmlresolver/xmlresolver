package org.xmlresolver;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlresolver.cache.ResourceCache;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.utils.URIUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;
import java.util.function.Supplier;

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
 * <tr><th>{@link ResolverFeature#ACCESS_EXTERNAL_DOCUMENT}</th>
 * <td>xml.catalog.accessExternalDocument</td>
 * <td>access-external-document</td>
 * <td>String</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#ACCESS_EXTERNAL_ENTITY}</th>
 * <td>xml.catalog.accessExternalEntity</td>
 * <td>access-external-entity</td>
 * <td>String</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#ALLOW_CATALOG_PI}</th>
 * <td>xml.catalog.allowPI</td>
 * <td>allow-oasis-xml-catalog-pi</td>
 * <td>Boolean¹</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#ARCHIVED_CATALOGS}</th>
 * <td>xml.catalog.archivedCatalogs</td>
 * <td>archived-catalogs</td>
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
 * <tr><th>{@link ResolverFeature#CACHE_ENABLED}</th>
 * <td>xml.catalog.cacheEnabled</td>
 * <td>cache-enabled</td>
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
 * <td>String</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#DEFAULT_LOGGER_LOG_LEVEL}</th>
 * <td>xml.catalog.defaultLoggerLogLevel</td>
 * <td>default-logger-log-level</td>
 * <td>String</td>
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
 * <td>"<code>public</code>" or "<code>system</code>"³</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#RESOLVER_LOGGER_CLASS}</th>
 * <td>xml.catalog.resolverLoggerClass</td>
 * <td>resolver-logger-class</td>
 * <td>String</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#SAXPARSERFACTORY_CLASS}</th>
 * <td>xml.catalog.saxParserFactoryClass</td>
 * <td>saxparserfactory-class</td>
 * <td>String</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#URI_FOR_SYSTEM}</th>
 * <td>xml.catalog.uriForSystem</td>
 * <td>uri-for-system</td>
 * <td>Boolean¹</td>
 * </tr>
 * <tr><th>{@link ResolverFeature#XMLREADER_SUPPLIER}</th>
 * <td>-</td>
 * <td>-</td>
 * <td>Supplier&lt;XMLReader&gt;</td>
 * </tr>
 * </tbody>
 * <tfoot>
 * <tr>
 * <td colspan="4">¹ Any of "true", "yes", or "1" is true; everything else is false.<td>
 * </tr>
 * <tr>
 * <td colspan="4">² The list of strings is semicolon delimited<td>
 * </tr>
 * <tr>
 * <td colspan="4">³ Public is preferred if the value is "public", any other value
 * is equivalent to "system".</td>
 * </tr>
 * </tfoot>
 * </table>
 * <p>Several additional features can only be set to objects at runtime and have no corresponding properties.</p>
 */

public class XMLResolverConfiguration implements ResolverConfiguration {
    private static final ResolverFeature<?>[] knownFeatures = { ResolverFeature.CATALOG_FILES,
            ResolverFeature.PREFER_PUBLIC, ResolverFeature.PREFER_PROPERTY_FILE,
            ResolverFeature.ALLOW_CATALOG_PI, ResolverFeature.CATALOG_ADDITIONS,
            ResolverFeature.CACHE_DIRECTORY, ResolverFeature.CACHE_UNDER_HOME,
            ResolverFeature.CACHE_ENABLED,
            ResolverFeature.CACHE, ResolverFeature.MERGE_HTTPS, ResolverFeature.MASK_JAR_URIS,
            ResolverFeature.CATALOG_MANAGER, ResolverFeature.URI_FOR_SYSTEM,
            ResolverFeature.CATALOG_LOADER_CLASS, ResolverFeature.PARSE_RDDL,
            ResolverFeature.CLASSPATH_CATALOGS, ResolverFeature.CLASSLOADER,
            ResolverFeature.ARCHIVED_CATALOGS, ResolverFeature.THROW_URI_EXCEPTIONS,
            ResolverFeature.RESOLVER_LOGGER_CLASS, ResolverFeature.RESOLVER_LOGGER,
            ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL,
            ResolverFeature.ACCESS_EXTERNAL_ENTITY, ResolverFeature.ACCESS_EXTERNAL_DOCUMENT,
            ResolverFeature.SAXPARSERFACTORY_CLASS, ResolverFeature.XMLREADER_SUPPLIER};

    private static List<String> classpathCatalogList = null;

    private final List<String> catalogs;
    private final List<String> additionalCatalogs;
    private Boolean preferPublic = ResolverFeature.PREFER_PUBLIC.getDefaultValue();
    private Boolean preferPropertyFile = ResolverFeature.PREFER_PROPERTY_FILE.getDefaultValue();
    private Boolean allowCatalogPI = ResolverFeature.ALLOW_CATALOG_PI.getDefaultValue();
    private String cacheDirectory = ResolverFeature.CACHE_DIRECTORY.getDefaultValue();
    private Boolean cacheUnderHome = ResolverFeature.CACHE_UNDER_HOME.getDefaultValue();
    private Boolean cacheEnabled = ResolverFeature.CACHE_ENABLED.getDefaultValue();
    private ResourceCache cache = ResolverFeature.CACHE.getDefaultValue(); // null
    private CatalogManager manager = ResolverFeature.CATALOG_MANAGER.getDefaultValue(); // also null
    private Boolean uriForSystem = ResolverFeature.URI_FOR_SYSTEM.getDefaultValue();
    private Boolean mergeHttps = ResolverFeature.MERGE_HTTPS.getDefaultValue();
    private Boolean maskJarUris = ResolverFeature.MASK_JAR_URIS.getDefaultValue();
    private String catalogLoader = ResolverFeature.CATALOG_LOADER_CLASS.getDefaultValue();
    private Boolean parseRddl = ResolverFeature.PARSE_RDDL.getDefaultValue();
    private Boolean classpathCatalogs = ResolverFeature.CLASSPATH_CATALOGS.getDefaultValue();
    private ClassLoader classLoader = ResolverFeature.CLASSLOADER.getDefaultValue();
    private Boolean archivedCatalogs = ResolverFeature.ARCHIVED_CATALOGS.getDefaultValue();
    private Boolean throwUriExceptions = ResolverFeature.THROW_URI_EXCEPTIONS.getDefaultValue();
    private Boolean showConfigChanges = false; // make the config process a bit less chatty
    private String resolverLoggerClass = ResolverFeature.RESOLVER_LOGGER_CLASS.getDefaultValue();
    private String defaultLoggerLogLevel = ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL.getDefaultValue();
    private String accessExternalEntity = ResolverFeature.ACCESS_EXTERNAL_ENTITY.getDefaultValue();
    private String accessExternalDocument = ResolverFeature.ACCESS_EXTERNAL_DOCUMENT.getDefaultValue();
    private String saxParserFactoryClass = ResolverFeature.SAXPARSERFACTORY_CLASS.getDefaultValue();
    private Supplier<XMLReader> xmlReaderSupplier = ResolverFeature.XMLREADER_SUPPLIER.getDefaultValue();
    private ResolverLogger resolverLogger = null;

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
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        showConfigChanges = false;
        catalogs = new ArrayList<>();
        additionalCatalogs = new ArrayList<>();

        // We have a tiny little bit of a chicken-and-egg problem here. The logger
        // can be configured, but we want to emit log messages during configuration.
        // The FallbackLogger buffers them until we have a configuration...
        FallbackLogger fallbackLogger = new FallbackLogger();
        resolverLogger = fallbackLogger;
        loadConfiguration(propertyFiles, catalogFiles);

        // Now get the real logger and forward any fallback messages to it.
        resolverLogger = null;
        resolverLogger = getFeature(ResolverFeature.RESOLVER_LOGGER);
        fallbackLogger.forward(resolverLogger);

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
        additionalCatalogs = new ArrayList<>();
        classLoader = current.classLoader;
        preferPublic = current.preferPublic;
        preferPropertyFile = current.preferPropertyFile;
        allowCatalogPI = current.allowCatalogPI;
        cacheDirectory = current.cacheDirectory;
        cacheUnderHome = current.cacheUnderHome;
        cache = current.cache;
        cacheEnabled = current.cacheEnabled;
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
        archivedCatalogs = current.archivedCatalogs;
        throwUriExceptions = current.throwUriExceptions;
        showConfigChanges = current.showConfigChanges;
        resolverLoggerClass = current.resolverLoggerClass;
        resolverLogger = current.resolverLogger;
        defaultLoggerLogLevel = current.defaultLoggerLogLevel;
        accessExternalEntity = current.accessExternalEntity;
        accessExternalDocument = current.accessExternalDocument;
        saxParserFactoryClass = current.saxParserFactoryClass;
        xmlReaderSupplier = current.xmlReaderSupplier;
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

        if (saxParserFactoryClass != null) {
            // Call setFeature to perform additional initialization for this property.
            setFeature(ResolverFeature.SAXPARSERFACTORY_CLASS, saxParserFactoryClass);
        }

        showConfig();
        showConfigChanges = true;
    }

    private void loadSystemPropertiesConfiguration() {
        String property = System.getProperty("xml.catalog.files");
        if (property != null) {
            StringTokenizer tokens = new StringTokenizer(property, ";");
            showConfigChange("Catalog list cleared");
            catalogs.clear();
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!"".equals(token.trim())) {
                    showConfigChange("Catalog: %s", token);
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
                    showConfigChange("Catalog: %s", token);
                    catalogs.add(token);
                }
            }
        }

        property = System.getProperty("xml.catalog.prefer");
        if (property != null) {
            showConfigChange("Prefer public: %s", property);
            preferPublic = "public".equals(property);
        }

        property = System.getProperty("xml.catalog.preferPropertyFile");
        if (property != null) {
            showConfigChange("Prefer propertyFile: %s", property);
            preferPropertyFile = isTrue(property);
        }

        property = System.getProperty("xml.catalog.allowPI");
        if (property != null) {
            showConfigChange("Allow catalog PI: %s", property);
            allowCatalogPI = isTrue(property);
        }

        property = System.getProperty("xml.catalog.cache");
        if (property != null) {
            showConfigChange("Cache directory: %s", property);
            cacheDirectory = property;
        }

        property = System.getProperty("xml.catalog.cacheUnderHome");
        if (property != null) {
            showConfigChange("Cache under home: %s", property);
            cacheUnderHome = isTrue(property);
        }

        property = System.getProperty("xml.catalog.cacheEnabled");
        if (property != null) {
            showConfigChange("Cache enabled: %s", property);
            cacheEnabled = isTrue(property);
        }

        property = System.getProperty("xml.catalog.uriForSystem");
        if (property != null) {
            showConfigChange("URI-for-system: %s", property);
            uriForSystem = isTrue(property);
        }

        property = System.getProperty("xml.catalog.mergeHttps");
        if (property != null) {
            showConfigChange("Merge-https: %s", property);
            mergeHttps = isTrue(property);
        }

        property = System.getProperty("xml.catalog.maskJarUris");
        if (property != null) {
            showConfigChange("Mask-jar-URIs: %s", property);
            maskJarUris = isTrue(property);
        }

        property = System.getProperty("xml.catalog.catalogLoaderClass");
        if (property != null) {
            showConfigChange("Catalog loader: %s", property);
            catalogLoader = property;
        }

        property = System.getProperty("xml.catalog.parseRddl");
        if (property != null) {
            showConfigChange("Use RDDL: %s", property);
            parseRddl = isTrue(property);
        }

        property = System.getProperty("xml.catalog.classpathCatalogs");
        if (property != null) {
            showConfigChange("Classpath catalogs: %s", property);
            classpathCatalogs = isTrue(property);
        }

        property = System.getProperty("xml.catalog.archivedCatalogs");
        if (property != null) {
            showConfigChange("Archived catalogs: %s", property);
            archivedCatalogs = isTrue(property);
        }

        property = System.getProperty("xml.catalog.throwUriExceptions");
        if (property != null) {
            showConfigChange("Throw URI exceptions: %s", property);
            throwUriExceptions = isTrue(property);
        }

        property = System.getProperty("xml.catalog.resolverLoggerClass");
        if (property != null) {
            showConfigChange("Resolver logger class: %s", property);
            resolverLoggerClass = property;
        }

        property = System.getProperty("xml.catalog.defaultLoggerLogLevel");
        if (property != null) {
            showConfigChange("Default logger log level: %s", property);
            defaultLoggerLogLevel = property;
        }

        property = System.getProperty("xml.catalog.accessExternalEntity");
        if (property != null) {
            showConfigChange("Access external entity: %s", property);
            accessExternalEntity = property;
        }

        property = System.getProperty("xml.catalog.accessExternalDocument");
        if (property != null) {
            showConfigChange("Access external document: %s", property);
            accessExternalDocument = property;
        }

        property = System.getProperty("xml.catalog.saxParserFactoryClass");
        if (property != null) {
            showConfigChange("SAXParserFactory class: %s", property);
            saxParserFactoryClass = property;
        }
    }

    private void loadPropertiesConfiguration(URL propertiesURL, Properties properties) {
        // Bit of a hack here.
        String property = properties.getProperty("catalog-logging");
        if (property != null && System.getProperty("xml.catalog.logging") == null) {
            System.setProperty("xml.catalog.logging", property);
        }

        boolean relative = true;
        String allow = properties.getProperty("relative-catalogs");
        if (allow != null) {
            relative = isTrue(allow);
        }
        showConfigChange("Relative catalogs: %s", relative);

        property = properties.getProperty("catalogs");
        if (property != null) {
            StringTokenizer tokens = new StringTokenizer(property, ";");
            catalogs.clear();
            showConfigChange("Catalog list cleared");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!"".equals(token.trim())) {
                    if (relative && propertiesURL != null) {
                        try {
                            token = new URL(propertiesURL, token).toString();
                        } catch (MalformedURLException e) {
                            resolverLogger.log(AbstractLogger.ERROR, "Cannot make absolute: " + token);
                        }
                    }
                    showConfigChange("Catalog: %s", token);
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
                            token = new URL(propertiesURL, token).toURI().toString();
                        } catch (URISyntaxException | MalformedURLException e) {
                            resolverLogger.log(AbstractLogger.ERROR, "Cannot make absolute: " + token);
                        }
                    }
                    showConfigChange("Catalog: %s", token);
                    catalogs.add(token);
                }
            }
        }

        property = properties.getProperty("prefer");
        if (property != null) {
            showConfigChange("Prefer public: %s", property);
            preferPublic = "public".equals(property);
        }

        property = properties.getProperty("prefer-property-file");
        if (property != null) {
            showConfigChange("Prefer propertyFile: %s", property);
            preferPropertyFile = isTrue(property);
        }

        property = properties.getProperty("allow-oasis-xml-catalog-pi");
        if (property != null) {
            showConfigChange("Allow catalog PI: %s", property);
            allowCatalogPI = isTrue(property);
        }

        property = properties.getProperty("cache");
        if (property != null) {
            showConfigChange("Cache directory: %s", property);
            cacheDirectory = property;
        }

        property = properties.getProperty("cache-under-home");
        if (property == null) {
            property = properties.getProperty("cacheUnderHome");
        }
        if (property != null) {
            showConfigChange("Cache under home: %s", property);
            cacheUnderHome = isTrue(property);
        }

        property = properties.getProperty("cache-enabled");
        if (property != null) {
            showConfigChange("Cache enabled: %s", property);
            cacheEnabled = isTrue(property);
        }

        property = properties.getProperty("uri-for-system");
        if (property != null) {
            showConfigChange("URI-for-system: %s", property);
            uriForSystem = isTrue(property);
        }

        property = properties.getProperty("merge-https");
        if (property != null) {
            showConfigChange("Merge-https: %s", property);
            mergeHttps = isTrue(property);
        }

        property = properties.getProperty("mask-jar-uris");
        if (property != null) {
            showConfigChange("Mask-jar-URIs: %s", property);
            maskJarUris = isTrue(property);
        }

        property = properties.getProperty("catalog-loader-class");
        if (property != null) {
            showConfigChange("Catalog loader: %s", property);
            catalogLoader = property;
        }

        property = properties.getProperty("parse-rddl");
        if (property != null) {
            showConfigChange("Parse RDDL: %s", property);
            parseRddl = isTrue(property);
        }

        property = properties.getProperty("classpath-catalogs");
        if (property != null) {
            showConfigChange("Classpath catalogs: %s", property);
            classpathCatalogs = isTrue(property);
        }

        property = properties.getProperty("archived-catalogs");
        if (property != null) {
            showConfigChange("Archived catalogs: %s", property);
            archivedCatalogs = isTrue(property);
        }

        property = properties.getProperty("throw-uri-exceptions");
        if (property != null) {
            showConfigChange("Throw URI exceptions: %s", property);
            throwUriExceptions = isTrue(property);
        }

        property = properties.getProperty("resolver-logger-class");
        if (property != null) {
            showConfigChange("Resolver logger class: %s", property);
            resolverLoggerClass = property;
        }

        property = properties.getProperty("default-logger-log-level");
        if (property != null) {
            showConfigChange("Default logger log level: %s", property);
            defaultLoggerLogLevel = property;
        }

        property = properties.getProperty("access-external-entity");
        if (property != null) {
            showConfigChange("Access external entity: %s", property);
            accessExternalEntity = property;
        }

        property = properties.getProperty("access-external-document");
        if (property != null) {
            showConfigChange("Access external document: %s", property);
            accessExternalDocument = property;
        }

        property = properties.getProperty("saxparserfactory-class");
        if (property != null) {
            showConfigChange("SAXParserFactory class: %s", property);
            saxParserFactoryClass = property;
        }
    }

    private void showConfig() {
        resolverLogger.log(AbstractLogger.CONFIG, "Logging: %s", System.getProperty("xml.catalog.logging"));
        resolverLogger.log(AbstractLogger.CONFIG, "Prefer public: %s", preferPublic);
        resolverLogger.log(AbstractLogger.CONFIG, "Prefer property file: %s", preferPropertyFile);
        resolverLogger.log(AbstractLogger.CONFIG, "Allow catalog PI: %s", allowCatalogPI);
        resolverLogger.log(AbstractLogger.CONFIG, "Parse RDDL: %s", parseRddl);
        resolverLogger.log(AbstractLogger.CONFIG, "URI for system: %s", uriForSystem);
        resolverLogger.log(AbstractLogger.CONFIG, "Merge http/https: %s", mergeHttps);
        resolverLogger.log(AbstractLogger.CONFIG, "Mask jar URIs: %s", maskJarUris);
        resolverLogger.log(AbstractLogger.CONFIG, "Cache under home: %s", cacheUnderHome);
        resolverLogger.log(AbstractLogger.CONFIG, "Cache directory: %s", cacheDirectory);
        resolverLogger.log(AbstractLogger.CONFIG, "Cache enabled: %s", cacheEnabled);
        resolverLogger.log(AbstractLogger.CONFIG, "Catalog loader: %s", catalogLoader);
        resolverLogger.log(AbstractLogger.CONFIG, "Classpath catalogs: %s", classpathCatalogs);
        resolverLogger.log(AbstractLogger.CONFIG, "Archived catalogs: %s", archivedCatalogs);
        resolverLogger.log(AbstractLogger.CONFIG, "Throw URI exceptions: %s", throwUriExceptions);
        resolverLogger.log(AbstractLogger.CONFIG, "Class loader: %s", classLoader);
        resolverLogger.log(AbstractLogger.CONFIG, "Logger class: %s", resolverLoggerClass);
        resolverLogger.log(AbstractLogger.CONFIG, "Access external entity: %s", accessExternalEntity);
        resolverLogger.log(AbstractLogger.CONFIG, "Access external document: %s", accessExternalDocument);
        resolverLogger.log(AbstractLogger.CONFIG, "SAXParserFactory class: %s", saxParserFactoryClass);
        resolverLogger.log(AbstractLogger.CONFIG, "XMLReader supplier: %s", xmlReaderSupplier);

        resolverLogger.log(AbstractLogger.CONFIG, "Default logger log level: %s", defaultLoggerLogLevel);
        for (String catalog: catalogs) {
            resolverLogger.log(AbstractLogger.CONFIG, "Catalog: %s", catalog);
        }
        if (classpathCatalogs) {
            for (String catalog : findClasspathCatalogFiles()) {
                resolverLogger.log(AbstractLogger.CONFIG, "Catalog: %s", catalog);
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
     * @param data The input source that provides the catalog content.
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
     * @throws NullPointerException if the value is null for features that cannot be null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> void setFeature(ResolverFeature<T> feature, T value) {
        if (feature == ResolverFeature.CATALOG_FILES) {
            synchronized (catalogs) {
                showConfigChange("Catalog list cleared");
                catalogs.clear();
                if (value != null) {
                    for (String cat : (List<String>) value) {
                        if (!"".equals(cat.trim()) && !catalogs.contains(cat.trim())) {
                            showConfigChange("Catalog: %s", cat.trim());
                            catalogs.add(cat.trim());
                        }
                    }
                }
            }
            return;
        } else if (feature == ResolverFeature.CATALOG_ADDITIONS) {
                synchronized (catalogs) {
                    if (value == null) {
                        additionalCatalogs.clear();
                    } else {
                        for (String cat : (List<String>) value) {
                            if (!"".equals(cat.trim()) && !additionalCatalogs.contains(cat.trim())) {
                                showConfigChange("Catalog: %s", cat.trim());
                                additionalCatalogs.add(cat.trim());
                            }
                        }
                    }
                }
                return;
        } else if (feature == ResolverFeature.CLASSLOADER) {
            classLoader = (ClassLoader) value;
            if (classLoader == null) {
                classLoader = getClass().getClassLoader();
            }
            showConfigChange("Catalog loader: %s", classLoader);
            return;
        } else if (feature == ResolverFeature.CACHE_DIRECTORY) {
            cacheDirectory = (String) value;
            showConfigChange("Cache directory: %s", cacheDirectory);
            cache = null;
            return;
        } else if (feature == ResolverFeature.CACHE) {
            cache = (ResourceCache) value;
            showConfigChange("Cache: %s", cache);
            return;
        }

        if (value == null) {
            throw new NullPointerException(feature.getName() + " must not be null");
        }

        if (feature == ResolverFeature.PREFER_PUBLIC) {
            preferPublic = (Boolean) value;
            showConfigChange("Prefer public: %s", preferPublic);
        } else if (feature == ResolverFeature.PREFER_PROPERTY_FILE) {
            preferPropertyFile = (Boolean) value;
            showConfigChange("Prefer propertyFile: %s", preferPropertyFile);
        } else if (feature == ResolverFeature.ALLOW_CATALOG_PI) {
            allowCatalogPI = (Boolean) value;
            showConfigChange("Allow catalog PI: %s", allowCatalogPI);
        } else if (feature == ResolverFeature.CACHE_UNDER_HOME) {
            cacheUnderHome = (Boolean) value;
            showConfigChange("Cache under home: %s", cacheUnderHome);
            cache = null;
        } else if (feature == ResolverFeature.CACHE_ENABLED) {
            cacheEnabled = (Boolean) value;
            showConfigChange("Cache enabled: %s", cacheEnabled);
            cache = null;
        } else if (feature == ResolverFeature.CATALOG_MANAGER) {
            manager = (CatalogManager) value;
            resolverLogger.log(AbstractLogger.CONFIG, "Catalog manager: %s", manager.toString());
        } else if (feature == ResolverFeature.URI_FOR_SYSTEM) {
            uriForSystem = (Boolean) value;
            showConfigChange("URI-for-system: %s", uriForSystem);
        } else if (feature == ResolverFeature.MERGE_HTTPS) {
            mergeHttps = (Boolean) value;
            showConfigChange("Merge-https: %s", mergeHttps);
        } else if (feature == ResolverFeature.MASK_JAR_URIS) {
            maskJarUris = (Boolean) value;
            showConfigChange("Mask-jar-URIs: %s", maskJarUris);
        } else if (feature == ResolverFeature.CATALOG_LOADER_CLASS) {
            catalogLoader = (String) value;
            showConfigChange("Catalog loader: %s", catalogLoader);
        } else if (feature == ResolverFeature.PARSE_RDDL) {
            parseRddl = (Boolean) value;
            showConfigChange("Use RDDL: %s", parseRddl);
        } else if (feature == ResolverFeature.CLASSPATH_CATALOGS) {
            classpathCatalogs = (Boolean) value;
            showConfigChange("Classpath catalogs: %s", classpathCatalogs);
        } else if (feature == ResolverFeature.ARCHIVED_CATALOGS) {
            archivedCatalogs = (Boolean) value;
            showConfigChange("Archived catalogs: %s", archivedCatalogs);
        } else if (feature == ResolverFeature.THROW_URI_EXCEPTIONS) {
            throwUriExceptions = (Boolean) value;
            showConfigChange("Throw URI exceptions: %s", throwUriExceptions);
        } else if (feature == ResolverFeature.RESOLVER_LOGGER_CLASS) {
            resolverLoggerClass = (String) value;
            showConfigChange("Resolver logger class: %s", resolverLoggerClass);
            // Re-initialize the logger so that it is an instance of the requested class
            resolverLogger = null;
            resolverLogger = getFeature(ResolverFeature.RESOLVER_LOGGER);
        } else if (feature == ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL) {
            defaultLoggerLogLevel = (String) value;
            showConfigChange("Default logger log level: %s", defaultLoggerLogLevel);
        } else if (feature == ResolverFeature.RESOLVER_LOGGER) {
            resolverLogger = (ResolverLogger) value;
            showConfigChange("Resolver logger: %s", resolverLogger);
        } else if (feature == ResolverFeature.ACCESS_EXTERNAL_ENTITY) {
            accessExternalEntity = (String) value;
            showConfigChange("Access external entity: %s", accessExternalEntity);
        } else if (feature == ResolverFeature.ACCESS_EXTERNAL_DOCUMENT) {
            accessExternalDocument = (String) value;
            showConfigChange("Access external document: %s", accessExternalDocument);
        } else if (feature == ResolverFeature.SAXPARSERFACTORY_CLASS) {
            try {
                Class<?> factoryClass = Class.forName((String) value);
                Constructor<?> constructor = factoryClass.getConstructor();
                SAXParserFactory factory = (SAXParserFactory) constructor.newInstance();
                factory.setNamespaceAware(true);
                factory.setValidating(false);
                factory.setXIncludeAware(false);
                // Instantiate one so we know if it'll work.
                XMLReader reader = factory.newSAXParser().getXMLReader();
                assert reader != null;
                xmlReaderSupplier = () -> {
                    try {
                        SAXParser parser = factory.newSAXParser();
                        return parser.getXMLReader();
                    } catch (ParserConfigurationException | SAXException ex) {
                        return null;
                    }
                };
            } catch (Exception ex) {
                // It's bad, you know.
                throw new RuntimeException(ex);
            }

            saxParserFactoryClass = (String) value;
            showConfigChange("SAXParserFactory class: %s", saxParserFactoryClass);
        } else if (feature == ResolverFeature.XMLREADER_SUPPLIER) {
            saxParserFactoryClass = null;
            xmlReaderSupplier = (Supplier<XMLReader>) value;
            showConfigChange("XMLReader supplier: %s", xmlReaderSupplier);
        } else {
            resolverLogger.log(AbstractLogger.ERROR, "Ignoring unknown feature: %s", feature.getName());
        }
    }

    private void showConfigChange(String message) {
        if (showConfigChanges) {
            resolverLogger.log(AbstractLogger.CONFIG, message);
        }
    }

    private void showConfigChange(String message, Object value) {
        if (showConfigChanges) {
            if (value == null) {
                resolverLogger.log(AbstractLogger.CONFIG, message, "null");
            } else {
                resolverLogger.log(AbstractLogger.CONFIG, message, value.toString());
            }
        }
    }

    private List<String> findClasspathCatalogFiles() {
        if (classpathCatalogList == null) {
            resolverLogger.log(AbstractLogger.CONFIG, "Searching for catalogs on classpath:");
            try {
                for (URL url : ((URLClassLoader) XMLResolverConfiguration.class.getClassLoader()).getURLs()) {
                    resolverLogger.log(AbstractLogger.CONFIG, "    " + url.toString());
                }
            } catch (ClassCastException ex) {
                resolverLogger.log(AbstractLogger.CONFIG, "    Unknown: cannot determine class path in JDK9+");
            }
            ArrayList<String> catalogs = new ArrayList<>();
            try {
                Enumeration<URL> resources = XMLResolverConfiguration.class.getClassLoader().getResources("org/xmlresolver/catalog.xml");
                while (resources.hasMoreElements()) {
                    URL catalog = resources.nextElement();
                    resolverLogger.log(AbstractLogger.CONFIG, "Catalog: " + catalog.toString());
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
            List<String> cats = null;
            synchronized (catalogs) {
                cats = new ArrayList<>(catalogs);
                cats.addAll(additionalCatalogs);
            }
            if (classpathCatalogs) {
                cats.addAll(findClasspathCatalogFiles());
            }
            return (T) cats;
        } else if (feature == ResolverFeature.CATALOG_ADDITIONS) {
            List<String> cats = null;
            synchronized (catalogs) {
                cats = new ArrayList<>(additionalCatalogs);
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
        } else if (feature == ResolverFeature.CACHE_ENABLED) {
            return (T) cacheEnabled;
        } else if (feature == ResolverFeature.CLASSLOADER) {
            return (T) classLoader;
        } else if (feature == ResolverFeature.ARCHIVED_CATALOGS) {
            return (T) archivedCatalogs;
        } else if (feature == ResolverFeature.THROW_URI_EXCEPTIONS) {
            return (T) throwUriExceptions;
        } else if (feature == ResolverFeature.RESOLVER_LOGGER_CLASS) {
            return (T) resolverLoggerClass;
        } else if (feature == ResolverFeature.DEFAULT_LOGGER_LOG_LEVEL) {
            return (T) defaultLoggerLogLevel;
        } else if (feature == ResolverFeature.RESOLVER_LOGGER) {
            if (resolverLogger == null) {
                try {
                    Class<?> loggerClass = Class.forName(resolverLoggerClass);
                    Constructor<?> constructor = loggerClass.getConstructor(ResolverConfiguration.class);
                    resolverLogger = (ResolverLogger) constructor.newInstance(this);
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                        | IllegalAccessException | InvocationTargetException ex) {
                    throw new IllegalArgumentException("Failed to instantiate logger: " + resolverLoggerClass + ": " + ex.getMessage());
                }
            }
            return (T) resolverLogger;
        } else if (feature == ResolverFeature.ACCESS_EXTERNAL_ENTITY) {
            return (T) accessExternalEntity;
        } else if (feature == ResolverFeature.ACCESS_EXTERNAL_DOCUMENT) {
            return (T) accessExternalDocument;
        } else if (feature == ResolverFeature.SAXPARSERFACTORY_CLASS) {
            return (T) saxParserFactoryClass;
        } else if (feature == ResolverFeature.XMLREADER_SUPPLIER) {
            return (T) xmlReaderSupplier;
        } else {
            resolverLogger.log(AbstractLogger.ERROR, "Ignoring unknown feature: %s", feature.getName());
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

    private static class FallbackLogger extends AbstractLogger {
        private ArrayList<Message> messages = new ArrayList<>();
        private String fallbackLogging = System.getProperty("xml.catalog.FallbackLoggerLogLevel");


        @Override
        public void log(String cat, String message, Object... params) {
            messages.add(new Message(cat, message, params));
            if (fallbackLogging != null) {
                // If you turned this on, you must be desperate.
                System.err.println(logMessage(cat, message, params));
            }
        }

        @Override
        public void warn(String message) {
            // never called
        }

        @Override
        public void info(String message) {
            // never called
        }

        @Override
        public void debug(String message) {
            // never called
        }

        public void forward(ResolverLogger logger) {
            for (Message message : messages) {
                logger.log(message.category, message.message, message.params);
            }
            messages.clear();
        }

        private static class Message {
            public final String category;
            public final String message;
            public final Object[] params;
            public Message(String level, String message, Object... params) {
                this.category = level;
                this.message = message;
                this.params = params;
            }
        }
    }
}
