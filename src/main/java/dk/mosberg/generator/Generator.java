package dk.mosberg.generator;

import java.nio.file.Path;
import java.util.List;

public final class Generator {

    public static void main(String[] args) {
        GeneratorConfig config;
        try {
            config = GeneratorConfig.fromArgs(args);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println();
            GeneratorConfig.printUsage(System.err);
            System.exit(2);
            return;
        }

        if (config.help()) {
            GeneratorConfig.printUsage(System.out);
            return;
        }

        if (config.modId().isBlank()) {
            System.err.println("Error: --modid is required.");
            System.err.println();
            GeneratorConfig.printUsage(System.err);
            System.exit(2);
            return;
        }

        Log.info("═══════════════════════════════════════");
        Log.info(" Minecraft Asset Generator (Fabric)");
        Log.info("═══════════════════════════════════════");
        Log.info("ModId:  " + config.modId());
        Log.info("Input:  " + config.inputDir().toAbsolutePath());
        Log.info("Output: " + config.outputDir().toAbsolutePath());
        Log.info("Strict: " + config.strict());
        Log.info("DryRun: " + config.dryRun());
        Log.info("Clean:  " + config.clean());

        try {
            if (config.clean()) {
                FileUtils.deleteDirectory(config.outputDir());
            }

            Path schemaDir = config.inputDir().resolve("schemas");
            SchemaValidator validator = new SchemaValidator(schemaDir, config.strict());
            AssetConfigLoader loader = new AssetConfigLoader(config.inputDir(), validator);

            List<MaterialDefinition> materials = loader.loadMaterials();

            AssetWriter writer = new AssetWriter(config.outputDir(), config.dryRun());

            FabricAssetGenerator generator = new FabricAssetGenerator(config, writer);
            generator.generate(materials);

            Log.info("✓ Generation complete.");
        } catch (Exception e) {
            Log.error("Generation failed: " + e.getMessage(), e);
            System.exit(1);
        }
    }
}
