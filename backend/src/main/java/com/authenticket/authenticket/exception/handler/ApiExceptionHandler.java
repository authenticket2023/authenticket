package com.authenticket.authenticket.exception.handler;

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
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * This class serves as an exception handler for various exceptions that may occur in the API.
 * It provides customized responses for specific exception types.
 */
@ControllerAdvice
public class ApiExceptionHandler extends Utility {
    /**
     * Handles exceptions related to bad credentials, such as invalid username or password.
     *
     * @param ex The exception being handled.
     * @return A ResponseEntity containing an ApiException with an appropriate message and a UNAUTHORIZED status code.
     */
    @ExceptionHandler({BadCredentialsException.class , UsernameNotFoundException.class})
    public ResponseEntity<Object> handleBadCredentialsException(Exception ex) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ApiException apiException = new ApiException(
                "Invalid username or password"
        );
        // Customize the response for bad credentials (e.g., invalid username or password)
        return new ResponseEntity<>(apiException, status);
    }

    /**
     * Handles exceptions related to access denied, typically due to insufficient permissions.
     *
     * @param ex The exception being handled.
     * @return A ResponseEntity containing an ApiException with an appropriate message and a FORBIDDEN status code.
     */
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

    /**
     * Handles exceptions related to insufficient authentication for accessing a resource.
     *
     * @param ex The exception being handled.
     * @return A ResponseEntity containing an ApiException with an appropriate message and a FORBIDDEN status code.
     */
    @ExceptionHandler({InsufficientAuthenticationException.class})
    public ResponseEntity<Object> handleInsufficientAuthentication(InsufficientAuthenticationException ex) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ApiException apiException = new ApiException(
                "Access denied! Please log in with a valid account and try again"
        );

        return new ResponseEntity<>(apiException, status);
    }

    /**
     * Handles exceptions related to expired JWT tokens.
     *
     * @param ex The exception being handled.
     * @return A ResponseEntity containing an ApiException with an appropriate message and a FORBIDDEN status code.
     */
    @ExceptionHandler({ExpiredJwtException.class})
    public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.FORBIDDEN;

        if ("ticket".equals(ex.getClaims().get("role"))) {
            ApiException apiException = new ApiException(
                    "Event '" + ex.getClaims().get("event") + "' is over."
            );
            status = HttpStatus.BAD_REQUEST;

            return new ResponseEntity<>(apiException, status);
        }
        ApiException apiException = new ApiException(
                "Token expired. Please log in again."
        );

        return new ResponseEntity<>(apiException, status);
    }

    /**
     * Handles generic exceptions not covered by specific exception handlers.
     *
     * @param e The exception being handled.
     * @return A ResponseEntity containing an ApiException with an appropriate message and a INTERNAL_SERVER_ERROR status code.
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(Exception e) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiException apiException = new ApiException(
                "Something went wrong: " + e.getMessage()
        );

        return new ResponseEntity<>(apiException, status);
    }

    /**
     * Handles exceptions of type ApiRequestException.
     *
     * @param e The ApiRequestException being handled.
     * @return A ResponseEntity containing an ApiException with the message from the exception and a BAD_REQUEST status code.
     */
    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiException apiException = new ApiException(
                e.getMessage()
        );

        return new ResponseEntity<>(apiException, status);
    }

    /**
     * Exception handler method for handling IllegalArgumentExceptions.
     *
     * This method is responsible for capturing and handling IllegalArgumentExceptions,
     * returning an appropriate ResponseEntity with error details in the response payload.
     *
     * @param e The IllegalArgumentException to be handled.
     * @return A ResponseEntity containing an ApiException with the error message and a
     *         status code indicating a bad request (HTTP 400).
     */
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        //Create payload to send inside response entity containing exception details
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiException apiException = new ApiException(
                e.getMessage()
        );

        return new ResponseEntity<>(apiException, status);
    }
}
