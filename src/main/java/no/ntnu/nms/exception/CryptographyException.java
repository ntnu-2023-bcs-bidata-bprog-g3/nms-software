package no.ntnu.nms.exception;

public class CryptographyException extends RuntimeException {
    /**
     * Constructor for CryptographyException.
     * @param message the message to be displayed
     */
    public CryptographyException(String message) {
        super(message);
    }
}
