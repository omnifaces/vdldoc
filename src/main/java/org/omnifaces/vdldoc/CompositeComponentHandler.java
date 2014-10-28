/*
 * Copyright (c) 2013, OmniFaces
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

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Neil Griffin
 * @author Vernon Singleton
 * @author Kyle Stiemann
 */
public class CompositeComponentHandler extends DefaultHandler {

	// Private Constants
	private static final String ATTRIBUTE = "attribute";
	private static final String COMPONENT = "component";
	private static final String COMPONENT_TYPE = "component-type";
	private static final String COMPOSITE_NAMESPACE_SUN = "http://java.sun.com/jsf/composite";
	private static final String COMPOSITE_NAMESPACE_JCP = "http://xmlns.jcp.org/jsf/composite";
	private static final Set<String> COMPOSITE_NAMESPACES = unmodifiableSet(new HashSet<String>(asList(COMPOSITE_NAMESPACE_SUN, COMPOSITE_NAMESPACE_JCP)));
	private static final String DEPRECATED = "deprecated";
	private static final String DEPRECATION ="deprecation";
	private static final String DESCRIPTION = "description";
	private static final String DISPLAY_NAME = "displayName";
	private static final String EDITABLE_VALUE_HOLDER = "editableValueHolder";
	private static final String EXAMPLE_URL = "example-url";
	private static final String EXTENSION = "extension";
	private static final String FACELET_COMPOSITE_COMPONENT = "Facelet Composite Component";
	private static final String INTERFACE = "interface";
	private static final String NAME = "name";
	private static final String REQUIRED = "required";
	private static final String SHORT_DESCRIPTION = "shortDescription";
	private static final String SINCE = "since";
	private static final String TAG = "tag";
	private static final String TAG_EXTENSION = "tag-extension";
	private static final String TAG_NAME = "tag-name";
	private static final String TYPE = "type";
	private static final String VALUE = "value";
	private static final String VALUE_HOLDER = "valueHolder";
	private static final String VDLDOC_NAMESPACE = "http://vdldoc.omnifaces.org";

	// Private Data Members
	private String componentName;
	private Document document;
	private String namespaceURI;
	private boolean valueGiven;
	private boolean valueHolder;
	private Node tagNode;
	private Node taglibNode;
	private Node tagExtensionNode;

	private HashMap<String,ImpliedAttribute> attributeMap;

	public CompositeComponentHandler(String componentName, Document document, String namespaceURI, Node taglibNode, HashMap<String,ImpliedAttribute> properties) {
		this.componentName = componentName;
		this.document = document;
		this.namespaceURI = namespaceURI;
		this.taglibNode = taglibNode;
		attributeMap = properties;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (uri != null && localName != null) {

			if (COMPOSITE_NAMESPACES.contains(uri) && localName.equals(INTERFACE)) {
				if (valueHolder) {
					if (valueGiven) {
						System.out.println("INFO: valueHolder = " + valueHolder + ", but valueGiven = " + valueGiven +
							". Since the xhtml declares a value attribute, we are not adding the implied value attribute for this composite component to the Vdldoc."
						);
					} else {
						addImpliedAttribute(tagNode, "value", "value", "The current value of this component.", "false", "java.lang.Object");
					}
				}

				tagNode = null;

			} else if (COMPOSITE_NAMESPACES.contains(uri) && localName.equals(EXTENSION)) {
				tagExtensionNode = null;
			}
		}
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
		return new InputSource(new StringReader(""));
	}

