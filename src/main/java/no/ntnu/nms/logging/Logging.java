package no.ntnu.nms.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.*;

/**
 * Logger class for the application.
 *
 * <p>Remember to call {@link Logging#setUpLogger(String)} before getting and using the logger.
 */
public class Logging {

    private static final String LOG_PATH = "./logs/";

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
        SimpleFormatter formatter = new SimpleFormatter() {
            private static final String format =
                    "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        lr.getMillis(),
                        lr.getSourceClassName(),
                        lr.getSourceMethodName(),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage(),
                        System.lineSeparator());
            }
        };
        Files.createDirectories(Paths.get(LOG_PATH));
        Handler fh = new FileHandler(LOG_PATH + "nms_software.log", 5242880, 5, true);
        fh.setFormatter(formatter);
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
