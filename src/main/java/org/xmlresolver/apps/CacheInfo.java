/*
 * CacheInfo.java
 *
 * Created on February 2, 2007, 7:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver.apps;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.w3c.dom.Element;
import org.xmlresolver.Catalog;
import org.xmlresolver.ResourceCache;
import org.xmlresolver.ResourceConnection;
import org.xmlresolver.helpers.DOMUtils;

/** An application to display information about items in the cache.
 *
 * @author ndw
 */
public class CacheInfo {
    private static String usage = "Usage: CacheInfo cache-directory";
    private static GregorianCalendar now = new GregorianCalendar();
    
    /** Creates a new instance of CacheInfo */
    public CacheInfo() {
    }

    public static void main(String[] args) {
        CacheInfo cache = new CacheInfo();

        if (args.length != 1) {
            System.err.println(usage);
            System.exit(1);
        }

        cache.run(args[0]);
    }

    public void run(String dir) {
        ResourceCache cache = new ResourceCache(dir);
        Element catalog = cache.catalog();
        Vector<String> expired = new Vector<String> ();
        Vector<String> uptodate = new Vector<String> ();

        if (catalog == null) {
            System.err.println(usage);
            System.exit(2);
        }

        int count = 0;
        Element entry = DOMUtils.getFirstElement(catalog);
        while (entry != null) {
            String uri = null;
            if ("uri".equals(entry.getLocalName())) {
                count++;
            } else if ("system".equals(entry.getLocalName())) {
                count++;
            }
            entry = DOMUtils.getNextElement(entry);
        }
        
        System.out.println("Cache contains " + count + " entries.");
        
        entry = DOMUtils.getFirstElement(catalog);
        while (entry != null) {
            Long cacheTime = -1L;
            String timestr = DOMUtils.attr(entry, Catalog.NS_XMLRESOURCE_EXT, "time");
            if (timestr != null) {
                cacheTime = Long.parseLong(timestr);
            }
            String etag = DOMUtils.attr(entry, Catalog.NS_XMLRESOURCE_EXT, "etag");

            if ("uri".equals(entry.getLocalName())) {
                System.out.println("URI:    " + entry.getAttribute("name"));
                showLocalFile(entry.getAttribute("uri"));
                System.out.println("\tCached on " + showTime(cacheTime));
                checkLastModified(entry.getAttribute("name"), cacheTime, etag);
                if (cache.expired(entry.getAttribute("name"), entry.getAttribute("uri"), entry)) {
                    System.out.println("\tEXPIRED");
                }
            } else if ("system".equals(entry.getLocalName())) {
                System.out.println("SYSTEM: " + entry.getAttribute("systemId"));
                showLocalFile(entry.getAttribute("uri"));
                System.out.println("\tCached on " + showTime(cacheTime));
                checkLastModified(entry.getAttribute("systemId"), cacheTime, etag);
                if (cache.expired(entry.getAttribute("systemId"), entry.getAttribute("uri"), entry)) {
                    System.out.println("\tEXPIRED");
                }
            } else if ("public".equals(entry.getLocalName())) {
                System.out.println("PUBLIC: " + entry.getAttribute("publicId"));
                showLocalFile(entry.getAttribute("uri"));
            } else {
                System.out.println("UNEXPECTED ENTRY TYPE!");
                showLocalFile(entry.getAttribute("uri"));
                System.out.println("\tCached on " + showTime(cacheTime));
            }
            entry = DOMUtils.getNextElement(entry);
        }
    }

    private void showLocalFile(String filename) {
        System.out.println("\tLocal: " + filename);
    }
    
    private String showTime(long cacheTime) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                           "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        GregorianCalendar cacheDate = new GregorianCalendar();
        cacheDate.setTimeInMillis(cacheTime);

        String result = cacheDate.get(Calendar.DAY_OF_MONTH) + " ";
        result += months[cacheDate.get(Calendar.MONTH)] + " ";
        result += cacheDate.get(Calendar.YEAR) + " at ";
        if (cacheDate.get(Calendar.HOUR) < 10) { result += "0"; }
        result += cacheDate.get(Calendar.HOUR) + ":";
        if (cacheDate.get(Calendar.MINUTE) < 10) { result += "0"; }
        result += cacheDate.get(Calendar.MINUTE) + ":";
        if (cacheDate.get(Calendar.SECOND) < 10) { result += "0"; }
        result += cacheDate.get(Calendar.SECOND);
        
        return result;
    }
    
    private void checkLastModified(String origURI, long cacheTime, String cachedEtag) {
        if (origURI == null || ! (origURI.startsWith("http:") || origURI.startsWith("https:"))) {
            System.out.println("\tCan't check age of actual resource.");
        }

        ResourceConnection rconn = new ResourceConnection(origURI);
        rconn.close();
        long lastModified = rconn.getLastModified();
        String etag = rconn.getEtag();
        if (rconn.getStatusCode() != 200) {
            System.out.println("\tHTTP returned " + rconn.getStatusCode());
        }

        if (lastModified <= 0) {
            System.out.println("\tServer did not report last-modified");
        } else {
            System.out.println("\tLast modified on " + showTime(lastModified));
        }

        if (etag != null && cachedEtag != null) {
            if (etag.equals(cachedEtag)) {
                System.out.println("\tEtags match: " + etag);
            } else {
                System.out.println("\tEtags don't match: " + cachedEtag + " =/= " + etag);
            }
        }

        if (lastModified > cacheTime) {
            System.out.println("\tEXPIRED");
        }
    }

    private boolean expired(String origURI, String uri, long cacheTime) {
        if (origURI == null || !(origURI.startsWith("http:") || origURI.startsWith("https:"))) {
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
