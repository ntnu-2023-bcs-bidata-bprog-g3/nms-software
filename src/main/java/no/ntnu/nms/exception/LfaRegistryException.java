package no.ntnu.nms.exception;

/**
 * Class for exceptions related to the LFA registry.
 */
public class LfaRegistryException extends RuntimeException {
    /**
     * Constructor for CryptographyException.
     * @param message the message to be displayed
     */
    public LfaRegistryException(String message) {
        super(message);
    }
}
