package pl.codeset.textbundle;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface TextBundle extends AutoCloseable {

    MetaData readMetaData() throws IOException;

    TextBundle writeMetaData(MetaData metaData) throws IOException;

    Set<Asset> readAssets() throws IOException;

    TextBundle writeAsset(Asset asset) throws IOException;

    TextBundle removeAsset(Asset asset) throws IOException;

    TextContent readContent() throws IOException;

    TextBundle writeContent(TextContent textContent) throws IOException;

    Path getPath();
}