	@Override
	public void startElement(String uri, String localName, String elementName, Attributes attributes)
		throws SAXException {

		if (uri != null && localName != null) {

			if (COMPOSITE_NAMESPACES.contains(uri) && localName.equals(INTERFACE)) {
				valueGiven = false;
				Element tagElement = document.createElementNS(namespaceURI, TAG);
				tagNode = taglibNode.appendChild(tagElement);

				String shortDescription = attributes.getValue(SHORT_DESCRIPTION);
				if (shortDescription != null) {
					Element shortDescriptionElement = document.createElementNS(namespaceURI, DESCRIPTION);
					shortDescriptionElement.setTextContent(shortDescription);
					tagNode.appendChild(shortDescriptionElement);
				}

				Element tagNameElement = document.createElementNS(namespaceURI, TAG_NAME);
				tagNameElement.setTextContent(componentName);
				tagNode.appendChild(tagNameElement);

				Element componentElement = document.createElementNS(namespaceURI, COMPONENT);
				Element componentTypeElement = document.createElementNS(namespaceURI, COMPONENT_TYPE);
				componentTypeElement.setTextContent(FACELET_COMPOSITE_COMPONENT);
				componentElement.appendChild(componentTypeElement);
				tagNode.appendChild(componentElement);

				for (Map.Entry<String, ImpliedAttribute> entry : attributeMap.entrySet()) {
				    String name = entry.getKey();
				    ImpliedAttribute attribute = entry.getValue();
				    addImpliedAttribute(
				    	tagNode,
				    	name,
				    	attribute.getDisplayName(),
				    	attribute.getDescription(),
				    	attribute.getRequired(),
				    	attribute.getType()
				    );
				}

			}
			else if (COMPOSITE_NAMESPACES.contains(uri) && localName.equals(ATTRIBUTE)) {

				if (tagNode != null) {

					Node attributeNode = tagNode.appendChild(document.createElementNS(namespaceURI, ATTRIBUTE));

					// description
					String description = attributes.getValue(SHORT_DESCRIPTION);

					if (description != null) {
						Element descriptionElement = document.createElementNS(namespaceURI, DESCRIPTION);
						descriptionElement.setTextContent(description);
						attributeNode.appendChild(descriptionElement);
					}

					// displayName
					String displayName = attributes.getValue(DISPLAY_NAME);

					if (displayName != null) {
						Element displayNameElement = document.createElementNS(namespaceURI, DISPLAY_NAME);
						displayNameElement.setTextContent(displayName);
						attributeNode.appendChild(displayNameElement);
					}

					// name
					Element nameElement = document.createElementNS(namespaceURI, NAME);
					String name = attributes.getValue(NAME);
					nameElement.setTextContent(name);
					attributeNode.appendChild(nameElement);

					if ("value".equals(name)) {
						valueGiven = true;
					}

					// required
					String required = attributes.getValue(REQUIRED);

					if (required == null) {
						required = Boolean.FALSE.toString();
					}

					Element requiredElement = document.createElementNS(namespaceURI, REQUIRED);
					requiredElement.setTextContent(required);
					attributeNode.appendChild(requiredElement);

					// type
					String type = attributes.getValue(TYPE);

					if (type == null) {
						type = String.class.getName();
					}

					Element typeElement = document.createElementNS(namespaceURI, TYPE);
					typeElement.setTextContent(type);
					attributeNode.appendChild(typeElement);

				}
			} else if (COMPOSITE_NAMESPACES.contains(uri) && localName.equals(EXTENSION)) {
				if (tagNode != null) {
					tagExtensionNode = tagNode.appendChild(document.createElementNS(namespaceURI, TAG_EXTENSION));
				}
			} else if (uri.equals(VDLDOC_NAMESPACE) && localName.equals(SINCE)) {

				if (tagExtensionNode != null) {

					String since = attributes.getValue(VALUE);

					if (since != null) {

						Element sinceElement = document.createElementNS(VDLDOC_NAMESPACE, SINCE);
						sinceElement.setTextContent(since);
						tagExtensionNode.appendChild(sinceElement);
					}
				}
			} else if (uri.equals(VDLDOC_NAMESPACE) && localName.equals(EXAMPLE_URL)) {

				if (tagExtensionNode != null) {

					String exampleURL = attributes.getValue(VALUE);

					if (exampleURL != null) {

						Element exampleURLElement = document.createElementNS(VDLDOC_NAMESPACE, EXAMPLE_URL);
						exampleURLElement.setTextContent(exampleURL);
						tagExtensionNode.appendChild(exampleURLElement);
					}
				}
			} else if (uri.equals(VDLDOC_NAMESPACE) && localName.equals(DEPRECATED)) {

				if (tagExtensionNode != null) {

					String deprecatedValue = attributes.getValue(VALUE);

					if (deprecatedValue != null) {

						Boolean deprecated = Boolean.parseBoolean(deprecatedValue);
						Element deprecationElement = document.createElementNS(VDLDOC_NAMESPACE, DEPRECATION);
						Element deprecatedElement = document.createElementNS(VDLDOC_NAMESPACE, DEPRECATED);
						deprecatedElement.setTextContent(deprecated.toString());
						deprecationElement.appendChild(deprecatedElement);

						String descriptionValue = attributes.getValue(SHORT_DESCRIPTION);

						if (descriptionValue != null) {
							Element descriptionElement = document.createElementNS(VDLDOC_NAMESPACE, DESCRIPTION);
							descriptionElement.setTextContent(descriptionValue);
							deprecationElement.appendChild(descriptionElement);
						}

						tagExtensionNode.appendChild(deprecationElement);
					}
				}
			} else if (COMPOSITE_NAMESPACES.contains(uri) && localName.equals(VALUE_HOLDER)) {
				if (tagNode != null) {
					valueHolder = true;
				}
			} else if (COMPOSITE_NAMESPACES.contains(uri) && localName.equals(EDITABLE_VALUE_HOLDER)) {
				if (tagNode != null) {
					valueHolder = true;
				}
			}
		}
	}

	public void addImpliedAttribute(Node node, String name, String displayName, String description, String required, String type) {
		// append default attributes
		Node attributeNode = node.appendChild(document.createElementNS(namespaceURI, ATTRIBUTE));

		// name
		Element nameElement = document.createElementNS(namespaceURI, NAME);
		nameElement.setTextContent(name);
		attributeNode.appendChild(nameElement);

		// display name
		Element displayNameElement = document.createElementNS(namespaceURI, DISPLAY_NAME);
		displayNameElement.setTextContent(displayName);
		attributeNode.appendChild(displayNameElement);

		// description
		Element descriptionElement = document.createElementNS(namespaceURI, DESCRIPTION);
		descriptionElement.setTextContent(description);
		attributeNode.appendChild(descriptionElement);

		// required
		Element requiredElement = document.createElementNS(namespaceURI, REQUIRED);
		requiredElement.setTextContent(required);
		attributeNode.appendChild(requiredElement);

		// type
		Element typeElement = document.createElementNS(namespaceURI, TYPE);
		typeElement.setTextContent(type);
		attributeNode.appendChild(typeElement);
	}

}
