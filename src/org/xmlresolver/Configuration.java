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
  
  private static boolean isTrue(String aString) {
    return "true".equalsIgnoreCase(aString) || "yes".equalsIgnoreCase(aString) || "1".equalsIgnoreCase(aString);
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
          Catalog.logger.warning("Cannot find " + fn);
          continue;
        }
        Properties properties = new Properties();
        try {
          properties.load(in);
          return new Configuration(properties, url);
        } catch (IOException e) {
          Catalog.logger.warning("I/O error reading " + fn);
        }
      }
    }
    return new Configuration(null, null);
  }
  
  private final Properties properties;
  private final URL propertiesFileUri;

  public Configuration(Properties properties, URL propertiesFileUri) {
    this.properties = properties;
    this.propertiesFileUri = propertiesFileUri;
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
            Catalog.logger.warning("Could not create default cache directory: " + dir);
          } else {
            cacheDir = dir;
          }
        }
      }
    }
    return cacheDir;
  }

  public String queryVerbosity() {
    return getProperty("xml.catalog.verbosity", "verbosity");
  }

  public boolean queryCacheSchemeURI(String scheme) {
    if (scheme == null) {
      return false;
    }
    String prop = getProperty("xml.catalog.cache." + scheme, "cache-" + scheme + "-uri");
    if (prop == null) {
      return !"file".equals(scheme);
    } else {
      return isTrue(prop);
    }
  }
}
