package pl.codeset.textbundle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextContent {

    private ContentType contentType;
    private byte[] content;

    TextContent(Path bundlePath, String contentPath, ContentType contentType) throws IOException {
        this.contentType = contentType;
        this.content = Files.readAllBytes(bundlePath.resolve(contentPath));
    }

    public TextContent(ContentType contentType) {
        this.contentType = contentType;
    }

    public TextContent(ContentType contentType, byte[] content) {
        this.contentType = contentType;
        this.content = content;
    }

    public TextContent(ContentType contentType, String content) {
        this(contentType, content.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentAsString() {
        return new String(content, StandardCharsets.UTF_8);
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public void setContent(String content) {
        this.content = content != null ? content.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }

    public void setContent(byte[] content) {
        this.content = content != null ? content : new byte[0];
    }
}
