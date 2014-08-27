<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
	xmlns:dc="http://purl.org/dc/elements/1.1/">
	<xsl:output method="xml" encoding="utf-8" indent="yes"/>

	<xsl:param name="host"/>
	<xsl:param name="pid"/>

	<xsl:template match="oai_dc:dc">
		<html>
			<head>
				<title><xsl:value-of select="dc:title"/></title>
				<link rel="stylesheet" type="text/css"
					href="http://gams.uni-graz.at/cocoon/css/dc.css"/>
			</head>
			<body>
				<div class="header">
					<img id="gams" alt="gams"
						src="http://gams.uni-graz.at/cocoon/img/gamslogo.png"/>
					<br/>
					<span class="center">Zentrum für Informationsmodellierung in den
						Geisteswissenschaften</span>
					<br/>
					<span>Universität Graz</span>
				</div>

				<div class="content">
					<div class="title" style="position: relative;">
						
						<xsl:if test="not(contains(dc:identifier, 'mws'))">
						<img width="100" height="80" border="0" style="float: left; padding: 0.8em; ">
						<xsl:attribute name="src">
							<xsl:text>http://gams.uni-graz.at/archive/objects/</xsl:text><xsl:value-of select="dc:identifier"/><xsl:text>/datastreams/THUMBNAIL/content</xsl:text>
						</xsl:attribute>
						</img>
						</xsl:if>
			        
					<h1 class="title" style="position: relative; top: 20px;">
						<xsl:apply-templates select="dc:title"/>
					</h1>
						
					</div>
					<table style="clear: both;">
						<xsl:apply-templates
							select="dc:creator | dc:subject | dc:description | dc:publisher | dc:contributor | dc:date | dc:type | dc:format | dc:identifier | dc:source | dc:language | dc:relation | dc:coverage | dc:rights"/>
						<tr>
							<th>
								<xsl:text>Permalink:</xsl:text>
							</th>
							<td>
								<xsl:text>http://gams.uni-graz.at/</xsl:text>
								<xsl:value-of select="dc:identifier"/>
							</td>
						</tr>
					</table>

					<div class="close">
						<a href="javascript:window.close();">
							<span>
								<xsl:text> close </xsl:text>
							</span>
						</a>
					</div>

				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="dc:title">
		<xsl:variable name="title">
			<xsl:value-of select="substring-after(name(),':')"/>
		</xsl:variable>
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template
		match="dc:creator | dc:subject | dc:description | dc:publisher | dc:contributor | dc:date | dc:type | dc:format | dc:identifier | dc:source | dc:language | dc:relation | dc:coverage | dc:rights">
		<xsl:variable name="prec">
			<xsl:value-of select="name(preceding-sibling::*[1]/.)"/>
		</xsl:variable>

		<xsl:variable name="title">
			<xsl:value-of select="substring-after(name(),':')"/>
		</xsl:variable>

		<tr>
			<th>
				<xsl:if test="$prec!=name(.)">
					<xsl:value-of
						select="translate(substring($title, 1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
					<xsl:value-of select="substring($title, 2)"/>: </xsl:if>
			</th>
			<td>
				<xsl:value-of select="."/>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>
