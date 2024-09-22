package org.xmlresolver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

/**
 * Interface for configuring the resolver.
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

    /**
     * Get a resource.
     * <p>This method takes a {@link ResourceRequest} and attempts to retrieve it.</p>
     * <p>The method should return a response indicating failure if it cannot retrieve the resource.
     * It should not return {@code null}.</p>
     * @param request The request.
     * @return A response.
     * @throws URISyntaxException If the request URI is syntactically invalid.
     * @throws IOException If the resource cannot be retrieved.
     */
    public ResourceResponse getResource(ResourceRequest request) throws URISyntaxException, IOException;

    /**
     * Get a resource.
     * <p>This method takes a {@link ResourceResponse} and attempts to retrieve it. This occurs, for example,
     * when a catalog lookup succeeds and the resolver wants to access it.</p>
     * <p>The method should return a response indicating failure if it cannot retrieve the resource.
     * It should not return {@code null}.</p>
     * @param request The request.
     * @return A response.
     * @throws URISyntaxException If the request URI is syntactically invalid.
     * @throws IOException If the resource cannot be retrieved.
     */
    public ResourceResponse getResource(ResourceResponse request) throws URISyntaxException, IOException;
}
