package pl.codeset.textbundle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Asset {

    private Path bundlePath;
    private final String targetPath;
    private byte[] content;

    Asset(Path bundlePath, Path path) {
        this.bundlePath = bundlePath;
        targetPath = bundlePath.relativize(path).toString();
    }

    public Asset(String fileName, byte[] bytes) {
        targetPath = Paths.get("assets", fileName).toString();
        content = bytes;
    }

    public Asset(String fileName, Path sourcePath) throws IOException {
        targetPath = Paths.get("assets", fileName).toString();
        content = Files.readAllBytes(sourcePath);
    }

    public String getFileName() {
        return targetPath.substring("assets/".length());
    }

    public byte[] readContent() {
        if (content == null && bundlePath != null) {
            try {
                content = Files.readAllBytes(bundlePath.resolve(targetPath));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return content;
    }

    public String getPath() {
        return targetPath;
    }
}
