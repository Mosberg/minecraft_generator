package dk.mosberg.generator;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Handles reading and validating JSON asset configuration files.
 */
public class AssetConfigLoader {
    private final Gson gson = new Gson();
    private final SchemaValidator schemaValidator;

    public AssetConfigLoader(SchemaValidator schemaValidator) {
        this.schemaValidator = schemaValidator;
    }

    /**
     * Loads and validates asset definitions from a JSON file.
     * 
     * @param jsonPath Path to the JSON file
     * @param schemaPath Path to the JSON schema file
     * @return List of AssetDefinition objects
     * @throws IOException if file read fails
     */
    public List<AssetDefinition> loadAssets(String jsonPath, String schemaPath) throws IOException {
        // Validate JSON against schema
        if (!schemaValidator.validate(jsonPath, schemaPath)) {
            throw new IOException("JSON validation failed for " + jsonPath);
        }
        // Parse JSON
        try (FileReader reader = new FileReader(jsonPath)) {
            Type listType = new TypeToken<ArrayList<AssetDefinition>>() {}.getType();
            return gson.fromJson(reader, listType);
        }
    }
}
