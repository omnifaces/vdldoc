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
 - Creates the help-doc page for VDL Generator.
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
					API Help (<xsl:value-of select="/javaee:vdldoc/javaee:config/javaee:window-title" />)
				</title>
				<link rel="stylesheet" type="text/css" href="stylesheet.css" title="Style" />
			</head>
			<body>
				<script type="text/javascript">
					if (location.href.indexOf('is-external=true') == -1) parent.document.title = "API Help";
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
						<li><a href="overview-summary.html">Overview</a></li>
						<li>Library</li>
						<li>Tag</li>
						<li class="navBarCell1Rev">Help</li>
					</ul>
				</div>
				<div class="subNav">
					<ul class="navList">
						<li><a href="index.html?help-doc.html" target="_top">Frames</a></li>
						<li><a href="help-doc.html" target="_top">No Frames</a></li>
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

				<div class="header">
					<h1 class="title">How This VDL Document Is Organized</h1>
					<div class="subTitle">This VDL (View Declaration Language) document has pages
					corresponding to the items in the navigation bar, described as follows.</div>
				</div>

				<div class="contentContainer">
					<ul class="blockList">
						<li class="blockList">
							<h2>Overview</h2>
							<p>The <a href="overview-summary.html">Overview</a> page is the front page of
							this VDL documentation and provides a list of all tag libraries with a summary
							for each.</p>
						</li>
						<li class="blockList">
							<h2>Library</h2>
							<p>Each tag library has a page that contains a list of its tags and
							functions, with a summary for each. This page can contain two categories:</p>
							<ul>
								<li>Tags</li>
								<li>Functions</li>
							</ul>
						</li>
						<li class="blockList">
							<h2>Tags</h2>
							<p>A tag library can have zero or more tags. Each tag has its own page that
							describes the tag and depending on the tag type, information about the UI
							component, behaviour, converter and/or validator, along with all attributes
							in detail.</p>
						</li>
						<li class="blockList">
							<h2>Functions</h2>
							<p>A tag library can contain zero or more EL functions. If a tag library has
							at least one function, a page is generated that lists all functions, the
							class that implements the function, the function signature, and an optional
							example use of the function.</p>
						</li>
						<li class="blockList">
							<h2>Frames/No Frames</h2>
							<p>These links show and hide the HTML frames. All pages are available with
							or without frames.</p>
						</li>
					</ul>
				</div>

				<!-- ========= START OF BOTTOM NAVBAR ======= -->
				<div class="bottomNav">
					<a name="navbar_bottom"></a>
					<a href="#skip-navbar_bottom" title="Skip navigation links"></a>
					<a name="navbar_bottom_firstrow"></a>
					<ul class="navList" title="Navigation">
						<li><a href="overview-summary.html">Overview</a></li>
						<li>Library</li>
						<li>Tag</li>
						<li class="navBarCell1Rev">Help</li>
					</ul>
				</div>
				<div class="subNav">
					<ul class="navList">
						<li><a href="index.html?help-doc.html" target="_bottom">Frames</a></li>
						<li><a href="help-doc.html" target="_bottom">No Frames</a></li>
					</ul>
					<ul class="navList" id="alltags_navbar_bottom">
						<li><a href="alltags-noframe.html">All Tags</a></li>
					</ul>
					<script type="text/javascript">
						document.getElementById("alltags_navbar_bottom").style.display = (window == top) ? "block" : "none";
					</script>
					<a name="skip-navbar_bottom"></a>
				</div>
				<!-- ========= END OF BOTTOM NAVBAR ========= -->

				<p class="about">Output generated by <a href="http://vdldoc.omnifaces.org" target="_blank">Vdldoc</a> View Declaration Language Documentation Generator.</p>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>