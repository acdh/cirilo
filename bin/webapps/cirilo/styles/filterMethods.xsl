<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:o="http://www.fedora.info/definitions/1/0/access/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
          
    <xsl:template match="o:objectMethods">        
        <xsl:copy>      
            <xsl:attribute name="pid"><xsl:value-of select="@pid"/></xsl:attribute>            
            <xsl:copy-of select="o:sDef[@pid!='fedora-system:3' and @pid!='sdef:Object']"/>
        </xsl:copy>        
    </xsl:template>    
    
</xsl:stylesheet>