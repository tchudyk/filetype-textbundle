package pl.codeset.textbundle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextBundleDirTest {

    @Test
    void shouldCreateTextBundleDir(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.bundle");

        // When
        try (TextBundleDir dir = new TextBundleDir(path)) {
            dir.writeContent(new TextContent(ContentType.MARKDOWN, "Sample MD"));
            dir.writeAsset(new Asset("test1.raw", "raw-content".getBytes(StandardCharsets.UTF_8)));
            dir.writeAsset(new Asset("test2.raw", "raw-content2".getBytes(StandardCharsets.UTF_8)));
        }

        // Then
        assertTrue(Files.exists(path.resolve("text.markdown")));
        assertTrue(Files.exists(path.resolve("assets/test1.raw")));
        assertTrue(Files.exists(path.resolve("assets/test1.raw")));
        assertTrue(Files.exists(path.resolve("info.json")));
    }

    @Test
    void shouldWriteAndReadCorrectContent(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.bundle");
        String contentToWrite = "Sample MD";

        // When
        try (TextBundleDir dir = new TextBundleDir(path)) {
            dir.writeContent(new TextContent(ContentType.MARKDOWN, contentToWrite));
        }

        String readContent;
        try (TextBundleDir dir = new TextBundleDir(path)) {
            readContent = dir.readContent().getContentAsString();
        }

        // Then
        assertEquals(contentToWrite, readContent);
    }

    @Test
    void shouldWriteAndReadCorrectAsset(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.bundle");
        byte[] contentToWrite = "Sample-asset".getBytes(StandardCharsets.UTF_8);

        // When
        try (TextBundleDir dir = new TextBundleDir(path)) {
            dir.writeAsset(new Asset("test.file", contentToWrite));
        }

        byte[] readContent;
        try (TextBundleDir dir = new TextBundleDir(path)) {
            readContent = dir.readAssets().iterator().next().readContent();
        }

        // Then
        assertArrayEquals(contentToWrite, readContent);
    }

    @Test
    void shouldUpdateContentWhenExists(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.bundle");
        try (TextBundleDir dir = new TextBundleDir(path)) {
            dir.writeContent(new TextContent(ContentType.MARKDOWN, "First content"));
        }

        // When
        String contentToWrite = "Sample MD";
        try (TextBundleDir dir = new TextBundleDir(path)) {
            dir.writeContent(new TextContent(ContentType.MARKDOWN, contentToWrite));
        }

        String readContent;
        try (TextBundleDir dir = new TextBundleDir(path)) {
            readContent = dir.readContent().getContentAsString();
        }

        // Then
        assertEquals(contentToWrite, readContent);
    }

    @Test
    void shouldUpdateAssetWhenExists(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.bundle");
        try (TextBundleDir dir = new TextBundleDir(path)) {
            dir.writeAsset(new Asset("test.file", "FirstContent".getBytes(StandardCharsets.UTF_8)));
        }

        // When
        byte[] contentToWrite = "Sample-asset".getBytes(StandardCharsets.UTF_8);
        try (TextBundleDir dir = new TextBundleDir(path)) {
            dir.writeAsset(new Asset("test.file", contentToWrite));
        }

        byte[] readContent;
        try (TextBundleDir dir = new TextBundleDir(path)) {
            readContent = dir.readAssets().iterator().next().readContent();
        }

        // Then
        assertArrayEquals(contentToWrite, readContent);
    }

    @Test
    void shouldRemoveOneAsset(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.bundle");
        try (TextBundleDir dir = new TextBundleDir(path)) {
            dir.writeAsset(new Asset("test1.file", "FirstContent".getBytes(StandardCharsets.UTF_8)));
            dir.writeAsset(new Asset("test2.file", "SecondContent".getBytes(StandardCharsets.UTF_8)));
        }

        // When
        try (TextBundleDir dir = new TextBundleDir(path)) {
            Asset asset = dir.readAssets().stream().filter(a -> a.getFileName().equals("test1.file"))
                    .findFirst().orElseThrow(RuntimeException::new);
            dir.removeAsset(asset);
        }

        Set<Asset> readAssets;
        try (TextBundleDir dir = new TextBundleDir(path)) {
            readAssets = dir.readAssets();
        }

        // Then
        assertEquals(1, readAssets.size());
        assertEquals("test2.file", readAssets.iterator().next().getFileName());
    }

    @Test
    void shouldConvertToTextPack(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.bundle");
        try (TextBundleDir dir = new TextBundleDir(path)) {
            dir.writeContent(new TextContent(ContentType.MARKDOWN, "Sample MD"));
            dir.writeAsset(new Asset("test1.raw", "raw-content".getBytes(StandardCharsets.UTF_8)));
            dir.writeAsset(new Asset("test2.raw", "raw-content2".getBytes(StandardCharsets.UTF_8)));
        }

        // When
        Path textPack = tempDir.resolve("sample.textpack");
        try (TextBundleDir dir = new TextBundleDir(path)) {
            dir.packTo(textPack);
        }

        assertTrue(Files.exists(textPack));
    }
}