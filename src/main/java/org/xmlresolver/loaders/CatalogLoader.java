package org.xmlresolver.loaders;

import org.xml.sax.InputSource;
import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;

public interface CatalogLoader {
    /**
     * Load the specified catalog.
     *
     * <p>The manager maintains a set of the catalogs that it has loaded. If an attempt is
     * made to load a catalog twice, the previously loaded catalog is returned.</p>
     *
     * @param catalog The catalog URI.
     * @return The parsed catalog.
     */
    public EntryCatalog loadCatalog(URI catalog);

    /**
     * Load the specified catalog from a given input source.
     *
     * <p>This method exists so that a catalog can be loaded even if it doesn't have a URI
     * that can be dereferenced. It must still have a URI.</p>
     *
     * <p>The manager maintains a set of the catalogs that it has loaded. If an attempt is
     * made to load a catalog twice, the previously loaded catalog is returned.</p>
     *
     * @param catalog The catalog URI.
     * @param source The input source.
     * @return The parsed catalog.
     */
    public EntryCatalog loadCatalog(URI catalog, InputSource source);

    public void setPreferPublic(boolean prefer);
    public boolean getPreferPublic();
}
