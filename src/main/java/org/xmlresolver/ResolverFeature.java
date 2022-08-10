package org.xmlresolver;

import com.thaiopensource.resolver.xml.sax.SAX;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlresolver.cache.ResourceCache;
import org.xmlresolver.logging.ResolverLogger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;
import java.util.function.Supplier;

/**
 * An individual resolver feature. The complete set of known features is instantiated as a
 * collection of static final fields.
 *
 * @param <T> The type of the feature.
 */

public class ResolverFeature<T> {
    private String name = null;
    private T defaultValue = null;

    private static final Map<String, ResolverFeature<?>> index = new TreeMap<String, ResolverFeature<?>>();

    protected ResolverFeature(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        index.put(name, this);
    }

    /**
     * Get the name of the feature.
     *
     * @return The feature name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the default value of the feature.
     *
     * @return The feature default value.
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Find a known static feature by name.
     *
     * @param name The feature name.
     * @return The instance of that feature.
     */
    public static ResolverFeature<?> byName(String name) {
        return index.get(name);
    }

    /**
     * Iterates over all of the known feature names.
     *
     * @return An iterator over the feature names.
     */
    public static Iterator<String> getNames() {
        return index.keySet().iterator();
    }

    /**
     * Sets the list of catalog files.
     */
    public static final ResolverFeature<List<String>> CATALOG_FILES = new ResolverFeature<>(
            "http://xmlresolver.org/feature/catalog-files", Collections.unmodifiableList(new ArrayList<>()));

    /**
     * Sets the list of additional catalog files.
     */
    public static final ResolverFeature<List<String>> CATALOG_ADDITIONS = new ResolverFeature<>(
            "http://xmlresolver.org/feature/catalog-additions", Collections.unmodifiableList(new ArrayList<>()));

    /**
     * Determines whether or not public IDs are preferred..
     */
    public static final ResolverFeature<Boolean> PREFER_PUBLIC = new ResolverFeature<>(
            "http://xmlresolver.org/feature/prefer-public", true);

    /**
     * Determines whether property file values are preferred over
     * system property values.
     *
     * <p>In earlier versions of this API, this was effectively always true.
     * The default is now false which allows system property values to override property file values.
     * Set this to <code>true</code> in your property file to preserve the old behavior.</p>
     */
    public static final ResolverFeature<Boolean> PREFER_PROPERTY_FILE = new ResolverFeature<>(
            "http://xmlresolver.org/feature/prefer-property-file", false);

    /**
     * Determines whether or not the catalog PI in a document
     * may change the list of catalog files to be consulted.
     *
     * <p>It defaults to <code>true</code>, but there's a small performance cost. Each parse needs
     * it's own copy of the configuration if you enable this feature (otherwise, the PI in one document
     * might have an effect on other documents). If you know you aren't using the PI, it might be sensible
     * to make this <code>false</code>.</p>
     */
    public static final ResolverFeature<Boolean> ALLOW_CATALOG_PI = new ResolverFeature<>(
            "http://xmlresolver.org/feature/allow-catalog-pi", true);

    /**
     * Sets the location of the cache directory.
     *
     * <p>If the value
     * is <code>null</code>, and <code>CACHE_UNDER_HOME</code> is <code>false</code>, no cache will
     * be used.</p>
     */
    public static final ResolverFeature<String> CACHE_DIRECTORY = new ResolverFeature<>(
            "http://xmlresolver.org/feature/cache-directory", (String) null);

    /**
     * Determines if a default cache location of <code>.xmlresolver.org/cache</code>
     * under the users home directory should be used for the cache.
     *
     * <p>This only applies if <code>CATALOG_CACHE</code>
     * is <code>null</code>.</p>
     */
    public static final ResolverFeature<Boolean> CACHE_UNDER_HOME = new ResolverFeature<>(
            "http://xmlresolver.org/feature/cache-under-home", true);

