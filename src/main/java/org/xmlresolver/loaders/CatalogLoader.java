package org.xmlresolver.loaders;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.utils.SaxProducer;

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

    /**
     * Load the specified catalog by sending events to the ContentHandler.
     *
     * <p>This method exists so that a catalog can be loaded even if it doesn't have a URI
     * that can be dereferenced. It must still have a URI.</p>
     *
     * <p>The manager maintains a set of the catalogs that it has loaded. If an attempt is
     * made to load a catalog twice, the previously loaded catalog is returned.</p>
     *
     * @param catalog The catalog URI.
     * @param producer The producer that delivers events to the ContentHandler.
     * @return The parsed catalog.
     */
    public EntryCatalog loadCatalog(URI catalog, SaxProducer producer);

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

    /** Allow archived catalogs on the catalog path.
     *
     * <p>If allowed, then ZIP files may be specified as catalogs. The loader
     * will return the catalog associated with the <code>/catalog.xml</code>
     * or <code>/org/xmlresolver/catalog.xml</code> within the ZIP file.</p>
     *
     * @param allow True if archived catalogs are to be allowed.
     */
    public void setArchivedCatalogs(boolean allow);

    /** Return whether archived catalogs are allowed.
     *
     * @return True if archived catalogs are allowed.
     */
    public boolean getArchivedCatalogs();

    /** Set the entity resolver used when loading catalogs.
     *
     * <p>When the resolver loads a catalog, it can't use itself as the entity resolver because
     * that would cause an infinite loop. Instead, it uses this resolver. The only entities that this
     * resolver needs to be able to handle are the ones used in document type declarations for
     * the <em>catalogs</em> themselves.</p>
     *
     * @param resolver the resolver
     */
    public void setEntityResolver(EntityResolver resolver);

    /** Return the entity resolver used when loading catalogs.
     *
     * @return resolver the resolver
     */
    public EntityResolver getEntityResolver();
}
