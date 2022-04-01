package org.xmlresolver.utils;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Similar to {@link java.util.function.Consumer} but
 * permits an IOException or a SAXException to be thrown.
 *
 * Influenced by <a href="https://github.com/evolvedbinary/j8fu/blob/master/src/main/java/com/evolvedbinary/j8fu/function/TriConsumer2E.java">TriConsumer2E&lt;ContentHandler, IOException, SAXException&gt;</a>
 */
@FunctionalInterface
public interface SaxProducer {

    /**
     * Causes the producer to process and send SAX events.
     *
     * @param contentHandler the Content Handler
     * @param dtdHandler the DTD handler, or null if absent.
     * @param errorHandler the error handler, or null if absent.
     *
     * @throws IOException if an error occurs during processing
     * @throws SAXException if a SAXException occurs when calling one of the handlers
     */
    void produce(ContentHandler contentHandler, DTDHandler dtdHandler, ErrorHandler errorHandler) throws IOException, SAXException;
}
