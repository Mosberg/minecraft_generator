package dk.mosberg.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;

public final class FileUtils {

    public static final Gson GSON = new Gson();
    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    private FileUtils() {}

    public static boolean exists(Path p) {
        return Files.exists(p);
    }

    public static String readString(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public static byte[] readBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    public static void ensureParent(Path file) throws IOException {
        Path parent = file.getParent();
        if (parent != null)
            Files.createDirectories(parent);
    }

    public static void writeBytesIfChanged(Path file, byte[] bytes) throws Exception {
        ensureParent(file);

        if (Files.exists(file)) {
            byte[] old = Files.readAllBytes(file);
            if (MessageDigest.isEqual(old, bytes)) {
                Log.info("unchanged " + file);
                return;
            }
        }

        Files.write(file, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Log.info("wrote     " + file + " (sha1=" + sha1(bytes) + ")");
    }

    private static String sha1(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return HexFormat.of().formatHex(md.digest(bytes));
    }

    public static List<Path> listJsonFiles(Path dir) throws IOException {
        List<Path> out = new ArrayList<>();
        if (!Files.exists(dir))
            return out;

        try (var stream = Files.walk(dir)) {
            stream.filter(
                    p -> Files.isRegularFile(p) && p.getFileName().toString().endsWith(".json"))
                    .forEach(out::add);
        }

        out.sort(Comparator.naturalOrder());
        return out;
    }

    public static void deleteDirectory(Path dir) throws IOException {
        if (!Files.exists(dir))
            return;
        try (var stream = Files.walk(dir)) {
            stream.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        Log.info("deleted  " + dir);
    }
}
