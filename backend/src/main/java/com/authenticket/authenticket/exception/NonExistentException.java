package com.authenticket.authenticket.exception;

public class NonExistentException extends ApiRequestException{
    public NonExistentException(String message) {
        super(message);
    }

    public NonExistentException(String object, Integer id) {
        super(object + " with ID " + id + " does not exist");
    }

    public NonExistentException(String message, Throwable cause) {
        super(message, cause);
    }
}
