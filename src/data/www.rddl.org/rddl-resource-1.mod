<!--
	PUBLIC "-//XML-DEV//ENTITIES RDDL Resource Module 1.0//EN"
	SYSTEM "http://www.rddl.org/rddl-resource-1.mod"
-->
<!-- resource: Resource Element ................................ -->

<!ENTITY % RDDL.resource.element  "INCLUDE" >
<![ %RDDL.resource.element; [
<!ENTITY % RDDL.resource.content
     "( #PCDATA | %Flow.mix;)*"
>
<!ENTITY % RDDL.extra.attrib "
	xml:base CDATA #IMPLIED
">

<!ELEMENT %RDDL.resource.qname;  %RDDL.resource.content; >
<!-- end of resource.element -->]]>

<!ENTITY % RDDL.resource.attlist  "INCLUDE" >
<![%RDDL.resource.attlist;[
<!ATTLIST %RDDL.resource.qname;
    %id.attrib;
	%I18n.attrib;
	%RDDL.xmlns.attrib;
	%RDDL.extra.attrib;
	%xlink.simple.attrib;
	%xlink.namespace.attrib;
>
<!ATTLIST %html.qname;
	%RDDL.extra.attrib;
	%xlink.namespace.attrib;
	%RDDL.xmlns.attrib.prefixed;
>
<!ATTLIST %div.qname;
	%RDDL.extra.attrib;
>
<!-- end of resource.attlist -->]]>


