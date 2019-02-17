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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xmlresolver.Catalog;
import org.xmlresolver.Resolver;
import org.xmlresolver.helpers.FileURI;
import org.xmlresolver.tools.ResolvingXMLReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

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
 * This class provides nothing more than a simple example of how the
 * resolver is used. For a more comprehensive command line parsing
 * app, see http://github.com/ndw/xjparse./
 *
 * <p>The process ends with error-level 1, if there are errors.</p>
 *
 * @author Norman Walsh
 * <a href="mailto:ndw@nwalsh.com">ndw@nwalsh.com</a>
 *
 * @version 2.0
 */
public class Parse {
    private static Logger logger = LoggerFactory.getLogger(Parse.class);

    private static final String SCHEMA_VALIDATION_FEATURE_ID
            = "http://apache.org/xml/features/validation/schema";

    private static final String SCHEMA_FULL_CHECKING_FEATURE_ID
            = "http://apache.org/xml/features/validation/schema-full-checking";

    private static final String EXTERNAL_SCHEMA_LOCATION_PROPERTY_ID
            = "http://apache.org/xml/properties/schema/external-schemaLocation";

    private static final String EXTERNAL_NONS_SCHEMA_LOCATION_PROPERTY_ID
            = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";

    public Parse() {
        // construct me!
    }

    /** The main entry point
     *
     * @param args Command line arguments
     * @throws FileNotFoundException if the file isn't found
     * @throws IOException if an I/O error occurs
     */
    public static void main (String[] args) throws FileNotFoundException, IOException {
        Parse parse = new Parse();
        if (! parse.run(args)) {
            System.exit(1);
        }
    }

    /* The main entry point
     */
    public boolean run(String[] args) {
        String  xmlfile    = null;
        int     maxErrs    = 10;
        boolean validating = true;
        boolean useSchema  = false;
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

            xmlfile = args[i];
        }

        if (xmlfile == null) {
            // Hack
            System.out.println("Usage: org.xmlresolver.apps.Parse [opts] xmlfile");
            System.out.println("");
            System.out.println("Where:");
            System.out.println("");
            System.out.println("-c catalogfile   Load a particular catalog file");
            System.out.println("-w               Perform a well-formed parse, not a validating parse");
            System.out.println("-s               Enable W3C XML Schema validation");
            System.out.println("-S schema.xsd    Use schema.xsd for validation (implies -s)");
            System.out.println("");
            System.out.println("The process ends with error-level 1, if there are errors.");
            return false;
        }

        Hashtable schemaList = lookupSchemas(xsdFiles);

        StringBuilder catalogList = new StringBuilder();
        for (int count = 0; count < catalogFiles.size(); count++) {
            String file = catalogFiles.elementAt(count);
            if (count > 0) { catalogList.append(";"); }
            catalogList.append(file);
        }

        Catalog catalog = null;
        if (catalogFiles.isEmpty()) {
            catalog = new Catalog();
        } else {
            catalog = new Catalog(catalogList.toString());
        }

        Resolver resolver = new Resolver(catalog);
	    ResolvingXMLReader reader = new ResolvingXMLReader(resolver);

        try {
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setFeature("http://xml.org/sax/features/validation", validating);

            if (useSchema) {
                reader.setFeature(SCHEMA_VALIDATION_FEATURE_ID, useSchema);
                reader.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, false);

                if (schemaList != null) {
                    String slh = "";
                    String nons_slh = "";
                    Enumeration nskey = schemaList.keys();
                    while (nskey.hasMoreElements()) {
                        String ns = (String) nskey.nextElement();
                        String xsd = (String) schemaList.get(ns);
                        if ("".equals(ns)) {
                            nons_slh = xsd;
                            logger.trace("Hint: ''=" + xsd);
                        } else {
                            if (!"".equals(slh)) {
                                slh = slh + " ";
                            }
                            slh = slh + ns + " " + xsd;
                            logger.trace("Hint: " + ns + "=" + xsd);
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

        XParseError xpe = new XParseError();
        xpe.setMaxMessages(maxErrs);
        reader.setErrorHandler(xpe);
        boolean pass = true;

        try {
            String parseType = validating ? "validating" : "well-formed";
            String nsType = "namespace-aware";
            System.out.println("Attempting "
                    + parseType
                    + ", "
                    + nsType
                    + " parse");
            reader.parse(xmlfile);
        } catch (Exception e) {
            System.out.println(e.toString());
            pass = false;
        }

        System.out.print("Parse ");
        if (!pass || xpe.getFatalCount() > 0) {
            System.out.print("failed ");
            pass = false;
        } else {
            System.out.print("succeeded ");
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

        return pass;
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
        } catch (ParserConfigurationException | SAXException | IOException pce) {
            pce.printStackTrace();
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
        private boolean showErrors = true;
        private boolean showWarnings = true;

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
        public XParseError() {
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
