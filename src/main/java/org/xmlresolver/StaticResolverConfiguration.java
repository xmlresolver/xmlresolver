package org.xmlresolver;

import java.util.Iterator;

public class StaticResolverConfiguration implements ResolverConfiguration {
    private static XMLResolverConfiguration config = null;

    public static ResolverConfiguration getInstance() {
        if (config == null) {
            config = new XMLResolverConfiguration();
            return config;
        } else {
            return config;
        }
    }

    @Override
    public <T> void setFeature(ResolverFeature<T> feature, T value) {
        throw new UnsupportedOperationException("Cannot set features on StaticResolverConfiguration");
    }

    @Override
    public <T> T getFeature(ResolverFeature<T> feature) {
        return getInstance().getFeature(feature);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Iterator<ResolverFeature<?>> getFeatures() {
        return getInstance().getFeatures();
    }
}
