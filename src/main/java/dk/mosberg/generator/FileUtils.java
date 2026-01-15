package dk.mosberg.generator;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.Gson;

/**
 * Utility for loading and saving files in the resource and output directories.
 */
public class FileUtils {
    private static final Gson gson = new Gson();

    public static <T> T readJsonFile(String path, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(path)) {
            return gson.fromJson(reader, clazz);
        }
    }

    public static void writeJsonFile(String path, Object obj) throws IOException {
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(obj, writer);
        }
    }
}
