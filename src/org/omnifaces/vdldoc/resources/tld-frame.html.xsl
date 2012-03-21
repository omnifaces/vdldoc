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
 - Creates the TLD frame (lower-left hand corner), listing the tags and functions that are in this particular tag
 - library.
 -
 - @author Bauke Scholtz
-->
<xsl:stylesheet
	xmlns:javaee="http://java.sun.com/xml/ns/javaee"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	version="2.0"
>
	<xsl:output method="html" indent="yes"
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html4/loose.dtd" />

	<xsl:param name="id">
		default
	</xsl:param>

	<xsl:template match="/">
		<xsl:apply-templates select="javaee:vdldoc/javaee:facelet-taglib" />
	</xsl:template>

	<xsl:template match="javaee:facelet-taglib">
		<xsl:if test="@id = $id">
			<xsl:variable name="title">
				<xsl:value-of select="$id" />
				<xsl:if test="normalize-space(javaee:description)">
					(<xsl:value-of select="javaee:description" disable-output-escaping="yes" />)
				</xsl:if>
			</xsl:variable>

			<html lang="en">
				<head>
					<title>
						<xsl:value-of select="$title" />
					</title>
					<meta name="keywords" content="$title" />
					<link rel="stylesheet" type="text/css" href="../stylesheet.css" title="Style" />
				</head>
				<body>
					<h1 class="bar">
						<a href="tld-summary.html" target="tagFrame">
							<xsl:value-of select="$id" />
						</a>
					</h1>
					<div class="indexContainer">
						<xsl:if test="count(javaee:tag) > 0">
							<h2 title="Tags">Tags</h2>
							<ul title="Tags">
								<xsl:apply-templates select="javaee:tag" />
							</ul>
						</xsl:if>
						<xsl:if test="count(javaee:function) > 0">
							<h2 title="Functions">Functions</h2>
							<ul title="Functions">
								<xsl:apply-templates select="javaee:function" />
							</ul>
						</xsl:if>
					</div>
				</body>
			</html>
		</xsl:if>
	</xsl:template>

	<xsl:template match="javaee:tag">
		<li>
			<a>
				<xsl:attribute name="href"><xsl:value-of select="javaee:tag-name" />.html</xsl:attribute>
				<xsl:attribute name="target">tagFrame</xsl:attribute>
				<xsl:value-of select="../@id" />:<xsl:value-of select="javaee:tag-name" />
			</a>
		</li>
	</xsl:template>

	<xsl:template match="javaee:function">
		<li>
			<a>
				<xsl:attribute name="href"><xsl:value-of select="javaee:function-name" />.fn.html</xsl:attribute>
				<xsl:attribute name="target">tagFrame</xsl:attribute>
				<i><xsl:value-of select="../@id" />:<xsl:value-of select="javaee:function-name" />()</i>
			</a>
		</li>
	</xsl:template>
</xsl:stylesheet>