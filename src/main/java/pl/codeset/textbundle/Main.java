package pl.codeset.textbundle;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {

//        Path path = Paths.get("/tmp/a1/ttt.textpack");
        Path path = Paths.get("/tmp/a1/ttt2l");

//        try (TextBundle textBundle = TextBundleFactory.get(path)) {
        try (TextBundle textBundle = new TextBundleDir(path)) {
//            MetaData metaData = textBundle.readMetaData();
//            metaData.setCreatorIdentifier("Keepmark");
//            metaData.setTransient(true);
//            textBundle.writeMetaData(metaData);

            TextContent content = new TextContent(ContentType.MARKDOWN);
            content.setContent("Testowy plik");
            Asset asset1 = new Asset("me.jpg", Paths.get("/home/tchudyk/dscf3303.jpg"));
            Asset asset2 = new Asset("me2.jpg", Paths.get("/home/tchudyk/facebook.jpg"));

            textBundle.writeContent(content)
                    .writeAsset(asset1)
                    .writeAsset(asset2);

            Set<Asset> assets = textBundle.readAssets();
            System.out.println(assets.iterator().next().readContent().length);

//            ((TextPack) textBundle).unpackTo(Paths.get("/tmp/a2"));
            ((TextBundleDir) textBundle).packTo(Paths.get("/tmp/a2/test.textpack"));
        }

    }
}
