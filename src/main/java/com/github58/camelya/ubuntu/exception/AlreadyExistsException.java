package com.github58.camelya.ubuntu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Class AlreadyExistsException represents the kind of exception that is thrown
 * when the model already exists in the database.
 *
 * @author Kamila Meshcheryakova
 * created 26.06.2020
 */
@SuppressWarnings("unused")
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "AlreadyExistsException already exists")
public class AlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
