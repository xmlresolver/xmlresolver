package org.xmlresolver.sources;

import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;
import org.xmlresolver.Resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

/** An {@link LSInput} with a <code>resolvedURI</code>.
 *
 */
public class ResolverLSInput implements LSInput {
    /** The underlying, resolved URI. */
    public final URI resolvedURI;
    final Resource rsrc;
    final String publicId;
    final String systemId;

    /** Construct the {@link org.w3c.dom.ls.LSInput} while preserving the local URI. */
    public ResolverLSInput(Resource rsrc, String publicId, String systemId) {
        resolvedURI = rsrc.localUri();
        this.rsrc = rsrc;
        this.publicId = publicId;
        this.systemId = systemId;
    }

    /** The LSInput API... */
    public Reader getCharacterStream() {
        return new InputStreamReader(rsrc.body());
    }

    /** The LSInput API... */
    public void setCharacterStream(Reader reader) {
        throw new UnsupportedOperationException("Can't set character stream on resolver LSInput");
    }

    /** The LSInput API... */
    public InputStream getByteStream() {
        return rsrc.body();
    }

    /** The LSInput API... */
    public void setByteStream(InputStream inputStream) {
        throw new UnsupportedOperationException("Can't set byte stream on resolver LSInput");
    }

    /** The LSInput API... */
    public String getStringData() {
        // The data is in the stream
        return null;
    }

    /** The LSInput API... */
    public void setStringData(String string) {
        throw new UnsupportedOperationException("Can't set string data on resolver LSInput");
    }

    /** The LSInput API... */
    public String getSystemId() {
        return systemId;
    }

    /** The LSInput API... */
    public void setSystemId(String string) {
        throw new UnsupportedOperationException("Can't set system ID on resolver LSInput");
    }

    /** The LSInput API... */
    public String getPublicId() {
        return publicId;
    }

    /** The LSInput API... */
    public void setPublicId(String string) {
        throw new UnsupportedOperationException("Can't set public ID on resolver LSInput");
    }

    /** The LSInput API... */
    public String getBaseURI() {
        return rsrc.uri().toString();
    }

    /** The LSInput API... */
    public void setBaseURI(String string) {
        throw new UnsupportedOperationException("Can't set base URI on resolver LSInput");
    }

    /** The LSInput API... */
    public String getEncoding() {
        return null; // Unknown
    }

    /** The LSInput API... */
    public void setEncoding(String string) {
        throw new UnsupportedOperationException("Can't set encoding on resolver LSInput");
    }

    /** The LSInput API... */
    public boolean getCertifiedText() {
        return false;
    }

    /** The LSInput API... */
    public void setCertifiedText(boolean b) {
        throw new UnsupportedOperationException("Can't set certified text on resolver LSInput");
    }
}
