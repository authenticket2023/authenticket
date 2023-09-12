package com.authenticket.authenticket.exception;

import com.authenticket.authenticket.service.Utility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ApiExceptionHandler extends Utility {
    @ExceptionHandler(value = {IOException.class})
    public ResponseEntity<Object> handleJwtException(ApiRequestException e) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiException apiException = new ApiException(
                e.getMessage()
        );

        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(Exception e) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiException apiException = new ApiException(
                "Something went wrong: " + e.getMessage()
        );

        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiException apiException = new ApiException(
                e.getMessage()
        );

        return new ResponseEntity<>(apiException, status);
    }
}
