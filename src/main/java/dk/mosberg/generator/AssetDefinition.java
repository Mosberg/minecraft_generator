package dk.mosberg.generator;

import java.util.Map;

/**
 * Represents a Minecraft asset definition parsed from JSON.
 */
public class AssetDefinition {
    private String type; // e.g. block, item, recipe
    private String name;
    private Map<String, Object> properties;

    public AssetDefinition(String type, String name, Map<String, Object> properties) {
        this.type = type;
        this.name = name;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
