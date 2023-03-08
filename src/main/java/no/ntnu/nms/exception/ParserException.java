package no.ntnu.nms.exception;

public class ParserException extends RuntimeException {
    /**
     * Constructor for ParserException
     * @param message the message to be displayed
     */
    public ParserException(String message) {
        super(message);
    }
}