    /**
     * Provides access to the {@link ResourceCache} that the resolver is using.
     */
    public static final ResolverFeature<ResourceCache> CACHE = new ResolverFeature<>(
            "http://xmlresolver.org/feature/cache", (ResourceCache) null);

    /**
     * Provides access to the {@link CatalogManager} that
     * the resolver is  using.
     */
    public static final ResolverFeature<CatalogManager> CATALOG_MANAGER = new ResolverFeature<>(
            "http://xmlresolver.org/feature/catalog-manager", (CatalogManager) null);

    /**
     * Determines whether or not <code>uri</code> catalog entries
     * can be used to resolve external identifiers.
     *
     * <p>This only applies if resolution fails through
     * system and public entries.</p>
     */
    public static final ResolverFeature<Boolean> URI_FOR_SYSTEM = new ResolverFeature<>(
            "http://xmlresolver.org/feature/uri-for-system", true);

    /**
     * Determines whether http: and https: URIs compare the same.
     *
     * <p>Historically, most web servers used <code>http:</code>, now most use <code>https:</code>.
     * There are existing catalogs that can't practically be updated that use <code>http:</code> for
     * system identifiers and URIs. But authors copying and pasting are likely to get <code>https:</code>
     * URIs. If this option is true, then <code>http:</code> and <code>https:</code> are considred
     * the same <em>for the purpose of comparison in the catalog</em>.</p>
     *
     * <p>This option has no effect on the URIs returned; it only influences catalog URI comparisons.</p>
     */
    public static final ResolverFeature<Boolean> MERGE_HTTPS = new ResolverFeature<>(
            "http://xmlresolver.org/feature/merge-https", true);

    /**
     * Determines whether a classpath: or jar: URI is returned by the resolver.
     *
     * <p>When the resolver finds a resource, for example a schema or a stylesheet, it returns
     * the location of the resolved resource as the base URI for the resource. This enables
     * the following common scenario:</p>
     *
     * <ul>
     *     <li>Download a distribution and store it locally.</li>
     *     <li>Create a catalog that maps from the entry point(s) into the local
     *     distribution: <code>http://example.com/acme-schema/start/here</code> -&gt; <code>/opt/acme-schema-1.0/here</code></li>
     *     <li>Profit.</li>
     * </ul>
     *
     * <p>A document requests <code>http://example.com/acme-schema/start/here</code>, the resolver
     * returns <code>/opt/achme-schema-1.0/here</code>. When the schema attempts to import a library,
     * the URI for that library is resolved against the base URI on the filesystem, a new path is
     * constructed, and it all just works.</p>
     *
     * <p>Adding support for <code>classpath</code> and <code>jar:</code> URIs to XML Resolver 3.0 has enabled another
     * very attractive scenario:</p>
     *
     * <ul>
     *     <li>Put the distribution in a jar file with an included catalog.</li>
     *     <li>Arrange for the project to depend on that jar file, so it'll be on the classpath.</li>
     *     <li>Add <code>classpath:/org/example/acme-schema/catalog.xml</code> to your catalog list.</li>
     *     <li>More profit!</li>
     * </ul>
     *
     * <p>Trouble is, the resolved URI will be something like this:</p>
     *
     * <p><code>jar:file:///where/the/jar/is/acme-schema-1.0.jar!/org/example/acme-schema/here</code></p>
     *
     * <p>And the trouble with that is, Java doesn't think the <code>classpath:</code> and <code>jar:</code>
     * URI schemes are hierarchical and won't resolve the URI for the imported library correctly. It will
     * work just fine for any document that doesn't include parts with relative URIs. The DocBook schema,
     * for example, is distributed as a single RELAX NG file, so it works. But the xslTNG stylesheets would
     * not.</p>
     *
     * <p>If <code>MASK_JAR_URIS</code> is true, the resolver will return the local resource from the jar
     * file, but will leave the URI unchanged. As long as the catalog has a mapping for all of the resources,
     * and not just the entry point(s), this will do exactly the right thing.</p>
     *
     * <p>Often, this can be achieved with, for example, a rewrite rule:</p>
     *
     * <pre>&lt;rewriteURI uriStartString="http://example.com/acme-start/"
     *             rewritePrefix="acme-start/"&gt;
     * </pre>
     *
     * <p>Assuming the catalog is in a location where that rewrite prefix works, the entry point
     * will be remapped and the local resource returned. The resource it imports will be resolved against
     * the http: URI, but that will also be remapped, and everyone wins.</p>
     *
     * <p>You don't need to use a rewrite rule, you can use any combination of catalog rules you like
     * as long as each of the requested URIs will be mapped.</p>
     */
    public static final ResolverFeature<Boolean> MASK_JAR_URIS = new ResolverFeature<>(
            "http://xmlresolver.org/feature/mask-jar-uris", true);

