package com.authenticket.authenticket.exception;

public class AwaitingVerificationException extends RuntimeException{
    private String userLabel;
    public AwaitingVerificationException(String message) {
        super(message);
    }

    public AwaitingVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
