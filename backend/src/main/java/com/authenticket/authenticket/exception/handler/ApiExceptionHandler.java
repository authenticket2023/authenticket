package com.authenticket.authenticket.exception.handler;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.exception.ApiException;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.service.Utility;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler extends Utility {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ApiException apiException = new ApiException(
                "Invalid username or password"
        );
        // Customize the response for bad credentials (e.g., invalid username or password)
        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.FORBIDDEN;

        ApiException apiException = new ApiException(
                "Access denied"
        );
        // Customize the response for access denied (e.g., unauthorized access to a resource)
        return new ResponseEntity<>(apiException, status);
    }
    @ExceptionHandler({InsufficientAuthenticationException.class, ExpiredJwtException.class})
    public ResponseEntity<Object> handleExpiredJwtException(Exception ex) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.FORBIDDEN;

        ApiException apiException = new ApiException(
                "Access denied! User not allowed to access"
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

        e.printStackTrace();

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
