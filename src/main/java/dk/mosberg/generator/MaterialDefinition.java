package dk.mosberg.generator;

import com.google.gson.JsonObject;

/**
 * Matches the important fields from material.schema.json. Unknown fields can live in "properties"
 * as a free-form bag.
 */
public record MaterialDefinition(String schema, String id, String name, String category,
        String palette, TextureOverrides textureOverrides, JsonObject properties,
        JsonObject recipe) {
}
