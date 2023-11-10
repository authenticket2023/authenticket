package com.authenticket.authenticket.exception;

/**
 * An exception indicating that an object or entity does not exist in the system.
 *
 * This exception is typically thrown when an operation is attempted on an object or entity that should exist but cannot
 * be found in the system. It can be used to signal that an object with a specific identifier or characteristics is
 * expected to exist, but no matching entity was found.
 */
public class NonExistentException extends ApiRequestException{

    /**
     * Constructs a new NonExistentException with the specified detail message.
     *
     * @param message A description of the exception.
     */
    public NonExistentException(String message) {
        super(message);
    }

    /**
     * Constructs a new NonExistentException with a custom message indicating that an object with a given identifier does not exist.
     *
     * @param object The type or name of the object or entity that does not exist.
     * @param id The identifier of the object that does not exist.
     */
    public NonExistentException(String object, Object id) {
        super(object + " with ID " + id.toString() + " does not exist");
    }

    /**
     * Constructs a new NonExistentException with the specified detail message and a cause.
     *
     * @param message A description of the exception.
     * @param cause The cause of the exception, which may be another throwable.
     */
    public NonExistentException(String message, Throwable cause) {
        super(message, cause);
    }
}
