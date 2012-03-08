/*
 * Copyright (c) 2012, OmniFaces
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *     * Neither the name of OmniFaces nor the names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.omnifaces.vdldoc;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * VDLdoc Generator. Takes a set of Facelets taglib files and generates a set of Java 7 javadoc-style HTML pages that
 * describe the various components of the given Facelets taglib files.
 * <p>
 * This is a forked and rewritten version of old TLDdoc Generator as previously available at
 * http://taglibrarydoc.dev.java.net (which is nowhere available right now).
 *
 * @author Bauke Scholtz
 */
public class VdldocGenerator {

	// Constants ------------------------------------------------------------------------------------------------------

	/** Path to VDL documentation resources (CSS, background images and XSLTs). */
	private static final String RESOURCE_PATH = "/org/omnifaces/vdldoc/resources";

	/** The default browser window title for the VDL documentation. */
    private static final String DEFAULT_WINDOW_TITLE = "VDL Documentation Generator - Generated Documentation";

	/** The default title for the VDL documentation index page. */
    private static final String DEFAULT_DOC_TITLE = DEFAULT_WINDOW_TITLE;

    /** The XML namespace for Java EE */
    private static final String NS_JAVAEE = "http://java.sun.com/xml/ns/javaee";

    /** If true, outputs the input to the transform before generation. */
    private static final boolean DEBUG_INPUT_DOCUMENT = false;

    // Properties -----------------------------------------------------------------------------------------------------

	/** The set of tag libraries we are parsing. */
	private List<File> taglibs = new ArrayList<File>();

	/** The output directory for generated files. */
	private File outputDirectory = new File("vdldoc");

	/** The browser window title for the VDL documentation. */
	private String windowTitle = DEFAULT_WINDOW_TITLE;

	/** The title for the VDL index page. */
	private String docTitle = DEFAULT_DOC_TITLE;

	/** True if no stdout is to be produced during generation. */
	private boolean quiet;

	/** The summary VDL document, used as input into XSLT. */
	private Document allTaglibs;

	/** Helps uniquely generate subsitutute prefixes in the case of missing or duplicate display-names. */
	private int substitutePrefix = 1;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Creates a new VDLDocGenerator.
	 */
	public VdldocGenerator() {
		//
	}

	// Public actions -------------------------------------------------------------------------------------------------

	/**
	 * Adds the given individual taglib file.
	 * @param taglib The taglib file to add.
	 */
	public void addTaglib(File taglib) {
		taglibs.add(taglib);
	}

	/**
	 * Sets the output directory for generated files. If not specified,
	 * defaults to "."
	 * @param dir The base directory for generated files.
	 */
	public void setOutputDirectory(File dir) {
		this.outputDirectory = dir;
	}

	/**
	 * Sets the browser window title for the documentation
	 * @param title The browser window title
	 */
	public void setWindowTitle(String title) {
		this.windowTitle = title;
	}

	/**
	 * Sets the title for the VDL documentation index page.
	 * @param title The title for the VDL documentation index page.
	 */
	public void setDocTitle(String title) {
		this.docTitle = title;
	}

