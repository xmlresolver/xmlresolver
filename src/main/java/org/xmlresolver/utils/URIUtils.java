/*
 * URIUtils.java
 *
 * Created on December 27, 2006, 4:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver.utils;

import org.jetbrains.annotations.NotNull;
import org.xmlresolver.ResolverLogger;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/** URI utility methods.
 *
 * @author ndw
 */
public abstract class URIUtils {
    protected static ResolverLogger logger = new ResolverLogger(URIUtils.class);

    /**
     * Creates a URI for the users current working directory.
     *
     * In order that this method should neither raise an exception nor return <code>null</code>,
     * if the <code>user.dir</code> system property cannot be converted into a URI, the
     * file URI "<code>file:///</code>" is returned.
     *
     * @return a file: URI for the current working directory
     */
    public static @NotNull URI cwd() {
        String dir = System.getProperty("user.dir");
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        try {
            return URIUtils.newURI(dir);
        } catch (URISyntaxException ex) {
            logger.log(ResolverLogger.WARNING, "Failed to create URI from user.dir: %s", dir);
            return URI.create("file:///"); // ¯\_(ツ)_/¯
        }
    }

    /**
     * Create a new URI, attempting to deal with the vagaries of file: URIs.
     *
     * Given something that looks like a file: URI or a path, return
     * a file: URI with a consistent number of leading "/". characters.
     * Any number of leading slashes are interpreted the same way.
     * (This is slightly at odds with specs, as file:///////path should
     * probably be an error. But this method "fixes it" to file:///path.)
     *
     * Strings that don't begin file: or /, are constructed with new URI() without
     * preprocessing. This will construct URIs for other schemes if the href
     * parameter is valid.
     *
     * This method will encode query strings, but that's ok for this application.
     *
     * @param href The string to be interpreted as a URI.
     * @return A URI constructed from the href parameter.
     * @throws URISyntaxException if the string cannot be converted into a URI.
     */
    public static URI newURI(String href) throws URISyntaxException {
        // This is a tiny bit complicated because I'm trying to avoid creating additional
        // string objects if I don't have to. I wonder if the effort is worth it.
        // Since I haven't measured it, "no".
        if (href.startsWith("file:")) {
            int pos = 5;
            while (pos <= href.length() && href.charAt(pos) == '/') {
                pos++;
            }
            if (pos > 5) {
                pos--;
            } else {
                pos = 0;
                href = "/" + href.substring(5);
            }
            if (href.contains("#")) {
                int hashpos = href.indexOf('#');
                String base = href.substring(pos, hashpos);
                String fragid = href.substring(hashpos + 1);
                return new URI("file", "", base, fragid);
            } else {
                return new URI("file", "", href.substring(pos), null);
            }
        } else if (href.startsWith("/")) {
            if (href.contains("#")) {
                int hashpos = href.indexOf('#');
                String base = href.substring(0, hashpos);
                String fragid = href.substring(hashpos + 1);
                return new URI("file", "", base, fragid);
            } else {
                return new URI("file", "", href, null);
            }
        } else {
            return new URI(href);
        }
    }

    /**
     * Perform character normalization on a URI reference.
     *
     * @param uriref The URI reference.
     * @return The normalized URI reference.
     */
    public static String normalizeURI(String uriref) {
        if (uriref == null) {
            return null;
        }

        StringBuilder newRef = new StringBuilder();
        byte[] bytes = uriref.getBytes(StandardCharsets.UTF_8);

        for (byte aByte : bytes) {
            int ch = aByte & 0xFF;
            if ((ch <= 0x20)        // ctrl
                    || (ch > 0x7F)  // high ascii
                    || (ch == 0x22) // "
                    || (ch == 0x3C) // <
                    || (ch == 0x3E) // >
                    || (ch == 0x5C) // \
                    || (ch == 0x5E) // ^
                    || (ch == 0x60) // `
                    || (ch == 0x7B) // {
                    || (ch == 0x7C) // |
                    || (ch == 0x7D) // }
                    || (ch == 0x7F)) {
                newRef.append(encodedByte(ch));
            } else {
                newRef.append((char) aByte);
            }
        }

        return newRef.toString();
    }

    /**
     * Perform %-encoding on a single byte.
     *
     * @param b The 8-bit integer that represents th byte. (Bytes are signed
     * but encoding needs to look at the bytes unsigned.)
     * @return The %-encoded string for the byte in question.
     */
    private static String encodedByte(int b) {
        String hex = Integer.toHexString(b).toUpperCase();
        if (hex.length() < 2) {
            return "%0" + hex;
        } else {
            return "%" + hex;
        }
    }
}
