package dk.mosberg.generator;

import com.google.gson.JsonObject;

public final class MinecraftFormatUtils {

    private MinecraftFormatUtils() {}

    public static JsonObject cubeAllBlockModel(String modid, String textureName) {
        // block/cube_all with texture 'all'
        JsonObject textures = new JsonObject();
        textures.addProperty("all", modid + ":block/" + textureName);

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/cube_all");
        model.add("textures", textures);
        return model;
    }

    public static JsonObject blockItemModel(String modid, String blockName) {
        // item model pointing to block model
        JsonObject model = new JsonObject();
        model.addProperty("parent", modid + ":block/" + blockName);
        return model;
    }

    public static JsonObject generatedItemModel(String modid, String textureName) {
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", modid + ":item/" + textureName);

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        model.add("textures", textures);
        return model;
    }

    public static JsonObject singletonBlockstate(String modid, String modelPathNoExt) {
        // { "variants": { "": { "model": "<modid>:block/<name>" } } }
        JsonObject model = new JsonObject();
        model.addProperty("model", modid + ":" + modelPathNoExt);

        JsonObject variant = new JsonObject();
        variant.add("", model);

        JsonObject root = new JsonObject();
        root.add("variants", variant);
        return root;
    }
}
