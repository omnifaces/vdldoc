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
	<xsl:param name="tagName">
		default
	</xsl:param>

	<!-- template rule matching source root element -->
	<xsl:template match="/">
		<xsl:apply-templates select="jakartaee:vdldoc/jakartaee:facelet-taglib" />
	</xsl:template>

	<xsl:template match="jakartaee:facelet-taglib">
		<xsl:if test="@id = $id">
			<xsl:apply-templates select="jakartaee:tag" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="jakartaee:tag">
		<xsl:if test="jakartaee:tag-name = $tagName">
			<xsl:variable name="title">
				<xsl:value-of select="jakartaee:tag-name" /> (<xsl:value-of select="/jakartaee:vdldoc/jakartaee:config/jakartaee:window-title" />)
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
									<xsl:attribute name="href">../index.html?<xsl:value-of select="$id" />/<xsl:value-of select="jakartaee:tag-name" />.html</xsl:attribute>
									Frames
								</a>
							</li>
							<li>
								<a target="_top">
									<xsl:attribute name="href"><xsl:value-of select="jakartaee:tag-name" />.html</xsl:attribute>
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
								<xsl:when test="jakartaee:tag-extension/vdldoc:deprecated or jakartaee:tag-extension/vdldoc:deprecation/vdldoc:deprecated = 'true'">
									<del>
										<xsl:value-of select="jakartaee:tag-name" />
									</del>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="jakartaee:tag-name" />
								</xsl:otherwise>
							</xsl:choose>
						</h2>
					</div>

					<div class="contentContainer">

						<!-- Tag Information -->
						<div class="description">
							<ul class="blockList">
								<li class="blockList">
									<dl>
										<dt>Description:</dt>
										<dd>
											<div class="block">
												<!-- vdldoc:deprecation is deprecated. It has been replaced by vdldoc:deprecated. -->
												<xsl:if test="jakartaee:tag-extension/vdldoc:deprecated or jakartaee:tag-extension/vdldoc:deprecation/vdldoc:deprecated = 'true'">
													<b>Deprecated. </b>
													<xsl:choose>
														<xsl:when test="jakartaee:tag-extension/vdldoc:deprecated">
															<xsl:value-of select="jakartaee:tag-extension/vdldoc:deprecated" />
														</xsl:when>
														<!-- vdldoc:deprecation is deprecated. It has been replaced by vdldoc:deprecated. -->
														<xsl:when test="jakartaee:tag-extension/vdldoc:deprecation/vdldoc:deprecated = 'true'">
															<xsl:value-of select="jakartaee:tag-extension/vdldoc:deprecation/vdldoc:description" />
														</xsl:when>
													</xsl:choose>
													<xsl:text>&#160;</xsl:text>
												</xsl:if>
												<xsl:choose>
													<xsl:when test="normalize-space(jakartaee:description)">
														<xsl:value-of select="jakartaee:description" disable-output-escaping="yes" />
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

						<xsl:if test="normalize-space(jakartaee:tag-extension/vdldoc:since)">
							<div class="since">
								<ul class="blockList">
									<li class="blockList">
										<dl>
											<dt>Since:</dt>
											<dd>
												<xsl:value-of select="jakartaee:tag-extension/vdldoc:since" disable-output-escaping="yes" />
											</dd>
										</dl>
									</li>
								</ul>
							</div>
						</xsl:if>

						<xsl:if test="normalize-space(jakartaee:tag-extension/vdldoc:example-url)">
							<div class="example-url">
								<ul class="blockList">
									<li class="blockList">
										<dl>
											<dt>Example usage of this component can be found at:</dt>
											<br />
											<dd>
												<a href="{jakartaee:tag-extension/vdldoc:example-url}" target="_blank">
													<xsl:value-of select="jakartaee:tag-extension/vdldoc:example-url" disable-output-escaping="yes" />
												</a>
											</dd>
										</dl>
									</li>
								</ul>
							</div>
						</xsl:if>

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

						<!-- Attribute Information -->
						<div class="summary">
							<table class="overviewSummary" border="0" cellpadding="3" cellspacing="0" summary="Attribute summary table, listing attribute information">
								<caption>
									<span>Attributes</span>
									<span class="tabEnd">&#160;</span>
								</caption>
								<thead>
									<tr>
										<th class="colFirst">Name</th>
										<th class="colOne">Required</th>
										<th class="colOne">Type</th>
										<th class="colLast">Description</th>
									</tr>
								</thead>
								<tbody>
									<xsl:choose>
										<xsl:when test="count(jakartaee:attribute) > 0">
											<xsl:apply-templates select="jakartaee:attribute" />
										</xsl:when>
										<xsl:otherwise>
											<td class="colOne" colspan="4">
												<i>No Attributes Defined.</i>
											</td>
										</xsl:otherwise>
									</xsl:choose>
								</tbody>
							</table>
						</div>
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
									<xsl:attribute name="href">../index.html?<xsl:value-of select="$id" />/<xsl:value-of select="jakartaee:tag-name" />.html</xsl:attribute>
									Frames
								</a>
							</li>
							<li>
								<a target="_bottom">
									<xsl:attribute name="href"><xsl:value-of select="jakartaee:tag-name" />.html</xsl:attribute>
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

	<xsl:template match="jakartaee:component">
		<xsl:call-template name="summary">
			<xsl:with-param name="caption" select="'Component Information'" />
			<xsl:with-param name="summary" select="'Component summary table, listing component information'" />
			<xsl:with-param name="id-name" select="'Component Type'" />
			<xsl:with-param name="id-value" select="jakartaee:component-type" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="jakartaee:behavior">
		<xsl:call-template name="summary">
			<xsl:with-param name="caption" select="'Behavior Information'" />
			<xsl:with-param name="summary" select="'Behavior summary table, listing behavior information'" />
			<xsl:with-param name="id-name" select="'Behavior ID'" />
			<xsl:with-param name="id-value" select="jakartaee:behavior-id" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="jakartaee:converter">
		<xsl:call-template name="summary">
			<xsl:with-param name="caption" select="'Converter Information'" />
			<xsl:with-param name="summary" select="'Converter summary table, listing converter information'" />
			<xsl:with-param name="id-name" select="'Converter ID'" />
			<xsl:with-param name="id-value" select="jakartaee:converter-id" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="jakartaee:validator">
		<xsl:call-template name="summary">
			<xsl:with-param name="caption" select="'Validator Information'" />
			<xsl:with-param name="summary" select="'Validator summary table, listing validator information'" />
			<xsl:with-param name="id-name" select="'Validator ID'" />
			<xsl:with-param name="id-value" select="jakartaee:validator-id" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="summary">
		<xsl:param name="caption" />
		<xsl:param name="summary" />
		<xsl:param name="id-name" />
		<xsl:param name="id-value" />

		<div class="summary">
			<table class="overviewSummary" border="0" cellpadding="3" cellspacing="0">
				<xsl:attribute name="summary">
					<xsl:value-of select="$summary" />
				</xsl:attribute>

				<caption>
					<span><xsl:value-of select="$caption" /></span>
					<span class="tabEnd">&#160;</span>
				</caption>
				<thead>
					<tr>
						<th class="colFirst" scope="col">Info</th>
						<th class="colLast" scope="col">Value</th>
					</tr>
				</thead>
				<tbody>
					<tr class="rowColor">
						<td class="colFirst"><xsl:value-of select="$id-name" /></td>
						<td class="colLast">
							<xsl:choose>
								<xsl:when test="normalize-space($id-value)">
									<code><xsl:value-of select="$id-value" /></code>
								</xsl:when>
								<xsl:otherwise>
									<i>None</i>
								</xsl:otherwise>
							</xsl:choose>
						</td>
					</tr>
					<tr class="altColor">
						<td class="colFirst">Handler Class</td>
						<td class="colLast">
							<xsl:choose>
								<xsl:when test="normalize-space(jakartaee:handler-class)">
									<code><xsl:value-of select="jakartaee:handler-class" /></code>
								</xsl:when>
								<xsl:otherwise>
									<i>None</i>
								</xsl:otherwise>
							</xsl:choose>
						</td>
					</tr>
					<xsl:if test="normalize-space(jakartaee:component-type)">
						<tr class="rowColor">
							<td class="colFirst">Renderer Type</td>
							<td class="colLast">
								<xsl:choose>
									<xsl:when test="normalize-space(jakartaee:renderer-type)">
										<code><xsl:value-of select="jakartaee:renderer-type" /></code>
									</xsl:when>
									<xsl:otherwise>
										<i>None</i>
									</xsl:otherwise>
								</xsl:choose>
							</td>
						</tr>
					</xsl:if>
					<tr>
						<xsl:attribute name="class">
							<xsl:choose>
								<xsl:when test="normalize-space(jakartaee:component-type)">altColor</xsl:when>
								<xsl:otherwise>rowColor</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<td class="colFirst">Description</td>
						<td class="colLast">
							<xsl:choose>
								<xsl:when test="normalize-space(jakartaee:description)">
									<xsl:value-of select="jakartaee:description" disable-output-escaping="yes" />
								</xsl:when>
								<xsl:otherwise>
									<i>None</i>
								</xsl:otherwise>
							</xsl:choose>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<xsl:template match="jakartaee:attribute">
		<tr>
			<xsl:attribute name="id">
				<xsl:value-of select="jakartaee:name" />
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="position() mod 2 = 0">altColor</xsl:when>
					<xsl:otherwise>rowColor</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>

			<td class="colFirst">
				<xsl:choose>
					<!-- vdldoc:deprecation is deprecated. It has been replaced by vdldoc:deprecated. -->
					<xsl:when test="../jakartaee:tag-extension/vdldoc:deprecated or ../jakartaee:tag-extension/vdldoc:deprecation/vdldoc:deprecated = 'true'">
						<a>
							<xsl:attribute name="href">
								<xsl:text>#</xsl:text><xsl:value-of select="jakartaee:name" />
							</xsl:attribute>
							<del>
								<code>
									<xsl:apply-templates select="jakartaee:name" />
								</code>
							</del>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<a>
							<xsl:attribute name="href">
								<xsl:text>#</xsl:text><xsl:value-of select="jakartaee:name" />
							</xsl:attribute>
							<code>
								<xsl:apply-templates select="jakartaee:name" />
							</code>
						</a>
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td class="colOne">
				<code>
					<xsl:choose>
						<xsl:when test="normalize-space(jakartaee:required)">
							<xsl:value-of select="jakartaee:required" />
						</xsl:when>
						<xsl:otherwise>
							false
						</xsl:otherwise>
					</xsl:choose>
				</code>
			</td>
			<td class="colOne">
				<xsl:choose>
					<xsl:when test="normalize-space(jakartaee:type)">
						<code>javax.el.ValueExpression</code>
						<br />(<i>must evaluate to </i><code><xsl:value-of select="jakartaee:type" /></code>)
					</xsl:when>
					<xsl:when test="normalize-space(jakartaee:method-signature)">
						<code>javax.el.MethodExpression</code>
						<br />(<i>signature must match </i><code><xsl:value-of select="jakartaee:method-signature" /></code>)
					</xsl:when>
					<xsl:otherwise>
						<code>java.lang.String</code>
					</xsl:otherwise>
				</xsl:choose>
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
