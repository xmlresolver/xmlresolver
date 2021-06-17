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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.xmlresolver.ResolverFeature;
import org.xmlresolver.XMLResolverConfiguration;
import org.xmlresolver.cache.CacheEntry;
import org.xmlresolver.cache.ResourceCache;
import org.xmlresolver.ResourceConnection;
import org.xmlresolver.utils.URIUtils;

/** An application to display information about items in the cache.
 *
 * @author ndw
 */
public class CacheDetails {
    private static String usage = "Usage: CacheInfo cache-directory";
    private static GregorianCalendar now = new GregorianCalendar();
    
    /** Creates a new instance of CacheInfo */
    public CacheDetails() {
    }

    public static void main(String[] args) {
        CacheDetails cache = new CacheDetails();

        if (args.length != 1) {
            System.err.println(usage);
            System.exit(1);
        }

        cache.run(args[0]);
    }

    public void run(String dir) {
        XMLResolverConfiguration config = new XMLResolverConfiguration();
        config.setFeature(ResolverFeature.CACHE_DIRECTORY, dir);
        ResourceCache cache = new ResourceCache(config);
        if (cache.directory() == null) {
            System.err.println(usage);
            System.exit(2);
        }

        ArrayList<String> expired = new ArrayList<>();
        ArrayList<String> uptodate = new ArrayList<> ();

        System.out.println("Cache contains " + cache.entries().size() + " entries.");

        for (CacheEntry entry : cache.entries()) {
            System.out.println(entry.uri);
            switch (entry.entry.getType()) {
                case URI:
                    System.out.println("\t<uri> entry");
                    break;
                case SYSTEM:
                    System.out.println("\t<system> entry");
                    break;
                case PUBLIC:
                    System.out.println("\t<public> entry");
                    break;
                default:
                    System.out.println("\tUnknown entry type!");
                    break;
            }
            System.out.println("\tLocal: " + entry.file.getAbsolutePath());
            if (entry.contentType() != null) {
                System.out.println("\tContent type: " + entry.contentType());
            }
            System.out.println("\tCached on " + showTime(entry.time));
            checkLastModified(entry);
            try {
                if (cache.expired(URIUtils.newURI(entry.file.getAbsolutePath()))) {
                    System.out.println("\tEXPIRED");
                }
            } catch (URISyntaxException ex) {
                System.out.println("\tURI SYNTAX EXCEPTION: " + entry.file.getAbsolutePath());
            }
            System.out.println();
        }
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
    
    private void checkLastModified(CacheEntry entry) {
        String uri = entry.uri.toString();
        if (uri.startsWith("file:")) {
            File src = new File(entry.uri.getPath());
            if (!src.exists()) {
                System.out.println("\tFile does not exist");
            } else {
                System.out.println("\tLast modified on " + showTime(src.lastModified()));
                if (src.lastModified() > entry.time) {
                    System.out.println("\tEXPIRED");
                }
            }
            return;
        } else if (!uri.startsWith("http:") && !uri.startsWith("https:")) {
            System.out.println("\tCan't check age of actual resource.");
            return;
        }

        ResourceConnection rconn = new ResourceConnection(entry.uri.toASCIIString());
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

        if (etag != null && entry.etag() != null) {
            if (etag.equals(entry.etag())) {
                System.out.println("\tEtags match: " + etag);
            } else {
                System.out.println("\tEtags don't match: " + entry.etag() + " ≠ " + etag);
            }
        }

        if (lastModified > entry.time) {
            System.out.println("\tEXPIRED");
        }
    }
}