    /**
     * Identifies the catalog loader class.
     *
     * <p>The default catalog loader class is usually fine. The validating class can be used
     * to enforce schema validity checks on loaded catalogs.</p>
     */
    public static final ResolverFeature<String> CATALOG_LOADER_CLASS = new ResolverFeature<>(
            "http://xmlresolver.org/feature/catalog-loader-class", "org.xmlresolver.loaders.XmlLoader");

    /**
     * Controls whether or not namespace documents will be parsed for RDDL annotations.
     *
     * <p>If this feature is enabled, then if an attempt to get a namespace returns an HTML document,
     * and if a nature and purpose has been specified in the request, the HTML document will be parsed
     * for RDDL annotations. If a matching entry is found, the annotated URI will be used.</p>
     *
     * <p>In the {@link org.w3c.dom.ls.LSResourceResolver}, if the type of the request is the XML
     * Schema or RELAX NG namespace, the purpose is assumed to be validation.</p>
     */
    public static final ResolverFeature<Boolean> PARSE_RDDL = new ResolverFeature<>(
            "http://xmlresolver.org/feature/rddl", true);

    /**
     * Determines whether or not catalogs on the classpath should be loaded automatically.
     *
     * <p>If this feature is enabled, then the resolver will attempt to find and load all
     * of the catalogs named <code>org/xmlresolver/catalog.xml</code> on the classpath.
     * These will be added to the of the catalogs loaded from properties.</p>
     */
    public static final ResolverFeature<Boolean> CLASSPATH_CATALOGS = new ResolverFeature<>(
            "http://xmlresolver.org/feature/classpath-catalogs", true);

    /**
     * Identify the ClassLoader to use for accessing resources on the classpath.
     *
     * <p>A {@link ClassLoader} is used to access the class path and load resources
     * from the class path. By default the resolver configuration uses the class
     * loader obtained from the class:</p>
     *
     * <pre>getClass().getClassLoader()</pre>
     *
     * <p>If you're using the resolver in an environment where an alternate class
     * loader is required, you can specify it with this feature.</p>
     */
    public static final ResolverFeature<ClassLoader> CLASSLOADER = new ResolverFeature<>(
            "http://xmlresolver.org/feature/classloader", null);

    /**
     * Adds support for placing ZIP files on the catalog path.
     *
     * <p>If enabled, then you can put ZIP files on the catalog path. The resolver
     * will look for <code>catalog.xml</code> and <code>org/xmlresolver/catalog.xml</code>,
     * using whichever it finds first. If it doesn't find either, the file is ignored.</p>
     */
    public static final ResolverFeature<Boolean> ARCHIVED_CATALOGS = new ResolverFeature<>(
            "http://xmlresolver.org/feature/archived-catalogs", true);

