package com.authenticket.authenticket.exception;

public class AlreadyExistsException extends ApiRequestException{
    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
