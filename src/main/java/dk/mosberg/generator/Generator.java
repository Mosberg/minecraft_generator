package dk.mosberg.generator;

import java.util.List;

public class Generator {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar minecraft_generator.jar <inputDir> <outputDir>");
            return;
        }
        String inputDir = args[0];
        String outputDir = args[1];

        try {
            SchemaValidator schemaValidator = new SchemaValidator();
            AssetConfigLoader loader = new AssetConfigLoader(schemaValidator);
            AssetWriter writer = new AssetWriter(outputDir);

            // Example: load assets.json and schema.json from inputDir
            String assetJson = inputDir + "/assets.json";
            String schemaJson = inputDir + "/schema.json";
            List<AssetDefinition> assets = loader.loadAssets(assetJson, schemaJson);

            writer.writeAssets(assets);
            System.out.println("✓ Asset generation complete. Output: " + outputDir);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
