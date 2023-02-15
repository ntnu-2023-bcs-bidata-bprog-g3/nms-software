package no.ntnu.nms.exception;

public class CryptographyException extends RuntimeException {
    /**
     * Constructor for CryptographyException.
     * @param message
     */
    public CryptographyException(String message) {
        super(message);
    }

    /**
     * Constructor for CryptographyException.
     * @param message
     * @param cause
     */
    public CryptographyException(String message, Throwable cause) {
        super(message, cause);
    }
}