	/**
	 * Sets quiet mode (produce no stdout during generation)
	 * @param quiet True if no output is to be produced, false otherwise.
	 */
	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	/**
	 * Commences documentation generation.
	 */
	public void generate() throws IllegalArgumentException {
		try {
			this.outputDirectory.mkdirs();
			copyStaticFiles();
			createSummaryDoc();
			generateOverview();
			generateTaglibDetail();
			outputSuccessMessage();
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		catch (SAXException e) {
			throw new IllegalArgumentException(e);
		}
		catch (ParserConfigurationException e) {
			throw new IllegalArgumentException(e);
		}
		catch (TransformerConfigurationException e) {
			throw new IllegalArgumentException(e);
		}
		catch (TransformerException e) {
			throw new IllegalArgumentException(e);
		}
	}

	// Private actions ------------------------------------------------------------------------------------------------

	/**
	 * Copies all static files to target directory.
	 */
	private void copyStaticFiles() throws IOException {
		copyResourceToFile(new File(this.outputDirectory, "stylesheet.css"), RESOURCE_PATH + "/stylesheet.css");
		File outputResourceDirectory = new File(this.outputDirectory, "resources");
		outputResourceDirectory.mkdirs();
		copyResourceToFile(new File(outputResourceDirectory, "background.gif"), RESOURCE_PATH + "/background.gif");
		copyResourceToFile(new File(outputResourceDirectory, "tab.gif"), RESOURCE_PATH + "/tab.gif");
		copyResourceToFile(new File(outputResourceDirectory, "titlebar_end.gif"), RESOURCE_PATH + "/titlebar_end.gif");
		copyResourceToFile(new File(outputResourceDirectory, "titlebar.gif"), RESOURCE_PATH + "/titlebar.gif");
	}

	/**
	 * Creates a summary document, comprising all input taglibs. This document is later used as input into XSLT to
	 * generate all non-static output pages. Stores the result as a DOM tree in the summaryTLD attribute.
	 */
	private void createSummaryDoc() throws IOException, SAXException, ParserConfigurationException,
		TransformerConfigurationException, TransformerException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		factory.setExpandEntityReferences(false);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		documentBuilder.setEntityResolver(new EntityResolver() {

			@Override
			public InputSource resolveEntity(String publicId, String systemId) {
				return new InputSource(new CharArrayReader(new char[0]));
			}
		});

		allTaglibs = documentBuilder.newDocument();

		// Create root <tlds> element:
		Element rootElement = allTaglibs.createElementNS(NS_JAVAEE, "facelet-taglibs");
		allTaglibs.appendChild(rootElement);

		// JDK 1.4 does not add xmlns for some reason - add it manually:
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", NS_JAVAEE);

		// Create configuration element:
		Element configElement = allTaglibs.createElementNS(NS_JAVAEE, "config");
		rootElement.appendChild(configElement);

		Element windowTitle = allTaglibs.createElementNS(NS_JAVAEE, "window-title");
		windowTitle.appendChild(allTaglibs.createTextNode(this.windowTitle));
		configElement.appendChild(windowTitle);

		Element docTitle = allTaglibs.createElementNS(NS_JAVAEE, "doc-title");
		docTitle.appendChild(allTaglibs.createTextNode(this.docTitle));
		configElement.appendChild(docTitle);

		// Append each <facelet-taglib> element from each taglib:
		println("Loading and translating " + taglibs.size() + " Tag Librar" + ((taglibs.size() == 1) ? "y" : "ies") + "...");

		for (File taglib : taglibs) {
	        FileInputStream in = new FileInputStream(taglib);
	        Document doc;

	        try {
	            doc = documentBuilder.parse(new InputSource(in));
	        }
	        finally {
	            in.close();
	        }

			// If this tag library has no tags and no functions, omit it.
			int numTags = doc.getDocumentElement().getElementsByTagNameNS("*", "tag").getLength()
						+ doc.getDocumentElement().getElementsByTagNameNS("*", "function").getLength();

			if (numTags > 0) {
				Element taglibNode = (Element) allTaglibs.importNode(doc.getDocumentElement(), true);

				if (!taglibNode.getNamespaceURI().equals(NS_JAVAEE)) {
					throw new IllegalArgumentException("Error: " + taglib.getAbsolutePath() + " does not have xmlns=\"" + NS_JAVAEE + "\"");
				}

				if (!taglibNode.getLocalName().equals("facelet-taglib")) {
					throw new IllegalArgumentException("Error: " + taglib.getAbsolutePath() + " does not have <facelet-taglib> as root.");
				}

				rootElement.appendChild(taglibNode);

				// Check if root has a display name attribute and add it if necessary.
				checkOrAddDisplayName(taglib, doc);
			}
		}

		// If debug enabled, output the resulting document, as a test:
		if (DEBUG_INPUT_DOCUMENT) {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(allTaglibs), new StreamResult(System.out));
		}
	}

	/**
	 * Check to see if there's a <display-name> which is to be used as tag prefix. If not, then autogenerate one anyway
	 * and give a warning.
	 * @param tagLibrary The tag library being populated
	 * @param doc The root element of the TLD DOM being populated.
	 */
	private void checkOrAddDisplayName(File taglib, Document doc) {
        if (doc.getDocumentElement().getElementsByTagNameNS( "*", "display-name" ).getLength() == 0) {
			String prefix = "prefix" + substitutePrefix;
			substitutePrefix++;
			Element displayName = doc.createElementNS(NS_JAVAEE, "display-name");
			displayName.appendChild(doc.createTextNode(prefix));
			doc.appendChild(displayName);
			println("WARNING: " + taglib.getAbsolutePath() + " is missing a <display-name> element.  Using '" + prefix + "'.");
        }
	}

	/**
	 * Generates all overview files, summarizing all TLDs.
	 */
	private void generateOverview() throws TransformerException {
		generatePage(new File(this.outputDirectory, "index.html"), RESOURCE_PATH + "/index.html.xsl");
		generatePage(new File(this.outputDirectory, "help-doc.html"), RESOURCE_PATH + "/help-doc.html.xsl");
		generatePage(new File(this.outputDirectory, "overview-frame.html"), RESOURCE_PATH + "/overview-frame.html.xsl");
		generatePage(new File(this.outputDirectory, "alltags-frame.html"), RESOURCE_PATH + "/alltags-frame.html.xsl");
		generatePage(new File(this.outputDirectory, "alltags-noframe.html"), RESOURCE_PATH + "/alltags-noframe.html.xsl");
		generatePage(new File(this.outputDirectory, "overview-summary.html"), RESOURCE_PATH + "/overview-summary.html.xsl");
	}

	/**
	 * Generates all the detail folders for each taglib.
	 */
	private void generateTaglibDetail() throws IllegalArgumentException, TransformerException {
		Set<String> displayNames = new HashSet<String>();
		Element root = allTaglibs.getDocumentElement();
		NodeList taglibs = root.getElementsByTagNameNS("*", "facelet-taglib");
		int size = taglibs.getLength();

		for (int i = 0; i < size; i++) {
			Element taglib = (Element) taglibs.item(i);
			String displayName = findElementValue(taglib, "display-name");

			if (!displayNames.add(displayName)) {
				throw new IllegalArgumentException("Two tag libraries exist with the same display-name '" + displayName + "'.  This is not yet supported.");
			}

			println("Generating docs for " + displayName + "...");
			File outDir = new File(this.outputDirectory, displayName);
			outDir.mkdir();

			// Generate information for each TLD:
			generateTLDDetail(outDir, displayName);

			// Generate information for each tag:
			NodeList tags = taglib.getElementsByTagNameNS("*", "tag");
			int numTags = tags.getLength();

			for (int j = 0; j < numTags; j++) {
				Element tag = (Element) tags.item(j);
				String tagName = findElementValue(tag, "tag-name");
				generateTagDetail(outDir, displayName, tagName);
			}

			// Generate information for each function:
			NodeList functions = taglib.getElementsByTagNameNS("*", "function");
			int numFunctions = functions.getLength();

			for (int j = 0; j < numFunctions; j++) {
				Element function = (Element) functions.item(j);
				String functionName = findElementValue(function, "function-name");
				generateFunctionDetail(outDir, displayName, functionName);
			}
		}
	}

	/**
	 * Generates the detail content for the tag library with the given display-name.
	 * Files will be placed in outdir.
	 */
	private void generateTLDDetail(File outDir, String displayName) throws TransformerException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("displayName", displayName);

		generatePage(new File(outDir, "tld-frame.html"), RESOURCE_PATH + "/tld-frame.html.xsl", parameters);
		generatePage(new File(outDir, "tld-summary.html"), RESOURCE_PATH + "/tld-summary.html.xsl", parameters);
	}

	/**
	 * Generates the detail content for the tag with the given name in the tag library with the given display-name.
	 * Files will be placed in outdir.
	 */
	private void generateTagDetail(File outDir, String displayName, String tagName) throws TransformerException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("displayName", displayName);
		parameters.put("tagName", tagName);

		generatePage(new File(outDir, tagName + ".html"), RESOURCE_PATH + "/tag.html.xsl", parameters);
	}

	/**
	 * Generates the detail content for the function with the given name in the tag library with the given display-name.
	 * Files will be placed in outdir.
	 */
	private void generateFunctionDetail(File outDir, String displayName, String functionName) throws TransformerException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("displayName", displayName);
		parameters.put("functionName", functionName);

		generatePage(new File(outDir, functionName + ".fn.html"), RESOURCE_PATH + "/function.html.xsl", parameters);
	}

	/**
	 * Searches through the given element and returns the value of the body of the element. Returns null if the element
	 * was not found.
	 */
	private static String findElementValue(Element parent, String tagName) {
		String result = null;
		NodeList elements = parent.getElementsByTagNameNS("*", tagName);

		if (elements.getLength() >= 1) {
			Element child = (Element) elements.item(0);
			Node body = child.getFirstChild();

			if (body.getNodeType() == Node.TEXT_NODE) {
				result = body.getNodeValue();
			}
		}

		return result;
	}

	/**
	 * Generates the given page dynamically, by running the summary document through the given XSLT transform. Assumes
	 * no parameters.
	 * @param outFile The target file
	 * @param inputXSL The stylesheet to use for the transformation
	 */
	private void generatePage(File outFile, String inputXSL) throws TransformerException {
		generatePage(outFile, inputXSL, null);
	}

	/**
	 * Generates the given page dynamically, by running the summary document through the given XSLT transform.
	 * @param outFile The target file.
	 * @param inputXSL The stylesheet to use for the transformation.
	 * @param parameters String key and Object value pairs to pass to the transformation.
	 */
	private void generatePage(File outFile, String inputXSL, Map<String, String> parameters) throws TransformerException {
		InputStream xsl = getResourceAsStream(inputXSL);
		Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsl));

		if (parameters != null) {
			Iterator<String> params = parameters.keySet().iterator();

			while (params.hasNext()) {
				String key = params.next();
				Object value = parameters.get(key);
				transformer.setParameter(key, value);
			}
		}

		transformer.transform(new DOMSource(allTaglibs), new StreamResult(outFile));
	}

	/**
	 * Copy the given resource to the given output file. The classloader
	 * that loaded TLDDocGenerator will be used to find the resource.
	 * If xsltDirectory is not null, the files are copied from that
	 * directory instead.
	 * @param outputFile The destination file
	 * @param resource The resource to copy, starting with '/'
	 */
	private static void copyResourceToFile(File outputFile, String resource) throws IOException {
		InputStream in = null;
		OutputStream out = null;

		try {
			in = getResourceAsStream(resource);
			out = new FileOutputStream(outputFile);
			byte[] buffer = new byte[1024];

			for (int length = 0; (length = in.read(buffer)) != -1;) {
				out.write(buffer, 0, length);
			}
		}
		finally {
			if (out != null) try { out.close(); } catch (IOException ignore) { /**/ }
			if (in != null ) try { in.close(); } catch (IOException ignore) { /**/ }
		}
	}

	/**
	 * If xsltDirectory is null, obtains an InputStream of the given
	 * resource from RESOURCE_PATH, using the class loader that loaded
	 * TLDDocGenerator. Otherwise, finds the file in xsltDirectory and
	 * defaults to the default stylesheet if it has not been overridden.
	 * @param resource, must start with RESOURCE_PATH.
	 * @return An InputStream for the given resource.
	 */
	private static InputStream getResourceAsStream(String resource) {
		return VdldocGenerator.class.getResourceAsStream(resource);
	}

	/**
	 * Outputs the given message to stdout, only if !quiet
	 */
	private void println(String message) {
		if (!quiet) {
			System.out.println(message);
		}
	}

	/**
	 * Displays a "success" message.
	 */
	private void outputSuccessMessage() {
		println("\nDocumentation generated.");
	}

}