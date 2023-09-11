package com.authenticket.authenticket.exception;

public class NonExistentException extends ApiRequestException{
    public NonExistentException(String message) {
        super(message);
    }

    public NonExistentException(String message, Throwable cause) {
        super(message, cause);
    }
}
