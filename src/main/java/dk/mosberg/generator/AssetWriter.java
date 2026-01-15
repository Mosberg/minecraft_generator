package dk.mosberg.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AssetWriter {

    private final Path outputRoot;
    private final boolean dryRun;

    public AssetWriter(Path outputRoot, boolean dryRun) {
        this.outputRoot = outputRoot;
        this.dryRun = dryRun;
    }

    public void writeJson(Path relativePath, JsonElement json) throws Exception {
        String content = FileUtils.PRETTY_GSON.toJson(json) + "\n";
        writeBytes(relativePath, content.getBytes(StandardCharsets.UTF_8));
    }

    public void copyPng(Path from, Path relativeTo) throws Exception {
        byte[] bytes = FileUtils.readBytes(from);
        writeBytes(relativeTo, bytes);
    }

    public void writePng(Path relativePath, BufferedImage image) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        writeBytes(relativePath, baos.toByteArray());
    }

    public void mergeLang(String modid, String langCode, Map<String, String> additions)
            throws Exception {
        Path rel = AssetsPaths.lang(modid, langCode);
        Path abs = outputRoot.resolve(rel);

        JsonObject merged = new JsonObject();
        if (FileUtils.exists(abs)) {
            try {
                merged = JsonParser.parseString(FileUtils.readString(abs)).getAsJsonObject();
            } catch (Exception e) {
                Log.warn("Existing lang file invalid; overwriting: " + abs);
            }
        }

        // Preserve insertion order in writing by building a LinkedHashMap-like merge.
        Map<String, String> out = new LinkedHashMap<>();
        for (String k : merged.keySet())
            out.put(k, merged.get(k).getAsString());
        out.putAll(additions);

        JsonObject json = new JsonObject();
        for (Map.Entry<String, String> e : out.entrySet())
            json.addProperty(e.getKey(), e.getValue());

        writeJson(rel, json);
    }

    private void writeBytes(Path relativePath, byte[] bytes) throws Exception {
        Path out = outputRoot.resolve(relativePath);
        if (dryRun) {
            Log.info("[dry-run] write " + relativePath + " (" + bytes.length + " bytes)");
            return;
        }
        FileUtils.writeBytesIfChanged(out, bytes);
    }
}
