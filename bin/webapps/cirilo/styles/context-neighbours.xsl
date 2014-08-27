<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="s" xmlns:s="http://www.w3.org/2001/sw/DataAccess/rf1/result" version="1.0">

    <xsl:param name="identifier"/>

    <xsl:template match="s:sparql">

        <neighbours>
            <xsl:if test="string-length($identifier) &gt; 0">
                <xsl:call-template name="neighbour">
                    <xsl:with-param name="name" select="'previous'"/>
                    <xsl:with-param name="node" select="s:results/s:result[s:identifier=$identifier]/preceding-sibling::s:result[1]" />
                </xsl:call-template>
                <xsl:call-template name="neighbour">
                    <xsl:with-param name="name" select="'next'"/>
                    <xsl:with-param name="node" select="s:results/s:result[s:identifier=$identifier]/following-sibling::s:result[1]" />
                </xsl:call-template>
            </xsl:if>    
        </neighbours>

    </xsl:template>

    <xsl:template name="neighbour">

        <xsl:param name="name"/>
        <xsl:param name="node"/>
        <xsl:variable name="pid" select="$node/s:identifier"/>

        <xsl:if test="contains($pid,'o:')">
            <xsl:element name="{$name}">
                <identifier>
                    <xsl:value-of select="$node/s:identifier"/>
                </identifier>
                <title>
                    <xsl:value-of select="$node/s:identifier"/>
                </title>
            </xsl:element>
        </xsl:if>
        
    </xsl:template>

</xsl:stylesheet>
