package no.ntnu.nms.exception;

public class ExceptionHandler {

    public static String getMessage(Exception e) {
        return e.getMessage().replace("\"", "'");
    }

}
