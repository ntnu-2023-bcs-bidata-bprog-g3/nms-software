package no.ntnu.nms.exception;

public class ParserException extends RuntimeException {
    /**
     * Constructor for ParserException
     * @param message the message to be displayed
     */
    public ParserException(String message) {
        super(message);
    }

    /**
     * Constructor for LedgerException
     * @param message the message to be displayed
     * @param cause the cause of the exception
     */
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
