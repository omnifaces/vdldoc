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
 - Creates the all tags page, listing all tags and functions included in all tag libraries for this generation.
 -
 - @author Bauke Scholtz
-->
<xsl:stylesheet
	xmlns:javaee="http://xmlns.jcp.org/xml/ns/javaee"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:vdldoc="http://vdldoc.omnifaces.org"
	version="2.0"
>
	<xsl:output method="html" indent="yes"
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html4/loose.dtd" />

	<xsl:template match="/">
		<html lang="en">
			<head>
				<title>All Tags / Functions</title>
				<link rel="stylesheet" type="text/css" href="stylesheet.css" title="Style" />
			</head>
			<body>
				<h1 class="bar">All Tags / Functions</h1>
				<div class="indexContainer">
					<ul>
						<xsl:apply-templates select="javaee:vdldoc/javaee:facelet-taglib/javaee:tag|javaee:vdldoc/javaee:facelet-taglib/javaee:function|javaee:vdldoc/javaee:facelet-taglib/javaee:taglib-extension/vdldoc:el-variable">
							<xsl:sort select="../@id" />
							<xsl:sort select="javaee:tag-name" />
							<xsl:sort select="javaee:function-name" />
							<xsl:sort select="vdldoc:el-variable-name" />
						</xsl:apply-templates>
					</ul>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="javaee:tag">
		<li>
			<a>
				<xsl:attribute name="href"><xsl:value-of select="../@id" />/<xsl:value-of select="javaee:tag-name" />.html</xsl:attribute>
				<xsl:choose>
					<xsl:when test="javaee:tag-extension/vdldoc:deprecation/vdldoc:deprecated = 'true'">
						<del>
							<xsl:value-of select="../@id" />:<xsl:value-of select="javaee:tag-name" />
						</del>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="../@id" />:<xsl:value-of select="javaee:tag-name" />
					</xsl:otherwise>
				</xsl:choose>
			</a>
		</li>
	</xsl:template>

	<xsl:template match="javaee:function">
		<li>
			<a>
				<xsl:attribute name="href"><xsl:value-of select="../@id" />/<xsl:value-of select="javaee:function-name" />.fn.html</xsl:attribute>
				<i><xsl:value-of select="../@id" />:<xsl:value-of select="javaee:function-name" />()</i>
			</a>
		</li>
	</xsl:template>
	
	<xsl:template match="vdldoc:el-variable">
		<li>
			<a target="tagFrame">
				<xsl:attribute name="href"><xsl:value-of select="../../@id" />/<xsl:value-of select="vdldoc:el-variable-name" />.el.html</xsl:attribute>
				<xsl:choose>
					<xsl:when test="vdldoc:deprecation/vdldoc:deprecated = 'true'">
						<del>
							<xsl:value-of select="vdldoc:el-variable-name" />
						</del>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="vdldoc:el-variable-name" />
					</xsl:otherwise>
				</xsl:choose>
			</a>
		</li>
	</xsl:template>
</xsl:stylesheet>