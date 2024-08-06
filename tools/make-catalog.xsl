<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:f="https://nwalsh.com/ns/functions"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns="urn:oasis:names:tc:entity:xmlns:xml:catalog"
                exclude-result-prefixes="map xs f"
                version="3.0">

<xsl:output method="xml" encoding="utf-8" indent="yes"/>
<xsl:strip-space elements="*"/>

<xsl:param name="generate-tests" select="()"/>

<xsl:template match="/">
  <xsl:variable name="entries" as="element()*">
    <xsl:apply-templates select="//public|//system|//uri"
                         mode="catalog-entry"/>
  </xsl:variable>
  <xsl:variable name="subset" select="()"/>
  <xsl:variable name="prefix" select="()"/>

  <!-- ============================================================ -->

  <xsl:if test="exists($generate-tests)">
    <xsl:result-document href="{$generate-tests}" method="text">
      <xsl:apply-templates select="//public|//system|//uri" mode="generate-tests"/>
    </xsl:result-document>
  </xsl:if>

  <!-- ============================================================ -->

  <xsl:variable name="entries" select="$entries except $subset"/>

  <xsl:variable name="prefix" as="node()*">
    <xsl:sequence select="$prefix"/>
    <delegatePublic publicIdStartString="-//W3C//ENTITIES XHTML"
                    catalog="cat-xhtml.xml"/>
    <delegatePublic publicIdStartString="-//W3C//ELEMENTS XHTML"
                    catalog="cat-xhtml.xml"/>
    <delegatePublic publicIdStartString="-//W3C//DTD XHTML"
                    catalog="cat-xhtml.xml"/>
    <delegateSystem systemIdStartString="https://www.w3.org/MarkUp/DTD/xhtml"
                    catalog="cat-xhtml.xml"/>
    <delegateSystem systemIdStartString="https://www.w3.org/TR/xhtml"
                    catalog="cat-xhtml.xml"/>
  </xsl:variable>

  <xsl:variable name="subset"
                select="$entries[starts-with(@publicId, '-//W3C//ENTITIES XHTML')
                                 or starts-with(@publicId, '-//W3C//ELEMENTS XHTML')
                                 or starts-with(@systemId,
                                       'https://www.w3.org/MarkUp/DTD/xhtml')
                                 or starts-with(@systemId,
                                       'https://www.w3.org/TR/xhtml')
                                 or starts-with(@publicId, '-//W3C//DTD XHTML')
                                ]"/>

  <xsl:result-document href="cat-xhtml.xml">
    <catalog prefer="public">
      <xsl:sequence select="$subset"/>
    </catalog>
  </xsl:result-document>

  <!-- ============================================================ -->

  <xsl:variable name="entries" select="$entries except $subset"/>

  <xsl:variable name="prefix" as="node()*">
    <xsl:sequence select="$prefix"/>
    <delegatePublic publicIdStartString="-//W3C//ENTITIES SVG"
                    catalog="cat-svg.xml"/>
    <delegatePublic publicIdStartString="-//W3C//ELEMENTS SVG"
                    catalog="cat-svg.xml"/>
    <delegatePublic publicIdStartString="-//W3C//DTD SVG"
                    catalog="cat-svg.xml"/>
    <delegateSystem systemIdStartString="https://www.w3.org/Graphics/SVG/1.1/"
                    catalog="cat-svg.xml"/>
  </xsl:variable>

  <xsl:variable name="subset"
                select="$entries[starts-with(@publicId, '-//W3C//ENTITIES SVG')
                                 or starts-with(@publicId, '-//W3C//ELEMENTS SVG ')
                                 or starts-with(@systemId,
                                      'https://www.w3.org/Graphics/SVG/1.1/')
                                 or starts-with(@publicId, '-//W3C//DTD SVG ')
                                ]"/>

  <xsl:result-document href="cat-svg.xml">
    <catalog prefer="public">
      <xsl:sequence select="$subset"/>
    </catalog>
  </xsl:result-document>

  <!-- ============================================================ -->

  <xsl:variable name="entries" select="$entries except $subset"/>

  <!-- These don't have a useful form for delegation, so leave them in misc -->
  <xsl:variable name="mathml-public"
                select="$entries[(starts-with(@publicId, '-//W3C//ENTITIES ')
                                  and contains(@publicId, 'MathML 2.0'))]"/>

  <xsl:variable name="entries" select="$entries except $mathml-public"/>

  <xsl:variable name="prefix" as="node()*">
    <xsl:sequence select="$prefix"/>
    <delegatePublic publicIdStartString="-//W3C//ENTITIES "
                    catalog="cat-entities.xml"/>
    <delegatePublic publicIdStartString="ISO 8879:1986//ENTITIES "
                    catalog="cat-entities.xml"/>
    <delegateSystem systemIdStartString="https://www.w3.org/2003/entities/"
                    catalog="cat-entities.xml"/>
    <delegateSystem systemIdStartString="https://www.w3.org/Math/DTD/mathml1/"
                    catalog="cat-entities.xml"/>
  </xsl:variable>

  <xsl:variable name="subset"
                select="$entries[starts-with(@publicId, '-//W3C//ENTITIES ')
                                 or starts-with(@publicId, 'ISO 8879:1986//ENTITIES ')
                                 or starts-with(@systemId,
                                      'https://www.w3.org/2003/entities/')
                                 or starts-with(@systemId,
                                      'https://www.w3.org/Math/DTD/mathml1/')
                                ]"/>

  <xsl:result-document href="cat-entities.xml">
    <catalog prefer="public">
      <xsl:sequence select="$subset"/>
    </catalog>
  </xsl:result-document>

  <!-- ============================================================ -->

  <xsl:variable name="entries" select="$entries except $subset"/>

  <xsl:variable name="prefix" as="node()*">
    <xsl:sequence select="$prefix"/>
    <delegateSystem systemIdStartString="https://www.w3.org/Math/DTD/mathml2/"
                    catalog="cat-mathml2.xml"/>
  </xsl:variable>

  <xsl:variable name="subset"
                select="$entries[starts-with(@systemId,
                                   'https://www.w3.org/Math/DTD/mathml2/')]"/>
  <xsl:result-document href="cat-mathml2.xml">
    <catalog prefer="public">
      <xsl:sequence select="$subset"/>
    </catalog>
  </xsl:result-document>

  <!-- ============================================================ -->

  <xsl:variable name="entries" select="$entries except $subset"/>

  <xsl:variable name="prefix" as="node()*">
    <xsl:sequence select="$prefix"/>
    <delegateSystem systemIdStartString="https://www.w3.org/Math/DTD/mathml3/"
                    catalog="cat-mathml3.xml"/>
  </xsl:variable>

  <xsl:variable name="subset"
                select="$entries[starts-with(@systemId,
                                   'https://www.w3.org/Math/DTD/mathml3/')]"/>
  <xsl:result-document href="cat-mathml3.xml">
    <catalog prefer="public">
      <xsl:sequence select="$subset"/>
    </catalog>
  </xsl:result-document>

  <!-- ============================================================ -->

  <xsl:variable name="entries" select="$entries except $subset"/>

  <xsl:variable name="prefix" as="node()*">
    <xsl:sequence select="$prefix"/>
    <xsl:text>&#10;</xsl:text>
    <xsl:comment> At present, only the RDDL public identifiers begin with -//XML-DEV// </xsl:comment>
    <delegatePublic publicIdStartString="-//XML-DEV//"
                    catalog="cat-rddl.xml"/>
    <delegateSystem systemIdStartString="http://www.rddl.org/"
                    catalog="cat-rddl.xml"/>
  </xsl:variable>

  <xsl:variable name="subset"
                select="$entries[starts-with(@publicId, '-//XML-DEV//')
                                 or starts-with(@systemId, 'http://www.rddl')]"/>

  <xsl:result-document href="cat-rddl.xml">
    <catalog prefer="public">
      <xsl:sequence select="$subset"/>
    </catalog>
  </xsl:result-document>

  <!-- ============================================================ -->

  <xsl:variable name="entries" select="$entries except $subset"/>

  <xsl:result-document href="catalog.xml">
    <catalog prefer="public">
      <xsl:sequence select="$prefix"/>
      <xsl:sequence select="$entries"/>
      <xsl:comment> The MathML public identifiers below aren’t in one of the  </xsl:comment>
      <xsl:comment> delegated catalogs because the form of the identifier     </xsl:comment>
      <xsl:comment> doesn’t lend itself to delegation. Simplest to just leave </xsl:comment>
      <xsl:comment> them here. </xsl:comment>
      <xsl:sequence select="$mathml-public"/>
    </catalog>
  </xsl:result-document>
