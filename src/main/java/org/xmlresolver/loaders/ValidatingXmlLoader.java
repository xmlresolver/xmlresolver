package org.xmlresolver.loaders;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xmlresolver.CatalogManager;
import org.xmlresolver.Resolver;
import org.xmlresolver.ResolverFeature;
import org.xmlresolver.ResolverLogger;
import org.xmlresolver.Resource;
import org.xmlresolver.XMLResolverConfiguration;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.exceptions.CatalogInvalidException;
import org.xmlresolver.exceptions.CatalogUnavailableException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;

/** A validating catalog loader.
 *
 * This loader will raise an exception if the catalog file cannot be read, is not
 * well-formed XML, or is not valid according to the XML Catalogs 1.1 schema.
 */
public class ValidatingXmlLoader implements CatalogLoader {
    protected static ResolverLogger logger = new ResolverLogger(CatalogManager.class);
    protected final HashMap<URI, EntryCatalog> catalogMap;
    private boolean preferPublic = true;
    private final Resolver resolver;
    private final XmlLoader underlyingLoader;

    public ValidatingXmlLoader() {
        XMLResolverConfiguration config = new XMLResolverConfiguration(Collections.emptyList(), Collections.emptyList());
        config.setFeature(ResolverFeature.PREFER_PUBLIC, true);
        config.setFeature(ResolverFeature.CATALOG_FILES, Collections.singletonList("classpath:/org/xmlresolver/validator/catalog.xml"));
        config.setFeature(ResolverFeature.CACHE_DIRECTORY, null);
        config.setFeature(ResolverFeature.CACHE_UNDER_HOME, false);
        config.setFeature(ResolverFeature.ALLOW_CATALOG_PI, false);
        resolver = new Resolver(config);
        underlyingLoader = new XmlLoader();
        catalogMap = new HashMap<>();
    }

    /** Load the specified catalog.
     *
     * @param catalog The catalog URI.
     * @return The parsed catalog, if it was available and valid.
     * @throws CatalogUnavailableException if the catalog could not be read.
     * @throws CatalogInvalidException if the catalog is invalid.
     */
    @Override
    public EntryCatalog loadCatalog(URI catalog) {
        if (catalogMap.containsKey(catalog)) {
            return catalogMap.get(catalog);
        }

        try {
            Resource rsrc = new Resource(catalog.toString());
            InputSource source = new InputSource(rsrc.body());
            source.setSystemId(catalog.toString());
            return loadCatalog(catalog, source);
        } catch (FileNotFoundException fex) {
            // Throwing an exception for a simple file not found error seems a little too aggressive
            logger.log(ResolverLogger.WARNING, "Failed to load catalog: %s: %s", catalog, fex.getMessage());
            catalogMap.put(catalog, new EntryCatalog(catalog, null, false));
            return catalogMap.get(catalog);
        } catch (IOException | URISyntaxException ex) {
            logger.log(ResolverLogger.ERROR, "Failed to load catalog: %s: %s", catalog, ex.getMessage());
            catalogMap.put(catalog, new EntryCatalog(catalog, null, false));
            throw new CatalogUnavailableException(ex.getMessage());
        }
    }

    /** Load the specified catalog from the specified stream.
     *
     * @param catalog The catalog URI.
     * @return The parsed catalog, if it was available and valid.
     * @throws CatalogInvalidException if the catalog is invalid.
     */
    @Override
    public EntryCatalog loadCatalog(URI catalog, InputSource source) {
        try {
            // This is a bit of a hack, but I don't expect catalogs to be huge and I have
            // to make sure that the stream can be read twice.
            ByteArrayOutputStream baos = null;
            if (source.getByteStream() != null) {
                baos = new ByteArrayOutputStream();
                InputStream instream = source.getByteStream();
                byte[] buf = new byte[4096];
                int readLen = instream.read(buf, 0, buf.length);
                while (readLen >= 0) {
                    baos.write(buf, 0, readLen);
                    readLen = instream.read(buf, 0, buf.length);
                }
                source.setByteStream(new ByteArrayInputStream(baos.toByteArray()));
            }

            CharArrayWriter caw = null;
            if (source.getCharacterStream() != null) {
                caw = new CharArrayWriter();
                Reader instream = source.getCharacterStream();
                char[] buf = new char[4096];
                int readLen = instream.read(buf, 0, buf.length);
                while (readLen >= 0) {
                    caw.write(buf, 0, readLen);
                    readLen = instream.read(buf, 0, buf.length);
                }
                source.setCharacterStream(new CharArrayReader(caw.toCharArray()));
            }

            MyErrorHandler errorHandler = new MyErrorHandler();
            PropertyMapBuilder builder = new PropertyMapBuilder();
            builder.put(ValidateProperty.ERROR_HANDLER, errorHandler);
            builder.put(ValidateProperty.ENTITY_RESOLVER, resolver);
            builder.put(ValidateProperty.URI_RESOLVER, resolver);

            ValidationDriver driver = new ValidationDriver();
            URL schemaUrl = ValidatingXmlLoader.class.getResource("/org/xmlresolver/schemas/oasis-xml-catalog-1.1.rng");
            if (schemaUrl == null) {
                throw new CatalogInvalidException("Failed to read catalog schema resource");
            }
            InputStream schemaStream = schemaUrl.openStream();
            InputSource schema = new InputSource(schemaStream);
            if (!driver.loadSchema(schema)) {
                if (errorHandler.getMessage() == null) {
                    throw new CatalogInvalidException("Failed to load catalog schema");
                } else {
                    throw new CatalogInvalidException("Failed to load catalog schema: " + errorHandler.getMessage());
                }
            }
            if (!driver.validate(source)) {
                throw new CatalogInvalidException("Catalog '" + catalog.toString() + "' is invalid: " + errorHandler.getMessage());
            };

            if (baos != null) {
                source.setByteStream(new ByteArrayInputStream(baos.toByteArray()));
            }

            if (caw != null) {
                source.setCharacterStream(new CharArrayReader(caw.toCharArray()));
            }
        } catch (IOException| SAXException ex) {
            throw new CatalogUnavailableException(ex.getMessage());
        }

        return underlyingLoader.loadCatalog(catalog, source);
    }

    /** Set the default "prefer public" status for this catalog.
     *
     * @param prefer True if public identifiers are to be preferred.
     */
    @Override
    public void setPreferPublic(boolean prefer) {
        preferPublic = prefer;
    }

    /** Return the current "prefer public" status.
     *
     * @return The current "prefer public" status of this catalog loader.
     */
    @Override
    public boolean getPreferPublic() {
        return preferPublic;
    }

    private static class MyErrorHandler implements ErrorHandler {
        private String firstError = null;
        private String firstWarning = null;

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            if (firstWarning == null) {
                firstWarning = exception.getMessage();
            }
            logger.log(ResolverLogger.WARNING, exception.getMessage());
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            if (firstError == null) {
                firstError = exception.getMessage();
            }
            logger.log(ResolverLogger.ERROR, exception.getMessage());
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            firstError = exception.getMessage();
            logger.log(ResolverLogger.ERROR, exception.getMessage());
        }

        public String getMessage() {
            return firstError == null ? firstWarning : firstError;
        }
    }
}
