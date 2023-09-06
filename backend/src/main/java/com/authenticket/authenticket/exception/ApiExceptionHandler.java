package com.authenticket.authenticket.exception;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.service.Utility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler extends Utility {
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
                e.getMessage() /*+ ": " + e.getClass()*/
        );

        return new ResponseEntity<>(apiException, status);
    }
}
