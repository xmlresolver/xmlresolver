package org.xmlresolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class XMLResolverConfiguration implements ResolverConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(XMLResolverConfiguration.class);
    private static final ResolverFeature<?>[] knownFeatures = { ResolverFeature.CATALOG_FILES,
            ResolverFeature.PREFER_PUBLIC, ResolverFeature.ALLOW_CATALOG_PI,
            ResolverFeature.CATALOG_CACHE, ResolverFeature.CACHE_UNDER_HOME,
            ResolverFeature.CACHE_EXCLUDE_REGEX, ResolverFeature.CACHE_INCLUDE_REGEX };

    private List<String> catalogs = new ArrayList<>();
    private Boolean preferPublic = ResolverFeature.PREFER_PUBLIC.getDefaultValue();
    private Boolean preferPropertyFile = ResolverFeature.PREFER_PROPERTY_FILE.getDefaultValue();
    private Boolean allowCatalogPI = ResolverFeature.ALLOW_CATALOG_PI.getDefaultValue();
    private String cache = ResolverFeature.CATALOG_CACHE.getDefaultValue();
    private Boolean cacheUnderHome = ResolverFeature.CACHE_UNDER_HOME.getDefaultValue();
    private List<String> cacheInclude = new ArrayList<>();
    private List<String> cacheExclude = new ArrayList<>();

    public XMLResolverConfiguration() {
        logger.debug("XMLResolver version " + BuildConfig.VERSION);
        catalogs.addAll(ResolverFeature.CATALOG_FILES.getDefaultValue());
        cacheInclude.addAll(ResolverFeature.CACHE_INCLUDE_REGEX.getDefaultValue());
        cacheExclude.addAll(ResolverFeature.CACHE_EXCLUDE_REGEX.getDefaultValue());
    }

    public void loadDefaultConfiguration() {
        // This is slightly odd. Historically, values in the properties file took precedence over
        // system property values. I think that's probably backwards, but I want to make it possible
        // to have backwards compatible behavior. So, if preferPropertFile is true, things stay the
        // way they always have been. Otherwise, we re-load the system properties after the properties
        // file. This allows, for example, xml.catalog.files to override the properties file.
        loadSystemPropertiesConfiguration();
        loadDefaultPropertiesConfiguration();
        if (!preferPropertyFile) {
            loadSystemPropertiesConfiguration();
        }
        if (catalogs.isEmpty()) {
            catalogs.add("./catalog.xml");
        }
    }

    public void loadSystemPropertiesConfiguration() {
        String property = System.getProperty("xml.catalog.files");
        if (property != null) {
            StringTokenizer tokens = new StringTokenizer(property, ";");
            catalogs.clear();
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!"".equals(token.trim())) {
                    catalogs.add(token);
                }
            }
        }

        property = System.getProperty("xml.catalog.prefer");
        if (property != null) {
            preferPublic = "public".equals(property);
        }

        property = System.getProperty("xml.catalog.preferPropertyFile");
        if (property != null) {
            preferPropertyFile = isTrue(property);
        }

        property = System.getProperty("xml.catalog.allowPI");
        if (property != null) {
            allowCatalogPI = isTrue(property);
        }

        property = System.getProperty("xml.catalog.cache");
        if (property != null) {
            cache = property;
        }

        property = System.getProperty("xml.catalog.cacheUnderHome");
        if (property != null) {
            cacheUnderHome = isTrue(property);
        }

        boolean explicitFile = false;
        boolean explicitJar = false;
        cacheInclude.clear();
        cacheExclude.clear();
        // Hack to convert the scheme properties to regex
        for (Object propObj : System.getProperties().keySet()) {
            if (propObj instanceof String) {
                String prop = (String) propObj;
                explicitFile = explicitFile || "xml.catalog.cache.file".equals(prop);
                explicitJar = explicitJar || "xml.catalog.cache.jar".equals(prop);
                if (prop.startsWith("xml.catalog.cache.")) {
                    // It's not that I don't trust you, but...
                    String scheme = prop.substring(18).toLowerCase().replaceAll("[^a-z0-9]", "");
                    boolean include = isTrue(System.getProperty(prop));
                    if (include) {
                        cacheInclude.add("^" + scheme + ":");
                    } else {
                        cacheExclude.add("^" + scheme + ":");
                    }
                }
            }
        }
        if (!explicitFile) {
            cacheExclude.add("^file:");
        }
        if (!explicitJar) {
            cacheExclude.add("^jar:");
        }
    }

    public void loadDefaultPropertiesConfiguration() {
        String propfn = System.getProperty("xmlresolver.properties");
        if (propfn == null) {
            propfn = System.getenv("XMLRESOLVER_PROPERTIES");
        }

        try {
            if (propfn == null) {
                URL jarprop = Catalog.class.getResource("/xmlresolver.properties");
                if (jarprop != null) {
                    loadPropertiesConfiguration(jarprop);
                }
            } else {
                loadPropertiesConfiguration(new URL(propfn));
            }
        } catch (MalformedURLException e) {
            logger.debug("Malformed property file URI: " + propfn);
        }
    }

    public void loadPropertiesConfiguration(URL propertiesURL) {
        try {
            Properties props = new Properties();
            props.load(propertiesURL.openStream());
            loadPropertiesConfiguration(propertiesURL, props);
        } catch (IOException e) {
            logger.debug("Failed to load properties from " + propertiesURL);
        }
    }

    public void loadPropertiesConfiguration(Properties properties) {
        loadPropertiesConfiguration(null, properties);
    }

    public void loadPropertiesConfiguration(URL propertiesURL, Properties properties) {
        String property = properties.getProperty("catalogs");
        if (property != null) {
            boolean relative = true;
            String allow = properties.getProperty("relative-catalogs");
            if (allow != null) {
                relative = isTrue(allow);
            }
            StringTokenizer tokens = new StringTokenizer(property, ";");
            catalogs.clear();
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!"".equals(token.trim())) {
                    if (relative && propertiesURL != null) {
                        try {
                            token = new URL(propertiesURL, token).toURI().toASCIIString();
                        } catch (URISyntaxException | MalformedURLException e) {
                            logger.debug("Cannot make absolute: " + token);
                        }
                    }
                    catalogs.add(token);
                }
            }
        }

        property = properties.getProperty("prefer");
        if (property != null) {
            preferPublic = "public".equals(property);
        }

        property = properties.getProperty("prefer-property-file");
        if (property != null) {
            preferPropertyFile = isTrue(property);
        }

        property = properties.getProperty("allow-oasis-xml-catalog-pi");
        if (property != null) {
            allowCatalogPI = isTrue(property);
        }

        property = properties.getProperty("cache");
        if (property != null) {
            cache = property;
        }

        property = properties.getProperty("cacheUnderHome");
        if (property == null) {
            property = properties.getProperty("cache-under-home");
        }
        if (property != null) {
            cacheUnderHome = isTrue(property);
        }

        boolean explicitFile = false;
        boolean explicitJar = false;
        cacheInclude.clear();
        cacheExclude.clear();
        // Hack to convert the scheme properties to regex
        for (String prop : properties.stringPropertyNames()) {
            // cache-{scheme}-uri
            explicitFile = explicitFile || "cache-file-uri".equals(prop);
            explicitJar = explicitJar || "cache-jar-uri".equals(prop);
            if (prop.startsWith("cache-") && prop.endsWith("-uri")) {
                String scheme = prop.substring(6);
                scheme = scheme.substring(0, scheme.length() - 4);
                // It's not that I don't trust you, but...
                scheme = scheme.toLowerCase().replaceAll("[^a-z0-9]", "");
                boolean include = isTrue(properties.getProperty(prop));
                if (include) {
                    cacheInclude.add("^" + scheme + ":");
                } else {
                    cacheExclude.add("^" + scheme + ":");
                }
            }
        }
        if (!explicitFile) {
            cacheExclude.add("^file:");
        }
        if (!explicitJar) {
            cacheExclude.add("^jar:");
        }
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
        } else if (feature == ResolverFeature.CATALOG_CACHE) {
            cache = (String) value;
        } else if (feature == ResolverFeature.CACHE_UNDER_HOME) {
            cacheUnderHome = (Boolean) value;
        } else if (feature == ResolverFeature.CACHE_EXCLUDE_REGEX) {
            cacheExclude = (List<String>) value;
        } else if (feature == ResolverFeature.CACHE_INCLUDE_REGEX) {
            cacheInclude = (List<String>) value;
        } else {
            logger.debug("Ignoring unknown feature: " + feature.getName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getFeature(ResolverFeature<T> feature) {
        if (feature == ResolverFeature.CATALOG_FILES) {
            return (T) catalogs;
        } else if (feature == ResolverFeature.PREFER_PUBLIC) {
            return (T) preferPublic;
        } else if (feature == ResolverFeature.PREFER_PROPERTY_FILE) {
            return (T) preferPropertyFile;
        } else if (feature == ResolverFeature.ALLOW_CATALOG_PI) {
            return (T) allowCatalogPI;
        } else if (feature == ResolverFeature.CATALOG_CACHE) {
            return (T) cache;
        } else if (feature == ResolverFeature.CACHE_UNDER_HOME) {
            return (T) cacheUnderHome;
        } else if (feature == ResolverFeature.CACHE_EXCLUDE_REGEX) {
            return (T) cacheExclude;
        } else if (feature == ResolverFeature.CACHE_INCLUDE_REGEX) {
            return (T) cacheInclude;
        } else {
            logger.debug("Ignoring unknown feature: " + feature.getName());
            return null;
        }
    }

    @Override
    public Iterator<ResolverFeature<?>> getFeatures() {
         return Arrays.stream(knownFeatures).iterator();
    }

    public List<String> queryCatalogFiles() {
        return Collections.unmodifiableList(catalogs);
    }

    private static boolean isTrue(String aString) {
        if (aString == null) {
            return false;
        } else {
            return "true".equalsIgnoreCase(aString) || "yes".equalsIgnoreCase(aString) || "1".equalsIgnoreCase(aString);
        }
    }
}
