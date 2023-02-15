package no.ntnu.nms.exception;

public class LedgerException extends RuntimeException {
    /**
     * Constructor for LedgerException
     * @param message
     */
    public LedgerException(String message) {
        super(message);
    }

    /**
     * Constructor for LedgerException
     * @param message
     * @param cause
     */
    public LedgerException(String message, Throwable cause) {
        super(message, cause);
    }
}
