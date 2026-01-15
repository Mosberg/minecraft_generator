# Minecraft Asset Generator - AI Coding Guidelines

## Project Architecture

- **Purpose**: JSON-driven Minecraft Java Edition asset generator with schema validation and texture recoloring
- **Entry Point**: `dk.mosberg.generator.Generator` (main class defined in `gradle.properties`)
- **Data Flow**: JSON configs in `src/main/resources` → Generator → Assets in `output/` directory
- **Modular Design**: Extensible for new asset types via JSON schemas in `src/main/resources/schemas/`

## Build System & Workflows

- **Primary Build Tool**: Gradle with custom tasks (`generate`, `cleanGenerated`, `regenerate`)
- **Generation Command**: `./gradlew generate` (uses `generator_input_dir` and `generator_output_dir` from `gradle.properties`)
- **Distribution**: Use `shadowJar` for fat JAR (includes all dependencies), `distZip` for complete distribution
- **Testing**: JUnit Jupiter (v6.0.2) with `useJUnitPlatform()` and custom logging configuration

## Key Configuration Files

- **`gradle.properties`**: Centralized version management and project settings (Java 21, dependency versions)
- **`build.gradle`**: Custom tasks for asset generation workflow, manifest configuration, and distribution setup
- **Compiler Settings**: Java 21 with `-Xlint:deprecation` and `-Xlint:unchecked` flags

## Dependencies & Libraries

- **JSON Processing**: Gson 2.13.2 for parsing configuration files
- **Logging**: SLF4J 2.0.17 with simple runtime binding (keep lightweight)
- **Annotations**: JetBrains Annotations 26.0.2 for null-safety
- **Shadow Plugin**: 9.3.1 for creating runnable fat JARs with merged service files

## Code Patterns

- **Package Structure**: `dk.mosberg.generator.*` - follow this naming convention
- **Resource Organization**: Place JSON schemas in `src/main/resources/schemas/`, textures in `src/main/resources/textures/`
- **JAR Manifest**: Include main class and implementation details as configured in `build.gradle` tasks
- **Error Handling**: Use SLF4J logging for diagnostics (configured for console output)

## Development Workflow

1. Edit JSON configurations in `src/main/resources/`
2. Run `./gradlew generate` to produce assets in `output/` directory
3. Use `./gradlew regenerate` for clean rebuilds
4. Build distributions with `./gradlew shadowJar` or `./gradlew distZip`

## File References

- [build.gradle](../build.gradle): Custom generation tasks and arguments handling
- [gradle.properties](../gradle.properties): Main class and directory configuration
- [src/main/resources/](../src/main/resources/): Input directory structure for JSON configs and assets</content>
  <parameter name="filePath">c:\Users\Rasmu\Documents\Projects\Java\minecraft_generator\.github\copilot-instructions.md
