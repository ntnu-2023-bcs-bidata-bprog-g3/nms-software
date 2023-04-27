package no.ntnu.nms.exception;

/**
 * Class for exceptions related to cryptography.
 */
public class ParserException extends RuntimeException {
    /**
     * Constructor for ParserException.
     * @param message the message to be displayed
     */
    public ParserException(String message) {
        super(message);
    }
}
