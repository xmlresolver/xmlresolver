<!-- ...................................................................... -->
<!-- XHTML Document Metainformation Module  ............................... -->
<!-- file: xhtml-meta-2.mod

     This is XHTML-RDFa, modules to annotate XHTML family documents.
     Copyright 2007 W3C (MIT, ERCIM, Keio), All Rights Reserved.
     Revision: $Id: xhtml-meta-2.mod,v 1.1 2007/04/02 16:44:01 jigsaw Exp $

     This DTD module is identified by the PUBLIC and SYSTEM identifiers:

       PUBLIC "-//W3C//ELEMENTS XHTML Metainformation 2.0//EN"
       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-meta-2.mod"

     Revisions:
     (none)
     ....................................................................... -->

<!-- Meta Information

        meta
        link

     This module declares the meta and link element types,
     used to provide declarative document metainformation.
-->

<!-- meta: Generic Metainformation ..................... -->

<!ENTITY % meta.element  "INCLUDE" >
<![%meta.element;[
<!ENTITY % meta.content  "( #PCDATA | %Inline.mix; )*" >
<!ENTITY % meta.qname  "meta" >
<!ELEMENT %meta.qname;  %meta.content; >
<!-- end of meta.element -->]]>

<!ENTITY % meta.attlist  "INCLUDE" >
<![%meta.attlist;[
<!ATTLIST %meta.qname;
      %Common.attrib;
      http-equiv   NMTOKEN                  #IMPLIED
      name         NMTOKEN                  #IMPLIED
      scheme       CDATA                    #IMPLIED
>
<!-- end of meta.attlist -->]]>

<!-- link: Media-Independent Link ...................... -->

<!ENTITY % link.element  "INCLUDE" >
<![%link.element;[
<!ENTITY % link.content  "( %link.qname; | %meta.qname; )*" >
<!ENTITY % link.qname  "link" >
<!ELEMENT %link.qname;  %link.content; >
<!-- end of link.element -->]]>

<!ENTITY % link.attlist  "INCLUDE" >
<![%link.attlist;[
<!ATTLIST %link.qname;
      %Common.attrib;
      charset      %Charset.datatype;       #IMPLIED
      type         %ContentType.datatype;   #IMPLIED
      media        %MediaDesc.datatype;     #IMPLIED
>
<!-- end of link.attlist -->]]>

<!-- end of xhtml-meta-2.mod -->
