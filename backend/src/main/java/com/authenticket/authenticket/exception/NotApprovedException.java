package com.authenticket.authenticket.exception;

/**
 * An exception indicating that an operation was attempted on an object or entity that has not been approved.
 *
 * This exception is typically thrown when an operation is attempted on an object that requires prior approval or a certain
 * status, and that status or approval condition has not been met. It can be used to signal that an operation cannot be
 * performed until the object or entity has been approved or satisfies certain requirements.
 */
public class NotApprovedException extends ApiRequestException{

    /**
     * Constructs a new NotApprovedException with the specified detail message.
     *
     * @param message A description of the exception.
     */
    public NotApprovedException(String message) {
        super(message);
    }

    /**
     * Constructs a new NotApprovedException with a custom message indicating that an object with a given identifier is not yet approved.
     *
     * @param object The type or name of the object or entity that is not approved.
     * @param id The identifier of the object that is not approved.
     */
    public NotApprovedException(String object, Object id) {
        super(object + " with ID " + id.toString() + " not yet approved");
    }

    /**
     * Constructs a new NotApprovedException with the specified detail message and a cause.
     *
     * @param message A description of the exception.
     * @param cause The cause of the exception, which may be another throwable.
     */
    public NotApprovedException(String message, Throwable cause) {
        super(message, cause);
    }
}
