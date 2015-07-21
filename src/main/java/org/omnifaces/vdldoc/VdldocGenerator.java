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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Attr;
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

	/** The sun.com XML namespace for Java EE. */
	private static final String NS_JAVAEE_SUN = "http://java.sun.com/xml/ns/javaee";

	/** The jcp.org XML namespace for Java EE. @since 2.0 */
	private static final String NS_JAVAEE_JCP = "http://xmlns.jcp.org/xml/ns/javaee";

	/** If true, outputs the input to the transform before generation. For internal use only. */
	private static final boolean DEBUG_INPUT_DOCUMENT = false;

	/** Error messages */
	private static final String ERROR_INVALID_TAGLIB =
		"%s is not a .taglib.xml file.";
	private static final String ERROR_INVALID_FACES_CONFIG =
		"%s is not a faces-config.xml file.";
	private static final String ERROR_INVALID_ATTRIBUTES_FILE =
		"%s is not a valid attributes file.";
	private static final String ERROR_NS_JAVAEE_MISSING =
		"%s does not have xmlns=\"" + NS_JAVAEE_JCP + "\"";
	private static final String ERROR_TAGLIB_MISSING =
		"%s does not have <facelet-taglib> as root.";
	private static final String ERROR_INVALID_COMPOSITELIB =
		"%s can not have more than one <composite-library-name>.";
	private static final String WARNING_NO_TAGS =
		"WARNING: %s does not have any <tag>s or <function>s. Skipping!";
	private static final String WARNING_OLD_NS_JAVAEE =
		"WARNING: %s uses old java.sun.com XML namespace. It's recommend to upgrade it to xmlns.jcp.org... ";
	private static final String WARNING_ID_MISSING =
		"WARNING: %s does not have <facelet-taglib id> attribute. Defaulting to base filename '%s'... ";
	private static final String WARNING_UNSUPPORTED_ATTRIBUTE =
		"WARNING: '%s' is not a supported composite attribute. Skipping!";
	private static final String WARNING_INVALID_ATTRIBUTE =
		"WARNING: '%s' is not in format 'name.attribute=value'. Skipping!";
	private static final String ERROR_DUPLICATE_ID =
		"Two tag libraries exist with the same <facelet-taglib id> attribute '%s'. This is not supported.";

	// Properties -----------------------------------------------------------------------------------------------------

	/** The set of tag library files we are parsing. */
	private Set<File> taglibs = new LinkedHashSet<File>();

	/** The faces config file we are parsing. */
	private File facesConfig;

	/** The output directory for generated files. */
	private File outputDirectory = new File("vdldoc");

	/** The browser window title for the VDL documentation. */
	private String windowTitle = DEFAULT_WINDOW_TITLE;

	/** The title for the VDL index page. */
	private String docTitle = DEFAULT_DOC_TITLE;

	/** The properties file for implied attributes of composite components. */
	private File attributesFile;

	/** True if "Output generated by Vdldoc" footer must be hidden. @since 2.1 */
	private boolean hideGeneratedBy;

	/** True if no stdout is to be produced during generation. */
	private boolean quiet;

	/** The summary VDL document, used as input into XSLT. */
	private Document summaryDocument;

	/** The mapping of implied attributes of composite components. */
	private HashMap<String, ImpliedAttribute> compositeAttributeMap;

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
	 * @throws IllegalArgumentException When the file does not exist, or is not a file, or does not have the extension
	 * <tt>.taglib.xml</tt>.
	 */
	public void addTaglib(File taglib) {
		if (!taglib.exists() || !taglib.isFile() || !taglib.getName().toLowerCase().endsWith(".taglib.xml")) {
			throw new IllegalArgumentException(String.format(ERROR_INVALID_TAGLIB, taglib.getAbsolutePath()));
		}

		taglibs.add(taglib);
	}

	/**
	 * Set the given individual faces config file.
	 * @param facesConfig The faces config file to set.
	 * @throws IllegalStateException When the faces config file is already been set.
	 * @throws IllegalArgumentException When the file does not exist, or is not a file, or does not have the name
	 * <tt>faces-config.xml</tt>.
	 */
	public void setFacesConfig(File facesConfig) {
		if (!facesConfig.exists() || !facesConfig.isFile() || !facesConfig.getName().equals("faces-config.xml")) {
			throw new IllegalArgumentException(String.format(ERROR_INVALID_FACES_CONFIG, facesConfig.getAbsolutePath()));
		}

		this.facesConfig = facesConfig;
	}

	/**
	 * Sets the given properties file for implied attributes of composite components.
	 * If not specified, defaults to "cc-attributes.properties".
	 * @param attributesFile The attributes file to set.
	 */
	public void setAttributes(File attributesFile) {
		if (!attributesFile.exists() || !attributesFile.isFile()) {
			throw new IllegalArgumentException(ERROR_INVALID_ATTRIBUTES_FILE);
		}

		this.attributesFile = attributesFile;
	}

	/**
	 * Sets the output directory for generated files. If not specified, defaults to "./vdldoc"
	 * @param outputDirectory The base directory for generated files.
	 */
	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	/**
	 * Sets the browser window title for the documentation.
	 * @param windowTitle The browser window title
	 */
	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	/**
	 * Sets the title for the VDL documentation index page.
	 * @param docTitle The title for the VDL documentation index page.
	 */
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

	/**
	 * Sets whether "Output generated by Vdldoc" footer must be hidden.
	 * @param hideGeneratedBy True if "Output generated by Vdldoc" footer must be hidden.
	 */
	public void setHideGeneratedBy(boolean hideGeneratedBy) {
		this.hideGeneratedBy = hideGeneratedBy;
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
			loadCompositeAttributeMap();
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
	 * Loads the composite component attribute map.
	 */
	private void loadCompositeAttributeMap() throws IOException {
		compositeAttributeMap = new HashMap<String, ImpliedAttribute>();
		Properties properties = new Properties();

		if (attributesFile == null) { // use the default
			properties.load(getResourceAsStream("cc-attributes.properties"));
		}
		else { // use the specified file
			print(String.format("Parsing %s file... ", attributesFile.getName()));
			FileInputStream input = new FileInputStream(attributesFile);

			try {
				properties.load(input);
			}
			finally {
				try { input.close(); } catch (IOException ignore) { /**/ }
			}
		}

		parseCompositeAttributeMap(properties, attributesFile != null);
	}

	/**
	 * Parses the composite component attribute properties file.
	 */
	private void parseCompositeAttributeMap(Properties properties, boolean log) {
		boolean warned = false;

		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);
			String name = "";
			String attributeName = "";
			String[] tokens = key.split("\\.");

			if (tokens.length == 2) {
				name = tokens[0];
				attributeName = tokens[1];
				ImpliedAttribute attribute = compositeAttributeMap.get(name);

				if (attribute == null) { // create a new one
					attribute = new ImpliedAttribute();
					attribute.setDisplayName(name);
					attribute.setDescription(name);
					attribute.setRequired("false");
					attribute.setType("java.lang.String");
				}

				if ("displayName".equals(attributeName)) {
					attribute.setDisplayName(value);
				}
				else if ("description".equals(attributeName)) {
					attribute.setDescription(value);
				}
				else if ("required".equals(attributeName)) {
					attribute.setRequired(value);
				}
				else if ("type".equals(attributeName)) {
					attribute.setType(value);
				}
				else if (log) {
					warned = true;
					println("");
					print(String.format(WARNING_UNSUPPORTED_ATTRIBUTE, key));
				}

				compositeAttributeMap.put(name, attribute);
			}

			else if (log) {
				warned = true;
				println("");
				print(String.format(WARNING_INVALID_ATTRIBUTE, key));
			}
		}

		if (log) {
			println(warned ? "" : "OK!");
		}
	}

	/**
	 * Creates a summary document, comprising all input taglibs. This document is later used as input into XSLT to
	 * generate all non-static output pages. Stores the result as a DOM tree in the <code>allTaglibs</code> property.
	 */
	private void createSummaryDoc()
		throws IOException, SAXException, ParserConfigurationException, TransformerException
	{
		DocumentBuilder builder = createDocumentBuilder();
		summaryDocument = builder.newDocument();

		// Create root <vdldoc> root element:
		Element vdldocElement = summaryDocument.createElementNS(NS_JAVAEE_JCP, "vdldoc");
		summaryDocument.appendChild(vdldocElement);

		// Create configuration element <config>:
		Element configElement = summaryDocument.createElementNS(NS_JAVAEE_JCP, "config");
		vdldocElement.appendChild(configElement);
		configElement.setAttribute("hide-generated-by", String.valueOf(hideGeneratedBy));

		Element windowTitle = summaryDocument.createElementNS(NS_JAVAEE_JCP, "window-title");
		windowTitle.appendChild(summaryDocument.createTextNode(this.windowTitle));
		configElement.appendChild(windowTitle);

		Element docTitle = summaryDocument.createElementNS(NS_JAVAEE_JCP, "doc-title");
		docTitle.appendChild(summaryDocument.createTextNode(this.docTitle));
		configElement.appendChild(docTitle);

		// Append <faces-config> element from faces config file to root:
		if (facesConfig != null) {
			print("Parsing faces-config.xml file... ");

			Document doc = parse(builder, facesConfig);
			Node facesConfigNode = summaryDocument.importNode(doc.getDocumentElement(), true);
			vdldocElement.appendChild(facesConfigNode);

			println("OK!");
		}

		// Append each <facelet-taglib> element from each taglib file to root:
		for (File taglib : taglibs) {
			print("Parsing " + taglib.getName() + " file... ");

			Document document = parse(builder, taglib);
			Element element = document.getDocumentElement();

			NodeList compositeNodes = element.getElementsByTagNameNS("*", "composite-library-name");
			NodeList tagNodes = element.getElementsByTagNameNS("*", "tag");
			NodeList functionNodes = element.getElementsByTagNameNS("*", "function");
			NodeList taglibExtensionNodes = element.getElementsByTagNameNS("*", "el-variable");
			int numTags = compositeNodes.getLength() + functionNodes.getLength() + tagNodes.getLength() + taglibExtensionNodes.getLength();

			// If this tag library has no composite libraries, tags or functions, skip it.
			if (numTags > 0) {
				Element taglibNode = (Element) summaryDocument.importNode(element, true);

				if (!taglibNode.getNamespaceURI().equals(NS_JAVAEE_JCP)) {
					throw new IllegalArgumentException(String.format(ERROR_NS_JAVAEE_MISSING, taglib.getName()));
				}

				if (!taglibNode.getLocalName().equals("facelet-taglib")) {
					throw new IllegalArgumentException(String.format(ERROR_TAGLIB_MISSING, taglib.getName()));
				}

				String id = taglibNode.getAttribute("id");

				if (id == null || id.trim().isEmpty()) {
					id = taglib.getName().substring(0, taglib.getName().indexOf('.'));
					taglibNode.setAttribute("id", id);
					print(String.format(WARNING_ID_MISSING, taglib.getName(), id));
				}

				vdldocElement.appendChild(taglibNode);

				if (compositeNodes.getLength() > 0) {
					if (compositeNodes.getLength() > 1) {
						throw new IllegalArgumentException(String.format(ERROR_INVALID_COMPOSITELIB, taglib.getName()));
					}

					String compositeLibraryName = compositeNodes.item(0).getTextContent();
					File parentFolder = taglib.getParentFile(); // This is WEB-INF in WAR and META-INF in JAR.

					if (parentFolder.getName().equals("WEB-INF")) {
						parentFolder = parentFolder.getParentFile();
					}

					File resourcesFolder = new File(parentFolder, "resources");
					File compositeLibraryFolder = new File(resourcesFolder, compositeLibraryName);
					File[] compositeComponentFiles = compositeLibraryFolder.listFiles(new FileFilter() {
						@Override
						public boolean accept(File pathname) {
							return (pathname != null) && (pathname.getName().endsWith(".xhtml"));
						}
					});

					if (compositeComponentFiles != null) {
						for (File compositeComponentFile : compositeComponentFiles) {
							parseCompositeComponentFile(NS_JAVAEE_JCP, taglibNode, compositeComponentFile);
							vdldocElement.appendChild(taglibNode);
						}
					}
				}

				println("OK!");
			}
			else {
				println(String.format(WARNING_NO_TAGS, taglib.getName()));
			}
		}

		// If debug enabled, output the resulting document, as a test:
		if (DEBUG_INPUT_DOCUMENT) {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(summaryDocument), new StreamResult(System.out));
		}
	}

	/**
	 * Parse composite component file.
	 */
	private void parseCompositeComponentFile(String namespaceURI, Node taglibNode, File inputFile)
		throws ParserConfigurationException, SAXException, IOException
	{
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setValidating(false);
		saxParserFactory.setNamespaceAware(true);
		SAXParser saxParser = saxParserFactory.newSAXParser();

		String inputFileName = inputFile.getName();
		String componentName = inputFileName.substring(0, inputFileName.lastIndexOf(".xhtml"));
		saxParser.parse(inputFile, new CompositeComponentHandler(
			componentName, summaryDocument, namespaceURI, taglibNode, compositeAttributeMap));
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
		Element root = summaryDocument.getDocumentElement();
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

			NodeList elVariables = taglib.getElementsByTagNameNS("*", "el-variable");
			int numELVariables = elVariables.getLength();

			for (int j = 0; j < numELVariables; j++) {
				Element elVariable = (Element) elVariables.item(j);
				String elVariableName = findElementValue(elVariable, "el-variable-name");
				generateELVariableDetail(outputDirectory, id, elVariableName);
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

	private void generateELVariableDetail(File outputDirectory, String id, String elVariableName)
			throws TransformerException
	{
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("id", id);
		parameters.put("elVariableName", elVariableName);

		generatePage(new File(outputDirectory, elVariableName + ".el.html"), "elvariable.html.xsl", parameters);
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

		transformer.transform(new DOMSource(summaryDocument), new StreamResult(outputFile));
	}

	// Helpers --------------------------------------------------------------------------------------------------------

	/**
	 * Create the document builder.
	 * @return The document builder.
	 * @throws ParserConfigurationException
	 */
	private static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
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

		return documentBuilder;
	}

	/**
	 * Parse the given file into a document.
	 * @param builder The involved document builder.
	 * @param file The file to be parsed.
	 * @return The document.
	 */
	private Document parse(DocumentBuilder builder, File file) throws SAXException, IOException {
		FileInputStream in = new FileInputStream(file);

		try {
			Document document = builder.parse(new InputSource(in));

			// If this document still uses old java.sun.com XML namespace, change it to new xmlns.jcp.org one.
			if (document.getDocumentElement().getNamespaceURI().equals(NS_JAVAEE_SUN)) {
				print(String.format(WARNING_OLD_NS_JAVAEE, file.getName()));
				Document documentWithNewNS = builder.newDocument();
				changeNamespace(document, documentWithNewNS);
				return documentWithNewNS;
			}
			else {
				return document;
			}
		}
		finally {
			try { in.close(); } catch (IOException ignore) { /**/ }
		}
	}

	/**
	 * Change the XML namespace of the node from {@value #NS_JAVAEE_SUN} to {@value #NS_JAVAEE_JCP}.
	 * @param from The source node.
	 * @param to The target node.
	 */
	private static void changeNamespace(Node from, Node to) {
		NodeList children = from.getChildNodes();
		Document document = (to.getNodeType() == Node.DOCUMENT_NODE) ? (Document) to : to.getOwnerDocument();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			changeNamespace(node, cloneAndChangeNamespace(document, node, to));
		}
	}

	/**
	 * Clone the document node and change the XML namespace from {@value #NS_JAVAEE_SUN} to {@value #NS_JAVAEE_JCP}.
	 * @param document The target document.
	 * @param from The source node.
	 * @param to The target node.
	 * @return The cloned node.
	 */
	private static Node cloneAndChangeNamespace(Document document, Node from, Node to) {
		if (from.getNodeType() == Node.ELEMENT_NODE) {
			Element clone = document.createElementNS(NS_JAVAEE_JCP, from.getNodeName());
			to.appendChild(clone);

			for (int i = 0; i < from.getAttributes().getLength(); i++) {
				Attr attr = (Attr) from.getAttributes().item(i);
				String value = attr.getValue();
				clone.setAttributeNS(attr.getNamespaceURI(), attr.getNodeName(), NS_JAVAEE_SUN.equals(value) ? NS_JAVAEE_JCP : value);
			}

			return clone;
		}
		else {
			Node clone = from.cloneNode(false);
			document.adoptNode(clone);
			to.appendChild(clone);
			return clone;
		}
	}

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
			if (out != null) {
				try { out.close(); } catch (IOException ignore) { /**/ }
			}
			if (in != null ) {
				try { in.close(); } catch (IOException ignore) { /**/ }
			}
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