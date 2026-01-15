package dk.mosberg.generator;

import java.io.IOException;
import java.util.List;

/**
 * Handles writing generated assets to the output directory.
 */
public class AssetWriter {
    private final String outputDir;

    public AssetWriter(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Writes asset definitions to files in the output directory.
     * 
     * @param assets List of AssetDefinition objects
     * @throws IOException if writing fails
     */
    public void writeAssets(List<AssetDefinition> assets) throws IOException {
        for (AssetDefinition asset : assets) {
            String fileName = outputDir + "/" + asset.getName() + ".json";
            FileUtils.writeJsonFile(fileName, asset);
        }
    }
}
