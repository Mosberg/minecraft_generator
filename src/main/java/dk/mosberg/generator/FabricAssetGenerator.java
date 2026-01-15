package dk.mosberg.generator;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;

public final class FabricAssetGenerator {

    private final GeneratorConfig config;
    private final AssetWriter writer;

    public FabricAssetGenerator(GeneratorConfig config, AssetWriter writer) {
        this.config = config;
        this.writer = writer;
    }

    public void generate(List<MaterialDefinition> materials) throws Exception {
        for (MaterialDefinition mat : materials) {
            generateMaterial(mat);
        }
    }

    private void generateMaterial(MaterialDefinition mat) throws Exception {
        String id = mat.id();
        String modid = config.modId();

        // Decide whether this material is primarily a block or item by category heuristics.
        boolean looksLikeBlock =
                mat.category().contains("block") || mat.category().contains("glass")
                        || mat.category().contains("wood") || mat.category().contains("plank");

        // Texture selection:
        // 1) explicit override in material JSON (resource-relative)
        // 2) default convention: textures/material/<category>/<id>.png inside input dir
        Path inputRoot = config.inputDir();
        String textureRel = null;

        if (mat.textureOverrides() != null) {
            if (looksLikeBlock && mat.textureOverrides().block() != null) {
                textureRel = mat.textureOverrides().block();
            } else if (!looksLikeBlock && mat.textureOverrides().item() != null) {
                textureRel = mat.textureOverrides().item();
            }
        }

        if (textureRel == null) {
            textureRel = "textures/material/" + mat.category() + "/" + id + ".png";
        }

        // Copy texture if exists. If it doesn't exist, still generate JSON; user can supply texture
        // later.
        if (FileUtils.exists(inputRoot.resolve(textureRel))) {
            if (looksLikeBlock) {
                writer.copyPng(inputRoot.resolve(textureRel), AssetsPaths.textureBlock(modid, id));
            } else {
                writer.copyPng(inputRoot.resolve(textureRel), AssetsPaths.textureItem(modid, id));
            }
        } else {
            Log.warn("Missing texture (skipped copy): " + inputRoot.resolve(textureRel));
        }

        if (looksLikeBlock) {
            writeBlockAssets(modid, id, mat.name());
        } else {
            writeItemAssets(modid, id, mat.name());
        }

        if (mat.recipe() != null) {
            // Write recipe JSON exactly as provided (schema enforces common structure).
            writer.writeJson(DataPaths.recipe(modid, id), mat.recipe());
        }
    }

    private void writeBlockAssets(String modid, String id, String displayName) throws Exception {
        // blockstate
        JsonObject blockstate = MinecraftFormatUtils.singletonBlockstate(modid, "block/" + id);
        writer.writeJson(AssetsPaths.blockstate(modid, id), blockstate);

        // block model
        JsonObject blockModel = MinecraftFormatUtils.cubeAllBlockModel(modid, id);
        writer.writeJson(AssetsPaths.modelBlock(modid, id), blockModel);

        // item model (points to block model)
        JsonObject itemModel = MinecraftFormatUtils.blockItemModel(modid, id);
        writer.writeJson(AssetsPaths.modelItem(modid, id), itemModel);

        // lang entry
        Map<String, String> lang = new LinkedHashMap<>();
        lang.put("block." + modid + "." + id, displayName);
        writer.mergeLang(modid, config.lang(), lang);
    }

    private void writeItemAssets(String modid, String id, String displayName) throws Exception {
        // item model (generated)
        JsonObject itemModel = MinecraftFormatUtils.generatedItemModel(modid, id);
        writer.writeJson(AssetsPaths.modelItem(modid, id), itemModel);

        // lang entry
        Map<String, String> lang = new LinkedHashMap<>();
        lang.put("item." + modid + "." + id, displayName);
        writer.mergeLang(modid, config.lang(), lang);
    }
}
