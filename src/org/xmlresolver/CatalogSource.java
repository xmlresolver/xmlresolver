package org.xmlresolver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Represents XML sources of catalogs that are parsed by a document builder.
 * 
 * @author swachter
 */
public abstract class CatalogSource<S> {

  private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

  static {
    factory.setNamespaceAware(true);
  }
  protected final S mySource;

  protected CatalogSource(S aSource) {
    mySource = aSource;
  }
  
  public abstract Element parse();

  //
  //
  //
  
  public abstract static class ParsingCatalogSource<S> extends CatalogSource<S> {

    public ParsingCatalogSource(S aSource) {
      super(aSource);
    }

    public Element parse() {
      try {
        DocumentBuilder builder = null;
        Document doc = null;
        builder = factory.newDocumentBuilder();
        doc = doParse(builder);
        return doc.getDocumentElement();
      } catch (ParserConfigurationException pce) {
        Catalog.logger.warning("Parser configuration exception attempting to load " + this);
        return null;
      } catch (FileNotFoundException fnfe) {
        // ignore this one
        Catalog.logger.finer("Catalog file not found: " + this);
      } catch (IOException ex) {
        Catalog.logger.warning("I/O exception reading " + this + ": " + ex.toString());
      } catch (SAXException ex) {
        Catalog.logger.warning("SAX exception reading " + this + ": " + ex.toString());
      }
      return null;
    }

    public String toString() {
      return mySource.toString();
    }

    protected abstract Document doParse(DocumentBuilder aDocumentBuilder) throws SAXException, IOException;
  }

  public static class UriCatalogSource extends ParsingCatalogSource<String> {

    public UriCatalogSource(String aSource) {
      super(aSource);
    }

    @Override
    protected Document doParse(DocumentBuilder aDocumentBuilder) throws SAXException, IOException {
      return aDocumentBuilder.parse(mySource);
    }
  }

  public static class InputSourceCatalogSource extends ParsingCatalogSource<InputSource> {

    public InputSourceCatalogSource(InputSource aSource) {
      super(aSource);
    }

    @Override
    protected Document doParse(DocumentBuilder aDocumentBuilder) throws SAXException, IOException {
      return aDocumentBuilder.parse(mySource);
    }
  }

  public static class InputStreamCatalogSource extends ParsingCatalogSource<InputStream> {

    public InputStreamCatalogSource(InputStream aSource) {
      super(aSource);
    }

    @Override
    protected Document doParse(DocumentBuilder aDocumentBuilder) throws SAXException, IOException {
      return aDocumentBuilder.parse(mySource);
    }
  }
  
  public static class DomCatalogSource extends CatalogSource<Element> {
    public DomCatalogSource(Element aSource) {
      super(aSource);
    }
    @Override
    public Element parse() {
      return mySource;
    }
    public String toString() { return "DOM(baseUri:" + mySource.getBaseURI() + ")"; }
  }
}
