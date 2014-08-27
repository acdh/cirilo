<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns="http://www.w3.org/2001/sw/DataAccess/rf1/result"
    xmlns:m="http://www.w3.org/2005/sparql-results#" 
    xmlns:q="http://www.openrdf.org/schema/qname#"
    exclude-result-prefixes="xs m q" version="1.0">

    <xsl:template match="m:sparql">
        <sparql>
            <head>
                <xsl:apply-templates select="m:head"/>
            </head>
            <results>
                <xsl:apply-templates select="m:results"/>
            </results>
        </sparql>
    </xsl:template>

    <xsl:template match="m:head">
        <xsl:for-each select="m:variable">
            <variable name="{@name}"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="m:results">
        <xsl:for-each select="m:result">
            <result>
                <xsl:for-each select="m:binding">
                    <xsl:element name="{@name}">
                        <xsl:choose>
                            <xsl:when test="m:uri">
                                <xsl:attribute name="uri">
                                    <xsl:value-of select="m:uri"/>
                                </xsl:attribute>
                            </xsl:when>
                            <xsl:when test="m:literal">
                                <xsl:value-of select="m:literal"/>
                            </xsl:when>    
                        </xsl:choose>
                    </xsl:element>
                </xsl:for-each>
            </result>
        </xsl:for-each>
    </xsl:template>


</xsl:stylesheet>
