package no.ntnu.nms.exception;

/**
 * Exception class for FileHandler.
 */
public class FileHandlerException extends RuntimeException {
    /**
     * Constructor for FileHandlerException.
     * @param message the message to be displayed
     */
    public FileHandlerException(String message) {
        super(message);
    }
}
