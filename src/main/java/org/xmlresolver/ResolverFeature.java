package org.xmlresolver;

import org.xmlresolver.cache.ResourceCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
     * the same <emphasis>for the purpose of comparison in the catalog</emphasis>.</p>
     *
     * <p>This option has no effect on the URIs returned; it only influences catalog URI comparisons.</p>
     */
    public static final ResolverFeature<Boolean> MERGE_HTTPS = new ResolverFeature<>(
            "http://xmlresolver.org/feature/merge-https", true);

}
