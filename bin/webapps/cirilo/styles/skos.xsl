<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:s="http://www.w3.org/2005/sparql-results#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:skos="http://www.w3.org/2004/02/skos/core#" 
    xmlns:owl ="http://www.w3.org/2002/07/owl#" 
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    version="1.0">

    <xsl:param name="mode"/>

    <xsl:variable name="skos" select="'http://www.w3.org/2004/02/skos/core'"/>
    <xsl:variable name="owl" select="'http://www.w3.org/2002/07/owl'"/>
    <xsl:variable name="dc" select="'http://purl.org/dc/elements/1.1/'"/>
    <xsl:variable name="type" select="'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'"/>

    <xsl:template match="content">

        <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            xmlns:skos="http://www.w3.org/2004/02/skos/core#">
            <xsl:choose>
                <xsl:when test="$mode='getConceptByURI'">
                    <xsl:variable name="concept" select="substring-after(//s:binding[@name='predicate' and contains(s:uri,$type)]/parent::s:result/s:binding[@name='object']/s:uri,'#')"/>
                    <xsl:if test="string-length($concept) &gt; 0">
                        <xsl:element name="{concat('skos:',$concept)}">
                            <xsl:attribute name="rdf:about">
                                <xsl:value-of select="uri"/>
                            </xsl:attribute>
                            <xsl:apply-templates select="//s:result[contains(s:binding[@name='predicate']/s:uri,$skos)]"><xsl:with-param name="ns" select="'skos'"></xsl:with-param></xsl:apply-templates>                            
                            <xsl:apply-templates select="//s:result[contains(s:binding[@name='predicate']/s:uri,$owl)]"><xsl:with-param name="ns" select="'owl'"></xsl:with-param></xsl:apply-templates>                            
                            <xsl:apply-templates select="//s:result[contains(s:binding[@name='predicate']/s:uri,$dc)]"><xsl:with-param name="ns" select="'dc'"></xsl:with-param></xsl:apply-templates>                            
                        </xsl:element>
                    </xsl:if>
                </xsl:when>
                <xsl:when test="$mode='getConceptByPrefLabel' or $mode='getConceptByExternalID'">
                    <xsl:variable name="concept" select="substring-after(//s:binding[@name='predicate' and contains(s:uri,$type)]/parent::s:result/s:binding[@name='object']/s:uri,'#')"/>
                    <xsl:if test="string-length($concept) &gt; 0">
                        <xsl:element name="{concat('skos:',$concept)}">
                        <xsl:attribute name="rdf:about">
                            <xsl:value-of select="//s:result[1]/s:binding[@name='subject']/s:uri"/>
                        </xsl:attribute>
                            <xsl:apply-templates  select="//s:result[contains(s:binding[@name='predicate']/s:uri,$skos)]"><xsl:with-param name="ns" select="'skos'"/></xsl:apply-templates>
                            <xsl:apply-templates  select="//s:result[contains(s:binding[@name='predicate']/s:uri,$owl)]"><xsl:with-param name="ns" select="'owl'"/></xsl:apply-templates>
                            <xsl:apply-templates  select="//s:result[contains(s:binding[@name='predicate']/s:uri,$dc)]"><xsl:with-param name="ns" select="'dc'"/></xsl:apply-templates>
                        </xsl:element>
                    </xsl:if>
                </xsl:when>
                <xsl:when test="$mode='getConceptRelatives'">
                    <xsl:for-each
                        select="//s:result[contains(s:binding[@name='predicate']/s:uri,'type')]/s:binding[@name='subject']/s:uri">
                        <xsl:variable name="uri" select="."/>
                        <xsl:variable name="concept" select="substring-after(//s:binding[@name='predicate' and contains(s:uri,$type) and contains(parent::s:result/s:binding[@name='subject']/s:uri,$uri)]/parent::s:result/s:binding[@name='object']/s:uri,'#')"/>
                        <xsl:if test="string-length($concept) &gt; 0">
                            <xsl:element name="{concat('skos:',$concept)}">
                            <xsl:attribute name="rdf:about">
                                <xsl:value-of select="$uri"/>
                            </xsl:attribute>
                                <xsl:apply-templates select="//s:result[contains(s:binding[@name='subject']/s:uri,$uri) and contains(s:binding[@name='predicate'],$skos)]"><xsl:with-param name="ns" select="'skos'"/></xsl:apply-templates>
                        </xsl:element>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:when>
            </xsl:choose>
        </rdf:RDF>
    </xsl:template>

    <xsl:template match="s:result">
        <xsl:param name="ns"/>
        <xsl:variable name="tag">
            <xsl:choose>
                <xsl:when test="$ns = 'dc'"><xsl:value-of select="substring-after(s:binding[@name='predicate']/s:uri,'1.1/')"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="substring-after(s:binding[@name='predicate']/s:uri,'#')"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:if test="$tag!='type'">
            <xsl:element name="{concat($ns,':',$tag)}">
                <xsl:if test="s:binding[@name='object']/s:literal/@xml:lang">
                    <xsl:attribute name="xml:lang">
                        <xsl:value-of select="s:binding[@name='object']/s:literal/@xml:lang"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="s:binding[@name='object']/s:literal">
                        <xsl:value-of select="s:binding[@name='object']/s:literal"/>
                    </xsl:when>
                    <xsl:when test="s:binding[@name='object']/s:uri">
                        <xsl:attribute name="rdf:resource">
                            <xsl:value-of select="s:binding[@name='object']/s:uri"/>
                        </xsl:attribute>
                    </xsl:when>
                </xsl:choose>
            </xsl:element>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
