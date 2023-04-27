package no.ntnu.nms.exception;

/**
 * Class for handling exceptions.
 */
public class LedgerException extends RuntimeException {
    /**
     * Constructor for LedgerException.
     * @param message the message to be displayed
     */
    public LedgerException(String message) {
        super(message);
    }
}
