<!-- ...................................................................... -->
<!-- XHTML Access Module .................................................. -->
<!-- file: xhtml-access-1.mod

     This is XHTML Access - the Access Module for XHTML.

     Copyright 2007-2008 W3C (MIT, ERCIM, Keio), All Rights Reserved.

     This DTD module is identified by the PUBLIC and SYSTEM identifiers:

       PUBLIC "-//W3C//ELEMENTS XHTML Access Element 1.0//EN"
       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-access-1.mod"

     Revisions:
     (none)
     ....................................................................... -->


<!ENTITY % Character.datatype "CDATA" >
<!ENTITY % CURIEs.datatype "CDATA" >
<!ENTITY % IDREFs.datatype "CDATA" > 

<!ENTITY % access.element  "INCLUDE" >
<![%access.element;[
<!ENTITY % access.content  "EMPTY" >
<!ENTITY % XHTML-ACCESS.access.qname  "access" >
<!ELEMENT %XHTML-ACCESS.access.qname;  %access.content; >
<!-- end of access.element -->]]>

<!ENTITY % access.attlist  "INCLUDE" >
<![%access.attlist;[
<!ATTLIST %access.qname;
      %Common.attrib;
      activate     ( yes | no )             #IMPLIED
      key          %Character.datatype;     #IMPLIED
      targetid     %IDREFs.datatype;        #IMPLIED
      targetrole   %CURIEs.datatype;        #IMPLIED
>
<!-- end of access.attlist -->]]>

<!-- end of xhtml-access-1.mod -->
