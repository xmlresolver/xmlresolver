<!-- ....................................................................... -->
<!-- SVG Template Qualified Name Module .................................... -->
<!-- file: svg-template-qname.mod

     This is SVG, a language for describing two-dimensional graphics in XML.
     Copyright 2001, 2002 W3C (MIT, INRIA, Keio), All Rights Reserved.
     Revision: $Id: svg-template-qname.mod,v 1.1 2002/04/20 18:08:11 fujisawa Exp $

     This DTD module is identified by the PUBLIC and SYSTEM identifiers:

        PUBLIC "-//W3C//ENTITIES SVG Template Qualified Name//EN"
        SYSTEM "svg-template-qname.mod"

     ....................................................................... -->

<!-- Template Qualified Name

     This module is contained in two parts, labeled Section 'A' and 'B':

        Section A declares parameter entities to support namespace-
        qualified names, namespace declarations, and name prefixing
        for SVG and extensions.

        Section B declares parameter entities used to provide
        namespace-qualified names for all SVG element types:
-->

<!-- Section A: Template XML Namespace Framework ::::::::::::::::: -->

<!-- 1. Declare a %MODULE.prefixed; conditional section keyword, used
        to activate namespace prefixing. The default value should
        inherit '%NS.prefixed;' from the DTD driver, so that unless
        overridden, the default behaviour follows the overall DTD
        prefixing scheme.
-->
<!ENTITY % NS.prefixed "IGNORE" >
<!ENTITY % MODULE.prefixed "%NS.prefixed;" >

<!-- 2. Declare a parameter entity (eg., %MODULE.xmlns;) containing
        the URI reference used to identify the Template namespace:
-->
<!ENTITY % MODULE.xmlns "http://template-namespace-uri" >

<!-- 3. Declare parameter entities (eg., %MODULE.prefix;) containing
        the default namespace prefix string(s) to use when prefixing
        is enabled. This may be overridden in the DTD driver or the
        internal subset of an document instance. If no default prefix
        is desired, this may be declared as an empty string.
-->
<!ENTITY % MODULE.prefix "" >

<!-- 4. Declare parameter entities (eg., %MODULE.pfx;) containing the
        colonized prefix(es) (eg., '%MODULE.prefix;:') used when
        prefixing is active, an empty string when it is not.
-->
<![%MODULE.prefixed;[
<!ENTITY % MODULE.pfx "%MODULE.prefix;:" >
]]>
<!ENTITY % MODULE.pfx "" >

<!-- 5. The parameter entity %MODULE.xmlns.extra.attrib; may be
        redeclared to contain any non-Template namespace declaration
        attributes for namespaces embedded in SVG. The default
        is an empty string.
-->
<![%MODULE.prefixed;[
<!ENTITY % MODULE.xmlns.extra.attrib
    "xmlns:%MODULE.prefix; %URI.datatype; #FIXED '%MODULE.xmlns;'" >
]]>
<!ENTITY % MODULE.xmlns.extra.attrib "" >

<!ENTITY % SVG.xmlns.extra.attrib
    "%MODULE.xmlns.extra.attrib;"
>

<!-- Section B: SVG Qualified Names :::::::::::::::::::::::::::::: -->

<!-- 6. This section declares parameter entities used to provide
        namespace-qualified names for all Template element types.
-->

<!-- module: svg-template.mod .......................... -->

<!ENTITY % MODULE.element.qname "%MODULE.pfx;element" >

<!-- end of svg-template-qname.mod -->
