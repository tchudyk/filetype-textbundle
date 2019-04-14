package pl.codeset.textbundle;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public enum ContentType {

    MARKDOWN("markdown");

    private final String extension;

    ContentType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static Optional<ContentType> findByPath(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return Stream.of(values()).filter(v -> fileName.endsWith(v.extension)).findFirst();
    }
}
