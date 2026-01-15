# Minecraft Asset Generator

A standalone Java tool that generates Minecraft Java Edition assets (models, blockstates, recipes, lang, textures) from **schema-validated** JSON inputs and reproducible texture recoloring.

## Features

- **JSON-Driven Generation**: Define assets using structured JSON configurations with built-in schema validation.
- **Material-Based Assets**: Generate blocks, items, recipes, and textures based on material definitions.
- **Texture Recoloring**: Automatically recolor base textures to create variants.
- **Modular Architecture**: Easily extensible for new asset types and generation rules.
- **Cross-Platform**: Runs on Windows, macOS, and Linux.
- **Build Integration**: Gradle-based build system with custom tasks for asset generation.

## Requirements

- Java 21 or higher
- Gradle 8.0+ (wrapper included)

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/mosberg/minecraft_generator.git
   cd minecraft_generator
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

## Usage

### Generating Assets

Run the generator with default settings:

```bash
./gradlew generate
```

This will process JSON files in `src/main/resources` and output generated assets to the `output` directory.

### Custom Input/Output Directories

Specify custom directories:

```bash
./gradlew generate --args="path/to/input path/to/output"
```

### Cleaning Generated Assets

Remove all generated assets:

```bash
./gradlew cleanGenerated
```

### Regenerate All Assets

Clean and regenerate in one step:

```bash
./gradlew regenerate
```

### Running the Application

Execute the generator as a standalone application:

```bash
./gradlew run
```

## Configuration

Edit `gradle.properties` to customize:

- `generator_input_dir`: Input directory for JSON configurations (default: `src/main/resources`)
- `generator_output_dir`: Output directory for generated assets (default: `output`)
- `java_version`: Target Java version (default: 21)
- Library versions for dependencies

## Project Structure

```
minecraft_generator/
├── src/main/java/dk/mosberg/          # Main source code
├── src/main/resources/                # JSON configurations and templates
├── output/                            # Generated assets (created by generator)
├── build.gradle                       # Build configuration
├── gradle.properties                  # Project properties
└── settings.gradle                    # Gradle settings
```

## Building and Distribution

### Create Distribution Archive

Build a ZIP distribution with the runnable JAR and resources:

```bash
./gradlew distZip
```

The archive will be created in `build/distributions/`.

### Create Fat JAR

Build a shaded JAR with all dependencies included:

```bash
./gradlew shadowJar
```

The JAR will be created in `build/libs/` with classifier `all`.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

Mosberg
