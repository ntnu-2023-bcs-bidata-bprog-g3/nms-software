package no.ntnu.nms.exception;

/**
 * Exception class for the LicenseGenerator.
 */
public class LicenseGeneratorException extends RuntimeException {
    /**
     * Constructor for CryptographyException.
     * @param message the message to be displayed
     */
    public LicenseGeneratorException(String message) {
        super(message);
    }
}
