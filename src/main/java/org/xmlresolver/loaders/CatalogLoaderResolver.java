package org.xmlresolver.loaders;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/** An entity resolver for the catalog loader
 *
 * <p>The only entities that this resolver needs to resolve are the DTDs for the catalogs.
 * (It isn't practical to use the CatalogResolver for these because it would create an
 * infinite loop.)</p>
 *
 * <p>This resolver handles the OASIS Open 1.0 and 1.1 catalog public and system
 * identifiers. If you have a custom DTD for your catalogs, you'll have to make sure that
 * it will resolve correctly at runtime or replace this resolver with one that handles
 * your custom DTD(s).</p>
 *
 */
public class CatalogLoaderResolver implements EntityResolver {
    // What I'd really like to do is load these from the jar file. But if I try to do that
    // I have to deal with the vagaries of what class loader is in use. The 1.0 and 1.1
    // DTDs are *never* going to change. If you're using edited versions of them, you
    // should have changed the public identifier. So I've base64 encoded them and stuck
    // them in as strings. ¯\_(ツ)_/¯

    /**
     * Construct the resolver.
     */
    public CatalogLoaderResolver() {
        // nop
    }

    private static final String catalog10dtd = "PCEtLSAkSWQ6IGNhdGFsb2cuZHRkLHYgMS4xMCAyMDAyLzEwLzE4IDIzOjU0OjU4IG5kdyBF" +
            "eHAgJCAtLT4KCjwhRU5USVRZICUgcHViSWRDaGFycyAiQ0RBVEEiPgo8IUVOVElUWSAlIHB1" +
            "YmxpY0lkZW50aWZpZXIgIiVwdWJJZENoYXJzOyI+CjwhRU5USVRZICUgcGFydGlhbFB1Ymxp" +
            "Y0lkZW50aWZpZXIgIiVwdWJJZENoYXJzOyI+CjwhRU5USVRZICUgdXJpUmVmZXJlbmNlICJD" +
            "REFUQSI+CjwhRU5USVRZICUgc3RyaW5nICJDREFUQSI+CjwhRU5USVRZICUgc3lzdGVtT3JQ" +
            "dWJsaWMgIihzeXN0ZW18cHVibGljKSI+Cgo8IUVOVElUWSAlIHAgIiI+CjwhRU5USVRZICUg" +
            "cyAiIj4KPCFFTlRJVFkgJSBuc2RlY2wgInhtbG5zJXM7Ij4KCjwhRU5USVRZICUgY2F0YWxv" +
            "ZyAiJXA7Y2F0YWxvZyI+CjwhRU5USVRZICUgcHVibGljICIlcDtwdWJsaWMiPgo8IUVOVElU" +
            "WSAlIHN5c3RlbSAiJXA7c3lzdGVtIj4KPCFFTlRJVFkgJSB1cmkgIiVwO3VyaSI+CjwhRU5U" +
            "SVRZICUgcmV3cml0ZVN5c3RlbSAiJXA7cmV3cml0ZVN5c3RlbSI+CjwhRU5USVRZICUgcmV3" +
            "cml0ZVVSSSAiJXA7cmV3cml0ZVVSSSI+CjwhRU5USVRZICUgZGVsZWdhdGVQdWJsaWMgIiVw" +
            "O2RlbGVnYXRlUHVibGljIj4KPCFFTlRJVFkgJSBkZWxlZ2F0ZVN5c3RlbSAiJXA7ZGVsZWdh" +
            "dGVTeXN0ZW0iPgo8IUVOVElUWSAlIGRlbGVnYXRlVVJJICIlcDtkZWxlZ2F0ZVVSSSI+Cjwh" +
            "RU5USVRZICUgbmV4dENhdGFsb2cgIiVwO25leHRDYXRhbG9nIj4KPCFFTlRJVFkgJSBncm91" +
            "cCAiJXA7Z3JvdXAiPgoKPCFFTlRJVFkgJSBsb2NhbC5jYXRhbG9nLm1peCAiIj4KPCFFTlRJ" +
            "VFkgJSBsb2NhbC5jYXRhbG9nLmF0dHJpYnMgIiI+Cgo8IUVMRU1FTlQgJWNhdGFsb2c7ICgl" +
            "cHVibGljO3wlc3lzdGVtO3wldXJpOwogICAgICAgICAgICAgICAgICAgICB8JXJld3JpdGVT" +
            "eXN0ZW07fCVyZXdyaXRlVVJJOwogICAgICAgICAgICAgICAgICAgICB8JWRlbGVnYXRlUHVi" +
            "bGljO3wlZGVsZWdhdGVTeXN0ZW07fCVkZWxlZ2F0ZVVSSTsKICAgICAgICAgICAgICAgICAg" +
            "ICAgfCVuZXh0Q2F0YWxvZzt8JWdyb3VwOyAlbG9jYWwuY2F0YWxvZy5taXg7KSs+CjwhQVRU" +
            "TElTVCAlY2F0YWxvZzsKCSVuc2RlY2w7CSV1cmlSZWZlcmVuY2U7CQkjRklYRUQKCQkndXJu" +
            "Om9hc2lzOm5hbWVzOnRjOmVudGl0eTp4bWxuczp4bWw6Y2F0YWxvZycKCXByZWZlcgkJJXN5" +
            "c3RlbU9yUHVibGljOwkjSU1QTElFRAoJeG1sOmJhc2UJJXVyaVJlZmVyZW5jZTsJCSNJTVBM" +
            "SUVECgklbG9jYWwuY2F0YWxvZy5hdHRyaWJzOwo+Cgo8IUVOVElUWSAlIGxvY2FsLnB1Ymxp" +
            "Yy5hdHRyaWJzICIiPgoKPCFFTEVNRU5UICVwdWJsaWM7IEVNUFRZPgo8IUFUVExJU1QgJXB1" +
            "YmxpYzsKCWlkCQlJRAkJCSNJTVBMSUVECglwdWJsaWNJZAklcHVibGljSWRlbnRpZmllcjsJ" +
            "I1JFUVVJUkVECgl1cmkJCSV1cmlSZWZlcmVuY2U7CQkjUkVRVUlSRUQKCXhtbDpiYXNlCSV1" +
            "cmlSZWZlcmVuY2U7CQkjSU1QTElFRAogICAgICAgICVsb2NhbC5wdWJsaWMuYXR0cmliczsK" +
            "PgoKPCFFTlRJVFkgJSBsb2NhbC5zeXN0ZW0uYXR0cmlicyAiIj4KCjwhRUxFTUVOVCAlc3lz" +
            "dGVtOyBFTVBUWT4KPCFBVFRMSVNUICVzeXN0ZW07CglpZAkJSUQJCQkjSU1QTElFRAoJc3lz" +
            "dGVtSWQJJXN0cmluZzsJCSNSRVFVSVJFRAoJdXJpCQkldXJpUmVmZXJlbmNlOwkJI1JFUVVJ" +
            "UkVECgl4bWw6YmFzZQkldXJpUmVmZXJlbmNlOwkJI0lNUExJRUQKICAgICAgICAlbG9jYWwu" +
            "c3lzdGVtLmF0dHJpYnM7Cj4KCjwhRU5USVRZICUgbG9jYWwudXJpLmF0dHJpYnMgIiI+Cgo8" +
            "IUVMRU1FTlQgJXVyaTsgRU1QVFk+CjwhQVRUTElTVCAldXJpOwoJaWQJCUlECQkJI0lNUExJ" +
            "RUQKCW5hbWUJCSVzdHJpbmc7CQkjUkVRVUlSRUQKCXVyaQkJJXVyaVJlZmVyZW5jZTsJCSNS" +
            "RVFVSVJFRAoJeG1sOmJhc2UJJXVyaVJlZmVyZW5jZTsJCSNJTVBMSUVECiAgICAgICAgJWxv" +
            "Y2FsLnVyaS5hdHRyaWJzOwo+Cgo8IUVOVElUWSAlIGxvY2FsLnJld3JpdGVTeXN0ZW0uYXR0" +
            "cmlicyAiIj4KCjwhRUxFTUVOVCAlcmV3cml0ZVN5c3RlbTsgRU1QVFk+CjwhQVRUTElTVCAl" +
            "cmV3cml0ZVN5c3RlbTsKCWlkCQlJRAkJCSNJTVBMSUVECglzeXN0ZW1JZFN0YXJ0U3RyaW5n" +
            "CSVzdHJpbmc7CSNSRVFVSVJFRAoJcmV3cml0ZVByZWZpeAkJJXN0cmluZzsJCSNSRVFVSVJF" +
            "RAogICAgICAgICVsb2NhbC5yZXdyaXRlU3lzdGVtLmF0dHJpYnM7Cj4KCjwhRU5USVRZICUg" +
            "bG9jYWwucmV3cml0ZVVSSS5hdHRyaWJzICIiPgoKPCFFTEVNRU5UICVyZXdyaXRlVVJJOyBF" +
            "TVBUWT4KPCFBVFRMSVNUICVyZXdyaXRlVVJJOwoJaWQJCUlECQkJI0lNUExJRUQKCXVyaVN0" +
            "YXJ0U3RyaW5nCSVzdHJpbmc7CQkjUkVRVUlSRUQKCXJld3JpdGVQcmVmaXgJJXN0cmluZzsJ" +
            "CSNSRVFVSVJFRAogICAgICAgICVsb2NhbC5yZXdyaXRlVVJJLmF0dHJpYnM7Cj4KCjwhRU5U" +
            "SVRZICUgbG9jYWwuZGVsZWdhdGVQdWJsaWMuYXR0cmlicyAiIj4KCjwhRUxFTUVOVCAlZGVs" +
            "ZWdhdGVQdWJsaWM7IEVNUFRZPgo8IUFUVExJU1QgJWRlbGVnYXRlUHVibGljOwoJaWQJCUlE" +
            "CQkJI0lNUExJRUQKCXB1YmxpY0lkU3RhcnRTdHJpbmcJJXBhcnRpYWxQdWJsaWNJZGVudGlm" +
            "aWVyOwkjUkVRVUlSRUQKCWNhdGFsb2cJCSV1cmlSZWZlcmVuY2U7CQkjUkVRVUlSRUQKCXht" +
            "bDpiYXNlCSV1cmlSZWZlcmVuY2U7CQkjSU1QTElFRAogICAgICAgICVsb2NhbC5kZWxlZ2F0" +
            "ZVB1YmxpYy5hdHRyaWJzOwo+Cgo8IUVOVElUWSAlIGxvY2FsLmRlbGVnYXRlU3lzdGVtLmF0" +
            "dHJpYnMgIiI+Cgo8IUVMRU1FTlQgJWRlbGVnYXRlU3lzdGVtOyBFTVBUWT4KPCFBVFRMSVNU" +
            "ICVkZWxlZ2F0ZVN5c3RlbTsKCWlkCQlJRAkJCSNJTVBMSUVECglzeXN0ZW1JZFN0YXJ0U3Ry" +
            "aW5nCSVzdHJpbmc7CSNSRVFVSVJFRAoJY2F0YWxvZwkJJXVyaVJlZmVyZW5jZTsJCSNSRVFV" +
            "SVJFRAoJeG1sOmJhc2UJJXVyaVJlZmVyZW5jZTsJCSNJTVBMSUVECiAgICAgICAgJWxvY2Fs" +
            "LmRlbGVnYXRlU3lzdGVtLmF0dHJpYnM7Cj4KCjwhRU5USVRZICUgbG9jYWwuZGVsZWdhdGVV" +
            "UkkuYXR0cmlicyAiIj4KCjwhRUxFTUVOVCAlZGVsZWdhdGVVUkk7IEVNUFRZPgo8IUFUVExJ" +
            "U1QgJWRlbGVnYXRlVVJJOwoJaWQJCUlECQkJI0lNUExJRUQKCXVyaVN0YXJ0U3RyaW5nCSVz" +
            "dHJpbmc7CQkjUkVRVUlSRUQKCWNhdGFsb2cJCSV1cmlSZWZlcmVuY2U7CQkjUkVRVUlSRUQK" +
            "CXhtbDpiYXNlCSV1cmlSZWZlcmVuY2U7CQkjSU1QTElFRAogICAgICAgICVsb2NhbC5kZWxl" +
            "Z2F0ZVVSSS5hdHRyaWJzOwo+Cgo8IUVOVElUWSAlIGxvY2FsLm5leHRDYXRhbG9nLmF0dHJp" +
            "YnMgIiI+Cgo8IUVMRU1FTlQgJW5leHRDYXRhbG9nOyBFTVBUWT4KPCFBVFRMSVNUICVuZXh0" +
            "Q2F0YWxvZzsKCWlkCQlJRAkJCSNJTVBMSUVECgljYXRhbG9nCQkldXJpUmVmZXJlbmNlOwkJ" +
            "I1JFUVVJUkVECgl4bWw6YmFzZQkldXJpUmVmZXJlbmNlOwkJI0lNUExJRUQKICAgICAgICAl" +
            "bG9jYWwubmV4dENhdGFsb2cuYXR0cmliczsKPgoKPCFFTlRJVFkgJSBsb2NhbC5ncm91cC5t" +
            "aXggIiI+CjwhRU5USVRZICUgbG9jYWwuZ3JvdXAuYXR0cmlicyAiIj4KCjwhRUxFTUVOVCAl" +
            "Z3JvdXA7ICglcHVibGljO3wlc3lzdGVtO3wldXJpOwogICAgICAgICAgICAgICAgICAgfCVy" +
            "ZXdyaXRlU3lzdGVtO3wlcmV3cml0ZVVSSTsKICAgICAgICAgICAgICAgICAgIHwlZGVsZWdh" +
            "dGVQdWJsaWM7fCVkZWxlZ2F0ZVN5c3RlbTt8JWRlbGVnYXRlVVJJOwogICAgICAgICAgICAg" +
            "ICAgICAgfCVuZXh0Q2F0YWxvZzsgJWxvY2FsLmdyb3VwLm1peDspKz4KPCFBVFRMSVNUICVn" +
            "cm91cDsKCWlkCQlJRAkJCSNJTVBMSUVECglwcmVmZXIJCSVzeXN0ZW1PclB1YmxpYzsJI0lN" +
            "UExJRUQKCXhtbDpiYXNlCSV1cmlSZWZlcmVuY2U7CQkjSU1QTElFRAogICAgICAgICVsb2Nh" +
            "bC5ncm91cC5hdHRyaWJzOwo+Cg==";

