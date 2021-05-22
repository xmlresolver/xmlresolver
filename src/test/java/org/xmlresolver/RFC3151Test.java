package org.xmlresolver;

import org.junit.Test;
import org.xmlresolver.utils.PublicId;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class RFC3151Test {
    @Test
    public void decodeDocBook() {
        String urn = "urn:publicid:-:OASIS:DTD+DocBook+XML+V4.1.2:EN";
        String publicId = "-//OASIS//DTD DocBook XML V4.1.2//EN";
        String decodedId = PublicId.decodeURN(urn);
        assertEquals(publicId, decodedId);
    }

    @Test
    public void encodeDecode() {
        String publicId = "-//EXAMPLE//DTD Example//EN";
        URI encodedId = PublicId.encodeURN(publicId);
        String decodedId = PublicId.decodeURN(encodedId.toString());
        assertEquals(publicId, decodedId);
    }

    @Test
    public void rfcexample1() {
        String publicId = "ISO/IEC 10179:1996//DTD DSSSL Architecture//EN";
        String urn = "urn:publicid:ISO%2FIEC+10179%3A1996:DTD+DSSSL+Architecture:EN";
        assertEquals(urn, PublicId.encodeURN(publicId).toString());
        assertEquals(publicId, PublicId.decodeURN(urn));
    }

    @Test
    public void rfcexample2() {
        String publicId = "ISO 8879:1986//ENTITIES Added Latin 1//EN";
        String urn = "urn:publicid:ISO+8879%3A1986:ENTITIES+Added+Latin+1:EN";
        assertEquals(urn, PublicId.encodeURN(publicId).toString());
        assertEquals(publicId, PublicId.decodeURN(urn));
    }

    @Test
    public void rfcexample3() {
        String publicId = "-//OASIS//DTD DocBook XML V4.1.2//EN";
        String urn = "urn:publicid:-:OASIS:DTD+DocBook+XML+V4.1.2:EN";
        assertEquals(urn, PublicId.encodeURN(publicId).toString());
        assertEquals(publicId, PublicId.decodeURN(urn));
    }

    @Test
    public void rfcexample4() {
        String publicId = "+//IDN example.org//DTD XML Bookmarks 1.0//EN//XML";
        String urn = "urn:publicid:%2B:IDN+example.org:DTD+XML+Bookmarks+1.0:EN:XML";
        assertEquals(urn, PublicId.encodeURN(publicId).toString());
        assertEquals(publicId, PublicId.decodeURN(urn));
    }

    @Test
    public void rfcexample5() {
        String publicId = "-//ArborText::prod//DTD Help Document::19970708//EN";
        String urn = "urn:publicid:-:ArborText;prod:DTD+Help+Document;19970708:EN";
        assertEquals(urn, PublicId.encodeURN(publicId).toString());
        assertEquals(publicId, PublicId.decodeURN(urn));
    }

    @Test
    public void rfcexample6() {
        String publicId = "foo";
        String urn = "urn:publicid:foo";
        assertEquals(urn, PublicId.encodeURN(publicId).toString());
        assertEquals(publicId, PublicId.decodeURN(urn));
    }

    @Test
    public void rfcexample7() {
        String publicId = "3+3=6";
        String urn = "urn:publicid:3%2B3=6";
        assertEquals(urn, PublicId.encodeURN(publicId).toString());
        assertEquals(publicId, PublicId.decodeURN(urn));
    }

    @Test
    public void rfcexample8() {
        String publicId = "-//Acme, Inc.//DTD Book Version 1.0";
        String urn = "urn:publicid:-:Acme,+Inc.:DTD+Book+Version+1.0";
        assertEquals(urn, PublicId.encodeURN(publicId).toString());
        assertEquals(publicId, PublicId.decodeURN(urn));
    }
}
