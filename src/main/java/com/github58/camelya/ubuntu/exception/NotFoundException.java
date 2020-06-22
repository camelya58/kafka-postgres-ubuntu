package com.github58.camelya.ubuntu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Class NotFoundException represents the kind of exception that is thrown
 * when the model to be obtained is not found in the database.
 *
 * @author Kamila Meshcheryakova
 * created 22.06.2020
 */
@SuppressWarnings("unused")
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "NotFoundException not found")
public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