    private static final String catalog11dtd = "PCEtLSAkSWQ6IGNhdGFsb2cuZHRkLHYgMS4xNCAyMDA1LzA0LzEzIDIwOjQ3OjA2IG5kdyBF" +
            "eHAgJCAtLT4KCjwhRU5USVRZICUgcHViSWRDaGFycyAiQ0RBVEEiPgo8IUVOVElUWSAlIHB1" +
            "YmxpY0lkZW50aWZpZXIgIiVwdWJJZENoYXJzOyI+CjwhRU5USVRZICUgcGFydGlhbFB1Ymxp" +
            "Y0lkZW50aWZpZXIgIiVwdWJJZENoYXJzOyI+CjwhRU5USVRZICUgdXJpUmVmZXJlbmNlICJD" +
            "REFUQSI+CjwhRU5USVRZICUgc3RyaW5nICJDREFUQSI+CjwhRU5USVRZICUgc3lzdGVtT3JQ" +
            "dWJsaWMgIihzeXN0ZW18cHVibGljKSI+Cgo8IUVOVElUWSAlIHAgIiI+CjwhRU5USVRZICUg" +
            "cyAiIj4KPCFFTlRJVFkgJSBuc2RlY2wgInhtbG5zJXM7Ij4KCjwhRU5USVRZICUgY2F0YWxv" +
            "ZyAiJXA7Y2F0YWxvZyI+CjwhRU5USVRZICUgcHVibGljICIlcDtwdWJsaWMiPgo8IUVOVElU" +
            "WSAlIHN5c3RlbSAiJXA7c3lzdGVtIj4KPCFFTlRJVFkgJSB1cmkgIiVwO3VyaSI+CjwhRU5U" +
            "SVRZICUgcmV3cml0ZVN5c3RlbSAiJXA7cmV3cml0ZVN5c3RlbSI+CjwhRU5USVRZICUgcmV3" +
            "cml0ZVVSSSAiJXA7cmV3cml0ZVVSSSI+CjwhRU5USVRZICUgc3lzdGVtU3VmZml4ICIlcDtz" +
            "eXN0ZW1TdWZmaXgiPgo8IUVOVElUWSAlIHVyaVN1ZmZpeCAiJXA7dXJpU3VmZml4Ij4KPCFF" +
            "TlRJVFkgJSBkZWxlZ2F0ZVB1YmxpYyAiJXA7ZGVsZWdhdGVQdWJsaWMiPgo8IUVOVElUWSAl" +
            "IGRlbGVnYXRlU3lzdGVtICIlcDtkZWxlZ2F0ZVN5c3RlbSI+CjwhRU5USVRZICUgZGVsZWdh" +
            "dGVVUkkgIiVwO2RlbGVnYXRlVVJJIj4KPCFFTlRJVFkgJSBuZXh0Q2F0YWxvZyAiJXA7bmV4" +
            "dENhdGFsb2ciPgo8IUVOVElUWSAlIGdyb3VwICIlcDtncm91cCI+Cgo8IUVOVElUWSAlIGxv" +
            "Y2FsLmNhdGFsb2cubWl4ICIiPgo8IUVOVElUWSAlIGxvY2FsLmNhdGFsb2cuYXR0cmlicyAi" +
            "Ij4KCjwhRUxFTUVOVCAlY2F0YWxvZzsgKCVwdWJsaWM7fCVzeXN0ZW07fCV1cmk7CiAgICAg" +
            "ICAgICAgICAgICAgICAgIHwlcmV3cml0ZVN5c3RlbTt8JXJld3JpdGVVUkk7CgkJICAgICB8" +
            "JXN5c3RlbVN1ZmZpeDt8JXVyaVN1ZmZpeDsKICAgICAgICAgICAgICAgICAgICAgfCVkZWxl" +
            "Z2F0ZVB1YmxpYzt8JWRlbGVnYXRlU3lzdGVtO3wlZGVsZWdhdGVVUkk7CiAgICAgICAgICAg" +
            "ICAgICAgICAgIHwlbmV4dENhdGFsb2c7fCVncm91cDsgJWxvY2FsLmNhdGFsb2cubWl4Oykr" +
            "Pgo8IUFUVExJU1QgJWNhdGFsb2c7CgklbnNkZWNsOwkldXJpUmVmZXJlbmNlOwkJI0ZJWEVE" +
            "CgkJJ3VybjpvYXNpczpuYW1lczp0YzplbnRpdHk6eG1sbnM6eG1sOmNhdGFsb2cnCglpZAkJ" +
            "SUQJCQkjSU1QTElFRAoJcHJlZmVyCQklc3lzdGVtT3JQdWJsaWM7CSNJTVBMSUVECgl4bWw6" +
            "YmFzZQkldXJpUmVmZXJlbmNlOwkJI0lNUExJRUQKCSVsb2NhbC5jYXRhbG9nLmF0dHJpYnM7" +
            "Cj4KCjwhRU5USVRZICUgbG9jYWwucHVibGljLmF0dHJpYnMgIiI+Cgo8IUVMRU1FTlQgJXB1" +
            "YmxpYzsgRU1QVFk+CjwhQVRUTElTVCAlcHVibGljOwoJaWQJCUlECQkJI0lNUExJRUQKCXB1" +
            "YmxpY0lkCSVwdWJsaWNJZGVudGlmaWVyOwkjUkVRVUlSRUQKCXVyaQkJJXVyaVJlZmVyZW5j" +
            "ZTsJCSNSRVFVSVJFRAoJeG1sOmJhc2UJJXVyaVJlZmVyZW5jZTsJCSNJTVBMSUVECiAgICAg" +
            "ICAgJWxvY2FsLnB1YmxpYy5hdHRyaWJzOwo+Cgo8IUVOVElUWSAlIGxvY2FsLnN5c3RlbS5h" +
            "dHRyaWJzICIiPgoKPCFFTEVNRU5UICVzeXN0ZW07IEVNUFRZPgo8IUFUVExJU1QgJXN5c3Rl" +
            "bTsKCWlkCQlJRAkJCSNJTVBMSUVECglzeXN0ZW1JZAklc3RyaW5nOwkJI1JFUVVJUkVECgl1" +
            "cmkJCSV1cmlSZWZlcmVuY2U7CQkjUkVRVUlSRUQKCXhtbDpiYXNlCSV1cmlSZWZlcmVuY2U7" +
            "CQkjSU1QTElFRAogICAgICAgICVsb2NhbC5zeXN0ZW0uYXR0cmliczsKPgoKPCFFTlRJVFkg" +
            "JSBsb2NhbC51cmkuYXR0cmlicyAiIj4KCjwhRUxFTUVOVCAldXJpOyBFTVBUWT4KPCFBVFRM" +
            "SVNUICV1cmk7CglpZAkJSUQJCQkjSU1QTElFRAoJbmFtZQkJJXN0cmluZzsJCSNSRVFVSVJF" +
            "RAoJdXJpCQkldXJpUmVmZXJlbmNlOwkJI1JFUVVJUkVECgl4bWw6YmFzZQkldXJpUmVmZXJl" +
            "bmNlOwkJI0lNUExJRUQKICAgICAgICAlbG9jYWwudXJpLmF0dHJpYnM7Cj4KCjwhRU5USVRZ" +
            "ICUgbG9jYWwucmV3cml0ZVN5c3RlbS5hdHRyaWJzICIiPgoKPCFFTEVNRU5UICVyZXdyaXRl" +
            "U3lzdGVtOyBFTVBUWT4KPCFBVFRMSVNUICVyZXdyaXRlU3lzdGVtOwoJaWQJCUlECQkJI0lN" +
            "UExJRUQKCXN5c3RlbUlkU3RhcnRTdHJpbmcJJXN0cmluZzsJI1JFUVVJUkVECglyZXdyaXRl" +
            "UHJlZml4CQklc3RyaW5nOwkJI1JFUVVJUkVECiAgICAgICAgJWxvY2FsLnJld3JpdGVTeXN0" +
            "ZW0uYXR0cmliczsKPgoKPCFFTlRJVFkgJSBsb2NhbC5yZXdyaXRlVVJJLmF0dHJpYnMgIiI+" +
            "Cgo8IUVMRU1FTlQgJXJld3JpdGVVUkk7IEVNUFRZPgo8IUFUVExJU1QgJXJld3JpdGVVUkk7" +
            "CglpZAkJSUQJCQkjSU1QTElFRAoJdXJpU3RhcnRTdHJpbmcJJXN0cmluZzsJCSNSRVFVSVJF" +
            "RAoJcmV3cml0ZVByZWZpeAklc3RyaW5nOwkJI1JFUVVJUkVECiAgICAgICAgJWxvY2FsLnJl" +
            "d3JpdGVVUkkuYXR0cmliczsKPgoKPCFFTlRJVFkgJSBsb2NhbC5zeXN0ZW1TdWZmaXguYXR0" +
            "cmlicyAiIj4KCjwhRUxFTUVOVCAlc3lzdGVtU3VmZml4OyBFTVBUWT4KPCFBVFRMSVNUICVz" +
            "eXN0ZW1TdWZmaXg7CglpZAkJSUQJCQkjSU1QTElFRAoJc3lzdGVtSWRTdWZmaXgJCSVzdHJp" +
            "bmc7CSNSRVFVSVJFRAoJdXJpCQkJJXN0cmluZzsJI1JFUVVJUkVECiAgICAgICAgJWxvY2Fs" +
            "LnN5c3RlbVN1ZmZpeC5hdHRyaWJzOwo+Cgo8IUVOVElUWSAlIGxvY2FsLnVyaVN1ZmZpeC5h" +
            "dHRyaWJzICIiPgoKPCFFTEVNRU5UICV1cmlTdWZmaXg7IEVNUFRZPgo8IUFUVExJU1QgJXVy" +
            "aVN1ZmZpeDsKCWlkCQlJRAkJCSNJTVBMSUVECgl1cmlTdWZmaXgJCSVzdHJpbmc7CSNSRVFV" +
            "SVJFRAoJdXJpCQkJJXN0cmluZzsJI1JFUVVJUkVECiAgICAgICAgJWxvY2FsLnVyaVN1ZmZp" +
            "eC5hdHRyaWJzOwo+Cgo8IUVOVElUWSAlIGxvY2FsLmRlbGVnYXRlUHVibGljLmF0dHJpYnMg" +
            "IiI+Cgo8IUVMRU1FTlQgJWRlbGVnYXRlUHVibGljOyBFTVBUWT4KPCFBVFRMSVNUICVkZWxl" +
            "Z2F0ZVB1YmxpYzsKCWlkCQlJRAkJCSNJTVBMSUVECglwdWJsaWNJZFN0YXJ0U3RyaW5nCSVw" +
            "YXJ0aWFsUHVibGljSWRlbnRpZmllcjsJI1JFUVVJUkVECgljYXRhbG9nCQkldXJpUmVmZXJl" +
            "bmNlOwkJI1JFUVVJUkVECgl4bWw6YmFzZQkldXJpUmVmZXJlbmNlOwkJI0lNUExJRUQKICAg" +
            "ICAgICAlbG9jYWwuZGVsZWdhdGVQdWJsaWMuYXR0cmliczsKPgoKPCFFTlRJVFkgJSBsb2Nh" +
            "bC5kZWxlZ2F0ZVN5c3RlbS5hdHRyaWJzICIiPgoKPCFFTEVNRU5UICVkZWxlZ2F0ZVN5c3Rl" +
            "bTsgRU1QVFk+CjwhQVRUTElTVCAlZGVsZWdhdGVTeXN0ZW07CglpZAkJSUQJCQkjSU1QTElF" +
            "RAoJc3lzdGVtSWRTdGFydFN0cmluZwklc3RyaW5nOwkjUkVRVUlSRUQKCWNhdGFsb2cJCSV1" +
            "cmlSZWZlcmVuY2U7CQkjUkVRVUlSRUQKCXhtbDpiYXNlCSV1cmlSZWZlcmVuY2U7CQkjSU1Q" +
            "TElFRAogICAgICAgICVsb2NhbC5kZWxlZ2F0ZVN5c3RlbS5hdHRyaWJzOwo+Cgo8IUVOVElU" +
            "WSAlIGxvY2FsLmRlbGVnYXRlVVJJLmF0dHJpYnMgIiI+Cgo8IUVMRU1FTlQgJWRlbGVnYXRl" +
            "VVJJOyBFTVBUWT4KPCFBVFRMSVNUICVkZWxlZ2F0ZVVSSTsKCWlkCQlJRAkJCSNJTVBMSUVE" +
            "Cgl1cmlTdGFydFN0cmluZwklc3RyaW5nOwkJI1JFUVVJUkVECgljYXRhbG9nCQkldXJpUmVm" +
            "ZXJlbmNlOwkJI1JFUVVJUkVECgl4bWw6YmFzZQkldXJpUmVmZXJlbmNlOwkJI0lNUExJRUQK" +
            "ICAgICAgICAlbG9jYWwuZGVsZWdhdGVVUkkuYXR0cmliczsKPgoKPCFFTlRJVFkgJSBsb2Nh" +
            "bC5uZXh0Q2F0YWxvZy5hdHRyaWJzICIiPgoKPCFFTEVNRU5UICVuZXh0Q2F0YWxvZzsgRU1Q" +
            "VFk+CjwhQVRUTElTVCAlbmV4dENhdGFsb2c7CglpZAkJSUQJCQkjSU1QTElFRAoJY2F0YWxv" +
            "ZwkJJXVyaVJlZmVyZW5jZTsJCSNSRVFVSVJFRAoJeG1sOmJhc2UJJXVyaVJlZmVyZW5jZTsJ" +
            "CSNJTVBMSUVECiAgICAgICAgJWxvY2FsLm5leHRDYXRhbG9nLmF0dHJpYnM7Cj4KCjwhRU5U" +
            "SVRZICUgbG9jYWwuZ3JvdXAubWl4ICIiPgo8IUVOVElUWSAlIGxvY2FsLmdyb3VwLmF0dHJp" +
            "YnMgIiI+Cgo8IUVMRU1FTlQgJWdyb3VwOyAoJXB1YmxpYzt8JXN5c3RlbTt8JXVyaTsKICAg" +
            "ICAgICAgICAgICAgICAgIHwlcmV3cml0ZVN5c3RlbTt8JXJld3JpdGVVUkk7CgkJICAgfCVz" +
            "eXN0ZW1TdWZmaXg7fCV1cmlTdWZmaXg7CiAgICAgICAgICAgICAgICAgICB8JWRlbGVnYXRl" +
            "UHVibGljO3wlZGVsZWdhdGVTeXN0ZW07fCVkZWxlZ2F0ZVVSSTsKICAgICAgICAgICAgICAg" +
            "ICAgIHwlbmV4dENhdGFsb2c7ICVsb2NhbC5ncm91cC5taXg7KSs+CjwhQVRUTElTVCAlZ3Jv" +
            "dXA7CglpZAkJSUQJCQkjSU1QTElFRAoJcHJlZmVyCQklc3lzdGVtT3JQdWJsaWM7CSNJTVBM" +
            "SUVECgl4bWw6YmFzZQkldXJpUmVmZXJlbmNlOwkJI0lNUExJRUQKICAgICAgICAlbG9jYWwu" +
            "Z3JvdXAuYXR0cmliczsKPgo=";

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        String catalogb64 = null;
        if ("-//OASIS//DTD XML Catalogs V1.1//EN".equals(publicId)
                || "http://www.oasis-open.org/committees/entity/release/1.1/catalog.dtd".equals(systemId)) {
            catalogb64 = catalog10dtd;
        }
        if ("-//OASIS//DTD XML Catalogs V1.0//EN".equals(publicId)
                || "http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd".equals(systemId)) {
            catalogb64 = catalog11dtd;
        }
        if (catalogb64 != null) {
            try {
                InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(catalogb64));
                return new InputSource(inputStream);
            } catch (IllegalArgumentException ex) {
                // nevermind then
            }
        }

        return null;
    }
}
