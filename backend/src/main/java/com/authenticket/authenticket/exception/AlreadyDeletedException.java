package com.authenticket.authenticket.exception;

/**
 * An exception class that represents a situation where an object has already been deleted or marked as deleted.
 * This exception is typically used to indicate that an operation was attempted on an object that is no longer
 * available in the system.
 */
public class AlreadyDeletedException extends ApiRequestException{
    /**
     * Constructs a new AlreadyDeletedException with the specified detail message.
     *
     * @param message A descriptive message indicating that the object is already deleted.
     */
    public AlreadyDeletedException(String message) {
        super(message);
    }

    /**
     * Constructs a new AlreadyDeletedException with a message that includes the object type and its ID,
     * indicating that the object is already deleted.
     *
     * @param object The type of the object that is already deleted.
     * @param id     The ID of the object that is already deleted.
     */
    public AlreadyDeletedException(String object, Object id) {
        super(object + " with ID " + id.toString() + " already deleted");
    }

    /**
     * Constructs a new AlreadyDeletedException with the specified detail message and a cause.
     *
     * @param message A descriptive message indicating that the object is already deleted.
     * @param cause   The cause of the exception.
     */
    public AlreadyDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
