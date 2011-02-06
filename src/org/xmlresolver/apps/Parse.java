// xjparse.java - A simple command-line XML parser

/* Copyright 2005 Norman Walsh.
 * Derived from org.apache.xml.resolver.apps.xread.
 * Portions copyright 2001-2004 The Apache Software Foundation or its
 * licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xmlresolver.apps;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.xml.sax.ErrorHandler;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXParseException;
import org.xmlresolver.Catalog;
import org.xmlresolver.Resolver;
import org.xmlresolver.helpers.FileURI;
import org.xmlresolver.tools.ResolvingXMLReader;

/**
 * A simple command-line XML parsing application.
 *
 * <p>This class implements a simple command-line XML Parser. It's
 * just a little wrapper around the JAXP XMLReader with support for
 * catalogs.
 * </p>
 *
 * <p>Usage: org.xmlresolver.apps.Parse [opts] xmlfile</p>
 *
 * <p>Where:</p>
 *
 * <dl>
 * <dt><code>-c</code> <em>catalogfile</em></dt>
 * <dd>Load a particular catalog file</dd>
 * <dt><code>-w</code></dt>
 * <dd>Perform a well-formed parse, not a validating parse</dd>
 * <dt><code>-v</code></dt>
 * <dd>Perform a validating parse (the default)</dd>
 * <dt><code>-s</code></dt>
 * <dd>Enable W3C XML Schema validation</dd>
 * <dt><code>-S</code> <em>schema.xsd</em></dt>
 * <dd>Use schema.xsd for validation (implies -s)</dd>
 * <dt><code>-f</code></dt>
 * <dd>Enable full schema checking (implies -s)</dd>
 * <dt><code>-n</code></dt>
 * <dd>Perform a namespace-ignorant parse</dd>
 * <dt><code>-N</code></dt>
 * <dd>Perform a namespace-aware parse (the default)</dd>
 * <dt><code>-d</code> <em>integer</em></dt>
 * <dd>Set the debug level (warnings are level 2)</dd>
 * <dt><code>-E</code> <em>integer</em></dt>
 * <dd>Set the maximum number of errors to display</dd>
 * </dl>
 *
 * <p>The process ends with error-level 1, if there are errors.</p>
 *
 * @see org.xmlresolver.tools.ResolvingXMLReader
 *
 * @author Norman Walsh
 * <a href="mailto:ndw@nwalsh.com">ndw@nwalsh.com</a>
 *
 * @version 1.0
 */
public class Parse {
    protected static final String SCHEMA_VALIDATION_FEATURE_ID
	= "http://apache.org/xml/features/validation/schema";

    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID
	= "http://apache.org/xml/features/validation/schema-full-checking";

    protected static final String EXTERNAL_SCHEMA_LOCATION_PROPERTY_ID
	= "http://apache.org/xml/properties/schema/external-schemaLocation";

    protected static final String EXTERNAL_NONS_SCHEMA_LOCATION_PROPERTY_ID
	= "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";

    public Parse() {
        // construct me!
    }
    
    /** The main entry point */
    public static void main (String[] args) throws FileNotFoundException, IOException {
        Parse parse = new Parse();
        
        //String[] testargs = { "-d", "finer", "-w", "/projects/sun/java.net/xmlresolver/java/documents/dtdtest.xml" };
        //parse.run(testargs);

        parse.run(args);
    }
    
