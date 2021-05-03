package org.xmlresolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ResolverFeature<T> {
    private String name = null;
    private T defaultValue = null;

    private static final Map<String, ResolverFeature<?>> index = new TreeMap<String, ResolverFeature<?>>();

    protected ResolverFeature(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public static ResolverFeature<?> byName(String name) {
        return index.get(name);
    }

    public static Iterator<String> getNames() {
        return index.keySet().iterator();
    }

    public static final ResolverFeature<List<String>> CATALOG_FILES = new ResolverFeature<List<String>>(
            "http://xmlresolver.org/feature/catalog-files", Collections.unmodifiableList(new ArrayList<String>()));

    public static final ResolverFeature<Boolean> PREFER_PUBLIC = new ResolverFeature<Boolean>(
            "http://xmlresolver.org/feature/prefer-public", true);

    public static final ResolverFeature<Boolean> PREFER_PROPERTY_FILE = new ResolverFeature<Boolean>(
            "http://xmlresolver.org/feature/prefer-property-file", false);

    public static final ResolverFeature<Boolean> ALLOW_CATALOG_PI = new ResolverFeature<Boolean>(
            "http://xmlresolver.org/feature/allow-catalog-pi", true);

    public static final ResolverFeature<String> CATALOG_CACHE = new ResolverFeature<String>(
            "http://xmlresolver.org/feature/cache", null);

    public static final ResolverFeature<Boolean> CACHE_UNDER_HOME = new ResolverFeature<Boolean>(
            "http://xmlresolver.org/feature/cache-under-home", true);

    public static final ResolverFeature<List<String>> CACHE_INCLUDE_REGEX = new ResolverFeature<List<String>>(
            "http://xmlresolver.org/feature/cache-include-regex", Collections.unmodifiableList(new ArrayList<String>()));

    public static final ResolverFeature<List<String>> CACHE_EXCLUDE_REGEX = new ResolverFeature<List<String>>(
            "http://xmlresolver.org/feature/cache-exclude-regex", Collections.unmodifiableList(new ArrayList<String>()));
}
