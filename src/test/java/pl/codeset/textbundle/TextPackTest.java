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

class TextPackTest {

    @Test
    void shouldCreateTextPackFile(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.textpack");

        // When
        try (TextPack file = new TextPack(path)) {
            file.writeContent(new TextContent(ContentType.MARKDOWN, "Sample MD"));
            file.writeAsset(new Asset("test1.raw", "raw-content".getBytes(StandardCharsets.UTF_8)));
            file.writeAsset(new Asset("test2.raw", "raw-content2".getBytes(StandardCharsets.UTF_8)));
        }

        // Then
        assertTrue(Files.exists(path));
    }

    @Test
    void shouldWriteAndReadCorrectContent(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.textpack");
        String contentToWrite = "Sample MD";

        // When
        try (TextPack file = new TextPack(path)) {
            file.writeContent(new TextContent(ContentType.MARKDOWN, contentToWrite));
        }

        String readContent;
        try (TextPack file = new TextPack(path)) {
            readContent = file.readContent().getContentAsString();
        }

        // Then
        assertEquals(contentToWrite, readContent);
    }

    @Test
    void shouldWriteAndReadCorrectAsset(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.textpack");
        byte[] contentToWrite = "Sample-asset".getBytes(StandardCharsets.UTF_8);

        // When
        try (TextPack file = new TextPack(path)) {
            file.writeAsset(new Asset("test.file", contentToWrite));
        }

        byte[] readContent;
        try (TextPack file = new TextPack(path)) {
            readContent = file.readAssets().iterator().next().readContent();
        }

        // Then
        assertArrayEquals(contentToWrite, readContent);
    }

    @Test
    void shouldUpdateContentWhenExists(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.bundle");
        try (TextPack file = new TextPack(path)) {
            file.writeContent(new TextContent(ContentType.MARKDOWN, "First content"));
        }

        // When
        String contentToWrite = "Sample MD";
        try (TextPack file = new TextPack(path)) {
            file.writeContent(new TextContent(ContentType.MARKDOWN, contentToWrite));
        }

        String readContent;
        try (TextPack file = new TextPack(path)) {
            readContent = file.readContent().getContentAsString();
        }

        // Then
        assertEquals(contentToWrite, readContent);
    }

    @Test
    void shouldUpdateAssetWhenExists(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.textpack");
        try (TextPack file = new TextPack(path)) {
            file.writeAsset(new Asset("test.file", "FirstContent".getBytes(StandardCharsets.UTF_8)));
        }

        // When
        byte[] contentToWrite = "Sample-asset".getBytes(StandardCharsets.UTF_8);
        try (TextPack file = new TextPack(path)) {
            file.writeAsset(new Asset("test.file", contentToWrite));
        }

        byte[] readContent;
        try (TextPack file = new TextPack(path)) {
            readContent = file.readAssets().iterator().next().readContent();
        }

        // Then
        assertArrayEquals(contentToWrite, readContent);
    }

    @Test
    void shouldRemoveOneAsset(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.textpack");
        try (TextPack file = new TextPack(path)) {
            file.writeAsset(new Asset("test1.file", "FirstContent".getBytes(StandardCharsets.UTF_8)));
            file.writeAsset(new Asset("test2.file", "SecondContent".getBytes(StandardCharsets.UTF_8)));
        }

        // When
        try (TextPack file = new TextPack(path)) {
            Asset asset = file.readAssets().stream()
                    .filter(a -> a.getFileName().equals("test1.file"))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
            file.removeAsset(asset);
        }

        Set<Asset> readAssets;
        try (TextPack file = new TextPack(path)) {
            readAssets = file.readAssets();
        }

        // Then
        assertEquals(1, readAssets.size());
        assertEquals("test2.file", readAssets.iterator().next().getFileName());
    }

    @Test
    void convertToTextBundleDir(@TempDir Path tempDir) throws IOException {
        // Given
        Path path = tempDir.resolve("my.textpack");
        try (TextPack file = new TextPack(path)) {
            file.writeContent(new TextContent(ContentType.MARKDOWN, "Sample MD"));
            file.writeAsset(new Asset("test1.raw", "raw-content".getBytes(StandardCharsets.UTF_8)));
            file.writeAsset(new Asset("test2.raw", "raw-content2".getBytes(StandardCharsets.UTF_8)));
        }

        // When
        Path unpacked = tempDir.resolve("unpackedDir");
        try (TextPack file = new TextPack(path)) {
            file.unpackTo(tempDir.resolve(unpacked));
        }

        // Then
        assertTrue(Files.exists(unpacked.resolve("text.markdown")));
        assertTrue(Files.exists(unpacked.resolve("assets/test1.raw")));
        assertTrue(Files.exists(unpacked.resolve("assets/test1.raw")));
        assertTrue(Files.exists(unpacked.resolve("info.json")));
    }
}