    /** The main entry point */
    public void run(String[] args) {

	String  xmlfile    = null;
	String  debugLevel = "info";
	int     maxErrs    = 10;
	boolean nsAware    = true;
	boolean validating = true;
	boolean useSchema  = false;
	boolean showWarnings = true;
	boolean showErrors = true;
	boolean fullChecking = false; // true implies useSchema
	Vector<String> xsdFiles = new Vector<String>();
	Vector<String> catalogFiles = new Vector<String>();

	for (int i=0; i<args.length; i++) {
	    if (args[i].equals("-c")) {
		++i;
		catalogFiles.add(args[i]);
		continue;
	    }

	    if (args[i].equals("-w")) {
		validating = false;
		continue;
	    }

	    if (args[i].equals("-v")) {
		validating = true;
		continue;
	    }

	    if (args[i].equals("-s")) {
		useSchema = true;
		continue;
	    }

	    if (args[i].equals("-S")) {
		++i;
		xsdFiles.add(args[i]);
		useSchema = true;
		continue;
	    }

	    if (args[i].equals("-f")) {
		fullChecking = true;
		useSchema = true;
		continue;
	    }

	    if (args[i].equals("-n")) {
		nsAware = false;
		continue;
	    }

	    if (args[i].equals("-N")) {
		nsAware = true;
		continue;
	    }

	    if (args[i].equals("-d")) {
		++i;
                debugLevel = args[i];
		continue;
	    }

	    if (args[i].equals("-E")) {
		++i;
		String errstr = args[i];
		try {
		    int errs = Integer.parseInt(errstr);
		    if (errs >= 0) {
			maxErrs = errs;
		    }
		} catch (Exception e) {
		    // nop
		}
		continue;
	    }

	    xmlfile = args[i];
	}

	if (xmlfile == null && !fullChecking) {
	    // Hack
	    System.out.println("Usage: com.nwalsh.parsers.xjparse [opts] xmlfile");
	    System.out.println("");
	    System.out.println("Where:");
	    System.out.println("");
	    System.out.println("-c catalogfile   Load a particular catalog file");
	    System.out.println("-w               Perform a well-formed parse, not a validating parse");
	    System.out.println("-v               Perform a validating parse (the default)");
	    System.out.println("-s               Enable W3C XML Schema validation");
	    System.out.println("-S schema.xsd    Use schema.xsd for validation (implies -s)");
	    System.out.println("-f               Enable full schema checking (implies -s)");
	    System.out.println("-n               Perform a namespace-ignorant parse");
	    System.out.println("-N               Perform a namespace-aware parse (the default)");
	    System.out.println("-d integer       Set the debug level (warnings are level 2)");
	    System.out.println("-E integer       Set the maximum number of errors to display");
	    System.out.println("");
	    System.out.println("The process ends with error-level 1, if there are errors.");
	    System.exit(1);
	}

	Hashtable schemaList = lookupSchemas(xsdFiles);

        String catalogList = "";
	for (int count = 0; count < catalogFiles.size(); count++) {
	    String file = catalogFiles.elementAt(count);
            if (count > 0) { catalogList += ";"; }
            catalogList += file;
	}

        Catalog catalog = null;
        if ("".equals(catalogList)) {
            catalog = new Catalog();
        } else {
            catalog = new Catalog(catalogList);
        }
        catalog.setVerbosity(debugLevel);
        showWarnings = (catalog.getVerbosity() >= 2);

        Resolver resolver = new Resolver(catalog);
	ResolvingXMLReader reader = new ResolvingXMLReader(resolver);
        
	try {
	    nsAware = true;
	    reader.setFeature("http://xml.org/sax/features/namespaces",
			      nsAware);
	    reader.setFeature("http://xml.org/sax/features/validation",
			      validating);
	    if (useSchema) {
		reader.setFeature(SCHEMA_VALIDATION_FEATURE_ID, useSchema);
		reader.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, fullChecking);
		if (schemaList != null) {
		    String slh = "";
		    String nons_slh = "";
		    Enumeration nskey = schemaList.keys();
		    while (nskey.hasMoreElements()) {
			String ns = (String) nskey.nextElement();
			String xsd = (String) schemaList.get(ns);
			if ("".equals(ns)) {
			    nons_slh = xsd;
			    if (catalog.getVerbosity() >= 3) {
				System.err.println("Hint: ''=" + xsd);
			    }
			} else {
			    if (!"".equals(slh)) {
				slh = slh + " ";
			    }
			    slh = slh + ns + " " + xsd;
			    if (catalog.getVerbosity() >= 3) {
				System.err.println("Hint: " + ns + "=" + xsd);
			    }
			}
		    }

		    if (!"".equals(slh)) {
			reader.setProperty(EXTERNAL_SCHEMA_LOCATION_PROPERTY_ID,
					   slh);
		    }

		    if (!"".equals(nons_slh)) {
			reader.setProperty(EXTERNAL_NONS_SCHEMA_LOCATION_PROPERTY_ID,
					   nons_slh);
		    }
		}
	    }
	} catch (SAXException e) {
	    // nop;
	}

        
	XParseError xpe = new XParseError(showErrors, showWarnings);
	xpe.setMaxMessages(maxErrs);
	reader.setErrorHandler(xpe);

        Date startTime = null;

