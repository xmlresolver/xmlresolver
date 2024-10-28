package org.xmlresolver.spi;

import java.util.List;

/**
 * The schema resolver manager.
 * <p>The resolver manager identifies the schemes that a particular resolver understands
 * and returns a resolver for each known scheme.</p>
 */
public interface SchemeResolverManager {
    /**
     * Get the known schemes.
     * <p>The resolver must identify the scheme (or schemes) it resolves.</p>
     * @return The list of schemes.
     */
    List<String> getKnownSchemes();

    /**
     * Get a resolver for the specified scheme.
     * <p>The resolver must return a {@link org.xmlresolver.spi.SchemeResolver} for the requested scheme.</p>
     * @param scheme The scheme (will always be one of the schemes returned by {@link #getKnownSchemes()})
     * @return The schema resolver.
     */
    SchemeResolver getSchemeResolver(String scheme);
}
