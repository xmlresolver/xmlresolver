<!-- file: rddl-qname-1.mod -->
<!-- 
	Copyright (c) 2000-2001 Jonathan Borden
	PUBLIC "-//XML-DEV//ENTITIES RDDL QName Module 1.0//EN"
	SYSTEM "http://www.rddl.org/rddl-qname-1.mod"
-->

<!-- Bring in the datatypes -->
<!ENTITY % RDDL-datatypes.mod
         PUBLIC "-//W3C//ENTITIES XHTML Datatypes 1.0//EN"
		 "xhtml-datatypes-1.mod" >
%RDDL-datatypes.mod;

<!-- Declare the actual namespace of this module -->
<!ENTITY % RDDL.xmlns "http://www.rddl.org/" >

<!-- Declare the default prefix for this module -->
<!ENTITY % RDDL.prefix "rddl" >

<!-- By default, disable prefixing of this module 
-->
<!ENTITY % NS.prefixed "IGNORE" >
<!--
	but note: driver 'defaults' this to INCLUDE 
-->
<!ENTITY % RDDL.prefixed "%NS.prefixed;" >

<!-- If this module's namespace is prefixed -->
<![%RDDL.prefixed;[
  <!ENTITY % RDDL.pfx  "%RDDL.prefix;:" >
]]>
<!ENTITY % RDDL.pfx  "" >

<!-- Declare a Parameter Entity (PE) that defines any external namespaces 
     that are used by this module -->
<!ENTITY % RDDL.xmlns.extra.attrib "" >

<!-- Declare a PE that defines the xmlns attributes for use by by RDDL. -->
<![%RDDL.prefixed;[
<!ENTITY % RDDL.xmlns.attrib
   "xmlns:%RDDL.prefix;  %URI.datatype;  #FIXED '%RDDL.xmlns;'
   %RDDL.xmlns.extra.attrib;"
>
]]>
<!ENTITY % RDDL.xmlns.attrib
   "xmlns	%URI.datatype;	#FIXED '%RDDL.xmlns;'
   	%RDDL.xmlns.extra.attrib;"
>
<!ENTITY % RDDL.xmlns.attrib.prefixed
   "xmlns:%RDDL.prefix;  %URI.datatype;  #FIXED '%RDDL.xmlns;'
   "
>

<!-- Make sure that the RDDL namespace attributes are included on the XHTML
     attribute set -->
<![%NS.prefixed;[
<!ENTITY % XHTML.xmlns.extra.attrib
	"%RDDL.xmlns.attrib;
	 %XLINK.xmlns.attrib;"
 >
]]>
<!ENTITY % XHTML.xmlns.extra.attrib
	"%XLINK.xmlns.attrib;"
>
<!ENTITY % RDDL.resource.qname "%RDDL.pfx;resource">
<!ENTITY % RDDL.bundle.qname "%RDDL.pfx;bundle">

