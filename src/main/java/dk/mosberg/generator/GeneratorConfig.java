package dk.mosberg.generator;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * CLI config for the generator.
 *
 * Backwards-compatible: - If args are: <inputDir> <outputDir>, it still works (but --modid is
 * required).
 */
public record GeneratorConfig(String modId, Path inputDir, Path outputDir, String lang,
        boolean strict, boolean dryRun, boolean clean, boolean help) {
    public static GeneratorConfig fromArgs(String[] args) {
        String modid = "";
        Path input = Paths.get("src/main/resources");
        Path output = Paths.get("output");
        String lang = "en_us";
        boolean strict = true;
        boolean dryRun = false;
        boolean clean = false;
        boolean help = false;

        List<String> positional = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (!a.startsWith("--")) {
                positional.add(a);
                continue;
            }

            String key = a;
            String value = null;

            int eq = a.indexOf('=');
            if (eq > 0) {
                key = a.substring(0, eq);
                value = a.substring(eq + 1);
            } else if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                value = args[++i];
            }

            switch (key) {
                case "--help" -> help = true;
                case "--modid" -> modid = requireValue(key, value);
                case "--input" -> input = Paths.get(requireValue(key, value));
                case "--output" -> output = Paths.get(requireValue(key, value));
                case "--lang" -> lang = requireValue(key, value);
                case "--strict" -> strict = true;
                case "--no-strict" -> strict = false;
                case "--dry-run" -> dryRun = true;
                case "--clean" -> clean = true;
                default -> throw new IllegalArgumentException("Unknown option: " + key);
            }
        }

        // Backwards-compatible positional args: <inputDir> <outputDir>
        if (positional.size() >= 1)
            input = Paths.get(positional.get(0));
        if (positional.size() >= 2)
            output = Paths.get(positional.get(1));
        if (positional.size() > 2)
            throw new IllegalArgumentException("Too many positional args.");

        return new GeneratorConfig(modid, input, output, lang, strict, dryRun, clean, help);
    }

    private static String requireValue(String key, String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(key + " requires a value.");
        return value;
    }

    public static void printUsage(PrintStream out) {
        out.println("Usage:");
        out.println("  java -jar minecraft_generator.jar --modid <modid> [options]");
        out.println(
                "  (or) java -jar minecraft_generator.jar <inputDir> <outputDir> --modid <modid>");
        out.println();
        out.println("Options:");
        out.println("  --modid <id>       Required. Fabric mod id (namespace).");
        out.println("  --input <dir>      Input dir (default: src/main/resources).");
        out.println("  --output <dir>     Output dir (default: output).");
        out.println("  --lang <code>      Lang file (default: en_us).");
        out.println("  --strict           Fail on schema validation errors (default).");
        out.println("  --no-strict        Log validation errors but continue.");
        out.println("  --dry-run          Don't write files; only log writes.");
        out.println("  --clean            Delete output dir before generating.");
        out.println("  --help             Print this help.");
        out.println();
        out.println("Input conventions (inside input dir):");
        out.println("  schemas/*.schema.json");
        out.println("  materials/*.json");
        out.println(
                "  textures/material/<category>/<id>.png  (optional; copied to assets/<modid>/textures/...)");
    }
}