    /**
     * Allows the resolver to throw exceptions for invalid URIs.
     *
     * <p>If enabled, when the resolver attempts to process a URI, if the processing
     * raises an exception, that exception will be thrown rather than suppressed. For
     * checked exceptions, such as <code>URISyntaxException</code>, what's thrown will
     * be a <code>IllegalArgumentException</code> wrapping the underlying exception.</p>
     */
    public static final ResolverFeature<Boolean> THROW_URI_EXCEPTIONS = new ResolverFeature<>(
            "http://xmlresolver.org/feature/throw-uri-exceptions", false);

    /**
     * Identifies the resolver logger class.
     *
     * <p>Any class that implements {@link org.xmlresolver.logging.ResolverLogger} can be used.
     * XML Resolver ships with two implementations:</p>
     *
     * <ul>
     *     <li>The {@link org.xmlresolver.logging.DefaultLogger org.xmlresolver.logging.DefaultLogger} class writes log messages
     *     to <code>System.err</code>.</li>
     *     <li>The {@link org.xmlresolver.logging.SystemLogger org.xmlresolver.logging.SystemLogger} class uses
     *     a logging backend. (You must configure a concrete backend for the logging facade on your
     *     classpath.)</li>
     * </ul>
     *
     */
    public static final ResolverFeature<String> RESOLVER_LOGGER_CLASS = new ResolverFeature<>(
            "http://xmlresolver.org/feature/resolver-logger-class", "org.xmlresolver.logging.DefaultLogger");

    /**
     * Identifies the resolver logger.
     *
     * <p>This feature can get and set an instance of the logger. The logger is usually
     * configured with the <code>RESOLVER_LOGGER_CLASS</code> feature. If the logger
     * has been configured via <code>RESOLVER_LOGGER_CLASS</code>, an attempt to get the <code>RESOLVER_LOGGER</code>
     * will instantiate the class (if it hasn't already been instantiated).</p>
     */
    public static final ResolverFeature<ResolverLogger> RESOLVER_LOGGER = new ResolverFeature<>(
            "http://xmlresolver.org/feature/resolver-logger", null);

    /**
     * Specify the logging level for the default logger.
     *
     * <p>This feature only applies to the {@link org.xmlresolver.logging.DefaultLogger} (or other classes
     * that use it). In particular, it has no effect if the {@link org.xmlresolver.logging.SystemLogger} is used.
     * How that is configured depends on the concrete backend selected at runtime.</p>
     */
    public static final ResolverFeature<String> DEFAULT_LOGGER_LOG_LEVEL = new ResolverFeature<>(
            "http://xmlresolver.org/feature/default-logger-log-level", "warn");

    /**
     * Specify the protocols allowed for entity lookup.
     *
     * <p>This feature restricts the protocols that are allowed during entity resolution. If an
     * attempt is made to resolve an entity and the system identifier for that entity uses a protocol
     * not listed in this feature, the request is rejected. See JAXP 185.</p>
     * <p>The keyword "all" allows all protocols. An empty list allows none.</p>
     */
    public static final ResolverFeature<String> ACCESS_EXTERNAL_ENTITY = new ResolverFeature<>(
            "http://xmlresolver.org/feature/access-external-entity", "all");

    /**
     * Specify the protocols allowed for URI lookup.
     *
     * <p>This feature restricts the protocols that are allowed during URI resolution. If an
     * attempt is made to resolve a document and the URI for that document uses a protocol
     * not listed in this feature, the request is rejected. See JAXP 185.</p>
     * <p>The keyword "all" allows all protocols. An empty list allows none.</p>
     */
    public static final ResolverFeature<String> ACCESS_EXTERNAL_DOCUMENT = new ResolverFeature<>(
            "http://xmlresolver.org/feature/access-external-document", "all");

    /**
     * Is the cache enabled?
     *
     * <p>If the cache <em>is not</em> enabled, no attempt will be made to create or use a cache.</p>
     */
    public static final ResolverFeature<Boolean> CACHE_ENABLED = new ResolverFeature<>(
            "http://xmlresolver.org/feature/cache-enabled", false);

