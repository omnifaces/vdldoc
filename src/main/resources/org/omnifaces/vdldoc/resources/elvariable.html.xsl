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
 - Creates the tag detail page (right frame), listing the known information for a given tag in a tag library.
 -
 - @author Bauke Scholtz
-->
<xsl:stylesheet
	xmlns:jakartaee="https://jakarta.ee/xml/ns/jakartaee"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:vdldoc="http://vdldoc.omnifaces.org"
	version="3.0"
>
	<xsl:output method="html" indent="yes"
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html4/loose.dtd" />

	<xsl:param name="id">
		default
	</xsl:param>
	<xsl:param name="elVariableName">
		default
	</xsl:param>

	<!-- template rule matching source root element -->
	<xsl:template match="/">
		<xsl:apply-templates select="jakartaee:vdldoc/jakartaee:facelet-taglib" />
	</xsl:template>

	<xsl:template match="jakartaee:facelet-taglib">
		<xsl:if test="@id = $id">
			<xsl:apply-templates select="jakartaee:taglib-extension" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="vdldoc:el-variable">
		<xsl:if test="vdldoc:el-variable-name = $elVariableName">
			<xsl:variable name="title">
				<xsl:value-of select="vdldoc:el-variable-name" /> (<xsl:value-of select="/jakartaee:vdldoc/jakartaee:config/jakartaee:window-title" />)
			</xsl:variable>

			<html lang="en">
				<head>
					<title>
						<xsl:value-of select="$title" />
					</title>
					<meta name="keywords" content="$title" />
					<link rel="stylesheet" type="text/css" title="Style">
						<xsl:attribute name="href">
							<xsl:value-of select="/jakartaee:vdldoc/jakartaee:config/@subfolder-css-location" />
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
							<li><a href="../overview-summary.html">Overview</a></li>
							<li><a href="tld-summary.html">Library</a></li>
							<li class="navBarCell1Rev">Tag</li>
							<li><a href="../help-doc.html">Help</a></li>
						</ul>
					</div>
					<div class="subNav">
						<ul class="navList">
							<li>
								<a target="_top">
									<xsl:attribute name="href">../index.html?<xsl:value-of select="$id" />/<xsl:value-of select="vdldoc:el-variable-name" />.el.html</xsl:attribute>
									Frames
								</a>
							</li>
							<li>
								<a target="_top">
									<xsl:attribute name="href"><xsl:value-of select="vdldoc:el-variable-name" />.html</xsl:attribute>
									No Frames
								</a>
							</li>
						</ul>
						<ul class="navList" id="alltags_navbar_top">
							<li><a href="../alltags-noframe.html">All Tags</a></li>
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
						<h1 title="Library" class="title">
							<xsl:value-of select="$id" />
						</h1>
						<h2 class="title">
							Tag
							<xsl:choose>
								<!-- vdldoc:deprecation is deprecated. It has been replaced by vdldoc:deprecated. -->
								<xsl:when test="vdldoc:deprecated or vdldoc:deprecation/vdldoc:deprecated = 'true'">
									<del>
										<xsl:value-of select="vdldoc:el-variable-name" />
									</del>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="vdldoc:el-variable-name" />
								</xsl:otherwise>
							</xsl:choose>
						</h2>
					</div>

					<div class="contentContainer">

						<!-- ELVariable Information -->
						<div class="description">
							<ul class="blockList">
								<li class="blockList">
									<dl>
										<dt>Description:</dt>
										<dd>
											<div class="block">
												<!-- vdldoc:deprecation is deprecated. It has been replaced by vdldoc:deprecated. -->
												<xsl:if test="vdldoc:deprecated or vdldoc:deprecation/vdldoc:deprecated = 'true'">
													<b>Deprecated. </b>
													<xsl:choose>
														<xsl:when test="vdldoc:deprecated">
															<xsl:value-of select="vdldoc:deprecated" />
														</xsl:when>
														<!-- vdldoc:deprecation is deprecated. It has been replaced by vdldoc:deprecated. -->
														<xsl:when test="vdldoc:deprecation/vdldoc:deprecated = 'true'">
															<xsl:value-of select="vdldoc:deprecation/vdldoc:description" />
														</xsl:when>
													</xsl:choose>
													<xsl:text>&#160;</xsl:text>
												</xsl:if>
												<xsl:choose>
													<xsl:when test="normalize-space(vdldoc:description)">
														<xsl:value-of select="vdldoc:description" disable-output-escaping="yes" />
													</xsl:when>
													<xsl:otherwise>
														<i>No Description</i>
													</xsl:otherwise>
												</xsl:choose>
											</div>
										</dd>
									</dl>
								</li>
							</ul>
						</div>

						<div class="type">
							<ul class="blockList">
								<li class="blockList">
									<dl>
										<dt>Type:</dt>
										<dd>
											<xsl:choose>
												<xsl:when test="normalize-space(vdldoc:type)">
													<xsl:value-of select="vdldoc:type" disable-output-escaping="yes" />
												</xsl:when>
												<xsl:otherwise>
													<i>java.lang.Object</i>
												</xsl:otherwise>
											</xsl:choose>
										</dd>
									</dl>
								</li>
							</ul>
						</div>

						<xsl:if test="normalize-space(vdldoc:since)">
							<div class="since">
								<ul class="blockList">
									<li class="blockList">
										<dl>
											<dt>Since:</dt>
											<dd>
												<xsl:value-of select="vdldoc:since" disable-output-escaping="yes" />
											</dd>
										</dl>
									</li>
								</ul>
							</div>
						</xsl:if>
						
						<xsl:if test="normalize-space(vdldoc:example-url)">
							<div class="example-url">
								<ul class="blockList">
									<li class="blockList">
										<dl>
											<dt>Example usage of this component can be found at:</dt>
											<br />
											<dd>
												<a href="{vdldoc:example-url}" target="_blank">
													<xsl:value-of select="vdldoc:example-url" disable-output-escaping="yes" />
												</a>
											</dd>
										</dl>
									</li>
								</ul>
							</div>						
						</xsl:if>
					</div>

					<!-- ========= START OF BOTTOM NAVBAR ======= -->
					<div class="bottomNav">
						<a name="navbar_bottom"></a>
						<a href="#skip-navbar_bottom" title="Skip navigation links"></a>
						<a name="navbar_bottom_firstrow"></a>
						<ul class="navList" title="Navigation">
							<li><a href="../overview-summary.html">Overview</a></li>
							<li><a href="tld-summary.html">Library</a></li>
							<li class="navBarCell1Rev">Tag</li>
							<li><a href="../help-doc.html">Help</a></li>
						</ul>
					</div>
					<div class="subNav">
						<ul class="navList">
							<li>
								<a target="_bottom">
									<xsl:attribute name="href">../index.html?<xsl:value-of select="$id" />/<xsl:value-of select="vdldoc:el-variable-name" />.html</xsl:attribute>
									Frames
								</a>
							</li>
							<li>
								<a target="_bottom">
									<xsl:attribute name="href"><xsl:value-of select="vdldoc:el-variable-name" />.html</xsl:attribute>
									No Frames
								</a>
							</li>
						</ul>
						<ul class="navList" id="alltags_navbar_bottom">
							<li><a href="../alltags-noframe.html">All Tags</a></li>
						</ul>
						<script type="text/javascript">
							document.getElementById("alltags_navbar_bottom").style.display = (window == top) ? "block" : "none";
						</script>
						<a name="skip-navbar_bottom"></a>
					</div>
					<!-- ========= END OF BOTTOM NAVBAR ========= -->

					<xsl:if test="/jakartaee:vdldoc/jakartaee:config/@hide-generated-by != 'true'">
						<p class="about">Output generated by <a href="http://vdldoc.omnifaces.org" target="_blank">Vdldoc</a> View Declaration Language Documentation Generator.</p>
					</xsl:if>
				</body>
			</html>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>