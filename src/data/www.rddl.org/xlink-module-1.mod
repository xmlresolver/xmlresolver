<!--
	Copyright (c) 2000 The Open Healthcare Group
	
	The content of ASTM Healthcare documents may include XLinks
	PUBLIC +//ASTM//ENTITIES Xlink 1.0//EN"
	SYSTEM "http://www.openhealth.org/ASTM/astm-xlink.mod"
-->
<!ENTITY % xlink.namespace.uri "'http://www.w3.org/1999/xlink'">



<!ENTITY % NS.prefixed "IGNORE">
<!ENTITY % XLINK.prefixed "INCLUDE">
<!ENTITY % XLINK.prefix "">
<!ENTITY % XLINK.xmlns "http://www.w3.org/1999/xlink">

<![ %XLINK.prefixed; [
	<!ENTITY % XLINK.pfx "%XLINK.prefix;:">
]]>
	<!ENTITY % XLINK.pfx "">
	<!ENTITY % xlink.namespace.attrib "xmlns:%XLINK.prefix; CDATA #FIXED 'http://www.w3.org/1999/xlink'">


<!ENTITY % xlink.attrib "
	%XLINK.pfx;type (simple|extended|locator|arc|resource|title) #IMPLIED
	%XLINK.pfx;arcrole %URI.datatype; #IMPLIED
	%XLINK.pfx;href %URI.datatype; #IMPLIED
	%XLINK.pfx;role %URI.datatype; #IMPLIED
	%XLINK.pfx;title CDATA #IMPLIED
	%XLINK.pfx;show CDATA #IMPLIED
	%XLINK.pfx;embed CDATA #IMPLIED
">
<!ENTITY % xlink.simple.attrib '
	%XLINK.pfx;type (simple|arc|locator|resource) #FIXED "simple"
	%XLINK.pfx;arcrole %URI.datatype; #IMPLIED
	%XLINK.pfx;href %URI.datatype; #IMPLIED
	%XLINK.pfx;role %URI.datatype; "%RDDL.xmlns;#resource"
	%XLINK.pfx;title CDATA #IMPLIED
	%XLINK.pfx;show (none) #FIXED "none"
	%XLINK.pfx;embed (none) #FIXED "none"
	%XLINK.pfx;label CDATA #IMPLIED
	%XLINK.pfx;locator CDATA #IMPLIED
	%XLINK.pfx;resource %URI.datatype; #IMPLIED
'>
<!ENTITY % xlink.extended.attrib '
	%XLINK.pfx;from CDATA #IMPLIED
	%XLINK.pfx;to CDATA #IMPLIED
	%XLINK.pfx;label CDATA #IMPLIED
'>

