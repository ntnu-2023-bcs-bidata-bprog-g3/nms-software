package no.ntnu.nms.exception;

/**
 * Exception class for FileHandler.
 */
public class FileHandlerException extends RuntimeException {
    /**
     * Constructor for FileHandlerException.
     * @param message
     */
    public FileHandlerException(String message) {
            super(message);
        }

    /**
     * Constructor for FileHandlerException.
     * @param message
     * @param cause
     */
    public FileHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

}
