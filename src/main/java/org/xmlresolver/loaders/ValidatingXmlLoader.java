package org.xmlresolver.loaders;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import org.xml.sax.*;
import org.xmlresolver.Resolver;
import org.xmlresolver.ResolverConfiguration;
import org.xmlresolver.ResolverFeature;
import org.xmlresolver.Resource;
import org.xmlresolver.catalog.entry.EntryCatalog;
import org.xmlresolver.exceptions.CatalogInvalidException;
import org.xmlresolver.exceptions.CatalogUnavailableException;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;
import org.xmlresolver.utils.SaxProducer;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

/** A validating catalog loader.
 *
 * This loader will raise an exception if the catalog file cannot be read, is not
 * well-formed XML, or is not valid according to the XML Catalogs 1.1 schema.
 */
public class ValidatingXmlLoader implements CatalogLoader {
    protected final ResolverConfiguration config;
    protected final ResolverLogger logger;
    protected final HashMap<URI, EntryCatalog> catalogMap;
    private final Resolver resolver;
    private final XmlLoader underlyingLoader;

    public ValidatingXmlLoader(ResolverConfiguration config) {
        this.config = config;
        logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);

        underlyingLoader = new XmlLoader(config);
        resolver = XmlLoader.getLoaderResolver();
        catalogMap = new HashMap<>();
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
        underlyingLoader.setEntityResolver(resolver);
    }

    @Override
    public EntityResolver getEntityResolver() {
        return underlyingLoader.getEntityResolver();
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
            Resource rsrc = new Resource(catalog);
            InputSource source = new InputSource(rsrc.body());
            source.setSystemId(catalog.toString());
            return loadCatalog(catalog, source);
        } catch (FileNotFoundException fex) {
            // Throwing an exception for a simple file not found error seems a little too aggressive
            logger.log(AbstractLogger.WARNING, "Failed to load catalog: %s: %s", catalog, fex.getMessage());
            catalogMap.put(catalog, new EntryCatalog(config, catalog, null, false));
            return catalogMap.get(catalog);
        } catch (IOException | URISyntaxException ex) {
            logger.log(AbstractLogger.ERROR, "Failed to load catalog: %s: %s", catalog, ex.getMessage());
            catalogMap.put(catalog, new EntryCatalog(config, catalog, null, false));
            throw new CatalogUnavailableException(ex.getMessage());
        }
    }

    /** Load the specified catalog from the specified stream.
     *
     * @param catalog The catalog URI.
     * @param source The input source.
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

            MyErrorHandler errorHandler = new MyErrorHandler(config);
            PropertyMapBuilder builder = new PropertyMapBuilder();
            builder.put(ValidateProperty.ERROR_HANDLER, errorHandler);
            builder.put(ValidateProperty.ENTITY_RESOLVER, resolver);
            builder.put(ValidateProperty.URI_RESOLVER, resolver);

            ValidationDriver driver = new ValidationDriver(builder.toPropertyMap(), builder.toPropertyMap(), null);
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
                String msg = errorHandler.getMessage();
                throw new CatalogInvalidException("Catalog '" + catalog.toString() + "' is invalid: " + msg);
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

    /** Load the specified catalog from the specified stream.
     *
     * @param catalog The catalog URI.
     * @param producer The producer that delivers events to the ContentHandler.
     * @throws CatalogInvalidException if the catalog is invalid.
     */
    @Override
    public EntryCatalog loadCatalog(URI catalog, SaxProducer producer) {
        try {
            MyErrorHandler errorHandler = new MyErrorHandler(config);
            PropertyMapBuilder builder = new PropertyMapBuilder();
            builder.put(ValidateProperty.ERROR_HANDLER, errorHandler);
            builder.put(ValidateProperty.ENTITY_RESOLVER, resolver);
            builder.put(ValidateProperty.URI_RESOLVER, resolver);

            ValidationDriver driver = new ValidationDriver(builder.toPropertyMap(), builder.toPropertyMap(), null);
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

            if (!driver.validate(SaxProducer.adaptForJing(producer))) {
                String msg = errorHandler.getMessage();
                throw new CatalogInvalidException("Catalog '" + catalog.toString() + "' is invalid: " + msg);
            };

        } catch (IOException| SAXException ex) {
            throw new CatalogUnavailableException(ex.getMessage());
        }

        return underlyingLoader.loadCatalog(catalog, producer);
    }

    @Override
    public void setPreferPublic(boolean prefer) {
        underlyingLoader.setPreferPublic(prefer);
    }

    @Override
    public boolean getPreferPublic() {
        return underlyingLoader.getPreferPublic();
    }

    @Override
    public void setArchivedCatalogs(boolean allow) {
        underlyingLoader.setArchivedCatalogs(allow);
    }

    @Override
    public boolean getArchivedCatalogs() {
        return underlyingLoader.getArchivedCatalogs();
    }

    private static class MyErrorHandler implements ErrorHandler {
        private final ResolverConfiguration config;
        private final ResolverLogger logger;
        private String firstError = null;
        private String firstWarning = null;

        public MyErrorHandler(ResolverConfiguration config) {
            this.config = config;
            logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            if (firstWarning == null) {
                firstWarning = exception.getMessage();
            }
            logger.log(AbstractLogger.WARNING, exception.getMessage());
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            if (firstError == null) {
                firstError = exception.getMessage();
            }
            logger.log(AbstractLogger.ERROR, exception.getMessage());
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            firstError = exception.getMessage();
            logger.log(AbstractLogger.ERROR, exception.getMessage());
        }

        public String getMessage() {
            return firstError == null ? firstWarning : firstError;
        }
    }
}
