package com.example.restricted_file_sharing_system.exception;

/**
 * Exception thrown when a user doesn't have access to a file.
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
