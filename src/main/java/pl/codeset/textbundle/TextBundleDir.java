package pl.codeset.textbundle;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class TextBundleDir implements TextBundle {

    private final Path path;

    public static boolean isBundleDir(Path path) {
        return Files.exists(path) && Files.exists(path.resolve("info.json"));
    }

    public TextBundleDir(Path path) throws IOException {
        this.path = path.normalize();
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    @Override
    public MetaData readMetaData() throws IOException {
        Path infoFile = path.resolve("info.json");
        if (Files.exists(infoFile)) {
            byte[] bytes = Files.readAllBytes(infoFile);
            return new Gson().fromJson(new String(bytes, StandardCharsets.UTF_8), MetaData.class);
        } else {
            return new MetaData();
        }
    }

    @Override
    public TextBundleDir writeMetaData(MetaData metaData) throws IOException {
        String json = new Gson().toJson(metaData);
        Files.write(path.resolve("info.json"), json.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    @Override
    public Set<Asset> readAssets() throws IOException {
        Set<Asset> assets = new HashSet<>();
        Files.walkFileTree(path.resolve("assets"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.isRegularFile()) {
                    assets.add(new Asset(path, file));
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return assets;
    }

    @Override
    public TextBundleDir writeAsset(Asset asset) throws IOException {
        Path assetPath = path.resolve(asset.getPath()).normalize();
        if (!assetPath.startsWith(path)) {
            throw new IllegalStateException("Invalid asset path - expected to be in " + path.toString());
        }
        updateMetaData();

        Files.createDirectories(assetPath.getParent());
        Files.write(assetPath, asset.readContent());
        return this;
    }

    @Override
    public TextBundleDir removeAsset(Asset asset) throws IOException {
        Path assetPath = path.resolve(asset.getPath()).normalize();
        if (!assetPath.startsWith(path)) {
            throw new IllegalStateException("Invalid asset path - expected to be in " + path.toString());
        }
        Files.deleteIfExists(assetPath);
        return this;
    }

    @Override
    public TextContent readContent() throws IOException {
        try (Stream<Path> paths = Files.walk(path)) {
            Path contentPath = paths
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().toLowerCase().startsWith("text."))
                    .findFirst()
                    .orElse(this.path.resolve("text." + ContentType.MARKDOWN.getExtension()));

            ContentType contentType = ContentType.findByPath(contentPath)
                    .orElseThrow(() -> new IOException("Unsupported content type " + contentPath.getFileName()));

            return new TextContent(path, contentPath.getFileName().toString(), contentType);
        }
    }

    @Override
    public TextBundleDir writeContent(TextContent textContent) throws IOException {
        if (!Files.exists(path.resolve("info.json"))) {
            writeMetaData(new MetaData());
        }
        updateMetaData();

        Files.write(path.resolve("text." + textContent.getContentType().getExtension()), textContent.getContent());
        return this;
    }

    private void updateMetaData() throws IOException {
        if (!Files.exists(path.resolve("info.json"))) {
            writeMetaData(new MetaData());
        }
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public void close() {
    }

    public void packTo(Path targetPath) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        URI uri = URI.create("jar:file:" + targetPath.toString());
        if (targetPath.getParent() != null) {
            Files.createDirectories(targetPath.getParent());
        }
        Files.deleteIfExists(targetPath);
        try (FileSystem fileSystem = FileSystems.newFileSystem(uri, env)) {
            Path zipRoot = fileSystem.getPath("/");
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, zipRoot.resolve(path.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Files.createDirectories(zipRoot.resolve(path.relativize(dir).toString()));
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
