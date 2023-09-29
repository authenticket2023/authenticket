package com.authenticket.authenticket.exception;

public class NotApprovedException extends ApiRequestException{
    public NotApprovedException(String message) {
        super(message);
    }

    public NotApprovedException(String object, Integer id) {
        super(object + " with ID " + id + " not yet approved");
    }

    public NotApprovedException(String message, Throwable cause) {
        super(message, cause);
    }
}
