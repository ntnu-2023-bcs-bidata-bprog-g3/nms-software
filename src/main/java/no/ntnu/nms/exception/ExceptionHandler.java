package no.ntnu.nms.exception;

/**
 * Class for handling exceptions, for removing illegal characters.
 */
public class ExceptionHandler {

    /**
     * Method for removing illegal characters from an exception message.
     * @param e the exception
     * @return the exception message without illegal characters
     */
    public static String getMessage(Exception e) {
        return e.getMessage().replace("\"", "'");
    }

}