	try {
            if (xmlfile != null) {
                String parseType = validating ? "validating" : "well-formed";
                String nsType = nsAware ? "namespace-aware" : "namespace-ignorant";
                if (maxErrs > 0) {
                    System.out.println("Attempting "
                            + parseType
                            + ", "
                            + nsType
                            + " parse");
                }
                startTime = new Date();
                reader.parse(xmlfile);
            } else {
                System.exit(0);
            }
                
	} catch (SAXException sx) {
	    System.out.println("SAX Exception: " + sx);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	Date endTime = new Date();

	long millisec = endTime.getTime() - startTime.getTime();
	long secs = 0;
	long mins = 0;
	long hours = 0;

	if (millisec > 1000) {
	    secs = millisec / 1000;
	    millisec = millisec % 1000;
	}

	if (secs > 60) {
	    mins = secs / 60;
	    secs = secs % 60;
	}

	if (mins > 60) {
	    hours = mins / 60;
	    mins = mins % 60;
	}

	if (maxErrs > 0) {
	    System.out.print("Parse ");
	    if (xpe.getFatalCount() > 0) {
		System.out.print("failed ");
	    } else {
		System.out.print("succeeded ");
		System.out.print("(");
		if (hours > 0) {
		    System.out.print(hours + ":");
		}
		if (hours > 0 || mins > 0) {
		    System.out.print(mins + ":");
		}
		System.out.print(secs + "." + millisec);
		System.out.print(") ");
	    }
	    System.out.print("with ");

	    int errCount = xpe.getErrorCount();
	    int warnCount = xpe.getWarningCount();

	    if (errCount > 0) {
		System.out.print(errCount + " error");
		System.out.print(errCount > 1 ? "s" : "");
		System.out.print(" and ");
	    } else {
		System.out.print("no errors and ");
	    }

	    if (warnCount > 0) {
		System.out.print(warnCount + " warning");
		System.out.print(warnCount > 1 ? "s" : "");
		System.out.print(".");
	    } else {
		System.out.print("no warnings.");
	    }

	    System.out.println("");
	}

	if (xpe.getErrorCount() > 0) {
	    System.exit(1);
	}
    }

    private static Hashtable lookupSchemas(Vector xsdFiles) {
        Hashtable<String,String> mapping = new Hashtable<String,String>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringComments(true);
            dbf.setNamespaceAware(true);
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();

            Enumeration xsdenum = xsdFiles.elements();
            while (xsdenum.hasMoreElements()) {
            String xsd = (String) xsdenum.nextElement();

            // Hack. Spaces will cause Xerces to fall over.
            xsd = xsd.replaceAll(" ","%20");

            Document doc = db.parse(xsd);
            Element s = doc.getDocumentElement();
            String targetNS = s.getAttribute("targetNamespace");
            if (targetNS == null || "".equals(targetNS)) {
                mapping.put("", xsd);
            } else {
                mapping.put(targetNS, xsd);
            }
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return mapping;
    }

    /**
     * An ErrorHandler for xparse.
     *
     * <p>This class is just the error handler for xparse.</p>
     *
     * @see Parse
     *
     * @author Norman Walsh
     * <a href="mailto:Norman.Walsh@Sun.COM">Norman.Walsh@Sun.COM</a>
     *
     * @version 1.0
     */
    class XParseError implements ErrorHandler {
        /** Show errors? */
        private boolean showErrors = true;

        /** Show warnings? */
        private boolean showWarnings = false;

        /** How many messages should be presented? */
        private int maxMessages = 10;

        /** The number of fatal errors seen so far. */
        private int fatalCount = 0;

        /** The number of errors seen so far. */
        private int errorCount = 0;

        /** The number of warnings seen so far. */
        private int warningCount = 0;

        /** The base URI of the running application. */
        private String baseURI = "";

        /** Constructor */
        public XParseError(boolean errors, boolean warnings) {
            showErrors = errors;
            showWarnings = warnings;

            try {
                URI uri = FileURI.makeURI("basename");
                baseURI = uri.toURL().toString();
            } catch (MalformedURLException mue) {
                // nop;
            }
        }

        /** Return the error count */
        public int getErrorCount() {
            return errorCount;
        }

        /** Return the fatal error count */
        public int getFatalCount() {
            return fatalCount;
        }

        /** Return the warning count */
        public int getWarningCount() {
            return warningCount;
        }

        /** Return the number of messages to display */
        public int getMaxMessages() {
            return maxMessages;
        }

        /** Set the number of messages to display */
        public void setMaxMessages(int max) {
            maxMessages = max;
        }

        /** SAX2 API */
        public void error(SAXParseException exception) {
            errorCount++;
            if (showErrors && (errorCount+warningCount < maxMessages)) {
                message("Error", exception);
            }
        }

        /** SAX2 API */
        public void fatalError(SAXParseException exception) {
            errorCount++;
            fatalCount++;
            if (showErrors && (errorCount+warningCount < maxMessages)) {
                message("Fatal error", exception);
            }
        }

        /** SAX2 API */
        public void warning(SAXParseException exception) {
            warningCount++;
            if (showWarnings && (errorCount+warningCount < maxMessages)) {
                message("Warning", exception);
            }
        }

        /** Display a message to the user */
        private void message(String type, SAXParseException exception) {
            String filename = exception.getSystemId();
            if (filename.startsWith(baseURI)) {
                filename = filename.substring(baseURI.length());
            }

            System.out.print(type
                    + ":"
                    + filename
                    + ":"
                    + exception.getLineNumber());

            if (exception.getColumnNumber() > 0) {
                System.out.print(":" + exception.getColumnNumber());
            }

            System.out.println(":" + exception.getMessage());
        }
    }
}
