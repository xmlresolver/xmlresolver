package org.xmlresolver;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.protocol.RedirectLocations;
import org.apache.hc.client5.http.utils.URIUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.xmlresolver.logging.AbstractLogger;
import org.xmlresolver.logging.ResolverLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ResourceConnection {
    private InputStream stream = null;
    private URI uri = null;
    private URI redirect = null;
    private int statusCode = -1;
    private String contentType = null;
    private String etag = null;
    private Long lastModified = -1L;
    private Long date = -1L;
    private CloseableHttpClient httpclient = null;

    // The headOnly parameter is a bit of a hack, but it's convenient to reuse the
    // ResourceConnection logic in multiple places. To check properties, I only
    // want to make a HEAD request, so ...

    public ResourceConnection(ResolverConfiguration config, String resolved) {
        this(config, resolved, false);
    }

    public ResourceConnection(ResolverConfiguration config, String resolved, boolean headOnly) {
        try {
            uri = org.xmlresolver.utils.URIUtils.newURI(resolved);
            URL url = uri.toURL();

            if ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) {
                // Use Apache HttpClient so that we can follow the redirect
                httpclient = HttpClients.createDefault();
                HttpClientContext context = HttpClientContext.create();

                HttpUriRequestBase httpreq = null;
                if (headOnly) {
                    httpreq = new HttpHead(uri);
                } else {
                    httpreq = new HttpGet(uri);
                }

                HttpResponse httpResponse = httpclient.execute(httpreq, context);
                HttpHost target = context.getHttpRoute().getTargetHost();
                RedirectLocations redirectLocations = context.getRedirectLocations();
                URI location = URIUtils.resolve(httpreq.getUri(), target, redirectLocations.getAll());
                if (uri.equals(location)) {
                    redirect = null;
                } else {
                    redirect = location;
                }

                statusCode = httpResponse.getCode();
                contentType = getHeader(httpResponse, "Content-Type", "application/octet-stream");
                etag = getHeader(httpResponse, "Etag", null);
                if (!headOnly) {
                    stream = ((ClassicHttpResponse)httpResponse).getEntity().getContent();
                }

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

                if (headOnly) {
                    connection.getInputStream().close();
                } else {
                    stream = connection.getInputStream();
                }

                contentType = null; // No point if it's not http
                etag = connection.getHeaderField("Etag");
                lastModified = connection.getLastModified();
                date = connection.getDate();

                try {
                    // This is almost always going to be an http URL
                    HttpURLConnection http = (HttpURLConnection) connection;
                    statusCode = http.getResponseCode();
                } catch (ClassCastException ex) {
                    // Assume it's ok?
                    statusCode = 200;
                }
            }
        } catch (URISyntaxException | IOException | IllegalArgumentException use) {
            ResolverLogger logger = config.getFeature(ResolverFeature.RESOLVER_LOGGER);
            logger.log(AbstractLogger.WARNING, "Failed to %s: %s: %s", headOnly ? "HEAD" : "GET", resolved, use.getMessage());
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

    public URI getUri() {
        return uri;
    }
    
    public URI getRedirect() {
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
