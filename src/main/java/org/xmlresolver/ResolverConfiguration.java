package org.xmlresolver;

import java.util.Iterator;

public interface ResolverConfiguration {
    public <T> void setFeature(ResolverFeature<T> feature, T value);
    public <T> T getFeature(ResolverFeature<T> feature);
    public Iterator<ResolverFeature<?>> getFeatures();
}
