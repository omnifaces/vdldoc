<?xml version="1.0" encoding="UTF-8" ?>

<!--
 - Copyright (c) 2012, OmniFaces
 - All rights reserved.
 -
 - Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 - following conditions are met:
 -
 -     * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 -       following disclaimer.
 -     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 -       following disclaimer in the documentation and/or other materials provided with the distribution.
 -     * Neither the name of OmniFaces nor the names of its contributors may be used to endorse or promote products
 -       derived from this software without specific prior written permission.
 -
 - THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 - INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 - DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 - OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 - DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 - STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 - EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-->

<!--
 - Creates the overview frame (upper left corner), listing all tag libraries included in this generation.
 -
 - @author Bauke Scholtz
-->
<xsl:stylesheet
	xmlns:javaee="http://xmlns.jcp.org/xml/ns/javaee"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	version="2.0"
>
	<xsl:output method="html" indent="yes"
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html4/loose.dtd" />

	<xsl:template match="/">
		<html lang="en">
			<head>
				<title>
					Overview (<xsl:value-of select="/javaee:vdldoc/javaee:config/javaee:window-title" />)
				</title>
				<link rel="stylesheet" type="text/css" title="Style">
					<xsl:attribute name="href"><xsl:value-of select="javaee:vdldoc/javaee:config/@css-location" /></xsl:attribute>
				</link>
			</head>
			<body>
				<div class="indexHeader">
					<a href="alltags-frame.html" target="tldFrame">All Tags / Functions</a>
				</div>
				<div class="indexContainer">
					<h2 title="Tag Libraries">Tag Libraries</h2>
					<ul title="Tag Libraries">
						<xsl:apply-templates select="javaee:vdldoc/javaee:facelet-taglib" />
					</ul>
				</div>
				<p>&#160;</p>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="javaee:facelet-taglib">
		<li>
			<a>
				<xsl:attribute name="href"><xsl:value-of select="@id" />/tld-frame.html</xsl:attribute>
				<xsl:attribute name="target">tldFrame</xsl:attribute>
				<xsl:value-of select="@id" />
			</a>
		</li>
	</xsl:template>
</xsl:stylesheet>