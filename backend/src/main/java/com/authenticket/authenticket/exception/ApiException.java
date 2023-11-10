package com.authenticket.authenticket.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A simple data class representing an API exception message.
 */
@Data
@AllArgsConstructor
public class ApiException {
    private final String message;
}
