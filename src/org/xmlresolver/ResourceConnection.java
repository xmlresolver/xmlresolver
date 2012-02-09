package org.xmlresolver;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

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
                HttpClient client = new HttpClient();
                GetMethod get = new GetMethod(absuri);
                // You must provide a custom retry handler
                get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
                statusCode = client.executeMethod(get);

                contentType = getHeader(get, "Content-Type", "application/octet-stream");
                etag = getHeader(get, "Etag", null);
                
                if (statusCode == 200) {
                    stream = get.getResponseBodyAsStream();
                    redirect = get.getURI().toString();
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
    
    private String getHeader(GetMethod get, String name, String def) {
        Header contentTypeHeader = get.getResponseHeader(name);
        HeaderElement[] elems = contentTypeHeader.getElements();
        if (elems == null || elems.length == 0) {
            // This should never happen
            return def;
        } else {
            return elems[0].getName();
        }
    }
}
