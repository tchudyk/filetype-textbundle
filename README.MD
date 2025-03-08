# TextBundle file type

This is Java implementation of TextBundle file type.

More information about this format you can find on official website [textbundle.org](http://textbundle.org/)

License: BSD.

### Maven
Add repository to your `pom.xml`:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add dependency:
```xml
<dependency>
    <groupId>com.github.tchudyk</groupId>
    <artifactId>filetype-textbundle</artifactId>
    <version>1.1.3</version>
</dependency>
```

## Usage
Create **TextBundle** directory.
```java
try (TextBundleDir dir = new TextBundleDir(Paths.get("myDir"))) {
    dir.writeContent(new TextContent(ContentType.MARKDOWN, "Sample Markdown file"));
    dir.writeAsset(new Asset("test1.txt", "raw-content".getBytes(StandardCharsets.UTF_8)));
    dir.writeAsset(new Asset("test2.txt", "raw-content2".getBytes(StandardCharsets.UTF_8)));
} catch (IOException e) {
    e.printStackTrace();
}
```


Create **TextPack** directory.
```java
try (TextPack file = new TextPack(Paths.get("sample.textpack"))) {
    file.writeContent(new TextContent(ContentType.MARKDOWN, "Sample Markdown file"));
    file.writeAsset(new Asset("test1.txt", "raw-content".getBytes(StandardCharsets.UTF_8)));
    file.writeAsset(new Asset("test2.txt", "raw-content2".getBytes(StandardCharsets.UTF_8)));
} catch (IOException e) {
    e.printStackTrace();
}
```

Convert **TextPack** to **TextBundle** (unpack)
```java
try (TextPack file = new TextPack(Paths.get("sample.textpack"))) {
    file.unpackTo(tempDir.resolve(Paths.get("/tmp/sample-unpacked")));
} catch (IOException e) {
    e.printStackTrace();
}
```

Convert **TextBundle** to **TextPack** (pack)
```java
Path textPack = tempDir.resolve("sample.textpack");
try (TextBundleDir dir = new TextBundleDir(path)) {
    dir.packTo(textPack);
} catch (IOException e) {
    e.printStackTrace();
}
```