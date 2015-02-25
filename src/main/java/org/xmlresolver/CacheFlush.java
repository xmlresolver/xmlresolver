/*
 * CacheFlush.java
 *
 * Created on January 26, 2007, 9:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlresolver.helpers.DOMUtils;

/** An application to flush expired entries from a ResourceCache.
 *
 * @author ndw
 */
public class CacheFlush {
    private static String usage = "Usage: CacheFlush cache-directory";
    private static GregorianCalendar now = new GregorianCalendar();
    
    /**
     * Creates a new instance of CacheFlush
     */
    public CacheFlush() {
    }
 
    public static void main(String[] args) {
        CacheFlush cache = new CacheFlush();

        /*
        if (args.length != 1) {
            System.err.println(usage);
            System.exit(1);
        }
        cache.run(args[0]);
         */
        
        cache.run("/projects/src/xmlresolver/catalogs/cache");
    }

    public void run(String dir) {
        ResourceCache cache = new ResourceCache(dir);
        Document catalog = cache.catalog();
        Vector<String> expired = new Vector<String> ();
        Vector<String> uptodate = new Vector<String> ();

        if (catalog == null) {
            System.err.println(usage);
            System.exit(2);
        }

        int count = 0;
        Element entry = DOMUtils.getFirstElement(catalog.getDocumentElement());
        while (entry != null) {
            String uri = null;
            if ("uri".equals(entry.getLocalName())) {
                count++;
            } else if ("system".equals(entry.getLocalName())) {
                count++;
            }
            entry = DOMUtils.getNextElement(entry);
        }
        
        System.out.println("Checking " + count + " cached URIs.");
        
        int okCount = 0;
        int expCount = 0;
        
        entry = DOMUtils.getFirstElement(catalog.getDocumentElement());
        while (entry != null) {
            String uri = null;
            if ("uri".equals(entry.getLocalName())) {
                uri = entry.getAttribute("name");
            } else if ("system".equals(entry.getLocalName())) {
                uri = entry.getAttribute("systemId");
            }

            if (uri != null) {
                long cacheTime = -1;
                String timeString = DOMUtils.attr(entry, Catalog.NS_XMLRESOURCE_EXT, "time");
                try {
                    cacheTime = Long.parseLong(timeString);
                } catch (NumberFormatException nfe) {
                    System.err.println(entry.getAttribute("uri") + ":");
                    System.err.println("\t" + uri);
                    System.err.println("\tInvalid cache time: " + timeString + "!?");
                }

                if (cache.expired(uri, entry.getAttribute("uri"), entry)) {
                    expCount++;
                    expired.add(uri);
                } else {
                    okCount++;
                    uptodate.add(uri);
                }
            }

            entry = DOMUtils.getNextElement(entry);
        }

        System.out.println("Expired " + expCount + " entries:");
        for (String uri : expired) {
            System.out.println("\t" + uri);
        }

        System.out.println("Up-to-date " + expCount + " entries:");
        for (String uri : uptodate) {
            System.out.println("\t" + uri);
        }

        System.out.println("Expired " + expCount + " entries; " + okCount + " up-to-date.");
    }
    
    private boolean expired(String origURI, String uri, long cacheTime) {
        if (origURI == null || !origURI.startsWith("http:")) {
            System.out.println(uri + ":");
            System.out.println("\t" + origURI);
            System.out.println("\tNot http:");
            return false;
        }
        
        try {
            URL url = new URL(uri);
            File localFile = new File(url.getPath());
            localFile.setLastModified(now.getTimeInMillis());
        } catch (MalformedURLException mue) {
            // nop
        }
        
        long lastModified = 0;
        try {
            URL url = new URL(origURI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            lastModified = conn.getLastModified();
            
            if (conn.getResponseCode() != 200) {
                System.out.println(uri + ":");
                System.out.println("\t" + origURI);
                System.out.println("\tHTTP returned " + conn.getResponseCode());
                return false;
            }
        } catch (MalformedURLException mue) {
            System.out.println(uri + ":");
            System.out.println("\t" + origURI);
            System.out.println("\tMalformedURLException");
            return false;
        } catch (IOException ioe) {
            System.out.println(uri + ":");
            System.out.println("\t" + origURI);
            System.out.println("\tIOException");
            return false;
        }

        if (lastModified == 0) {
            System.out.println(uri + ":");
            System.out.println("\t" + origURI);
            System.out.println("\tUnknown: no last-modified header");
            return false;
        } else if (lastModified > cacheTime) {
            System.out.println(uri + ":");
            System.out.println("\t" + origURI);
            System.out.println("\tExpired");
            return true;
        } else {
            return false;
        }
    }
}
