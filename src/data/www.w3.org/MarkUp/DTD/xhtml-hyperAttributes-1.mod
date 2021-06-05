<!-- ...................................................................... -->
<!-- XHTML Hypertext Attributes Module  ................................... -->
<!-- file: xhtml-hyperAttributes-1.mod

     This is XHTML-RDFa, modules to annotate XHTML family documents.
     Copyright 2007 W3C (MIT, ERCIM, Keio), All Rights Reserved.
     Revision: $Id: xhtml-hyperAttributes-1.mod,v 1.2 2007/08/11 19:06:51 jigsaw Exp $

     This DTD module is identified by the PUBLIC and SYSTEM identifiers:

       PUBLIC "-//W3C//ENTITIES XHTML HyperAttributes 1.0//EN"
       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-hyperAttributes-1.mod"

     Revisions:
     ....................................................................... -->

<!-- Common Attributes

     This module declares a collection of hypertext related 
     attributes.

     %NS.decl.attrib; is declared in the XHTML Qname module.

	 This file also includes declarations of "global" versions of the 
     attributes.  The global versions of the attributes are for use on 
     elements in other namespaces.  
-->

<!ENTITY % href.attrib
     "href        %URI.datatype;             #IMPLIED"
>

<![%XHTML.global.attrs.prefixed;[
<!ENTITY % XHTML.global.href.attrib
     "%XHTML.prefix;:href           %URI.datatype;        #IMPLIED"
>
]]>

<!ENTITY % hreflang.attrib
     "hreflang        %LanguageCode.datatype;             #IMPLIED"
>

<![%XHTML.global.attrs.prefixed;[
<!ENTITY % XHTML.global.hreflang.attrib
     "%XHTML.prefix;:hreflang           %LanguageCode.datatype;        #IMPLIED"
>
]]>

<!ENTITY % hrefmedia.attrib
     "hrefmedia        %MediaDesc.datatype;             #IMPLIED"
>

<![%XHTML.global.attrs.prefixed;[
<!ENTITY % XHTML.global.hrefmedia.attrib
     "%XHTML.prefix;:hrefmedia           %MediaDesc.datatype;        #IMPLIED"
>
]]>

<!ENTITY % hreftype.attrib
     "hreftype        %ContentTypes.datatype;             #IMPLIED"
>

<![%XHTML.global.attrs.prefixed;[
<!ENTITY % XHTML.global.hreftype.attrib
     "%XHTML.prefix;:hreftype           %ContentTypes.datatype;        #IMPLIED"
>
]]>

<!ENTITY % Hyper.attrib.extra "" >

<!ENTITY % Hyper.attrib
     "%href.attrib;
      %hreflang.attrib;
      %hrefmedia.attrib;
      %hreftype.attrib;
      %Hyper.attrib.extra;"
>

<!ENTITY % XHTML.global.hyper.attrib.extra "" >

<![%XHTML.global.attrs.prefixed;[
<!ENTITY % XHTML.global.hyper.attrib
     "%XHTML.global.href.attrib;
      %XHTML.global.hreflang.attrib;
      %XHTML.global.hrefmedia.attrib;
      %XHTML.global.hreftype.attrib;
      %XHTML.global.hyper.attrib.extra;"
>
]]>

<!ENTITY % XHTML.global.hyper.attrib "" >

<!-- end of xhtml-hyperAttributes-1.mod -->
