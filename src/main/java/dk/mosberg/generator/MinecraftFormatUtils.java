package dk.mosberg.generator;

import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Utility for handling Minecraft-specific asset formats (models, blockstates, recipes, lang).
 */
public class MinecraftFormatUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String toModelJson(Map<String, Object> properties) {
        // Example: create a basic block model
        Map<String, Object> model =
                Map.of("parent", properties.getOrDefault("parent", "block/cube_all"), "textures",
                        properties.get("textures"));
        return gson.toJson(model);
    }

    public static String toBlockstateJson(Map<String, Object> properties) {
        // Example: create a simple blockstate
        Map<String, Object> blockstate = Map.of("variants", properties.get("variants"));
        return gson.toJson(blockstate);
    }

    public static String toRecipeJson(Map<String, Object> properties) {
        // Example: shaped recipe
        Map<String, Object> recipe =
                Map.of("type", properties.getOrDefault("type", "minecraft:crafting_shaped"),
                        "pattern", properties.get("pattern"), "key", properties.get("key"),
                        "result", properties.get("result"));
        return gson.toJson(recipe);
    }

    public static String toLangEntry(String key, String value) {
        return String.format("\"%s\": \"%s\"", key, value);
    }
}
