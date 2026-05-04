package org.xmlresolver.spi;

/**
 * The schema resolver provider.
 */
public interface SchemeResolverProvider {
    /**
     * Create a new schema resolver manager.
     * @return The schema resolver manager.
     */
    SchemeResolverManager create();
}