</xsl:template>

<xsl:template match="manifest">
  <catalog prefer="public">
    <xsl:apply-templates/>
  </catalog>
</xsl:template>

<xsl:template match="element()">
  <xsl:message>Unexpected element: <xsl:sequence select="node-name(.)"/></xsl:message>
</xsl:template>

<xsl:template match="attribute()|text()|comment()|processing-instruction()">
  <xsl:copy/>
</xsl:template>

<!-- ============================================================ -->

<xsl:template match="public" mode="catalog-entry">
  <public publicId="{.}" uri="{substring-after(../@uri, 'root/')}"/>
</xsl:template>

<xsl:template match="system" mode="catalog-entry">
  <system systemId="{.}" uri="{substring-after(../@uri, 'root/')}"/>
</xsl:template>

<xsl:template match="uri" mode="catalog-entry">
  <uri name="{.}" uri="{substring-after(../@uri, 'root/')}"/>
</xsl:template>

<!-- ============================================================ -->

<xsl:function name="f:generate-id" as="xs:string">
  <xsl:param name="element" as="element()"/>
  <xsl:sequence select="'id_' || count($element/ancestor::entry/preceding-sibling::*)
                              || '_' || count($element/preceding-sibling::*)"/>
</xsl:function>

<xsl:template match="public" mode="generate-tests">
  <xsl:text>    @Test&#10;</xsl:text>
  <xsl:text>    public void gen_lookupPublic</xsl:text>
  <xsl:sequence select="f:generate-id(.)"/>
  <xsl:text>() {&#10;</xsl:text>
  <xsl:text>        URI result = manager.lookupPublic(null, "</xsl:text>
  <xsl:value-of select="."/>
  <xsl:text>");&#10;</xsl:text>
  <xsl:text>        Assertions.assertNotNull(result);&#10;</xsl:text>
  <xsl:text>    }&#10;&#10;</xsl:text>
</xsl:template>

<xsl:template match="system" mode="generate-tests">
  <xsl:text>    @Test&#10;</xsl:text>
  <xsl:text>    public void gen_lookupSystem</xsl:text>
  <xsl:sequence select="f:generate-id(.)"/>
  <xsl:text>() {&#10;</xsl:text>
  <xsl:text>        URI result = manager.lookupSystem("</xsl:text>
  <xsl:value-of select="."/>
  <xsl:text>");&#10;</xsl:text>
  <xsl:text>        Assertions.assertNotNull(result);&#10;</xsl:text>
  <xsl:text>    }&#10;&#10;</xsl:text>
</xsl:template>

<xsl:template match="uri" mode="generate-tests">
  <xsl:text>    @Test&#10;</xsl:text>
  <xsl:text>    public void gen_lookupUri</xsl:text>
  <xsl:sequence select="f:generate-id(.)"/>
  <xsl:text>() {&#10;</xsl:text>
  <xsl:text>        URI result = manager.lookupURI("</xsl:text>
  <xsl:value-of select="."/>
  <xsl:text>");&#10;</xsl:text>
  <xsl:text>        Assertions.assertNotNull(result);&#10;</xsl:text>
  <xsl:text>    }&#10;&#10;</xsl:text>
</xsl:template>

</xsl:stylesheet>
