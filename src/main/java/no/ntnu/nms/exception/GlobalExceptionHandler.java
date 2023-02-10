package no.ntnu.nms.exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

/**
 * Exception class for FileHandler.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Constructor for FileHandlerException.
     * @param  e {@link FileHandlerException} the exception.
     * @return message {@link String} the error message.
     */
    @ExceptionHandler(FileHandlerException.class)
    public String handleFileHandlerException(FileHandlerException e) {
        return e.getMessage();
    }

    /**
     * Constructor for IOException.
     * @param e {@link IOException} the exception.
     * @return message {@link String} the error message.
     */
    @ExceptionHandler(IOException.class)
    public String handleException(IOException e) {
        return e.getMessage();
    }

    /**
     * Constructor for ClassNotFoundException.
     * @param e {@link ClassNotFoundException} the exception.
     * @return message {@link String} the error message.
     */
    @ExceptionHandler(ClassNotFoundException.class)
    public String handleException(Exception e) {
        return e.getMessage();
    }
}
