<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:d='https://saxonica.com/ns/doclet'
                exclude-result-prefixes="#all"
                expand-text="yes"
                version="3.0">

<xsl:strip-space elements="*"/>

<xsl:output method="xml" encoding="utf-8" indent="yes"/>

<xsl:mode on-no-match="shallow-copy"/>

<xsl:template match="d:doclet">
  <doc>
    <xsl:apply-templates select="d:package">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
  </doc>
</xsl:template>

<xsl:template match="d:package">
  <package>
    <name>{@name/string()}</name>
    <xsl:apply-templates select="* except (d:purpose|d:description)">
      <xsl:sort select="@fullname"/>
    </xsl:apply-templates>
  </package>
</xsl:template>

<xsl:template match="d:classref">
  <xsl:variable name="fullname" select="@fullname/string()"/>
  <xsl:variable name="class"
                select="/d:doclet/d:class[@fullname = $fullname]"/>
  <xsl:assert test="count($class) = 1"/>
  <xsl:apply-templates select="$class"/>
</xsl:template>

<xsl:template match="d:interfaceref">
  <xsl:variable name="fullname" select="@fullname/string()"/>
  <xsl:variable name="iface"
                select="/d:doclet/d:interface[@fullname = $fullname]"/>
  <xsl:assert test="count($iface) = 1"/>
  <xsl:apply-templates select="$iface"/>
</xsl:template>

<xsl:template match="d:class|d:interface">
  <xsl:element name="{local-name(.)}">
    <name>{@fullname/string()}</name>
    <xsl:apply-templates select="d:field[@access='public']">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="d:constructor[@access='public']">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="d:method[@access='public']">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
  </xsl:element>
</xsl:template>

<xsl:template match="d:field">
  <field name="{@name}" type="{d:type/@fullname}"/>
</xsl:template>

<xsl:template match="d:constructor|d:method">
  <xsl:element name="{local-name(.)}">
    <xsl:copy-of select="@name"/>
    <xsl:if test="not(self::d:constructor)">
      <return>
        <xsl:sequence select="(d:return/d:type/@fullname,d:return/d:type/@name)[1]/string()"/>
      </return>
    </xsl:if>
    <xsl:apply-templates select="d:parameter"/>
  </xsl:element>
</xsl:template>

<xsl:template match="d:parameter">
  <parameter name="{@name}" type="{d:type/@fullname}"/>
</xsl:template>

<xsl:template match="*">
  <xsl:message select="'No template for:', local-name(.)"/>
</xsl:template>

</xsl:stylesheet>
