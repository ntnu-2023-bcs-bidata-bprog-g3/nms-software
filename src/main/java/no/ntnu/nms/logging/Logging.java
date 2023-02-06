package no.ntnu.nms.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logger class for the application.
 *
 * <p>Remember to call {@link Logging#setUpLogger(String)} before getting and using the logger.
 */
public class Logging {

    private static final String LOG_PATH = "./logs/src/";

    /**
     * Logger object to be returned when getter is called.
     */
    private static Logger logger;

    /**
     * Creating and setting up a logger to use for logging.
     *
     * @param logLevel the level of logging the logger should include. Should be part of
     *                 the {@link Level} class.
     * @throws IOException thrown if there is an error with creating or writing to the file.
     */
    public static void setUpLogger(String logLevel) throws IOException {
        Files.createDirectories(Paths.get(LOG_PATH));
        Handler fh = new FileHandler("nms_software.log", 5242880, 5, true);
        fh.setFormatter(new SimpleFormatter());
        logger = Logger.getLogger("src");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.parse(logLevel));
        logger.addHandler(fh);
        logger.info("Logger initialized");
    }

    /**
     * getter for the static logger object, used for logging correctly.
     *
     * @return logger object used for logging.
     * @throws NullPointerException if {@link Logging#setUpLogger(String)} is not yet called.
     */
    public static Logger getLogger() throws NullPointerException {
        if (logger != null) {
            return logger;
        } else {
            throw new NullPointerException("The logger is not yet set up, and cannot be returned.");
        }
    }
}
