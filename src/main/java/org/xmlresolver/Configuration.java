package org.xmlresolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Encapsulates the configuration logic of the XML catalog resolver.
 *
 * @author ndw
 * @author swachter
 */
public class Configuration {
    private final Properties properties;
    private final URL propertiesFileUri;

    public Configuration(Properties properties, URL propertiesFileUri) {
        Catalog.logger.debug("XMLResolver version " + BuildConfig.VERSION);
        Catalog.logger.debug("Loading xmlresolver.properties: " + propertiesFileUri);
        this.properties = properties;
        this.propertiesFileUri = propertiesFileUri;
    }

    private static boolean isTrue(String aString) {
        if (aString == null) {
            return false;
        } else {
            return "true".equalsIgnoreCase(aString) || "yes".equalsIgnoreCase(aString) || "1".equalsIgnoreCase(aString);
        }
    }

    public static Configuration create(String aPropertiesFiles) {
        if (aPropertiesFiles != null) {
            for (String fn : aPropertiesFiles.split(";")) {
                if ("".equals(fn)) continue;
                URL url = Catalog.class.getResource("/" + fn);
                if (url == null) {
                    continue;
                }
                InputStream in = Catalog.class.getResourceAsStream("/" + fn);
                if (in == null) {
                    Catalog.logger.warn("Cannot find " + fn);
                    continue;
                }
                Properties properties = new Properties();
                try {
                    properties.load(in);
                    return new Configuration(properties, url);
                } catch (IOException e) {
                    Catalog.logger.warn("I/O error reading " + fn);
                }
            }
        }

        Configuration config = fromFilename(System.getProperty("xmlresolver.properties"));
        if (config == null) {
            config = fromFilename(System.getenv("XMLRESOLVER_PROPERTIES"));
        }
        if (config == null) {
            config = fromProperties(Catalog.class.getResource("/xmlresolver.properties"));
        }
        if (config == null) {
            config = new Configuration(null, null);
        }

        return config;
    }

    private static Configuration fromFilename(String filename) {
        Configuration config = null;

        if (filename != null) {
            try {
                URL propURL = new URL(filename);
                config = fromProperties(propURL);
            } catch (MalformedURLException mue) {
                Catalog.logger.warn("Malformed xmlresolver.properties URL: " + filename);
            }
        }

        return config;
    }

    private static Configuration fromProperties(URL propurl) {
        Configuration config = null;

        if (propurl != null) {
            Properties properties = new Properties();
            try {
                properties.load(propurl.openStream());
                config = new Configuration(properties, propurl);
            } catch (IOException e) {
                Catalog.logger.warn("I/O error reading " + propurl);
            }
        }

        return config;
    }

    private String getProperty(String aPropertyName) {
        return properties != null ? properties.getProperty(aPropertyName) : null;
    }

    private String getProperty(String aSystemPropertyName, String aPropertyName) {
        String res = System.getProperty(aSystemPropertyName);
        return res != null ? res: getProperty(aPropertyName);
    }

    public Vector<String> queryCatalogFiles() {
        String catalogList = System.getProperty("xml.catalog.files");
        boolean fromPropertiesFile = false;
        boolean relativeCatalogs = true;
        if (properties != null) {
            if (catalogList == null) {
                catalogList = properties.getProperty("catalogs");
                if (catalogList != null) {
                    fromPropertiesFile = true;
                }
            }
            String allow = properties.getProperty("relative-catalogs");
            if (allow != null) {
                relativeCatalogs = isTrue(allow);
            }
        }
        if (catalogList == null) {
            catalogList = "./catalog.xml";
        }
        Vector<String> catalogFiles = new Vector<String>();
        StringTokenizer tokens = new StringTokenizer(catalogList, ";");
        while (tokens.hasMoreTokens()) {
            String catalogFile = tokens.nextToken();
            URL absURI = null;
            if (fromPropertiesFile && !relativeCatalogs) {
                try {
                    absURI = new URL(propertiesFileUri, catalogFile);
                    catalogFile = absURI.toString();
                } catch (MalformedURLException mue) {
                    // nop
                }
            }
            catalogFiles.add(catalogFile);
        }
        return catalogFiles;
    }

    public boolean queryPreferPublic() {
        String prefer = getProperty("xml.catalog.prefer", "prefer");
        return prefer == null || "public".equalsIgnoreCase(prefer);
    }

    public boolean queryAllowPI() {
        String allowpi = getProperty("xml.catalog.allowPI", "allow-oasis-xml-catalog-pi");
        return isTrue(allowpi);
    }

    public String queryCache() {
        String cacheDir = getProperty("xml.catalog.cache", "cache");
        if (cacheDir == null) {

            if (isTrue(getProperty("xml.catalog.cacheUnderHome", "cacheUnderHome"))) {
                // Let's see if we can find a reasonable default...
                String home = System.getProperty("user.home");
                if (home != null && !"".equals(home)) {
                    String dir = home + "/.xmlresolver/cache";
                    File fDir = new File(dir);
                    if (!fDir.exists()) {
                        fDir.mkdirs();
                    }
                    if (!fDir.exists() || !fDir.isDirectory()) {
                        Catalog.logger.warn("Could not create default cache directory: " + dir);
                    } else {
                        cacheDir = dir;
                    }
                }
            }
        }
        return cacheDir;
    }

    public boolean queryCacheSchemeURI(String scheme) {
        if (scheme == null) {
            return false;
        }
        String prop = getProperty("xml.catalog.cache." + scheme, "cache-" + scheme + "-uri");
        if (prop == null) {
            // FIXME: should probably base this test on the beginning of the absolute URI instead of the scheme.
            // The jar: scheme is odd. I definitely don't want to cache jar:file:... URIs, but if a
            // jar:https:... URI came through it probably would be nice to cache that. However, for the
            // moment, we're just going to skip jar: URIs because that's all the control we have
            return !("file".equals(scheme) || "jar".equals(scheme));
        } else {
            return isTrue(prop);
        }
    }
}
