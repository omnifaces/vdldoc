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

import static org.assertj.core.api.Assertions.assertThat;
import static org.omnifaces.vdldoc.VdldocGenerator.NS_JAKARTA_EE;
import static org.omnifaces.vdldoc.VdldocGenerator.NS_VDLDOC;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

class CompositeComponentHandlerTest {

    private Document document;
    private Element taglibNode;
    private HashMap<String, ImpliedAttribute> attributeMap;

    @BeforeEach
    void setUp() throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        var builder = factory.newDocumentBuilder();
        document = builder.newDocument();

        var root = document.createElementNS(NS_JAKARTA_EE, "vdldoc");
        document.appendChild(root);
        taglibNode = document.createElementNS(NS_JAKARTA_EE, "facelet-taglib");
        root.appendChild(taglibNode);

        attributeMap = new HashMap<>();
        var idAttr = new ImpliedAttribute();
        idAttr.setDisplayName("id");
        idAttr.setDescription("The identifier of the component.");
        idAttr.setRequired("false");
        idAttr.setType("java.lang.String");
        attributeMap.put("id", idAttr);

        var renderedAttr = new ImpliedAttribute();
        renderedAttr.setDisplayName("rendered");
        renderedAttr.setDescription("Whether to render the component.");
        renderedAttr.setRequired("false");
        renderedAttr.setType("java.lang.Boolean");
        attributeMap.put("rendered", renderedAttr);
    }

    private void parseXhtml(String xhtml) throws Exception {
        var saxFactory = SAXParserFactory.newInstance();
        saxFactory.setNamespaceAware(true);
        saxFactory.setValidating(false);
        var parser = saxFactory.newSAXParser();

        var handler = new CompositeComponentHandler(
            "testComponent", document, NS_JAKARTA_EE, taglibNode, attributeMap);

        parser.parse(new InputSource(new ByteArrayInputStream(xhtml.getBytes(StandardCharsets.UTF_8))), handler);
    }

    private NodeList getTagElements() {
        return taglibNode.getElementsByTagNameNS("*", "tag");
    }

    private static String getElementText(Element parent, String localName) {
        var list = parent.getElementsByTagNameNS("*", localName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return null;
    }

    @Nested
    class BasicCompositeInterface {

        @Test
        void parsesInterfaceAndCreatesTagElement() throws Exception {
            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tags = getTagElements();
            assertThat(tags.getLength()).isEqualTo(1);

            var tag = (Element) tags.item(0);
            assertThat(getElementText(tag, "tag-name")).isEqualTo("testComponent");
            assertThat(getElementText(tag, "component-type")).isEqualTo("Facelet Composite Component");
        }

        @Test
        void addsImpliedAttributesFromMap() throws Exception {
            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var attributes = tag.getElementsByTagNameNS("*", "attribute");

            // Should have implied attributes: id, rendered
            assertThat(attributes.getLength()).isGreaterThanOrEqualTo(2);
        }

        @Test
        void parsesShortDescription() throws Exception {
            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface shortDescription="A test composite component">
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            assertThat(getElementText(tag, "description")).isEqualTo("A test composite component");
        }

        @Test
        void emptyAttributeMapAddsNoImpliedAttributes() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var attributes = tag.getElementsByTagNameNS("*", "attribute");
            assertThat(attributes.getLength()).isEqualTo(0);
        }
    }

    @Nested
    class AttributeParsing {

        @Test
        void parsesExplicitAttribute() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface>
                    <cc:attribute name="label" required="true" type="java.lang.String"
                        shortDescription="The label text"/>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var attributes = tag.getElementsByTagNameNS("*", "attribute");
            assertThat(attributes.getLength()).isEqualTo(1);

            var attr = (Element) attributes.item(0);
            assertThat(getElementText(attr, "name")).isEqualTo("label");
            assertThat(getElementText(attr, "required")).isEqualTo("true");
            assertThat(getElementText(attr, "type")).isEqualTo("java.lang.String");
            assertThat(getElementText(attr, "description")).isEqualTo("The label text");
        }

        @Test
        void defaultsRequiredToFalse() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface>
                    <cc:attribute name="myAttr"/>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var attributes = tag.getElementsByTagNameNS("*", "attribute");
            var attr = (Element) attributes.item(0);
            assertThat(getElementText(attr, "required")).isEqualTo("false");
        }

        @Test
        void defaultsTypeToString() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface>
                    <cc:attribute name="myAttr"/>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var attributes = tag.getElementsByTagNameNS("*", "attribute");
            var attr = (Element) attributes.item(0);
            assertThat(getElementText(attr, "type")).isEqualTo("java.lang.String");
        }

        @Test
        void parsesDisplayName() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface>
                    <cc:attribute name="label" displayName="Label Attribute"/>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var attributes = tag.getElementsByTagNameNS("*", "attribute");
            var attr = (Element) attributes.item(0);
            assertThat(getElementText(attr, "displayName")).isEqualTo("Label Attribute");
        }

        @Test
        void attributeOutsideInterfaceIsIgnored() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:attribute name="orphan"/>
                <cc:interface>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var attributes = tag.getElementsByTagNameNS("*", "attribute");
            assertThat(attributes.getLength()).isEqualTo(0);
        }

        @Test
        void multipleAttributes() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface>
                    <cc:attribute name="first" required="true" type="java.lang.String"/>
                    <cc:attribute name="second" required="false" type="java.lang.Integer"/>
                    <cc:attribute name="third"/>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var attributes = tag.getElementsByTagNameNS("*", "attribute");
            assertThat(attributes.getLength()).isEqualTo(3);
        }
    }

    @Nested
    class ValueHolder {

        @Test
        void valueHolderAddsImpliedValueAttribute() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface>
                    <cc:valueHolder/>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var attributes = tag.getElementsByTagNameNS("*", "attribute");

            var hasValue = false;
            for (var i = 0; i < attributes.getLength(); i++) {
                var attr = (Element) attributes.item(i);
                if ("value".equals(getElementText(attr, "name"))) {
                    hasValue = true;
                    assertThat(getElementText(attr, "type")).isEqualTo("java.lang.Object");
                }
            }
            assertThat(hasValue).isTrue();
        }

        @Test
        void editableValueHolderAddsImpliedValueAttribute() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface>
                    <cc:editableValueHolder/>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var attributes = tag.getElementsByTagNameNS("*", "attribute");

            var hasValue = false;
            for (var i = 0; i < attributes.getLength(); i++) {
                var attr = (Element) attributes.item(i);
                if ("value".equals(getElementText(attr, "name"))) {
                    hasValue = true;
                }
            }
            assertThat(hasValue).isTrue();
        }

        @Test
        void explicitValueAttributeSuppressesImpliedValue() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee">
                <cc:interface>
                    <cc:attribute name="value" type="java.lang.String"/>
                    <cc:valueHolder/>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var attributes = tag.getElementsByTagNameNS("*", "attribute");

            // Should only have one "value" attribute (the explicit one), not two
            var valueCount = 0;
            for (var i = 0; i < attributes.getLength(); i++) {
                var attr = (Element) attributes.item(i);
                if ("value".equals(getElementText(attr, "name"))) {
                    valueCount++;
                }
            }
            assertThat(valueCount).isEqualTo(1);
        }
    }

    @Nested
    class LegacyNamespaces {

        @Test
        void parsesWithSunCompositeNamespace() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="http://java.sun.com/jsf/composite">
                <cc:interface>
                    <cc:attribute name="sunAttr"/>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            assertThat(getElementText(tag, "tag-name")).isEqualTo("testComponent");

            var attributes = tag.getElementsByTagNameNS("*", "attribute");
            assertThat(attributes.getLength()).isEqualTo(1);
        }

        @Test
        void parsesWithJcpCompositeNamespace() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="http://xmlns.jcp.org/jsf/composite">
                <cc:interface>
                    <cc:attribute name="jcpAttr"/>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            assertThat(getElementText(tag, "tag-name")).isEqualTo("testComponent");

            var attributes = tag.getElementsByTagNameNS("*", "attribute");
            assertThat(attributes.getLength()).isEqualTo(1);
        }

        @Test
        void parsesWithJakartaFacesCompositeNamespace() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="jakarta.faces.composite">
                <cc:interface>
                    <cc:attribute name="facesAttr"/>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            assertThat(getElementText(tag, "tag-name")).isEqualTo("testComponent");

            var attributes = tag.getElementsByTagNameNS("*", "attribute");
            assertThat(attributes.getLength()).isEqualTo(1);
        }
    }

    @Nested
    class VdldocExtensions {

        @Test
        void parsesSinceExtension() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee"
                      xmlns:vdldoc="http://vdldoc.omnifaces.org">
                <cc:interface>
                    <cc:extension>
                        <vdldoc:since value="3.0"/>
                    </cc:extension>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var sinceElements = tag.getElementsByTagNameNS(NS_VDLDOC, "since");
            assertThat(sinceElements.getLength()).isEqualTo(1);
            assertThat(sinceElements.item(0).getTextContent()).isEqualTo("3.0");
        }

        @Test
        void parsesExampleUrlExtension() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee"
                      xmlns:vdldoc="http://vdldoc.omnifaces.org">
                <cc:interface>
                    <cc:extension>
                        <vdldoc:example-url value="http://example.com/demo"/>
                    </cc:extension>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var exampleElements = tag.getElementsByTagNameNS(NS_VDLDOC, "example-url");
            assertThat(exampleElements.getLength()).isEqualTo(1);
            assertThat(exampleElements.item(0).getTextContent()).isEqualTo("http://example.com/demo");
        }

        @Test
        void parsesDeprecatedExtensionWithShortDescription() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee"
                      xmlns:vdldoc="http://vdldoc.omnifaces.org">
                <cc:interface>
                    <cc:extension>
                        <vdldoc:deprecated shortDescription="Use newComponent instead"/>
                    </cc:extension>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var deprecatedElements = tag.getElementsByTagNameNS(NS_VDLDOC, "deprecated");
            assertThat(deprecatedElements.getLength()).isEqualTo(1);
            assertThat(deprecatedElements.item(0).getTextContent()).isEqualTo("Use newComponent instead");
        }

        @Test
        void parsesDeprecatedExtensionWithValue() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee"
                      xmlns:vdldoc="http://vdldoc.omnifaces.org">
                <cc:interface>
                    <cc:extension>
                        <vdldoc:deprecated value="Deprecated since 2.0"/>
                    </cc:extension>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var deprecatedElements = tag.getElementsByTagNameNS(NS_VDLDOC, "deprecated");
            assertThat(deprecatedElements.getLength()).isEqualTo(1);
            assertThat(deprecatedElements.item(0).getTextContent()).isEqualTo("Deprecated since 2.0");
        }

        @Test
        void parsesOldVdldocNamespace() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee"
                      xmlns:vdldoc="http://vdldoc.org/vdldoc">
                <cc:interface>
                    <cc:extension>
                        <vdldoc:since value="1.0"/>
                    </cc:extension>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            // Old vdldoc namespace should still be recognized; the since element is created with the new NS
            var sinceElements = tag.getElementsByTagNameNS(NS_VDLDOC, "since");
            assertThat(sinceElements.getLength()).isEqualTo(1);
            assertThat(sinceElements.item(0).getTextContent()).isEqualTo("1.0");
        }

        @Test
        void extensionOutsideInterfaceIsIgnored() throws Exception {
            attributeMap.clear();

            var xhtml = """
                <html xmlns:cc="https://jakarta.ee/xml/ns/jakartaee"
                      xmlns:vdldoc="http://vdldoc.omnifaces.org">
                <cc:extension>
                    <vdldoc:since value="1.0"/>
                </cc:extension>
                <cc:interface>
                </cc:interface>
                </html>
                """;
            parseXhtml(xhtml);

            var tag = (Element) getTagElements().item(0);
            var sinceElements = tag.getElementsByTagNameNS(NS_VDLDOC, "since");
            assertThat(sinceElements.getLength()).isEqualTo(0);
        }
    }

    @Nested
    class AddImpliedAttribute {

        @Test
        void addsAllFieldsCorrectly() {
            var parent = document.createElementNS(NS_JAKARTA_EE, "tag");
            taglibNode.appendChild(parent);

            var handler = new CompositeComponentHandler(
                "test", document, NS_JAKARTA_EE, taglibNode, attributeMap);

            handler.addImpliedAttribute(parent, "myName", "My Display Name", "My description", "true", "java.lang.Integer");

            var attributes = parent.getElementsByTagNameNS("*", "attribute");
            assertThat(attributes.getLength()).isEqualTo(1);

            var attr = (Element) attributes.item(0);
            assertThat(getElementText(attr, "name")).isEqualTo("myName");
            assertThat(getElementText(attr, "displayName")).isEqualTo("My Display Name");
            assertThat(getElementText(attr, "description")).isEqualTo("My description");
            assertThat(getElementText(attr, "required")).isEqualTo("true");
            assertThat(getElementText(attr, "type")).isEqualTo("java.lang.Integer");
        }
    }

    @Nested
    class ResolveEntity {

        @Test
        void returnsEmptyInputSource() throws Exception {
            var handler = new CompositeComponentHandler(
                "test", document, NS_JAKARTA_EE, taglibNode, attributeMap);

            var source = handler.resolveEntity("//publicId", "http://example.com/systemId");
            assertThat(source).isNotNull();
        }
    }
}
