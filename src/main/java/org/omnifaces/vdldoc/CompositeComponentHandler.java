/*
 * Copyright (c) OmniFaces
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
    private static final String COMPOSITE_NAMESPACE_JEE = "https://jakarta.ee/xml/ns/jakartaee";
    private static final Set<String> COMPOSITE_NAMESPACES = unmodifiableSet(new HashSet<>(asList(COMPOSITE_NAMESPACE_SUN, COMPOSITE_NAMESPACE_JCP, COMPOSITE_NAMESPACE_JEE)));
    private static final String DEPRECATED = "deprecated";
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

    /**
     * Creates a new handler for parsing a composite component XHTML file.
     * @param componentName The name of the composite component (derived from the filename).
     * @param document The summary DOM document to append elements to.
     * @param namespaceURI The target XML namespace URI.
     * @param taglibNode The parent taglib node to append the tag element to.
     * @param properties The map of implied attributes to add to composite components.
     */
    public CompositeComponentHandler(String componentName, Document document, String namespaceURI, Node taglibNode, HashMap<String,ImpliedAttribute> properties) {
        this.componentName = componentName;
        this.document = document;
        this.namespaceURI = namespaceURI;
        this.taglibNode = taglibNode;
        attributeMap = properties;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if ((uri != null) && (localName != null)) {

            if (COMPOSITE_NAMESPACES.contains(uri) && INTERFACE.equals(localName)) {
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

            } else if (COMPOSITE_NAMESPACES.contains(uri) && EXTENSION.equals(localName)) {
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

        if ((uri != null) && (localName != null)) {

            if (COMPOSITE_NAMESPACES.contains(uri) && INTERFACE.equals(localName)) {
                valueGiven = false;
                var tagElement = document.createElementNS(namespaceURI, TAG);
                tagNode = taglibNode.appendChild(tagElement);

                var shortDescription = attributes.getValue(SHORT_DESCRIPTION);
                if (shortDescription != null) {
                    var shortDescriptionElement = document.createElementNS(namespaceURI, DESCRIPTION);
                    shortDescriptionElement.setTextContent(shortDescription);
                    tagNode.appendChild(shortDescriptionElement);
                }

                var tagNameElement = document.createElementNS(namespaceURI, TAG_NAME);
                tagNameElement.setTextContent(componentName);
                tagNode.appendChild(tagNameElement);

                var componentElement = document.createElementNS(namespaceURI, COMPONENT);
                var componentTypeElement = document.createElementNS(namespaceURI, COMPONENT_TYPE);
                componentTypeElement.setTextContent(FACELET_COMPOSITE_COMPONENT);
                componentElement.appendChild(componentTypeElement);
                tagNode.appendChild(componentElement);

                for (Map.Entry<String, ImpliedAttribute> entry : attributeMap.entrySet()) {
                    var name = entry.getKey();
                    var attribute = entry.getValue();
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
            else if (COMPOSITE_NAMESPACES.contains(uri) && ATTRIBUTE.equals(localName)) {

                if (tagNode != null) {

                    var attributeNode = tagNode.appendChild(document.createElementNS(namespaceURI, ATTRIBUTE));

                    // description
                    var description = attributes.getValue(SHORT_DESCRIPTION);

                    if (description != null) {
                        var descriptionElement = document.createElementNS(namespaceURI, DESCRIPTION);
                        descriptionElement.setTextContent(description);
                        attributeNode.appendChild(descriptionElement);
                    }

                    // displayName
                    var displayName = attributes.getValue(DISPLAY_NAME);

                    if (displayName != null) {
                        var displayNameElement = document.createElementNS(namespaceURI, DISPLAY_NAME);
                        displayNameElement.setTextContent(displayName);
                        attributeNode.appendChild(displayNameElement);
                    }

                    // name
                    var nameElement = document.createElementNS(namespaceURI, NAME);
                    var name = attributes.getValue(NAME);
                    nameElement.setTextContent(name);
                    attributeNode.appendChild(nameElement);

                    if ("value".equals(name)) {
                        valueGiven = true;
                    }

                    // required
                    var required = attributes.getValue(REQUIRED);

                    if (required == null) {
                        required = Boolean.FALSE.toString();
                    }

                    var requiredElement = document.createElementNS(namespaceURI, REQUIRED);
                    requiredElement.setTextContent(required);
                    attributeNode.appendChild(requiredElement);

                    // type
                    var type = attributes.getValue(TYPE);

                    if (type == null) {
                        type = String.class.getName();
                    }

                    var typeElement = document.createElementNS(namespaceURI, TYPE);
                    typeElement.setTextContent(type);
                    attributeNode.appendChild(typeElement);

                }
            } else if (COMPOSITE_NAMESPACES.contains(uri) && EXTENSION.equals(localName)) {
                if (tagNode != null) {
                    tagExtensionNode = tagNode.appendChild(document.createElementNS(namespaceURI, TAG_EXTENSION));
                }
            } else if (isVdldocNamespace(uri) && SINCE.equals(localName)) {

                if (tagExtensionNode != null) {

                    var since = attributes.getValue(VALUE);

                    if (since != null) {

                        var sinceElement = document.createElementNS(VdldocGenerator.NS_VDLDOC, SINCE);
                        sinceElement.setTextContent(since);
                        tagExtensionNode.appendChild(sinceElement);
                    }
                }
            } else if (isVdldocNamespace(uri) && EXAMPLE_URL.equals(localName)) {

                if (tagExtensionNode != null) {

                    var exampleURL = attributes.getValue(VALUE);

                    if (exampleURL != null) {

                        var exampleURLElement = document.createElementNS(VdldocGenerator.NS_VDLDOC, EXAMPLE_URL);
                        exampleURLElement.setTextContent(exampleURL);
                        tagExtensionNode.appendChild(exampleURLElement);
                    }
                }
            } else if (isVdldocNamespace(uri) && DEPRECATED.equals(localName)) {

                if (tagExtensionNode != null) {

                    var deprecatedElement = document.createElementNS(VdldocGenerator.NS_VDLDOC, DEPRECATED);
                    var deprecatedShortDescription = attributes.getValue(SHORT_DESCRIPTION);
                    var deprecatedValue = attributes.getValue(VALUE);

                    if (deprecatedShortDescription != null) {
                        deprecatedElement.setTextContent(deprecatedShortDescription);
                    }
                    else if (deprecatedValue != null) {
                        deprecatedElement.setTextContent(deprecatedValue);
                    }

                    tagExtensionNode.appendChild(deprecatedElement);
                }
            } else if (COMPOSITE_NAMESPACES.contains(uri) && VALUE_HOLDER.equals(localName)) {
                if (tagNode != null) {
                    valueHolder = true;
                }
            } else if ((COMPOSITE_NAMESPACES.contains(uri) && EDITABLE_VALUE_HOLDER.equals(localName)) && (tagNode != null)) {
                valueHolder = true;
            }
        }
    }

    /**
     * Adds an implied attribute element to the given parent node.
     * @param node The parent node to append the attribute to.
     * @param name The attribute name.
     * @param displayName The display name.
     * @param description The description.
     * @param required Whether the attribute is required.
     * @param type The fully qualified Java type.
     */
    public void addImpliedAttribute(Node node, String name, String displayName, String description, String required, String type) {
        // append default attributes
        var attributeNode = node.appendChild(document.createElementNS(namespaceURI, ATTRIBUTE));

        // name
        var nameElement = document.createElementNS(namespaceURI, NAME);
        nameElement.setTextContent(name);
        attributeNode.appendChild(nameElement);

        // display name
        var displayNameElement = document.createElementNS(namespaceURI, DISPLAY_NAME);
        displayNameElement.setTextContent(displayName);
        attributeNode.appendChild(displayNameElement);

        // description
        var descriptionElement = document.createElementNS(namespaceURI, DESCRIPTION);
        descriptionElement.setTextContent(description);
        attributeNode.appendChild(descriptionElement);

        // required
        var requiredElement = document.createElementNS(namespaceURI, REQUIRED);
        requiredElement.setTextContent(required);
        attributeNode.appendChild(requiredElement);

        // type
        var typeElement = document.createElementNS(namespaceURI, TYPE);
        typeElement.setTextContent(type);
        attributeNode.appendChild(typeElement);
    }

    private static boolean isVdldocNamespace(String uri) {
        return VdldocGenerator.NS_VDLDOC.equals(uri) || VdldocGenerator.NS_VDLDOC_OLD.equals(uri);
    }

}
