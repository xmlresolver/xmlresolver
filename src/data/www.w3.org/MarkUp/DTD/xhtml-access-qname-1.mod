<!-- ....................................................................... -->
<!-- XHTML Access Qname Module  ............................................ -->
<!-- file: xhtml-access-qname-1.mod

     This is XHTML Access - the Access Attribute Module for XHTML.

     Copyright 2007-2008 W3C (MIT, ERCIM, Keio), All Rights Reserved.

     This DTD module is identified by the PUBLIC and SYSTEM identifiers:

       PUBLIC "-//W3C//ENTITIES XHTML Access Attribute Qnames 1.0//EN"
       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-access-qname-1.mod"

     Revisions:
     (none)
     ....................................................................... -->

<!-- XHTML Access Attribute Qname (Qualified Name) Module

     This module is contained in two parts, labeled Section 'A' and 'B':

       Section A declares parameter entities to support namespace-
       qualified names, namespace declarations, and name prefixing
       for XHTML Access and extensions.

       Section B declares parameter entities used to provide
       namespace-qualified names for the XHTML access element:

         %XHTML-ACCESS.access.qname;   the xmlns-qualified name for access
         ...

     XHTML Access extensions would create a module similar to this one.
-->

<!-- Section A: XHTML Access Attribute XML Namespace Framework ::::::::::::::: -->

<!-- 1. Declare a %XHTML-ACCESS.prefixed; conditional section keyword, used
        to activate namespace prefixing. The default value should
        inherit '%NS.prefixed;' from the DTD driver, so that unless
        overridden, the default behavior follows the overall DTD
        prefixing scheme.
-->
<!ENTITY % NS.prefixed "IGNORE" >
<!ENTITY % XHTML-ACCESS.prefixed "%NS.prefixed;" >

<!-- 2. Declare a parameter entity (eg., %XHTML-ACCESS.xmlns;) containing
        the URI reference used to identify the XHTML Access Attribute namespace
-->
<!ENTITY % XHTML-ACCESS.xmlns  "http://www.w3.org/1999/xhtml" >

<!-- 3. Declare parameter entities (eg., %XML.prefix;) containing
        the default namespace prefix string(s) to use when prefixing
        is enabled. This may be overridden in the DTD driver or the
        internal subset of an document instance. If no default prefix
        is desired, this may be declared as an empty string.

     NOTE: As specified in [XMLNAMES], the namespace prefix serves
     as a proxy for the URI reference, and is not in itself significant.
-->
<!ENTITY % XHTML-ACCESS.prefix  "" >

<!-- 4. Declare parameter entities (eg., %XHTML-ACCESS.pfx;) containing the
        colonized prefix(es) (eg., '%XHTML-ACCESS.prefix;:') used when
        prefixing is active, an empty string when it is not.
-->
<![%XHTML-ACCESS.prefixed;[
<!ENTITY % XHTML-ACCESS.pfx  "%XHTML-ACCESS.prefix;:" >
]]>
<!ENTITY % XHTML-ACCESS.pfx  "" >

<!-- declare qualified name extensions here ............ -->
<!ENTITY % xhtml-access-qname-extra.mod "" >
%xhtml-access-qname-extra.mod;

<!-- 5. The parameter entity %XHTML-ACCESS.xmlns.extra.attrib; may be
        redeclared to contain any non-XHTML Access namespace 
        declaration attributes for namespaces embedded in XML. The default
        is an empty string.  XLink should be included here if used
        in the DTD.
-->
<!ENTITY % XHTML-ACCESS.xmlns.extra.attrib "" >


<!-- Section B: XML Qualified Names ::::::::::::::::::::::::::::: -->

<!-- 6. This section declares parameter entities used to provide
        namespace-qualified names for the XHTML Access element.
-->

<!ENTITY % XHTML-ACCESS.access.qname  "%XHTML-ACCESS.pfx;access" >

<!-- end of xhtml-access-qname-1.mod -->
