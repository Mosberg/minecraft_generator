# Minecraft Asset Generator — AI Coding Guidelines

## Architecture Overview

- **Purpose:** Generate Minecraft Java Edition assets (models, blockstates, recipes, lang, textures) from schema-validated JSON and reproducible texture recoloring.
- **Main Entry Point:** [src/main/java/dk/mosberg/generator/Generator.java](../src/main/java/dk/mosberg/generator/Generator.java) (class name set in [gradle.properties](../gradle.properties))
- **Data Flow:** JSON configs in [src/main/resources/](../src/main/resources/) → Generator → output in [output/](../output/)
- **Extensibility:** Add new asset types via JSON schemas ([src/main/resources/schemas/](../src/main/resources/schemas/)).

## Build & Workflow

- **Build Tool:** Gradle (see [build.gradle](../build.gradle))
- **Key Tasks:**
  - `generate`: Runs asset generation (`./gradlew generate`).
  - `cleanGenerated`: Removes generated assets (`./gradlew cleanGenerated`).
  - `regenerate`: Cleans and regenerates assets (`./gradlew regenerate`).
  - `shadowJar`: Builds fat JAR with dependencies (`./gradlew shadowJar`).
  - `distZip`: Creates ZIP distribution (`./gradlew distZip`).
- **Testing:** JUnit Jupiter (v6.0.2) with custom logging (see [build.gradle](../build.gradle)).
- **Run Application:** `./gradlew run` (uses main class from [gradle.properties](../gradle.properties)).

## Configuration & Conventions

- **Central Config:** [gradle.properties](../gradle.properties) — controls Java version, dependency versions, input/output directories, main class.
- **Compiler:** Java 21, strict linting (`-Xlint:deprecation`, `-Xlint:unchecked`).
- **Dependencies:**
  - Gson 2.13.2 (JSON parsing)
  - SLF4J 2.0.17 (logging, console output)
  - JetBrains Annotations 26.0.2 (null-safety)
  - Shadow plugin 9.3.1 (fat JAR)
- **Package Naming:** Use `dk.mosberg.generator.*` for all source files.
- **Resource Layout:**
  - Place JSON schemas in [src/main/resources/schemas/](../src/main/resources/schemas/)
  - Place base textures in [src/main/resources/textures/](../src/main/resources/textures/)
- **Manifest:** JAR manifest includes main class and implementation details (see [build.gradle](../build.gradle)).
- **Error Handling:** Use SLF4J for all diagnostics.

## Example Workflow

1. Edit JSON configs in [src/main/resources/](../src/main/resources/)
2. Run `./gradlew generate` to produce assets in [output/](../output/)
3. Use `./gradlew regenerate` for a clean rebuild
4. Build distributions with `./gradlew shadowJar` or `./gradlew distZip`

## Key File References

- [build.gradle](../build.gradle): Custom generator tasks and argument handling
- [gradle.properties](../gradle.properties): Entrypoint, directories, and version management
- [src/main/resources/](../src/main/resources/): Input configs and templates
- [output/](../output/): Generated assets

---

For new asset types, add schemas to [src/main/resources/schemas/](../src/main/resources/schemas/). For new generation logic, extend [src/main/java/dk/mosberg/generator/Generator.java](../src/main/java/dk/mosberg/generator/Generator.java) and follow the established package/resource conventions.
<parameter name="filePath">c:\Users\Rasmu\Documents\Projects\Java\minecraft_generator\.github\copilot-instructions.md
