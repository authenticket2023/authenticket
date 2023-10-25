package com.authenticket.authenticket.exception;

/**
 * An exception class for representing invalid API requests.
 * This exception is typically thrown when a request to an API endpoint is considered invalid.
 */
public class InvalidRequestException extends ApiRequestException{
    /**
     * Constructs a new InvalidRequestException with the specified error message.
     *
     * @param message The error message explaining the reason for the invalid request.
     */
    public InvalidRequestException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidRequestException with the specified error message and a nested cause.
     *
     * @param message The error message explaining the reason for the invalid request.
     * @param cause   The cause of the exception, typically another exception that led to this error.
     */
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
