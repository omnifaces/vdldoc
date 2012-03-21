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
	xmlns:javaee="http://java.sun.com/xml/ns/javaee"
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
				<link rel="stylesheet" type="text/css" href="stylesheet.css" title="Style" />
			</head>
			<body>
				<script type="text/javascript">
					if (location.href.indexOf('is-external=true') == -1) parent.document.title = "Overview";
				</script>
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
					<script type="text/javascript">
						document.getElementById("alltags_navbar_top").style.display = (window == top) ? "block" : "none";
					</script>
					<a name="skip-navbar_top"></a>
				</div>
				<!-- ========= END OF TOP NAVBAR ========= -->

				<div class="contentContainer">
					<h2>
						<xsl:value-of select="/javaee:vdldoc/javaee:config/javaee:doc-title" />
					</h2>

					<div class="summary">
						<ul class="blockList">
							<li class="blockList">
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
										<xsl:apply-templates select="/javaee:vdldoc/javaee:facelet-taglib" />
									</tbody>
								</table>
							</li>
						</ul>
					</div>

					<xsl:if test="count(/javaee:vdldoc/javaee:faces-config/javaee:managed-bean) > 0">
						<div class="summary">
							<ul class="blockList">
								<li class="blockList">
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
											<xsl:apply-templates select="/javaee:vdldoc/javaee:faces-config/javaee:managed-bean" />
										</tbody>
									</table>
								</li>
							</ul>
						</div>
					</xsl:if>

					<xsl:if test="count(/javaee:vdldoc/javaee:faces-config/javaee:validator) > 0">
						<div class="summary">
							<ul class="blockList">
								<li class="blockList">
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
											<xsl:apply-templates select="/javaee:vdldoc/javaee:faces-config/javaee:behavior" />
										</tbody>
									</table>
								</li>
							</ul>
						</div>
					</xsl:if>
				</div>


						<!-- Component Information -->
						<xsl:if test="normalize-space(javaee:component)">
							<xsl:apply-templates select="javaee:component" />
						</xsl:if>

						<!-- Behavior Information -->
						<xsl:if test="normalize-space(javaee:behavior)">
							<xsl:apply-templates select="javaee:behavior" />
						</xsl:if>

						<!-- Converter Information -->
						<xsl:if test="normalize-space(javaee:converter)">
							<xsl:apply-templates select="javaee:converter" />
						</xsl:if>

						<!-- Validator Information -->
						<xsl:if test="normalize-space(javaee:validator)">
							<xsl:apply-templates select="javaee:validator" />
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

				<p class="about">Output generated by <a href="http://code.google.com/p/vdldoc" target="_blank">Vdldoc</a> View Declaration Language Documentation Generator.</p>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="javaee:managed-bean">
		<tr>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="position() mod 2 = 0">altColor</xsl:when>
					<xsl:otherwise>rowColor</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>

			<td class="colFirst">
				<code><xsl:value-of select="javaee:managed-bean-name" /></code>
			</td>
			<td class="colOne">
				<code><xsl:value-of select="javaee:managed-bean-class" /></code>
			</td>
			<td class="colOne">
				<code><xsl:value-of select="javaee:managed-bean-scope" /></code>
			</td>
			<td class="colLast">
				<xsl:choose>
					<xsl:when test="normalize-space(javaee:description)">
						<xsl:value-of select="javaee:description" disable-output-escaping="yes" />
					</xsl:when>
					<xsl:otherwise>
						<i>No Description</i>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="javaee:facelet-taglib">
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
					<xsl:when test="normalize-space(javaee:description)">
						<xsl:value-of select="javaee:description" disable-output-escaping="yes" />
					</xsl:when>
					<xsl:otherwise>
						<i>No Description</i>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>