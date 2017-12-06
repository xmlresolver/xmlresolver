/*
 * Resource.java
 *
 * Created on January 9, 2007, 7:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver;

import java.io.InputStream;

/** Represents a web resource.
 *
 * <p>An web resource consists of an absolute URI, an input stream, and a MIME content type.
 * In some circumstances, such as when the initial URI is a <code>file:</code> URI, the
 * content type may be unknown (<code>null</code>).</p>
 */
public class Resource {
    private InputStream stream = null;
    private String uri = null;
    private String contentType = null;

    /** Creates a new instance of Resource.
     *
     * @param stream The stream from which the resource can be read
     * @param uri The URI
     */
    public Resource(InputStream stream, String uri) {
        this.stream = stream;
        this.uri = uri;
    }

    /** Creates a new instance of Resource.
     *
     * @param stream The stream from which the resource can be read
     * @param uri The URI
     * @param contentType The content type
     */
    public Resource(InputStream stream, String uri, String contentType) {
        this.stream = stream;
        this.uri = uri;
        this.contentType = contentType;
    }
    
    /** Return the InputStream associated with the resource.
     *
     * <p>The stream returned is the actual stream used when creating the resource. Reading from this
     * stream changes the resource.</p>
     *
     * @return The stream
     */
    public InputStream body() {
        return stream;
    }

    /** Return the URI associated with the resource.
     *
     * @return The URI
     */
    public String uri() {
        return uri;
    }

    /** Return the MIME content type associated with the resource.
     *
     * @return The content type
     */
    public String contentType() {
        return contentType;
    }
}
