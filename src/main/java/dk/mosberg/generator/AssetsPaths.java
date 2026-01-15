package dk.mosberg.generator;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class AssetsPaths {

    private AssetsPaths() {}

    public static Path blockstate(String modid, String name) {
        return Paths.get("assets", modid, "blockstates", name + ".json");
    }

    public static Path modelBlock(String modid, String name) {
        return Paths.get("assets", modid, "models", "block", name + ".json");
    }

    public static Path modelItem(String modid, String name) {
        return Paths.get("assets", modid, "models", "item", name + ".json");
    }

    public static Path textureBlock(String modid, String name) {
        return Paths.get("assets", modid, "textures", "block", name + ".png");
    }

    public static Path textureItem(String modid, String name) {
        return Paths.get("assets", modid, "textures", "item", name + ".png");
    }

    public static Path lang(String modid, String langCode) {
        return Paths.get("assets", modid, "lang", langCode + ".json");
    }
}
