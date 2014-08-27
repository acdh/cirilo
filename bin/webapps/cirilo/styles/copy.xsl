<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.tei-c.org/ns/1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">    
    
    <xsl:template match="content">
        <xsl:apply-templates select="*|@*|text()"/>        
    </xsl:template>        

    <xsl:template match="uri"/>
    
    <xsl:template match="*|@*|text()">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>
