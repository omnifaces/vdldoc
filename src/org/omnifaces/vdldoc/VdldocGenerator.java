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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
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

	/** If true, outputs the input to the transform before generation. For internal use only. */
	private static final boolean DEBUG_INPUT_DOCUMENT = false;

	/** Error messages */
	private static final String ERROR_JAVAEE_MISSING =
		"%s does not have xmlns=\"" + NS_JAVAEE + "\"";
	private static final String ERROR_TAGLIB_MISSING =
		"%s does not have <facelet-taglib> as root.";
	private static final String WARNING_ID_MISSING =
		"WARNING: %s does not have <facelet-taglib id> attribute. Defaulting to base filename '%s'... ";
	private static final String ERROR_DUPLICATE_ID =
		"Two tag libraries exist with the same <facelet-taglib id> attribute '%s'. This is not supported.";

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
	private Document summary;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Creates a new VdldocGenerator.
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
	 * Sets the output directory for generated files. If not specified, defaults to "."
	 * @param dir The base directory for generated files.
	 */
	public void setOutputDirectory(File dir) {
		this.outputDirectory = dir;
	}

	/**
	 * Sets the browser window title for the documentation.
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
	 * Sets quiet mode (produce no stdout during generation).
	 * @param quiet True if no output is to be produced, false otherwise.
	 */
	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	/**
	 * Generate documentation.
	 */
	public void generate() throws IllegalArgumentException {
		try {
			createSummaryDoc();
			copyStaticFiles();
			generateOverview();
			generateTaglibDetail();
			println("VDL documentation generation is finished!");
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
		catch (TransformerException e) {
			throw new IllegalArgumentException(e);
		}
	}

	// Internal actions -----------------------------------------------------------------------------------------------

	/**
	 * Creates a summary document, comprising all input taglibs. This document is later used as input into XSLT to
	 * generate all non-static output pages. Stores the result as a DOM tree in the <code>allTaglibs</code> property.
	 */
	private void createSummaryDoc()
		throws IOException, SAXException, ParserConfigurationException, TransformerException
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

		summary = documentBuilder.newDocument();

		// Create root <facelet-taglibs> element:
		Element rootElement = summary.createElementNS(NS_JAVAEE, "facelet-taglibs");
		summary.appendChild(rootElement);

		// JDK 1.4 does not add xmlns for some reason - add it manually:
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", NS_JAVAEE);

		// Create configuration element <config>:
		Element configElement = summary.createElementNS(NS_JAVAEE, "config");
		rootElement.appendChild(configElement);

		Element windowTitle = summary.createElementNS(NS_JAVAEE, "window-title");
		windowTitle.appendChild(summary.createTextNode(this.windowTitle));
		configElement.appendChild(windowTitle);

		Element docTitle = summary.createElementNS(NS_JAVAEE, "doc-title");
		docTitle.appendChild(summary.createTextNode(this.docTitle));
		configElement.appendChild(docTitle);

		// Append each <facelet-taglib> element from each taglib to root:
		print("Parsing " + taglibs.size() + " taglib files... ");

		for (File taglib : taglibs) {
			FileInputStream in = new FileInputStream(taglib);
			Document doc;

			try {
				doc = documentBuilder.parse(new InputSource(in));
			}
			finally {
				try { in.close(); } catch (IOException ignore) { /**/ }
			}

			// If this tag library has no tags and no functions, omit it.
			int numTags = doc.getDocumentElement().getElementsByTagNameNS("*", "tag").getLength()
						+ doc.getDocumentElement().getElementsByTagNameNS("*", "function").getLength();

			if (numTags > 0) {
				Element taglibNode = (Element) summary.importNode(doc.getDocumentElement(), true);

				if (!taglibNode.getNamespaceURI().equals(NS_JAVAEE)) {
					throw new IllegalArgumentException(String.format(ERROR_JAVAEE_MISSING, taglib.getName()));
				}

				if (!taglibNode.getLocalName().equals("facelet-taglib")) {
					throw new IllegalArgumentException(String.format(ERROR_TAGLIB_MISSING, taglib.getName()));
				}

				String id = taglibNode.getAttribute("id");

				if (id == null || id.trim().isEmpty()) {
					id = taglib.getName().substring(0, taglib.getName().indexOf('.'));
					taglibNode.setAttribute("id", taglib.getName().substring(0, taglib.getName().indexOf('.')));
					print(String.format(WARNING_ID_MISSING, taglib.getName(), id));
				}

				rootElement.appendChild(taglibNode);
			}
		}

		println("OK!");

		// If debug enabled, output the resulting document, as a test:
		if (DEBUG_INPUT_DOCUMENT) {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(summary), new StreamResult(System.out));
		}
	}

	/**
	 * Copies all static files to target directory.
	 */
	private void copyStaticFiles() throws IOException {
		print("Copying static files... ");

		outputDirectory.mkdirs();
		copyResourceToFile("stylesheet.css", outputDirectory);
		File outputResourceDirectory = new File(outputDirectory, "resources");
		outputResourceDirectory.mkdirs();
		copyResourceToFile("background.gif", outputResourceDirectory);
		copyResourceToFile("tab.gif", outputResourceDirectory);
		copyResourceToFile("titlebar_end.gif", outputResourceDirectory);
		copyResourceToFile("titlebar.gif", outputResourceDirectory);

		println("OK!");
	}

	/**
	 * Generates all overview files, summarizing all TLDs.
	 */
	private void generateOverview() throws TransformerException {
		print("Generating overview pages... ");

		generatePage(new File(outputDirectory, "index.html"), "index.html.xsl");
		generatePage(new File(outputDirectory, "help-doc.html"), "help-doc.html.xsl");
		generatePage(new File(outputDirectory, "overview-frame.html"), "overview-frame.html.xsl");
		generatePage(new File(outputDirectory, "alltags-frame.html"), "alltags-frame.html.xsl");
		generatePage(new File(outputDirectory, "alltags-noframe.html"), "alltags-noframe.html.xsl");
		generatePage(new File(outputDirectory, "overview-summary.html"), "overview-summary.html.xsl");

		println("OK!");
	}

	/**
	 * Generates all the detail folders for each taglib.
	 */
	private void generateTaglibDetail() throws IllegalArgumentException, TransformerException {
		Set<String> ids = new HashSet<String>();
		Element root = summary.getDocumentElement();
		NodeList taglibs = root.getElementsByTagNameNS("*", "facelet-taglib");
		int size = taglibs.getLength();

		for (int i = 0; i < size; i++) {
			Element taglib = (Element) taglibs.item(i);
			String id = taglib.getAttribute("id");

			if (!ids.add(id)) {
				throw new IllegalArgumentException(String.format(ERROR_DUPLICATE_ID, id));
			}

			print("Generating docs for taglib '" + id + "'... ");
			File outputDirectory = new File(this.outputDirectory, id);
			outputDirectory.mkdir();

			// Generate information for each TLD:
			generateTaglibDetail(outputDirectory, id);

			// Generate information for each tag:
			NodeList tags = taglib.getElementsByTagNameNS("*", "tag");
			int numTags = tags.getLength();

			for (int j = 0; j < numTags; j++) {
				Element tag = (Element) tags.item(j);
				String tagName = findElementValue(tag, "tag-name");
				generateTagDetail(outputDirectory, id, tagName);
			}

			// Generate information for each function:
			NodeList functions = taglib.getElementsByTagNameNS("*", "function");
			int numFunctions = functions.getLength();

			for (int j = 0; j < numFunctions; j++) {
				Element function = (Element) functions.item(j);
				String functionName = findElementValue(function, "function-name");
				generateFunctionDetail(outputDirectory, id, functionName);
			}

			println("OK!");
		}
	}

	/**
	 * Generates the detail content for the tag library with the given display-name.
	 * Files will be placed in given output directory.
	 * @param outputDirectory The output directory.
	 * @param id The ID of the tag library.
	 */
	private void generateTaglibDetail(File outputDirectory, String id) throws TransformerException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("id", id);

		generatePage(new File(outputDirectory, "tld-frame.html"), "tld-frame.html.xsl", parameters);
		generatePage(new File(outputDirectory, "tld-summary.html"), "tld-summary.html.xsl", parameters);
	}

	/**
	 * Generates the detail content for the tag with the given name in the tag library with the given display-name.
	 * Files will be placed in given output directory.
	 * @param outputDirectory The output directory.
	 * @param id The ID of the tag library.
	 * @param tagName The name of the tag.
	 */
	private void generateTagDetail(File outputDirectory, String id, String tagName)
		throws TransformerException
	{
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("id", id);
		parameters.put("tagName", tagName);

		generatePage(new File(outputDirectory, tagName + ".html"), "tag.html.xsl", parameters);
	}

	/**
	 * Generates the detail content for the function with the given name in the tag library with the given display-name.
	 * Files will be placed in given output directory.
	 * @param outputDirectory The output directory.
	 * @param id The ID of the tag library.
	 * @param functionName The name of the function.
	 */
	private void generateFunctionDetail(File outputDirectory, String id, String functionName)
		throws TransformerException
	{
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("id", id);
		parameters.put("functionName", functionName);

		generatePage(new File(outputDirectory, functionName + ".fn.html"), "function.html.xsl", parameters);
	}

	/**
	 * Generates the given page dynamically, by running the summary document through the given XSLT transform. Assumes
	 * no parameters.
	 * @param outputFile The output file.
	 * @param inputXSL The file name of the input XSL.
	 */
	private void generatePage(File outputFile, String inputXSL) throws TransformerException {
		generatePage(outputFile, inputXSL, Collections.<String, String>emptyMap());
	}

	/**
	 * Generates the given page dynamically, by running the summary document through the given XSLT transform.
	 * @param outputFile The output file.
	 * @param inputXSL The file name of the input XSL.
	 * @param params The XSL parameters.
	 */
	private void generatePage(File outputFile, String inputXSL, Map<String, String> params)
		throws TransformerException
	{
		InputStream xsl = getResourceAsStream(inputXSL);
		Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsl));

		for (Map.Entry<String, String> entry : params.entrySet()) {
			transformer.setParameter(entry.getKey(), entry.getValue());
		}

		transformer.transform(new DOMSource(summary), new StreamResult(outputFile));
	}

	// Helpers --------------------------------------------------------------------------------------------------------

	/**
	 * Searches through the given element and returns the value of the body of the element. Returns null if the element
	 * was not found.
	 * @param parent The parent DOM element to search in.
	 * @param tagName The tag name of the DOM element to return the body for.
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
	 * Copy the given resource file to the given directory. The classloader that loaded VdldocGenerator will be used to
	 * find the resource.
	 * @param resourceName The file name of the resource file.
	 * @param outputDirectory The output directory.
	 */
	private static void copyResourceToFile(String resourceName, File outputDirectory) throws IOException {
		InputStream in = null;
		OutputStream out = null;

		try {
			in = getResourceAsStream(resourceName);
			out = new FileOutputStream(new File(outputDirectory, resourceName));
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
	 * Obtains an input stream of the given resource file using the class loader that loaded VdldocGenerator.
	 * @param resourceName The name of the resource file.
	 * @return An input stream of the resource file with the given name.
	 */
	private static InputStream getResourceAsStream(String resourceName) {
		return VdldocGenerator.class.getResourceAsStream(RESOURCE_PATH + "/" + resourceName);
	}

	// "Logging" ------------------------------------------------------------------------------------------------------

	/**
	 * Prints the given message to stdout, only if !quiet.
	 * @param message The message to be printed.
	 */
	private void print(String message) {
		if (!quiet) {
			System.out.print(message);
		}
	}

	/**
	 * Prints the given message with newline to stdout, only if !quiet.
	 * @param message The message to be printed.
	 */
	private void println(String message) {
		if (!quiet) {
			System.out.println(message);
		}
	}

}