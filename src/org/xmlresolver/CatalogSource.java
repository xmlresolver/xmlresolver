package org.xmlresolver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Represents XML sources of catalogs that are parsed by a document builder.
 * 
 * @author swachter
 */
public abstract class CatalogSource<S> {
    public static Logger logger = LoggerFactory.getLogger(Catalog.class);

    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

  static {
    factory.setNamespaceAware(true);
  }
  
  protected final S mySource;

  protected CatalogSource(S aSource) {
    mySource = aSource;
  }

  public Document parse() {
    DocumentBuilder builder = null;
    Document doc = null;
    try {
      builder = factory.newDocumentBuilder();
      doc = doParse(builder);
      return doc;
    } catch (ParserConfigurationException pce) {
      Catalog.logger.warn("Parser configuration exception attempting to load " + this);
      return null;
    } catch (FileNotFoundException fnfe) {
      // ignore this one
      Catalog.logger.debug("Catalog file not found: " + this);
    } catch (IOException ex) {
      Catalog.logger.warn("I/O exception reading " + this + ": " + ex.toString());
    } catch (SAXException ex) {
      Catalog.logger.warn("SAX exception reading " + this + ": " + ex.toString());
    }
    return doc;
  }

  protected abstract Document doParse(DocumentBuilder aDocumentBuilder) throws SAXException, IOException;

  public String toString() {
    return mySource.toString();
  }

    public static class UriCatalogSource extends CatalogSource<String> {
      public UriCatalogSource(String aSource) {
        super(aSource);
      }
      @Override
      protected Document doParse(DocumentBuilder aDocumentBuilder) throws SAXException, IOException {
          logger.trace("Parse URI: " + mySource);
          return aDocumentBuilder.parse(mySource);
      }
    }
    
    public static class InputSourceCatalogSource extends CatalogSource<InputSource> {
      public InputSourceCatalogSource(InputSource aSource) {
        super(aSource);
      }
      @Override
      protected Document doParse(DocumentBuilder aDocumentBuilder) throws SAXException, IOException {
        return aDocumentBuilder.parse(mySource);
      }
    }
    
    public static class InputStreamCatalogSource extends CatalogSource<InputStream> {
      public InputStreamCatalogSource(InputStream aSource) {
        super(aSource);
      }
      @Override
      protected Document doParse(DocumentBuilder aDocumentBuilder) throws SAXException, IOException {
        return aDocumentBuilder.parse(mySource);
      }
    }
    
}
