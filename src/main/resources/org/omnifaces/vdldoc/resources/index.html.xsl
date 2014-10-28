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
 - Creates the index page for Tag Library Documentation Generator.
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
					<xsl:value-of select="/javaee:vdldoc/javaee:config/javaee:window-title" />
				</title>
				<script type="text/javascript">
					targetPage = "" + window.location.search;
					if (targetPage != "" &amp;&amp; targetPage != "undefined") targetPage = targetPage.substring(1);
					if (targetPage.indexOf(":") != -1) targetPage = "undefined";
					function loadFrames() {
						if (targetPage != "" &amp;&amp; targetPage != "undefined") top.tagFrame.location = top.targetPage;
					}
				</script>
			</head>
			<frameset cols="20%,80%" title="Documentation frame" onload="top.loadFrames()">
				<frameset rows="30%,70%" title="Left frames" onload="top.loadFrames()">
					<frame src="overview-frame.html" name="tldListFrame" title="All Tag Libraries" />
					<frame src="alltags-frame.html" name="tldFrame" title="All Tags / Functions" />
				</frameset>
				<frame src="overview-summary.html" name="tagFrame" title="Library and tag descriptions" scrolling="yes" />
			</frameset>
			<noframes>
				<noscript>
					<div>JavaScript is disabled on your browser.</div>
				</noscript>
				<h2>Frame Alert</h2>
				<p>
					This document is designed to be viewed using the frames feature. If you see this message, you are using
					a non-frame-capable web client. Link to <a href="overview-summary.html">Non-frame version</a>.
				</p>
			</noframes>
		</html>
	</xsl:template>
</xsl:stylesheet>