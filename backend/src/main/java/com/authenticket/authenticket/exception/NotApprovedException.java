package com.authenticket.authenticket.exception;

public class NotApprovedException extends ApiRequestException{
    public NotApprovedException(String message) {
        super(message);
    }

    public NotApprovedException(String object, Object id) {
        super(object + " with ID " + id.toString() + " not yet approved");
    }

    public NotApprovedException(String message, Throwable cause) {
        super(message, cause);
    }
}
