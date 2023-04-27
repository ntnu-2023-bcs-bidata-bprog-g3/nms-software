package no.ntnu.nms.exception;

/**
 * Class for exceptions related to cryptography.
 */
public class CryptographyException extends RuntimeException {
    /**
     * Constructor for CryptographyException.
     * @param message the message to be displayed
     */
    public CryptographyException(String message) {
        super(message);
    }
}
