package dk.mosberg.generator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class AssetConfigLoader {

    private final Path inputDir;
    private final SchemaValidator validator;

    public AssetConfigLoader(Path inputDir, SchemaValidator validator) {
        this.inputDir = inputDir;
        this.validator = validator;
    }

    public List<MaterialDefinition> loadMaterials() throws Exception {
        Path materialsDir = inputDir.resolve("materials");
        Path schemaPath = inputDir.resolve("schemas").resolve("material.schema.json");

        List<Path> files = FileUtils.listJsonFiles(materialsDir);
        List<MaterialDefinition> materials = new ArrayList<>();

        for (Path f : files) {
            if (!validator.validateFile(f, schemaPath)) {
                String msg = "Material failed schema validation: " + f;
                if (validator.strict())
                    throw new IllegalArgumentException(msg);
                Log.warn(msg);
                continue;
            }

            MaterialDefinition mat =
                    FileUtils.GSON.fromJson(FileUtils.readString(f), MaterialDefinition.class);
            if (mat == null) {
                String msg = "Material JSON is null/invalid: " + f;
                if (validator.strict())
                    throw new IllegalArgumentException(msg);
                Log.warn(msg);
                continue;
            }

            materials.add(mat);
        }

        Log.info("Loaded materials: " + materials.size());
        return materials;
    }
}
