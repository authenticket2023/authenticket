package com.authenticket.authenticket.exception;

/**
 * An exception that represents a generic API request exception.
 *
 * This exception is the base class for various exceptions related to API requests. It can be extended to create
 * more specific exceptions for different scenarios.
 */
public class ApiRequestException extends RuntimeException {

    /**
     * Constructs a new ApiRequestException with the specified detail message.
     *
     * @param message A description of the exception.
     */
    public ApiRequestException(String message) {
        super(message);
    }

    /**
     * Constructs a new ApiRequestException with the specified detail message and a cause.
     *
     * @param message A description of the exception.
     * @param cause The cause of the exception, which may be another throwable.
     */
    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
