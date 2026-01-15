package dk.mosberg.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized SLF4J logger for diagnostics and error reporting.
 */
public class Log {
    private static final Logger logger = LoggerFactory.getLogger("MinecraftAssetGenerator");

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void warn(String msg) {
        logger.warn(msg);
    }

    public static void error(String msg) {
        logger.error(msg);
    }

    public static void error(String msg, Throwable t) {
        logger.error(msg, t);
    }
}
