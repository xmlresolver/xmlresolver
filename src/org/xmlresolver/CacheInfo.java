/*
 * CacheInfo.java
 *
 * Created on February 2, 2007, 7:01 AM
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
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
        
        //cache.run("/projects/src/xmlresolver/catalogs/cache");
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
        
        System.out.println("Cache contains " + count + " entries.");
        
        entry = DOMUtils.getFirstElement(catalog.getDocumentElement());
        while (entry != null) {
            if ("uri".equals(entry.getLocalName())) {
                System.out.println("URI:    " + entry.getAttribute("name"));
                showLocalFile(entry.getAttribute("uri"));
                Long cacheTime = Long.parseLong(DOMUtils.attr(entry, Catalog.NS_XMLRESOURCE_EXT, "oTime"));
                Long checkTime = Long.parseLong(DOMUtils.attr(entry, Catalog.NS_XMLRESOURCE_EXT, "time"));
                if (cacheTime == checkTime) {
                    System.out.println("\tCached on " + showTime(cacheTime));
                } else {
                    System.out.println("\tCached on " + showTime(cacheTime) + " (last checked " + showTime(checkTime) + ")");
                }
                checkLastModified(entry.getAttribute("name"), cacheTime);
            } else if ("system".equals(entry.getLocalName())) {
                System.out.println("SYSTEM: " + entry.getAttribute("systemId"));
                showLocalFile(entry.getAttribute("uri"));
                Long cacheTime = Long.parseLong(DOMUtils.attr(entry, Catalog.NS_XMLRESOURCE_EXT, "oTime"));
                Long checkTime = Long.parseLong(DOMUtils.attr(entry, Catalog.NS_XMLRESOURCE_EXT, "time"));
                if (cacheTime == checkTime) {
                    System.out.println("\tCached on " + showTime(cacheTime));
                } else {
                    System.out.println("\tCached on " + showTime(cacheTime) + " (last checked " + showTime(checkTime) + ")");
                }
                checkLastModified(entry.getAttribute("systemId"), cacheTime);
            } else if ("public".equals(entry.getLocalName())) {
                System.out.println("PUBLIC: " + entry.getAttribute("publicId"));
                showLocalFile(entry.getAttribute("uri"));
            } else {
                System.out.println("UNEXPECTED ENTRY TYPE!");
                showLocalFile(entry.getAttribute("uri"));
                Long cacheTime = Long.parseLong(DOMUtils.attr(entry, Catalog.NS_XMLRESOURCE_EXT, "oTime"));
                Long checkTime = Long.parseLong(DOMUtils.attr(entry, Catalog.NS_XMLRESOURCE_EXT, "time"));
                if (cacheTime == checkTime) {
                    System.out.println("\tCached on " + showTime(cacheTime));
                } else {
                    System.out.println("\tCached on " + showTime(cacheTime) + " (last checked " + showTime(checkTime) + ")");
                }
            }

            /*
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
             */

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
    
    private void checkLastModified(String origURI, long cacheTime) {
        if (origURI == null || !origURI.startsWith("http:")) {
            System.out.println("\tCan't check age of actual resource.");
        }
        
        long lastModified = -1;
        try {
            URL url = new URL(origURI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() != 200) {
                System.out.println("\tHTTP returned " + conn.getResponseCode());
            }
            lastModified = conn.getLastModified();
        } catch (MalformedURLException mue) {
            System.out.println("\tMalformed URL: " + origURI);
            return;
        } catch (UnknownHostException uhe) {
            System.out.println("\tUnknown host exception: " + uhe.getMessage());
            return;
        } catch (IOException ioe) {
            System.out.println("\tI/O Exception: " + ioe.getMessage());
            return;
        }

        if (lastModified < 0) {
            System.out.println("\tServer did not report last-modified");
        } else {
            System.out.println("\tLast modified " + showTime(lastModified));
        }

        if (lastModified > cacheTime) {
            System.out.println("\tEXPIRED");
        }
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
