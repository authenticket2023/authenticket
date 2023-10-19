package com.authenticket.authenticket.exception;

/**
 * An exception indicating that an object or entity is in a state of awaiting verification.
 *
 * This exception is typically thrown when an operation is attempted on an object or entity that is in a state of
 * awaiting verification or approval. It is used to signal that an object or entity is not yet fully approved or verified.
 */
public class AwaitingVerificationException extends ApiRequestException{

    /**
     * Constructs a new AwaitingVerificationException with the specified detail message.
     *
     * @param message A description of the exception.
     */
    public AwaitingVerificationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AwaitingVerificationException with the specified detail message and a cause.
     *
     * @param message A description of the exception.
     * @param cause The cause of the exception, which may be another throwable.
     */
    public AwaitingVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