    /**
     * Identify the SAXParserFactory class.
     *
     * <p>If unconfigured, parsers are created with {@link #XMLREADER_SUPPLIER}.</p>
     *
     * <p>This feature and the {@link #XMLREADER_SUPPLIER} are different mechanisms for
     * configuring the same underlying feature: how does the resolver get an XML parser if it needs one? (For
     * example, it needs one to parse the cache, but the {@link org.xmlresolver.tools.ResolvingXMLFilter ResolvingXMLFilter} and
     * {@link org.xmlresolver.tools.ResolvingXMLReader ResolvingXMLReader} also use this mechanism.)</p>
     *
     * <p>The <code>SAXPARSERFACTORY_CLASS</code> is initially <code>null</code> and the
     * <code>XMLREADER_SUPPLIER</code> is used. The purpose of the <code>SAXPARSERFACTORY_CLASS</code>
     * is to allow the factory to be configured with a property since the {@link #XMLREADER_SUPPLIER} cannot.</p>
     *
     * <p>If a <code>SAXPARSERFACTORY_CLASS</code> is specified, it will be used in favor of the default
     * <code>XMLREADER_SUPPLIER.</code> If an <code>XMLREADER_SUPPLIER</code> is explicitly set after the
     * configuration is initialized, it will set this feature to <code>null</code> and take precedence.</p>
     */
    public static final ResolverFeature<String> SAXPARSERFACTORY_CLASS = new ResolverFeature<>(
            "http://xmlresolver.org/feature/saxparserfactory-class", null);

    /**
     * Configure the default XML reader.
     *
     * <p>The default supplier is obtained with <code>SAXParserFactory.newInstance()</code> and
     * the global mechanisms that it uses.</p>
     *
     * <p>This feature and the {@link #SAXPARSERFACTORY_CLASS} are different mechanisms for
     * configuring the same underlying feature: how does the resolver get an XML parser if it needs one? (For
     * example, it needs one to parse the cache, but the {@link org.xmlresolver.tools.ResolvingXMLFilter ResolvingXMLFilter} and
     * {@link org.xmlresolver.tools.ResolvingXMLReader ResolvingXMLReader} also use this mechanism.)</p>
     *
     * <p>The {@link #SAXPARSERFACTORY_CLASS} is initially <code>null</code> and the
     * <code>XMLREADER_SUPPLIER</code> is used. The purpose of the <code>SAXPARSERFACTORY_CLASS</code>
     * is to allow the factory to be configured with a property since the <code>XMLREADER_SUPPLIER</code> cannot.</p>
     *
     * <p>If a <code>SAXPARSERFACTORY_CLASS</code> is specified, it will be used in favor of the default
     * <code>XMLREADER_SUPPLIER.</code> If an <code>XMLREADER_SUPPLIER</code> is explicitly set after the
     * configuration is initialized, it will set this feature to <code>null</code> and take precedence.</p>
     */
    public static final ResolverFeature<Supplier<XMLReader>> XMLREADER_SUPPLIER = new ResolverFeature<>(
            "http://xmlresolver.org/feature/xmlreader-supplier", () -> {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            factory.setXIncludeAware(false);
            SAXParser parser = factory.newSAXParser();
            return parser.getXMLReader();
        } catch (ParserConfigurationException| SAXException ex) {
            return null;
        }
    });

    /**
     * Fix backslashes in system identifiers on Windows?
     * <p>System identifiers are URIs and URIs may not contain un-escaped backslashes. However, Windows
     * uses the backslash as the path separator and it's not uncommon for filenames to appear in system
     * identifiers. If this flag is set, the resolver will coerce backslashes into forward slashes in
     * system identifiers.</p>
     */
    public static final ResolverFeature<Boolean> FIX_WINDOWS_SYSTEM_IDENTIFIERS = new ResolverFeature<>(
            "http://xmlresolver.org/feature/fix-windows-system-identifiers", false);


}
