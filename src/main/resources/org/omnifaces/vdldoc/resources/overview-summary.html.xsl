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
 - Creates an overview summary (right frame), listing all tag libraries included in this generation.
 -
 - @author Bauke Scholtz
-->
<xsl:stylesheet
	xmlns:jakartaee="https://jakarta.ee/xml/ns/jakartaee"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	version="3.0"
>
	<xsl:output method="html" indent="yes"
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html4/loose.dtd" />

	<xsl:template match="/">
		<html lang="en">
			<head>
				<title>
					Overview (<xsl:value-of select="/jakartaee:vdldoc/jakartaee:config/jakartaee:window-title" />)
				</title>
				<link rel="stylesheet" type="text/css" title="Style">
					<xsl:attribute name="href">
						<xsl:value-of select="/jakartaee:vdldoc/jakartaee:config/@css-location" />
					</xsl:attribute>
				</link>
			</head>
			<body>
				<noscript>
					<div>JavaScript is disabled on your browser.</div>
				</noscript>

				<!-- ========= START OF TOP NAVBAR ======= -->
				<div class="topNav">
					<a name="navbar_top"></a>
					<a href="#skip-navbar_top" title="Skip navigation links"></a>
					<a name="navbar_top_firstrow"></a>
					<ul class="navList" title="Navigation">
						<li class="navBarCell1Rev">Overview</li>
						<li>Library</li>
						<li>Tag</li>
						<li><a href="help-doc.html">Help</a></li>
					</ul>
				</div>
				<div class="subNav">
					<ul class="navList">
						<li><a href="index.html?overview-summary.html" target="_top">Frames</a></li>
						<li><a href="overview-summary.html" target="_top">No Frames</a></li>
					</ul>
					<ul class="navList" id="alltags_navbar_top">
						<li><a href="alltags-noframe.html">All Tags</a></li>
					</ul>
					<div>
						<script type="text/javascript">
							document.getElementById("alltags_navbar_top").style.display = (window == top) ? "block" : "none";
						</script>
					</div>
					<a name="skip-navbar_top"></a>
				</div>
				<!-- ========= END OF TOP NAVBAR ========= -->

				<div class="header">
					<h1 class="title">
						<xsl:value-of select="/jakartaee:vdldoc/jakartaee:config/jakartaee:doc-title" />
					</h1>
				</div>
				<div class="contentContainer">
					<table class="overviewSummary" border="0" cellpadding="3" cellspacing="0" summary="Tag Library table, listing tag libraries, and an explanation">
						<caption>
							<span>Tag Libraries</span>
							<span class="tabEnd">&#160;</span>
						</caption>
						<thead>
							<tr>
								<th class="colFirst" scope="col">Library</th>
								<th class="colLast" scope="col">Description</th>
							</tr>
						</thead>
						<tbody>
							<xsl:apply-templates select="/jakartaee:vdldoc/jakartaee:facelet-taglib" />
						</tbody>
					</table>

					<xsl:if test="count(/jakartaee:vdldoc/jakartaee:faces-config/jakartaee:managed-bean) > 0">
						<table class="overviewSummary" border="0" cellpadding="3" cellspacing="0" summary="Managed bean table, listing managed beans, and an explanation">
							<caption>
								<span>Managed beans</span>
								<span class="tabEnd">&#160;</span>
							</caption>
							<thead>
								<tr>
									<th class="colFirst" scope="col">Name</th>
									<th class="colOne" scope="col">Class</th>
									<th class="colOne" scope="col">Scope</th>
									<th class="colLast" scope="col">Description</th>
								</tr>
							</thead>
							<tbody>
								<xsl:apply-templates select="/jakartaee:vdldoc/jakartaee:faces-config/jakartaee:managed-bean" />
							</tbody>
						</table>
					</xsl:if>

					<xsl:if test="count(/jakartaee:vdldoc/jakartaee:faces-config/jakartaee:validator) > 0">
						<table class="overviewSummary" border="0" cellpadding="3" cellspacing="0" summary="Components table, listing component, and an explanation">
							<caption>
								<span>Validators</span>
								<span class="tabEnd">&#160;</span>
							</caption>
							<thead>
								<tr>
									<th class="colFirst" scope="col">Name</th>
									<th class="colOne" scope="col">Class</th>
									<th class="colOne" scope="col">Scope</th>
									<th class="colLast" scope="col">Description</th>
								</tr>
							</thead>
							<tbody>
								<xsl:apply-templates select="/jakartaee:vdldoc/jakartaee:faces-config/jakartaee:behavior" />
							</tbody>
						</table>
					</xsl:if>
				</div>


						<!-- Component Information -->
						<xsl:if test="normalize-space(jakartaee:component)">
							<xsl:apply-templates select="jakartaee:component" />
						</xsl:if>

						<!-- Behavior Information -->
						<xsl:if test="normalize-space(jakartaee:behavior)">
							<xsl:apply-templates select="jakartaee:behavior" />
						</xsl:if>

						<!-- Converter Information -->
						<xsl:if test="normalize-space(jakartaee:converter)">
							<xsl:apply-templates select="jakartaee:converter" />
						</xsl:if>

						<!-- Validator Information -->
						<xsl:if test="normalize-space(jakartaee:validator)">
							<xsl:apply-templates select="jakartaee:validator" />
						</xsl:if>

				<!-- ======= START OF BOTTOM NAVBAR ====== -->
				<div class="bottomNav">
					<a name="navbar_bottom"></a>
					<a href="#skip-navbar_bottom" title="Skip navigation links"></a>
					<a name="navbar_bottom_firstrow"></a>
					<ul class="navList" title="Navigation">
						<li class="navBarCell1Rev">Overview</li>
						<li>Library</li>
						<li>Tag</li>
						<li><a href="help-doc.html">Help</a></li>
					</ul>
				</div>
				<div class="subNav">
					<ul class="navList">
						<li><a href="index.html?overview-summary.html" target="_top">Frames</a></li>
						<li><a href="overview-summary.html" target="_top">No Frames</a></li>
					</ul>
					<ul class="navList" id="alltags_navbar_bottom">
						<li><a href="alltags-noframe.html">All Tags</a></li>
					</ul>
					<script type="text/javascript">
						document.getElementById("alltags_navbar_bottom").style.display = (window == top) ? "block" : "none";
					</script>
					<a name="skip-navbar_bottom"></a>
				</div>
				<!-- ======== END OF BOTTOM NAVBAR ======= -->

				<xsl:if test="/jakartaee:vdldoc/jakartaee:config/@hide-generated-by != 'true'">
					<p class="about">Output generated by <a href="http://vdldoc.omnifaces.org" target="_blank">Vdldoc</a> View Declaration Language Documentation Generator.</p>
				</xsl:if>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="jakartaee:managed-bean">
		<tr>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="position() mod 2 = 0">altColor</xsl:when>
					<xsl:otherwise>rowColor</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>

			<td class="colFirst">
				<code><xsl:value-of select="jakartaee:managed-bean-name" /></code>
			</td>
			<td class="colOne">
				<code><xsl:value-of select="jakartaee:managed-bean-class" /></code>
			</td>
			<td class="colOne">
				<code><xsl:value-of select="jakartaee:managed-bean-scope" /></code>
			</td>
			<td class="colLast">
				<xsl:choose>
					<xsl:when test="normalize-space(jakartaee:description)">
						<xsl:value-of select="jakartaee:description" disable-output-escaping="yes" />
					</xsl:when>
					<xsl:otherwise>
						<i>No Description</i>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="jakartaee:facelet-taglib">
		<tr>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="position() mod 2 = 0">altColor</xsl:when>
					<xsl:otherwise>rowColor</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>

			<td class="colFirst">
				<a>
					<xsl:attribute name="href"><xsl:value-of select="@id" />/tld-summary.html</xsl:attribute>
					<xsl:value-of select="@id" />
				</a>
			</td>
			<td class="colLast">
				<xsl:choose>
					<xsl:when test="normalize-space(jakartaee:description)">
						<xsl:value-of select="jakartaee:description" disable-output-escaping="yes" />
					</xsl:when>
					<xsl:otherwise>
						<i>No Description</i>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>