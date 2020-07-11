/*
 * URIUtils.java
 *
 * Created on December 27, 2006, 4:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmlresolver.helpers;

import java.io.UnsupportedEncodingException;

/** URI utility methods.
 *
 * @author ndw
 */
public class URIUtils {
    
    /** Creates a new instance of URIUtils */
    protected URIUtils() {
    }

    /**
     * Perform character normalization on a URI reference.
     *
     * @param uriref The URI reference
     * @return The normalized URI reference.
     */
    public static String normalizeURI(String uriref) {
        String newRef = "";
        byte[] bytes;

        if (uriref == null) {
            return null;
        }

        try {
            bytes = uriref.getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee) {
            // this can't happen
            return uriref;
        }

        for (int count = 0; count < bytes.length; count++) {
            int ch = bytes[count] & 0xFF;

            if ((ch <= 0x20)    // ctrl
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
                newRef += encodedByte(ch);
            } else {
                newRef += (char) bytes[count];
            }
        }

        return newRef;
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
