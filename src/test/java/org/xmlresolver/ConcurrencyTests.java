package org.xmlresolver;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.SAXParserFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class ConcurrencyTests {

  private static final SAXException ExpectedEx= new SAXException();
  
  private static final class TestErrorHandler implements ErrorHandler {
    @Override
    public void warning(SAXParseException e) throws SAXException {
      throw e;
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
      throw e;
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
      if (e.getMessage().equals("Element type \"invalid\" must be declared.")) {
        throw ExpectedEx;
      }
      throw e;
    }
  }
  
  private static final String Invalid_Docbook_4_5_Article = "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\" \"http://docbook.org/xml/4.5/docbookx.dtd\"><article><section id=\"cross-ref-id\"><title>Section Title</title><para>Some section content</para><invalid/></section></article>";
  private static final String Docbook_Catalog_Zip = "src/test/resources/concurrency/docbook-xml-4.5.zip";
  
  @Test
  public void testConcurrentArchiveLoading() throws Exception {
      concurrentParseAndResolveOp(Docbook_Catalog_Zip, Invalid_Docbook_4_5_Article, Optional.of(50), Optional.empty());
  }
  
  private static void concurrentParseAndResolveOp(String catalogFiles, String content, Optional<Integer> numTestsOpt, Optional<Integer> numThreadsOpt) throws Exception {
    
    final int numThreads= numThreadsOpt.orElseGet(()->Runtime.getRuntime().availableProcessors() * 3);
    ExecutorService executorService= Executors.newFixedThreadPool(numThreads);
    int numTests = numTestsOpt.orElse(400);
    try {
      while (numTests-- > 0) {
        {
          XMLResolverConfiguration config = new XMLResolverConfiguration(catalogFiles);
  
          CountDownLatch latch= new CountDownLatch(numThreads);
          
          Callable<OpResult> createConfiguration= () -> {
            latch.countDown();
            latch.await();
  
            final SAXParserFactory saxFactory = org.apache.xerces.jaxp.SAXParserFactoryImpl.newInstance();
            saxFactory.setValidating(true);
            saxFactory.setFeature("http://xml.org/sax/features/validation", true);
            
            XMLReader reader= saxFactory.newSAXParser().getXMLReader();
            reader.setErrorHandler(new TestErrorHandler());
            reader.setFeature("http://xml.org/sax/features/validation", true);
            reader.setEntityResolver(new XMLResolver(config).getEntityResolver());
            
            InputSource is= new InputSource(new StringReader(content));
            try {
              reader.parse(is);
              return OpResult.fromFailure(null, "The content should not validate against the docbook DTD");
            } catch (Exception e) {
              if (e != ExpectedEx) {
                return OpResult.fromFailure(e, "Test in thread "+ Thread.currentThread().getId() + " unexpectedly failed with \"" + e.getMessage() + "\"");
              }
            }
            return OpResult.SUCCESS;
          };
          
          List<Future<OpResult>> ops= IntStream.rangeClosed(1, numThreads)
              .mapToObj(i -> executorService.submit(createConfiguration))
              .collect(Collectors.toList());
          
          ops.forEach(x -> {
            try {
              OpResult result = x.get();
              if(result.failed()) {
                result.getFailure().printStackTrace();
               Assertions.fail(result.getFailureMessage());
              }
            } catch (InterruptedException | ExecutionException e) {
             Assertions.fail("Test failed: "+e.getMessage());
            }
          });
          System.gc();
        }
        System.out.println("------------------------------------------- " + numTests);
      }
    } finally {
      executorService.shutdown();
    }
  }
  
  private static final class OpResult {
    private final boolean success;
    private final Throwable failure;
    private final String failureMessage;
    
    public static final OpResult SUCCESS = new OpResult(true, null, null);
    
    public static OpResult fromFailure(Throwable failure, String failureMessage) {
      return new OpResult(false, failure, failureMessage);
    }
    
    private OpResult(boolean success, Throwable failure, String failureMessage) {
      this.success= success;
      this.failure= failure;
      this.failureMessage= failureMessage;
    }
    
    public boolean failed() {
      return !success;
    }

    public Throwable getFailure() {
      return failure;
    }

    public String getFailureMessage() {
      return failureMessage;
    }
  }
}
