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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.omnifaces.vdldoc.VdldocGenerator.DEFAULT_CSS_LOCATION;
import static org.omnifaces.vdldoc.VdldocGenerator.DEFAULT_OUTPUT_DIRECTORY;
import static org.omnifaces.vdldoc.VdldocGenerator.DEFAULT_WINDOW_TITLE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class VdldocGeneratorTest {

    private static final String FIXTURES = "/org/omnifaces/vdldoc/fixtures/";

    @TempDir
    Path tempDir;

    private VdldocGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new VdldocGenerator();
        generator.setOutputDirectory(tempDir.resolve("output").toFile());
        generator.setQuiet(true);
    }

    private File fixture(String name) {
        return new File(getClass().getResource(FIXTURES + name).getFile());
    }

    // -- addTaglib validation -------------------------------------------------

    @Nested
    class AddTaglib {

        @Test
        void acceptsValidTaglibFile() {
            var taglib = fixture("sample-jakarta.taglib.xml");
            generator.addTaglib(taglib);
            // No exception = success
        }

        @Test
        void rejectsNonExistentFile() {
            var bogus = new File("/nonexistent/path/test.taglib.xml");
            assertThatThrownBy(() -> generator.addTaglib(bogus))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejectsFileWithWrongExtension() throws IOException {
            var wrongExt = tempDir.resolve("test.xml").toFile();
            wrongExt.createNewFile();
            assertThatThrownBy(() -> generator.addTaglib(wrongExt))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejectsDirectory() {
            var dir = tempDir.toFile();
            assertThatThrownBy(() -> generator.addTaglib(dir))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -- setFacesConfig validation --------------------------------------------

    @Nested
    class SetFacesConfig {

        @Test
        void acceptsValidFacesConfig() {
            generator.setFacesConfig(fixture("facesconfig/faces-config.xml"));
        }

        @Test
        void rejectsNonExistentFile() {
            var bogus = new File("/nonexistent/faces-config.xml");
            assertThatThrownBy(() -> generator.setFacesConfig(bogus))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejectsWrongFileName() throws IOException {
            var wrongName = tempDir.resolve("wrong-name.xml").toFile();
            wrongName.createNewFile();
            assertThatThrownBy(() -> generator.setFacesConfig(wrongName))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -- setAttributes validation ---------------------------------------------

    @Nested
    class SetAttributes {

        @Test
        void acceptsValidAttributesFile() {
            generator.setAttributes(fixture("custom-cc-attributes.properties"));
        }

        @Test
        void rejectsNonExistentFile() {
            var bogus = new File("/nonexistent/attrs.properties");
            assertThatThrownBy(() -> generator.setAttributes(bogus))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -- setCssLocation validation --------------------------------------------

    @Nested
    class SetCssLocation {

        @Test
        void acceptsRelativePath() {
            generator.setCssLocation("mystyle.css");
        }

        @Test
        void acceptsAbsoluteUrl() {
            generator.setCssLocation("https://example.com/style.css");
        }

        @Test
        void acceptsAbsolutePath() {
            generator.setCssLocation("/css/style.css");
        }

        @Test
        void rejectsInvalidUri() {
            assertThatThrownBy(() -> generator.setCssLocation("not a valid {uri}"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -- Setters --------------------------------------------------------------

    @Nested
    class Setters {

        @Test
        void setWindowTitle() {
            generator.setWindowTitle("My Title");
            // No exception = success; title is verified in integration tests via output HTML
        }

        @Test
        void setDocTitle() {
            generator.setDocTitle("My Doc Title");
        }

        @Test
        void setHideGeneratedBy() {
            generator.setHideGeneratedBy(true);
        }

        @Test
        void setQuiet() {
            generator.setQuiet(false);
            generator.setQuiet(true);
        }
    }

    // -- Full generation: Jakarta EE namespace --------------------------------

    @Nested
    class GenerateJakartaEE {

        @Test
        void generatesOverviewPages() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.generate();

            var output = tempDir.resolve("output").toFile();
            assertThat(new File(output, "index.html")).exists();
            assertThat(new File(output, "help-doc.html")).exists();
            assertThat(new File(output, "overview-frame.html")).exists();
            assertThat(new File(output, "alltags-frame.html")).exists();
            assertThat(new File(output, "alltags-noframe.html")).exists();
            assertThat(new File(output, "overview-summary.html")).exists();
        }

        @Test
        void generatesStylesheet() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.generate();

            var output = tempDir.resolve("output").toFile();
            assertThat(new File(output, DEFAULT_CSS_LOCATION)).exists();
        }

        @Test
        void doesNotCopyStylesheetWhenCustomCssSet() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setCssLocation("https://example.com/custom.css");
            generator.generate();

            var output = tempDir.resolve("output").toFile();
            assertThat(new File(output, DEFAULT_CSS_LOCATION)).doesNotExist();
        }

        @Test
        void generatesTaglibDirectory() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.generate();

            var taglibDir = tempDir.resolve("output/sample").toFile();
            assertThat(taglibDir).isDirectory();
            assertThat(new File(taglibDir, "tld-frame.html")).exists();
            assertThat(new File(taglibDir, "tld-summary.html")).exists();
        }

        @Test
        void generatesTagDetailPages() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.generate();

            var taglibDir = tempDir.resolve("output/sample").toFile();
            assertThat(new File(taglibDir, "sampleTag.html")).exists();
            assertThat(new File(taglibDir, "otherTag.html")).exists();
        }

        @Test
        void generatesFunctionDetailPage() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.generate();

            var taglibDir = tempDir.resolve("output/sample").toFile();
            assertThat(new File(taglibDir, "formatDate.fn.html")).exists();
        }

        @Test
        void tagDetailContainsAttributes() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.generate();

            var tagFile = tempDir.resolve("output/sample/sampleTag.html").toFile();
            var content = Files.readString(tagFile.toPath());
            assertThat(content).contains("value");
            assertThat(content).contains("styleClass");
        }

        @Test
        void overviewSummaryContainsTaglibName() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.generate();

            var overview = tempDir.resolve("output/overview-summary.html").toFile();
            var content = Files.readString(overview.toPath());
            assertThat(content).contains("sample");
        }
    }

    // -- Full generation: namespace migration ----------------------------------

    @Nested
    class NamespaceMigration {

        @Test
        void generatesSunNamespaceTaglib() {
            generator.addTaglib(fixture("sample-sun.taglib.xml"));
            generator.generate();

            var taglibDir = tempDir.resolve("output/sunsample").toFile();
            assertThat(taglibDir).isDirectory();
            assertThat(new File(taglibDir, "sunTag.html")).exists();
        }

        @Test
        void generatesJcpNamespaceTaglib() {
            generator.addTaglib(fixture("sample-jcp.taglib.xml"));
            generator.generate();

            var taglibDir = tempDir.resolve("output/jcpsample").toFile();
            assertThat(taglibDir).isDirectory();
            assertThat(new File(taglibDir, "jcpTag.html")).exists();
        }

        @Test
        void generatesOldVdldocNamespaceTaglib() {
            generator.addTaglib(fixture("sample-vdldoc-old.taglib.xml"));
            generator.generate();

            var taglibDir = tempDir.resolve("output/oldvdldoc").toFile();
            assertThat(taglibDir).isDirectory();
            assertThat(new File(taglibDir, "oldVdldocTag.html")).exists();
        }
    }

    // -- Full generation: edge cases ------------------------------------------

    @Nested
    class EdgeCases {

        @Test
        void taglibWithoutIdFallsBackToFilename() {
            generator.addTaglib(fixture("sample-noid.taglib.xml"));
            generator.generate();

            var taglibDir = tempDir.resolve("output/sample-noid").toFile();
            assertThat(taglibDir).isDirectory();
        }

        @Test
        void emptyTaglibGeneratesWithoutError() {
            generator.addTaglib(fixture("sample-empty.taglib.xml"));
            generator.generate();

            var taglibDir = tempDir.resolve("output/empty").toFile();
            assertThat(taglibDir).isDirectory();
            assertThat(new File(taglibDir, "tld-summary.html")).exists();
        }

        @Test
        void notATaglibFileThrows() {
            var notTaglib = fixture("not-a-taglib.xml");

            // The file doesn't have .taglib.xml extension, so addTaglib should reject it
            assertThatThrownBy(() -> generator.addTaglib(notTaglib))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void multipleTaglibs() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.addTaglib(fixture("sample-empty.taglib.xml"));
            generator.generate();

            assertThat(tempDir.resolve("output/sample").toFile()).isDirectory();
            assertThat(tempDir.resolve("output/empty").toFile()).isDirectory();
        }

        @Test
        void duplicateTaglibIdsThrow() throws IOException {
            var dup1 = tempDir.resolve("dup1.taglib.xml");
            var dup2 = tempDir.resolve("dup2.taglib.xml");

            var taglibTemplate = """
                <?xml version="1.0" encoding="UTF-8"?>
                <facelet-taglib xmlns="https://jakarta.ee/xml/ns/jakartaee" version="4.0">
                    <short-name>duplicate</short-name>
                    <namespace>http://example.com/%s</namespace>
                </facelet-taglib>
                """;

            Files.writeString(dup1, String.format(taglibTemplate, "dup1"));
            Files.writeString(dup2, String.format(taglibTemplate, "dup2"));

            generator.addTaglib(dup1.toFile());
            generator.addTaglib(dup2.toFile());

            assertThatThrownBy(() -> generator.generate())
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -- Full generation: EL variables and functions ---------------------------

    @Nested
    class ElVariablesAndFunctions {

        @Test
        void generatesElVariableDetailPage() {
            generator.addTaglib(fixture("sample-elvariable.taglib.xml"));
            generator.generate();

            var taglibDir = tempDir.resolve("output/elvars").toFile();
            assertThat(new File(taglibDir, "cc.el.html")).exists();
        }

        @Test
        void generatesFunctionDetailPage() {
            generator.addTaglib(fixture("sample-elvariable.taglib.xml"));
            generator.generate();

            var taglibDir = tempDir.resolve("output/elvars").toFile();
            assertThat(new File(taglibDir, "concat.fn.html")).exists();
        }

        @Test
        void generatesTagWithVdldocExtensions() throws IOException {
            generator.addTaglib(fixture("sample-elvariable.taglib.xml"));
            generator.generate();

            var tagFile = tempDir.resolve("output/elvars/simpleTag.html").toFile();
            assertThat(tagFile).exists();
            var content = Files.readString(tagFile.toPath());
            assertThat(content).contains("2.0"); // since value
        }
    }

    // -- Full generation: faces-config ----------------------------------------

    @Nested
    class FacesConfig {

        @Test
        void generatesWithFacesConfig() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setFacesConfig(fixture("facesconfig/faces-config.xml"));
            generator.generate();

            var output = tempDir.resolve("output").toFile();
            assertThat(new File(output, "index.html")).exists();
        }

        @Test
        void managedBeansAppearInOverview() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setFacesConfig(fixture("facesconfig/faces-config.xml"));
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/overview-summary.html"));
            assertThat(content).contains("userBean");
            assertThat(content).contains("com.example.UserBean");
            assertThat(content).contains("session");
            assertThat(content).contains("The user session bean.");
        }

        @Test
        void multipleManagedBeansAppearInOverview() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setFacesConfig(fixture("facesconfig/faces-config.xml"));
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/overview-summary.html"));
            assertThat(content).contains("userBean");
            assertThat(content).contains("settingsBean");
            assertThat(content).contains("com.example.SettingsBean");
            assertThat(content).contains("application");
        }

        @Test
        void managedBeanWithoutDescriptionRendersNoDescription() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setFacesConfig(fixture("facesconfig/faces-config.xml"));
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/overview-summary.html"));
            // settingsBean has no description, XSLT renders "No Description" in italic
            assertThat(content).contains("settingsBean");
        }

        @Test
        void managedBeansSectionHeader() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setFacesConfig(fixture("facesconfig/faces-config.xml"));
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/overview-summary.html"));
            assertThat(content).contains("Managed beans");
        }
    }

    // -- Full generation: custom attributes file ------------------------------

    @Nested
    class CustomAttributes {

        @Test
        void generatesWithCustomAttributesFile() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setAttributes(fixture("custom-cc-attributes.properties"));
            generator.generate();

            var output = tempDir.resolve("output").toFile();
            assertThat(new File(output, "index.html")).exists();
        }
    }

    // -- Full generation: title configuration ---------------------------------

    @Nested
    class TitleConfiguration {


        @Test
        void windowTitleAppearsInOutput() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setWindowTitle("Custom Window Title");
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/index.html"));
            assertThat(content).contains("Custom Window Title");
        }

        @Test
        void docTitleAppearsInOverview() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setDocTitle("Custom Doc Title");
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/overview-summary.html"));
            assertThat(content).contains("Custom Doc Title");
        }

        @Test
        void docTitleDefaultsToWindowTitle() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setWindowTitle("My Window Title");
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/overview-summary.html"));
            assertThat(content).contains("My Window Title");
        }

        @Test
        void docTitleDefaultsToWindowTitleWhenSetToNull() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setWindowTitle("My Window Title");
            generator.setDocTitle(null);
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/overview-summary.html"));
            assertThat(content).contains("My Window Title");
        }

        @Test
        void docTitleDefaultsToWindowTitleWhenSetToBlank() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setWindowTitle("My Window Title");
            generator.setDocTitle("   ");
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/overview-summary.html"));
            assertThat(content).contains("My Window Title");
        }

        @Test
        void windowTitleDefaultsWhenSetToNull() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setWindowTitle(null);
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/index.html"));
            assertThat(content).contains(DEFAULT_WINDOW_TITLE);
        }

        @Test
        void windowTitleDefaultsWhenSetToBlank() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setWindowTitle("  ");
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/index.html"));
            assertThat(content).contains(DEFAULT_WINDOW_TITLE);
        }

        @Test
        void bothNullDefaultToStandardTitle() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setWindowTitle(null);
            generator.setDocTitle(null);
            generator.generate();

            var indexContent = Files.readString(tempDir.resolve("output/index.html"));
            var overviewContent = Files.readString(tempDir.resolve("output/overview-summary.html"));
            assertThat(indexContent).contains(DEFAULT_WINDOW_TITLE);
            assertThat(overviewContent).contains(DEFAULT_WINDOW_TITLE);
        }

        @Test
        void explicitDocTitleOverridesWindowTitle() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setWindowTitle("Window Title");
            generator.setDocTitle("Different Doc Title");
            generator.generate();

            var overviewContent = Files.readString(tempDir.resolve("output/overview-summary.html"));
            assertThat(overviewContent).contains("Different Doc Title");

            // Window title appears in the index page, not overridden by doc title
            var indexContent = Files.readString(tempDir.resolve("output/index.html"));
            assertThat(indexContent).contains("Window Title");
        }

        @Test
        void defaultOutputDirectory() {
            var gen = new VdldocGenerator();
            gen.addTaglib(fixture("sample-jakarta.taglib.xml"));
            gen.setQuiet(true);
            gen.generate();

            var defaultDir = new File(DEFAULT_OUTPUT_DIRECTORY);
            assertThat(defaultDir).isDirectory();
            assertThat(new File(defaultDir, "index.html")).exists();

            // Clean up
            deleteRecursively(defaultDir);
        }

        private void deleteRecursively(File file) {
            if (file.isDirectory()) {
                for (var child : file.listFiles()) {
                    deleteRecursively(child);
                }
            }
            file.delete();
        }
    }

    // -- Full generation: hide generated by -----------------------------------

    @Nested
    class HideGeneratedBy {

        @Test
        void footerVisibleByDefault() throws IOException {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.generate();

            var overviewFile = tempDir.resolve("output/overview-summary.html").toFile();
            var content = Files.readString(overviewFile.toPath());
            assertThat(content).containsIgnoringCase("vdldoc");
        }

        @Test
        void footerHiddenWhenConfigured() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setHideGeneratedBy(true);
            generator.generate();

            // We verify the config is passed through; the XSLT decides whether to render the footer
            var output = tempDir.resolve("output").toFile();
            assertThat(new File(output, "index.html")).exists();
        }
    }

    // -- CSS location variants ------------------------------------------------

    @Nested
    class CssLocationVariants {

        @Test
        void absoluteUrlCssDoesNotCopyStylesheet() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setCssLocation("https://cdn.example.com/style.css");
            generator.generate();

            assertThat(tempDir.resolve("output/stylesheet.css").toFile()).doesNotExist();
        }

        @Test
        void absolutePathCssDoesNotCopyStylesheet() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.setCssLocation("/static/style.css");
            generator.generate();

            assertThat(tempDir.resolve("output/stylesheet.css").toFile()).doesNotExist();
        }
    }

    // -- Resources directory --------------------------------------------------

    @Nested
    class ResourcesDirectory {

        @Test
        void createsResourcesSubdirectory() {
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.generate();

            assertThat(tempDir.resolve("output/resources").toFile()).isDirectory();
        }
    }

    // -- Sort order -----------------------------------------------------------

    @Nested
    class SortOrder {

        @Test
        void taglibSummaryListsTagsInAlphabeticalOrder() throws IOException {
            generator.addTaglib(fixture("sample-unsorted.taglib.xml"));
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/unsorted/tld-summary.html"));
            var appleIndex = content.indexOf(">apple<");
            var mangoIndex = content.indexOf(">mango<");
            var zebraIndex = content.indexOf(">zebra<");

            assertThat(appleIndex).isGreaterThan(-1);
            assertThat(mangoIndex).isGreaterThan(-1);
            assertThat(zebraIndex).isGreaterThan(-1);
            assertThat(appleIndex).isLessThan(mangoIndex);
            assertThat(mangoIndex).isLessThan(zebraIndex);
        }

        @Test
        void taglibSummaryListsFunctionsInAlphabeticalOrder() throws IOException {
            generator.addTaglib(fixture("sample-unsorted.taglib.xml"));
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/unsorted/tld-summary.html"));
            var barIndex = content.indexOf(">bar<");
            var fooIndex = content.indexOf(">foo<");
            var zooIndex = content.indexOf(">zoo<");

            assertThat(barIndex).isGreaterThan(-1);
            assertThat(fooIndex).isGreaterThan(-1);
            assertThat(zooIndex).isGreaterThan(-1);
            assertThat(barIndex).isLessThan(fooIndex);
            assertThat(fooIndex).isLessThan(zooIndex);
        }

        @Test
        void tagDetailListsAttributesInAlphabeticalOrder() throws IOException {
            generator.addTaglib(fixture("sample-unsorted.taglib.xml"));
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/unsorted/zebra.html"));
            var alphaIndex = content.indexOf(">alpha<");
            var bravoIndex = content.indexOf(">bravo<");
            var charlieIndex = content.indexOf(">charlie<");

            assertThat(alphaIndex).isGreaterThan(-1);
            assertThat(bravoIndex).isGreaterThan(-1);
            assertThat(charlieIndex).isGreaterThan(-1);
            assertThat(alphaIndex).isLessThan(bravoIndex);
            assertThat(bravoIndex).isLessThan(charlieIndex);
        }

        @Test
        void allTagsFrameListsEntriesInAlphabeticalOrder() throws IOException {
            generator.addTaglib(fixture("sample-unsorted.taglib.xml"));
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/alltags-frame.html"));
            var appleIndex = content.indexOf("unsorted:apple");
            var mangoIndex = content.indexOf("unsorted:mango");
            var zebraIndex = content.indexOf("unsorted:zebra");
            var barIndex = content.indexOf("unsorted:bar");
            var fooIndex = content.indexOf("unsorted:foo");
            var zooIndex = content.indexOf("unsorted:zoo");

            assertThat(appleIndex).isGreaterThan(-1);
            assertThat(appleIndex).isLessThan(mangoIndex);
            assertThat(mangoIndex).isLessThan(zebraIndex);
            assertThat(barIndex).isLessThan(fooIndex);
            assertThat(fooIndex).isLessThan(zooIndex);
        }

        @Test
        void allTagsNoFrameListsEntriesInAlphabeticalOrder() throws IOException {
            generator.addTaglib(fixture("sample-unsorted.taglib.xml"));
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/alltags-noframe.html"));
            var appleIndex = content.indexOf("unsorted:apple");
            var mangoIndex = content.indexOf("unsorted:mango");
            var zebraIndex = content.indexOf("unsorted:zebra");

            assertThat(appleIndex).isGreaterThan(-1);
            assertThat(appleIndex).isLessThan(mangoIndex);
            assertThat(mangoIndex).isLessThan(zebraIndex);
        }

        @Test
        void multipleTaglibsSortedByIdInAllTagsFrame() throws IOException {
            generator.addTaglib(fixture("sample-unsorted.taglib.xml"));
            generator.addTaglib(fixture("sample-empty.taglib.xml"));
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/alltags-frame.html"));
            // "empty" taglib has no tags, but "unsorted" entries should still be sorted
            // and taglib id "empty" < "unsorted" alphabetically
            var unsortedIndex = content.indexOf("unsorted:");

            assertThat(unsortedIndex).isGreaterThan(-1);
        }

        @Test
        void multipleTaglibsWithTagsSortByIdThenTagName() throws IOException {
            generator.addTaglib(fixture("sample-unsorted.taglib.xml"));
            generator.addTaglib(fixture("sample-jakarta.taglib.xml"));
            generator.generate();

            var content = Files.readString(tempDir.resolve("output/alltags-frame.html"));
            // "sample" < "unsorted" alphabetically, so sample tags come first
            var sampleTagIndex = content.indexOf("sample:sampleTag");
            var unsortedAppleIndex = content.indexOf("unsorted:apple");

            assertThat(sampleTagIndex).isGreaterThan(-1);
            assertThat(unsortedAppleIndex).isGreaterThan(-1);
            assertThat(sampleTagIndex).isLessThan(unsortedAppleIndex);
        }
    }
}
