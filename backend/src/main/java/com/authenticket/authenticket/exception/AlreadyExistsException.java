package com.authenticket.authenticket.exception;

/**
 * An exception class that represents a situation where an object already exists and cannot be created again.
 * This exception is typically used to indicate that an operation was attempted on an object that should be unique.
 */
public class AlreadyExistsException extends ApiRequestException{

    /**
     * Constructs a new AlreadyExistsException with the specified detail message.
     *
     * @param message A descriptive message indicating that the object already exists and cannot be created again.
     */
    public AlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new AlreadyExistsException with the specified detail message and a cause.
     *
     * @param message A descriptive message indicating that the object already exists and cannot be created again.
     * @param cause   The cause of the exception.
     */
    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
