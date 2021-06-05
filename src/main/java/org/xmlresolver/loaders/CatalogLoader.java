package org.xmlresolver.loaders;

import org.xml.sax.InputSource;
import org.xmlresolver.catalog.entry.EntryCatalog;

import java.net.URI;

/** The catalog loader interface.
 *
 * <p>The loader interface provides two entry points to load catalogs, one with a URI and one with
 * both a URI and an input source (in case the catalog comes from some source that cannot be
 * directly accessed by a URI, or the URI is unknown).</p>
 *
 * <p>The catalog specification mandates that it must be possible to specify the default
 * "prefer public" behavior of any catalog, so that is supported as well.</p>
 */

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

    /** Set the default "prefer public" status for this catalog.
     *
     * @param prefer True if public identifiers are to be preferred.
     */
    public void setPreferPublic(boolean prefer);

    /** Return the current "prefer public" status.
     *
     * @return The current "prefer public" status of this catalog loader.
     */
    public boolean getPreferPublic();
}
