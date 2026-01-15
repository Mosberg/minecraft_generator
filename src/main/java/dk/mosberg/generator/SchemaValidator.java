package dk.mosberg.generator;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Pragmatic JSON schema validator for this generator. Supports the subset used by your schemas: -
 * type, required, properties, additionalProperties, enum, const - min/max length, min/max items -
 * pattern for strings - $ref to other files and #/$defs paths - oneOf (tries each branch)
 */
public final class SchemaValidator {

    private final Path schemaDir;
    private final boolean strict;
    private final Map<Path, JsonObject> schemaCache = new ConcurrentHashMap<>();

    public SchemaValidator(Path schemaDir, boolean strict) {
        this.schemaDir = schemaDir;
        this.strict = strict;
    }

    public boolean strict() {
        return strict;
    }

    public boolean validateFile(Path jsonFile, Path schemaFile) {
        try {
            JsonElement json = JsonParser.parseString(FileUtils.readString(jsonFile));
            JsonObject schema = loadSchema(schemaFile);
            validate(json, schema, schemaFile);
            return true;
        } catch (Exception e) {
            Log.error("Schema validation error: " + jsonFile + " -> " + e.getMessage());
            if (strict)
                Log.error("Validation stack:", e);
            return false;
        }
    }

    private JsonObject loadSchema(Path schemaFile) {
        return schemaCache.computeIfAbsent(schemaFile.normalize(), p -> {
            try {
                return JsonParser.parseString(FileUtils.readString(p)).getAsJsonObject();
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Failed to read schema: " + p + " (" + e.getMessage() + ")", e);
            }
        });
    }

    private void validate(JsonElement instance, JsonObject schema, Path currentSchemaFile) {
        // Resolve $ref
        if (schema.has("$ref")) {
            JsonObject resolved = resolveRef(schema.get("$ref").getAsString(), currentSchemaFile);
            validate(instance, resolved, currentSchemaFile);
            return;
        }

        // oneOf
        if (schema.has("oneOf")) {
            JsonArray arr = schema.getAsJsonArray("oneOf");
            for (JsonElement option : arr) {
                try {
                    validate(instance, option.getAsJsonObject(), currentSchemaFile);
                    return;
                } catch (Exception ignored) {
                }
            }
            throw new IllegalArgumentException("oneOf: no option matched.");
        }

        // type
        if (schema.has("type")) {
            String type = schema.get("type").getAsString();
            if (!matchesType(instance, type)) {
                throw new IllegalArgumentException("Expected type '" + type + "'");
            }
        }

        // const
        if (schema.has("const")) {
            JsonElement c = schema.get("const");
            if (!c.equals(instance)) {
                throw new IllegalArgumentException("Expected const " + c + " but got " + instance);
            }
        }

        // enum
        if (schema.has("enum")) {
            boolean ok = false;
            for (JsonElement e : schema.getAsJsonArray("enum")) {
                if (e.equals(instance)) {
                    ok = true;
                    break;
                }
            }
            if (!ok)
                throw new IllegalArgumentException("Value not in enum: " + instance);
        }

        // string constraints
        if (instance.isJsonPrimitive() && instance.getAsJsonPrimitive().isString()) {
            String s = instance.getAsString();
            if (schema.has("minLength") && s.length() < schema.get("minLength").getAsInt()) {
                throw new IllegalArgumentException("minLength violated");
            }
            if (schema.has("maxLength") && s.length() > schema.get("maxLength").getAsInt()) {
                throw new IllegalArgumentException("maxLength violated");
            }
            if (schema.has("pattern")) {
                Pattern p = Pattern.compile(schema.get("pattern").getAsString());
                if (!p.matcher(s).matches())
                    throw new IllegalArgumentException("pattern violated");
            }
        }

        // array constraints
        if (instance.isJsonArray()) {
            JsonArray arr = instance.getAsJsonArray();
            if (schema.has("minItems") && arr.size() < schema.get("minItems").getAsInt()) {
                throw new IllegalArgumentException("minItems violated");
            }
            if (schema.has("maxItems") && arr.size() > schema.get("maxItems").getAsInt()) {
                throw new IllegalArgumentException("maxItems violated");
            }
            if (schema.has("items")) {
                JsonObject itemsSchema = schema.getAsJsonObject("items");
                for (JsonElement el : arr)
                    validate(el, itemsSchema, currentSchemaFile);
            }
        }

        // object constraints
        if (instance.isJsonObject()) {
            JsonObject obj = instance.getAsJsonObject();

            if (schema.has("required")) {
                for (JsonElement req : schema.getAsJsonArray("required")) {
                    String k = req.getAsString();
                    if (!obj.has(k))
                        throw new IllegalArgumentException("Missing required field: " + k);
                }
            }

            if (schema.has("properties")) {
                JsonObject props = schema.getAsJsonObject("properties");
                for (Map.Entry<String, JsonElement> e : props.entrySet()) {
                    String key = e.getKey();
                    if (obj.has(key)) {
                        validate(obj.get(key), e.getValue().getAsJsonObject(), currentSchemaFile);
                    }
                }
            }

            if (schema.has("additionalProperties")
                    && !schema.get("additionalProperties").getAsBoolean()) {
                // Only enforce if we know the "properties" set.
                if (schema.has("properties")) {
                    JsonObject props = schema.getAsJsonObject("properties");
                    for (String key : obj.keySet()) {
                        if (!props.has(key)) {
                            throw new IllegalArgumentException(
                                    "additionalProperties=false but found: " + key);
                        }
                    }
                }
            }
        }
    }

    private boolean matchesType(JsonElement el, String type) {
        return switch (type) {
            case "object" -> el.isJsonObject();
            case "array" -> el.isJsonArray();
            case "string" -> el.isJsonPrimitive() && el.getAsJsonPrimitive().isString();
            case "boolean" -> el.isJsonPrimitive() && el.getAsJsonPrimitive().isBoolean();
            case "integer" -> el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber()
                    && isInteger(el.getAsDouble());
            case "number" -> el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber();
            default -> true; // unknown types ignored
        };
    }

    private boolean isInteger(double d) {
        return Math.floor(d) == d && !Double.isInfinite(d) && !Double.isNaN(d);
    }

    private JsonObject resolveRef(String ref, Path currentSchemaFile) {
        // Examples in your schemas:
        // - "common.schema.json#/$defs/snakeId"
        // - "#/$defs/hex6"
        if (ref.startsWith("#/")) {
            JsonObject base = loadSchema(currentSchemaFile);
            return resolvePointer(base, ref.substring(2));
        }

        String[] parts = ref.split("#", 2);
        Path target = schemaDir.resolve(parts[0]).normalize();
        JsonObject root = loadSchema(target);

        if (parts.length == 1)
            return root;
        String pointer = parts[1];
        if (pointer.startsWith("/"))
            pointer = pointer.substring(1);
        return resolvePointer(root, pointer);
    }

    private JsonObject resolvePointer(JsonObject root, String pointer) {
        // Supports pointers like: $defs/snakeId
        String[] segs = pointer.split("/");
        JsonElement cur = root;
        for (String seg : segs) {
            if (!cur.isJsonObject())
                throw new IllegalArgumentException("Invalid $ref pointer: " + pointer);
            JsonObject o = cur.getAsJsonObject();
            if (!o.has(seg))
                throw new IllegalArgumentException("Invalid $ref pointer: " + pointer);
            cur = o.get(seg);
        }
        if (!cur.isJsonObject())
            throw new IllegalArgumentException("$ref did not resolve to object: " + pointer);
        return cur.getAsJsonObject();
    }
}
