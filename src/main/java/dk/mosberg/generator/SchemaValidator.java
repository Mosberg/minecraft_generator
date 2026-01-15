package dk.mosberg.generator;

import java.io.FileReader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility for validating asset JSON against schemas using Gson only.
 */
public class SchemaValidator {
    private final Gson gson = new Gson();

    /**
     * Validates a JSON file against a schema file (structure only, not full JSON Schema spec).
     *
     * @param jsonPath Path to JSON file
     * @param schemaPath Path to schema file
     * @return true if valid, false otherwise
     */
    public boolean validate(String jsonPath, String schemaPath) {
        try (FileReader jsonReader = new FileReader(jsonPath);
                FileReader schemaReader = new FileReader(schemaPath)) {
            JsonObject json = JsonParser.parseReader(jsonReader).getAsJsonObject();
            JsonObject schema = JsonParser.parseReader(schemaReader).getAsJsonObject();
            // Basic validation: check required fields from schema exist in json
            if (schema.has("required")) {
                for (JsonElement req : schema.getAsJsonArray("required")) {
                    String key = req.getAsString();
                    if (!json.has(key)) {
                        Log.error("Missing required field: " + key);
                        return false;
                    }
                }
            }
            // Optionally, add more checks here (type, enum, etc.)
            return true;
        } catch (Exception e) {
            Log.error("Schema validation failed: " + e.getMessage(), e);
            return false;
        }
    }
}
