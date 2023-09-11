package com.authenticket.authenticket.exception;

public class AlreadyDeletedException extends ApiRequestException{
    public AlreadyDeletedException(String message) {
        super(message);
    }

    public AlreadyDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
