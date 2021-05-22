package org.xmlresolver;

import java.util.Iterator;

/**
 * Interface for configuring the resolver. The actual configurations are defined
 * as constants in {@link XMLResolverConfiguration}.
 */

public interface ResolverConfiguration {
    /**
     * Sets a feature value.
     *
     * @param feature The feature.
     * @param value The new value.
     * @param <T> The type of the new value which varies according to the feature.
     */
    public <T> void setFeature(ResolverFeature<T> feature, T value);

    /**
     * Gets a feature value.
     *
     * @param feature The feature.
     * @param <T> The type of the new value which varies according to the feature.
     * @return The current value of that feature.
     */
    public <T> T getFeature(ResolverFeature<T> feature);

    /**
     * Iterates over all the features known to the particular configuration.
     *
     * @return An iterator over all known features.
     */
    public Iterator<ResolverFeature<?>> getFeatures();
}
