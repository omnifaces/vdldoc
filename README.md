# Vdldoc

Vdldoc is a forked and rewritten version of old JSP TLDdoc Generator as
previously available at
[http://taglibrarydoc.dev.java.net](http://taglibrarydoc.dev.java.net)
(which is nowhere available right now). Vdldoc has near-complete support
for Facelet `*.taglib.xml` files. The generated documentation has Java
7 javadoc look'n'feel.

As a preview, you can check the following online examples:

-   [OmniFaces](https://github.com/omnifaces/omnifaces) [VDL
    documentation](http://wiki.omnifaces.googlecode.com/hg/vdldoc/index.html)
-   [PrimeFaces](http://primefaces.org) [4.0 VDL
    documentation](http://www.primefaces.org/docs/vdl/4.0/)
-   [RichFaces](http://jboss.org/richfaces) [5.0.0 Alpha1 VDL
    documentation](http://docs.jboss.org/richfaces/5.0.X/5.0.0.Alpha1/vdldoc/)

## Installation

Just drop the [Vdldoc 1.1 JAR
file](https://code.google.com/p/vdldoc/downloads/detail?name=vdldoc-1.1.jar)
into the classpath. If you are using Maven, you can include the following
dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>org.omnifaces</groupId>
    <artifactId>vdldoc</artifactId>
    <version>1.1</version>
</dependency>
```

**Note:** Version 1.0 is not available in Maven. Also note that John
Yeary created a [Vdldoc Maven
Plugin](http://code.bluelotussoftware.com/vdldoc-maven-plugin) around
Vdldoc 1.0 which enables you to automatically generate new VDL
documentation during a Maven build. For usage details, see [this Google+
post](https://plus.google.com/u/0/112146428878473069965/posts/16vXoZonXjk).

## Usage

Much like old TLDdoc usage, once the jar is in runtime classpath, run it
as follows:

```java
VdldocGenerator generator = new VdldocGenerator();
generator.setWindowTitle("Browser window title"); // Else default will be used.
generator.setDocTitle("Documentation title"); // Else default will be used.
generator.setOutputDirectory(new File("/path/to/vdldoc")); // Else ./vdldoc will be used.
generator.setFacesConfig(new File("/path/to/faces-config.xml")); // Optional.
generator.addTaglib(new File("/path/to/foo.taglib.xml"));
generator.addTaglib(new File("/path/to/bar.taglib.xml"));
// ...
generator.generate();
```

The `<facelet-taglib id>` will be used as the taglib prefix. Make sure
that you have supplied one. E.g.

```xml
<facelet-taglib id="o">
    <description>OmniFaces UI components.</description>
    <namespace>http://omnifaces.org/ui</namespace>
    ...
</facelet-taglib>
```

Otherwise it will default to the base filename without the `.taglib.xml`
extension.

**Note:** MyFaces has a [known parsing
issue](https://issues.apache.org/jira/browse/MYFACES-3537) when using
`<description>` in the `<facelet-taglib>` as demonstrated above. This
has been fixed for MyFaces 2.0.14 and 2.1.8. This problem is completely
unrelated to Vdldoc.

## CLI

The `vdldoc.jar` can be also run in the command line like so: 

```sh
java -jar vdldoc-1.1.jar foo.taglib.xml bar.taglib.xml
``` 

The files will by default be generated into a new `/vdldoc` folder in
the working directory. The usage options are laid out below:

```
------------------------------------------------------------------------------
                        VDL Documentation Generator
Usage:
               vdldoc [options] taglib1 [taglib2 [taglib3 ...]]
options:
  -help                  Displays this help message.
  -d <directory>         Destination directory for output files.
                         This defaults to new dir called 'vdldoc'.
  -windowtitle <text>    Browser window title. This defaults to
                         VDL Documentation Generator - Generated Documentation
  -doctitle <html-code>  Documentation title for the VDL index page.
                         This defaults to the same as window title.
  -facesconfig <path>    Path to the faces-config.xml file.
  -attr <path>           Path to properties file containing descriptions for
                         implied attributes of composite components, such as
                         'id', 'rendered', etc.
  -q                     Quiet Mode, i.e. disable logging.
taglib1 [taglib2 [taglib3 ...]]: Space separated paths to .taglib.xml files.
    NOTE: if an argument or file path contains by itself spaces, quote it.
------------------------------------------------------------------------------
```

### License

Vdldoc has BSD license, because the original TLDDoc was also BSD. Note
that we would personally like it to be Apache 2.0 license instead, but
we don't know how to relicense it without any potential law trouble. So
we decided to keep it BSD.
