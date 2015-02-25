package org.xmlresolver;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

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
    
    public ResourceConnection(String resolved) {
        try {
            URI uri = new URI(resolved);
            URL url = uri.toURL();

            absuri = url.toString();

            if (absuri.startsWith("http:") || absuri.startsWith("https:")) {
                // Use Apache HttpClient so that we can follow the redirect
                SystemDefaultHttpClient client = new SystemDefaultHttpClient();
                client.setHttpRequestRetryHandler(new StandardHttpRequestRetryHandler(3, false));

                HttpParams params = new BasicHttpParams();
                HttpContext localContext = new BasicHttpContext();

                HttpUriRequest httpRequest = new HttpGet(absuri);
                httpRequest.setParams(params);

                HttpResponse httpResponse = client.execute(httpRequest, localContext);
                statusCode = httpResponse.getStatusLine().getStatusCode();
                contentType = getHeader(httpResponse, "Content-Type", "application/octet-stream");
                etag = getHeader(httpResponse, "Etag", null);
                
                if (statusCode == 200) {
                    stream = httpResponse.getEntity().getContent();

                    HttpHost host = (HttpHost) localContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                    HttpUriRequest req = (HttpUriRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST);
                    redirect = (req.getURI().isAbsolute()) ? req.getURI().toString() : (host.toURI() + req.getURI());
                    if (absuri.equals(redirect)) {
                        redirect = null;
                    }
                }
            } else {
                URLConnection connection = url.openConnection();
                connection.connect();
                stream = connection.getInputStream();
                contentType = null; // No point if it's not http
                etag = connection.getHeaderField("etag");
                statusCode = 200;
            }
        } catch (URISyntaxException use) {
            // nop
        } catch (IOException ioe) {
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
    
    private String getHeader(HttpResponse resp, String name, String def) {
        Header[] headers = resp.getHeaders(name);

        if (headers == null) {
            return def;
        }

        if (headers == null || headers.length == 0) {
            // This should never happen
            return def;
        } else {
            return headers[0].getValue();
        }
    }
}
