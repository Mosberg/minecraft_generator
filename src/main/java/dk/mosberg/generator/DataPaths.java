package dk.mosberg.generator;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class DataPaths {

    private DataPaths() {}

    public static Path recipe(String modid, String name) {
        return Paths.get("data", modid, "recipes", name + ".json");
    }
}
