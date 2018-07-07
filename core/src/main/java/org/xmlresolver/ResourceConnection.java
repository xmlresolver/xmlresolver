package org.xmlresolver;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: 2/8/12
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceConnection {
    private InputStream stream = null;
    private String absuri = null;
    private String redirect = null;
    private int statusCode = -1;
    private String contentType = null;
    private String etag = null;
    private Long lastModified = -1L;
    private Long date = -1L;
    private CloseableHttpClient httpclient = null;
    
    public ResourceConnection(String resolved) {
        try {
            URI uri = new URI(resolved);
            URL url = uri.toURL();

            absuri = url.toString();

            if (absuri.startsWith("http:") || absuri.startsWith("https:")) {
                // Use Apache HttpClient so that we can follow the redirect
                httpclient = HttpClients.createDefault();
                HttpClientContext context = HttpClientContext.create();
                HttpGet httpget = new HttpGet(absuri);
                HttpResponse httpResponse = httpclient.execute(httpget, context);
                HttpHost target = context.getTargetHost();
                List<URI> redirectLocations = context.getRedirectLocations();
                URI location = URIUtils.resolve(httpget.getURI(), target, redirectLocations);
                redirect = location.toASCIIString();
                if (absuri.equals(redirect)) {
                    redirect = null;
                }
                stream = httpResponse.getEntity().getContent();
                statusCode = httpResponse.getStatusLine().getStatusCode();
                contentType = getHeader(httpResponse, "Content-Type", "application/octet-stream");
                etag = getHeader(httpResponse, "Etag", null);

                SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
                String dateString = getHeader(httpResponse, "Last-Modified", null);
                if (dateString != null) {
                    try {
                        Date d = format.parse(dateString);
                        lastModified = d.getTime();
                    } catch (ParseException e) {
                        // nop
                    }
                }

                dateString = getHeader(httpResponse, "Date", null);
                if (dateString != null) {
                    try {
                        Date d = format.parse(dateString);
                        date = d.getTime();
                    } catch (ParseException e) {
                        // nop
                    }
                }
            } else {
                URLConnection connection = url.openConnection();
                connection.connect();
                stream = connection.getInputStream();
                contentType = null; // No point if it's not http
                etag = connection.getHeaderField("Etag");
                lastModified = connection.getLastModified();
                date = connection.getDate();
                statusCode = 200;
            }
        } catch (URISyntaxException | IOException use) {
            // nop
        }
    }

    public InputStream getStream() {
        return stream;
    }

    public String getContentType() {
        return contentType;
    }

    public String getEtag() {
        return etag;
    }

    public String getURI() {
        return absuri;
    }
    
    public String getRedirect() {
        return redirect;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getLastModified() {
        return lastModified;
    }

    public long getDate() {
        return date;
    }

    public void close() {
        if (httpclient != null) {
            try {
                httpclient.close();
            } catch (IOException e) {
                // nop
            }
        }
    }

    private String getHeader(HttpResponse resp, String name, String def) {
        Header[] headers = resp.getHeaders(name);

        if (headers == null) {
            return def;
        }

        if (headers.length == 0) {
            // This should never happen
            return def;
        } else {
            return headers[0].getValue();
        }
    }
}
