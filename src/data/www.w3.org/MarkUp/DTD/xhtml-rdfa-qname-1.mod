<!-- ....................................................................... -->
<!-- XHTML RDFa Qname Module  ............................................ -->
<!-- file: xhtml-rdfa-qname-1.mod

     This is XHTML RDFa - the RDFa Attribute Module for XHTML.

     Copyright 2007 W3C (MIT, ERCIM, Keio), All Rights Reserved.

     This DTD module is identified by the PUBLIC and SYSTEM identifiers:

       PUBLIC "-//W3C//ENTITIES XHTML RDFa Attribute Qnames 1.0//EN"
       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-rdfa-qname-1.mod"

     Revisions:
     (none)
     ....................................................................... -->

<!-- XHTML RDFa Attribute Qname (Qualified Name) Module

     This module is contained in two parts, labeled Section 'A' and 'B':

       Section A declares parameter entities to support namespace-
       qualified names, namespace declarations, and name prefixing
       for XHTML RDFa and extensions.

       Section B declares parameter entities used to provide
       namespace-qualified names for the XHTML RDFa elements
       and attributes:

         %link.qname;   the xmlns-qualified name for link
         ...

     XHTML RDFa extensions would create a module similar to this one.
-->

<!-- Section A: XHTML RDFa Attribute XML Namespace Framework ::::::::::::::: -->

<!-- 1. Declare a %XHTML-RDFA.prefixed; conditional section keyword, used
        to activate namespace prefixing. The default value should
        inherit '%NS.prefixed;' from the DTD driver, so that unless
        overridden, the default behavior follows the overall DTD
        prefixing scheme.
-->
<!ENTITY % NS.prefixed "IGNORE" >
<!ENTITY % XHTML-RDFA.prefixed "%NS.prefixed;" >

<!-- 2. Declare a parameter entity (eg., %XHTML-RDFA.xmlns;) containing
        the URI reference used to identify the XHTML RDFa Attribute namespace
-->
<!ENTITY % XHTML-RDFA.xmlns  "http://www.w3.org/1999/xhtml" >

<!-- 3. Declare parameter entities (eg., %XML.prefix;) containing
        the default namespace prefix string(s) to use when prefixing
        is enabled. This may be overridden in the DTD driver or the
        internal subset of an document instance. If no default prefix
        is desired, this may be declared as an empty string.

     NOTE: As specified in [XMLNAMES], the namespace prefix serves
     as a proxy for the URI reference, and is not in itself significant.
-->
<!ENTITY % XHTML-RDFA.prefix  "" >

<!-- 4. Declare parameter entities (eg., %XHTML-RDFA.pfx;) containing the
        colonized prefix(es) (eg., '%XHTML-RDFA.prefix;:') used when
        prefixing is active, an empty string when it is not.
-->
<![%XHTML-RDFA.prefixed;[
<!ENTITY % XHTML-RDFA.pfx  "%XHTML-RDFA.prefix;:" >
]]>
<![%XHTML.prefixed;[
<!ENTITY % XHTML-RDFA.pfx  "%XHTML.prefix;:" >
]]>
<!ENTITY % XHTML-RDFA.pfx  "" >

<!-- declare qualified name extensions here ............ -->
<!ENTITY % xhtml-rdfa-qname-extra.mod "" >
%xhtml-rdfa-qname-extra.mod;

<!-- 5. The parameter entity %XHTML-RDFA.xmlns.extra.attrib; may be
        redeclared to contain any non-XHTML RDFa Attribute namespace 
        declaration attributes for namespaces embedded in XML. The default
        is an empty string.  XLink should be included here if used
        in the DTD.
-->
<!ENTITY % XHTML-RDFA.xmlns.extra.attrib "" >


<!-- Section B: XML Qualified Names ::::::::::::::::::::::::::::: -->

<!-- 6. This section declares parameter entities used to provide
        namespace-qualified names for the XHTML RDFa attribute modules.

        Note that these names are NOT prefixed to be compatible with 
        XHTML Modularization 1.1
-->

<!-- end of xhtml-rdfa-qname-1.mod -->
