<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:s="http://xmlresolver.com/ns/sample"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="s xlink xs"
                version="3.0">

<xsl:output method="html" version="5" encoding="utf-8" indent="yes"/>

<xsl:strip-space elements="s:book s:chapter s:article"/>

<xsl:template match="/">
  <html>
    <head>
      <title>
        <xsl:choose>
          <xsl:when test="*/s:title">
            <xsl:apply-templates select="*/s:title/node()"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>Untitled</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </title>
    </head>
    <body>
      <xsl:apply-templates/>
    </body>
  </html>
</xsl:template>

<xsl:template match="s:book|s:chapter|s:article">
  <div class="{local-name(.)}">
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="s:title">
  <h2>
    <xsl:apply-templates/>
  </h2>
</xsl:template>

<xsl:template match="s:book/s:title|/s:article/s:title">
  <h1>
    <xsl:apply-templates/>
  </h1>
</xsl:template>

<xsl:template match="s:link">
  <a href="{@xlink:href}">
    <xsl:apply-templates/>
  </a>
  <xsl:if test="starts-with(@xlink:href, 'http://')">
    <xsl:message select="'Warning: http: link: ' || @xlink:href"/>
  </xsl:if>
</xsl:template>

<xsl:template match="element()">
  <xsl:element name="{local-name(.)}">
    <xsl:apply-templates select="@*,node()"/>
  </xsl:element>
</xsl:template>

<xsl:template match="attribute()|text()|comment()|processing-instruction()">
  <xsl:copy/>
</xsl:template>

</xsl:stylesheet>
