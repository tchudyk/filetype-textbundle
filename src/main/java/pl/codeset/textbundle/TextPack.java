package pl.codeset.textbundle;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextPack implements TextBundle {

    private final Path filePath;
    private final FileSystem fileSystem;

    public TextPack(Path path) throws IOException {
        filePath = path;
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        URI uri = URI.create("jar:file:" + path.toString());
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        fileSystem = FileSystems.newFileSystem(uri, env);
    }

    @Override
    public MetaData readMetaData() throws IOException {
        try {
            byte[] bytes = Files.readAllBytes(fileSystem.getPath("info.json"));
            return new Gson().fromJson(new String(bytes, StandardCharsets.UTF_8), MetaData.class);
        } catch (NoSuchFileException e) {
            return new MetaData();
        }
    }

    @Override
    public TextPack writeMetaData(MetaData metaData) throws IOException {
        String json = new Gson().toJson(metaData);
        Files.write(fileSystem.getPath("info.json"), json.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    @Override
    public Set<Asset> readAssets() throws IOException {
        Set<Asset> assets = new HashSet<>();
        Path assetsPath = fileSystem.getPath("assets");
        if (!Files.exists(assetsPath)) {
            return assets;
        }
        Files.walkFileTree(assetsPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.isRegularFile()) {
                    assets.add(new Asset(fileSystem.getPath("/"), file));
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return assets;
    }

    @Override
    public TextPack writeAsset(Asset asset) throws IOException {
        Path path = fileSystem.getPath(asset.getPath());
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        updateMetaData();

        Files.write(fileSystem.getPath("/").resolve(path), asset.readContent());
        return this;
    }

    @Override
    public TextPack removeAsset(Asset asset) throws IOException {
        Files.deleteIfExists(fileSystem.getPath("/").resolve(asset.getPath()));
        return this;
    }

    @Override
    public TextContent readContent() throws IOException {
        try (Stream<Path> paths = Files.walk(fileSystem.getPath("/"))) {
            Path contentPath = paths
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().toLowerCase().startsWith("text."))
                    .findFirst()
                    .orElse(fileSystem.getPath("text." + ContentType.MARKDOWN.getExtension()));

            ContentType contentType = ContentType.findByPath(contentPath)
                    .orElseThrow(() -> new IOException("Unsupported content type " + contentPath.getFileName()));

            return new TextContent(fileSystem.getPath("/"), contentPath.getFileName().toString(), contentType);
        }
    }

    @Override
    public TextPack writeContent(TextContent textContent) throws IOException {
        updateMetaData();

        Files.write(fileSystem.getPath("text." + textContent.getContentType().getExtension()), textContent.getContent());
        return this;
    }

    private void updateMetaData() throws IOException {
        if (!Files.exists(fileSystem.getPath("info.json"))) {
            writeMetaData(new MetaData());
        }
    }

    public void optimize() throws IOException {
        String contentAsString = readContent().getContentAsString();
        Set<Asset> assets = readAssets();
        Set<Asset> toRemove = assets.stream()
                .filter(a -> !contentAsString.contains(a.getPath()))
                .collect(Collectors.toSet());
        for (Asset asset : toRemove) {
            removeAsset(asset);
        }
    }

    @Override
    public Path getPath() {
        return filePath;
    }

    @Override
    public void close() throws IOException {
        fileSystem.close();
    }

    public void unpackTo(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        Path zipRoot = fileSystem.getPath("/");
        Files.walkFileTree(zipRoot, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, path.resolve(zipRoot.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(path.resolve(zipRoot.relativize(dir).toString()));
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
